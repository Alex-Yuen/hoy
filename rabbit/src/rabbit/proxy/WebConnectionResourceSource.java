package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import rabbit.io.HandlerRegistration;
import rabbit.io.SocketHandler;
import rabbit.io.WebConnection;
import rabbit.handler.BlockListener;
import rabbit.handler.ResourceSource;
import rabbit.util.TrafficLogger;

/** A resource source that gets the data from a WebConnection
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class WebConnectionResourceSource 
    implements ResourceSource, SocketHandler, ChunkDataFeeder {
    private Selector selector;
    private SelectionKey sk;
    private WebConnection wc;
    private ByteBuffer buffer;
    private TrafficLogger tl;
    private BlockListener listener;
    private boolean isChunked;
    private long dataSize;
    private long totalRead = 0;    
    private int currentMark = 0;
    private ChunkHandler chunkHandler;
    
    public WebConnectionResourceSource (Selector selector, WebConnection wc, 
					ByteBuffer buffer, TrafficLogger tl,
					boolean isChunked, long dataSize, 
					boolean strictHttp) {
	this.selector = selector;
	this.wc = wc;
	this.buffer = buffer;
	this.tl = tl;
	this.isChunked = isChunked;
	if (isChunked)
	    chunkHandler = new ChunkHandler (this, strictHttp);
	this.dataSize = dataSize;
    }

    /** FileChannels can not be used, will always return false.
     * @return false
     */
    public boolean supportsTransfer () {
	return false;
    }

    public long length () {
	return dataSize;
    }

    public long transferTo (long position, long count, 
			    WritableByteChannel target)
	throws IOException {
	throw new IllegalStateException ("transferTo can not be used.");
    }
    
    public void addBlockListener (BlockListener listener) {
	this.listener = listener;
	if (isChunked)
	    chunkHandler.addBlockListener (listener);
	if (dataSize > 0 && totalRead >= dataSize) {
	    cleanupAndFinish ();
	} else if (buffer.hasRemaining ()) {
	    handleBlock ();
	} else {       
	    register ();
	}
    }

    public void finishedRead () {
	cleanupAndFinish ();
    }

    private void cleanupAndFinish () {
	unregister ();
	listener.finishedRead ();
    }

    public void register () {
	HandlerRegistration hr = new HandlerRegistration (this);
	SocketChannel c = wc.getChannel ();
	try {
	    sk = c.register (selector, SelectionKey.OP_READ, hr);
	} catch (IOException e) {
	    listener.failed (e);
	}
    }

    private void unregister () {
	if (sk != null && sk.isValid ()) {
	    sk.interestOps (0);
	    sk.attach ("WebConnectionResourceSource.unregister");
	}
    }

    private void handleBlock () {
	if (!isChunked) {
	    totalRead += buffer.remaining ();		
	    listener.bufferRead (buffer);
	} else {
	    chunkHandler.handleData (buffer);
	    totalRead = chunkHandler.getTotalRead ();
	}
    }

    public void readMore () {
	buffer.compact ();
	currentMark = buffer.position ();
	register ();
    }

    public void run () {
	// we read one block and then we wait until it is sent, so we 
	// unregister the read operation first...
	unregister ();
	buffer.position (currentMark); // keep our saved data.
	buffer.limit (buffer.capacity ());
	try {
	    int read = wc.getChannel ().read (buffer);
	    currentMark = 0;
	    if (read == 0) {
		register ();
	    } else if (read == -1) {
		cleanupAndFinish ();
	    } else {
		tl.read (read);
		buffer.flip ();
		handleBlock ();
	    }
	} catch (IOException e) {
	    listener.failed (e);
	}
    }

    public boolean useSeparateThread () {
	return false;
    }

    public void timeout () {
	listener.timeout ();
    }

    public void release (Connection con) {
	con.getProxy ().releaseWebConnection (wc);
    }
}
