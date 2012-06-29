package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import rabbit.http.HttpHeader;
import rabbit.io.WebConnection;
import rabbit.io.WebConnectionListener;
import rabbit.util.Coder;
import rabbit.util.Logger;

/** A handler that shuttles ssl traffic
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class SSLHandler implements TunnelDoneListener {
    private HttpProxy proxy;
    private Connection con;
    private TrafficLoggerHandler tlh;
    private HttpHeader request;
    private SocketChannel channel;
    private Selector selector;

    private ByteBuffer buffer;
    private WebConnection wc;
    
    public SSLHandler (HttpProxy proxy, Connection con, HttpHeader request, 
		       TrafficLoggerHandler tlh) {
	this.proxy = proxy;
	this.con = con;
	this.request = request;
	this.tlh = tlh;
    }

    /** Are we allowed to proxy ssl-type connections ?
     * @return true if we allow the CONNECT &lt;port&gt; command.
     */	
    public boolean isAllowed () {	
	String hp = request.getRequestURI ();
	int c = hp.indexOf (':');
	Integer port = new Integer (443);
	if (c >= 0) {
	    try {
		port = new Integer (hp.substring (c+1));
	    } catch (NumberFormatException e) {
		getLogger ().logWarn ("Connect to odd port: " + e);
		return false;
	    }
	}
	if (proxy.proxySSL == false)
	    return false;
	if (proxy.proxySSL == true && proxy.sslports == null)
	    return true;
	for (int i = 0; i < proxy.sslports.size (); i++) {
	    if (port.equals (proxy.sslports.get (i)))
		return true;
	}
	return false;
    }

    /** handle the tunnel.
     * @param channel the client channel
     * @param selector the proxy selector
     * @param buffer the buffer used, may contain data from client. 
     */
    public void handle (SocketChannel channel, Selector selector, 
			ByteBuffer buffer) {
    	//System.out.println("here");
	this.channel = channel;
	this.selector = selector;
	this.buffer = buffer;	
	if (proxy.isProxyConnected ()) {
	    String auth = proxy.getProxyAuthString ();
	    // it should look like this (using RabbIT:RabbIT):
	    // Proxy-authorization: Basic UmFiYklUOlJhYmJJVA==
	    request.setHeader ("Proxy-authorization", 
			       "Basic " + Coder.uuencode (auth));
	}
	WebConnectionListener wcl = new WebConnector ();
	proxy.getWebConnection (request, wcl);
    }

    private class WebConnector implements WebConnectionListener {
	private String uri;
	
	public WebConnector () {
	    uri = request.getRequestURI ();
	    // java needs protocoll to build URL
	    request.setRequestURI ("http://" + uri);
	}
	
	public void connectionEstablished (WebConnection wce) {
	    wc = wce;
	    if (proxy.isProxyConnected ()) {
		request.setRequestURI (uri); // send correct connect to next proxy.
		setupChain ();
	    }
	    sendOkReplyAndTunnel (null);
	}
	
	public void timeout () {
	    String err = "SSLHandler: Timeout waiting for web connection";
	    getLogger ().logWarn (err);	    
	    closeDown ();
	}

	public void failed (Exception e) {
	    String err = "SSLHandler: failed to get web connection: " + e;
	    getLogger ().logWarn (err);	    
	    closeDown ();
	}
    }

    private Logger getLogger () {
	return proxy.getLogger ();
    }
    
    private ConnectionLogger getConnectionLogger () {
	return proxy.getConnectionLogger ();
    }

    private void closeDown () {
	if (wc != null) {
	    try {
		wc.close ();
	    } catch (IOException e) {
		getLogger ().logWarn ("failed to close webconnection: " + e);
	    }
	    wc = null;
	}
	con.logAndClose (null);
    }

    private void setupChain () {
	HttpResponseListener cr = new ChainResponseHandler ();
	try {
	    HttpResponseReader sender = 
		new HttpResponseReader (wc.getChannel (), selector, 
					proxy.getLogger (), tlh.getNetwork (),
					request, proxy.getStrictHttp (), 
					proxy.isProxyConnected (), cr);
	} catch (IOException e) {
	    String err = "IOException when waiting for chained response: " + e;
	    getLogger ().logWarn (err);
	    closeDown ();
	}
    }

    private class ChainResponseHandler implements HttpResponseListener {
	public void httpResponse (HttpHeader response, ByteBuffer buffer, 
				  boolean keepalive, boolean isChunked, 
				  long dataSize) {
	    String status = response.getStatusCode (); 
	    if (!"200".equals (status)) {
		closeDown ();
	    } else {
		sendOkReplyAndTunnel (buffer);
	    }
	}

	public void failed (Exception cause) {
	    String err = "SSLHandler: failed to get chained response";
	    getLogger ().logWarn (err);
	    closeDown ();	    
	}
	
	public void timeout () {
	    String err = "SSLHandler: Timeout waiting for chained response";
	    getLogger ().logWarn (err);
	    closeDown ();
	}	
    }

    private void sendOkReplyAndTunnel (ByteBuffer server2client) {
	HttpHeader reply = new HttpHeader ();
	reply.setStatusLine ("HTTP/1.0 200 Connection established");
	reply.setHeader ("Proxy-agent", proxy.getServerIdentity ());
	
	HttpHeaderSentListener tc = new TunnelConnected (server2client);
	try {
	    HttpHeaderSender sender = 
		new HttpHeaderSender (channel, selector, proxy.getLogger (), 
				      tlh.getClient (), reply, false, tc);
	} catch (IOException e) {
	    getLogger ().logWarn ("IOException when sending header: " + e);
	    closeDown ();
	}	
    }

    private class TunnelConnected implements HttpHeaderSentListener {
	private ByteBuffer server2client;
	
	public TunnelConnected (ByteBuffer server2client) {
	    this.server2client = server2client;
	}
	
	public void httpHeaderSent () {
	    tunnelData (server2client);
	}
	
	public void timeout () {
	    String err = "SSLHandler: Timeout when sending http header";
	    getLogger ().logWarn (err);
	    closeDown ();
	}

	public void failed (Exception e) {
	    String err = "SSLHandler: Exception when sending http header: " + e;
	    getLogger ().logWarn (err);
	    closeDown ();
	}
    }

    private void tunnelData (ByteBuffer server2client) {
	SocketChannel sc = wc.getChannel ();
	try {
	    Tunnel t1 = new Tunnel (selector, getLogger (), channel, 
				    buffer, tlh.getClient (), sc, 
				    server2client, tlh.getNetwork (), this);
	} catch (IOException e) {
	    getLogger ().logWarn ("SSLHandler error setting up tunnels: " + e);
	    closeDown ();
	}
    }

    public void tunnelClosed () {
	if (wc != null) {
	    con.logAndClose (null);
	    try {
		wc.close ();
	    } catch (IOException e) {
		getLogger ().logWarn ("failed to close webconnection: " + e);
	    }
	}
	wc = null;
    }
}
