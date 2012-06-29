package rabbit.proxy;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import rabbit.filter.IPAccessFilter;
import rabbit.util.Config;
import rabbit.util.Logger;

/** An access controller based on socket channels. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SocketAccessController {
    /** the filters, a List of classes (in given order) */
    private List<IPAccessFilter> accessfilters = 
    new ArrayList<IPAccessFilter> ();

    public SocketAccessController (String filters, Config config, 
				   Logger logger) {
	accessfilters = new ArrayList<IPAccessFilter> ();
	loadAccessFilters (filters, accessfilters, config, logger);
    }

    private void loadAccessFilters (String filters, 
				    List<IPAccessFilter> accessfilters, 
				    Config config, Logger logger) {
	StringTokenizer st = new StringTokenizer (filters, ",");
	String classname = "";
	while (st.hasMoreElements ()) {
	    try {
		classname = st.nextToken ().trim ();
		Class<? extends IPAccessFilter> cls = 
		    Class.forName (classname).asSubclass (IPAccessFilter.class);
		IPAccessFilter ipf = cls.newInstance ();
		ipf.setup (logger, config.getProperties (classname));
		accessfilters.add (ipf);
	    } catch (ClassNotFoundException ex) {
		logger.logError ("Could not load class: '" + 
				 classname + "' " + ex);
	    } catch (InstantiationException ex) {
		logger.logError ("Could not instansiate: '" + 
				 classname + "' " + ex);
	    } catch (IllegalAccessException ex) {
		logger.logError ("Could not instansiate: '" + 
				 classname + "' " + ex);		
	    }
	}
    }
    
    public List<IPAccessFilter> getAccessFilters () {
	return Collections.unmodifiableList (accessfilters);
    }

    public boolean checkAccess (SocketChannel sc) {
	for (IPAccessFilter filter : getAccessFilters ()) {
	    if (filter.doIPFiltering (sc))
		return true;
	}
	return false;
    }
}
