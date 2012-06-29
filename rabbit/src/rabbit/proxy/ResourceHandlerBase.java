package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import rabbit.http.HttpHeader;
import rabbit.io.HandlerRegistration;
import rabbit.io.SocketHandler;
import rabbit.io.WebConnection;

/** A base for client resource transfer classes.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
abstract class ResourceHandlerBase implements ClientResourceHandler {
    protected Connection con;
    protected ByteBuffer buffer;
    protected TrafficLoggerHandler tlh;
    protected WebConnection wc;
    protected ClientResourceTransferredListener listener;
    private SelectionKey sk;
    private int readStartPos = 0;

    public ResourceHandlerBase (Connection con, 
				ByteBuffer buffer, 
				TrafficLoggerHandler tlh) {
	this.con = con;
	this.buffer = buffer;
	this.tlh = tlh;
    }
    
    /**  Will store the variables and call doTransfer ()
     */
    public void transfer (WebConnection wc,  
			  ClientResourceTransferredListener crtl) {
	this.wc = wc;
	this.listener = crtl;
	doTransfer ();
    }
    
    void doTransfer () {
	if (buffer.remaining () > 0)
	    sendBuffer ();
	else 
	    waitForRead ();
    }
    
    abstract void sendBuffer ();

    protected void waitForRead () {
	if (buffer.remaining () > 0)
	    readStartPos = buffer.position ();
	SocketHandler sh = new Reader ();
	HandlerRegistration hr = new HandlerRegistration (sh);	
	SocketChannel c = con.getChannel ();
	try {
	    sk = c.register (con.getSelector (), SelectionKey.OP_READ, hr);
	} catch (IOException e) {
	    listener.failed (e);
	}
    }
    
    private class Reader implements SocketHandler {
	public void run () {
	    try {
		sk.interestOps (0);
		buffer.clear ();
		buffer.position (readStartPos);		
		int read = con.getChannel ().read (buffer);
		if (read == 0) {
		    waitForRead ();
		} else if (read == -1) {
		    failed (new IOException ("Failed to read request"));
		} else {
		    tlh.getClient ().read (read);
		    buffer.flip ();
		    sendBuffer ();
		}
	    } catch (IOException e) {
		listener.failed (e);
	    }
	}

	public void timeout () {
	    listener.timeout ();
	}

	public boolean useSeparateThread () {
	    return false;
	}
    }

    public void timeout () {
	listener.timeout ();
    }

    public void failed (Exception e) {
	listener.failed (e);
    }
}
