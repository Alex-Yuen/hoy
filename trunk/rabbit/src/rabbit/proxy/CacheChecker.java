package rabbit.proxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Date;
import rabbit.cache.Cache;
import rabbit.cache.CacheEntry;
import rabbit.http.HttpDateParser;
import rabbit.http.HttpHeader;
import rabbit.util.Logger;

/** A class to verify if a cache entry can be used.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class CacheChecker {
    
    private static final String EXP_ERR = "No expected header found";
    
    HttpHeader checkExpectations (Connection con, 
				  HttpHeader header, 
				  HttpHeader webheader) {
	String exp = header.getHeader ("Expect");
	if (exp == null)
	    return null;
	if (exp.equals ("100-continue")) {
	    String status = webheader.getStatusCode ();
	    if (status.equals ("200") || status.equals ("304"))
		return null;
	    return con.getHttpGenerator ().get417 (exp);
	}

	String[] sts = exp.split (";");
	for (String e : sts) {
	    int i = e.indexOf ('=');
	    if (i == -1 || i == e.length () -1) 
		return con.getHttpGenerator ().get417 (e);
	    String type = e.substring (0, i);
	    String value = e.substring (i + 1);
	    if (type.equals ("expect")) {
		String h = webheader.getHeader (value);
		if (h == null)
		    return con.getHttpGenerator ().get417 (EXP_ERR);
	    }
	}
	
	return con.getHttpGenerator ().get417 (exp);
    }

    private HttpHeader checkIfMatch (Connection con, 
				     HttpHeader header, 
				     Connection.RequestHandler rh) {
	CacheEntry entry = rh.entry;
	if (entry == null)
	    return null;
	HttpHeader oldresp = rh.dataHook;
	HttpHeader expfail = checkExpectations (con, header, oldresp);
	if (expfail != null)
	    return expfail;
	String im = header.getHeader ("If-Match");
	if (im == null)
	    return null;
	String et = oldresp.getHeader ("Etag");
	if (!con.checkStrongEtag (et, im))
	    return con.getHttpGenerator ().get412 ();
	return null;
    }    

    /** Check if we can use the cached entry.
     * @param header the reques.
     * @param rh the RequestHandler
     * @return true if the request was handled, false otherwise.
     */
    public boolean checkCachedEntry (Connection con, 
				     HttpHeader header,
				     Connection.RequestHandler rh) {
	con.getCounter ().inc ("Cache hits");
	con.setKeepalive (true);
	HttpHeader resp = checkIfMatch (con, header, rh);
	if (resp == null) {
	    NotModifiedHandler nmh = new NotModifiedHandler ();
	    resp = nmh.is304 (header, con, rh);
	}
	if (resp != null) {
	    con.sendAndRestart (resp);
	    return true;
	}		
	con.setMayCache (false);
	try {		    
	    resp = con.setupCachedEntry (rh); 
	    if (resp != null) {
		con.sendAndClose (resp);
		return true;
	    }
	} catch (FileNotFoundException e) {
	    rh.content = null; // ignore sorta, to pull resource from the web.
	    rh.entry = null;
	} catch (IOException e) {
	    rh.content = null;
	    rh.entry = null;
	}
	return false;
    }

    /*
      If-None-Match: "tag-hbhpjfvtsy"\r\n
      If-Modified-Since: Thu, 11 Apr 2002 20:56:16 GMT\r\n
      If-Range: "tag-hbhpjfvtsy"\r\n

      -----------------------------------

      If-Unmodified-Since: Thu, 11 Apr 2002 20:56:16 GMT\r\n
      If-Match: "tag-ajbqyucqaf"\r\n
      If-Range: "tag-ajbqyucqaf"\r\n

    */
    public boolean checkConditions (Connection con, 
				    HttpHeader header, HttpHeader webheader) {
	String inm = header.getHeader ("If-None-Match");
	if (inm != null) {
	    String etag = webheader.getHeader ("ETag");
	    if (!con.checkWeakEtag (inm, etag))
		return false;
	}
	Date dm = null;
	String sims = header.getHeader ("If-Modified-Since");
	if (sims != null) {
	    Date ims = HttpDateParser.getDate (sims);
	    String lm = webheader.getHeader ("Last-Modified");
	    if (lm != null) {
		dm = HttpDateParser.getDate (lm);
		if (dm.getTime () - ims.getTime () < 60000) //dm.after (ims))
		    return false;
	    }
	}
	String sums = header.getHeader ("If-Unmodified-Since");
	if (sums != null) {
	    Date ums = HttpDateParser.getDate (sums);
	    if (dm != null) {
		if (dm.after (ums))
		    return false;
	    } else {
		String lm = webheader.getHeader ("Last-Modified");
		if (lm != null) {
		    dm = HttpDateParser.getDate (lm);
		    if (dm.after (ums))
			return false;		
		} 
	    }
	}
	return true;
    }

    private void removeCaches (HttpHeader request, 
			       HttpHeader webHeader, String type, 
			       Cache<HttpHeader, HttpHeader> cache, 
			       Logger logger) {
	String loc = webHeader.getHeader (type);
	if (loc == null)
	    return;
	try {
	    URL u = new URL (request.getRequestURI ());
	    URL u2 = new URL (u, loc);
	    String host1 = u.getHost ();
	    String host2 = u.getHost ();
	    if (!host1.equals (host2))
		return;
	    int port1 = u.getPort ();
	    if (port1 == -1)
		port1 = 80;
	    int port2 = u2.getPort ();
	    if (port2 == -1)
		port2 = 80;
	    if (port1 != port2)
		return;
	    HttpHeader h = new HttpHeader ();
	    h.setRequestURI (u2.toString ());
	    cache.remove (h);
	} catch (MalformedURLException e) {
	    logger.logWarn ("removeCaches got bad url: " +
				  request.getRequestURI () + ", " + 
				  loc + ": " + e);
	}
    }

    private void removeCaches (HttpHeader request, HttpHeader webHeader,
			       Cache<HttpHeader, HttpHeader> cache, 
			       Logger logger) {
	removeCaches (request, webHeader, "Location", cache, logger);
	removeCaches (request, webHeader, "Content-Location", cache, logger);
    }

    void removeOtherStaleCaches (HttpHeader request, HttpHeader webHeader, 
				 Cache<HttpHeader, HttpHeader> cache, 
				 Logger logger) {
	String method = request.getMethod ();
	String status = webHeader.getStatusCode ();
	if ((method.equals ("PUT") || method.equals ("POST")) 
	    && status.equals ("201")) {
	    removeCaches (request, webHeader, cache, logger);
	} else if (method.equals ("DELETE") && status.equals ("200")) {
	    removeCaches (request, webHeader, cache, logger);
	}
    }
}
