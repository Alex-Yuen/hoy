package rabbit.proxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import rabbit.util.Level;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** A class to handle proxy logging. 
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ProxyLogger implements Logger, ConnectionLogger {

    /** The current config */
    private SProperties config;
    
    /** Output for errors */
    private LogWriter errorLog = new LogWriter (System.err, true);

    /** monitor for error log. */
    private Object errorMonitor = new Object ();
    
    /** Output for accesses */
    private LogWriter accessLog = new LogWriter (System.out, true);

    /** monitor for access log. */
    private Object accessMonitor = new Object ();

    /** The current error log level. */
    private Level logLevel = Level.MSG;

    /** The format we write dates on. */
    private SimpleDateFormat sdf = 
    new SimpleDateFormat ("dd/MMM/yyyy:HH:mm:ss 'GMT'");

    /** The monitor for sdf. */
    private Object sdfMonitor = new Object ();

    /** The distance to GMT in milis. */
    private long offset;    

    /** Create a new ProxyLogger. */
    public ProxyLogger () {
	TimeZone tz = sdf.getTimeZone ();
	GregorianCalendar gc = new GregorianCalendar ();
	gc.setTime (new Date ());
	offset = tz.getOffset (gc.get (Calendar.ERA),
			       gc.get (Calendar.YEAR),
			       gc.get (Calendar.MONTH),
			       gc.get (Calendar.DAY_OF_MONTH),
			       gc.get (Calendar.DAY_OF_WEEK),
			       gc.get (Calendar.MILLISECOND));	
    }

    /** Get the distance to GMT in millis 
     */
    public long getOffset () {
	return offset;
    }

    public void setup (SProperties config) {
	this.config = config;
	String loglvl = config.getProperty ("loglevel", "WARN");
	logLevel = getErrorLevel (loglvl);
	
	errorLog = setupLog (config, errorLog, errorMonitor, "errorlog", 
			     "logs/error_log", System.err);
	accessLog = setupLog (config, accessLog, accessMonitor, "accesslog", 
			      "logs/access_log", System.out);
    }
    
    /** Configure the error log.
     */
    private LogWriter setupLog (SProperties config, LogWriter currentLogger, 
				Object monitor, String entry, 
				String defaultLog, PrintStream defaultStream) {
	String log = config.getProperty (entry, defaultLog);
	synchronized (monitor) {
	    try {	    
		closeLog (currentLogger);
		if (!log.equals ("")) {
		    File f = new File (log);
		    File p = new File (f.getParent ());
		    if (!p.exists ())
			p.mkdirs ();
		    return new LogWriter (new FileWriter (log, true), true);
		} else {
		    return new LogWriter (defaultStream, true);
		}
	    } catch (IOException e) {
		logFatal ("Could not create log on '" + log +  "' exiting");
	    }
	    return new LogWriter (defaultStream, true);
	}
    }
    
    public void rotateLogs () {
	logMsg ("Log rotation requested.");
	Date d = new Date ();
	SimpleDateFormat lf = new SimpleDateFormat ("yyyy-MM-dd");
	String date = lf.format (d);	
	errorLog = rotateLog (config, errorLog, errorMonitor, "errorlog", 
			      "logs/error_log", System.err, date);
	accessLog = rotateLog (config, accessLog, accessMonitor, "accesslog", 
			       "logs/access_log", System.out, date);	
    }

    private LogWriter rotateLog (SProperties config, LogWriter w, 
				 Object monitor, String entry, 
				 String defaultLog, 
				 PrintStream defaultStream, String date) {
	synchronized (monitor) {
	    if (w != null && !w.isSystemWriter ()) {
		closeLog (w);
		String log = config.getProperty (entry, defaultLog);
		File f = new File (log);
		File fn = new File (log + "-" + date);	    
		if (f.renameTo (fn))
		    return setupLog (config, w, monitor, entry, 
				     defaultLog, defaultStream);
		logError ("failed to rotate error log!");
	    }
	    return w;
	}
    }

    /** Get the actual error level from the given String.
     * @param errorlevel the String to translate.
     * @return the errorlevel suitable for the given String.
     */
    private Level getErrorLevel (String errorlevel) {
	Level l = Enum.valueOf (Level.class, errorlevel.toUpperCase ());
	return l;
    }

    /** Flush and close the logfile given.
     * @param w the logfile to close.
     */
    private void closeLog (LogWriter w) {
	if (w != null && !w.isSystemWriter ()) {
	    w.flush ();
	    w.close ();
	}
    }
    
    /** Close down this logger. Will set the access and error logs to console.
     */
    public void close () {
	synchronized (accessMonitor) {
	    accessLog.flush ();
	    accessLog.close ();
	    accessLog = new LogWriter (System.out, true);
	}
	synchronized (errorMonitor) {
	    errorLog.flush ();
	    errorLog.close ();
	    errorLog = new LogWriter (System.err, true);
	}
    }

    public void logDebug (String error) {
	logError (Level.DEBUG, error);		
    }

    public void logAll (String error) {
	logError (Level.ALL, error);		
    }

    public void logInfo (String error) {
	logError (Level.INFO, error);		
    }

    public void logWarn (String error) {
	logError (Level.WARN, error);		
    }

    public void logMsg (String error) {
	logError (Level.MSG, error);		
    }

    public void logError (String error) {
	logError (Level.ERROR, error);	
    }

    public void logFatal (String error) {
	logError (Level.FATAL, error);
    }

    public void logError (Level level, String error) {
	if (logLevel.compareTo (level) > 0)
	    return;
	Date d = new Date ();
	d.setTime (d.getTime () - offset);
	StringBuilder sb = new StringBuilder ("[");
	synchronized (sdfMonitor) {
	    sb.append (sdf.format (d));
	}
	sb.append ("][");
	sb.append (level);
	sb.append ("][");
	sb.append (error);
	sb.append ("]");
	synchronized (errorMonitor) {
	    errorLog.println (sb.toString ());
	}	
    }

    public void logConnection (Connection con) {
	StringBuilder sb = new StringBuilder ();
	Socket s = con.getChannel ().socket (); 
	if (s != null) {
	    InetAddress ia = s.getInetAddress (); 
	    if (ia != null)
		sb.append (ia.getHostAddress());
	    else 
		sb.append ("????");
	}
	sb.append (" - ");
	sb.append ((con.getUserName () != null ? con.getUserName () : "-"));
	sb.append (" ");
	long now = System.currentTimeMillis ();
	Date d = new Date (now - offset);
	synchronized (sdfMonitor) {
	    sb.append (sdf.format (d));
	}
	sb.append (" \"");
	sb.append (con.getRequestLine ());
	sb.append ("\" ");
	sb.append (con.getStatusCode ());
	sb.append (" ");
	sb.append (con.getContentLength ());
	sb.append (" ");
	sb.append ((con.getExtraInfo () != null ? con.getExtraInfo () : ""));
	synchronized (accessMonitor) {
	    accessLog.println (sb.toString ());
	}
    }
}
