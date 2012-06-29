package rabbit.filter;

import java.net.URL;
import java.net.MalformedURLException;
import java.nio.channels.SocketChannel;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.proxy.HttpGenerator;
import rabbit.util.Logger;
import rabbit.util.SProperties;
import rabbit.util.SimpleUserHandler;

/** This is a filter that requires users to use proxy-authentication.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ProxyAuth implements HttpFilter {
    private SimpleUserHandler userHandler;
    
    /** test if a socket/header combination is valid or return a new HttpHeader.
     *  Check that the user has been authenticate..
     * @param socket the SocketChannel that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return null if everything is fine or a HttpHeader 
     *         describing the error (like a 403).
     */
    public HttpHeader doHttpInFiltering (SocketChannel socket, 
					 HttpHeader header, Connection con) {
	if (con.getMeta ())
	    return null;
	String username = con.getUserName ();
	String pwd = con.getPassword ();
	if (!userHandler.isValidUser (username, pwd))
	    return getError (con, header);
	return null;
    }

    private HttpHeader getError (Connection con, HttpHeader header) {
	HttpGenerator hg = con.getHttpGenerator ();
	try {
	    return hg.get407 ("internet", new URL (header.getRequestURI ()));
	} catch (MalformedURLException e) {
	    con.getProxy ().getLogger ().logWarn ("Bad url: " + e);
	    return hg.get407 ("internet", null);
	}
    }

    /** test if a socket/header combination is valid or return a new HttpHeader.
     *  does nothing.
     * @param socket the SocketChannel that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return This method always returns null.
     */
    public HttpHeader doHttpOutFiltering (SocketChannel socket, 
					  HttpHeader header, Connection con) {
	return null;
    }    

    /** Setup this class with the given properties.
     * @param logger the Logger to output errors/warnings on.
     * @param properties the new configuration of this class.
     */
    public void setup (Logger logger, SProperties properties) {
	String userFile = properties.getProperty ("userfile", "conf/allowed");
	userHandler = new SimpleUserHandler ();
	userHandler.setFile (userFile, logger);
    }
}
