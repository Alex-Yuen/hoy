package rabbit.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import rabbit.filter.HtmlFilter;
import rabbit.filter.HtmlFilterFactory;
import rabbit.html.HtmlBlock;
import rabbit.html.HtmlParseException;
import rabbit.html.HtmlParser;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.proxy.TrafficLoggerHandler;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** This handler filters out unwanted html features.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class FilterHandler extends GZipHandler {
    private SProperties config = new SProperties ();
    private List<HtmlFilterFactory> filterClasses = 
    new ArrayList<HtmlFilterFactory> ();

    private List<HtmlFilter> filters; 
    private HtmlParser parser;
    private HtmlBlock block = null;

    // For creating the factory.
    public FilterHandler () {	
    }

    /** Create a new FilterHandler for the given request.
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
    public FilterHandler (Connection con, TrafficLoggerHandler tlh, 
			  HttpHeader request, ByteBuffer clientBuffer,
			  HttpHeader response, ResourceSource content, 
			  boolean mayCache, boolean mayFilter, long size, 
			  boolean compress, 
			  List<HtmlFilterFactory> filterClasses) {
	super (con, tlh, request, clientBuffer, response, content, 
	       mayCache, mayFilter, size, compress);
	if (this.mayFilter) {
	    this.filterClasses = filterClasses;
	    response.removeHeader ("Content-Length");
	    /* Not sure why we would need this, used to be in rabbit/2.x
	    if (!con.getChunking ())
		con.setKeepalive (false);
	    */
	    parser = new HtmlParser ();
	    filters = initFilters ();
	}
    }

    @Override
    public Handler getNewInstance (Connection con, TrafficLoggerHandler tlh,
				   HttpHeader header, ByteBuffer buffer, 
				   HttpHeader webHeader, 
				   ResourceSource content, boolean mayCache, 
				   boolean mayFilter, long size) {
	return new FilterHandler (con, tlh, header, buffer, webHeader, content,
				  mayCache, mayFilter, size, compress, 
				  filterClasses);
    }
    
    @Override
    protected void writeDataToGZipper (byte[] arr) 
	throws IOException {	
	handleArray (arr, gz);
    }

    @Override
    protected void modifyBuffer (ByteBuffer buf) {
	if (!mayFilter) {
	    super.modifyBuffer (buf);
	    return;
	}
	byte[] arr = new byte[buf.remaining ()];
	buf.get (arr);
	try {
	    handleArray (arr, new Stream2Channel ());
	} catch (IOException e) {
	    failed (e);	    
	}
    }

    private void handleArray (byte[] arr, OutputStream out) 	
	throws IOException {
	if (block != null) {
	    int rs = block.restSize ();
	    byte[] buf = new byte[arr.length + rs];
	    block.insertRest (buf);
	    System.arraycopy (arr, 0, buf, rs, arr.length);
	    arr = buf;
	}
	parser.setText (arr);
	HtmlBlock currentBlock = null;
	try {
	    currentBlock = parser.parse ();
	    for (HtmlFilter hf : filters)
		hf.filterHtml (currentBlock);
	    currentBlock.send (out);
	} catch (HtmlParseException e) {
	    getLogger ().logInfo ("Bad HTML: " + e.toString ());
	    out.write (arr);
	    currentBlock = null;
	}
	if (currentBlock != null && currentBlock.restSize () > 0)
	    block = currentBlock;
	else 
	    block = null;
    }

    @Override
    protected void finishData () throws IOException {
	if (block != null) {
	    if (gz != null)
		block.sendRest (gz);
	    else 
		block.sendRest (new Stream2Channel ());
	}
	super.finishData ();
    }

    /** Initialize the filter we are using.
     * @return a List of HtmlFilters.
     */
    private List<HtmlFilter> initFilters () {
	int fsize = filterClasses.size ();
	List<HtmlFilter> fl = new ArrayList<HtmlFilter> (fsize);
	Class<HtmlFilter> cls = null;
	
	for (int i = 0; i < fsize; i++) {
	    HtmlFilterFactory hff = filterClasses.get (i);
	    fl.add (hff.newFilter (con, request, response));
	}
	return fl;
    }

    /** Setup this class.
     * @param prop the properties of this class.
     */
    @Override public void setup (Logger logger, SProperties prop) {
	super.setup (logger, prop);
	config = prop;
	String fs = config.getProperty ("filters", "");
	filterClasses = new ArrayList<HtmlFilterFactory> ();
	String[] names = fs.split (",");
	for (String classname : names) {
	    try {
		Class<? extends HtmlFilterFactory> cls = 
		    Class.forName (classname).
		    asSubclass (HtmlFilterFactory.class);
		filterClasses.add (cls.newInstance ());
	    } catch (ClassNotFoundException e) {
		logger.logWarn ("Could not find filter: '" + classname + "'");
	    } catch (InstantiationException e) {
		logger.logWarn ("Could not instanciate class: '" + 
				classname + "' " + e);
	    } catch (IllegalAccessException e) {
		logger.logWarn ("Could not get constructor for: '" + 
				classname + "' " + e);
	    }
	}
    }
}
