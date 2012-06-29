package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import rabbit.http.HttpHeader;

/** A handler that transfers request resources with a known content length.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class ContentTransferHandler extends ResourceHandlerBase 
    implements BlockSentListener {
    private long dataSize;
    private long transferred = 0;
    private long toTransfer = 0;
    
    public ContentTransferHandler (Connection con, 
				   ByteBuffer buffer, 
				   long dataSize, 
				   TrafficLoggerHandler tlh) {
	super (con, buffer, tlh);
	this.dataSize = dataSize;
    }

    public void modifyRequest (HttpHeader header) {
	// nothing.
    }
    
    void sendBuffer () {
	toTransfer = Math.min (buffer.remaining (), 
			       dataSize - transferred);
	ByteBuffer sendBuffer = buffer;
	if (toTransfer < buffer.remaining ()) {
	    int limit = buffer.limit ();
	    // int cast is safe since buffer.remaining returns an int
	    buffer.limit (buffer.position () + (int)toTransfer);
	    sendBuffer = buffer.slice ();
	    buffer.limit (limit);
	}
	try {
	    BlockSender bs = 
		new BlockSender (wc.getChannel (), con.getSelector (), 
				 con.getLogger (), tlh.getNetwork (),
		 		 sendBuffer, false, this);
	} catch (IOException e) {
	    failed (e);
	}
    }

    public void blockSent () {
	transferred += toTransfer;
	if (transferred < dataSize)
	    doTransfer ();
	else 
	    listener.clientResourceTransferred ();
    }
}
