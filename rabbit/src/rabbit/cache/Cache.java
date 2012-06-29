package rabbit.cache;

import java.net.URL;
import java.util.Collection;

/** A cache, mostly works like a map in lookup, insert and delete.
 *  A cache may be persistent over sessions. 
 *  A cache may clean itself over time.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface Cache<K, V> {

    /** Get the maximum size for this cache.
     * @return the maximum size in bytes this cache.
     */
    long getMaxSize ();

    /** Set the maximum size for this cache.
     * @param newMaxSize the new maximum size for the cache.
     */
    void setMaxSize (long newMaxSize);
    
    /** Get the number of miliseconds the cache stores things usually.
     *  This is the standard expiretime for objects, but you can set it for 
     *  CacheEntries individially if you want to.
     *  NOTE 1: dont trust that an object will be in the cache this long.
     *  NOTE 2: dont trust that an object will be removed from the cache when it expires.
     * @return the number of miliseconds objects are stored normally.
     */
    long getCacheTime ();

    /** Set the standard expiry-time for CacheEntries
     * @param newCacheTime the number of miliseconds to keep objects normally.
     */
    void setCacheTime (long newCacheTime);

    /** Get the current size of the cache
     * @return the current size of the cache in bytes.
     */
    long getCurrentSize ();

    /** Get the current number of entries in the cache.
     * @return the current number of entries in the cache.
     */
    long getNumberOfEntries ();

    /** Get the location where this cache stores its files.
     * @return the location, null if no physical location is used.
     */
    URL getCacheDir ();

    /** Get the CacheEntry assosiated with given object.
     * @param k the key.
     * @return the NCacheEntry or null (if not found).
     */ 
    CacheEntry<K, V> getEntry (K k);

    /** Get the file name for a real cache entry. 
     * @param ent the cache entry 
     */
    String getEntryName (CacheEntry<K, V> ent);

    /** Get the file name for a cache entry. 
     * @param id the id of the cache entry
     * @param real false if this is a temporary cache file, 
     *             true if it is a realized entry.
     */
    String getEntryName (long id, boolean real);
    
    /** Reserve space for a CacheEntry with key o.
     * @param k the key for the CacheEntry.
     * @return a new CacheEntry initialized for the cache.
     */
    CacheEntry<K, V> newEntry (K k);

    /** Insert a CacheEntry into the cache.
     * @param ent the CacheEntry to store.
     */
    void addEntry (CacheEntry<K, V> ent);

    /** Signal that a cache entry have changed.
     */
    void entryChanged (CacheEntry<K, V> ent, K newKey, V newValue);

    /** Remove the Entry with key o from the cache.
     * @param k the key for the CacheEntry.
     */
    void remove (K k);

    /** Clear the Cache from files. 
     */
    void clear ();

    /** Get the CacheEntries in the cache.
     * @return an Enumeration of the CacheEntries.
     */    
    Collection<CacheEntry<K, V>> getEntries ();

    /** Make sure that the cache is written to the disk.
     */
    void flush ();
    
    /** Stop this cache. 
     *  If this cache is using any cleaner threads they have 
     *  to be stopped when this method is called.
     */
    void stop ();
}
