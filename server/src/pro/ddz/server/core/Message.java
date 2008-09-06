package pro.ddz.server.core;

//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;

public class Message {
	private String userId;
	private ArrayList<String> datum;
	
//	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private static Calendar cal = Calendar.getInstance();
	
	public Message(String userId){
		this.userId = userId;
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
			//这里做垃圾消息去除机制
			/**
			 * 原理是，根据服务器和用户当前状态，有些消息是没有用的，是过时的，在datum数组中。
			 */
			System.out.println(userId);
			if(true){
				sb.append(s);
				sb.append('\n');
			}
		}
		if(sb.length()>=1)
			sb.deleteCharAt(sb.length()-1);
		datum.clear();
		return sb.toString();
	}
}
