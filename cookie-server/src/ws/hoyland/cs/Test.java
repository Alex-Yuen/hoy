package ws.hoyland.cs;

import java.lang.reflect.Proxy;
import java.util.Collection;

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
		YDM y = (YDM)JNALoader.load("/yundamaAPI.dll", YDM.class);
	}

}
