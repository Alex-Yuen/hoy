package rabbit.proxy;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** A DNS handler using the standard java packages.
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class DNSSunHandler implements DNSHandler {
    /** Do any neccessary setup. */
    public void setup (SProperties config, Logger logger) {
	// empty.
    }

    /** Look up an internet address. */
    public InetAddress getInetAddress (URL url) throws UnknownHostException {
	return InetAddress.getByName (url.getHost ());
    }

    public InetAddress getInetAddress (String host) throws UnknownHostException {
	return InetAddress.getByName (host);
    }
}
