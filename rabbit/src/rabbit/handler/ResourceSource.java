package rabbit.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import rabbit.proxy.Connection;
import rabbit.proxy.Transferable;

/** A resource source.
 * 
 *  Use supportsTransfer to check if this resource supports transfer, 
 *  if it does then use the transferTo method. 
 *  A resource that does not support transfer will listen for blocks
 *  that are read, using a BlockListener.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ResourceSource extends Transferable {
    
    /** Return true if FileChannel.transferTo can be used. 
     *  Will generally only be true if the resource is served
     *  from a FileChannel.
     */
    boolean supportsTransfer ();

    /** Get the length of the resource in bytes. 
     * @return the size of the resource or -1 if unknown.
     */
    long length ();

    /** Add a ByteBuffer listener. 
     */
    void addBlockListener (BlockListener bl);

    /** Release any held resources. 
     * @param con the Connection handling the resource.
     */
    void release (Connection con);
}
