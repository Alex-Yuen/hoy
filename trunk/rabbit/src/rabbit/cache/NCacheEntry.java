package rabbit.cache;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/** 
 *  A cached object.
 */
class NCacheEntry<K, V> implements Externalizable, CacheEntry<K, V> {
    private static final long serialVersionUID = 20050430;

    /** @serial The key for the object usually a URL or a filename.*/
    private FiledKey<K> key = null;
    /** @serial The date this entry was cached. */
    private long cachetime = -1;
    /** @serial The date this entry expires.*/
    private long expires = Long.MAX_VALUE;
    /** @serial The number of bytes this object is.*/
    private long size = 0;
    /** @serial The unique id of the object.*/
    private long id = 0;
    /** @serial The hooked data of the cached object. */
    private FiledHook<V> datahook;

    /** Not to be used, for externalizable only. */
    public NCacheEntry () {
    }

    /** Create a new CacheEntry for given key and filename
     * @param key the key for the object.
     * @param id the identity of this entry
     */
    public NCacheEntry (K key, long id) {
	this.key = new MemoryKey<K> (key);
	this.id = id;
    }    
    
    /** Set the key were holding data for
     * @param key the key we have data for
     */
    void setKey (FiledKey<K> key) {
	this.key = key;
    }
    
    /** Get the key were holding data for
     * @return the keyobject
     */
    public K getKey () {
	return key.getData ();
    }   
    
    /** Get the date this object was cached.
     * @return a date.
     */
    public long getCacheTime () {
	return cachetime;
    }

    /** Set the date this object was cached.
     * @param date the date.
     */
    public void setCacheTime (long date) {
	cachetime = date;
    }

    /** Get the size of our file
     * @return the size of our data
     */
    public long getSize () {
	return size;
    }  

    /** Sets the size of our data
     * @param size the new Size
     */
    public void setSize (long size) {
	this.size = size;
    }

    /** Get the expiry-date of our file
     * @return the expiry date of our data
     */
    public long getExpires () {
	return expires;
    }  

    /** Sets the expirydate of our data
     * @param d the new expiry-date.
     */
    public void setExpires (long d) {
	this.expires = d;
    }
    
    /** Get the id of our entry.
     * @return the id of the entry.
     */
    public long getId () {
	return id;
    }

    /** Get the real data hook
     */
    protected FiledHook<V> getRealDataHook () {
	return datahook;
    }

    /** Get the hooked data.
     * @param cache the NCache this entry lives in. 
     * @return the the hooked data.
     */
    public V getDataHook (Cache<K, V> cache) {
	return datahook.getData (cache, this);
    }

    /** Set the hooked data.
     * @param o the new filed hook
     */
    void setFiledDataHook (FiledHook<V> o) {
	this.datahook = o;
    }

    /** Sets the data hook for this cache object.
     *  Since it is not always possible to make the key hold this...
     * @param o the new data.
     */
    public void setDataHook (V o) {
	this.datahook = new MemoryHook<V> (o);
    }

    /** Read the cache entry from the object input.
     */
    @SuppressWarnings( "unchecked" )    
    public void readExternal (ObjectInput in) 
	throws IOException, ClassNotFoundException {
	key = (FiledKey<K>)in.readObject ();
	cachetime = in.readLong ();
	expires = in.readLong ();
	size = in.readLong ();
	id = in.readLong ();
	datahook = (FiledHook<V>)in.readObject ();
    }
    
    /** Write the object to the object output.
     */
    public void writeExternal (ObjectOutput out) throws IOException {
	out.writeObject (key);
	out.writeLong (cachetime);
	out.writeLong (expires);
	out.writeLong (size);
	out.writeLong (id);
	out.writeObject (datahook);
    }
}
