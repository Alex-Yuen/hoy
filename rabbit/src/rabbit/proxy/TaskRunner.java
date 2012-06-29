package rabbit.proxy;

/** A handler that can run task in off-main threads.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface TaskRunner {
    
    /** Run a task in another thread. 
     *  The task will be run sometime in the future.
     * @param r the task to run.    
     */
    void runThreadTask (Runnable r);

    /** Run a task on the main thread.
     *  The task will be run sometime in the future.
     * @param r the task to run on the main thread.
     */ 
    void runMainTask (Runnable r);
}
