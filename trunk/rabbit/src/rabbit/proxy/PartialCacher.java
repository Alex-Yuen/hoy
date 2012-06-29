package rabbit.proxy;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.StringTokenizer;
import rabbit.http.HttpHeader;
import rabbit.http.ContentRangeParser;
import rabbit.util.Logger;

/** An updater that writes an updated range to a cache file.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class PartialCacher {
    private ContentRangeParser crp;
    private FileWriter fw;

    public PartialCacher (Logger logger, String fileName, HttpHeader response) 
	throws IOException {
	//Content-Range: 0-4/25\r\n
	String cr = response.getHeader ("Content-Range");
	if (cr != null) 
	    crp = new ContentRangeParser (cr, logger);
	if (!crp.isValid ())
	    throw new IllegalArgumentException ("bad range: " + cr);
	RandomAccessFile raf = new RandomAccessFile (fileName, "rw");
	FileChannel fc = raf.getChannel ();
	fc.position (crp.getStart ());
	fw = new FileWriter (fc);
    }
    
    private class FileWriter implements WritableByteChannel {
	private FileChannel fc;

	public FileWriter (FileChannel fc) {
	    this.fc = fc;
	}

	public int write (ByteBuffer src) throws IOException {
	    return fc.write (src);
	}
	
	public boolean isOpen () {
	    return fc.isOpen ();
	}

	public void close () throws IOException {
	    fc.close ();
	}
    }

    public WritableByteChannel getChannel () {
	return fw;
    }

    public long getStart () {
	return crp.getStart ();
    }
    
    public long getEnd () {
	return crp.getEnd ();
    }

    public long getTotal () {
	return crp.getTotal ();
    }
}
