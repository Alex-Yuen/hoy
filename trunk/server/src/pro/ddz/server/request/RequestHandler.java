package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.Calendar;
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
	private Request request;
	private boolean finish;
	private static Calendar cal = Calendar.getInstance();
	
	public RequestHandler(HttpServletRequest req, RequestQueue queue, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList){
		this.req = req;
		this.queue = queue;
		this.messageMap = messageMap;
		this.dao = dao;
		this.onlineList = onlineList;
		this.finish = false;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		//构造一个Request抽象类的具体对象
		String cmd = this.req.getHeader("Cmd");
		String type = this.req.getHeader("Type");
		
		//refresh onlineList
		synchronized(this.onlineList){
			for(User user:onlineList){
				if(user.getId()==Integer.parseInt(this.req.getHeader("UID"))){
					user.setLastRequestTime(cal.getTime());
					break;
				}
			}
		}
		
		try {
			if("QUICK".equals(cmd)){
				request = new QuickRegisterRequest(queue, messageMap, dao, onlineList);
			}
			
			if("ASYNC".equals(type)){
				//异步情况下，放入队列
				synchronized(this.queue){
					//放到RequestQueue
					if(request!=null){
						this.queue.add(request);
					}
				}
			}else{
				//同步情况下，直接执行
				if(request.isExecutable()){
					request.execute();
					this.finish = true;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getContent(){
		String result = "";
		//如果未完成，则继续等待
		while(!this.finish){
			try{
				Thread.sleep(10);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		result = request.getResult();
		return result;
	}
}
