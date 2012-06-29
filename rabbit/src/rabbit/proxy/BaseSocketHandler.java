package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import rabbit.io.HandlerRegistration;
import rabbit.io.SocketHandler;
import rabbit.util.Logger;

/** A base class for socket handlers.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public abstract class BaseSocketHandler implements SocketHandler {
    /** The client channel. */
    protected SocketChannel channel; 
    
    /** The selector we are using. */
    protected Selector selector;

    /** The selection key we are using. */
    protected SelectionKey sk;

    /** The logger to use. */
    protected Logger logger;
    
    /** The current read buffer. */
    protected ByteBuffer buffer;

    public BaseSocketHandler (SocketChannel channel, ByteBuffer buffer, 
			      Selector selector, Logger logger) 
	throws IOException {
	this.channel = channel;
	this.buffer = buffer;
	this.selector = selector;
	this.logger = logger;
	register ();
    }

    protected void register () throws ClosedChannelException {
	int ops = getSocketOperations ();
	if (ops != 0) {
	    HandlerRegistration hr = new HandlerRegistration (this);
	    sk = channel.register (selector, ops, hr);
	}
    }
    
    protected void allocateBuffer () {
	// Socket reads really needs direct buffers... 
	// TODO: get from pool.
	// TODO: how large? 
	buffer = ByteBuffer.allocateDirect (4096);
    }

    protected abstract int getSocketOperations ();
    
    public boolean useSeparateThread () {
	return false;
    }

    protected Logger getLogger () {
	return 	logger;
    }

    protected void closeDown () {
	try {
	    sk.attach ("BaseSocketHandler.closeDown");
	    sk.cancel ();
	    channel.close ();
	    clear ();
	} catch (IOException e) {
	    getLogger ().logWarn ("Failed to close down connection: " + e);
	}	
    }

    protected void unregister () {
	if (sk != null && sk.isValid ()) {
	    sk.interestOps (0);
	    sk.attach ("BaseSocketHandler.unregister");
	}
	clear ();
    }

    private void clear () {
	sk = null;
	logger = null;
	selector = null;
	channel = null;
    }
}
