package pro.ddz.server.model;

import java.util.HashMap;

public class Desk {
	private int id;
	private int size;
	private HashMap<String, User> users;
	
	public Desk(int id, int size){
		this.id = id;
		this.size = size;
		this.users = new HashMap<String, User>();
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
	
	public HashMap<String, User> getUsers(){
		return this.users;
	}
	
	public boolean sitDown(User user){
		boolean contain = false;
		synchronized(this){
			for(int i=0;i<this.size;i++){
				User u = (User)this.users.get(String.valueOf(i));
				if(u!=null&&u.getId()==user.getId()){
					contain = true;
					break;
				}
			}
			
			if(!contain&&this.users.size()<this.size){
				for(int i=0;i<this.size;i++){
					User u = (User)this.users.get(String.valueOf(i));
					if(u==null){
						this.users.put(String.valueOf(i), user);
						return true;
					}
				}	
			}
		}
		return false;
	}
	
	public void leftUp(User user){
		for(String key:this.users.keySet()){
			User u = (User)this.users.get(key);
			if(u.getId()==user.getId()){
				this.users.remove(key);
				break;
			}
		}
	}
}
