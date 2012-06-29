package rabbit.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import rabbit.io.Range;
import rabbit.io.WebConnection;
import rabbit.io.WebConnectionListener;
import rabbit.handler.BaseHandler;
import rabbit.http.HttpHeader;
import rabbit.http.ContentRangeParser;
import rabbit.util.Logger;

/** A class that tries to setup a resource from the cache
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SCC {
    private Connection con;
    private HttpHeader header; 
    private ByteBuffer buffer; 
    private Connection.RequestHandler rh;

    public SCC (Connection con, HttpHeader header, ByteBuffer buffer, 
		Connection.RequestHandler rh) {
	this.con = con;
	this.header = header;
	this.buffer = buffer;
	this.rh = rh;
    }

    private Logger getLogger () {
	return con.getLogger ();
    }

    /** 
     * @return null if everything looks ok, non-null on an errornous request.
     */
    public HttpHeader establish () throws IOException {
	String ifRange = header.getHeader ("If-Range");
	boolean mayRange = true;
	rh.webHeader = rh.dataHook;
	if (ifRange != null) {	    
	    String etag = rh.webHeader.getHeader ("ETag");
	    if (etag != null) {  
		/* rfc is fuzzy about if it should be weak or strong match here.
		mayRange = 
		    checkWeakEtag (rh.webheader.getHeader ("ETag"), ifRange);
		*/
		mayRange = con.checkStrongEtag (etag, ifRange);
	    } else {
		// we can't use strong validators on dates!
		mayRange = false;
	    }
	    CacheChecker cck = new CacheChecker ();
	    boolean cc = cck.checkConditions (con, header, rh.webHeader);
	    if (mayRange && !cc) {
		// abort...
		rh.webHeader = null;
		rh.content = null;
		return null;
	    } else if (!cc) {
		rh.content = null;
		return null;
	    }
	}

	List<Range> ranges = null;
	if (mayRange) {
	    try {
		ranges = getRanges (header, rh);
	    } catch (IllegalArgumentException e) {
		return con.getHttpGenerator ().get416 (e);
	    }
	}
	con.setChunking (false);
	if (ranges != null) {
	    long totalSize = getTotalSize (rh);
	    if (!haveAllRanges (ranges, rh, totalSize)) {
		// abort and get from web..
		rh.webHeader = null;
		rh.content = null;
		return null;
	    }
	    setupRangedEntry (ifRange, ranges, totalSize);
	} else {
	    HttpProxy proxy = con.getProxy ();
	    rh.content = 
		new CacheResourceSource (proxy.getCache (), rh.entry, proxy);
	    rh.size = rh.entry.getSize ();
	    rh.webHeader.setStatusCode ("200");
	    rh.webHeader.setReasonPhrase ("OK");
	}
	setAge ();
	// do we have a handler for it?
	HttpProxy proxy = con.getProxy ();
	String ctype = rh.webHeader.getHeader ("Content-Type"); 
 	if (ctype != null)
 	    rh.handlerFactory = proxy.getCacheHandlerFactory (ctype);
 	if (rh.handlerFactory == null || ranges != null)
	    // Simply send, its already filtered.
	    rh.handlerFactory = new BaseHandler (); 
	WarningsHandler wh = new WarningsHandler ();
	wh.removeWarnings (getLogger (), rh.webHeader, false);
	return null;
    }

    private void setupRangedEntry (String ifRange, List<Range> ranges, 
				   long totalSize) 
	throws IOException {
	HttpProxy proxy = con.getProxy ();
	rh.content = 
	    new RandomCacheResourceSource (proxy.getCache (), 
					   rh, proxy,
					   ranges, totalSize);
	con.setChunking (false);
	rh.webHeader = 
	    con.getHttpGenerator ().get206 (ifRange, rh.webHeader);
	con.setStatusCode ("206");
	if (ranges.size () > 1) {
	    //prepare multipart...
	    rh.webHeader.removeHeader ("Content-Length");
	    String CT = 
		"multipart/byteranges; boundary=THIS_STRING_SEPARATES";
	    rh.webHeader.setHeader ("Content-Type", CT);
	} else {
	    Range r = ranges.get (0);
	    rh.webHeader.setHeader ("Content-Range", 
				    "bytes " + r.getStart () + "-" + 
				    r.getEnd () + "/" + totalSize);
	    rh.size = (r.getEnd () - r.getStart () + 1);
	    rh.webHeader.setHeader ("Content-Length", "" + rh.size);
	}		
    }

    private void setAge () {
	String age = rh.webHeader.getHeader ("Age");
	long now = System.currentTimeMillis ();
	long secs = (now - rh.entry.getCacheTime ()) / 1000;
	if (age != null) {
	    try {
		long l = Long.parseLong (age);
		secs += l;
	    } catch (NumberFormatException e) {
		getLogger ().logWarn ("bad Age : '" + age + "'");
	    }
	}
	rh.webHeader.setHeader ("Age", "" + secs);
    }

    private List<Range> getRanges (HttpHeader header, Connection.RequestHandler rh) {
	//Range: bytes=10-,11-10\r\n
	List<String> ranges = header.getHeaders ("Range");
	int z = ranges.size ();
	if (z == 0)
	    return null;
	List<Range> ret = new ArrayList<Range> ();
	try {
	    for (int i = 0; i < z; i++) {
		String rs = ((String)ranges.get (i)).trim ();
		if (!rs.startsWith ("bytes"))
		    return null;
		rs = rs.substring (5);
		int j = rs.indexOf ('=');
		if (j == -1)
		    return null;
		rs = rs.substring (j + 1);
		String[] st = rs.split (",");
		for (String r : st) {
		    Range range = parseRange (r);
		    if (range == null)
			return null;
		    ret.add (range);
		}
	    }
	} catch (NumberFormatException e) {
	    return null;
	}
	return ret;
    }

    private static final String SBTZ = 
    "bad range: start bigger than size";
    private static final String SAELTZ = 
    "bad range: start and end both less than zero";
    private static final String SLTZ = 
    "bad range: start less than zero";
    private static final String FR = 
    "bad range: full range";

    private Range parseRange (String r) {
	int d = r.indexOf ('-');
	if (d == -1)
	    throw new NumberFormatException ("bad range: no '-'");
	String s = r.substring (0, d).trim ();
	String e = r.substring (d + 1).trim ();
	long start = -1;
	long end = -1;
	long size = rh.entry.getSize ();
	if (s.length () > 0) {
	    start = Integer.parseInt (s);
	    if (e.length () > 0) {
		end = Integer.parseInt (e);
	    } else {
		// to the end...
		end = size;
	    }
	    if (start > size)		
		throw new IllegalArgumentException (SBTZ);
	    if (start > end)  // ignore this...
		return null;
	    if (start < 0 || end < 0)
		throw new IllegalArgumentException (SAELTZ);
	    return new Range (start, end);
	} else if (e.length () > 0) {
	    // no start so this many bytes from the end...
	    start = Integer.parseInt (e);
	    if (start < 0)
		throw new IllegalArgumentException (SLTZ);
	    start = size - start;
	    end = size;
	    return new Range (start, end);
	} else {
	    // "-"
	    throw new NumberFormatException (FR);
	}
    }

    private long getTotalSize (Connection.RequestHandler rh) {
	String cr = rh.webHeader.getHeader ("Content-Range");
	if (cr != null) {
	    int i = cr.lastIndexOf ('/');
	    if (i != -1) {
		return Long.parseLong (cr.substring (i + 1));
	    }
	}
	String cl = rh.webHeader.getHeader ("Content-Length");
	if (cl != null)
	    return Long.parseLong (cl);
	// ok fallback...
	return rh.entry.getSize ();
    }
    
    private boolean haveAllRanges (List<Range> ranges, 
				   Connection.RequestHandler rh, 
				   long totalSize) {
	// do we have all of it?
	if (rh.entry.getSize () == totalSize)
	    return true;
	
	// check each range...
	// TODO add support for many parts...
	//Content-Range: bytes 0-4/25\r\n
	String cr = rh.webHeader.getHeader ("Content-Range");
	if (cr == null)   // TODO check if its ok to return true here.. 
	    // if we do not have a content range we ought to have full resource.
	    return false; 
	
	for (int i = 0; i < ranges.size (); i++) {
	    Range r = ranges.get (i);
	    long start = r.getStart ();
	    long end = r.getEnd ();
	    String t = "bytes " + start + "-" + end + "/" + totalSize;
	    if (!t.equals (cr)) {
		ContentRangeParser crp = 
		    new ContentRangeParser (cr, getLogger ());
		if (crp.isValid ()) {
		    if (crp.getStart () > start || crp.getEnd () < end)
			return false;
		}
	    }
	}
	return true;
    }
}
