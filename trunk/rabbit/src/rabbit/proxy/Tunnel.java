package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import rabbit.io.HandlerRegistration;
import rabbit.io.SocketHandler;
import rabbit.util.Logger;
import rabbit.util.TrafficLogger;

/** A handler that just tunnels data.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class Tunnel implements SocketHandler {
    private Selector selector;
    private Logger logger;
    private SocketChannel from;
    private ByteBuffer fromBuffer;
    private TrafficLogger fromLogger;
    private SelectionKey fromSk = null;
    private SocketChannel to;
    private ByteBuffer toBuffer;
    private TrafficLogger toLogger;
    private SelectionKey toSk = null;
    private TunnelDoneListener listener;
    
    public Tunnel (Selector selector, Logger logger, 
		   SocketChannel from, ByteBuffer fromBuffer, 
		   TrafficLogger fromLogger, 
		   SocketChannel to, ByteBuffer toBuffer, 
		   TrafficLogger toLogger,
		   TunnelDoneListener listener) 
	throws IOException {
	this.selector = selector;
	this.logger = logger;
	this.from = from;
	this.fromBuffer = fromBuffer;
	this.fromLogger = fromLogger;
	this.to = to;
	if (toBuffer == null) {
	    toBuffer = ByteBuffer.allocate (2048);
	    toBuffer.limit (0); // set initial state: no bytes to write
	}
	this.toBuffer = toBuffer;
	this.toLogger = toLogger;
	this.listener = listener;
	
	sendBuffers ();
    }
    
    private void registerRead () throws IOException {
	fromBuffer.clear ();
	toBuffer.clear ();
	HandlerRegistration hr = new HandlerRegistration (this, Long.MAX_VALUE);
	fromSk = from.register (selector, SelectionKey.OP_READ, hr);
	hr = new HandlerRegistration (this, Long.MAX_VALUE);
	toSk = to.register (selector, SelectionKey.OP_READ, hr);
    }

    private void sendBuffers () throws IOException {	
	boolean needMore1 = sendBuffer (fromBuffer, to, toLogger);
	if (needMore1) {
	    HandlerRegistration hr = 
		new HandlerRegistration (this, Long.MAX_VALUE);
	    toSk = to.register (selector, SelectionKey.OP_WRITE, hr);
	}
	
	boolean needMore2 = sendBuffer (toBuffer, from, fromLogger);
	if (needMore2) {
	    HandlerRegistration hr = 
		new HandlerRegistration (this, Long.MAX_VALUE);
	    fromSk = from.register (selector, SelectionKey.OP_WRITE, hr);
	}
	
	if (!(needMore1 || needMore2)) 
	    registerRead ();
    }

    /** Send the buffer to the channel. 
     * @return true if more data needs to be written.
     */
    private boolean sendBuffer (ByteBuffer buffer, SocketChannel channel, 
				TrafficLogger tl) 
	throws IOException {

	if (buffer.hasRemaining ()) {
	    int written;
	    do {
		written = channel.write (buffer);
		tl.write (written);
	    } while (written > 0 && buffer.remaining () > 0);
	}
	if (buffer.remaining () == 0) {
	    return false;
	} else {
	    return true;
	}
    }
    
    private boolean readBuffers () throws IOException {
	boolean read1 = readBuffer (from, fromBuffer, fromLogger);
	boolean read2 = readBuffer (to, toBuffer, toLogger);
	return (read1 || read2);
    }
	
    private boolean readBuffer (SocketChannel channel, ByteBuffer buffer, 
				TrafficLogger tl) 
	throws IOException {
	int read = channel.read (buffer);
	buffer.flip ();
	if (read == -1) {
	    closeDown ();
	    return false;
	} 
	tl.read (read);
	return true;
    }

    private Logger getLogger () {
	return logger;
    }
	
    public boolean useSeparateThread () {
	return false;
    }

    public void timeout () {
	getLogger ().logWarn ("Tunnel: timeout during handling");
	throw new IllegalStateException ("Tunnels should not get timeout");
    }

    public void run () {
	try {
	    if (fromBuffer.position () == 0 && toBuffer.position () == 0)
		if (readBuffers ())		    
		    sendBuffers ();
	} catch (IOException e) {
	    getLogger ().logWarn ("Tunnel: failed to handle: " + e);
	    closeDown ();
	}
    }

    private void closeDown () {
	// we do not want to close the channels, 
	// it is up to the listener to do that.
	listener.tunnelClosed ();
    }
}
