package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;

/** A class that reads multipart data from one channel and writes 
 *  it to the other channel.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class MultiPartPipe {
    private String boundary;
    private boolean endFound = false;
    
    public MultiPartPipe (String ctHeader) {
	StringTokenizer st = new StringTokenizer (ctHeader, " =\n\r\t;");
	while (st.hasMoreTokens ()) {
	    String t = st.nextToken ();
	    if (t.equals ("boundary") && st.hasMoreTokens ()) {
		boundary = st.nextToken ();
		break;
	    }
	}
	if (boundary == null)
	    throw new IllegalArgumentException ("failed to find multipart " +
						"boundary in: '" + ctHeader + 						"'");
    }

    /** Parse the buffer, will set the position and the limit.
     */
    public void parseBuffer (ByteBuffer buf) throws IOException {
	int pos = buf.position ();
	LineReader lr = new LineReader (true);
	LineHandler lh = new LineHandler (buf);
	do {
	    lr.readLine (buf, lh);
	} while (!endFound && buf.hasRemaining ());
	
	// send the block. 
	buf.position (pos);
    }

    public boolean isFinished () {
	return endFound;
    }

    private class LineHandler implements LineListener {
	private ByteBuffer buf;
	
	public LineHandler (ByteBuffer buf) {
	    this.buf = buf;
	}
	
	// check for end line and if it is found we limit the buffer to 
	// this position.
	public void lineRead (String line) throws IOException {
	    if (line.startsWith ("--") && line.endsWith ("--")) {
		if (line.substring (2, line.length () - 2).equals (boundary)) {
		    buf.limit (buf.position ());
		    endFound = true;
		}
	    }
	}
    }
}
