package ws.hoyland.sm.service;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import ws.hoyland.sm.Engine;

public class ProxyService implements ProxyServiceMBean {
	private String proxies;
	private List<Integer> clients = new ArrayList<Integer>();

	public String getProxies() {
		//this.proxies = "127.0.0.1:8080\r\n"; 
		return this.proxies;
	}

	public void setProxies(String proxies) {
		System.err.println("Setting proxies...");
		this.proxies = proxies;
		for(;;){
			//通知所有注册的JMX客户端
		}		
	}

	@Override
	public void register(int port) {
		clients.add(port);
	}
	
	@Override
	public void unRegister(int port) {
		clients.remove(port);
	}
	
	@Override
	public void notifyReload() {
		for(Integer port : clients){
			try{
				JMXServiceURL url = new JMXServiceURL(
						"service:jmx:rmi:///jndi/rmi://localhost:"+port+"/service");
				JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
				
				MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
				//String domain = mbsc.getDefaultDomain();
				
				ObjectName objectName = new ObjectName("ws.hoyland.sm.service:name=ProxyService");
				ProxyServiceMBean service = (ProxyServiceMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, ProxyServiceMBean.class, true);
				service.doReload();
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
	}

	@Override
	public void doReload() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				Engine.getInstance().reloadProxies();												
			}
		}).start();		
	}	
}
