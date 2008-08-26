package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.message.Message;
import pro.ddz.server.model.User;

public abstract class Request {
	protected int userId;
	protected RequestQueue queue;
	protected HashMap<String, Message> messageMap;
	protected DataAccessObject dao;
	protected ArrayList<User> onlineList;
	protected String result;
	
	public Request(RequestQueue queue, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList){
		this.queue = queue;
		this.messageMap = messageMap;
		this.dao = dao;
		this.onlineList = onlineList;
	}
	protected Message getMessage(){
		if(this.userId==0){
			return null;
		}else{
			Message message = messageMap.get(String.valueOf(this.userId));
			if(message==null){
				message = new Message();
				messageMap.put(String.valueOf(this.userId), message);
			}
			return message;
		}
	}
	public abstract boolean isExecutable();
	public abstract void execute();
	
	public String getResult(){
		return this.result;
	}
}
