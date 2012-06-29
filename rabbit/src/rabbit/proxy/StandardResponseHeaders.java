package rabbit.proxy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import rabbit.http.HttpDateParser;
import rabbit.http.HttpHeader;
import rabbit.util.Config;

/** A class that can create standard response headers. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class StandardResponseHeaders implements HttpGenerator {
    /** The connection handling the response. */
    private Connection con;

    public StandardResponseHeaders (Connection con) {
	this.con = con;
    }

    /** Get a new HttpHeader. This is the same as 
     * getHeader ("HTTP/1.0 200 OK");
     * @return a new HttpHeader.
     */
    public HttpHeader getHeader () {
	return getHeader ("HTTP/1.1 200 OK");
    }
    
    /** Get a new HttpHeader initialized with some data.
     * @param statusLine the statusline of the response.
     * @return a new HttpHeader.
     */
    public HttpHeader getHeader (String statusLine) {
	HttpHeader ret = new HttpHeader ();
	ret.setStatusLine (statusLine);
	ret.setHeader ("Server", con.getProxy ().getServerIdentity ());
	ret.setHeader ("Content-type", "text/html");
	ret.setHeader ("Pragma", "no-cache");
	ret.setHeader ("Date", HttpDateParser.getDateString (new Date ()));
	return ret;    
    }

    /** Get a 200 Ok header
     * @return a 200 HttpHeader .
     */
    public HttpHeader get200 () {
	HttpHeader header = getHeader ("HTTP/1.1 200 Ok");  
	return header;
    }

    private void copyHeaderIfExists (String type, 
				     HttpHeader from, HttpHeader to) {
	String d = from.getHeader (type);
	if (d != null)
	    to.setHeader (type, d);	
    }

    public HttpHeader get206 (String ifRange, HttpHeader header) {
	HttpHeader ret = new HttpHeader ();
	ret.setStatusLine ("HTTP/1.1 206 Partial Content");
	boolean tiny = ifRange != null;
	if (tiny) {
	    String etag = header.getHeader ("ETag");
	    if (etag != null && con.checkStrongEtag (ifRange, etag))
		tiny = false;
	}
	if (tiny) {
	    copyHeaderIfExists ("Date", header, ret);
	    copyHeaderIfExists ("ETag", header, ret);
	    copyHeaderIfExists ("Content-Location", header, ret);	    
	    //copyHeaderIfExists ("Expires", header, ret);
	    /* should do this also in certain conditions...
	       copyHeadersIfExists ("Cache-Control", header, ret);
	       copyHeadersIfExists ("Vary", header, ret);
	    */
	} else {
	    header.copyHeader (ret);
	}
	return ret;
    }    

    /** Get a 304 Not Modified header for the given old header
     * @param oldresp the cached header.
     * @return a 304 HttpHeader .
     */
    public HttpHeader get304 (HttpHeader oldresp) {
	HttpHeader header = getHeader ("HTTP/1.1 304 Not Modified");  
	copyHeaderIfExists ("Date", oldresp, header);
	copyHeaderIfExists ("Content-Location", oldresp, header);
	copyHeaderIfExists ("ETag", oldresp, header);
	String etag = header.getHeader ("Etag");
	if (etag != null && !con.isWeak (etag))
	    copyHeaderIfExists ("Expires", oldresp, header);
	List<String> ccs = oldresp.getHeaders ("Cache-Control");
	for (int i = 0, s = ccs.size (); i < s; i++) 
	    header.addHeader ("Cache-Control", ccs.get (i));
	ccs = oldresp.getHeaders ("Vary");
	for (int i = 0, s = ccs.size (); i < s; i++) 
	    header.addHeader ("Vary", (String)ccs.get (i));	
	return header;
    }

    /** Get a 400 Bad Request header for the given exception.
     * @param exception the Exception handled.
     * @return a HttpHeader for the exception.
     */
    public HttpHeader get400 (Exception exception) {
	// in most cases we should have a header out already, but to be sure...
	HttpHeader header = getHeader ("HTTP/1.1 400 Bad Request ");  
	StringBuilder accreq = 
	    new StringBuilder (HtmlPage.getPageHeader (con, "400 Bad Request") +
			       "Unable to handle request:<br><b><XMP>\n" + 
			       exception +
			       "</XMP></b></body></html>\n");
	header.setContent (accreq.toString ());
	return header;
    }

    /** Get a 403 Forbidden header.
     * @return a HttpHeader.
     */
    public HttpHeader get403 () {
	// in most cases we should have a header out already, but to be sure...
	HttpHeader header = getHeader ("HTTP/1.1 403 Forbidden");  
	StringBuilder accreq = 
	    new StringBuilder (HtmlPage.getPageHeader (con, "403 Forbidden") +
			       "That is forbidden</body></html>");
	header.setContent (accreq.toString ());
	return header;
    }

    /** Get a 404 File not found.
     * @return a HttpHeader.
     */
    public HttpHeader get404 (String file) {
	// in most cases we should have a header out already, but to be sure...
	HttpHeader header = getHeader ("HTTP/1.1 404 File not found");  
	StringBuilder accreq = 
	    new StringBuilder (HtmlPage.getPageHeader (con, "404 File not found") +
			       "File '" + file + "' not found.</body></html>");
	header.setContent (accreq.toString ());
	return header;
    }

    /** Get a 407 Proxy Authentication Required for the given realm and url.
     * @param realm the realm that requires auth.
     * @param url the URL of the request made.
     * @return a suitable HttpHeader.
     */
    public HttpHeader get407 (String realm, URL url) {
	HttpHeader header = 
	    getHeader ("HTTP/1.1 407 Proxy Authentication Required");
	header.setHeader ("Proxy-Authenticate", 
			  "Basic realm=\"" + realm + "\"");
	String h407 = "407 Proxy Authentication Required";
	StringBuilder accreq = 
	    new StringBuilder (HtmlPage.getPageHeader (con, h407) +
			       "access to: <b>" + url + 
			       " </b><br>requires some authentication\n" +
			       "</body></html>\n");
	header.setContent (accreq.toString ());
	return header;
    }

    /** Get a 412 Precondition Failed header.
     * @return a suitable HttpHeader.
     */
    public HttpHeader get412 () {
	HttpHeader header = getHeader ("HTTP/1.1 412 Precondition Failed");
	String sh = "412 Precondition Failed";
	StringBuilder accreq = 
	    new StringBuilder (HtmlPage.getPageHeader (con, sh) + 
			       "</body></html>\n");
	header.setContent (accreq.toString ());
	return header;
    }

    /** Get a 414 Request-URI Too Long
     * @return a suitable HttpHeader.
     */
    public HttpHeader get414 () {
	HttpHeader header = getHeader ("HTTP/1.1 414 Request-URI Too Long");
	String sh = "414 Request-URI Too Long";
	StringBuilder accreq = 
	    new StringBuilder (HtmlPage.getPageHeader (con, sh) + 
			       "</body></html>\n");
	header.setContent (accreq.toString ());
	return header;
    }

    /** Get a Requested Range Not Satisfiable for the given exception.
     * @param exception the Exception made.
     * @return a suitable HttpHeader.
     */
    public HttpHeader get416 (Throwable exception) {
	String sh = "HTTP/1.1 416 Requested Range Not Satisfiable ";
	HttpHeader header = getHeader (sh);
	String shh = "416 Requested Range Not Satisfiable";
	StringBuilder accreq = 
	    new StringBuilder (HtmlPage.getPageHeader (con, shh) +
			       "Request out of range: " + exception + 
			       ".</b>\n</body></html>\n");
	header.setContent (accreq.toString ());
	return header;
    }
    
    /** Get a 417 Expectation Failed header.
     * @param expectation the expectation that failed.
     * @return a suitable HttpHeader.
     */
    public HttpHeader get417 (String expectation) {
	HttpHeader header = getHeader ("HTTP/1.1 417 Expectation failed");
	String sh = "417 Expectation failed";
	StringBuilder accreq = 
	    new StringBuilder (HtmlPage.getPageHeader (con, sh) +
			       "RabbIT does not handle the '" + expectation + 
			       "' kind of expectations yet.</b>\n" +
			       "</body></html>\n");
	header.setContent (accreq.toString ());
	return header;
    }


    /** Get a 500 Internal Server Error header for the given exception.
     * @param exception the Exception made.
     * @return a suitable HttpHeader.
     */
    public HttpHeader get500 (Throwable exception) {
	// in most cases we should have a header out already, but to be sure...
	// normally this only thrashes the page... Too bad.
	HttpHeader header = getHeader ("HTTP/1.1 500 Internal Server Error ");  
	StringWriter sw = new StringWriter ();
	PrintWriter sos = new PrintWriter (sw);
	exception.printStackTrace (sos);
	
	Properties sysprop = System.getProperties ();
	
	HttpProxy proxy = con.getProxy ();
	Config config = proxy.getConfig ();
	String sh = "500 Internal Server Error";
	StringBuilder sb = 
	    new StringBuilder (HtmlPage.getPageHeader (con, sh));
	sb.append ("You have found a bug in RabbIT please report this" + 
		   "(together with the URL you tried to visit) to the " +
		   "<a href=\"http://www.khelekore.org/rabbit/\" target =" + 
		   "\"_top\">RabbIT</a> crew.<br><br>\n" +
		   "<font size = 4>Connection status</font><br><hr noshade>\n" +
		   "status: " + con.getStatus ()  + "<br>\n" + 
		   "started: " + new Date (con.getStarted ()) + "<br>\n" +
		   "keepalive: " + con.getKeepalive () + "<br>\n" + 
		   "meta: " + con.getMeta () + "<br>\n" +
		   "mayusecache: " + con.getMayUseCache () + "<br>\n" + 
		   "maycache: " + con.getMayCache () + "<br>\n" + 
		   "mayfilter: " + con.getMayFilter () + "<br>\n"+ 
		   "requestline: " + con.getRequestLine () + "<br>\n" + 
		   "statuscode: " + con.getStatusCode () + "<br>\n" + 
		   "extrainfo: " + con.getExtraInfo () + "<br>\n" +						
		   "contentlength: " + con.getContentLength () + "<br>\n" + 
		   "<br>\n" +
		   "<font size = 4>Proxy status</font><br>\n<hr noshade>\n" +
		   "proxy version: " + proxy.VERSION + "<br>\n" + 
		   "proxy identity: " + proxy.getServerIdentity () + "<br>\n" +
		   "server host: " + proxy.getHost () + "<br>\n" +
		   "server port: " + proxy.getPort () + "<br>\n" +
		   "accessfilters: " + 
		   config.getProperty ("Filters", "accessfilters") + 
		   "<br>\n" + 
		   "httpinfilters: " + 
		   config.getProperty ("Filters", "httpinfilters") + 
		   "<br>\n" + 
		   "httpoutfilters:" + 
		   config.getProperty ("Filters", "httpoutfilters") +
		   "<br>\n<br>\n" + 					       
		   "<font size = 4>System properties</font><br>\n" + 
		   "<hr noshade>\n" + 
		   "java.version: " + 
		   sysprop.getProperty ("java.version") + "<br>\n" +
		   "java.vendor: " + 
		   sysprop.getProperty ("java.vendor") + "<br>\n" +
		   "os.name: " + 
		   sysprop.getProperty ("os.name") + "<br>\n" +
		   "os.version: " + 
		   sysprop.getProperty ("os.version") + "<br>\n" +
		   "os.arch: " + 
		   sysprop.getProperty ("os.arch") + "<br>\n" +
		   "error is:<BR><XMP>\n" + sw + 
		   "</XMP><br>" + 
		   "<hr noshade>\n" +
		   "</body></html>\n");
	header.setContent (sb.toString ());
	return header;
    }

    String[][] placeTransformers = {
	{"www.", ".com"}, 
	{"", ".com"}, 
	{"www.", ".org"}, 
	{"", ".org"}, 
	{"www.", ".net"}, 
	{"", ".net"}
    };

    /** Get a 504 Gateway Timeout for the given exception.
     * @param exception the Exception made.
     * @return a suitable HttpHeader.
     */
    public HttpHeader get504 (Throwable exception, String requestLine) {
	HttpHeader header = getHeader ("HTTP/1.1 504 Gateway Time-out ");
	
	HttpHeader hh = new HttpHeader ();
	hh.setRequestLine (requestLine);
	String uri = hh.getRequestURI ();
	try {
	    URL u = new URL (uri);
	    StringBuilder content = 
		new StringBuilder ("\n\n<br>Did you mean to go to: <ul>");
	    Set<String> places = new HashSet<String> ();
	    for (int i = 0; i < placeTransformers.length; i++) {
		String pre = placeTransformers[i][0];
		String suf = placeTransformers[i][1];
		String place = getPlace (u, pre, suf); 
		if (place != null && !places.contains (place)) {
		    content.append ("<li><a href=\"" + place + "\">" + 
				    place + "</a></li>\n");
		    places.add (place);
		}
	    }
	    content.append ("</ul>");
	    header.setContent (content.toString ());
	} catch (MalformedURLException e) {
	    // ignore.
	}
	
	return header;
    }

    private String getPlace (URL u, String hostPrefix, String hostSuffix) {
	String host = u.getHost ();
	if (host.startsWith (hostPrefix))
	    hostPrefix = "";
	if (host.endsWith (hostSuffix))
	    hostSuffix = "";
	if (hostPrefix.equals ("") && hostSuffix.equals (""))
	    return null;
	return u.getProtocol () + "://" + hostPrefix + u.getHost () + 
	    hostSuffix + 
	    (u.getPort () == -1 ? "" : ":" + u.getPort ()) + 
	    u.getFile ();		
    }
}
