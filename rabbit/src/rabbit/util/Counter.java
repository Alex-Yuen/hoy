package rabbit.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

/** This class counts different messages
 */
public class Counter 
{
    // All the messages we count.
    private Map<String, Count> counters = 
	new ConcurrentHashMap<String, Count> ();
    
    /** This class holds one messages counts
     */
    static class Count {
	private int counter = 0;
	
	/** Create a new Count
	 */
	Count () {
	}
	
	/** Increase its value by one
	 */
	void inc () {
	    counter++;
	}
	
	/** Get the count for this message
	 * @return the number of times this message has been counted.
	 */
	public int count () {
	    return counter;
	}
    }

    /** Increase a logentry.
     * @param log the event to increase 
     */
    public void inc (String log) {
	Count l = counters.get(log);
	if (l == null) {
	    l = new Count ();
	    counters.put (log,l);
	}
	l.inc ();	
    }
    
    /** Get all events
     * @return an Set of all events
     */
    public Set<String> keys () {
	return counters.keySet ();
    }

    /** Get the current count for an event.
     * @param key the event were intrested in
     * @return the current count of event.
     */
    public int get (String key) {
	Count l = counters.get (key);
	if (l == null)
	    return 0;
	else 
	    return l.count ();
    }
}

