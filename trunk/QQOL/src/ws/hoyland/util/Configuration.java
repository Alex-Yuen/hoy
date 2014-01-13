package ws.hoyland.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import ws.hoyland.qqol.Engine;
import ws.hoyland.qqol.EngineMessageType;

public class Configuration extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6545878206825977406L;
	private static Configuration instance;

	private Configuration() {
		try{
			InputStream is = Configuration.class.getResourceAsStream("/qqol.ini");
			load(is);
			is.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static synchronized Configuration getInstance(){
		if(instance==null){
			instance = new Configuration();
		}
		return instance;
	}

	public void save(){		
		try{
			URL url = ClassLoader.getSystemResource("qqol.ini");
			File file = new File(url.toURI());
			OutputStream os = new FileOutputStream(file);
			store(os, null);
			os.flush();
			os.close();
			
			EngineMessage message = new EngineMessage();
			message.setType(EngineMessageType.IM_CONFIG_UPDATED);
			message.setData(this);
			
			Engine.getInstance().fire(message);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
