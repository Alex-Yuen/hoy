package rabbit.meta;

import java.io.IOException;
import java.nio.ByteBuffer;
import rabbit.http.HttpHeader;
import rabbit.proxy.BlockSender;
import rabbit.proxy.BlockSentListener;
import rabbit.proxy.ChunkEnder;
import rabbit.proxy.Connection;
import rabbit.proxy.HtmlPage;
import rabbit.util.SProperties;
import rabbit.util.TrafficLogger;

/** A base class for meta handlers.
 *
 *  This meta handler will send a http header that say that the content is
 *  chunked. Then 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public abstract class BaseMetaHandler 
    implements MetaHandler, BlockSentListener {
    protected HttpHeader request;
    protected SProperties htab;
    protected Connection con;
    protected TrafficLogger tlProxy;
    protected TrafficLogger tlClient;
    private boolean first = true;
    
    private static enum Mode { SEND_HEADER, SEND_DATA, CLEANUP };
    private Mode mode = Mode.SEND_HEADER;

    public static enum PageCompletion { PAGE_NOT_DONE, PAGE_DONE };
    
    public void handle (HttpHeader request, 
			SProperties htab, 
			Connection con, 
			TrafficLogger tlProxy, 
			TrafficLogger tlClient) throws IOException {
	this.request = request;
	this.htab = htab;
	this.con = con;
	this.tlProxy = tlProxy;
	this.tlClient = tlClient;
	HttpHeader response = con.getHttpGenerator ().getHeader ();
	response.setHeader ("Transfer-Encoding", "Chunked");
	byte[] b2 = response.toString ().getBytes ("ASCII");
	ByteBuffer buffer = ByteBuffer.wrap (b2);
	BlockSender bs = 
		new BlockSender (con.getChannel (), con.getSelector (), 
				 con.getLogger (), tlClient, buffer, 
				 false, this);
    }

    public void blockSent () {
	try {
	    switch (mode) {
	    case CLEANUP:
		cleanup ();
		break;
	    case SEND_DATA:
		endChunking ();
		break;
	    case SEND_HEADER: 
		buildAndSendData ();
		break;
	    default:
		failed (new RuntimeException ("Odd mode: " + mode));
	    }
	} catch (IOException e) {
	    failed (e);
	}
    }

    protected void cleanup () throws IOException {
	con.logAndRestart ();
    }

    protected void endChunking () throws IOException {
	mode = Mode.CLEANUP;
	ChunkEnder ce = new ChunkEnder ();
	ce.sendChunkEnding (con.getChannel (), con.getSelector (), 
			    con.getLogger (), tlClient, this);
    }

    protected void buildAndSendData () throws IOException {
	StringBuilder sb = new StringBuilder (2048);
	if (first) {
	    sb.append (HtmlPage.getPageHeader (con, getPageHeader ()));
	    first = false;
	}
	if (addPageInformation (sb) == PageCompletion.PAGE_DONE) {
	    sb.append ("\n</body></html>");
	    mode = Mode.SEND_DATA;
	}
	byte[] b1 = sb.toString ().getBytes ("ASCII");
	ByteBuffer data = ByteBuffer.wrap (b1);
	BlockSender bs = 
	    new BlockSender (con.getChannel (), con.getSelector (), 
			     con.getLogger (), tlClient, data, true, this);
    }
    
    /** Get the page header name
     */
    protected abstract String getPageHeader ();
  
    /** Add the page information 
     * @param sb The page being build.
     * @return the current status of the page.
     */
    protected abstract PageCompletion addPageInformation (StringBuilder sb);
  
    public void failed (Exception e) {
	con.getLogger ().logWarn ("Exception when handling meta: " + e);
	con.logAndClose (null);
    }
    
    public void timeout () {
	con.getLogger ().logWarn ("Timeout when handling meta.");
	con.logAndClose (null);	
    }
}
