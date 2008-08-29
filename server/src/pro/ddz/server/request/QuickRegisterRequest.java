package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import pro.ddz.server.core.Request;
import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;

public class QuickRegisterRequest extends Request {

	public QuickRegisterRequest(HttpServletRequest req, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(req, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//实现快速注册功能
		//QUICK|0~9|USERID|USERNAME|NICKNAME|PASSWORD|SEXUAL@time
		User user = dao.quickRegister();
		StringBuffer data = new StringBuffer();
		
		if(user!=null){
			this.userId = user.getId();
			data.append("QUICK");
			data.append('|');
			data.append("1");
			data.append('|');
			data.append(this.userId);
			data.append('|');
			data.append(user.getUserName());
			data.append('|');
			data.append(user.getNickName());
			data.append('|');
			data.append(user.getPassword());
			data.append('|');
			data.append(user.isSexual());
		}else{
			data.append("QUICK");
			data.append('|');
			data.append("2");
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
