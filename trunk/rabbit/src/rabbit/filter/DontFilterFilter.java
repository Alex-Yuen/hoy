package rabbit.filter;

import java.nio.channels.SocketChannel;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.util.Logger;
import rabbit.util.SProperties;
import rabbit.util.PatternHelper;

/** This is a class that makes sure the proxy doesnt filter certain pages.
 *  It matches pages based on the URL.
 *  <p>
 *  It uses the config option <tt>dontFilterURLmatching</tt> 
 *  with a default value of the empty string. <br>
 *  Matching is done with regular expressions, using find on the url.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class DontFilterFilter implements HttpFilter {
    private Pattern pattern;
    private Pattern uap;

    /** 
     */
    public DontFilterFilter () {
    }
    
    /** Test if a socket/header combination is valid or return a new HttpHeader.
     *  If the request matches a certain criteria dont filter it. This filter 
     *  is good for pages with broken HTML that would wreck the HTML parser. 
     * @param socket the SocketChannel that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return This filter always returns null
     */
    public HttpHeader doHttpInFiltering (SocketChannel socket, 
					 HttpHeader header, Connection con) {
	Matcher m = pattern.matcher (header.getRequestURI ());
	if (m.find ())
	    con.setMayFilter (false);

	String ua = header.getHeader ("User-Agent");
	if (ua != null) {
	    m = uap.matcher (ua);
	    if (m.find ())
		con.setMayFilter (false);
	}
	return null;
    }
    
    /** test if a socket/header combination is valid or return a new HttpHeader.
     * @param socket the SocketChannel that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return This filter always returns null
     */
    public HttpHeader doHttpOutFiltering (SocketChannel socket, 
					  HttpHeader header, Connection con) {
	return null;
    }

    /** Setup this class with the given properties.
     * @param logger the Logger for errors/warnings.
     * @param properties the new configuration of this class.
     */
    public void setup (Logger logger, SProperties properties) {
	PatternHelper ph = new PatternHelper ();
	pattern = ph.getPattern (properties, "dontFilterURLmatching", 
				 "DontFilterFilter: bad pattern: ", logger);
	uap = ph.getPattern (properties, "dontFilterAgentsMatching", 
			     "DontFilterFilter: bad user agent pattern: ", 
			     logger);
    }
}
