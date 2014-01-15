package ws.hoyland.util;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class Cookie {
	private static Cookie instance;
	private DB db = null;
	private Map<String, Map<String, byte[]>> map = null;

	private Cookie() {

		URL url = Cookie.class.getClassLoader().getResource("");
		String xpath = url.getPath();
		File file = new File(xpath + "cookie.dat");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		db = DBMaker.newFileDB(file).closeOnJvmShutdown()
				.encryptionEnable("password").make();

		map = db.getHashMap("QQCookies");
	}

	public static synchronized void reset() {
		instance = new Cookie();
	}

	public static synchronized Cookie getInstance() {
		if(instance==null){
			reset();
		}
		return instance;
	}
	
	public Map<String, byte[]> get(String account){
		return map.get(account);
	}
	
	public void put(String account, Map<String, byte[]> details){
		map.put(account, details);
		db.commit();
	}
	
	public void remove(String account){
		map.remove(account);
		db.commit();
	}
	
}
