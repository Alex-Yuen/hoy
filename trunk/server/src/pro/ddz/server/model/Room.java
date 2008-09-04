package pro.ddz.server.model;

import java.util.ArrayList;

public class Room {
	private int id;
	private int score;//房间积分
	private int maxUserSize;
	private ArrayList<Desk> desks;
	private ArrayList<User> users;	//进入房间的用户列表
	
	public static int USERS_PER_DESK = 3;
	public static int NOMAL_ROOM_SCORE = 10;
	public static int WAITING_USER_COUNT = 4;//最大等待用户数
	
	public Room(int id, int deskPerRoom){
		this.id = id;
		this.score = NOMAL_ROOM_SCORE;
		this.desks = new ArrayList<Desk>();
		this.users = new ArrayList<User>();
		
		this.maxUserSize = deskPerRoom*USERS_PER_DESK+WAITING_USER_COUNT;
		
		Desk desk = null;
		
		//initialize desks
		for(int i=0; i<deskPerRoom; i++){
			desk = new Desk(this.id*100+i+1, USERS_PER_DESK); // desk id
			desks.add(desk);
		}
	}
	
	public int getId(){
		return this.id;
	}
	
	public int size(){
		return this.desks.size();
	}
	
	public ArrayList<User> getUsers(){
		return this.users;
	}
	
	public ArrayList<Desk> getDesks(){
		return this.desks;
	}
	
	public int getMaxUserSize(){
		return this.maxUserSize;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public boolean joinRoom(User user){
		boolean contain = false;
		System.out.println("[JOIN USER]");
		synchronized(this.users){
			if(this.users.size()<this.maxUserSize){
					for(User u:this.users){
						if(user.getId()==u.getId()){
							contain = true;
							break;
						}
					}
					if(!contain){
						this.users.add(user);
						return true;
					}
			}
		}
		return false;
	}
	
	public boolean leftRoom(User user){
		synchronized(this.users){
			this.users.remove(user);
		}
		return true;
	}
}
