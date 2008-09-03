package pro.ddz.server.core;

//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;

public class Message {
	private ArrayList<String> datum;
	
//	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private static Calendar cal = Calendar.getInstance();
	
	public Message(){
		this.datum = new ArrayList<String>();
	}
	
	public synchronized void add(String data){
		StringBuffer sb = new StringBuffer();
		sb.append(data);
//		sb.append('@');
//		sb.append(sdf.format(cal.getTime()));
		this.datum.add(sb.toString());
	}
	
	public synchronized String getDatum(){
		StringBuffer sb = new StringBuffer();
		for(String s:datum){
			sb.append(s);
			sb.append('\n');
		}
		if(sb.length()>=1)
			sb.deleteCharAt(sb.length()-1);
		datum.clear();
		return sb.toString();
	}
}
