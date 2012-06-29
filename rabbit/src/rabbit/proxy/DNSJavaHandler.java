package rabbit.proxy;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import org.xbill.DNS.Address;
import org.xbill.DNS.Cache;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** A DNS handler using the dnsjava packages
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class DNSJavaHandler implements DNSHandler {
    /** Do any neccessary setup. */
    public void setup (SProperties config, Logger logger) {
	String ct = config.getProperty ("dnscachetime", "8").trim ();
	int time = 8 * 3600; 
	try {
	    time = Integer.parseInt (ct) * 3600;
	} catch (NumberFormatException e) {
	    logger.logWarn ("bad number for dnscachetime: '" + ct + 
			    "', using: " + (time / 3600) + " hours");
	}
	Cache dnsCache = Lookup.getDefaultCache (DClass.IN);
	dnsCache.setMaxCache (time);
	dnsCache.setMaxNCache (time);
    }

    /** Look up an internet address. */
    public InetAddress getInetAddress (URL url) throws UnknownHostException {
	return Address.getByName (url.getHost ());	
    }

    public InetAddress getInetAddress (String host) throws UnknownHostException {
	return Address.getByName (host);	
    }
}
