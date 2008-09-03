package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.core.Request;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;

public class LoginRequest extends Request {
	private static String VERSION = "1.0"; 
	public LoginRequest(HashMap<String, String> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(parameters, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//实现快速注册功能
		//LOGIN|0~9|USERID@time
		User user = dao.login(parameters.get("username"), parameters.get("password"));
		StringBuffer data = new StringBuffer();
		
		//TODO
		/**
		 * 检查是否已经在线
		 */
		
		if(user!=null){
			this.userId = user.getId();
			data.append("LOGIN");
			data.append('|');
			data.append("1");
			data.append('|');
			data.append(this.userId);
			data.append('|');
			data.append(VERSION);
		}else{
			data.append("LOGIN");
			data.append('|');
			data.append("2");
		}
		
		//添加到在线用户列表
		boolean contain = false;
		synchronized(this.onlineList){
			for(User u:this.onlineList){
				if(u.getId()==user.getId()){
					contain = true;
					break;
				}
			}
			//没有在线
			if(!contain){
				this.onlineList.add(user);
			}
		}
				
		if(this.isAsync){
			getMessage().add(data.toString());
		}else{
			this.result = data.toString();
		}
	}

	@Override
	public boolean isExecutable() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
