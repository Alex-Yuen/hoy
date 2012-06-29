package rabbit.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.StringTokenizer;
import rabbit.cache.Cache;
import rabbit.cache.CacheEntry;
import rabbit.http.HttpDateParser;
import rabbit.http.HttpHeader;
import rabbit.http.ContentRangeParser;
import rabbit.proxy.BlockSender;
import rabbit.proxy.BlockSentListener;
import rabbit.proxy.ChunkEnder;
import rabbit.proxy.Connection;
import rabbit.proxy.HttpHeaderSender;
import rabbit.proxy.HttpHeaderSentListener;
import rabbit.proxy.PartialCacher;
import rabbit.proxy.TrafficLoggerHandler;
import rabbit.proxy.TransferHandler;
import rabbit.proxy.TransferListener;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** This class is an implementation of the Handler interface.
 *  This handler does no filtering, it only sends the data as
 *  effective as it can.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class BaseHandler 
    implements Handler, HandlerFactory, HttpHeaderSentListener, BlockListener, 
               BlockSentListener {
    /** The Connection handling the request.*/
    protected Connection con;
    /** The traffic logger handler. */
    protected TrafficLoggerHandler tlh;
    /** The actual request made. */
    protected HttpHeader request;
    /** The client buffer. */
    protected ByteBuffer clientBuffer;
    /** The actual response. */
    protected HttpHeader response;
    /** The resource */
    protected ResourceSource content;

    /** The cache entry if available. */
    protected CacheEntry<HttpHeader, HttpHeader> entry = null;
    /** The cache channel. */
    protected WritableByteChannel cacheChannel;
    
    /** May we cache this request. */
    protected boolean mayCache;
    /** May we filter this request */
    protected boolean mayFilter;
    /** The length of the data beeing handled or -1 if unknown.*/
    protected long size = -1;
    /** The total amount of data that we read. */
    protected long totalRead = 0;

    /** The flag for the last empty chunk */
    private boolean emptyChunkSent = false;

    /** For creating the factory.
     */
    public BaseHandler () {	
    }
    
    /** Create a new BaseHandler for the given request.
     * @param con the Connection handling the request.
     * @param request the actual request made.
     * @param clientBuffer the client side buffer.
     * @param response the actual response.
     * @param content the resource.
     * @param mayCache May we cache this request? 
     * @param mayFilter May we filter this request?
     * @param size the size of the data beeing handled.
     */
    public BaseHandler (Connection con, TrafficLoggerHandler tlh, 
			HttpHeader request, ByteBuffer clientBuffer,
			HttpHeader response, ResourceSource content, 
			boolean mayCache, boolean mayFilter, long size) {
	this.con = con;
	this.tlh = tlh;
	this.request = request;
	this.clientBuffer = clientBuffer;
	this.response = response;
	if (response == null)
	    throw new IllegalArgumentException ("response may not be null");
	this.content = content;
	this.mayCache = mayCache;
	this.mayFilter = mayFilter;
	this.size = size;
    }

    public Handler getNewInstance (Connection con, TrafficLoggerHandler tlh, 
				   HttpHeader header, ByteBuffer buffer, 
				   HttpHeader webHeader, 
				   ResourceSource content, boolean mayCache, 
				   boolean mayFilter, long size) {
	return new BaseHandler (con, tlh, header, buffer, webHeader, content, 
				mayCache, mayFilter, size);
    }

    protected Logger getLogger () {
	return con.getLogger ();
    }

    /** Handle the request.
     * A request is made in these steps: 
     * <xmp>
     * sendHeader (); 
     * addCache (); 
     * prepare ();
     * send (); 
     * finishData ();
     * finish ();
     * </xmp>
     * Note that finish is always called, no matter what exceptions are thrown.
     * The middle steps are most probably only performed if the previous steps
     * have all succeded
     */
    public void handle () {
	sendHeader ();
    }

    /** 
     * ®return false this handler never modifies the content.
     */
    public boolean changesContentSize () {
	return false;
    }

    protected void sendHeader () {
	try {
	    HttpHeaderSender responseSender = 
		new HttpHeaderSender (con.getChannel (), con.getSelector (), 
				      getLogger (), tlh.getClient (), 
				      response, false, this);
	} catch (IOException e) {
	    failed (e);
	}
    }
    
    public void httpHeaderSent () {
	addCache ();
	try {
	    prepare ();
	    send ();
	} catch (IOException e) {
	    failed (e);
	}
    }

    /** This method is used to prepare the data for the resource being sent.
     *  This method does nothing here.
     */
    protected void prepare () throws IOException {
	// nothing here.
    }    

    /** This method is used to finish the data for the resource being sent.
     *  This method does nothing here.
     */
    protected void finishData () throws IOException {
	// nothing here.
    }
    
    private void removePrivateParts (HttpHeader header, String type) {
	for (String val : header.getHeaders ("Cache-Control")) {
	    int j = val.indexOf (type);
	    if (j >= 0) {
		String p = val.substring (j + type.length ());
		StringTokenizer st = new StringTokenizer (p, ",\"");
		while (st.hasMoreTokens ()) {
		    String t = st.nextToken ();
		    header.removeHeader (t);
		}
	    }
	}		
    }

    private void removePrivateParts (HttpHeader header) {
	removePrivateParts (header, "private=");
	removePrivateParts (header, "no-cache=");
    }

    /** Mark the current response as a partial response. 
     */
    protected void setPartialContent (long got, long shouldbe) {
	response.setHeader ("RabbIT-Partial", "" + shouldbe);
    }
    
    /** Close nesseccary channels and adjust the cached files.
     *  If you override this one, remember to call super.finish ()!
     * @param good if true then the connection may be restarted, 
     *             if false then the connection may not be restared
     */
    protected void finish (boolean good) {
	boolean ok = false;
	try {
	    if (content != null)
		content.release (con);
	    if (cacheChannel != null) { 
		try {
		    cacheChannel.close ();
		} catch (IOException e) {
		    failed (e);
		}
	    }
	    
	    if (entry != null && mayCache) {
		Cache<HttpHeader, HttpHeader> cache = 
		    con.getProxy ().getCache ();
		String entryName = 
		    cache.getEntryName (entry.getId (), false);
		File f = new File (entryName);
		long filesize = f.length ();
		entry.setSize (filesize);
		String cl = response.getHeader ("Content-Length");
		if (cl == null) {
		    response.removeHeader ("Transfer-Encoding");
		    response.setHeader ("Content-Length", "" + filesize);
		}		
		removePrivateParts (response);
		cache.addEntry (entry);
	    }
	    if (response != null 
		&& response.getHeader ("Content-Length") != null)
		con.setContentLength (response.getHeader ("Content-length"));
	    
	    ok = true;
	} finally {
	    // and clean up...
	    request = null;
	    response = null;
	    content = null;
	    entry = null;
	    cacheChannel = null;
	}
	if (good && ok)
	    con.logAndRestart ();
	else 
	    con.logAndClose (null);
	con = null;
	clientBuffer = null;
    }

    /** Try to use the resource size to decide if we may cache or not. 
     *  If the size is known and the size is bigger than the maximum cache 
     *  size, then we dont want to cache the resource. 
     */
    protected boolean mayCacheFromSize () {
	Cache<HttpHeader, HttpHeader>  cache = con.getProxy ().getCache ();
	if ((size > 0 && size > cache.getMaxSize ()) || 
	    (cache.getMaxSize () == 0))
	    return false;
	return true;
    }

    /** Check if this handler may force the cached resource to be 
     *  less than the cache max size.
     * @return true
     */
    protected boolean mayRestrictCacheSize () {
	return true;
    }

    /** Set the expire time on the cache entry. 
     *  If the expire time is 0 then the cache is not written.
     */
    private void setCacheExpiry () {
	String expires = response.getHeader ("Expires");
	if (expires != null) {
	    Date exp = HttpDateParser.getDate (expires);
	    // common case, handle it...
	    if (exp == null && expires.equals ("0"))
		exp = new Date (0);
	    if (exp != null) {
		long now = System.currentTimeMillis ();
		if (now > exp.getTime ()) {
		    getLogger ().logWarn ("expire date in the past: '" + 
					  expires + "'");
		    entry = null;
		    return;
		}
		entry.setExpires (exp.getTime ());
	    } else {
		getLogger ().logMsg ("unable to parse expire date: '" + 
				     expires + "' for URI: '" + 
				     request.getRequestURI () + "'");
		entry = null;
		return;
	    }
	}
    }

    private void updateRange (CacheEntry<HttpHeader, HttpHeader> old, 
			      HttpHeader response, 
			      PartialCacher pc, 
			      Cache<HttpHeader, HttpHeader> cache) {	
	HttpHeader oldRequest = old.getKey ();
	HttpHeader oldResponse = old.getDataHook (cache);
	String cr = oldResponse.getHeader ("Content-Range");
	if (cr == null) {
	    String cl = oldResponse.getHeader ("Content-Length");
	    if (cl != null) {
		long size = Long.parseLong (cl);
		cr = "bytes 0-" + (size - 1) + "/" + size;
	    }
	}
	ContentRangeParser crp = new ContentRangeParser (cr, getLogger ());
	if (crp.isValid ()) {
	    long start = crp.getStart ();
	    long end = crp.getEnd ();
	    long total = crp.getTotal ();
	    String t = total < 0 ? "*" : Long.toString (total);
	    if (end == pc.getStart () - 1) {
		oldRequest.setHeader ("Range", 
				      "bytes=" + start + "-" + end);
		oldResponse.setHeader ("Content-Range", 
				       "bytes " + start + "-" + 
				       pc.getEnd () + "/" + t);
	    } else {
		oldRequest.addHeader ("Range", 
				      "bytes=" + start + "-" + end);
		oldResponse.addHeader ("Content-Range", 
				       "bytes " + start + "-" + 
				       pc.getEnd () + "/" + t);
	    }
	    cache.entryChanged (old, oldRequest, oldResponse);
	}
    }

    private void setupPartial (CacheEntry<HttpHeader, HttpHeader> oldEntry,
			       CacheEntry<HttpHeader, HttpHeader> entry, 
			       String entryName, 
			       Cache<HttpHeader, HttpHeader> cache) 
	throws IOException {
	if (oldEntry != null) {
	    String oldName = cache.getEntryName (oldEntry);
	    PartialCacher pc = 
		new PartialCacher (getLogger (), oldName, response);
	    cacheChannel = pc.getChannel ();
	    updateRange (oldEntry, response, pc, cache);
	    return;
	} else {
	    entry.setDataHook (response);
	    PartialCacher pc = 
		new PartialCacher (getLogger (), entryName, response);
	    cacheChannel = pc.getChannel ();
	}
    }

    /** Set up the cache stream if available.
     */
    protected void addCache () {
	if (mayCache && mayCacheFromSize ()) {
	    Cache<HttpHeader, HttpHeader> cache = con.getProxy ().getCache ();
	    entry = cache.newEntry (request);
	    setCacheExpiry ();
	    if (entry == null) {
		getLogger ().logAll ("Expiry =< 0 set on entry, will not cache");				     
		return;
	    }
	    String entryName = cache.getEntryName (entry.getId (), false);
	    if (response.getStatusCode ().equals ("206")) {
		CacheEntry<HttpHeader, HttpHeader> oldEntry = 
		    cache.getEntry (request);
		try {
		    setupPartial (oldEntry, entry, entryName, cache);
		} catch (IOException e) {
		    getLogger ().logWarn ("Got IOException, " + 
					  "not updating cache: " + 
					  e + "'");
		    entry = null;
		    cacheChannel = null;		    
		}
	    } else {
		entry.setDataHook (response);
		try {
		    FileOutputStream cacheStream = 
			new FileOutputStream (entryName);
		    /* TODO: implement this: 
		       if (mayRestrictCacheSize ())
		       cacheStream = new MaxSizeOutputStream (cacheStream, 
		       cache.getMaxSize ());
		    */
		    cacheChannel = cacheStream.getChannel ();
		} catch (IOException e) {
		    getLogger ().logWarn ("Got IOException, not caching: " + 
					  e + "'");
		    entry = null;
		    cacheChannel = null;
		}
	    }
	}
    }

    /** This method is used to prepare the stream for the data being sent.
     *  This method does nothing here.
     */
    protected void prepareStream () {
	// nothing here.
    }

    /** Check if this handler supports direct transfers.
     * @return this handler always return true.
     */
    protected boolean mayTransfer () {
	return true;
    }

    protected void send () {
	if (mayTransfer () 
	    && content.length () > 0 
	    && content.supportsTransfer ()) {
	    TransferListener tl = new ContentTransferListener ();
	    TransferHandler th = 
		new TransferHandler (con.getProxy (), 
				     content, con.getChannel (), 
				     tlh.getCache (), tlh.getClient (), tl);
	    th.transfer ();
	} else {
	    content.addBlockListener (this);
	}
    }

    private class ContentTransferListener implements TransferListener {
	public void transferOk () {
	    try {
		finishData ();
		finish (true);
	    } catch (IOException e) {
		failed (e);
	    }	    
	}
	public void failed (Exception cause) {
	    BaseHandler.this.failed (cause);
	}	
    }

    protected void writeCache (ByteBuffer buf) throws IOException {
	// TODO: another thread?
	buf.mark ();
	while (buf.hasRemaining ())
	    cacheChannel.write (buf);
	buf.reset ();
	tlh.getCache ().write (buf.remaining ());
    }

    public void bufferRead (ByteBuffer buf) {
	try {
	    // TODO: do this in another thread? 
	    if (cacheChannel != null)
		writeCache (buf);
	    totalRead += buf.remaining ();
	    new BlockSender (con.getChannel (), con.getSelector (), 
			     getLogger (), tlh.getClient (), 
			     buf, con.getChunking (), this);
	} catch (IOException e) {
	    failed (e);
	}
    }

    public void blockSent () {
	content.addBlockListener (this);
    }
    
    public void finishedRead () {
	try {
	    if (size > 0 && totalRead != size)
		setPartialContent (totalRead, size);
	    finishData ();
	    if (con.getChunking () && !emptyChunkSent) {
		emptyChunkSent = true;
		BlockSentListener bsl = new Finisher ();
		ChunkEnder ce = new ChunkEnder ();
		ce.sendChunkEnding (con.getChannel (), con.getSelector (), 
				    getLogger (), tlh.getClient (), bsl);
	    } else {
		finish (true);
	    }
	} catch (IOException e) {
	    failed (e);
	}
    }

    private class Finisher implements BlockSentListener {
	public void blockSent () {
	    finish (true);
	}
	public void failed (Exception cause) {
	    BaseHandler.this.failed (cause);
	}			
	public void timeout () {
	    BaseHandler.this.timeout ();
	}			
    }

    String getStackTrace (Exception cause) {
	StringWriter sw = new StringWriter ();
	PrintWriter ps = new PrintWriter (sw);
	cause.printStackTrace (ps);
	return sw.toString ();
    }

    protected void removeCache () {
	if (cacheChannel != null) {
	    try {
		cacheChannel.close ();
		Cache<HttpHeader, HttpHeader> cache = 
		    con.getProxy ().getCache ();
		String entryName = cache.getEntryName (entry.getId (), false);
		File f = new File (entryName);
		f.delete ();
		entry = null;
	    } catch (IOException e) {
		getLogger ().logMsg ("failed to remove cache entry: " + e);				     
	    } finally {
		cacheChannel = null;
	    } 
	}
    }
    
    public void failed (Exception cause) {
	if (con != null)
	    getLogger ().logWarn ("BaseHandler: error handling request: " + 
				  getStackTrace (cause));
	removeCache ();
	finish (false);
    }

    public void timeout () {
	if (con != null)
	    getLogger ().logWarn ("BaseHandler: timeout");
	removeCache ();
	finish (false);
    }

    public void setup (Logger logger, SProperties properties) {
	// nothing to do.
    }
}
