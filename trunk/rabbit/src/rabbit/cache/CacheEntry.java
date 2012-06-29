package rabbit.cache;

/** A cached object.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface CacheEntry<K, V> {

    /** Get the id of the entry.
     * @return the id of the entry.
     */
    public long getId ();

    /** Get the key were holding data for
     * @return the key object
     */
    K getKey ();

    /** Get the date this object was cached.
     * @return a date (millis since the epoch).
     */
    long getCacheTime ();

    /** Set the date this object was cached.
     * @param date the date.
     */
    void setCacheTime (long date);

    /** Get the size of our file
     * @return the size of our data
     */
    long getSize ();

    /** Sets the size of our data file
     * @param size the new Size
     */
    void setSize (long size);

    /** Get the expiry-date of our file
     * @return the expiry date of our data
     */
    long getExpires ();

    /** Sets the expirydate of our data
     * @param d the new expiry-date.
     */
    void setExpires (long d);	

    /** Get the hooked data.
     * @param cache the Cache this entry lives in. 
     * @return the the hooked data.
     */
    V getDataHook (Cache<K, V> cache);

    /** Sets the data hook for this cache object.
     *  Since it is not always possible to make the key hold this...
     * @param o the new data.
     */
    void setDataHook (V o);
}
