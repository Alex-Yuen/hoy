package net.xland.aqq.service;

public class Packet {
	private String sid;
	private byte[] content;
	
	public Packet(String sid, byte[] content){
		this.sid = sid;
		this.content = content;
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
