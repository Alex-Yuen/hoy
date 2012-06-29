package rabbit.http;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import rabbit.util.StringCache;

/** This class holds a header value, that is a &quot;type: some text&quot;
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class Header implements Externalizable {
    private String type;
    private String value;

    /** The String consisting of \r and \n */
    public static final String CRLF = "\r\n";

    /** The string cache we are using. */
    private static StringCache stringCache = StringCache.getSharedInstance ();

    private static String getCachedString (String s) {
	return stringCache.getCachedString (s);
    }

    /** Used for externalization */
    public Header () {}
	
    public Header (String type, String value) {
	this.type = getCachedString (type);
	this.value = getCachedString (value);
    }

    public String getType () {
	return type;
    }

    public String getValue () {
	return value;
    }

    public void setValue (String newValue) {
	value = newValue;
    }

    public boolean equals (Object o) {
	if (o instanceof Header) {
	    return (((Header)o).type.equalsIgnoreCase (type));
	}
	return false;
    }

    public int hashCode() {
	return type.hashCode ();
    }

    public void append (String s) {
	value += CRLF + s;
	value = getCachedString (value);
    }

    public void writeExternal (ObjectOutput out) throws IOException {
	out.writeObject (type);
	out.writeObject (value);
    }
	
    public void readExternal (ObjectInput in) 
	throws IOException, ClassNotFoundException {
	type = getCachedString ((String)in.readObject ());
	value = getCachedString ((String)in.readObject ());
    }
}
