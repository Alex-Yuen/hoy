package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import rabbit.handler.BlockListener;
import rabbit.http.HttpHeader;

/** A handler that transfers chunked request resources.
 *  Will chunk data to the real server or fail. Note that we can only 
 *  do this if we know that the upstream server is HTTP/1.1 compatible.
 *  
 *  How do we determine if upstream is HTTP/1.1 compatible? 
 *  If we can not then we have to add a Content-Length header and not chunk, 
 *  That means we have to buffer the full resource.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class ChunkedContentTransferHandler extends ResourceHandlerBase 
    implements ChunkDataFeeder, BlockListener, BlockSentListener {

    private boolean sentEndChunk = false;
    private ChunkHandler chunkHandler;

    public ChunkedContentTransferHandler (Connection con, 
					  ByteBuffer buffer, 
					  TrafficLoggerHandler tlh) {
	super (con, buffer, tlh);
	chunkHandler = new ChunkHandler (this, con.getProxy ().getStrictHttp ());
	chunkHandler.addBlockListener (this);
    }

    public void modifyRequest (HttpHeader header) {
	header.setHeader ("Transfer-Encoding", "chunked");
    }

    void sendBuffer () {
	chunkHandler.handleData (buffer);
    }

    public void bufferRead (ByteBuffer buf) {
	try {
	    BlockSender bs = 
		new BlockSender (wc.getChannel (), con.getSelector (), 
				 con.getLogger (), tlh.getNetwork (),
		 		 buf, true, this);
	} catch (IOException e) {
	    failed (e);
	}
    }

    public void finishedRead () {
	try {
	    ChunkEnder ce = new ChunkEnder ();
	    sentEndChunk = true;	
	    ce.sendChunkEnding (wc.getChannel (), con.getSelector (), 
				con.getLogger (), tlh.getNetwork (), this);
	} catch (IOException e) {
	    failed (e);
	}
    }    
    
    public void register () {
	waitForRead ();
    }

    public void readMore () {
	buffer.compact ();
	register ();
    }

    public void blockSent () {
	if (sentEndChunk)
	    listener.clientResourceTransferred ();
	else 
	    doTransfer ();
    }
}
