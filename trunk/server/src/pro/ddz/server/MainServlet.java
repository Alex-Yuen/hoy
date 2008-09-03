package pro.ddz.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;
import pro.ddz.server.core.Request;
import pro.ddz.server.core.RequestExecutor;
import pro.ddz.server.core.RequestHandler;

public class MainServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3423154401523644881L;

	private Queue<Request> requestQueue;
	private HashMap<String, Message> messageMap;
	private DataAccessObject dao;
	private ArrayList<Scene> scenes;
	private ArrayList<User> onlineList;
	
	private static int SCENE_SIZE = 1;
	private static int ROOM_PER_SCENE = 5;
	
	public MainServlet() {
		//initialize
		this.requestQueue = new LinkedList<Request>();
		this.messageMap = new HashMap<String, Message>();//after user login, add a Message Object.
		this.dao = new DataAccessObject();
		this.onlineList = new ArrayList<User>();
		
		scenes = new ArrayList<Scene>();
		Scene scene = null;
		for(int i=0; i<SCENE_SIZE; i++){
			scene = new Scene(i+1, ROOM_PER_SCENE, Scene.NOMAL_ROOM);
			scenes.add(scene);
		}
		
		//start request executor thread
		Thread executor = new Thread(new RequestExecutor(requestQueue));
		executor.setName("RequestExecutor");
		executor.start();
		
		//start watcher thread
		Thread watcher = new Thread(new Watcher(onlineList));
		watcher.setName("Watcher");
		watcher.start();
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HashMap<String, String> parameters = new HashMap<String, String>();
		String type = req.getHeader("Type");
		String cmd = req.getHeader("Cmd");
		
		System.out.println(">>>>>>>>>>>>>>>>>["+new Date()+"]");
		Enumeration<?> en = req.getHeaderNames();
		while(en.hasMoreElements()){
			String name = (String)en.nextElement();
			parameters.put(name, req.getHeader(name));
			System.out.println(name+":"+req.getHeader(name));
		}
		
		String content = null;
		
		if("SYNC".equals(type)){
			//Õ¨≤Ω
			RequestHandler handler = new RequestHandler(parameters, requestQueue, messageMap, dao, onlineList, scenes);
			content = handler.getContent();
		}else if("ASYNC".equals(type)){
			//“Ï≤Ω
			String userId = req.getHeader("UID");
			if(userId!=null){
				Message message = getMessage(userId);
				if(message!=null){
					content = message.getDatum();
				}else{
					content = "Invalid UID";
				}
			}else{
				content = "Invalid UID";
			}

			if("".equals(content)){
				content = "No new messages";
			}
			// a handler to deal with the request
			new RequestHandler(parameters, requestQueue, messageMap, dao, onlineList, scenes);
		}else{
			content = "Invalid Type";
		}
		
		if(cmd==null){
			content = "Invalid Cmd";
		}
		
		System.out.println("<<<<<<<<<<<<<<<<<");
		System.out.println(content);
		DataOutputStream dos = new DataOutputStream(resp.getOutputStream());
		dos.writeUTF(content);
		dos.flush();
		dos.close();
	}
	
	private Message getMessage(String userId){
		if(userId==null){
			return null;
		}else{
			Message message = messageMap.get(userId);
			if(message==null){
				message = new Message();
				messageMap.put(userId, message);
			}
			return message;
		}
	}
	/**
	 * remain for test from browser
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
}