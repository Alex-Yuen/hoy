package ws.hoyland.cs.servlet;

import java.util.LinkedHashMap;

public class Cookies extends LinkedHashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -829780747705919217L;
	private static Cookies instance;
//	private static Random random = new Random();
	private int idx = -1;
	
	private Cookies() {
		
	}
	
	public static synchronized Cookies getInstance(){
		if(instance==null){
			instance = new Cookies();
		}
		return instance;
	}

	public Cookies(int arg0) {
		super(arg0);
	}

	public synchronized String peek(){
		idx++;
		if(idx>=size()){
			idx = 0;
		}
		
		if(size()>0){
			//int idx = random.nextInt(size());
			String[] cks = this.values().toArray(new String[0]);
			return cks[idx];
		}else{
			return null;
		}
	}	
}
