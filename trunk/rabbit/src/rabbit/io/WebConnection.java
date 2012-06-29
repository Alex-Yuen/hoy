package rabbit.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import rabbit.util.Counter;
import rabbit.util.Logger;

/** A class to handle a connection to the Internet.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class WebConnection {
    private int id;
    private Address address;
    private Counter counter;
    private Logger logger;
    private SocketChannel channel;
    private long releasedAt = -1;
    private boolean keepalive = true;
    private boolean mayPipeline = false;
    
    private static int idCounter;

    /** Create a new WebConnection to the given InetAddress and port.
     * @param address the computer to connect to.
     * @param counter the Counter to used to collect statistics
     * @param logger the Logger to use.
     */
    public WebConnection (Address address, Counter counter, Logger logger) {
	this.id = idCounter++;
	this.address = address;
	this.counter = counter;
	this.logger = logger;
	counter.inc ("WebConnections created");
    }

    public String toString () {
	return "WebConnection(id: " + id + 
	    ", address: "  + address + 
	    ", keepalive: " + keepalive + 
	    ", releasedAt: " + releasedAt + ")";
    }

    public Address getAddress () {
	return address;
    }

    public SocketChannel getChannel () {
	return channel;
    }

    public void close () throws IOException {
	counter.inc ("WebConnections closed");
	channel.close ();
    }

    public void connect (Selector selector, WebConnectionListener wcl) 
	throws IOException {
	// if we are a keepalive connection then just say so..
	if (channel != null && channel.isConnected ()) {
	    wcl.connectionEstablished (this);
	} else {
	    // ok, open the connection....
	    channel = SocketChannel.open ();
	    channel.configureBlocking (false);
	    SocketAddress addr = 
		new InetSocketAddress (address.getInetAddress (),
				       address.getPort ());
	    boolean connected = channel.connect (addr);
	    if (connected) {
		channel.socket ().setTcpNoDelay (true);
		wcl.connectionEstablished (this);
	    } else {
		new ConnectListener (selector, wcl);
	    }
	}
    }

    private class ConnectListener implements SocketHandler {
	private WebConnectionListener wcl;
	private SelectionKey sk;

	public ConnectListener (Selector selector, WebConnectionListener wcl)
	    throws IOException {
	    this.wcl = wcl;
	    HandlerRegistration hr = new HandlerRegistration (this);
	    sk = channel.register (selector, SelectionKey.OP_CONNECT, hr);
	}
	
	public void run () {
	    try {
		sk.interestOps (0);
		sk.attach ("WebConnection.ConnectListener.run");
		channel.finishConnect ();
		channel.socket ().setTcpNoDelay (true);
		wcl.connectionEstablished (WebConnection.this);
	    } catch (IOException e) {
		wcl.failed (e);
	    }
	}
	
	public void timeout () {
	    wcl.timeout ();
	}

	public boolean useSeparateThread () {
	    return false;
	}
    }
    
    /** Set the keepalive value for this WebConnection, 
     *  Can only be turned off. 
     * @param b the new keepalive value.
     */
    public void setKeepalive (boolean b) {
	keepalive &= b;
    }

    /** Get the keepalive value of this WebConnection.
     * @return true if this WebConnection may be reused.
     */
    public boolean getKeepalive () {
	return keepalive;
    }

    /** Mark this WebConnection as released at current time.
     */
    public void setReleased () {
	releasedAt = System.currentTimeMillis ();
    }

    /** Get the time that this WebConnection was released.
     */
    public long getReleasedAt () {
	return releasedAt;
    }

    /** Mark this WebConnection for pipelining.
     */
    public void setMayPipeline (boolean b) {
	mayPipeline = b;
    }

    /** Check if this WebConnection may be used for pipelining.
     */
    public boolean mayPipeline () {
	return mayPipeline;
    }
}
