package rabbit.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import rabbit.http.HttpDateParser;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.proxy.HttpHeaderSender;
import rabbit.proxy.HttpHeaderSentListener;
import rabbit.proxy.TransferHandler;
import rabbit.proxy.TransferListener;
import rabbit.proxy.Transferable;
import rabbit.util.SProperties;
import rabbit.util.TrafficLogger;

/** A file resource handler. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class FileSender implements MetaHandler, HttpHeaderSentListener {
    private Connection con;
    private TrafficLogger tlClient;
    private TrafficLogger tlProxy;
    private FileInputStream fis;
    private FileChannel fc;
    private long length;
    
    public void handle (HttpHeader request, 
			SProperties htab, 
			Connection con, 
			TrafficLogger tlProxy,
			TrafficLogger tlClient) throws IOException{
	this.con = con;
	this.tlProxy = tlProxy;
	this.tlClient = tlClient;
	
	String file = htab.getProperty ("argstring");
	if (file == null) 
	    throw (new IllegalArgumentException ("no file given."));
	if (file.indexOf ("..") >= 0)    // file is un-url-escaped 
	    throw (new IllegalArgumentException ("Bad filename given"));
	
	String filename = "htdocs/" + file;
	if (filename.endsWith ("/"))
	    filename = filename + "index.html";
	filename = filename.replace ('/', File.separatorChar);

	File fle = new File (filename);
	if (!fle.exists ()) {
	    // remove htdocs
	    do404 (filename.substring (7)); 
	    return;
	}

	// TODO: check etag/if-modified-since and handle it.
	HttpHeader response = con.getHttpGenerator ().getHeader ();
	setMime (filename, response);

	length = fle.length ();
	response.setHeader ("Content-Length", Long.toString (length));
	con.setContentLength (response.getHeader ("Content-Length"));
	Date lm = new Date (fle.lastModified () - 
			    con.getProxy ().getOffset ());
	response.setHeader ("Last-Modified", 
			    HttpDateParser.getDateString (lm));	
	try {
	    fis = new FileInputStream (filename);
	} catch (IOException e) {
	    throw (new IllegalArgumentException ("Could not open file: '" + 
						 file + "'."));
	}
	sendHeader (response);
    }

    private void setMime (String filename, HttpHeader response) {
	// TODO: better filename mapping.
	if (filename.endsWith ("gif"))
	    response.setHeader ("Content-type", "image/gif");
	else if (filename.endsWith ("jpeg") || filename.endsWith ("jpg"))
	    response.setHeader ("Content-type", "image/jpeg");
	else if (filename.endsWith ("txt"))
	    response.setHeader ("Content-type", "text/plain");
    }

    private void do404 (String filename) 
	throws IOException {
	HttpHeader response = con.getHttpGenerator ().get404 (filename);
	sendHeader (response);
    }

    private void sendHeader (HttpHeader header) 
	throws IOException {
	HttpHeaderSender hhs = 
	    new HttpHeaderSender (con.getChannel (), con.getSelector (), 
				  con.getLogger (), tlClient, header, 
				  true, this);
    }

    /** Write the header and the file to the output. 
     */
    private void channelTransfer (long length) {
	TransferListener ftl = new FileTransferListener ();
	TransferHandler th = 
	    new TransferHandler (con.getProxy (), 
				 new FCTransferable (length), 
				 con.getChannel (), tlProxy, tlClient, ftl);
	th.transfer ();	
    }

    private class FCTransferable implements Transferable {
	private long length; 
	
	public FCTransferable (long length) {
	    this.length = length;
	}
	
	public long transferTo (long position, long count, 
				WritableByteChannel target) 
	    throws IOException { 
	    return fc.transferTo (position, count, target);
	}
	
	public long length () {
	    return length;
	}
    }

    private class FileTransferListener implements TransferListener {
	public void transferOk () {
	    closeFile ();
	    con.logAndRestart ();	    
	}
	
	public void failed (Exception cause) {
	    closeFile ();
	    FileSender.this.failed (cause);
	}
    }

    private void closeFile () {
	if (fc != null) {
	    try {
		fc.close ();
	    } catch (IOException e) {
		con.getLogger ().logWarn ("Exception closing channel: " + e);
	    }
	}
	if (fis != null) {
	    try {
		fis.close ();	
	    } catch (IOException e) {
		con.getLogger ().logWarn ("Exception closing file: " + e);
	    }
	}
    }
    
    public void httpHeaderSent () {
	if (fis != null) {
	    fc = fis.getChannel ();
	    channelTransfer (length);
	} else {
	    con.logAndRestart ();
	}
    }

    public void failed (Exception e) {
	closeFile ();
	con.getLogger ().logWarn ("Exception when handling meta: " + e);
	con.logAndClose (null);
    }

    public void timeout () {
	closeFile ();
	con.getLogger ().logWarn ("Timeout when handling meta.");
	con.logAndClose (null);
    }
}
