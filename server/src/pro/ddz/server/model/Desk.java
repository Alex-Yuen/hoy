package pro.ddz.server.model;

import java.util.ArrayList;

public class Desk {
	private int id;
	private int size;
	private ArrayList<User> users;
	
	public Desk(int id, int size){
		this.id = id;
		this.size = size;
		this.users = new ArrayList<User>();
	}
	
	public int size(){
		return this.size;
	}
	
	public int currentCount(){
		return this.users.size();
	}
	
	public int getId(){
		return this.id;
	}
	
	public ArrayList<User> getUsers(){
		return this.users;
	}
	
	public void sitDown(User user){
		if(this.users.size()<this.size&&!this.users.contains(user)){
			this.users.add(user);
		}
	}
	
	public void leftUp(User user){
		if(this.users.contains(user)){
			this.users.remove(user);
		}
	}
}
