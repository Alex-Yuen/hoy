package ws.hoyland.cs.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Cookies extends ArrayList<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -829780747705919217L;
	private static Cookies instance;
	private static Random random = new Random();
	private int idx = 0;
	
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

	public Cookies(Collection<? extends String> arg0) {
		super(arg0);
	}

	public synchronized String peek(){
		idx++;
		if(idx==size()){
			idx = 0;
		}
		
		if(size()>0){
			//int idx = random.nextInt(size());
			return this.get(idx);
		}else{
			return null;
		}
	}	
}
