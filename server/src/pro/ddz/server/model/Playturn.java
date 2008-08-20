package pro.ddz.server.model;

import java.util.Date;

public class Playturn {
	private int id;
	private int deskId;
	private String[] users;
	private int result;
	private Date startTime;
	private Date endTime;
	
	public static int USER_COUNT = 3;
	
	public Playturn(){
		this.users = new String[USER_COUNT];
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getDeskId() {
		return deskId;
	}

	public void setDeskId(int deskId) {
		this.deskId = deskId;
	}

	public String[] getUsers() {
		return users;
	}
	public void setUser(int index, String user) {
		this.users[index] = user;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
