package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import rabbit.util.Logger;
import rabbit.util.TrafficLogger;
import rabbit.proxy.BlockSentListener;

/** A class that sends the chunk ending (with an empty footer).
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ChunkEnder {
    private static final byte[] CHUNK_ENDING = 
    new byte[] {'0', '\r', '\n', '\r', '\n'};

    public void sendChunkEnding (SocketChannel channel, Selector selector, 
				 Logger logger, TrafficLogger tl, 
				 BlockSentListener bsl) 
	throws IOException {
	ByteBuffer bb = ByteBuffer.wrap (CHUNK_ENDING);
	new BlockSender (channel, selector, logger, tl, bb, false, bsl);
    }
}
