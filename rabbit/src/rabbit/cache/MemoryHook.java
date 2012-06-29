package rabbit.cache;

/** A key to use when searching the cache.
 *
 *  This class only exists to trick equals/hashCode that we
 *  have the same key. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class MemoryHook<T> extends FiledHook<T> {
    private T data;

    public MemoryHook (T data) {
	this.data = data;
    }

    public <K, V> T getData (Cache<K, V> cache, CacheEntry<K, V> entry) {
	return data;
    }
}
