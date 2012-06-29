package rabbit.cache;

/** A key to use when searching the cache.
 *
 *  This class only exists to trick equals/hashCode that we
 *  have the same key. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class MemoryKey<T> extends FiledKey<T> {
    private T data;

    public MemoryKey (T data) {
	this.data = data;
	hashCode = data.hashCode ();
    }

    public T getData () {
	return data;
    }
}
