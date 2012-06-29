package rabbit.handler;

import java.nio.ByteBuffer;
import rabbit.proxy.AsyncListener;

/** A listener for resource data.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface BlockListener extends AsyncListener {

    /** A buffer has been read, the buffer has been flip:ed 
     *  before this call is made so position and remaining are valid.
     */
    void bufferRead (ByteBuffer buf);

    /** The resource have been fully transferred 
     */
    void finishedRead ();    
}
