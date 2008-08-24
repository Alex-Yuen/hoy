package pro.ddz.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.message.Message;
import pro.ddz.server.model.Room;
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
	private ArrayList<Room> rooms;
	private ArrayList<User> onlineList;
	
	private static int ROOM_SIZE = 25;
	private static int DESK_PER_ROOM = 10;
	
	public MainServlet() {
		//initialize
		this.requestQueue = new RequestQueue();
		this.messageMap = new HashMap<String, Message>();//after user login, add a Message Object.
		this.dao = new DataAccessObject();
		this.onlineList = new ArrayList<User>();
		
		rooms = new ArrayList<Room>();
		Room room = null;
		for(int i=0; i<ROOM_SIZE; i++){
			room = new Room(i, DESK_PER_ROOM);
			rooms.add(room);
		}
		
		//start request executor thread
		new Thread(new RequestExecutor(requestQueue)).start();
		
		//start watcher thread
		new Thread(new Watcher(onlineList)).start();
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String userId = req.getHeader("USER-ID");
		
		String content = null;
		if(userId!=null){
			Message message = this.messageMap.get(userId);
			if(message!=null){
				content = message.getDatum();
			}else{
				content = "Invalid User-ID";
			}
		}else{
			content = "Test Successfully";
		}
		
		resp.setHeader("Content", content);
		
		// a handler to deal with the request
		new RequestHandler(req, requestQueue, messageMap, dao, onlineList);
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