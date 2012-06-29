package rabbit.util;

// $Id: SProperties.java,v 1.2 2005/08/02 20:50:21 robo Exp $

import java.util.HashMap;

/** A simple string properties class. 
 */
public class SProperties extends HashMap<String, String> {
    private static final long serialVersionUID = 20050430;

    public String getProperty (String key) {
	return get (key);
    }

    public String getProperty (String key, String defaultValue) {
	String val = get (key);
	if (val == null)
	    return defaultValue;
	return val;
    }
}
