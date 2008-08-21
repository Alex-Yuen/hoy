package pro.ddz.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import pro.ddz.server.model.User;

public class Watcher implements Runnable {
	private ArrayList<User> onlineList;
	private static Calendar cal = Calendar.getInstance();
	
	static{
		cal.add(Calendar.MINUTE, -2);	//比较时间调前两分钟
	}
	
	public Watcher(ArrayList<User> onlineList){
		this.onlineList = onlineList;
	}
	
	@Override
	public void run() {
		//监视在线列表
		try{
			Iterator<User> it = this.onlineList.iterator();
			synchronized(this.onlineList){
				while(it.hasNext()){
					User user = (User)it.next();
					if(user.getLastRequestTime().before(cal.getTime())){
						it.remove();
					}
				}
			}
			//休眠1分钟
			Thread.sleep(1000);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
