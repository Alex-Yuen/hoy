package rabbit.util;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Helper class for regular expresions.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class PatternHelper {
    
    /** Get a Pattern for a given property. 
     * @param properties the properties to use.
     * @param configOption the property to get.
     * @param warn the warning message to log if construction fails
     * @param logger the Logger to log warnings to.
     * @return a Pattern or null if no pattern could be created.
     */ 
    public Pattern getPattern (SProperties properties, 
			       String configOption, 
			       String warn, Logger logger) {
	Pattern ret = null;
	String val = properties.getProperty (configOption);
	if (val != null) {
	    try {
		ret = Pattern.compile (val, Pattern.CASE_INSENSITIVE);
	    } catch (PatternSyntaxException e) {
		logger.logWarn (warn + e);
	    }
	}
	return ret;
    }
}
