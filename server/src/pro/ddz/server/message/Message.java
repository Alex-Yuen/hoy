package pro.ddz.server.message;

import java.util.Date;

public class Message {
	private int from;
	private int to;
	private String data;
	private Date receiveTime;
	
	public static int FROM_SYSTEM = 0;
	public static int TO_ALL_USER = 0;
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
}
