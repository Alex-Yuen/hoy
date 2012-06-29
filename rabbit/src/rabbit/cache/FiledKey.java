package rabbit.cache;

/** A class that stores cache keys in compressed form. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class FiledKey<T> extends FiledHook<T> {
    private static final long serialVersionUID = 20050430;

    protected int hashCode; // the hashCode for the contained object.
    private long id;
    protected transient Cache cache;    
    
    protected String getExtension () {
	return ".key";
    }

    protected <K, V> void setCache (Cache<K, V> cache) {
	this.cache = cache;
    }
    
    protected <K, V> void storeKey (Cache<K, V> cache, 
				    CacheEntry<K, V> entry, T key) {
	setCache (cache);
	hashCode = key.hashCode ();
	id = entry.getId ();
	storeHook (cache, entry, key);
    }

    private String getFileName () {
	return cache.getEntryName (id, true) + getExtension (); 
    }
    
    /** Get the hashCode for the contained key object. */
    public int hashCode () {
	return hashCode;
    }

    /** Check if the given object is equal to the contained key. */
    public boolean equals (Object data) {
	T myData = getData ();
	if (data != null && data instanceof FiledKey) {
	    data = ((FiledKey)data).getData ();
	}
	if (myData != null) {
	    return myData.equals (data);
	} else {
	    return data == null;
	}
    }
    
    /** Get the actual key object. */
    public T getData () {
	return readHook (getFileName ());
    }

    /** Get the unique id for this object. */
    public long getId () {
	return id;
    }

    public String toString () {
	return "FiledKey: " + hashCode + ", " + getFileName ();
    }
}
