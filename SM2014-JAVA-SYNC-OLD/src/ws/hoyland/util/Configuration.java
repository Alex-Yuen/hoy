package ws.hoyland.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

public class Configuration extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6545878206825977406L;
	private static Configuration instance;
	private String path;

	private Configuration(String path) {
		try{
			URL url = Configuration.class.getClassLoader().getResource("");
			String xpath = url.getPath();
			
			if(xpath.length()>4){
				if("/lib/".equals(xpath.substring(xpath.lastIndexOf("/")-4, xpath.lastIndexOf("/")+1))){
					xpath = xpath.replace("/lib/", "/");	
				}
			}
			try{
				xpath = URLDecoder.decode(xpath, "UTF-8");
			}catch(Exception e){
				e.printStackTrace();
			}
			
			this.path = path;
			File file = new File(xpath+path);
			if(!file.exists()){
				file.createNewFile();
			}
			
			InputStream is = new FileInputStream(file);//("/"+path);
			load(is);
			is.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Configuration getInstance(String path){
		synchronized(Configuration.class){
			if(instance==null){
				instance = new Configuration(path);
			}
		}
		return instance;		
	}

	public void save(){		
		try{
			URL url = ClassLoader.getSystemResource(path);
			File file = new File(url.toURI());
			OutputStream os = new FileOutputStream(file);
			store(os, null);
			os.flush();
			os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
