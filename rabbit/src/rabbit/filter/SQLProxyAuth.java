package rabbit.filter;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.proxy.HttpGenerator;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** This is a filter that requires users to use 
 *  proxy-authentication, using users in a sql table.
 *
 *  Will read the following parameters from the config file: 
 *  <ul>
 *  <li>driver 
 *  <li>url
 *  <li>user
 *  <li>password
 *  <li>select
 *  </ul>
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SQLProxyAuth implements HttpFilter {
    private String url = null;
    private String dbuser = null;
    private String dbpwd = null;
    private String select = null;
    private String DEFAULT_SELECT = 
    "select password from users where username = ?";

    private java.sql.Connection db = null;

    public SQLProxyAuth (){
    }

    private synchronized void initConnection () throws SQLException {
	db = DriverManager.getConnection (url, dbuser, dbpwd);
    }

    /** test if a socket/header combination is valid or return a new HttpHeader.
     *  Check that the user has been authenticate..
     * 
     *  Check if we want to cache user info...
     * 
     * @param socket the SocketChannel that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return null if everything is fine or a HttpHeader 
     *         describing the error (like a 403).
     */
    public HttpHeader doHttpInFiltering (SocketChannel socket, 
					 HttpHeader header, Connection con) {
	if (con.getMeta ())
	    return null;
	String username = con.getUserName ();
	String pwd = con.getPassword ();
	if (username == null || pwd == null) 
	    return getError (con, header);
	String rpwd = null;
	try { 
	    rpwd = getBackendPassword (con.getProxy ().getLogger (), username);
	} catch (SQLException e) {
	    Logger log = con.getProxy ().getLogger ();
	    log.logWarn ("Exception when trying to get user: " + e);
	    closeDB (con);
	}
	if (rpwd == null || !rpwd.equals (pwd))
	    return getError (con, header);
	return null;
    }

    /** test if a socket/header combination is valid or return a new HttpHeader.
     * @param socket the Socket that made the request.
     * @param header the actual request made.
     * @param con the Connection handling the request.
     * @return This method always returns null.
     */
    public HttpHeader doHttpOutFiltering (SocketChannel socket, 
					  HttpHeader header, Connection con) {
	return null;
    }

    private HttpHeader getError (Connection con, HttpHeader header) {
	HttpGenerator hg = con.getHttpGenerator ();
	try {
	    return hg.get407 ("internet", new URL (header.getRequestURI ()));
	} catch (MalformedURLException e) {
	    con.getProxy ().getLogger ().logWarn ("Bad url: " + e);
	    return hg.get407 ("internet", null);
	}
    }

    private synchronized void closeDB (Connection con) {
	if (db == null)
	    return;
	try {
	    db.close ();
	} catch (SQLException e) {
	    Logger log = con.getProxy ().getLogger ();
	    log.logWarn ("failed to close database: " + e);
	}
	db = null;
    } 

    private String getBackendPassword (Logger logger, String username) 
	throws SQLException {
	if (db == null)
	    initConnection ();
	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
	    ps = db.prepareStatement (select);
	    ps.setString (1, username);
	    rs = ps.executeQuery ();
	    if (rs.next ()) {
		return rs.getString (1);		
	    }
	    return null;
	} finally {
	    if (rs != null) {
		try {
		    rs.close ();
		} catch (SQLException e) {
		    logger.logError ("failed to close resultset: " + e);
		}
	    }
	    if (ps != null) {
		try {
		    ps.close ();
		} catch (SQLException e) {
		    logger.logError ("failed to close statement: " + e);
		}		
	    }
	} 
    }

    /** Setup this class with the given properties.
     * @param logger the Logger to output errors/warnings on.
     * @param properties the new configuration of this class.
     */
    public void setup (Logger logger, SProperties properties) {
	String driver = properties.getProperty ("driver");
	try {
	    Class.forName (driver);
	} catch (ClassNotFoundException e) {
	    logger.logError ("Failed to load driver");
	}
	url = properties.getProperty ("url");
	dbuser = properties.getProperty ("user");
	dbpwd = properties.getProperty ("password");
	select = properties.getProperty ("select", DEFAULT_SELECT);
    }    
}
