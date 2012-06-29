package rabbit.http;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/** A header suitable for multi part handling.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class MultipartHeader extends GeneralHeader {
    private String header;

    /** Used for Externalizable, not to be used for other purposes. */
    MultipartHeader () {
    }
    
    public MultipartHeader (String header) {
	this.header = header;
    }
	
    public String toString () {
	StringBuilder ret = new StringBuilder (header);
	ret.append (Header.CRLF);
	ret.append (super.toString ());
	return ret.toString ();
    }

    public void writeExternal (ObjectOutput out) throws IOException {
	out.writeObject (header);
    }
	
    public void readExternal (ObjectInput in) throws IOException, ClassNotFoundException {
	header = (String)in.readObject ();
    }
}
