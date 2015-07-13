package net.xland.aqq.service;

public class Packet {
	private int slpt; //sleeptime
	private String sid;
	private byte[] content;
	
	public Packet(String sid, byte[] content){
		this.sid = sid;
		this.content = content;
	}	
	
	public int getSlpt() {
		return slpt;
	}

	public void setSlpt(int slpt) {
		this.slpt = slpt;
	}

	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}	
}
