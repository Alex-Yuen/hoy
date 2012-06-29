package rabbit.handler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import rabbit.proxy.Connection;
import rabbit.proxy.TrafficLoggerHandler;
import rabbit.http.HttpHeader;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** The methods needed to create a new Handler.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */ 
public interface HandlerFactory {
    
    /** Get a new Handler for the given request made.
     * @param connection the Connection handling the request.
     * @param tlh the Traffic logger handler.
     * @param header the request.
     * @param buffer the client side buffer (may contain the next request).
     * @param webheader the response.
     * @param content the resource.
     * @param mayCache if the handler may cache the response.
     * @param mayFilter if the handler may filter the response.
     * @param size the Size of the data beeing handled (-1 = unknown length).
     */
    Handler getNewInstance (Connection connection,
			    TrafficLoggerHandler tlh,
			    HttpHeader header,
			    ByteBuffer buffer, 
			    HttpHeader webheader,
			    ResourceSource content, 
			    boolean mayCache,
			    boolean mayFilter,
			    long size);
    
    /** setup the handler factory. 
     */
    void setup (Logger logger, SProperties properties);
}
