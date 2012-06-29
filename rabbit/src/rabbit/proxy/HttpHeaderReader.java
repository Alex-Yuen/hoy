package rabbit.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import rabbit.http.Header;
import rabbit.http.HttpHeader;
import rabbit.util.Logger;
import rabbit.util.TrafficLogger;

/** A handler that reads http headers
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HttpHeaderReader extends BaseSocketHandler 
    implements LineListener {
    
    private HttpHeader header;
    private Header head = null;    
    private boolean append = false;
    private boolean request = true;
    private boolean strictHttp = true;
    private HttpHeaderListener reader;
    private boolean headerRead = false;

    // State variables.
    private boolean keepalive = true;
    private boolean ischunked = false;
    private long dataSize = -1;   // -1 for unknown.
    private int startParseAt = 0;

    private TrafficLogger tl;

    private static final ByteBuffer HTTP_IDENTIFIER = 
    ByteBuffer.wrap (new byte[]{(byte)'H', (byte)'T', (byte)'T', 
				(byte)'P', (byte)'/'});
    
    private static final ByteBuffer EXTRA_LAST_CHUNK = 
    ByteBuffer.wrap (new byte[]{(byte)'0', (byte)'\r', (byte)'\n', 
				(byte)'\r', (byte)'\n'});
    

    /** 
     * @param request true if a request is read, false if a response is read.
     *                Servers may respond without header (HTTP/0.9) so try to 
     *                handle that.
     */ 
    public HttpHeaderReader (SocketChannel channel, ByteBuffer buffer, 
			     Selector selector, Logger logger, TrafficLogger tl,
			     boolean request, boolean strictHttp,
			     HttpHeaderListener reader) 
	throws IOException {
	super (channel, buffer, selector, logger);
	this.tl = tl;
	this.request = request;
	this.strictHttp = strictHttp;
	this.reader = reader;
	if (buffer != null) {
//		String s = new String(buffer.array());
//		System.out.println(s);
	    if (buffer.hasRemaining ()) {
		startParseAt = buffer.position ();
		parseBuffer ();
	    } else {
		buffer.clear ();
	    }
	}
    }

    public void timeout () {
	reader.timeout ();
    }
    
    public void run () {
	Logger logger = getLogger ();
	try {
		 //System.out.println("2");
	    if (buffer == null)
		allocateBuffer ();
	    // read http request
	    // make sure we have room for reading.
	    int pos = buffer.position ();
	    buffer.limit (buffer.capacity ());
	    int read = channel.read (buffer);
	    if (read == -1) {
		closeDown ();
		reader.closed ();
		return;
	    }
	    tl.read (read);
	    buffer.position (startParseAt);
	    buffer.limit (read + pos);
	    parseBuffer ();
	} catch (IOException e) {
	    logger.logWarn ("Failed to handle connection: " + e);
	    reader.failed (e);
	}
    }
    
    private void parseBuffer () throws IOException {
	int startPos = buffer.position ();
	buffer.mark ();
	boolean done = handleBuffer ();
	if (!done) {
	    register ();
	    int fullPosition = buffer.position ();
	    buffer.reset ();
	    int pos = buffer.position ();
	    if (pos == startPos) {
		if (buffer.remaining () + pos >= buffer.capacity ()) {
		    // ok, we did no progress, abort, client is sending 
		    // too long lines. 
		    // TODO: perhaps grow buffer....
		    throw new RequestLineTooLongException ();
		} else {
		    // set back position so the next read aligns...
		    buffer.position (fullPosition);
		}		
	    } else {
		// ok, some data handled, make space for more.
		buffer.compact ();
		startParseAt = 0;
	    }
	} else {
	    setState ();	    
	    unregister ();
	    reader.httpHeaderRead (header, buffer, 
				   keepalive, ischunked, dataSize);
	}
    }

    @Override protected int getSocketOperations () {
	return 
	    buffer != null && buffer.hasRemaining () ? 0 : SelectionKey.OP_READ;
    }

    private void setState () {
	dataSize = -1;
	String cl = header.getHeader ("Content-Length");
	if (cl != null) {
	    try {
		dataSize = Long.parseLong (cl);
	    } catch (NumberFormatException e) {
		dataSize = -1;
	    }
	}
	String con = header.getHeader ("Connection");
	// Netscape specific header...
	String pcon = header.getHeader ("Proxy-Connection");
	if (con != null && con.equalsIgnoreCase ("close"))
	    setKeepAlive (false);
	if (keepalive && pcon != null && pcon.equalsIgnoreCase ("close"))
	    setKeepAlive (false);
	
	if (header.isResponse ()) {
	    if (header.getResponseHTTPVersion ().equals ("HTTP/1.1")) {
		String chunked = header.getHeader ("Transfer-Encoding");
		setKeepAlive (true);
		ischunked = false;
		
		if (chunked != null && chunked.equalsIgnoreCase ("chunked")) {
		    /* If we handle chunked data we must read the whole page
		     * before continuing, since the chunk footer must be 
		     * appended to the header (read the RFC)...
		     * 
		     * As of RFC 2616 this is not true anymore...
		     * this means that we throw away footers and it is legal.
		     */
		    ischunked = true;
		    header.removeHeader ("Content-Length");
		    dataSize = -1;
		}
	    } else {
		setKeepAlive (false);
	    }
	    
	    if (!(dataSize > -1 || ischunked))
		setKeepAlive (false);
	} else {
	    String httpVersion = header.getHTTPVersion ();
	    if (httpVersion != null && httpVersion.equals ("HTTP/1.1")) {
		String chunked = header.getHeader ("Transfer-Encoding");
		if (chunked != null && chunked.equalsIgnoreCase ("chunked")) {
		    ischunked = true;
		    header.removeHeader ("Content-Length");
		    dataSize = -1;
		}
	    }
	}
    }

    /** read the data from the buffer and try to build a http header.
     * 
     * @return true if a full header was read, false if more data is needed.
     */
    private boolean handleBuffer () throws IOException {
	if (!request && header == null) {
	    if (!verifyResponse ())
		return true;
	}
	LineReader lr = new LineReader (strictHttp);
	while (!headerRead && buffer.hasRemaining ())
	    lr.readLine (buffer, this);
	return headerRead;
    }

    /** Verify that the response starts with "HTTP/" 
     *  Failure to verify response => treat all of data as content = HTTP/0.9.
     */
    private boolean verifyResponse () throws IOException {	
	// some broken web servers (apache/2.0.4x) send multiple last-chunks
	if (buffer.remaining () > 4 && matchBuffer (EXTRA_LAST_CHUNK)) {
	    Logger log = getLogger ();
	    log.logWarn ("Found a last-chunk, trying to ignore it.");
	    buffer.position (buffer.position () + EXTRA_LAST_CHUNK.capacity ());
	    return verifyResponse ();
	}

	if (buffer.remaining () > 4 && !matchBuffer (HTTP_IDENTIFIER)) {
	    Logger log = getLogger ();
	    log.logWarn ("http response header with odd start:" + 
			 getBufferStartString (5));
	    header = new HttpHeader ();
	    header.setStatusLine ("HTTP/1.1 200 OK");
	    header.setHeader ("Connection", "close");
	    return true;
	}

	return true;
    }

    private boolean matchBuffer (ByteBuffer test) {
	int len = test.remaining ();
	if (buffer.remaining () < len)
	    return false;
	int pos = buffer.position ();
	for (int i = 0; i < len; i++)
	    if (buffer.get (pos + i) != test.get (i))
		return false;
	return true;
    }

    private String getBufferStartString (int size) {
	try {
	    byte[] arr = new byte[size];
	    buffer.get (arr);
	    return new String (arr, "ASCII");
	} catch (UnsupportedEncodingException e) {
	    return "unable to get ASCII: " + e.toString ();
	}
    }    
    
    /** Handle a newly read line. */
    public void lineRead (String line) throws IOException {
	if (line.length () == 0) {
	    headerRead = header != null;
	    return;
	}

	if (header == null) {
	    header = new HttpHeader ();
	    header.setRequestLine (line);
	    headerRead = false;
	    return;
	}

	if (header.isDot9Request ()) {
	    headerRead = true;
	    return;
	}

	char c;
	if (header.size () == 0 &&
	    line.length () > 0 && 
	    ((c = line.charAt (0)) == ' ' || c == '\t')) {
	    header.setReasonPhrase (header.getReasonPhrase () + line);
	    headerRead = false;
	    return;
	} 

	readHeader (line);
	headerRead = false;
    }

    public void readHeader (String msg) throws IOException {
	if (msg == null) 
	    throw (new IOException ("Couldnt read headers, connection must be closed"));
	char c = msg.charAt (0);
	if (c == ' ' || c == '\t' || append) {
	    if (head != null) {
		head.append (msg);
		append = checkQuotes (head.getValue ());
	    } else {
		throw (new IOException ("Malformed header from: " + 
					channel.socket ().getInetAddress () + 
					", msg: " + msg));
	    }
	    return;
	}
	int i = msg.indexOf (':');	    
	if (i < 0) {
	    switch (msg.charAt (0)) {
	    case 'h':
	    case 'H':
		if (msg.toLowerCase ().startsWith ("http/")) {
		    /* ignoring header since it looks
		     * like a duplicate responseline
		     */
		    return;
		}
		// fallthrough
	    default:
		throw (new IOException ("Malformed header:" + msg));
	    }
	}
	int j = i;
	while (j > 0 && ((c = msg.charAt (j - 1)) == ' ' || c == '\t'))
	    j--;
	// ok, the header may be empty, so trim away whites.
	String value = msg.substring (i + 1);
	
	/* there are some sites with broken headers
	 * like http://docs1.excite.com/functions.js
	 * which returns lines such as this (20040416) /robo
	 * msg is: 'Cache-control: must-revalidate"'
	 * so we only check for append when in strict mode...
	 */
	if (strictHttp) 
	    append = checkQuotes (value);
	if (!append)
	    value = value.trim ();
	head = new Header (msg.substring (0, j), value);
	header.addHeader (head);
    }

    private boolean checkQuotes (String v) {
	int q = v.indexOf ('"');
	if (q == -1)
	    return false;
	boolean halfquote = false;
	int l = v.length ();
	for (; q < l; q++) {
	    char c = v.charAt (q);
	    switch (c) {
	    case '\\':
		q++;    // skip one...
		break;
	    case '"':
		halfquote = !halfquote;
		break;
	    }
	}
	return halfquote;
    }

    /** Set the keep alive value to currentkeepalive & keepalive
     * @param keepalive the new keepalive value.
     */
    private void setKeepAlive (boolean keepalive) {
	this.keepalive = (this.keepalive && keepalive);
    }
}
