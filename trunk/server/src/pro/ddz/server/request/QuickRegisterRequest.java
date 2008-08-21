package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.message.Message;
import pro.ddz.server.model.User;

public class QuickRegisterRequest extends Request {

	public QuickRegisterRequest(RequestQueue queue, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList){
		super(queue, messageMap, dao, onlineList);
	}
	
	@Override
	public void execute() {
		//实现快速注册功能
		//QUICK_REGISTER_OK|USERID|USERNAME|NICKNAME|PASSWORD|SEXUAL@time
		User user = dao.quickRegister();
		this.userId = user.getId();
		StringBuffer data = new StringBuffer();
		data.append("QUICK_REGISTER_OK");
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
		
		synchronized(this.onlineList){
			this.onlineList.add(user);
		}
		
		getMessage().add(data.toString());
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
