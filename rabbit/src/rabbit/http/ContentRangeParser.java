package rabbit.http;

import java.util.StringTokenizer;
import rabbit.util.Logger;

/** A class that parses content range headers.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ContentRangeParser {
    private long start;
    private long end;
    private long total;
    private boolean valid = false;

    public ContentRangeParser (String cr, Logger logger) {
	if (cr != null) {
	    if (cr.startsWith ("bytes "))
		cr = cr.substring (6);
	    StringTokenizer st = new StringTokenizer (cr, "-/");
	    if (st.countTokens () == 3) {
		try {
		    start = Long.parseLong (st.nextToken ());
		    end = Long.parseLong (st.nextToken ());
		    String length = st.nextToken ();
		    if ("*".equals (length))
			total = -1;
		    else 
			total = Long.parseLong (length);
		    valid = true;
		} catch (NumberFormatException e) {
		    logger.logWarn ("bad content range: " + e + 
				    " for string: '" + cr + "'");
		}
	    }
	}
    }

    public boolean isValid () {
	return valid;
    }

    public long getStart () {
	return start;
    }
    
    public long getEnd () {
	return end;
    }

    public long getTotal () {
	return total;
    }    
}
