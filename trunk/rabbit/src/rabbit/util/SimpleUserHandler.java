package rabbit.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/** This is a class that handles users authentication using a simple text file.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SimpleUserHandler {  // TODO: implements interface.
    private String userFile = null;
    private Map<String, String> users = new HashMap<String, String> ();

    public SimpleUserHandler () {
    }

    /** Set the file to use for users, will read the files. 
     *  Will discard any previous loaded users.
     * @param userFile the filename to read the users from.
     * @param logger the logger to write errors to.
     */
    public void setFile (String userFile, Logger logger) {
	this.userFile = userFile;

	FileReader fr = null;
	try {
	    fr = new FileReader (userFile);
	    users = loadUsers (fr);
	} catch (FileNotFoundException e) {
	    logger.logWarn ("could not load the users file: '" + userFile + "'. " + e);
	} catch (IOException e) {
	    logger.logWarn ("Error while loading the users file: '" + userFile + "'. " + e);	    
	} finally {
	    if (fr != null) {
		try {
		    fr.close ();
		} catch (IOException e) {
		    logger.logWarn ("Error when closing file: " + e);
		}
	    }
	}
    }

    /** Load the users from the given Reader.
     * @param r the Reader with the users.
     */
    public Map<String, String> loadUsers (Reader r) 
	throws IOException {
	BufferedReader br = new BufferedReader (r);
	String line;
	Map<String, String> u = new HashMap<String, String> ();
	while ((line = br.readLine ()) != null) {
	    String[] creds = line.split ("[: \n\t]");
	    if (creds.length != 2)
		continue;
	    String name = creds[0];
	    String pass = creds[1];
	    u.put (name, pass);
	}
	return u;
    }

    /** Saves the users from the given Reader.
     * @param r the Reader with the users.
     */
    public void saveUsers (Reader r) throws IOException {
	if (userFile == null) 
	    return;
	BufferedReader br = new BufferedReader (r);
	PrintWriter fw = new PrintWriter (new FileWriter (userFile));
	String line;
	while ((line = br.readLine ()) != null) 
	    fw.println (line);
	fw.flush ();
	fw.close ();
    }

    /** Return the hash of users.
     */
    public Map<String, String> getUsers () {
	return users;
    }

    /** Return the hash of users.
     */
    public void setUsers (Map<String, String> users) {
	this.users = users;
    }
    
    /** Check if a user/password combination is valid.
     * @param username the username.
     * @param password the decrypted password.
     * @return true if both username and password match a valid user.
     */
    public boolean isValidUser (String username, String password) {
	if (username == null)
	    return false;
	String pass = users.get (username);
	return (pass != null && password != null && pass.equals (password));	
    }
}
