package rabbit.meta;

import rabbit.cache.Cache;
import rabbit.http.HttpHeader;

/** Clears the cache completely
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ClearCache extends BaseMetaHandler {
    private boolean timeToClean = false;

    protected String getPageHeader () {
	return "Clearing cache";
    }
    
    /** Add the page information */
    protected PageCompletion addPageInformation (StringBuilder sb) {
	// Send the wait message on the first time.
	// Start the cleaning when the wait message has been sent.
	if (!timeToClean) {
	    sb.append ("Please wait...<br>\n");
	    timeToClean = true;
	    return PageCompletion.PAGE_NOT_DONE;
	} else {
	    Cache<HttpHeader, HttpHeader> cache = con.getProxy ().getCache ();
	    cache.clear ();
	    sb.append ("<font color=\"blue\">done!</font>\n");
	    return PageCompletion.PAGE_DONE;
	}
    }
}
