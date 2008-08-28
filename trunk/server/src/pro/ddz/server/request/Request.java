package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.message.Message;
import pro.ddz.server.model.User;

public abstract class Request {
	protected int userId;
	protected HttpServletRequest req;
	protected boolean isAsync;	// «∑Ò“Ï≤Ω«Î«Û
	protected HashMap<String, Message> messageMap;
	protected DataAccessObject dao;
	protected ArrayList<User> onlineList;
	protected String result;
	
	public Request(HttpServletRequest req, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList){
		this.req = req;
		this.messageMap = messageMap;
		this.dao = dao;
		this.onlineList = onlineList;
		if("ASYNC".equals(req.getHeader("Type"))){
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
