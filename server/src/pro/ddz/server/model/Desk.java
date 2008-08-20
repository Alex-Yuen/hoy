package pro.ddz.server.model;

public class Desk {
	private int id;
	private User[] users;
	
	public Desk(int id, int size){
		this.id = id;
		this.users = new User[size];
	}
	
	public int deskSize(){
		return this.users.length;
	}
	
	public int userCount(){
		int count = 0;
		for(int i=0; i<this.users.length; i++){
			if(users[i]!=null){
				count++;
			}
		}
		return count;
	}
	
	public int getId(){
		return this.id;
	}
}
