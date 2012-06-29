package rabbit.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import rabbit.util.Logger;
import rabbit.util.TrafficLogger;

/** A handler that writes data blocks.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class BlockSender extends BaseSocketHandler {
    private ByteBuffer chunkBuffer;
    private ByteBuffer buffer;
    private ByteBuffer end;
    private ByteBuffer[] buffers;
    private TrafficLogger tl;
    private boolean chunking;
    private BlockSentListener sender;
    private int ops = 0;
    
    public BlockSender (SocketChannel channel, Selector selector, 
			Logger logger, TrafficLogger tl, ByteBuffer buffer, 
			boolean chunking, BlockSentListener sender) 
	throws IOException {
	super (channel, null, selector, logger);
	this.tl = tl;
	this.buffer = buffer;
	this.chunking = chunking;
	if (chunking) {
	    int len = buffer.remaining ();
	    String s = Long.toHexString (len) + "\r\n";
	    try {
		chunkBuffer = ByteBuffer.wrap (s.getBytes ("ASCII"));
	    } catch (UnsupportedEncodingException e) {
		logger.logError ("BlockSender: ASCII not found!");
	    }
	    end = ByteBuffer.wrap (new byte[] {'\r', '\n'});
	    buffers = new ByteBuffer[]{chunkBuffer, buffer, end};
	} else {
	    buffers = new ByteBuffer[]{buffer};
	    end = buffer;
	}
	this.sender = sender;
	writeBuffer ();
    }

    @Override protected int getSocketOperations () {
	return ops;
    }

    public void timeout () {
	sender.timeout ();
    }

    public void run () {
	try {
	    writeBuffer ();
	} catch (IOException e) {
	    sender.failed (e);
	}
    }
    
    private void writeBuffer () throws IOException {
	long written;
	do {
	    written = channel.write (buffers);
	    tl.write (written);
	} while (written > 0 && end.remaining () > 0);

	if (end.remaining () == 0) {
	    unregister ();
	    sender.blockSent ();
	} else {
	    ops = SelectionKey.OP_WRITE;
	    register ();
	}
    }
}    
