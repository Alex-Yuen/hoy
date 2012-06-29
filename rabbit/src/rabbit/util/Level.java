package rabbit.util;

/** The logging levels. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public enum Level implements Comparable<Level> {
    /** Show all messages in the log. */
    DEBUG (0),
    /** Show all normal messages and higher. */
    ALL (5),
    /** Show information messages and higher. */
    INFO (10),
    /** Show warnings and higer. */
    WARN (15),
    /** Show important messages and above. */
    MSG (20),
    /** Show error messages and higer. */
    ERROR (25),
    /** Show only fatal messages. */
    FATAL (30);
    
    private final int level;

    private Level (int level) {
	this.level = level;
    }
}
