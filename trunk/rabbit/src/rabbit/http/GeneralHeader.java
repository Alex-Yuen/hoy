package rabbit.http;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.Externalizable;
import java.util.ArrayList;
import java.util.List;

/** A class to handle general headers.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class GeneralHeader implements Externalizable {    

    /** The headers of this Header in order.
     */
    protected List<Header> headers = new ArrayList<Header> ();

    /** Create a new HTTPHeader from scratch
     */
    public GeneralHeader () {    
    }

    public int size () {
	return headers.size ();
    }

    /** Get the text value of this header 
     * @return a String describing this GeneralHeader.
     */
    public String toString () {
	StringBuilder ret = new StringBuilder ();
	int hsize = headers.size ();
	for (int i = 0; i < hsize; i++) {
	    Header h = headers.get (i);
	    ret.append (h.getType ());
	    ret.append (": ");
	    ret.append (h.getValue ());
	    ret.append (Header.CRLF);
	}
	ret.append (Header.CRLF);
	return ret.toString ();
    }

    /** get the value of header type 
     * @param type the Header were intrested in.
     * @return the value of type or null if no value is set.
     */
    public String getHeader (String type) {
	int s = headers.size ();
	for (int i = 0; i < s; i++) {
	    Header h = headers.get (i);
	    if (h.getType ().equalsIgnoreCase (type))
		return h.getValue ();
	}
	return null;
    }
    /** Set or replaces a value for given type.
     * @param type the type or category that we want to set.
     * @param value the value we want to set
     */
    public void setHeader (String type, String value) {
	int s = headers.size ();
	for (int i = 0; i < s; i++) {
	    Header h = headers.get (i);
	    if (h.getType ().equalsIgnoreCase (type)) {
		h.setValue (value);
		return;
	    }	    
	}
	Header h = new Header (type, value);
	headers.add (h);
    }

    /** Set a specified header 
     * @param current the type or category that we want to set.
     * @param newValue the value we want to set
     */
    public void setExistingValue (String current, String newValue) {
	int s = headers.size ();
	for (int i = 0; i < s; i++) {
	    Header h = headers.get (i);
	    if (h.getValue ().equals (current)) {
		h.setValue (newValue);
		return;
	    }	    
	}
    }

    /** Add a new header. Old headers of the same type remain. 
     *  The new header is placed last.
     * @param type the type or category that we want to set.
     * @param value the value we want to set
     */
    public void addHeader (String type, String value) {
	Header h = new Header (type, value);
	addHeader (h);	
    }

    /** Add a new header. Old headers of the same type remain. 
     *  The new header is placed last.
     * @param h the Header to add
     */
    public void addHeader (Header h) {
	headers.add (h);	
    }

    /** removes a headerline from this header
     * @param type the type we want to remove
     */
    public void removeHeader (String type) {
	int s = headers.size ();
	for (int i = 0; i < s; i++) {
	    Header h = headers.get (i);
	    if (h.getType ().equalsIgnoreCase (type)) {
		headers.remove (i);
		i--;
		s--;
	    }
	}
    }

    /** removes a header with the specified value 
     * @param value the value of the header we want to remove
     */
    public void removeValue (String value) {
	int s = headers.size ();
	for (int i = 0; i < s; i++) {
	    Header h = headers.get (i);
	    if (h.getValue ().equals (value)) {
		headers.remove (i);
		return;
	    }
	}
    }

    /** Get all headers of a specified type...
     * @param type the type of the headers to get, eg. "Cache-Control".
     */
    public List<String> getHeaders (String type) {
	List<String> ret = new ArrayList<String> ();
	int s = headers.size ();
	for (int i = 0; i < s; i++) {
	    Header h = headers.get (i);
	    if (h.getType ().equalsIgnoreCase (type)) {
		ret.add (h.getValue ());
	    }
	}
	return ret;
    }

    /** Copy all headers in this header to the given header. 
     * @param to the GeneralHeader to add headers to.
     */
    public void copyHeader (GeneralHeader to) {
	for (int i = 0; i < headers.size (); i++) {
	    Header h = headers.get (i);
	    to.addHeader (h.getType (), h.getValue ());
	}
    }
    
    public void readExternal (ObjectInput in) 
	throws IOException, ClassNotFoundException {
	List objects = (List)in.readObject ();
	for (Object o : objects)
	    headers.add ((Header)o);
    }

    public void writeExternal (ObjectOutput out) throws IOException { 
	out.writeObject (headers);
    }
}
    
