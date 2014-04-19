package ws.hoyland.sm;

import java.util.Stack;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import ws.hoyland.security.ClientDetecter;
import ws.hoyland.sm.service.ProxyServiceMBean;
import ws.hoyland.util.Converts;
import ws.hoyland.util.HoylandClassLoader;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println( System.currentTimeMillis() / 1000);
		
//		
//		String resp = "({						acctid : \"0\",				percent : +\"\",		status : 0,		errcode : 0,	errmsg : \"\"})";
////		if(resp.startsWith("(")){
////			resp = resp.substring(1, resp.length()-1);
////		}
////		System.out.println(resp);
//		
//		if(resp.startsWith("(")){
//			resp = resp.substring(1, resp.length()-1);
//		}
//		
//		try {
//			System.out.println(resp.substring(36));
//			JSONObject json = new JSONObject(resp);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		//System.out.println(Converts.hexStringToB64("1e395b2f6e5eb556c0d08c64f73be3ad7a08826fd2d0d55d839196ae142d0cb6b610d4fc4b5769d283a868f5f78a1068ee2946a753bc5462d6d41342dea265121c6d2e7d2f19bc9b688d2301a185da7855e19271a9d903da4deb275d4ed3e5f48c626b18eab7eb19d6ae7b3377663a2896b7c901fb41022dbc011d5076f8af1c"));
		
//		byte[] encrypted = Converts.hexStringToByte("1e395b2f6e5eb556c0d08c64f73be3ad7a08826fd2d0d55d839196ae142d0cb6b610d4fc4b5769d283a868f5f78a1068ee2946a753bc5462d6d41342dea265121c6d2e7d2f19bc9b688d2301a185da7855e19271a9d903da4deb275d4ed3e5f48c626b18eab7eb19d6ae7b3377663a2896b7c901fb41022dbc011d5076f8af1c");
//		
//		
//		Base64 base64 = new Base64();
//		String epwd = base64.en(encrypted);
//		//String epwd = (new String(base64.encode(encrypted)));
//		System.err.println("epwd="+epwd);
		
		
//		Base64 base64 = new Base64();
//		System.out.println(new String(base64.encode(new byte[]{0x12, 0x34})));
//		try {
		
//			JMXServiceURL url = new JMXServiceURL(
//					"service:jmx:rmi:///jndi/rmi://localhost:8023/service");
//			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
//			
//			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
//			//String domain = mbsc.getDefaultDomain();
//			
//			ObjectName objectName = new ObjectName("ws.hoyland.sm.service:name=ProxyService");
//			ProxyServiceMBean service = (ProxyServiceMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, ProxyServiceMBean.class, true);
//			System.out.println(service.getProxies());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		Class<?> clazz = null;
//		try {
//			clazz = new HoylandClassLoader().loadClass("ws.hoyland.sm.Dynamicer");
//			System.out.println(clazz.getMethod("excute", new Class[] {
//					DefaultHttpClient.class, HttpGet.class }).invoke(null, new Object[]{new DefaultHttpClient(),  new HttpGet("http://www.baidu.com")})) ;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//System.out.println(ClientDetecter.getMachineID("SMZS"));
//		Stack<String> stack = new Stack<String>();
//		//stack.setSize(10);
//		
//		for(int i=0;i<20;i++){
//			stack.push(String.valueOf(i));
//		}
//		System.out.println(stack.size());
//		//stack.re
//		
//		while(!stack.isEmpty()){
//			System.out.println(stack.pop());
//		}
//		System.out.println(stack.size());
		
		String accl = "1----2----323";
		accl = accl.substring(accl.indexOf("----")+4);
		System.out.println(accl);
	}

}
