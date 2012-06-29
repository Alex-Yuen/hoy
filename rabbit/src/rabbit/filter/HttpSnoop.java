package rabbit.filter;

import java.nio.channels.SocketChannel;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** This is a class that prints the Http headers on the standard out stream.
 */
public class HttpSnoop implements HttpFilter {

    /** test if a socket/header combination is valid or return a new HttpHeader.
     * @param socket the SocketChannel that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return This method always returns null.
     */
    public HttpHeader doHttpInFiltering (SocketChannel socket, 
					 HttpHeader header, Connection con) {
	System.out.println (header.toString ());
	return null;
    }
    
    /** test if a socket/header combination is valid or return a new HttpHeader.
     * @param socket the Socket that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return This method always returns null.
     */
    public HttpHeader doHttpOutFiltering (SocketChannel socket, 
					  HttpHeader header, Connection con) {
	System.out.println (con.getRequestLine () + "\n" + 
			    header.toString ());
	return null;
    }

    /** Setup this class with the given properties.
     * @param logger the Logger to output errors/warnings on.
     * @param properties the new configuration of this class.
     */
    public void setup (Logger logger, SProperties properties) {
	// nothing.
    }
}

