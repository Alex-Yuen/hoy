package rabbit.proxy;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import rabbit.filter.HttpFilter;
import rabbit.http.HttpHeader;
import rabbit.util.Config;
import rabbit.util.Logger;

/** A class to load and run the HttpFilters.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class HttpHeaderFilterer {
    private List<HttpFilter> httpInFilters;
    private List<HttpFilter> httpOutFilters;
    
    public HttpHeaderFilterer (String in, String out, 
			       Config config, HttpProxy proxy) {
	httpInFilters = new ArrayList<HttpFilter> ();
	loadHttpFilters (in, httpInFilters, config, proxy);
	
	httpOutFilters = new ArrayList<HttpFilter> ();
	loadHttpFilters (out, httpOutFilters, config, proxy);
    }

    /** Runs all input filters on the given header. 
     * @param con the Connection handling the request
     * @param channel the SocketChannel for the client
     * @param in the request. 
     * @return null if all is ok, a HttpHeader if this request is blocked.
     */
    public HttpHeader filterHttpIn (Connection con, 
				    SocketChannel channel, HttpHeader in) {
	for (HttpFilter hf : httpInFilters) {
	    HttpHeader badresponse = 
		hf.doHttpInFiltering (channel, in, con);
	    if (badresponse != null)
		return badresponse;	    
	}
	return null;
    }

    /** Runs all output filters on the given header. 
     * @param con the Connection handling the request
     * @param channel the SocketChannel for the client
     * @param in the response. 
     * @return null if all is ok, a HttpHeader if this request is blocked.
     */
    public HttpHeader filterHttpOut (Connection con, 
				    SocketChannel channel, HttpHeader in) {
	for (HttpFilter hf : httpOutFilters) {
	    HttpHeader badresponse = 
		hf.doHttpOutFiltering (channel, in, con);
	    if (badresponse != null)
		return badresponse;	    
	}
	return null;
    }

    private void loadHttpFilters (String filters, List<HttpFilter> ls,
				  Config config, HttpProxy proxy) {
	String[] filterArray = filters.split (",");
	for (String className : filterArray) {
	    Logger log = proxy.getLogger ();
	    try {
		className = className.trim ();
		Class<? extends HttpFilter> cls = 
		    Class.forName (className).asSubclass (HttpFilter.class);
		HttpFilter hf = cls.newInstance ();
		hf.setup (log, config.getProperties (className));
		ls.add (hf);
	    } catch (ClassNotFoundException ex) {
		log.logError ("Could not load class: '" + 
			      className + "' " + ex);
	    } catch (InstantiationException ex) {
		log.logError ("Could not instansiate: '" + 
			      className + "' " + ex);
	    } catch (IllegalAccessException ex) {
		log.logError ("Could not access: '" + 
			      className + "' " + ex);
	    }
	}
    }

    public List<HttpFilter> getHttpInFilters () {
	return httpInFilters;
    }

    public List<HttpFilter> getHttpOutFilters () {
	return httpOutFilters;
    }
}
