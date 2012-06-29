package rabbit.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import rabbit.cache.Cache;
import rabbit.cache.NCache;
import rabbit.filter.HttpFilter;
import rabbit.handler.HandlerFactory;
import rabbit.http.HttpHeader;
import rabbit.http.HttpDateParser;
import rabbit.io.ConnectionHandler;
import rabbit.io.HandlerRegistration;
import rabbit.io.InetAddressListener;
import rabbit.io.Resolver;
import rabbit.io.SocketHandler;
import rabbit.io.WebConnection;
import rabbit.io.WebConnectionListener;
import rabbit.util.Config;
import rabbit.util.Counter;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** A filtering and caching http proxy. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpProxy implements Runnable, Resolver, TaskRunner {

    /** Current version */
    public static final String VERSION = "RabbIT proxy version 3.2.0";

    /** The current config of this proxy. */
    private Config config;

    /** The time this proxy was started. Time in millis. */
    private long started;

    /** The identity of this server. */
    private String serverIdentity = VERSION;

    /** The logger of this proxy. */
    private ProxyLogger logger = new ProxyLogger ();

    /** The id sequence for acceptors. */
    private static int acceptorId = 0;

    /** The dns handler */
    private DNSHandler dnsHandler;

    /** The socket access controller. */
    private SocketAccessController socketAccessController; 
    
    /** The http header filterer. */
    private HttpHeaderFilterer httpHeaderFilterer;
    
    /** The connection handler */
    private ConnectionHandler conhandler;

    /** The local adress of the proxy. */
    private InetAddress localhost;

    /** The port the proxy is using. */
    private int port = -1;

    /** Adress of connected proxy. */
    private InetAddress proxy = null;
    /** Port of the connected proxy. */
    private int proxyport = -1;

    /** Is the proxy currently listening. */
    private boolean accepting = false;

    /** The serversocket the proxy is using. */
    private ServerSocketChannel ssc = null;

    /** The selector the proxy is using. */
    private Selector selector = null;

    /** If this proxy is using strict http parsing. */
    private boolean strictHttp = true;

    /** Maximum number of concurrent connections */
    private int maxConnections = 50;

    /** The counter of events. */
    private Counter counter = new Counter ();

    /** The thread pool */ // TODO: possibility to configure...
    private ExecutorService executorService = Executors.newCachedThreadPool ();
    
    /** The queue to get back on the main thread. */
    private List<Runnable> returnedTasks = new ArrayList<Runnable> ();

    /** The cache-handler */
    private NCache<HttpHeader, HttpHeader> cache;

    /** Are we allowed to proxy ssl? */
    protected boolean proxySSL = false;
    /** The List of acceptable ssl-ports. */
    protected List<Integer> sslports = null;

    /** The handler factory handler. */
    private HandlerFactoryHandler handlerFactoryHandler;

    /** All the currently active connections. */
    private List<Connection> connections = new ArrayList<Connection> ();
    
    /** The total traffic in and out of this proxy. */
    private TrafficLoggerHandler tlh = new TrafficLoggerHandler ();

    /** Is this proxy running. */
    private boolean running = false;

    public HttpProxy () throws UnknownHostException {
	localhost = InetAddress.getLocalHost ();
    }

    /** Set the config file to use for this proxy.
     * @param conf the name of the file to use for proxy configuration.
     */
    public void setConfig (String conf) throws IOException {
	setConfig (new Config (conf));
    }

    private void setupLogging () {
	logger.setup (config.getProperties ("logging"));
    }

    private void setupDateParsing () {
	HttpDateParser.setOffset (getOffset ());
    }

    private void setupDNSHandler () {
	/* DNSJava have problems with international versions of windows.
	 * so we default to the default dns handler. 
	 */
	String osName = System.getProperty ("os.name");
	if (osName.toLowerCase ().indexOf ("windows") > -1) {
	    logger.logWarn ("This seems like a windows system, " + 
			    "will use default sun handler for DNS");
	    dnsHandler = new DNSSunHandler ();
	} else {
	    String dnsHandlerClass = 
		config.getProperty (getClass ().getName (), "dnsHandler", 
				    "rabbit.proxy.DNSJavaHandler");
	    try {
		Class<? extends DNSHandler> clz = 
		    Class.forName (dnsHandlerClass).asSubclass (DNSHandler.class);
		dnsHandler = clz.newInstance ();	    
		dnsHandler.setup (config.getProperties ("dns"), logger);
	    } catch (Exception e) {
		logger.logError ("Unable to create and setup dns handler: " + e +
				 ", will try to use default instead.");
		dnsHandler = new DNSJavaHandler ();
		dnsHandler.setup (config.getProperties ("dns"), logger);
	    }
	}
    }

    /** Configure the chained proxy rabbit is using (if any). 
     */
    private void setupProxyConnection () {
	String sec = getClass ().getName ();
	String pname = config.getProperty (sec, "proxyhost", "");
	String pport = config.getProperty (sec, "proxyport", "");
	if (!pname.equals ("") && !pport.equals ("")) {
	    try {
		proxy = dnsHandler.getInetAddress (pname);
	    } catch (UnknownHostException e) {
		logger.logFatal ("Unknown proxyhost: '" + pname + "' exiting");
	    }
	    try {
		proxyport = Integer.parseInt (pport.trim ());
	    } catch (NumberFormatException e) {
		logger.logFatal ("Strange proxyport: '" + pport + "' exiting");
	    }
	}
    }

    private void setupCache () {
	SProperties props = 
	    config.getProperties (NCache.class.getName ());
	cache = new NCache<HttpHeader, HttpHeader> (getLogger (), props);
    }

    /** Configure the SSL support RabbIT should have.
     */
    private void setupSSLSupport () {
	String ssl = config.getProperty ("sslhandler", "allowSSL", "no");
	ssl = ssl.trim ();
	if (ssl.equals ("no")) {
	    proxySSL = false;
	} else if (ssl.equals ("yes")) {
	    proxySSL = true;
	    sslports = null;
	} else {
	    proxySSL = true;	    
	    // ok, try to get the portnumbers.
	    sslports = new ArrayList<Integer> ();
	    StringTokenizer st = new StringTokenizer (ssl, ",");
	    while (st.hasMoreTokens ()) {
		String s = null;
		try {
		    Integer port = new Integer (s = st.nextToken ());
		    sslports.add (port);
		} catch (NumberFormatException e) {
		    logger.logWarn ("bad number: '" + s + 
				    "' for ssl port, ignoring.");
		}
	    }
	}
    }

    public void setStrictHttp (boolean b) {
	this.strictHttp = b;
    }

    public boolean getStrictHttp () {
	return strictHttp;
    }

    /** Configure the maximum number of simultanious connections we handle 
     */
    private void setupMaxConnections () {
	String mc = config.getProperty (getClass ().getName (), 
					"maxconnections", "500").trim ();
	try {
	    maxConnections = Integer.parseInt (mc);
	} catch (NumberFormatException e) {
	    logger.logWarn ("bad number for maxconnections: '" + 
			    mc + "', using old value: " + maxConnections);
	}
    }

    private void setupConnectionHandler () {
	conhandler = new ConnectionHandler (logger, counter, this, selector);
	String p = conhandler.getClass ().getName ();
	conhandler.setup (logger, config.getProperties (p));
    }
    
    private void setConfig (Config config) {
	this.config = config;
	setupLogging ();
	setupDateParsing ();
	setupDNSHandler ();
	setupProxyConnection ();
	String cn = getClass ().getName ();
	serverIdentity = config.getProperty (cn, "serverIdentity", VERSION); 
	String strictHttp = config.getProperty (cn, "StrictHTTP", "true"); 
	setStrictHttp (strictHttp.equals ("true"));
	setupMaxConnections ();	
	setupCache ();
	setupSSLSupport ();
	loadClasses ();
	openSocket ();
	setupConnectionHandler ();
	logger.logMsg ("Configuration loaded: ready for action.");
    }

    /** Open a socket on the specified port 
     *  also make the proxy continue accepting connections.
     */
    private void openSocket () {
	int tport = 
	    Integer.parseInt (config.getProperty (getClass ().getName (), 
						  "port", "9666").trim ());
	if (tport != port) {
	    try {
		port = tport;
		accepting = false;
		closeSocket ();
		ssc = ServerSocketChannel.open ();
		ssc.configureBlocking (false);
		ssc.socket ().bind (new InetSocketAddress (port));
		accepting = true;
		selector = Selector.open ();
		Acceptor acceptor = 
		    new Acceptor (acceptorId++, this, selector);
		HandlerRegistration hr = 
		    new HandlerRegistration (acceptor, Long.MAX_VALUE);
		ssc.register (selector, SelectionKey.OP_ACCEPT, hr);
	    } catch (IOException e) {
		logger.logFatal ("Failed to open serversocket on port " + 
				 port);
		stop ();
	    }
	}
    }
    
    /** Closes the serversocket and makes the proxy stop listening for
     *	connections. 
     */
    private void closeSocket () {
	try {
	    accepting = false;
	    if (selector != null) {
		selector.close ();
		selector = null;
	    }	    
	    if (ssc != null) {
		ssc.close ();
		ssc = null;
	    }
	} catch (IOException e) {
	    logger.logFatal ("Failed to close serversocket on port " + port);
	    stop ();
	}
    }

    /** Make sure all filters and handlers are available
     */
    private void loadClasses () {
	SProperties hProps = config.getProperties ("Handlers");
	SProperties chProps = config.getProperties ("CacheHandlers");
	handlerFactoryHandler = 
	    new HandlerFactoryHandler (hProps, chProps, config, getLogger ());

	String filters = config.getProperty ("Filters", "accessfilters","");
	socketAccessController = 
	    new SocketAccessController (filters, config, logger);
	
	String in = config.getProperty ("Filters", "httpinfilters","");
	String out = config.getProperty ("Filters", "httpoutfilters","");
	httpHeaderFilterer = 
	    new HttpHeaderFilterer (in, out, config, this);
    }

    
    /** Run the proxy in a separate thread. */
    public void start () {
	started = System.currentTimeMillis ();	
	Thread t = new Thread (this, VERSION);
	running = true;
	t.start ();
    }

    /** Run the proxy in a separate thread. */
    public void stop () {
	// TODO: what level do we want here? 
	getLogger ().logFatal ("HttpProxy.stop() called, shutting down");
	closeSocket ();
	// TODO: wait for remaining connections.
	// TODO: as it is now, it will just close connections in the middle.
	executorService.shutdown ();	
	logger.close ();
	cache.flush ();
	cache.stop ();	
	running = false;
    }

    public void run () {
	while (running) {
	    while (!accepting || !selector.isOpen ()) {
		try {
		    // wait for reconfigure
		    Thread.sleep (2 * 1000);
		} catch (InterruptedException e) {
		    // ignore
		}
	    }
	    try {
		int num = selector.select (10 * 1000);
		if (selector.isOpen ()) {
		    cancelTimeouts ();
		    handleSelects ();
		    runReturnedTasks ();
		}
	    } catch (IOException e) {
		logger.logError ("Failed to accept, " + 
				 "trying to restart serversocket: " + e);
		closeSocket ();
		openSocket ();
	    } catch (Exception e) {
		logger.logError ("Unknown error: " + e + 
				 " attemting to ignore");
		e.printStackTrace ();
	    }
	}	
    }

    private void cancelTimeouts () throws IOException {
	long now = System.currentTimeMillis ();
	for (SelectionKey sk : selector.keys ()) {
	    Object a = sk.attachment ();
	    if (a instanceof String) {
		// ignore, this is used for status.
	    } else {
		HandlerRegistration hr = (HandlerRegistration)a;
		if (hr != null && now - hr.when > 60 * 1000) {
		    cancelKeyAndCloseChannel (sk);
		    hr.handler.timeout ();
		}
	    }
	}
    }
    
    /** Close down a client that has timed out. 
     */
    private void cancelKeyAndCloseChannel (SelectionKey sk) {
	sk.cancel ();
	try {
	    SocketChannel sc = (SocketChannel)sk.channel ();
	    sc.close ();
	} catch (IOException e) {
	    logger.logError ("failed to shutdown and close socket: " + e);
	}
    }
    
    private void handleSelects () throws IOException {
	Set<SelectionKey> selected = selector.selectedKeys ();
	for (Iterator<SelectionKey> i = selected.iterator (); i.hasNext (); ) {
	    SelectionKey sk = i.next ();
	    Object a = sk.attachment ();
	    if (a != null && a instanceof HandlerRegistration) {
		HandlerRegistration hr = (HandlerRegistration)a;
		if (hr != null && hr.handler != null)
		    handle (sk, hr.handler);
	    } else if (a == null) {
		logger.logWarn ("No handler for:" + sk);
	    } else {
		// Ok, something is very bad here, try to shutdown the channel
		// and hope that we handle it ok elsewhere...
		logger.logError ("Bad handler for:" + sk + ": " + a);
		sk.cancel ();
		sk.channel ().close ();
	    }
	}
	selected.clear ();
    }

    private void handle (SelectionKey sk, SocketHandler handler) {
	if (handler.useSeparateThread ()) {
	    // need to cancel so that we do not get multiple selects...
	    sk.cancel (); 
	    executorService.execute (handler);
	} else {
	    handler.run ();
	}
    }

    private void runReturnedTasks () {
	synchronized (returnedTasks) {
	    for (Runnable r : returnedTasks)
		r.run ();
	    returnedTasks.clear ();
	}
    }

    public void runMainTask (Runnable r) {
	synchronized (returnedTasks) {
	    returnedTasks.add (r);
	    selector.wakeup ();
	}
    }

    public void runThreadTask (Runnable run) {
	executorService.execute (run);
    }

    public Cache<HttpHeader, HttpHeader> getCache () {
	return cache;
    }
    
    public Logger getLogger () {
	return logger;
    }

    public long getOffset () {
	return logger.getOffset ();
    }

    public long getStartTime () {
	return started;
    }

    ConnectionLogger getConnectionLogger () {
	return logger;
    }

    ServerSocketChannel getServerSocketChannel () {
	return ssc;
    }

    public Counter getCounter () {
	return counter;
    }

    SocketAccessController getSocketAccessController () {
	return socketAccessController;
    }

    HttpHeaderFilterer getHttpHeaderFilterer () {
	return httpHeaderFilterer;
    }

    public Config getConfig () {
	return config;
    }

    HandlerFactory getHandlerFactory (String mime) {
	return handlerFactoryHandler.getHandlerFactory (mime);
    }

    HandlerFactory getCacheHandlerFactory (String mime) {
	return handlerFactoryHandler.getCacheHandlerFactory (mime);
    }
    
    public String getVersion () {
	return VERSION;
    }

    public String getServerIdentity () {
	return serverIdentity;
    }

    /** Get the local host.
     * @return the InetAddress of the host the proxy is running on.
     */
    public InetAddress getHost () {
	return localhost;
    }

    /** Get the port this proxy is using.
     * @return the port number the proxy is listening on.
     */ 
    public int getPort () {
	return port;
    }

    /** Get the InetAddress for a given url.
     *  We do dns lookups on a separate thread until we have an 
     *  asyncronous dns library. 
     *  We jump back on the main thread before telling the listener.
     */
    public void getInetAddress (final URL url, final InetAddressListener ial) {
	if (isProxyConnected ()) {
	    ial.lookupDone (proxy);
	    return; 
	}
	Runnable r = new Runnable () {
		public void run () {
		    try {
			final InetAddress ia = dnsHandler.getInetAddress (url);
			runMainTask (new Runnable () {
				public void run () {
				    ial.lookupDone (ia);
				}
			    });
		    } catch (final UnknownHostException e) {
			runMainTask (new Runnable () {
				public void run () {
				    ial.unknownHost (e);
				}
			    });
		    }
		}
	    };
	executorService.execute (r);
    }

    /** Get the port to connect to.
     * @param port the port we want to connect to.
     * @return the port to connect to.
     */
    public int getConnectPort (int port) {
	if (isProxyConnected ())    // are we talking through another proxy?
	    return proxyport;
	return port;
    }

    /** Try hard to check if the given address matches the proxy. 
     *  Will use the localhost name and all ip addresses.
     */
    public boolean isSelf (String uhost, int urlport) {
	if (urlport == getPort ()) {		
	    String proxyhost = getHost ().getHostName ();	    
	    if (uhost.equalsIgnoreCase (proxyhost))
		return true;
	    try {
		Enumeration<NetworkInterface> e = 
		    NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements ()) {
		    NetworkInterface ni = e.nextElement ();
		    Enumeration<InetAddress> ei = ni.getInetAddresses ();
		    while (ei.hasMoreElements ()) {
			InetAddress ia = ei.nextElement ();
			if (ia.getHostAddress ().equals (uhost))
			    return true;
		    }
		}
	    } catch (SocketException e) {
		logger.logWarn ("failed to get network interfaces: " + e);
	    }
	}	
	return false;
    }

    /** Is this proxy chained to another proxy? 
     * @return true if the proxy is connected to another proxy.
     */
    public boolean isProxyConnected () {
	return proxy != null;
    }

    /** Get the authenticationstring to use for proxy.
     * @return an authentication string.
     */ 
    public String getProxyAuthString () {
	return config.getProperty (getClass ().getName (), "proxyauth");
    }

    /** Get a WebConnection.
     * @param header the http header to get the host and port from
     * @param wcl the listener that wants to get the connection.
     */
    public void getWebConnection (HttpHeader header, 
				  WebConnectionListener wcl) {
	conhandler.getConnection (header, wcl);
    }
    
    /** Release a WebConnection so that it may be reused if possible.
     * @param wc the WebConnection to release.
     */
    public void releaseWebConnection (WebConnection wc) {
	conhandler.releaseConnection (wc);
    }
    
    /** Mark a WebConnection for pipelining.
     * @param wc the WebConnection to mark.
     */
    public void markForPipelining (WebConnection wc) {
	conhandler.markForPipelining (wc);
    }

    /** Add a current connection 
     * @param con the connection
     */
    public void addCurrentConnection (Connection con) {
	connections.add (con);
    }

    /** Remove a current connection. 
     * @param con the connection
     */
    public void removeCurrentConnection (Connection con) {
	connections.remove (con);
    }

    /** Get the connection handler. 
     */
    public ConnectionHandler getConnectionHandler () {
	return conhandler;
    }

    /** Get all the current connections 
     */
    public List<Connection> getCurrentConnections () {
	return Collections.unmodifiableList (connections);
    }

    /** Update the currently transferred traffic statistics. 
     */
    protected void updateTrafficLog (TrafficLoggerHandler tlh) {
	synchronized (this.tlh) {
	    tlh.addTo (this.tlh);
	}
    }

    /** Get the currently transferred traffic statistics.
     */
    public TrafficLoggerHandler getTrafficLoggerHandler () {
	return tlh;
    }
}
