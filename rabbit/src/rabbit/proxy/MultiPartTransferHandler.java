package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import rabbit.http.HttpHeader;

/** A handler that transfers request resources with multipart data.
 *  Will send the multipart upstream. Note that we can only do this 
 *  if we know that the upstream server is HTTP/1.1 compatible.
 *  
 *  How do we determine if upstream is HTTP/1.1 compatible? 
 *  If we can not then we have to add a Content-Length header, 
 *  That means we have to buffer the full resource.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class MultiPartTransferHandler extends ResourceHandlerBase 
    implements BlockSentListener {
    private MultiPartPipe mpp = null;

    public MultiPartTransferHandler (Connection con, 
				     ByteBuffer buffer, 
				     TrafficLoggerHandler tlh, 
				     String ctHeader) {
	super (con, buffer, tlh);
	mpp = new MultiPartPipe (ctHeader);
    }
    
    public void modifyRequest (HttpHeader header) {
	// nothing.
    }

    void sendBuffer () {
	try {
	    ByteBuffer sendBuffer = buffer.slice ();
	    mpp.parseBuffer (sendBuffer);
	    BlockSender bs = 
		new BlockSender (wc.getChannel (), con.getSelector (), 
				 con.getLogger (), tlh.getNetwork (),
		 		 sendBuffer, false, this);
	} catch (IOException e) {
	    failed (e);
	}
    }
    public void blockSent () {
	if (!mpp.isFinished ())
	    doTransfer ();
	else 
	    listener.clientResourceTransferred ();
    }
}
