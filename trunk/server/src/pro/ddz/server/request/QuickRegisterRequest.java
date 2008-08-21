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
		// TODO Auto-generated method stub

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
