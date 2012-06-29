package rabbit.proxy;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.List;
import rabbit.cache.Cache;
import rabbit.cache.CacheEntry;
import rabbit.filter.HttpFilter;
import rabbit.handler.BaseHandler;
import rabbit.handler.Handler;
import rabbit.handler.HandlerFactory;
import rabbit.handler.MultiPartHandler;
import rabbit.handler.ResourceSource;
import rabbit.http.HttpDateParser;
import rabbit.http.HttpHeader;
import rabbit.io.WebConnection;
import rabbit.util.Counter;
import rabbit.util.Logger;
import rabbit.util.TrafficLogger;

/** The base connection class for rabbit. 
 *
 *  This is the class that handle the http protocoll for proxies.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Connection {
    /** The id of this connection. */
    private ConnectionId id;
    
    /** The client channel */
    private SocketChannel channel;
    
    /** The current request */
    private HttpHeader request;

    /** The current byte buffer */
    private ByteBuffer requestBuffer;
    
    /** The selector to use */
    private Selector selector;
    
    /** The proxy we are serving */
    private HttpProxy proxy;

    /** The current status of this connection. */
    private String status;
    
    /** The time this connection was started. */
    private long started;

    private boolean  keepalive      = true;
    private boolean  meta           = false;
    private boolean  chunk          = true;
    private boolean  mayUseCache    = true;
    private boolean  mayCache       = true;
    private boolean  mayFilter      = true;
    private boolean  mustRevalidate = false;
    private boolean  addedINM       = false;
    private boolean  addedIMS       = false;
    
    /** If the user has authenticated himself */
    private String userName = null;
    private String password = null;

    /* Current status information */
    private String requestVersion = null;
    private String requestLine   = null;
    private String statusCode    = null;
    private String extraInfo     = null;
    private String contentLength = null;

    private ClientResourceHandler clientResourceHandler;

    private StandardResponseHeaders responseHandler = 
    new StandardResponseHeaders (this);

    private TrafficLoggerHandler tlh = new TrafficLoggerHandler ();

    public Connection (ConnectionId id,	SocketChannel channel, 
		       Selector selector, HttpProxy proxy) {
	this.id = id;
	this.channel = channel;
	this.selector = selector;
	this.proxy = proxy;
	proxy.addCurrentConnection (this);
    }

    public ConnectionId getId () {
	return id;
    }

    /** Read a request. 
     */
    public void readRequest () {
	clearStatuses (); 
	try {
	    channel.socket ().setTcpNoDelay (true);
	    HttpHeaderListener clientListener = new RequestListener ();
	    HttpHeaderReader requestReader = 
		new HttpHeaderReader (channel, requestBuffer, selector, 
				      getLogger (), tlh.getClient (), true,
				      proxy.getStrictHttp (), clientListener);
	} catch (Throwable ex) {
	    handleFailedRequestRead (ex);
	}
    }
    
    private void handleFailedRequestRead (Throwable t) {
	if (t instanceof RequestLineTooLongException) {
	    HttpHeader err = getHttpGenerator ().get414 ();
	    sendAndClose (err);
	} else {	    
	    getLogger ().logInfo ("Exception when reading request: " + 
				  getStackTrace (t));
	    closeDown ();
	}
    }

    private class RequestListener implements HttpHeaderListener {
	public void httpHeaderRead (HttpHeader header, ByteBuffer buffer, 
				    boolean keepalive, boolean isChunked, 
				    long dataSize) {
	    setKeepalive (keepalive);
	    requestRead (header, buffer, isChunked, dataSize);
	}

	public void closed () {
	    closeDown ();	    
	}

	public void timeout () {
	    getLogger ().logInfo ("Timeout when reading client request");
	    closeDown ();
	}
	
	public void failed (Exception e) {
	    handleFailedRequestRead (e);
	}
    } 

    private String getStackTrace (Throwable t) {
	StringWriter sw = new StringWriter ();
	PrintWriter ps = new PrintWriter (sw);
	t.printStackTrace (ps);
	return sw.toString ();
    }

    private void handleInternalError (Throwable t) {
	extraInfo = 
	    extraInfo != null ? extraInfo + t.toString () : t.toString ();
	String message = getStackTrace (t);
	getLogger ().logError ("Internal Error: " + message);
	HttpHeader internalError = getHttpGenerator ().get500 (t);
	sendAndClose (internalError);	
    }

    private void requestRead (HttpHeader request, ByteBuffer buffer, 
			      boolean isChunked, long dataSize) {
	status = "Request read, processing";
	this.request = request;
	this.requestBuffer = buffer;
	requestVersion = request.getHTTPVersion ();
	if (requestVersion == null) {
	    // TODO: fix http/0.9 handling.
	    getLogger ().logInfo ("bad header read: " + request);
	    closeDown ();
	    return;
	}
	requestVersion = requestVersion.toUpperCase ();
	request.addHeader ("Via", requestVersion + " RabbIT");
	    
	requestLine = request.getRequestLine ();
	getCounter ().inc ("Requests");
	
	try {
	    // SSL requests are special in a way... 
	    // Don't depend upon being able to build URLs from the header...
	    if (request.isSSLRequest ()) {
	    	System.out.println("req is ssl req");
		status = "Handling ssl request";
		checkAndHandleSSL (requestBuffer);
		return;
	    }

	    // Read in any posted data.
	    if (isChunked) {
		setMayUseCache (false);
		setMayCache (false);
		status = "Request read, reading chunked data";
		setupChunkedContent ();
	    }

	    String ct = null;
	    ct = request.getHeader ("Content-Type");
	    if (request.getContent () == null 
		&& (ct == null || !ct.startsWith ("multipart/byteranges")) 
		&& dataSize > -1) {
		setupClientResourceHandler (dataSize);
	    }

	    if (ct != null) {
		status = "Request read, reading multipart data";
		readMultiPart (ct);
	    }
 
	    filterAndHandleRequest ();
	} catch (Throwable t) {
	    handleInternalError (t);
	}
    }

    /** Filter the request and handle it. 
     * @param header the request
     */
    // TODO: filtering here may block! be prepared to run filters in a 
    // TODO: separate thread.
    private void filterAndHandleRequest () {
	// Filter the request based on the header.
	// A response means that the request is blocked.
	// For ad blocking, bad header configuration (http/1.1 correctness) ... 
	HttpHeaderFilterer filterer = proxy.getHttpHeaderFilterer ();
	HttpHeader badresponse = filterer.filterHttpIn (this, channel, request);
	if (badresponse != null) {
	    statusCode = badresponse.getStatusCode ();
	    sendAndClose (badresponse);
	} else {
	    status = "Handling request";
	    if (getMeta ())
		handleMeta ();
	    else
		handleRequest ();
	}
    }

    /** Handle a meta page.
     */
    public void handleMeta () {
	status = "Handling meta page";
	MetaHandlerHandler mhh = new MetaHandlerHandler ();
	try {
	    mhh.handleMeta (this, request, tlh.getProxy (), tlh.getClient ());
	} catch (IOException ex) {
	    logAndClose (null);
	}
    }
    
    /** A container to send around less parameters.*/
    class RequestHandler {
	public ResourceSource content = null;
	public ByteBuffer webBuffer = null;
	public HttpHeader webHeader = null;
	public CacheEntry<HttpHeader, HttpHeader> entry = null;
	public HttpHeader dataHook = null; // the entrys datahook if any.
	public HandlerFactory handlerFactory = null;
	public long size = -1;
	public WebConnection wc = null;	
	public long requestTime = -1;
	public ConditionalChecker cond = new ConditionalChecker (getLogger ());
	public boolean conditional;
    }

    private void checkNoStore (CacheEntry<HttpHeader, HttpHeader> entry) {
	if (entry == null)
	    return;
	for (String cc : request.getHeaders ("Cache-Control"))
	    if (cc.equals ("no-store"))
		proxy.getCache ().remove (entry.getKey ());
    }
    
    private boolean checkMaxAge (RequestHandler rh) {
	return rh.cond.checkMaxAge (this, rh.dataHook, rh);
    }

    /** Handle a request by getting the datastream (from the cache or the web).
     *  After getting the handler for the mimetype, send it.
     */
    public void handleRequest () {
	// TODO: move this method to separate thread, 
	// TODO: it may block in many places.
	RequestHandler rh = new RequestHandler ();	
	Cache<HttpHeader, HttpHeader> cache = proxy.getCache ();
	String method = request.getMethod ();
	if (!method.equals ("GET") && !method.equals ("HEAD"))
	    cache.remove (request);
		
	rh.entry = cache.getEntry (request);
	if (rh.entry != null)
	    rh.dataHook = rh.entry.getDataHook (proxy.getCache ());

	checkNoStore (rh.entry);
	if (!rh.cond.checkMaxStale (request, rh) && checkMaxAge (rh))
	    setMayUseCache (false);
	
	rh.conditional = rh.cond.checkConditional (this, request, rh);
	if (partialContent (rh)) 
	    fillupContent (rh);
	checkIfRange (rh);

	boolean mc = getMayCache ();
	if (getMayUseCache ()) {
	    // in cache?
	    if (rh.entry != null) {
		CacheChecker cc = new CacheChecker ();
		if (cc.checkCachedEntry (this, request, rh)) {
		    return;
		}
	    }
	}
	
	if (rh.content == null) {
	    // Ok cache did not have a usable resource, 
	    // so get the resource from the net.
	    // reset value to one before we thought we could use cache...
	    mayCache = mc;
	    SWC swc = new SWC (this, proxy.getOffset (), request, requestBuffer,
			       tlh, clientResourceHandler, rh);
	    swc.establish ();
	} else {
	    resourceEstablished (rh);
	}
    }

    void webConnectionSetupFailed (RequestHandler rh, Exception cause) {
	getLogger ().logWarn ("strange error setting up web connection: " + 
			      cause.toString ());
	tryStaleEntry (rh, cause);
    }

    private void setMayCacheFromCC (RequestHandler rh) {
	HttpHeader resp = rh.webHeader;
	for (String val : resp.getHeaders ("Cache-Control")) {
	    if ("public".equals (val) 
		|| "must-revalidate".equals (val) 
		|| val.startsWith ("s-maxage=")) {
		String auth = request.getHeader ("Authorization");		
		if (auth != null) {
		    // TODO this ignores no-store and a few other things...
		    mayCache = true;
		    break;
		}
	    }
	}
    }

    /** Check if we must tunnel a request. 
     *  Currently will only check if the Authorization starts with NTLM or Negotiate. 
     * @param rh the request handler. 
     */
    protected boolean mustTunnel (RequestHandler rh) {
	String auth = request.getHeader ("Authorization");
	if (auth != null) {
	    if (auth.startsWith ("NTLM") || auth.startsWith ("Negotiate"))
		return true;
	}
	return false;
    }
    
    void webConnectionEstablished (RequestHandler rh) {
	getProxy ().markForPipelining (rh.wc);
	setMayCacheFromCC (rh);
	resourceEstablished (rh);
    }

    private void tunnel (RequestHandler rh) {
	try {
	    TunnelDoneListener tdl = new TDL (rh);
	    Tunnel tunnel = 
		new Tunnel (selector, getLogger (), channel, requestBuffer,
			    tlh.getClient (), rh.wc.getChannel (), 
			    rh.webBuffer, tlh.getNetwork (), tdl);
	} catch (IOException ex) {
	    logAndClose (rh);
	}
    }
    
    private void resourceEstablished (RequestHandler rh) {
	try {
	    // and now we filter the response header if any.
	    if (!request.isDot9Request ()) {
		if (mustTunnel (rh)) {
		    tunnel (rh);
		    return;
		}
		
		String status = rh.webHeader.getStatusCode ().trim ();
		rh.cond.checkStaleCache (request, this, rh);
		CacheChecker cc = new CacheChecker ();
		cc.removeOtherStaleCaches (request, rh.webHeader, 
					   proxy.getCache (), getLogger ());
		if (status.equals ("304")) {
		    NotModifiedHandler nmh = new NotModifiedHandler ();
		    nmh.updateHeader (rh, getLogger ());
		    if (rh.entry != null) {
			proxy.getCache ().entryChanged (rh.entry, 
							request, rh.dataHook);
		    }
		}

		HttpHeader bad = cc.checkExpectations (this, request, rh.webHeader);
		if (bad == null) {
		    HttpHeaderFilterer filterer = 
			proxy.getHttpHeaderFilterer ();
		    bad = filterer.filterHttpOut (this, channel, rh.webHeader);
		}
		if (bad != null) {
		    rh.content.release (this);
		    sendAndClose (bad);
		    return;
		}
		
		rh.entry = proxy.getCache ().getEntry (request);
		if (rh.conditional && rh.entry != null 
		    && status.equals ("304")) {
		    if (handleConditional (rh)) {
			return;
		    }
		} else if (status != null && status.length () > 0) {
		    if (status.equals ("304") || status.equals ("204") 
			|| status.charAt (0) == '1') {
			rh.content.release (this);
			sendAndClose (rh.webHeader);
			return;
		    }
		}
	    }
	    
	    setHandlerFactory (rh);
	    Handler handler = 
		rh.handlerFactory.getNewInstance (this, tlh, 
						  request, requestBuffer, 
						  rh.webHeader, rh.content, 
						  getMayCache (), 
						  getMayFilter (), rh.size);
	    if (handler == null) {
		doError (500, "Something fishy with that handler....");
	    } else {
		finalFixesOnWebHeader (rh, handler);
		
		// HTTP/0.9 does not support HEAD, so webheader should be valid.
		if (request.isHeadOnlyRequest ()) {
		    rh.content.release (this);
		    sendAndRestart (rh.webHeader);
		} else {
		    handler.handle ();
		}
	    }
	} catch (Throwable t) {
	    handleInternalError (t);
	}
    }
	
    private void finalFixesOnWebHeader (RequestHandler rh, Handler handler) {
	if (chunk) {
	    if (rh.size < 0 || handler.changesContentSize ()) {
		rh.webHeader.removeHeader ("Content-Length");
		rh.webHeader.setHeader ("Transfer-Encoding", "chunked");
	    } else {
		setChunking (false);
	    }
	} else {
	    if (getKeepalive ()) {
		rh.webHeader.setHeader ("Proxy-Connection", "Keep-Alive");
		rh.webHeader.setHeader ("Connection", "Keep-Alive");
	    } else {
		rh.webHeader.setHeader ("Proxy-Connection", "close");
		rh.webHeader.setHeader ("Connection", "close");
	    }
	}
    }

    private void setHandlerFactory (RequestHandler rh) {
	if (rh.handlerFactory == null) {
	    if (rh.webHeader != null) {
		String ct = rh.webHeader.getHeader ("Content-Type");
		if (ct != null) {
		    ct = ct.toLowerCase ();
		    if (getMayFilter ())
			rh.handlerFactory = 
			    proxy.getHandlerFactory (ct.toLowerCase ());
		    if (rh.handlerFactory == null 
			&& ct.startsWith ("multipart/byteranges"))
			rh.handlerFactory = new MultiPartHandler ();
		}
	    }
	    if (rh.handlerFactory == null) {              // still null
		rh.handlerFactory = new BaseHandler ();   // fallback...
	    }
	}   
    }

    private boolean handleConditional (RequestHandler rh) throws IOException {
	HttpHeader cachedHeader = rh.dataHook;
	proxy.releaseWebConnection (rh.wc);
	if (addedINM) 
	    request.removeHeader ("If-None-Match");
	if (addedIMS)
	    request.removeHeader ("If-Modified-Since");

	if (checkWeakEtag (cachedHeader, rh.webHeader)) {
	    NotModifiedHandler nmh = new NotModifiedHandler ();
	    nmh.updateHeader (rh, getLogger ());
	    setMayCache (false);
	    try {
		HttpHeader res304 = nmh.is304 (request, this, rh);
		if (res304 != null) {
		    sendAndClose (res304);
		    return true;
		} else {
		    if (rh.content != null)
			rh.content.release (this);
		    setupCachedEntry (rh);
		}
	    } catch (IOException e) {
		getLogger ().logWarn ("Conditional request: IOException (" + 
				      request.getRequestURI () + ",: " + e); 
	    }
	} else {
	    // retry... 
	    request.removeHeader ("If-None-Match");
	    proxy.getCache ().remove (request);
	    handleRequest ();
	    return true;
	}
	
	// send the cached entry. 
	return false;
    }

    private class TDL implements TunnelDoneListener {
	private RequestHandler rh;
	
	public TDL (RequestHandler rh) {
	    this.rh = rh;
	}
	
	public void tunnelClosed () {
	    logAndClose (rh);
	}
    }

    private void tryStaleEntry (RequestHandler rh, Exception e) {
	// do we have a stale entry?
	if (rh.entry != null && rh.conditional && !mustRevalidate) {
	    handleStaleEntry (rh);
	} else {
	    doError (504, e);
	    return;
	}
    }

    private void handleStaleEntry (RequestHandler rh) {
	setMayCache (false);
	try {
	    setupCachedEntry (rh);
	    rh.webHeader.addHeader ("Warning", 
				    "110 RabbIT \"Response is stale\"");
	    resourceEstablished (rh);
	} catch (IOException ex) {
	    doError (504, ex);
	    return;
	}
    }
    
    HttpHeader setupCachedEntry (RequestHandler rh) throws IOException {
	SCC swc = new SCC (this, request, requestBuffer, rh);
	HttpHeader ret = swc.establish ();
	return ret;
    }

    private void setupChunkedContent () throws IOException {
	setMayUseCache (false);
	setMayCache (false);
	clientResourceHandler = 
	    new ChunkedContentTransferHandler (this, requestBuffer, tlh);
    }

    private void setupClientResourceHandler (long dataSize) {
	setMayUseCache (false);
	setMayCache (false);
	clientResourceHandler = 
	    new ContentTransferHandler (this, requestBuffer, dataSize, tlh);
    }

    private void readMultiPart (String ct) {
	// Content-Type: multipart/byteranges; boundary=B-qpuvxclkeavxeywbqupw
	if (ct.startsWith ("multipart/byteranges")) {
	    setMayUseCache (false);
	    setMayCache (false);
	    
	    clientResourceHandler = 
		new MultiPartTransferHandler (this, requestBuffer, tlh, ct);
	}
    }

    /** Handles the case where the request does not have a valid body.
     * @param header the request made.
     */
    private void handleBadContent (HttpHeader header, String desc) {
	getLogger ().logDebug ("bad content for:\n" + header);
	doError (400, "Bad content: " + desc);
    }

    private boolean partialContent (RequestHandler rh) {
	if (rh.entry == null)
	    return false;
	String method = request.getMethod ();
	if (!method.equals ("GET"))
	    return false;
	HttpHeader resp = rh.dataHook;
	String realLength = resp.getHeader ("RabbIT-Partial");
	return (realLength != null);
    }
    
    private void fillupContent (RequestHandler rh) {
	setMayUseCache (false);
	setMayCache (true);
	// TODO: if the need arise, think about implementing smart partial updates. 
    }

    private void checkIfRange (RequestHandler rh) {
	CacheEntry entry = rh.entry;
	if (entry == null)
	    return;
	String ifRange = request.getHeader ("If-Range");
	if (ifRange == null)
	    return;
	String range = request.getHeader ("Range");
	if (range == null)
	    return;
	Date d = HttpDateParser.getDate (ifRange);
	HttpHeader oldresp = rh.dataHook;
	if (d == null) {
	    // we have an etag...
	    String etag = oldresp.getHeader ("Etag");
	    if (etag == null || !checkWeakEtag (etag, ifRange))
		setMayUseCache (false);
	}
    }

    /** Send an error (400 Bad Request) to the client.
     * @param status the status code of the error.
     * @param message the error message to tell the client.
     */
    public void doError (int status, String message) {
	this.statusCode = Integer.toString (status);
	HttpHeader header = responseHandler.getHeader ("HTTP/1.0 400 Bad Request");
	StringBuilder error = 
	    new StringBuilder (HtmlPage.getPageHeader (this, "400 Bad Request") +
			       "Unable to handle request:<br><b>" + 
			       message +
			       "</b></body></html>\n");
	header.setContent (error.toString ());
	sendAndClose (header);
    }

    /** Send an error (400 Bad Request or 504) to the client.
     * @param statuscode the status code of the error.
     * @param e the exception to tell the client.
     */
    public void doError (int statuscode, Exception e) {
	StringWriter sw = new StringWriter ();
	PrintWriter ps = new PrintWriter (sw);
	e.printStackTrace (ps);
	String message = sw.toString ();
	this.statusCode = Integer.toString (statuscode);
	extraInfo = (extraInfo != null ? 
		     extraInfo + e.toString () : 
		     e.toString ());
	HttpHeader header = null;
	if (statuscode == 504) 
	    header = getHttpGenerator ().get504 (e, requestLine);
	else 
	    header = getHttpGenerator ().getHeader ("HTTP/1.0 400 Bad Request");
	
	StringBuilder sb = new StringBuilder ();
	sb.append (HtmlPage.getPageHeader (this, statuscode + " " + 
					   header.getReasonPhrase ()) +
		   "Unable to handle request:<br><b>" + 
		   e.getMessage () +
		   (header.getContent () != null ? 
		    "<br>" + header.getContent () : 
		    "") + 
		   "</b><br><xmp>" + message + "</xmp></body></html>\n");
	header.setContent (sb.toString ());
	sendAndClose (header);
    }

    private void checkAndHandleSSL (ByteBuffer buffer) {
	SSLHandler sslh = new SSLHandler (proxy, this, request, tlh);
	if (sslh.isAllowed ()) {
	    sslh.handle (channel, selector, buffer);
	} else {
	    HttpHeader badresponse = responseHandler.get403 ();
	    sendAndClose (badresponse);
	}
    }       

    public SocketChannel getChannel () {
	return channel;
    }

    public Selector getSelector () {
	return selector;
    }

    public HttpProxy getProxy () {
	return proxy;
    }

    private void closeDown () {
	try {
	    channel.close ();
	} catch (IOException e) {
	    getLogger ().logWarn ("Failed to close down connection: " + e);
	}
	proxy.removeCurrentConnection (this);
    }

    public Logger getLogger () {
	return proxy.getLogger ();
    }

    private ConnectionLogger getConnectionLogger () {
	return proxy.getConnectionLogger ();
    }

    public Counter getCounter () {
	return proxy.getCounter ();
    }

    /** Resets the statuses for this connection.
     */
    private void clearStatuses () {
	status         = "Reading request";
	started        = System.currentTimeMillis ();
	request        = null;
	keepalive      = true;
	meta           = false;
	chunk          = true;
	mayUseCache    = true;
	mayCache       = true;
	mayFilter      = true;
	mustRevalidate = false;
	addedINM       = false;
	addedIMS       = false;
	userName       = null;
	password       = null;
	requestLine    = "?";
	statusCode     = "200";
	extraInfo      = null;
	contentLength  = "-";
	clientResourceHandler = null;
    }

    /** Set keepalive to a new value. Note that keepalive can only be
     *	promoted down. 
     * @param keepalive the new keepalive value.
     */
    public void setKeepalive (boolean keepalive) {
	this.keepalive = (this.keepalive && keepalive);
    }
    
    /** Get the keepalive value.
     * @return true if keepalive should be done, false otherwise.
     */
    public boolean getKeepalive () {
	return keepalive;
    }

    public String getUserName () {
	return userName;
    }

    public void setUserName (String userName) {
	this.userName = userName;
    }
    
    public String getPassword () {
	return password;
    }

    public void setPassword (String password) {
	this.password = password;
    }

    public String getRequestLine () {
	return requestLine;
    }

    /** Get the http version that the client used.
     *  We modify the request header to hold HTTP/1.1 since that is
     *  what rabbit uses, but the real client may have sent a 1.0 header.
     */
    public String getRequestVersion () {
	return requestVersion;
    }

    public String getStatus () {
	return status;
    }

    public String getStatusCode () {
	return statusCode;
    }

    public String getContentLength () {
	return contentLength;
    }

    public String getExtraInfo () {
	return extraInfo;
    }

    /** Set the extra info.
     * @param info the new info.
     */
    public void setExtraInfo (String info) {
	this.extraInfo = info;
    }
    
    /** Get the time this connection was started. */
    public long getStarted () {
	return started;
    }

    /** Set the chunking option.
     * @param b if true this connection should use chunking.
     */
    public void setChunking (boolean b) {
	chunk = b;
    }

    /** Get the chunking option.
     * @return if this connection is using chunking.
     */
    public boolean getChunking () {
	return chunk;
    }

    /** Get the state of this request.
     * @return true if this is a metapage request, false otherwise.
     */
    public boolean getMeta () {
	return meta;
    }

    /** Set the state of this request.
     * @param meta true if this request is a metapage request, false otherwise.
     */
    public void setMeta (boolean meta) {
	this.meta = meta;
    }    

    /** Set the state of this request. This can only be promoted down..
     * @param useCache true if we may use the cache for this request, 
     *        false otherwise.
     */
    public void setMayUseCache (boolean useCache) {
	mayUseCache = mayUseCache && useCache;
    }

    /** Get the state of this request.
     * @return true if we may use the cache for this request, false otherwise.
     */
    public boolean getMayUseCache () {
	return mayUseCache;
    }
    
    /** Set the state of this request. This can only be promoted down.
     * @param cacheAllowed true if we may cache the response, false otherwise.
     */
    public void setMayCache (boolean cacheAllowed) {
	mayCache = cacheAllowed && mayCache;
    }

    /** Get the state of this request.
     * @return true if we may cache the response, false otherwise.
     */
    public boolean getMayCache () {
	return mayCache;
    }

    /** Get the state of this request. This can only be promoted down.
     * @param filterAllowed true if we may filter the response, false otherwise.
     */
    public void setMayFilter (boolean filterAllowed) {
	mayFilter = filterAllowed && mayFilter;
    }
    
    /** Get the state of the request.
     * @return true if we may filter the response, false otherwise.
     */
    public boolean getMayFilter () {
	return mayFilter;
    }

    void setAddedINM (boolean b) {
	addedINM = b;
    }

    void setAddedIMS (boolean b) {
	addedIMS = b;
    }

    void setMustRevalidate (boolean b) {
	mustRevalidate = b;
    }

    /** Set the content length of the response.
     * @param contentLength the new content length.
     */
    public void setContentLength (String contentLength) {
	this.contentLength = contentLength;
    }

    void setStatusCode (String statusCode) {
	this.statusCode = statusCode;
    }

    private void setStatusesFromHeader (HttpHeader header) {
	statusCode = header.getStatusCode ();
	String cl = header.getHeader ("Content-Length");
	if (cl != null)
	    contentLength  = cl;
    }

    void sendAndRestart (HttpHeader header) {
	status = "Sending response.";
	setStatusesFromHeader (header);
	if (!keepalive) {
	    sendAndClose (header);
	} else {
	    HttpHeaderSentListener sar = new SendAndRestartListener ();
	    try {
		HttpHeaderSender sender = 
		    new HttpHeaderSender (channel, selector, getLogger (), 
					  tlh.getClient (), header, false, sar);
	    } catch (IOException e) {
		getLogger ().logWarn ("IOException when sending header: " + e);
		closeDown ();
	    }	    
	}
    }

    public boolean useFullURI () {
	return proxy.isProxyConnected ();
    }

    private class SendAndRestartListener implements HttpHeaderSentListener {
	public void httpHeaderSent () {
	    logConnection ();
	    readRequest ();
	}
	
	public void timeout () {
	    getLogger ().logInfo ("Timeout when sending http header");
	    logAndClose (null);
	}

	public void failed (Exception e) {
	    getLogger ().logInfo ("Exception when sending http header: " + e);
	    logAndClose (null);
	}
    }


    void sendAndClose (HttpHeader header) {
	status = "Sending response and closing.";
	setStatusesFromHeader (header);
	keepalive = false;
	HttpHeaderSentListener scl = new SendAndCloseListener ();
	try {
	    HttpHeaderSender sender = 
		new HttpHeaderSender (channel, selector, getLogger (), 
				      tlh.getClient (), header, false, scl);
	} catch (IOException e) {
	    getLogger ().logWarn ("IOException when sending header: " + e);
	    closeDown ();
	}
    }
    
    public void logAndClose (RequestHandler rh) {
	if (rh != null && rh.wc != null) {
	    proxy.releaseWebConnection (rh.wc);
	} 
	logConnection ();
	closeDown ();
    }

    public void logAndRestart () {
	logConnection ();	
	if (getKeepalive ())
	    readRequest ();
	else 
	    closeDown ();
    }

    private class SendAndCloseListener implements HttpHeaderSentListener {
	public void httpHeaderSent () {
	    logAndClose (null);
	}
	
	public void timeout () {
	    getLogger ().logInfo ("Timeout when sending http header");
	    logAndClose (null);
	}

	public void failed (Exception e) {
	    getLogger ().logInfo ("Exception when sending http header: " + e);
	    logAndClose (null);
	}
    }

    boolean isWeak (String t) {
	return t.startsWith ("W/");
    }

    boolean checkStrongEtag (String et, String im) {
	return !isWeak (im) && im.equals (et);
    }

    boolean checkWeakEtag (HttpHeader h1, HttpHeader h2) {
	String et1 = h1.getHeader ("Etag");
	String et2 = h2.getHeader ("Etag");
	if (et1 == null || et2 == null)
	    return true;
	return checkWeakEtag (et1, et2);
    }

    boolean checkWeakEtag (String et, String im) {
	if (et == null || im == null)
	    return false;
	if (isWeak (et))
	    et = et.substring (2);
	if (isWeak (im))
	    im = im.substring (2);
	return im.equals (et);
    }

    public HttpGenerator getHttpGenerator () {
	return responseHandler;
    }

    private void logConnection () {
	getConnectionLogger ().logConnection (Connection.this);
	proxy.updateTrafficLog (tlh);
	tlh.clear ();
    }
}
