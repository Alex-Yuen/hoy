package rabbit.proxy;

import java.util.Date;
import java.util.List;
import rabbit.cache.CacheEntry;
import rabbit.http.HttpDateParser;
import rabbit.http.HttpHeader;
import rabbit.util.Logger;

class NotModifiedHandler {
    /** Check if the request allows us to use a "304 Not modified" response.
     * @param in the request being made.
     * @param rh the RequestHandler for this request
     */
    public HttpHeader is304 (HttpHeader in, Connection con, 
			     Connection.RequestHandler rh) {
	CacheEntry entry = rh.entry;
	if (entry == null)
	    return null;
	HttpHeader oldresp = rh.dataHook;

	/* if we should have gotten anything but a 2xx or a 304, 
	 * we should act like IMS and INM was not there.
	 */
	/* This should not be needed, it is checked before we enter this method.
	HttpHeader expfail = checkExpectations (in, oldresp);
	if (expfail != null)
	    return expfail;
	*/

	String ifRange = in.getHeader ("If-Range");
	if (ifRange != null)
	    return null;
	
	String sims = in.getHeader ("If-Modified-Since");
	String sums = in.getHeader ("If-Unmodified-Since");
	List<String> vinm = in.getHeaders ("If-None-Match");
	String et = oldresp.getHeader ("Etag");
	String range = in.getHeader ("Range");
	boolean mustUseStrong = range != null;
	boolean etagMatch = false;
	Date ims = null;
	Date ums = null;
	Date dm = null;
	if (sims != null) 
	    ims = HttpDateParser.getDate (sims);
	if (sums != null)
	    ums = HttpDateParser.getDate (sums);
	if (ims != null || ums != null) {
	    String lm = oldresp.getHeader ("Last-Modified");
	    if (lm == null)
		return ematch (con, etagMatch, oldresp);
	    dm = HttpDateParser.getDate (lm);
	}
	
	long diff;
	if (ums != null && (diff = dm.getTime () - ums.getTime ()) >= 0) {
	    if (mustUseStrong && diff > 60000)
		return con.getHttpGenerator ().get412 ();
	    else 
		return con.getHttpGenerator ().get412 ();
	}

	/* Check if we have a match of etags (Weak comparison). */
	if (et != null) {
	    for (String sinm : vinm) {
		if (sinm != null 
		    && (sinm.equals ("*") 
			|| ((mustUseStrong 
			     && con.checkStrongEtag (et, sinm)) 
			    || (!mustUseStrong 
				&& con.checkWeakEtag (et, sinm)))))
		    etagMatch = true;
	    }
	} 

	if (sims == null) {
	    /* No IMS, act upon INM only. */
	    return ematch (con, etagMatch, oldresp);
	} else {
	    /* Here we may or may not have a etagMatch.
	     * Etagmatch and bad(nonexistant/unparsable..) IMS => act on etag
	     */
	    if (ims == null) {
		con.getLogger ().logInfo ("unparseable date: " + sims + 
					  " for URL: " + in.getRequestURI ());
		return ematch (con, etagMatch, oldresp);
	    }
	    
	    if (dm == null)
		return ematch (con, etagMatch, oldresp);
	    
	    if (dm.after (ims)) 
		return null;
	    if (vinm.size () < 1) {
		if (mustUseStrong) {
		    if (dm.getTime () - ims.getTime () < 60000) 
			return null;
		}
		return con.getHttpGenerator ().get304 (oldresp);
	    }
	    return ematch (con, etagMatch, oldresp);
	}
    }
    
    private HttpHeader ematch (Connection con, 
			       boolean etagMatch, HttpHeader oldresp) {
	if (etagMatch)
	    return con.getHttpGenerator ().get304 (oldresp);
	else 
	    return null;
    }

    private void updateHeader (Connection.RequestHandler rh, 
			       HttpHeader cachedHeader, String header) {
	String h = rh.webHeader.getHeader (header);
	if (h != null)
	    cachedHeader.setHeader (header, h);
    }
    
    void updateHeader (Connection.RequestHandler rh, Logger logger) {
	if (rh.entry == null)
	    return;
	HttpHeader cachedHeader = rh.dataHook;
	updateHeader (rh, cachedHeader, "Date");
	updateHeader (rh, cachedHeader, "Expires");
	updateHeader (rh, cachedHeader, "Content-Location");
	List<String> ccs = rh.webHeader.getHeaders ("Cache-Control");
	if (ccs.size () > 0) {
	    cachedHeader.removeHeader ("Cache-Control");
	    for (String cc : ccs)
		cachedHeader.addHeader ("Cache-Control", cc);
	}
	List<String> varys = rh.webHeader.getHeaders ("Vary");
	if (varys.size () > 0) {
	    cachedHeader.removeHeader ("Vary");
	    for (String v : varys) 
		cachedHeader.addHeader ("Vary", v);
	}
	
	WarningsHandler wh = new WarningsHandler ();
	wh.removeWarnings (logger, cachedHeader, true); 
	wh.updateWarnings (cachedHeader, rh.webHeader);	
    }   
}
