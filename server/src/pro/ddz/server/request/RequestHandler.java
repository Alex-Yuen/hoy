package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.message.Message;
import pro.ddz.server.model.User;

public class RequestHandler implements Runnable {
	private HttpServletRequest req;
	private RequestQueue queue;
	private HashMap<String, Message> messageMap;
	private DataAccessObject dao;
	private ArrayList<User> onlineList;
	
	public RequestHandler(HttpServletRequest req, RequestQueue queue, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList){
		this.req = req;
		this.queue = queue;
		this.messageMap = messageMap;
		this.dao = dao;
		this.onlineList = onlineList;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//构造一个Request抽象类的具体对象
		System.out.println(req);
		System.out.println(messageMap);
		System.out.println(dao);
		System.out.println(onlineList);

		synchronized(queue){
			//放到RequestQueue
		}
	}
}
