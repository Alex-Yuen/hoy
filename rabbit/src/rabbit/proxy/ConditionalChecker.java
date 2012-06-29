package rabbit.proxy;

import java.util.Date;
import java.util.List;
import rabbit.cache.CacheEntry;
import rabbit.http.HttpDateParser;
import rabbit.http.HttpHeader;
import rabbit.util.Logger;

/** A class used to check for conditional requests. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class ConditionalChecker {
    
    private Logger logger;

    public ConditionalChecker (Logger logger) {
	this.logger = logger;
    }

    boolean checkConditional (Connection con, HttpHeader header, 
			      Connection.RequestHandler rh) {
	CacheEntry<HttpHeader, HttpHeader> entry = rh.entry;	
	return (checkVary (con, header, rh) ||
		checkMaxAge (con, header, rh) || 
		checkNoCache (con, header, rh) ||
		checkQuery (con, header, rh) ||
		checkMinFresh (con, header, rh) || 
		checkRevalidation (con, header, rh));
    }

    private boolean checkVary (Connection con, HttpHeader req, 
			       Connection.RequestHandler rh) {
	CacheEntry<HttpHeader, HttpHeader> entry = rh.entry;
	if (entry == null)
	    return false;
	HttpHeader resp = rh.dataHook;
	List<String> varies = resp.getHeaders ("Vary");
	for (String vary : varies) {
	    if (vary.equals ("*")) {
		con.setMayUseCache (false);
		return false;
	    } else {
		HttpHeader origreq = entry.getKey (); 
		List<String> vals = origreq.getHeaders (vary);
		List<String> nvals = req.getHeaders (vary);
		if (vals.size () != nvals.size ()) {
		    return setupRevalidation (con, req, rh);
		} else {
		    for (String val : vals) {
			int k = nvals.indexOf (val);
			if (k == -1) {
			    return setupRevalidation (con, req, rh);
			}
		    }
		}
	    }
	}
	return false;
    }

    private boolean checkMaxAge (Connection con, HttpHeader req, 
				 Connection.RequestHandler rh, String cached) {
	if (cached != null) {
	    cached = cached.trim(); 
	    if (cached.startsWith ("max-age=0")) {
		return setupRevalidation (con, req, rh);
	    } else {
		Date now = new Date ();
		CacheEntry<HttpHeader, HttpHeader> entry = rh.entry;
		if (checkMaxAge (cached, "max-age=", 
				 entry.getCacheTime (), now) 
		    || checkMaxAge (cached, "s-maxage=", 
				    entry.getCacheTime (), now)) {
		    con.setMayUseCache (false);
		    return false;
		}
	    }
	}
	return false;
    }
    
    private boolean checkMaxAge (String cached, String type, 
				 long cachetime, Date now) {
	if (cached.startsWith (type)) {
	    String secs = cached.substring (type.length ());
	    try {
		long l = Long.parseLong (secs) * 1000;
		long ad = now.getTime () - cachetime;
		if (ad > l)
		    return true;
	    } catch (NumberFormatException e) {
		logger.logWarn ("Bad number for max-age: '" + 
				cached.substring (8) + "'");		    
	    }
	}
	return false;
    }

    protected boolean checkMaxAge (Connection con, HttpHeader req, 
				   Connection.RequestHandler rh) {
	CacheEntry<HttpHeader, HttpHeader> entry = rh.entry;
	if (entry == null)
	    return false;
 	List<String> ccs = req.getHeaders ("Cache-Control");
	for (String cc : ccs) {
	    if (checkMaxAge (con, req, rh, cc))
	 	return true;
	}
	return false;
    }

    private boolean checkNoCacheHeader (List<String> v) {
	for (String nc : v) 
	    if (nc.equals ("no-cache"))
		return true;
	return false;
    }

    private boolean checkNoCache (Connection con, HttpHeader header, 
				  Connection.RequestHandler rh) {
	CacheEntry<HttpHeader, HttpHeader> entry = rh.entry;
	if (entry == null)
	    return false;
	boolean noCache = false;
	// Only check the response header, 
	// request headers with no-cache == refetch.
	HttpHeader resp = rh.dataHook;
	noCache = checkNoCacheHeader (resp.getHeaders ("Cache-Control"));
	if (noCache) {
	    return setupRevalidation (con, header, rh);
	}
	return false;
    }

    private boolean checkQuery (Connection con, HttpHeader header, 
				Connection.RequestHandler rh) {
	CacheEntry<HttpHeader, HttpHeader> entry = rh.entry;
	if (entry == null)
	    return false;
	String uri = header.getRequestURI ();
	int i = uri.indexOf ('?');
	if (i >= 0) {
	    return setupRevalidation (con, header, rh);
	}
	return false;
    }

    protected long getCacheControlValue (HttpHeader header, String cc) {
	for (String ncc : header.getHeaders ("Cache-Control")) {
	    String[] sts = ncc.split (",");
	    for (String nc : sts) {
		nc = nc.trim ();
		if (nc.startsWith (cc))
		    return Long.parseLong (nc.substring (cc.length ()));
	    }
	}
	return -1;
    }
    
    private boolean checkMinFresh (Connection con, HttpHeader header, 
				   Connection.RequestHandler rh) {
	CacheEntry<HttpHeader, HttpHeader> entry = rh.entry;
	if (entry == null)
	    return false;
	long minFresh = getCacheControlValue (header, "min-fresh=");
	if (minFresh == -1)
	    return false;
	long maxAge = getCacheControlValue (rh.dataHook, "max-age=");
	if (maxAge == -1)
	    return false;
	long currentAge = (System.currentTimeMillis () - 
			   entry.getCacheTime ()) / 1000;
	if ((maxAge - currentAge) < minFresh)
	    return setupRevalidation (con, header, rh);
	return false;
    }

    private boolean checkRevalidation (Connection con, HttpHeader header, 
				       Connection.RequestHandler rh) {
	CacheEntry<HttpHeader, HttpHeader> entry = rh.entry;
	if (entry == null)
	    return false;
	
	HttpHeader resp = rh.dataHook;
	for (String ncc : resp.getHeaders ("Cache-Control")) {
	    String[] sts = ncc.split (",");
	    for (String nc : sts) {
		nc = nc.trim ();
		if (nc.equals ("must-revalidate") ||
		    nc.equals ("proxy-revalidate")) {
		    con.setMustRevalidate (true);
		    long maxAge = 
			getCacheControlValue (rh.dataHook, "max-age=");
		    if (maxAge >= 0) {
			long currentAge = (System.currentTimeMillis () - 
					   entry.getCacheTime ()) / 1000;
			if (maxAge == 0 || currentAge > maxAge) {
			    return setupRevalidation (con, header, rh);
			}
		    }
		} else if (nc.startsWith ("s-maxage=")) {
		    con.setMustRevalidate (true);
		    long sm = 
			Long.parseLong (nc.substring ("s-maxage=".length ()));
		    if (sm >= 0) {
			long currentAge = (System.currentTimeMillis () - 
					   entry.getCacheTime ()) / 1000;
			if (sm == 0 || currentAge > sm) {			
			    return setupRevalidation (con, header, rh);
			}
		    }
		}
	    }
	}
	return false;
    }

    private boolean setupRevalidation (Connection con, HttpHeader req, 
				       Connection.RequestHandler rh) {
	CacheEntry<HttpHeader, HttpHeader> entry = rh.entry;
	con.setMayUseCache (false);
	String method = req.getMethod ();
	// if we can not filter (noproxy-request) we can not revalidate...
	if (method.equals ("GET") && entry != null && con.getMayFilter ()) {
	    HttpHeader resp = rh.dataHook;
	    String etag = resp.getHeader ("ETag");
	    String lmod = resp.getHeader ("Last-Modified");
	    if (etag != null) {
		String inm = req.getHeader ("If-None-Match");
		if (inm == null) {
		    req.setHeader ("If-None-Match", etag);
		    con.setAddedINM (true);
		}
		return true;
	    } else if (lmod != null) {
		String ims = req.getHeader ("If-Modified-Since");
		if (ims == null) {
		    req.setHeader ("If-Modified-Since", lmod);
		    con.setAddedIMS (true);
		}
		return true;
	    } else {
		con.setMayUseCache (false);
		return false;
	    }
	}
	return false;
    }

    boolean checkMaxStale (HttpHeader req, Connection.RequestHandler rh) {
	for (String cc : req.getHeaders ("Cache-Control")) {
	    cc = cc.trim ();
	    if (cc.equals ("max-stale")) {
		if (rh.entry != null) {
		    HttpHeader resp = rh.dataHook;
		    long maxAge = 
			rh.cond.getCacheControlValue (resp, "max-age=");
		    if (maxAge >= 0) {
			long now = System.currentTimeMillis ();
			long secs = (now - rh.entry.getCacheTime ()) / 1000;
			long currentAge = secs;
			String age = resp.getHeader ("Age");
			if (age != null)
			    currentAge += Long.parseLong (age);
			if (currentAge > maxAge) {
			    resp.addHeader ("Warning", 
					    "110 RabbIT \"Response is stale\"");
			}
		    }
		}
		return true;
	    }
	}
	return false;
    }

    private void checkStaleHeader (HttpHeader header, HttpHeader webHeader, 
				   HttpHeader cachedWebHeader, String str, 
				   Connection con) {
	String cln = webHeader.getHeader (str);
	String clo = cachedWebHeader.getHeader (str);
	if (clo != null) {
	    if (!clo.equals (cln)) {
		con.getProxy ().getCache ().remove (header);
		return;
	    }
	} else { 
	    if (cln != null) {
		con.getProxy ().getCache ().remove (header); 
		return; 
	    }
	}
    }

    void checkStaleCache (HttpHeader header, Connection con, 
			  Connection.RequestHandler rh) {
	if (rh.entry == null)
	    return;
	if (rh.webHeader.getStatusCode ().trim ().equals ("304"))
	    return;
	HttpHeader cachedWebHeader = rh.dataHook;

	String sd = rh.webHeader.getHeader ("Date");
	String cd = cachedWebHeader.getHeader ("Date");
	if (sd != null && cd != null) {
	    Date d1 = HttpDateParser.getDate (sd);
	    Date d2 = HttpDateParser.getDate (cd);
	    // if we get a response with a date older than we have, 
	    // we keep our cache.
	    if (d1 != null && d1.before (d2)) {
		con.setMayCache (false);
		return;
	    }
	}
	
	if (rh.webHeader.getStatusCode ().equals ("200"))
	    checkStaleHeader (header, rh.webHeader, 
			      cachedWebHeader, "Content-Length", con);
	checkStaleHeader (header, rh.webHeader, 
			  cachedWebHeader, "Content-MD5", con);
	checkStaleHeader (header, rh.webHeader, 
			  cachedWebHeader, "ETag", con);
	checkStaleHeader (header, rh.webHeader, 
			  cachedWebHeader, "Last-Modified", con);
    }
}
