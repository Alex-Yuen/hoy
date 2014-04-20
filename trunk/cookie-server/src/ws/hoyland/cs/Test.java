package ws.hoyland.cs;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ws.hoyland.util.JNALoader;
import ws.hoyland.util.YDM;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//        System.out.println(Proxy.class.getName());
//        Class clazz=Proxy.class;
//        Class clazz1=Proxy.getProxyClass(Collection.class.getClassLoader(), Collection.class);
//        System.out.println(clazz);
//        System.out.println(clazz1);
//		YDM y = (YDM)JNALoader.load("/yundamaAPI.dll", YDM.class);
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("1", "11");
		map.put("2", "12");
		map.put("3", "13");
		map.put("4", "14");
		
		String[] x = map.keySet().toArray(new String[0]);
		for(int i=0;i<x.length;i++){
			System.out.println(x[i]);
		}
	}

}
