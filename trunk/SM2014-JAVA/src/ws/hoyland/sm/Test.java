package ws.hoyland.sm;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import ws.hoyland.sm.service.ProxyServiceMBean;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://localhost:8023/service");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			//String domain = mbsc.getDefaultDomain();
			
			ObjectName objectName = new ObjectName("ws.hoyland.sm.service:name=ProxyService");
			ProxyServiceMBean service = (ProxyServiceMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, ProxyServiceMBean.class, true);
			System.out.println(service.getProxies());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
