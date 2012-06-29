package rabbit.proxy;

import java.nio.ByteBuffer;
import rabbit.http.HttpHeader;

/** A listener for http headers.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface HttpHeaderListener extends AsyncListener {
    /** one http header has been read 
     * @param header the HttpHeader that was read
     * @param buffer the ByteBuffer that may or may not hold unread data.
     * @param keepalive if the sender want to use keepalive.
     * @param isChunked if false content is not chunked, 
     *                  if true content is chunked.
     * @param dataSize the contents size or -1 if size is unknown.
     */
    void httpHeaderRead (HttpHeader header, ByteBuffer buffer, 
			 boolean keepalive, boolean isChunked, long dataSize);

    /** The other end closed the connection. 
     *  Quite common on persistent connections.
     */
    void closed ();
}
