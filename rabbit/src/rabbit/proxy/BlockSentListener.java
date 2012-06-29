package rabbit.proxy;

/** A listener for notification that a data block has been sent.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface BlockSentListener extends AsyncListener {
    /** The http header has been sent. */
    void blockSent ();
}
