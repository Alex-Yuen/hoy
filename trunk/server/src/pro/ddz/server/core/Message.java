package pro.ddz.server.core;

//import java.text.SimpleDateFormat;
import java.util.ArrayList;

import pro.ddz.server.model.User;
//import java.util.Calendar;

public class Message {
	private String userId;
	private User user;
	private ArrayList<String> datum;
	
//	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private static Calendar cal = Calendar.getInstance();
	
	public Message(String userId, ArrayList<User> onlineList){
		this.userId = userId;
		this.datum = new ArrayList<String>();
		for(User u:onlineList){
			if(u.getId()==Integer.parseInt(this.userId)){
				this.user = u;
				break;
			}
		}
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
			//������������Ϣȥ������
			boolean ignore = false;
			/**
			 * ԭ���ǣ����ݷ��������û���ǰ״̬����Щ��Ϣ��û���õģ��ǹ�ʱ�ģ���datum�����С�
			 */
			char[] ss = s.toCharArray();
			int len = 0;
			for(char ssx:ss){
				if(ssx=='|'){
					len++;
				}
			}
			
			//���˹��򣬼�������

			//SIT|1|1101|0|0|0
			if(s.startsWith("SIT")&&this.user.getDeskId()>0&&len==5){
				ignore = true;
			}
			if(s.startsWith("LEFT")&&this.user.getDeskId()>0&&len==3){
				ignore = true;
			}
			
			if(!ignore){
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
