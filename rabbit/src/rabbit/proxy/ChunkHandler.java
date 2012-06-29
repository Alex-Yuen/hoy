package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;
import rabbit.handler.BlockListener;

class ChunkHandler {
    private ChunkDataFeeder feeder;
    private boolean strictHttp;
    private int currentChunkSize = -1;
    private int readFromChunk = 0;
    private boolean readTrailingCRLF = false;
    private boolean readExtension = false;
    private BlockListener listener;
    private long totalRead = 0;    

    public ChunkHandler (ChunkDataFeeder feeder, boolean strictHttp) {
	this.feeder = feeder;
	this.strictHttp = strictHttp;
    }

    public void addBlockListener (BlockListener listener) {
	this.listener = listener;
    }

    public long getTotalRead () {
	return totalRead;
    }

    private boolean needChunkSize () {
	return currentChunkSize == -1;
    }
    
    public void handleData (ByteBuffer buffer) {
	try {
	    if (needChunkSize ()) {
		buffer.mark ();
		if (!readTrailingCRLF && totalRead > 0) {
		    if (buffer.remaining () < 2) {
			feeder.readMore ();
			return;
		    }
		    readOffCRLF (buffer);
		    buffer.mark ();
		}
		LineReader lr = new LineReader (strictHttp);
		lr.readLine (buffer, new ChunkSizeHandler ());
		if (currentChunkSize == 0) {
		    readFooter (buffer);
		} else if (currentChunkSize > -1) {
		    readFromChunk = 0;
		    handleChunkData (buffer);
		} else {
		    buffer.reset ();
		    if (buffer.position () > 0) {
			feeder.readMore ();
		    } else {
			if (checkChunkSizeAndExtension (buffer)) {
			    // rest of buffer is a huge extension that we 
			    // do not recognize, so we ignore it...
			    feeder.readMore ();
			} else {
			    String err = "failed to read chunk size";
			    listener.failed (new IOException (err));
			}
		    }
		}
	    } else if (readExtension) {
		tryToReadExtension (buffer);
	    } else {
		if (currentChunkSize == 0) 
		    readFooter (buffer);
		else
		    handleChunkData (buffer);
	    }
	} catch (IOException e) {
	    listener.failed (e);
	}
    }

    private void tryToReadExtension (ByteBuffer buffer) throws IOException {
	LineReader lr = new LineReader (strictHttp);
	lr.readLine (buffer, new ExtensionHandler ());
	if (readExtension) {
	    feeder.readMore ();
	} else {
	    if (currentChunkSize == 0) {
		readFooter (buffer);
	    } else if (currentChunkSize > -1) {
		readFromChunk = 0;
		handleChunkData (buffer);
	    }
	}
    }

    private boolean checkChunkSizeAndExtension (ByteBuffer buffer) {
	buffer.mark ();
	int pos = buffer.position ();
	StringBuilder sb = new StringBuilder ();
	while (buffer.remaining () > 0) {
	    byte b = buffer.get ();
	    if (!(b >= '0' && b <= '9' || 
		  b >= 'a' && b <= 'f' || 
		  b >= 'A' && b <= 'F' || 
		  b == ';')) {
		buffer.reset ();
		return false;
	    }
	    if (b == ';') {
		// ok, extension follows. 
		currentChunkSize = Integer.parseInt (sb.toString (), 16);
		readExtension = true;
		buffer.position (buffer.limit ());
		return true;
	    }
	    sb.append ((char)b);
	}
	// ok, if we get here it may be a valid chunk size, 
	// but it will be very large... ignore for now.
	return false;
    }

    private void handleChunkData (ByteBuffer buffer) {
	int remaining = buffer.remaining ();	
	int leftInChunk = currentChunkSize - readFromChunk;
	int thisChunk = Math.min (remaining, leftInChunk);
	if (thisChunk == 0) {
	    feeder.readMore ();
	    return;
	}
	readFromChunk += thisChunk;
	totalRead += thisChunk;
	if (thisChunk < remaining) {
	    int limit = buffer.limit ();
	    ByteBuffer copy = buffer.duplicate ();
	    int nextPos = buffer.position () + thisChunk;
	    copy.limit (nextPos);
	    buffer.position (nextPos);
	    if (readFromChunk == currentChunkSize) {
		currentChunkSize = -1;
		readTrailingCRLF = false;
	    }
	    listener.bufferRead (copy);
	} else {
	    // all the rest of the current buffer
	    if (readFromChunk == currentChunkSize) {
		currentChunkSize = -1;
		readTrailingCRLF = false;
	    }
	    listener.bufferRead (buffer);
	}
    }

    private void readOffCRLF (ByteBuffer buffer) throws IOException {
	byte b1 = buffer.get ();
	byte b2 = buffer.get ();
	if (!(b1 == '\r' && b2 == '\n'))
	    throw new IOException ("failed to read CRLF: " + (int)b1 + ", " + (int)b2);
	readTrailingCRLF = true;
    }

    private void readFooter (ByteBuffer buffer) throws IOException {
	LineReader lr = new LineReader (strictHttp);
	EmptyLineHandler elh;
	do {
	    buffer.mark ();
	    elh = new EmptyLineHandler ();
	    lr.readLine (buffer, elh);
	    if (!elh.lineRead ()) {
		feeder.readMore ();
		return;
	    }
	} while (!elh.ok ());
	listener.finishedRead ();
    }
    
    private class EmptyLineHandler implements LineListener {
	private boolean ok = false;
	private boolean lineRead = false;
	public void lineRead (String line) throws IOException {
	    lineRead = true;
	    ok = "".equals (line);
	} 
	public boolean ok () {
	    return ok;
	}
	public boolean lineRead () {
	    return lineRead;
	} 
    }
    
    private class ChunkSizeHandler implements LineListener {
	public void lineRead (String line) throws IOException {
	    StringTokenizer st = new StringTokenizer (line, "\t \n\r(;");
	    if (st.hasMoreTokens ()) {
		String hex = st.nextToken ();
		try {
		    currentChunkSize = Integer.parseInt (hex, 16);
		} catch (NumberFormatException e) {
		    throw new IOException ("Chunk size is not a hex number: '" +
					   line + "', '" + hex + "'.");
		}
	    } else {
		throw new IOException ("Chunk size is not available.");
	    }
	}
    }
    
    private class ExtensionHandler implements LineListener {
	public void lineRead (String line) throws IOException {
	    readExtension = false;
	}
    }
}
