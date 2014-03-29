//import java.io.File;
//import java.io.FileOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import app.appletviewer;

public class jagexappletviewer {

	public jagexappletviewer() {
		// TODO Auto-generated constructor stub
		// jagexappletviewer.main(arg0)
	}

	public static void main(String args[]) {
//		try {
//			FileOutputStream out = new FileOutputStream(new File("C:/viewer.txt"));
//
//			for (int i = 0; i < args.length; i++) {
//				out.write((args[i] + "\r\n").getBytes());
//				out.flush();
//			}
//
//			out.write("--------------\r\n".getBytes());
//			
//			out.write((System.getProperty("com.jagex.config")+"\r\n").getBytes());
//			out.write((System.getProperty("com.jagex.configfile")+"\r\n").getBytes());
//			out.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		System.setProperty("com.jagex.config", "http://www.runescape.com/k=3/l=$(Language:0)/jav_config.ws");

		
		 // 取得系统属性
	    Properties prop = System.getProperties();
	    // 设置http访问要使用的代理服务器的地址
	    prop.setProperty("http.proxyHost", "127.0.0.1");
	    // 设置http访问要使用的代理服务器的端口
	    prop.setProperty("http.proxyPort", "8888");
	    //System.out.println(qb.class.);
		if (args.length < 1) {
			System.exit(0);
		}
		appletviewer.a(args[0], -1);
		
	}
}
