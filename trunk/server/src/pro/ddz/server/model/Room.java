package pro.ddz.server.model;

import java.util.ArrayList;

public class Room {
	private int id;
	private int userCount;
	private ArrayList<Desk> desks;
	
	public static int USERS_PER_DESK = 3;
	
	public Room(int id, int deskPerRoom){
		this.id = id;
		this.userCount = 0;
		this.desks = new ArrayList<Desk>();
		Desk desk = null;
		
		//initialize desks
		for(int i=0; i<deskPerRoom; i++){
			desk = new Desk(this.id*1000+i, USERS_PER_DESK); // desk id
			desks.add(desk);
		}
	}
	
	public int getId(){
		return this.id;
	}
	
	public int userCount(){
		return this.userCount;
	}
	
	public int deskCount(){
		return this.desks.size();
	}
	
	public void joinRoom(User user){
		this.userCount++;
	}
}
