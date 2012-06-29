package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import rabbit.http.HttpHeader;
import rabbit.util.Logger;
import rabbit.util.TrafficLogger;

/** A handler that write one http header and reads a response
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpResponseReader 
    implements HttpHeaderSentListener, HttpHeaderListener {
    
    private SocketChannel channel;
    private Selector selector;
    private Logger logger;
    private TrafficLogger tl;
    private boolean strictHttp;
    private HttpResponseListener listener;
    
    public HttpResponseReader (SocketChannel channel, Selector selector, 
			       Logger logger, TrafficLogger tl, 
			       HttpHeader header, boolean fullURI, 
			       boolean strictHttp, 
			       HttpResponseListener listener)
	throws IOException {
	this.channel = channel;
	this.selector = selector;
	this.logger = logger;
	this.tl = tl;
	this.strictHttp = strictHttp;
	this.listener = listener;
	HttpHeaderSender sender = 
	    new HttpHeaderSender (channel, selector, logger, tl, 
				  header, fullURI, this);
    }
    
    public void httpHeaderSent () {
	try {
	    HttpHeaderReader requestReader = 
		new HttpHeaderReader (channel, null, selector, logger,
				      tl, false, strictHttp, this);
	} catch (IOException e) {
	    failed (e);
	}
    }
    
    public void httpHeaderRead (HttpHeader header, ByteBuffer buffer, 
				boolean keepalive, boolean isChunked, 
				long dataSize) {
	listener.httpResponse (header, buffer, keepalive, isChunked, dataSize);
    }
    
    public void closed () {
	listener.failed (new IOException ("Connection closed"));
    }
    
    public void failed (Exception cause) {
	listener.failed (cause);
    }

    public void timeout () {
	listener.timeout ();
    }
}
