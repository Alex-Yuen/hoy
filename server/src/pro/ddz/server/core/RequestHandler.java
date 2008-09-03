package pro.ddz.server.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;
import pro.ddz.server.request.DeskRequest;
import pro.ddz.server.request.LoginRequest;
import pro.ddz.server.request.QuickRegisterRequest;
import pro.ddz.server.request.RoomRequest;
import pro.ddz.server.request.SceneRequest;
import pro.ddz.server.request.ScenesRequest;

public class RequestHandler implements Runnable {
	private Map<String, String[]> parameters;
	private Queue<Request> queue;
	private HashMap<String, Message> messageMap;
	private DataAccessObject dao;
	private ArrayList<User> onlineList;
	private ArrayList<Scene> scenes;
	private Request request;
	private boolean finish;
	private static Calendar cal = Calendar.getInstance();
	
	public RequestHandler(Map<String, String[]> parameters, Queue<Request> queue, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		this.parameters = parameters;
		this.queue = queue;
		this.messageMap = messageMap;
		this.dao = dao;
		this.onlineList = onlineList;
		this.scenes = scenes;
		this.finish = false;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		//构造一个Request抽象类的具体对象
		String cmd = this.parameters.get("Cmd")[0];
		String type = this.parameters.get("Type")[0];
		
		//refresh onlineList
		synchronized(this.onlineList){
			for(User user:onlineList){
				//System.out.println(user.getId());
				if(this.parameters.get("UID")!=null&&user.getId()==Integer.parseInt(this.parameters.get("UID")[0])){
					user.setLastRequestTime(cal.getTime());
					break;
				}
			}
		}
		
		try {						
			if("QUICK".equals(cmd)){
				request = new QuickRegisterRequest(parameters, messageMap, dao, onlineList, scenes);
			}else if("LOGIN".equals(cmd)){
				request = new LoginRequest(parameters, messageMap, dao, onlineList, scenes);
			}else if("SCENE".equals(cmd)){
				request = new SceneRequest(parameters, messageMap, dao, onlineList, scenes);
			}else if("ROOM".equals(cmd)){
				request = new RoomRequest(parameters, messageMap, dao, onlineList, scenes);
			}else if("DESK".equals(cmd)){
				request = new DeskRequest(parameters, messageMap, dao, onlineList, scenes);
			}else if("SCENES".equals(cmd)){
				request = new ScenesRequest(parameters, messageMap, dao, onlineList, scenes);
			}
			
			if(request!=null){
				if("ASYNC".equals(type)){
					//异步情况下，放入队列
					//System.out.println("ADD TO QUEUE");
					synchronized(this.queue){
						//放到RequestQueue
						this.queue.offer(request);
						System.out.println("[ADD TO QUEUE]");
					}
				}else{
					//同步情况下，直接执行
					if(request.isExecutable()){
						request.execute();
						this.finish = true;
					}
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
