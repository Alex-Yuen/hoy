package pro.ddz.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import pro.ddz.server.model.User;

public class Watcher implements Runnable {
	private ArrayList<User> onlineList;
	private static Calendar cal = Calendar.getInstance();
	
	static{
		cal.add(Calendar.MINUTE, -2);	//�Ƚ�ʱ���ǰ������
	}
	
	public Watcher(ArrayList<User> onlineList){
		this.onlineList = onlineList;
	}
	
	@Override
	public void run() {
		//���������б�
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
			//����1����
			Thread.sleep(1000);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
