package rabbit.util;

/** A logger interface. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface Logger {
    /** Log a debug message.
     *  Same as <code>logError (DEBUG, error);</code>
     */
    void logDebug (String error);

    /** Log some information
     *  Same as <code>logError (ALL, error);</code>
     */
    void logAll (String error);

    /** Log some information.
     *  Same as <code>logError (Info, error);</code>
     */
    void logInfo (String error);

    /** Log a warning.
     *  Same as <code>logError (WARN, error);</code>
     */
    void logWarn (String error);

    /** Log a message.
     *  Same as <code>logError (MSG, error);</code>
     */
    void logMsg (String error);

    /** Log an error.
     *  Same as <code>logError (ERROR, error);</code>
     */
    void logError (String error);

    /** Log an fatal error.
     *  Same as <code>logError (FATAL, error);</code>
     */
    void logFatal (String error);

    /** Log an error of given type and with given message.
     * @param level the error level.
     * @param error the error message.
     */
    void logError (Level level, String error);   

    /** Rotate the current logs. 
     *  That is close the current log files and move them away, 
     *  then reopen the log files again.
     */
    void rotateLogs ();
}
