package pro.ddz.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.message.Message;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;
import pro.ddz.server.request.RequestExecutor;
import pro.ddz.server.request.RequestHandler;
import pro.ddz.server.request.RequestQueue;

public class MainServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3423154401523644881L;

	private RequestQueue requestQueue;
	private HashMap<String, Message> messageMap;
	private DataAccessObject dao;
	private ArrayList<Scene> scenes;
	private ArrayList<User> onlineList;
	
	private static int SCENE_SIZE = 1;
	private static int ROOM_PER_SCENE = 5;
	
	public MainServlet() {
		//initialize
		this.requestQueue = new RequestQueue();
		this.messageMap = new HashMap<String, Message>();//after user login, add a Message Object.
		this.dao = new DataAccessObject();
		this.onlineList = new ArrayList<User>();
		
		scenes = new ArrayList<Scene>();
		Scene scene = null;
		for(int i=0; i<SCENE_SIZE; i++){
			scene = new Scene(i+1, ROOM_PER_SCENE);
			scenes.add(scene);
		}
		
		//start request executor thread
		new Thread(new RequestExecutor(requestQueue)).start();
		
		//start watcher thread
		new Thread(new Watcher(onlineList)).start();
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String type = req.getHeader("Type");
		String cmd = req.getHeader("Cmd");
		
		System.out.println(">>>>>>>>>>>>>>>>>["+new Date()+"]");
		Enumeration<?> en = req.getHeaderNames();
		while(en.hasMoreElements()){
			String name = (String)en.nextElement();
			System.out.println(name+":"+req.getHeader(name));
		}
		
		String content = null;
		
		if("SYNC".equals(type)){
			//Õ¨≤Ω
			RequestHandler handler = new RequestHandler(req, requestQueue, messageMap, dao, onlineList, scenes);
			content = handler.getContent();
		}else if("ASYNC".equals(type)){
			//“Ï≤Ω
			String userId = req.getHeader("UID");
			if(userId!=null){
				Message message = this.messageMap.get(userId);
				if(message!=null){
					content = message.getDatum();
				}else{
					content = "Invalid UID";
				}
			}else{
				content = "Invalid UID";
			}

			// a handler to deal with the request
			new RequestHandler(req, requestQueue, messageMap, dao, onlineList, scenes);
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
	
	/**
	 * remain for test from browser
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
}