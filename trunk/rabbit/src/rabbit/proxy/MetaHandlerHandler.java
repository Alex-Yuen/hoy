package rabbit.proxy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import rabbit.http.HttpHeader;
import rabbit.meta.MetaHandler;
import rabbit.util.Coder;
import rabbit.util.SProperties;
import rabbit.util.TrafficLogger;

class MetaHandlerHandler {

    /** Handle a meta page.
     * @param header the request being made.
     */
    public void handleMeta (Connection con, HttpHeader header, 
			    TrafficLogger tlProxy, TrafficLogger tlClient) 
	throws IOException {
	con.getCounter ().inc ("Meta pages requested");
	URL url = null;
	try {
	    url = new URL (header.getRequestURI ());
	} catch (MalformedURLException e) {
	    // this should not happen since HTTPBaseHandler managed to do it...
	}
	String file = url.getFile ().substring (1);  // remove initial '/'
	if (file.length () == 0)
	    file = "FileSender/";
	
	int index = -1;
	String args = "";
	if ((index = file.indexOf ("?")) >= 0) {
	    args = file.substring (index + 1);
	    file = file.substring (0, index);
	}
	SProperties htab = splitArgs (args);
 	if ((index = file.indexOf ("/")) >= 0) {
	    String fc = file.substring (index + 1);
	    file = file.substring (0, index);
	    htab.put ("argstring", fc);
	}
	String error = null;
	try {
	    if (file.indexOf (".") < 0)
		file = "rabbit.meta." + file;
	    
	    Class<? extends MetaHandler> cls = 
		Class.forName (file).asSubclass (MetaHandler.class);
	    MetaHandler mh = null;
	    mh = cls.newInstance ();
	    mh.handle (header, htab, con, tlProxy, tlClient);
	    con.getCounter ().inc ("Meta pages handled");	
	    // Now take care of every error...
	} catch (NoSuchMethodError e) {
	    error = "Given metahandler doesnt have a public no-arg constructor:"
		+ file + ", " + e;
	} catch (ClassCastException e) {
	    error = "Given metapage is not a MetaHandler:" + file + ", " + e;
	} catch (ClassNotFoundException e) {
	    error = "Couldnt find class:" + file + ", " + e;
	} catch (InstantiationException e) {
	    error = "Couldnt instantiate metahandler:" + file + ", " + e;
	} catch (IllegalAccessException e) {
	    error = "Que? metahandler access violation?:" + file + ", " + e;
	} catch (IllegalArgumentException e) {
	    error = "Strange name of metapage?:" + file + ", " + e;
	} 
	if (error != null) {
	    con.getLogger ().logWarn (error);
	    con.doError (400, error);
	    return;
	}
    }

    /** Splits the CGI-paramsstring into variables and values.
     *  put these values into a hashtable for easy retrival
     * @param params the CGI-querystring.
     * @return a map with type->value maps for the CGI-querystring
     */
    public SProperties splitArgs (String params) {
	SProperties htab = new SProperties ();
	StringTokenizer st = new StringTokenizer (params, "=&", true);
	String key = null;
	while (st.hasMoreTokens ()) {
	    String next = st.nextToken ();
	    if (next.equals ("=")) {
		// nah..
	    } else if (next.equals ("&")) {
		if (key != null) {
		    htab.put (key, "");
		    key = null;
		}
	    } else if (key == null) {
		key = next;
	    } else {
		htab.put (key, Coder.URLdecode (next));
		key = null;
	    }
	}
	return htab;
    }
}
