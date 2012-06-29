package rabbit.io;

/** A handler for a socket channel. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface SocketHandler extends Runnable {
    /** Signal that the select operation timed out. */
    void timeout ();

    /** Check if this handler needs to run in a separate thread. */
    boolean useSeparateThread ();
}
