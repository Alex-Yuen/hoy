package rabbit.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.StringTokenizer;
import rabbit.http.HttpHeader;
import rabbit.proxy.BlockSender;
import rabbit.proxy.Connection;
import rabbit.proxy.LineListener;
import rabbit.proxy.LineReader;
import rabbit.proxy.MultiPartPipe;
import rabbit.proxy.TrafficLoggerHandler;

/** This class handles multipart responses, this handler does not 
 *  filter the resource.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class MultiPartHandler extends BaseHandler {
    private MultiPartPipe mpp = null;
    
    // For creating the factory.
    public MultiPartHandler () {	
    }

    /** Create a new BaseHansler for the given request.
     * @param con the Connection handling the request.
     * @param request the actual request made.
     * @param clientBuffer the client side buffer.
     * @param response the actual response.
     * @param content the resource.
     */
    public MultiPartHandler (Connection con, TrafficLoggerHandler tlh,
			     HttpHeader request, ByteBuffer clientBuffer, 
			     HttpHeader response, ResourceSource content) {
	super (con, tlh, request, clientBuffer, response, 
	       content, false, false, -1);
	con.setChunking (false);

	//Content-Type: multipart/byteranges; boundary=B-mmrokjxyjnwsfcefrvcg\r\n	
	String ct = response.getHeader ("Content-Type");
	mpp = new MultiPartPipe (ct);
    }

    @Override
    public Handler getNewInstance (Connection con, TrafficLoggerHandler tlh, 
				   HttpHeader header, ByteBuffer buffer, 
				   HttpHeader webHeader, 
				   ResourceSource content, boolean mayCache, 
				   boolean mayFilter, long size) {
	return new MultiPartHandler (con, tlh, header, buffer, 
				     webHeader, content);
    }

    /** We may remove trailers, so we may modify the content.
     * ®return true this handler modifies the content.
     */
    @Override public boolean changesContentSize () {
	return true;
    }

    @Override 
    protected void send () {
	content.addBlockListener (this);	
    }

    /* A Typical case: 
     * The header is already read: 
     *  
     * <xmp>
     * HTTP/1.1 206 Partial Content\r\n
     * Connection: keep-alive\r\n
     * Date: Sun, 05 Feb 2006 15:02:20 GMT\r\n
     * Content-Type: multipart/byteranges; boundary=B-cbwbjaxizibtumtuxtti\r\n
     * \r\n
     * </xmp>
     *  
     * Then comes the data: 
     *      
     * <xmp>
     * \r\n
     * --B-cbwbjaxizibtumtuxtti\r\n
     * Content-Range: bytes 0-5/105\r\n
     * \r\n
     * body-y\r\n
     * --B-cbwbjaxizibtumtuxtti\r\n
     * Content-Range: bytes 7-10/105\r\n
     * \r\n
     * jqka\r\n
     * --B-cbwbjaxizibtumtuxtti--\r\n
     * </xmp>
     */
    /*
     * For now we only try to read lines and if we find the ending line we stop.
     * This is not a fully correct handling, but it seems to work well enough.
     */    
    public void bufferRead (ByteBuffer buf) {
	try {
	    mpp.parseBuffer (buf);
	    new BlockSender (con.getChannel (), con.getSelector (), 
			     getLogger (), tlh.getClient (), 
			     buf, con.getChunking (), this);	
	} catch (IOException e) {
	    failed (e);	    
	}
    }
}
