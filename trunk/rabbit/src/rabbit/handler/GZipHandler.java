package rabbit.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;
import rabbit.http.HttpHeader;
import rabbit.proxy.BlockSender;
import rabbit.proxy.Connection;
import rabbit.proxy.TrafficLoggerHandler;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** This handler compresses the data passing through it. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class GZipHandler extends BaseHandler {
    protected boolean compress = true;
    protected GZIPOutputStream gz = null;
    private boolean isCompressing = false;
    private boolean addListener = false;
    private boolean waitingForBlockSent = false;

    /** For creating the factory.
     */
    public GZipHandler () {
    }
    
    /** Create a new GZipHandler for the given request.
     * @param con the Connection handling the request.
     * @param request the actual request made.
     * @param clientBuffer the client side buffer.
     * @param response the actual response.
     * @param content the resource.
     * @param mayCache May we cache this request? 
     * @param mayFilter May we filter this request?
     * @param size the size of the data beeing handled.
     * @param compress if we want this handler to compress or not.
     */
    public GZipHandler (Connection con, TrafficLoggerHandler tlh, 
			HttpHeader request, ByteBuffer clientBuffer,
			HttpHeader response, ResourceSource content, 
			boolean mayCache, boolean mayFilter, long size, 
			boolean compress) {
	super (con, tlh, request, clientBuffer, response, content, 
	       mayCache, mayFilter, size);
	this.compress = compress;
	if (compress) {
	    String gzip = response.getHeader ("Content-Encoding");
	    isCompressing = !(gzip != null 
			      && (gzip.equalsIgnoreCase ("gzip") 
				  || gzip.equalsIgnoreCase ("compress")
				  || gzip.equalsIgnoreCase ("deflate")));
	    if (isCompressing) {
		response.removeHeader ("Content-Length");
		response.setHeader ("Content-Encoding", "gzip");
		if (!con.getChunking ())
		    con.setKeepalive (false);
	    } else {
		this.mayFilter = false;
	    }
	}
    }    

    @Override
    public Handler getNewInstance (Connection con, TrafficLoggerHandler tlh,
				   HttpHeader header, ByteBuffer buffer, 
				   HttpHeader webHeader, 
				   ResourceSource content, boolean mayCache, 
				   boolean mayFilter, long size) {
	return new GZipHandler (con, tlh, header, buffer, webHeader, content,
				mayCache, mayFilter, size, 
				compress && mayFilter); 
    }

    /** 
     * ®return true this handler modifies the content.
     */
    @Override public boolean changesContentSize () {
	return true;
    }

    @Override
    protected void prepare () throws IOException {
	super.prepare ();
	if (isCompressing) {
	    Stream2Channel s2c = new GZStream2Channel ();
	    gz = new GZIPOutputStream (s2c);
	}
    }
    
    @Override
    protected void finishData () throws IOException {
	addListener = false;
	if (isCompressing)
	    gz.finish ();
	super.finishData ();
    }

    /** Check if this handler supports direct transfers.
     * @return this handler always return false.
     */
    @Override
    protected boolean mayTransfer () {
	return false;
    }

    protected class Stream2Channel extends OutputStream {
	public void write (byte[] b) throws IOException {
	    write (b, 0, b.length);
	}

	public void write (byte[] b, int off, int len) throws IOException {
	    ByteBuffer buf = ByteBuffer.wrap (b, off, len);
	    if (cacheChannel != null)
		writeCache (buf);
	    new BlockSender (con.getChannel (), con.getSelector (), 
			     getLogger (), tlh.getClient (), buf, 
			     con.getChunking (), GZipHandler.this);	    
	}

	public void write (int b) throws IOException {
	    byte[] buf = new byte[1];
	    buf[0] = (byte)b;
	    write (buf);
	}
    }

    protected class GZStream2Channel extends Stream2Channel {
	public void write (byte[] b, int off, int len) throws IOException {
	    waitingForBlockSent = true;
	    super.write (b, off, len);
	}
    }
    
    public void blockSent () {
	if (addListener) {
	    addListener = false;
	    content.addBlockListener (this);
	}
    }
    
    /** Write the current block of data to the gzipper.
     *  If you override this method you probably want to override 
     *  the modifyBuffer(ByteBuffer) as well.
     * @param arr the data to write to the gzip stream.
     */
    protected void writeDataToGZipper (byte[] arr) throws IOException {
	gz.write (arr);	
    }

    /** This method is used when we are not compressing data. 
     *  This method will just call "super.bufferRead (buf);"
     * @param buf the buffer that was just read.
     */
    protected void modifyBuffer (ByteBuffer buf) {
	super.bufferRead (buf);	
    }

    @Override
    public void bufferRead (ByteBuffer buf) {
	if (isCompressing) {
	    try {
		// we normally have direct buffers and we can not use
		// array() on them. Create a new byte[] and copy data into it.
		byte[] arr = new byte[buf.remaining ()];
		buf.get (arr);
		addListener = true;
		waitingForBlockSent = false;
		writeDataToGZipper (arr);
		if (!waitingForBlockSent)
		    content.addBlockListener (this);
	    } catch (IOException e) {
		failed (e);
	    }
	} else {
	    addListener = true;
	    modifyBuffer (buf);
	}
    }
    
    @Override 
    public void setup (Logger logger, SProperties prop) {
	if (prop != null) {
	    String comp = prop.getProperty ("compress", "true");
	    if (comp.equalsIgnoreCase ("false"))
		compress = false;
	    else
		compress = true;
	}
    }
}
