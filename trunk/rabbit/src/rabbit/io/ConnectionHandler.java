package rabbit.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rabbit.http.HttpHeader;
import rabbit.util.Counter;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** A class to handle the connections to the net. 
 *  Tries to reuse connections whenever possible.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ConnectionHandler {
    // The logger to use
    private Logger logger;
    
    // The counter to use.
    private Counter counter;
    
    // The resolver to use
    private Resolver resolver;

    // is the cleaner running?
    private boolean running = true;

    // The available connections.
    private Map<Address, List<WebConnection>> activeConnections;

    // the keepalivetime.
    private long keepaliveTime = 1000;

    // should we use pipelining...
    private boolean usePipelining = true;

    // the selector for keepalive connections...
    private Selector selector = null;

    public ConnectionHandler (Logger logger, Counter counter, 
			      Resolver resolver, Selector selector) {
	this.logger = logger;
	this.counter = counter;
	this.resolver = resolver;
	this.selector = selector;

	activeConnections = new HashMap<Address, List<WebConnection>> ();
    }

    /** Set the keep alive time for this handler.
     * @param milis the keep alive time in miliseconds.
     */
    public void setKeepaliveTime (long milis) {
	keepaliveTime = milis;
    }
    
    /** Get the current keep alive time.
     * @return the keep alive time in miliseconds.
     */
    public long getKeepaliveTime () {
	return keepaliveTime;
    }

    public Map<Address, List<WebConnection>> getActiveConnections () {
	return Collections.unmodifiableMap (activeConnections);
    }

    /** Get a WebConnection for the given header.
     * @param header the HttpHeader containing the URL to connect to.
     * @param wcl the Listener that wants the connection.
     */
    public void getConnection (final HttpHeader header, 
			       final WebConnectionListener wcl) {	
	// TODO: should we use the Host: header if its available? probably...
	String requri = header.getRequestURI ();
	URL url = null;
	try {
	    url = new URL (requri);
	} catch (MalformedURLException e) {
	    wcl.failed (e);
	}
	int port = url.getPort () > 0 ? url.getPort () : 80;
	final int rport = resolver.getConnectPort (port);
	
	resolver.getInetAddress (url, new InetAddressListener () {
		public void lookupDone (InetAddress ia) {
		    Address a = new Address (ia, rport);
		    getConnection (header, wcl, a);
		}

		public void unknownHost (Exception e) {
		    wcl.failed (e);
		}		
	    });
    }
    
    private void getConnection (HttpHeader header, 
				WebConnectionListener wcl, 
				Address a) {
	String error = null;
	WebConnection wc = null;
	counter.inc ("WebConnections used");
	String method = header.getMethod ().trim ();
	
	// since we should not retry POST (and other) we 
	// have to get a fresh connection for them..
	if (!(method != null && 
	      (method.equals ("GET") || method.equals ("HEAD")))) {
	    wc = new WebConnection (a, counter, logger);
	} else {
		//System.out.println("create a pooled connection:"+method);
	    wc = getPooledConnection (a, activeConnections);
	    if (wc == null)
		wc = new WebConnection (a, counter, logger);
	}
	try {
	    wc.connect (selector, wcl);
	} catch (IOException e) {
	    wcl.failed (e);
	}
    }

    private WebConnection 
    getPooledConnection (Address a, Map<Address, List<WebConnection>> conns) {
	synchronized (conns) {
	    List<WebConnection> pool = conns.get (a);
	    if (pool != null) {
		synchronized (pool) {
		    if (pool.size () > 0) 
			return pool.remove (pool.size () - 1);
		}
	    }
	}
	return null;
    }
    
    private void removeFromPool (WebConnection wc, 
				 Map<Address, List<WebConnection>> conns) {
	synchronized (conns) {
	    List<WebConnection> pool = conns.get (wc.getAddress ());
	    if (pool != null) {
		synchronized (pool) {
		    pool.remove (wc);
		    if (pool.size () == 0)
			conns.remove (wc.getAddress ());
		}
	    }
	}
    }

    /** Return a WebConnection to the pool so that it may be reused.
     * @param wc the WebConnection to return.
     */
    public void releaseConnection (WebConnection wc) {
	counter.inc ("WebConnections released");
	if (!wc.getChannel ().isOpen ()) {
	    return;
	}
	
	Address a = wc.getAddress ();
	if (!wc.getKeepalive ()) {
	    closeWebConnection (wc);
	    return;
	}

	synchronized (wc) {
	    wc.setReleased ();
	}
	synchronized (activeConnections) {
	    List<WebConnection> pool = 
		activeConnections.get (a);
	    if (pool == null) {
		pool = new ArrayList<WebConnection> ();
		activeConnections.put (a, pool);
	    }
	    SocketChannel channel = wc.getChannel ();
	    try {
		new CloseListener (wc);
		pool.add (wc);
	    } catch (IOException e) {
		logger.logWarn ("Get IOException when setting up a " + 
				"CloseListener: " + e);
		closeWebConnection (wc);
	    }
	}	
    }

    private void closeWebConnection (WebConnection wc) {
	if (wc == null)
	    return;
	if (!wc.getChannel ().isOpen ())
	    return;
	try {
	    wc.close ();
	} catch (IOException e) {
	    logger.logWarn ("Failed to close WebConnection: " + wc);
	}
    }
    
    private class CloseListener implements SocketHandler {
	private WebConnection wc;
	private SelectionKey sk;

	public CloseListener (WebConnection wc) throws IOException {
	    this.wc = wc;
	    SocketChannel channel = wc.getChannel ();
	    HandlerRegistration hr = new HandlerRegistration (this);
	    sk = channel.register (selector, SelectionKey.OP_READ, hr);
	}

	public void run () {
	    closeChannel ();
	}
	
	public void timeout () {
	    closeChannel ();
	}

	private void closeChannel () {
	    try {
		sk.cancel ();
		removeFromPool (wc, activeConnections);
		wc.close ();
	    } catch (IOException e) {
		String err = 
		    "CloseListener: Failed to close web connection: " + e;
		logger.logWarn (err);
	    }
	}

	public boolean useSeparateThread () {
	    return false;
	}
    }

    /** Mark a WebConnection ready for pipelining.
     * @param wc the WebConnection to mark ready for pipelining.
     */
    public void markForPipelining (WebConnection wc) {
	if (!usePipelining)
	    return;
	synchronized (wc) {
	    if (!wc.getKeepalive ())
		return;
	    wc.setMayPipeline (true);
	}
    }
    
    public void setup (Logger logger, SProperties config) {
	if (config == null)
	    return;
	String kat = config.getProperty ("keepalivetime", "1000"); 
	try {
	    setKeepaliveTime (Long.parseLong (kat)); 
	} catch (NumberFormatException e) { 
	    String err = 
		"Bad number for ConnectionHandler keepalivetime: '" + kat + "'";
	    logger.logWarn (err);
	}
	String up = config.get ("usepipelining");
	if (up == null)
	    up = "true";
	usePipelining = up.equalsIgnoreCase ("true");
    }
}
