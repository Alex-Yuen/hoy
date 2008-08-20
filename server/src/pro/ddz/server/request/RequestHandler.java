package pro.ddz.server.request;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.message.Message;

public class RequestHandler implements Runnable {
	private HttpServletRequest req;
	private RequestQueue queue;
	private HashMap<String, Message> messageMap;
	private DataAccessObject dao;
	
	public RequestHandler(HttpServletRequest req, RequestQueue queue, HashMap<String, Message> messageMap, DataAccessObject dao){
		this.req = req;
		this.queue = queue;
		this.messageMap = messageMap;
		this.dao = dao;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//构造一个Request抽象类的具体对象
		System.out.println(req);
		System.out.println(messageMap);
		System.out.println(dao);

		synchronized(queue){
			//放到RequestQueue
		}
	}
}
