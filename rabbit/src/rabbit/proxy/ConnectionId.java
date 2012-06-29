package rabbit.proxy;

/** The id for a connection. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class ConnectionId {
    private int group;
    private long id;
    
    public ConnectionId (int group, long id) {
	this.group = group;
	this.id = id;
    }

    public String toString () {
	StringBuilder sb = new StringBuilder ();
	sb.append ("[").append (group).append (",").append (id).append ("]");
	return sb.toString ();
    }
}
