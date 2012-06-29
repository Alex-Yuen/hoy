package rabbit.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** A class to store the cache entrys data hook on file. 
 *  A Http Header is a big thing so it is nice to write it to disk. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class FiledHook<T> implements Serializable {
    private static final long serialVersionUID = 20050430;

    public FiledHook () {
    }

    protected String getExtension () {
	return ".hook";
    }

    /** Get the hooked data. 
     */
    public <K, V> T getData (Cache<K, V> cache, CacheEntry<K, V> entry) {
	return readHook (cache, entry);
    }

    /** Read the hooked data. 
     */
    protected <K, V> T readHook (Cache<K, V> cache, CacheEntry<K, V> entry) {
	String name = cache.getEntryName (entry) + getExtension ();
	return readHook (name);
    }

    /** Read the hooked data. 
     */
    @SuppressWarnings( "unchecked" )
    protected T readHook (String name) {
	ObjectInputStream is = null;
	try {
	    File f = new File (name);
	    if (!f.exists())
		return null;
	    FileInputStream fis = new FileInputStream (f);
	    BufferedInputStream bis = new BufferedInputStream (fis);
	    is = new ObjectInputStream (new GZIPInputStream (bis));
	    T hook = (T)is.readObject ();
	    return hook;
	} catch (ClassNotFoundException e) {
	    e.printStackTrace ();
	} catch (IOException e) {
	    e.printStackTrace ();
	} finally {
	    if (is != null) {
		try {
		    is.close ();
		} catch (IOException e) {
		    e.printStackTrace ();
		}
	    }
	}
	return null;
    }

    /** Set the hooked data. 
     */
    protected <K, V> void storeHook (Cache<K, V> cache, 
				     CacheEntry<K, V> entry, T hook) {
	ObjectOutputStream os = null;
	try {
	    String name = cache.getEntryName (entry) + getExtension ();
	    FileOutputStream fos = new FileOutputStream (name);
	    BufferedOutputStream bos = new BufferedOutputStream (fos);
	    os = new ObjectOutputStream (new GZIPOutputStream (bos));
	    os.writeObject (hook);
	    os.flush ();
	} catch (IOException e) {
	    e.printStackTrace ();
	} finally {
	    if (os != null) {
		try {
		    os.close ();
		} catch (IOException e) {
		    e.printStackTrace ();
		}
	    }
	}
    }
}
