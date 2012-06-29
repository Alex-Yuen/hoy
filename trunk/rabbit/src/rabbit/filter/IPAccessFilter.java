package rabbit.filter;

import java.io.IOException;
import java.io.Reader;
import java.nio.channels.SocketChannel;
import java.util.List;
import rabbit.util.IPAccess;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** This interface holds the method needed to do socket based access filtering.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */ 
public interface IPAccessFilter {

    /** Filter based on a socket.
     * @param s the Socket to check.
     * @return true if the Socket should be allowed, false otherwise.
     */
    boolean doIPFiltering (SocketChannel s);

    /** Setup this filter.
     * @param logger the Logger to use in case of errors/warnings.
     * @param properties the SProperties to get the settings from.
     */
    void setup (Logger logger, SProperties properties);

    /** Get the list of ips allowed
     */
    public List<IPAccess> getIPList ();

    /** Loads in the accessess allowed from the given Reader
     * @param r the Reader were data is available
     */
    public void loadAccess (Logger logger, Reader r) throws IOException;

    /** Saves the accesslist from the given Reader.
     * @param r the Reader with the users.
     */
    public void saveAccess (Logger logger, Reader r) throws IOException;
}
