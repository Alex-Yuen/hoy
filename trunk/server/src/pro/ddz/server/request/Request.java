package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.message.Message;
import pro.ddz.server.model.User;

public abstract class Request {
	protected RequestQueue queue;
	protected HashMap<String, Message> messageMap;
	protected DataAccessObject dao;
	protected ArrayList<User> onlineList;
	
	public Request(RequestQueue queue, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList){
		this.queue = queue;
		this.messageMap = messageMap;
		this.dao = dao;
		this.onlineList = onlineList;
	}
	public abstract boolean isExecutable();
	public abstract void execute();
}
