package pro.ddz.server.request;

import java.util.HashMap;

import pro.ddz.server.message.Message;

public abstract class Request {
	protected RequestQueue queue;
	protected HashMap<String, Message> messageMap;
	
	public abstract boolean isExecutable();
	public abstract void execute();
}
