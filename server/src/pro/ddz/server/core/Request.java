package pro.ddz.server.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;

public abstract class Request {
	protected int userId;
	protected Map<String, String[]> parameters;
	protected boolean isAsync;	// «∑Ò“Ï≤Ω«Î«Û
	protected HashMap<String, Message> messageMap;
	protected DataAccessObject dao;
	protected ArrayList<User> onlineList;
	protected ArrayList<Scene> scenes;
	protected String result;
	
	public Request(Map<String, String[]> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		this.parameters = parameters;
		this.messageMap = messageMap;
		this.dao = dao;
		this.onlineList = onlineList;
		this.scenes = scenes;
		if("ASYNC".equals(parameters.get("Type")[0])){
			this.isAsync = true;
		}else{
			this.isAsync = false;
		}
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
