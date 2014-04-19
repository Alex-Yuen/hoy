package ws.hoyland.util;

import java.net.URL;
import java.net.URLDecoder;

import com.sun.jna.Native;

public class JNALoader {

	public JNALoader() {
		// TODO Auto-generated constructor stub
	}
	
	public static Object load(String path, Class<?> clazz){
		URL url = JNALoader.class.getClassLoader().getResource("");
		String xpath = url.getPath();
		xpath = xpath.substring(1);
		
		try {
			xpath = xpath.substring(0, xpath.indexOf("/WEB-INF/"));
			xpath = URLDecoder.decode(xpath, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("loading path="+xpath);
		Object o = Native.loadLibrary(xpath+path, clazz);
		return o;
	}

}
