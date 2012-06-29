package rabbit.proxy;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import rabbit.util.TrafficLogger;

/** A handler that transfers data from a Transferable to a socket channel. 
 *  Since file transfers may take time we run in a separate thread.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class TransferHandler implements Runnable {
    private TaskRunner tr;
    private Transferable t;
    private WritableByteChannel channel;
    private TrafficLogger tlFrom;
    private TrafficLogger tlTo;
    private TransferListener listener;
    private long pos = 0; 
    private long count;
    
    public TransferHandler (TaskRunner tr, 
			    Transferable t, WritableByteChannel channel, 
			    TrafficLogger tlFrom, TrafficLogger tlTo, 
			    TransferListener listener) {
	this.tr = tr;
	this.t = t;
	this.channel = channel;
	this.tlFrom = tlFrom;
	this.tlTo = tlTo;
	this.listener = listener;
	count = t.length ();
    }

    public void transfer () {
	tr.runThreadTask (this);
    }
    
    public void run () {
	try {
	    while (count > 0) {
		long written = 
		    t.transferTo (pos, count, channel);		    
		pos += written; 
		count -= written;
		tlFrom.transferFrom (written);
		tlTo.transferTo (written);
	    }
	    returnOk ();
	} catch (IOException e) {
	    returnWithFailure (e);
	}	    
    }
    
    private void returnWithFailure (final Exception cause) {
	tr.runMainTask (new Runnable () {
		public void run () {
		    listener.failed (cause);
		}
	    });	    
    }
    private void returnOk () {
	tr.runMainTask (new Runnable () {
		public void run () {
		    listener.transferOk ();
		}
	    });
    }
}
