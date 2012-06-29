package rabbit.proxy;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import rabbit.io.SocketHandler;

class Acceptor implements SocketHandler {
    private int id;
    private long counter;
    private HttpProxy proxy;
    private Selector selector;

    public Acceptor (int id, HttpProxy proxy, Selector selector) {
	this.id = id;
	this.proxy = proxy;
	this.selector = selector;
    }

    public boolean useSeparateThread () {
	return false;
    }

    public void run () {
	try {
	    SocketChannel sc = proxy.getServerSocketChannel ().accept ();
	    if (sc == null)
		return;
	    proxy.getCounter ().inc ("Socket accepts");
	    if (!proxy.getSocketAccessController ().checkAccess (sc)) {
		proxy.getLogger ().logWarn ("Rejecting access from " + 
					    sc.socket ().getInetAddress ());
		proxy.getCounter ().inc ("Rejected IP:s");
		sc.close ();
	    } else {
		sc.configureBlocking (false);
		Connection c = new Connection (getId (), sc, selector, proxy);
		c.readRequest ();
	    }
	} catch (IOException e) {
	    proxy.getLogger ().logWarn ("Accept failed: " + e);
	}
    }

    private ConnectionId getId () {
	synchronized (this) {
	    return new ConnectionId (id, counter++);
	}
    }

    public void timeout () {
	throw new IllegalStateException ("Acceptor should not get timeout");
    }
}
