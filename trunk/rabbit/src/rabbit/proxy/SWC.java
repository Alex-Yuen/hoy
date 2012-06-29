package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.Date;
import rabbit.io.WebConnection;
import rabbit.io.WebConnectionListener;
import rabbit.http.HttpDateParser;
import rabbit.http.HttpHeader;

/** A class that tries to establish a connection to the real server
 *  or the next proxy in the chain. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SWC implements HttpHeaderSentListener, 
                            HttpHeaderListener, WebConnectionListener, 
                            ClientResourceTransferredListener {
    private Connection con;
    private long timeOffset;
    private HttpHeader header; 
    private ByteBuffer buffer; 
    private TrafficLoggerHandler tlh;
    private ClientResourceHandler crh;
    private Connection.RequestHandler rh;
    
    private int attempts = 0;
    private String method;
    private boolean safe = true;

    private char status = '0';

    private Exception lastException;

    public SWC (Connection con, long timeOffset,
		HttpHeader header, ByteBuffer buffer, 
		TrafficLoggerHandler tlh, 
		ClientResourceHandler crh,
		Connection.RequestHandler rh) {
	this.con = con; 
	this.timeOffset = timeOffset;
	this.header = header;
	this.buffer = buffer;
	this.tlh = tlh;
	this.crh = crh;
	this.rh = rh;	
	method = header.getMethod ().trim ();
    }

    public void establish () {
	attempts++;
	con.getCounter ().inc ("Trying to establish a WebConnection: " + 
			       attempts);
	
	// if we cant get a connection in five cancel..
	if (!safe || attempts > 5) {
	    con.webConnectionSetupFailed (rh, lastException);
	} else {
	    rh.requestTime = System.currentTimeMillis ();
	    con.getProxy ().getWebConnection (header, this);
	}
    } 

    public void connectionEstablished (WebConnection wc) {
	con.getCounter ().inc ("WebConnection established: " + 
			       attempts);
	rh.wc = wc;
	/* TODO: handle this
 	if (header.getContentStream () != null) 
	    header.setHeader ("Transfer-Encoding", "chunked");
	*/

	// we cant retry if we sent the header...
	safe = wc.getReleasedAt () > 0 
	    || (method != null 
		&& (method.equals ("GET") || method.equals ("HEAD")));
	
	try {
	    if (crh != null)
		crh.modifyRequest (header);
	    
	    HttpHeaderSender sender = 
		new HttpHeaderSender (wc.getChannel (), con.getSelector (), 
				      con.getLogger (), tlh.getNetwork (), 
				      header, con.useFullURI (), this);
	} catch (IOException e) {
	    failed (e);
	}
    }

    public void httpHeaderSent () {
	if (crh != null)
	    crh.transfer (rh.wc, this);
	else
	    httpHeaderSentTransferDone ();
    }	
    
    public void clientResourceTransferred () {
	httpHeaderSentTransferDone ();
    }

    public void clientResourceAborted (HttpHeader reason) {	
	if (rh != null && rh.wc != null) {
	    rh.wc.setKeepalive (false); 
	    con.getProxy ().releaseWebConnection (rh.wc);
	}
	con.sendAndClose (reason);
    }
    
    private void httpHeaderSentTransferDone () {
	if (!header.isDot9Request ()) {
	    readRequest ();
	} else {
	    // HTTP/0.9 close after resource..
	    rh.wc.setKeepalive (false);
	    con.webConnectionEstablished (rh);
	}
    }

    private void readRequest () {
	con.getCounter ().inc ("Trying read response from WebConnection: " + 
			       attempts);
	try {
	    HttpHeaderReader hhreader = 
		new HttpHeaderReader (rh.wc.getChannel (), rh.webBuffer, 
				      con.getSelector (), con.getLogger (),
				      tlh.getNetwork (), false, 
				      con.getProxy ().getStrictHttp (), this);
	} catch (IOException e) {
	    failed (e);
	}
    }

    public void httpHeaderRead (HttpHeader header, ByteBuffer buffer, 
				boolean keepalive, boolean isChunked, 
				long dataSize) {
	con.getCounter ().inc ("Read response from WebConnection: " + 
			       attempts);
	rh.webHeader = header;
	rh.webBuffer = buffer;
	rh.wc.setKeepalive (keepalive);
	
	String sc = rh.webHeader.getStatusCode ();
	if (sc.length () > 0 && (status = sc.charAt (0)) == '1') {
	    //if client is using http/1.1
	    if (con.getRequestVersion ().endsWith ("1.1")) {
		// tell client
		Looper l = new Looper ();
		con.getCounter ().inc ("WebConnection got 1xx reply " + 
				       attempts);
		try {
		    HttpHeaderSender sender = 
			new HttpHeaderSender (con.getChannel (), 
					      con.getSelector (), 
					      con.getLogger (), 
					      tlh.getClient (),
					      header, false, l);
		    return;
		} catch (IOException e) {
		    failed (e);
		}
	    }
	}
	
	// since we have posted the full request we 
	// loop while we get 100 (continue) response.
	if (status == '1') {
	    readRequest ();
	} else {
	    String responseVersion = rh.webHeader.getResponseHTTPVersion ();
	    setAge (rh);
	    WarningsHandler wh = new WarningsHandler ();
	    wh.removeWarnings (con.getLogger (), rh.webHeader, false);
	    rh.webHeader.addHeader ("Via", responseVersion + " RabbIT");
	    HttpProxy proxy = con.getProxy ();
	    rh.size = dataSize;
	    rh.content = 
		new WebConnectionResourceSource (con.getSelector (), rh.wc, 
						 buffer, tlh.getNetwork (), 
						 isChunked, dataSize, 
						 proxy.getStrictHttp ());
	    con.webConnectionEstablished (rh);
	}
    }

    public void closed () {
	lastException = new IOException ("closed");
	establish ();
    }
    
    /** Calculate the age of the resource, needs ntp to be accurate. 
     */
    private void setAge (Connection.RequestHandler rh) {
	long now = System.currentTimeMillis ();
	String age = rh.webHeader.getHeader ("Age");
	String date = rh.webHeader.getHeader ("Date");
	Date dd = HttpDateParser.getDate (date);
	long ddt = now;
	if (dd != null)
	    ddt = dd.getTime ();
	long lage = 0;
	try {
	    if (age != null)
		lage = Long.parseLong (age);
	    long dt = Math.max ((now - ddt) / 1000, 0);
	    long correct_age = lage + dt;
	    long correct_recieved_age = Math.max (dt, lage);
	    long corrected_initial_age = correct_recieved_age + dt;
	    if (corrected_initial_age > 0) {
		rh.webHeader.setHeader ("Age", "" + corrected_initial_age);
	    }
	} catch (NumberFormatException e) {
	    // if we cant parse it, we leave the Age header..
	    con.getLogger ().logWarn ("Bad age: " + age);
	}
    }

    private class Looper implements HttpHeaderSentListener {
	
	public void httpHeaderSent () {
	    // read the next request...
	    readRequest ();
	}
	
	public void timeout () {
	    SWC.this.timeout ();
	}
	
	public void failed (Exception e) {
	    SWC.this.failed (e);	    
	}
    }
    
    public void timeout () {
	// retry
	lastException = new IOException ("timeout");
	establish ();
    }
    
    public void failed (Exception e) {
	lastException = e;
	con.getCounter ().inc ("WebConnections failed: " + 
			       attempts + ": " + e);
	if (rh.wc != null) {
	    try {
		rh.wc.close ();
	    } catch (IOException ioe) {
		con.getLogger ().logWarn ("Unable to close WebConnection" + 
					  ioe);
	    }
	}
	rh.wc = null;

	// retry
	establish ();
    }
}
