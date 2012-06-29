package rabbit.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import rabbit.handler.BlockListener;
import rabbit.handler.ResourceSource;
import rabbit.http.HttpHeader;

/** A resource that comes from a file.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class FileResourceSource implements ResourceSource {    
    protected FileChannel fc;
    
    // used for block handling.
    private BlockListener listener;
    private TaskRunner tr;
    protected ByteBuffer buffer;

    public FileResourceSource (String filename, TaskRunner tr) 
	throws IOException {
	File f = new File (filename);
	if (!f.exists ())
	    throw new FileNotFoundException ("File: " + filename + 
					     " not found");
	if (!f.isFile ())
	    throw new FileNotFoundException ("File: " + filename + 
					     " is not a regular file");
	FileInputStream fis = new FileInputStream (f);
	fc = fis.getChannel ();
	this.tr = tr;
    }    

    /** FileChannels can be used, will always return true.
     * @return true
     */
    public boolean supportsTransfer () {
	return true;
    }

    public long length () {
	try {
	    return fc.size ();
	} catch (IOException e) {
	    e.printStackTrace ();
	    return -1;
	}
    }

    public long transferTo (long position, long count, 
			    WritableByteChannel target)
	throws IOException {
	return fc.transferTo (position, count, target);
    }

    /** Generally we do not come into this method, but it can happen..
     */
    public void addBlockListener (BlockListener listener) {
	this.listener = listener;
	tr.runThreadTask (new ReadBlock ());
    }

    private class ReadBlock implements Runnable {
	public void run () {
	    try {
		if (buffer == null) {
		    buffer = ByteBuffer.allocateDirect (4096);
		} else {
		    buffer.compact ();
		    buffer.limit (buffer.capacity ());
		}
		int read = fc.read (buffer);
		if (read == -1) {
		    returnFinished ();
		} else {
		    buffer.flip ();
		    returnBlockRead ();
		}
	    } catch (IOException e) {
		returnWithFailure (e);
	    }
	}
    }

    private void returnWithFailure (final Exception e) {
	tr.runMainTask (new Runnable () {
		public void run () {
		    listener.failed (e);
		}
	    });
    }

    private void returnFinished () {
	tr.runMainTask (new Runnable () {
		public void run () {
		    listener.finishedRead ();
		}
	    });
    }

    private void returnBlockRead () {
	tr.runMainTask (new Runnable () {
		public void run () {
		    listener.bufferRead (buffer);
		}
	    });
    }

    public void release (Connection con) {
	try {
	    if (fc != null)
		fc.close ();
	    listener = null;
	    tr = null;
	    buffer = null;
	} catch (IOException e) {
	    // TODO: handle...
	}
    }
}
