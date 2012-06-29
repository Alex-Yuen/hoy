package rabbit.filter;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import rabbit.http.HttpDateParser;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.proxy.HttpGenerator;
import rabbit.proxy.HttpProxy;
import rabbit.util.Coder;
import rabbit.util.Logger;
import rabbit.util.SProperties;
import rabbit.util.SimpleUserHandler;

/** This is a class that filter http headers to make them nice.
 *  This filter sets up username and password if supplied and 
 *  also sets up keepalive.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpBaseFilter implements HttpFilter {
    public static final String NOPROXY = "http://noproxy.";
    private static final BigInteger ZERO = new BigInteger ("0");
    private static final BigInteger ONE = new BigInteger ("1");

    private List<String> removes = new ArrayList<String> ();
    private boolean cookieId = false;
    private SimpleUserHandler userHandler = new SimpleUserHandler ();

    /** 
     */
    public HttpBaseFilter () {
    }

    /** We got a proxy authentication, handle it...
     * @param uap the authentication string.
     * @param con the Connection.
     */
    private void handleProxyAuthentication (String uap, Connection con) {
	// guess we should handle digest here also.. :-/
	if (uap.startsWith ("Basic ")) {
	    uap = uap.substring ("Basic ".length ());
	    String userapass = Coder.uudecode (uap);
	    int i = -1;
	    if ((i = userapass.indexOf (":")) > -1) {
		String userid = userapass.substring (0, i);
		String pass = userapass.substring (i + 1);
		con.setUserName (userid);
		con.setPassword (pass);
	    }
	}
    }

    /** Handle the authentications.
     *  If we have a proxy-authentication we set the 
     *  connections username and password.
     *  We also rewrite authentications in the URL to a standard header,
     *  since java does not handle them.
     * @param header the Request.
     * @param con the Connection.
     */
    private void handleAuthentications (HttpHeader header, Connection con) {
	String uap = header.getHeader ("Proxy-Authorization");
	if (uap != null) 
	    handleProxyAuthentication (uap, con);
	
        /*
         * Java URL:s doesn't handle user/pass in the URL as in rfc1738:
         * //<user>:<password>@<host>:<port>/<url-path>
         *
         * Convert these to an Authorization header and remove from URI.
         */	
        String requestURI = header.getRequestURI();
        
        int s3, s4, s5;
        if ((s3 = requestURI.indexOf("//")) >= 0 
	    && (s4 = requestURI.indexOf('/', s3 + 2)) >= 0 
	    && (s5 = requestURI.indexOf('@', s3 + 2)) >= 0 
	    && s5 < s4) {
            
            String userPass = requestURI.substring(s3 + 2, s5);
            header.setHeader("Authorization", "Basic " +
			     Coder.uuencode(userPass));
	    
            header.setRequestURI(requestURI.substring(0, s3 + 2) +
				 requestURI.substring(s5 + 1));
        }
    }

    /** Check if this is a noproxy request, and if so handle it.
     * @param requri the requested resource.
     * @param header the actual request.
     * @param con the Connection.
     * @return the new request URI
     */
    private String handleNoProxyRequest (String requri, HttpHeader header, 
					 Connection con) {
	requri = "http://" + requri.substring (NOPROXY.length ());
	header.setRequestURI (requri);
	con.setMayUseCache (false);
	con.setMayCache (false);
	con.setMayFilter (false);
	return requri;
    }

    /** Check that the requested URL is valid and if it is a meta request.
     * @param requri the requested resource.
     * @param header the actual request.
     * @param con the Connection.
     */
    private HttpHeader handleURLSetup (String requri, HttpHeader header, 
				       Connection con) {
	try {
	    // is this request to our self?
	    HttpProxy proxy = con.getProxy ();
	    if (requri != null && requri.charAt (0) == '/') {
		requri = 
		    "http://" + proxy.getHost ().getHostName () + 
		    ":" + proxy.getPort () + requri;
		header.setRequestURI (requri);
	    }
	    URL url = new URL (requri);
	    header.setHeader ("Host",  
			      url.getPort () > -1 ? 
			      url.getHost () + ":" + url.getPort () : 
			      url.getHost ());
	    int urlport = url.getPort ();
	    // This could give a DNS-error if no DNS is available.
	    // And since we have not decided if we should proxy it 
	    // up the chain yet, do string comparison..
	    // InetAddress urlhost = InetAddress.getByName (url.getHost ());	    
	    String uhost = url.getHost ();
	    if (proxy.isSelf (uhost, urlport)) {
		con.setMayUseCache (false);
		con.setMayCache (false);
		con.setMayFilter (false);
		if (!userHandler.isValidUser (con.getUserName (), 
					      con.getPassword ()) 
		    && !isPublic (url)) {
		    HttpHeader ret = 
			con.getHttpGenerator ().get407 (uhost + ":" + 
							  urlport, url);
		    return ret;
		}
		con.setMeta (true);
	    }
	} catch (MalformedURLException e) {   
	    return con.getHttpGenerator ().get400 (e);
	}
	return null;
    }
    
    /** Remove all "Connection" tokens from the header.
     * @param header the HttpHeader that needs to be cleaned.
     */
    private void removeConnectionTokens (HttpHeader header) {
	List<String> cons = header.getHeaders ("Connection");	
	for (String val : cons) {
	    /* ok, split it... */
	    int s = -1;
	    int start = 0;
	    while (start < val.length ()) {
		while (val.length () > start + 1 
		       && (val.charAt (start) == ' ' 
			   || val.charAt (start) == ','))
		    start++;
		if (val.length () > start + 1 && val.charAt (start) == '"') {
		    start++;
		    s = val.indexOf ('"', start);
		    while (s >= -1 
			   && val.charAt (s - 1) == '\\' 
			   && val.length () > s + 1)
			s = val.indexOf ('"', s + 1);			
		    if (s == -1)
			s = val.length ();
		    String t = val.substring (start, s).trim ();
		    /* ok, unquote the value... */
		    StringBuilder sb = new StringBuilder (t.length ());
		    for (int c = 0; c < t.length (); c++) {
			char z = t.charAt (c);
			if (z != '\\')
			    sb.append (z);
		    }
		    t = sb.toString ();
		    header.removeHeader (t);
		    s = val.indexOf (',', s + 1);
		    if (s == -1)
			start = val.length ();
		    else 
			start = s + 1;
		} else {
		    s = val.indexOf (',', start + 1);
		    if (s == -1)
			s = val.length ();
		    String t = val.substring (start, s).trim ();
		    header.removeHeader (t);
		    start = s + 1;
		}
	    }
	}
    }

    private HttpHeader checkMaxForwards (Connection con, HttpHeader header, 
					 String val) {
	try {
	    BigInteger bi = new BigInteger (val);
	    if (bi.equals (ZERO)) {
		if (header.getMethod ().equals ("TRACE")) {
		    HttpHeader ret = con.getHttpGenerator ().get200 ();
		    ret.setContent (header.toString ());
		    return ret;
		} else {
		    HttpHeader ret = con.getHttpGenerator ().get200 ();
		    ret.setHeader ("Allow", "GET,HEAD,POST,OPTIONS,TRACE");
		    ret.setHeader ("Content-Length", "0");
		    return ret;
		}
	    } else {
		BigInteger b3 = bi.subtract (ONE);
		header.setHeader ("Max-Forwards", b3.toString ());
	    }
	} catch (NumberFormatException e) {
	    HttpProxy proxy = con.getProxy ();
	    proxy.getLogger ().logWarn ("Bad number for Max-Forwards: '" +
					val + "'");
	}
	return null;
    }

    /** test if a socket/header combination is valid or return a new HttpHeader.
     * @param socket the SocketChannel that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return null if everything is fine or a HttpHeader describing 
     *         the error (like a 403).
     */
    public HttpHeader doHttpInFiltering (SocketChannel socket, 
					 HttpHeader header, Connection con) {
	// ok, no real header then dont do a thing.
	if (header.isDot9Request ()) {
	    con.setMayCache (false);
	    con.setMayUseCache (false);
	    con.setKeepalive (false);
	    return null;
	}
	
	handleAuthentications (header, con);
	
	boolean maychunk = true;
	boolean mayKeepAlive = true;
    
	String requestVersion = header.getHTTPVersion ().toUpperCase ();
	if (requestVersion.equals ("HTTP/1.1")) {
	    String host = header.getHeader ("Host");
	    if (host == null) {
		Exception exe = 
		    new Exception ("No host header set in HTTP/1.1 request");
		HttpHeader ret = 
		    con.getHttpGenerator ().get400 (exe);
		return ret;
	    }
	    maychunk = true;    
	    String closeit = header.getHeader ("Proxy-Connection");
	    if (closeit == null)
		closeit = header.getHeader ("Connection");
	    mayKeepAlive = (closeit == null 
			    || !closeit.equalsIgnoreCase ("close"));
	} else {
	    header.setHTTPVersion ("HTTP/1.1");
	    maychunk = false;
	    // stupid netscape to not follow the standards, 
	    // only "Connection" should be used...
	    String keepalive = header.getHeader ("Proxy-Connection");
	    mayKeepAlive = (keepalive != null 
			    && keepalive.equalsIgnoreCase ("Keep-Alive"));
	    if (!mayKeepAlive) {
		keepalive = header.getHeader ("Connection");
		mayKeepAlive = (keepalive != null 
				&& keepalive.equalsIgnoreCase ("Keep-Alive"));
	    }	
	}
	
	boolean useCached = true;
	boolean cacheAllowed = true;
	// damn how many system that use cookies with id's
	/* 
        System.out.println ("auth: " + header.getHeader ("authorization") + 
                            ", cookie:" + header.getHeader ("cookie") + 
                            ", Pragma: " + header.getHeader ("Pragma") + 
                            ", Cache: " + header.getHeader ("Cache-Control"));
	*/
	//String cached = header.getHeader ("Pragma");
	List<String> ccs = header.getHeaders ("Cache-Control");
	for (String cached : ccs) {
	    cached = cached.trim ();
	    if (cached.equals ("no-store")) {
		useCached = false;
		cacheAllowed = false;
	    } else if (cached.equals ("no-cache")) {
		useCached = false;
	    } else if (cached.equals ("no-transform")) {
		useCached = false;     // cache is transformed.
		cacheAllowed = false;  // dont store, no point.
		con.setMayFilter (false);
	    }
	}

	ccs = header.getHeaders ("Pragma");
	for (String cached : ccs) {
	    cached = cached.trim ();
	    if (cached.equals ("no-cache")) {
		useCached = false;
	    }
	}

	
	String method = header.getMethod ().trim ();
	if (!method.equals ("GET") && !method.equals ("HEAD")) {
	    useCached = false;
	    cacheAllowed = false;
	    //mayKeepAlive = false;
	} else if (method.equals ("HEAD")) {
	    maychunk = false;
	}
	con.setChunking (maychunk);

	String mf = header.getHeader ("Max-Forwards");
	if (mf != null) {
	    HttpHeader ret = checkMaxForwards (con, header, mf);
	    if (ret != null) {
		return ret;
	    }
	}

	String auths = header.getHeader ("authorization");
	if (auths != null) {
	    useCached = false;     // dont use cached files, 
	    cacheAllowed = false;  // and dont cache it.
	} else if (cookieId) {
	    String cookie = header.getHeader ("cookie");
	    String lccookie = null;
	    if (cookie != null &&     // cookie-passwords suck.
		(((lccookie = cookie.toLowerCase ()).indexOf ("password") >= 0)
		 || (lccookie.indexOf ("id") >= 0))) {
		useCached = false;     // dont use cached files, 
		cacheAllowed = false;  // and dont cache it.
	    }
	}
	con.setMayUseCache (useCached);
	con.setMayCache (cacheAllowed);
	con.setKeepalive (mayKeepAlive);
	
	String requri = header.getRequestURI ();
	if (requri.toLowerCase ().startsWith (NOPROXY)) 
	    requri = handleNoProxyRequest (requri, header, con);       
		
	HttpHeader headerr = handleURLSetup (requri, header, con);
	if (headerr != null) 
	    return headerr;
	
	removeConnectionTokens (header);
	int rsize = removes.size ();
	for (int i = 0; i < rsize; i++) {
	    String r = removes.get (i);
	    header.removeHeader (r);
	}
	
	HttpProxy proxy = con.getProxy ();
	if (proxy.isProxyConnected ()) {
	    String auth = proxy.getProxyAuthString ();
	    // it should look like this (using RabbIT:RabbIT):
	    // Proxy-authorization: Basic UmFiYklUOlJhYmJJVA==
	    header.setHeader ("Proxy-authorization", 
			      "Basic " + Coder.uuencode (auth));
	}

	// try to use keepalive backwards.
	// This is not needed since it is a HTTP/1.1 request.
	// header.setHeader ("Connection", "Keep-Alive");

	return null;
    }

    private boolean checkCacheControl (String cachecontrol) {
	String[] caches = cachecontrol.split (",");
	for (String cached : caches) {
	    cached = cached.trim ();
	    if (cached.equals ("no-store"))
		return false;
	    if (cached.equals ("private")) 
		return false;
	}
	return true;
    }

    /** test if a socket/header combination is valid or return a new HttpHeader.
     * @param socket the SocketChannel that made the request.
     * @param header the actual response made.
     * @param con the Connection handling the request.
     * @return null if everything is fine or a HttpHeader 
     *         describing the error (like a 403).
     */
    public HttpHeader doHttpOutFiltering (SocketChannel socket, 
					  HttpHeader header, Connection con) {
	boolean useCache = true;
	//String cached = header.getHeader ("Pragma");
	List<String> ccs = header.getHeaders ("Cache-Control");
	for (String cached : ccs) {
	    if (cached != null) 
		useCache &= checkCacheControl (cached);
	}
	
	String status = header.getStatusCode ().trim ();
	if (!(status.equals ("200") || status.equals ("206") 
	      || status.equals ("304"))) {
	    con.setKeepalive (false);    
	    useCache = false;
	}

	String age = header.getHeader ("Age");
	long secs = 0;
	if (age == null)
	    age = "0";
	try {
	    secs = Long.parseLong (age);
	} catch (NumberFormatException e) {
	    // ignore, we already have a warning for this..
	}
	if (secs > 60 * 60 * 24) 
	    header.setHeader ("Warning", "113 RabbIT \"Heuristic expiration\"");
	
	header.setResponseHTTPVersion ("HTTP/1.1");
	con.setMayCache (useCache);

	/** Dont filter compressed pages... 
	    someone else has already thought about speed... */
	String ce = header.getHeader ("Content-Encoding");
	if (ce != null &&
	    (ce.equalsIgnoreCase ("gzip") 
	     || (ce.equalsIgnoreCase ("compress"))))
	    con.setMayFilter (false);

	/** Try to make sure that IE can handle NTLM authentication. */
	/** This does not work. */
	/*
	List ls = header.getHeaders ("WWW-Authenticate");
	for (Iterator i = ls.iterator (); i.hasNext (); ) {
	    String s = (String)i.next ();
	    if (s.indexOf ("Negotiate") != -1 || 
		s.indexOf ("NTLM") != -1) {
		con.setMayFilter (false);
		con.setChunking (false);
	    }
	}
	*/
	
	removeConnectionTokens (header);
	for (String r : removes)
	    header.removeHeader (r);
	
	String d = header.getHeader ("Date");
	if (d == null) {
	    // ok, maybe we should check if there is an Age set
	    // otherwise we can do like this.
	    header.setHeader ("Date", 
			      HttpDateParser.getDateString (new Date ()));
	}
	
	String cl = header.getHeader ("Content-Length");
	if (cl == null) 
	    if (!con.getChunking ())
		con.setKeepalive (false);

	return null;
    }

    /** Setup this class with the given properties.
     * @param properties the new configuration of this class.
     */
    public void setup (Logger logger, SProperties properties) {
	removes.clear ();
	String rs = properties.getProperty ("remove", "");
	String[] sts = rs.split (",");
	for (String r : sts)
	    removes.add (r.trim ());
	String userFile = properties.getProperty ("userfile", "conf/users");
	userHandler.setFile (userFile, logger);
	String cid = properties.getProperty ("cookieid", "false");
	cookieId = cid.equals ("true");
    }
    
    /** Check if a given url is a public URL of the Proxy.
     * @param url the URL to check.
     * @return true if this url has public access, false otherwise.
     */
    public boolean isPublic (URL url) {
	String file = url.getFile ();
	if (file.startsWith ("/FileSender/public/"))
	    return true;
	return false;
    }
}
