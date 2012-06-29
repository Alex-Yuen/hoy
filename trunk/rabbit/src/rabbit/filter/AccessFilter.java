package rabbit.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import rabbit.util.IPAccess;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** This is a class that filters access based on ip adress.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class AccessFilter implements IPAccessFilter {
    private String accessfile;
    private List<IPAccess> iplist = new ArrayList<IPAccess> ();
    private static final String DEFAULTCONFIG = "conf/access";

    /** Filter based on a socket.
     * @param s the SocketChannel to check.
     * @return true if the Socket should be allowed, false otherwise.
     */
    public boolean doIPFiltering (SocketChannel s) {
	for (IPAccess ipa : iplist)
	    if (ipa.inrange (s.socket ().getInetAddress ()))
		return true;
	return false;
    }
    
    /** Setup this class.
     * @param properties the Properties to get the settings from.
     */
    public void setup (Logger logger, SProperties properties) {
	String file = properties.getProperty ("accessfile", DEFAULTCONFIG);
	loadAccess (logger, file);
    }
     
    /** Read the data (accesslists) from a file.
     * @param filename the name of the file to read from.
     */
    private void loadAccess (Logger logger, String filename) {
	filename = filename.replace ('/', File.separatorChar);
	accessfile = filename;
	
	FileReader fr = null;
	try {
	    fr = new FileReader (accessfile);
	    loadAccess (logger, fr);	    
	} catch (IOException e) {
	    logger.logFatal ("Accessfile '" + accessfile + 
			     "' not found: no one allowed: " + e);
	} finally {
	    if (fr != null) {
		try {
		    fr.close ();
		} catch (IOException e) {
		    logger.logFatal ("failed to close accessfile: " + e);
		}
	    }
	}
    }
    
    /** Loads in the accessess allowed from the given Reader
     * @param r the Reader were data is available
     */
    public void loadAccess (Logger logger, Reader r) throws IOException {
	List<IPAccess> iplist = new ArrayList<IPAccess> ();
	LineNumberReader br = new LineNumberReader (r);
	String line = null;
	while ((line = br.readLine ()) != null) {
	    // remove comments....
	    int index = line.indexOf ('#');
	    if (index >= 0) 
		line = line.substring (0,index);
	    if (line.equals (""))
		continue;
	    StringTokenizer st = new StringTokenizer (line);
	    if (st.countTokens () != 2) {
		logger.logWarn ("Bad line in accessconf:" + br.getLineNumber());
		continue;
	    }
	    String low = st.nextToken ();
	    InetAddress lowip = null;
	    try {
		lowip = InetAddress.getByName (low);	    
	    } catch (UnknownHostException e) {
		logger.logWarn ("Bad host: " + low + " at line:" + 
				br.getLineNumber());
		continue;	    
	    }
	    
	    String high = st.nextToken ();
	    InetAddress highip = null;
	    try {
		highip = InetAddress.getByName (high);
	    } catch (UnknownHostException e) {
		logger.logWarn ("Bad host: " + high + " at line:" + 
				br.getLineNumber());
		continue;	    
	    }
	    
	    if (lowip != null && highip != null) 
		iplist.add (new IPAccess (lowip, highip));
	}
	br.close ();
	this.iplist = iplist;
    }

    /** Saves the accesslist from the given Reader.
     * @param r the Reader with the users.
     */
    public void saveAccess (Logger logger, Reader r) throws IOException {
	if (accessfile == null) 
	    return;
	BufferedReader br = new BufferedReader (r);
	PrintWriter fw = new PrintWriter (new FileWriter (accessfile));
	String line;
	while ((line = br.readLine ()) != null)
	    fw.println (line);
	fw.flush ();
	fw.close ();
	br.close ();
    }

    /** Get the list of ips allowed
     */
    public List<IPAccess> getIPList () {
	return iplist;
    }
}
