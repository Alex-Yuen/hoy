package pro.ddz.server.request;

import java.util.HashMap;

import pro.ddz.server.message.Message;

public abstract class Request {
	private RequestQueue queue;
	private HashMap<String, Message> messageMap;
	
	public abstract boolean isExecutable();
	public abstract void execute();
}
