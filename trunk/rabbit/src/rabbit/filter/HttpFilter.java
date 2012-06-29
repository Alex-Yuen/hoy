package rabbit.filter;

import java.nio.channels.SocketChannel;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** A filter for http headers. 
 */
public interface HttpFilter {

    /** test if a socket/header combination is valid or 
     *  return a new HttpHeader.
     * @param socket the Socket that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return null if everything is fine or a HTTPHeader 
     *         describing the error (like a 403).
     */
    HttpHeader doHttpInFiltering (SocketChannel socket, 
				  HttpHeader header, 
				  Connection con);
    
    /** test if a socket/header combination is valid or 
     *  return a new HttpHeader.
     * @param socket the Socket that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return null if everything is fine or a HTTPHeader 
     *         describing the error (like a 403).
     */
    HttpHeader doHttpOutFiltering (SocketChannel socket, 
				   HttpHeader header, 
				   Connection con);

    /** Setup this filter.
     * @param logger the Logger to output errors/warnings on.
     * @param properties the SProperties to get the settings from.
     */
    void setup (Logger logger, SProperties properties);
}
