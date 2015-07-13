package net.xland.aqq.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import net.xland.aqq.service.task.MobileTask;
import net.xland.util.Cryptor;

public abstract class Task implements Runnable {
	protected QQServer server;
	protected short seq;
	protected String sid;
	protected byte[] content;
	protected Map<String, Object> session;
	protected Cryptor cryptor = new Cryptor();
	
	protected ByteArrayOutputStream bos = new ByteArrayOutputStream();	
	protected static byte[] outterkey = new byte[16];
	
	public void setServer(QQServer server) {
		this.server = server;		
	}

	public void setSequence(short seq) {
		this.seq = seq;		
	}
	
	protected void submit(){
//		this.content = this.bos.toByteArray();
		bos.reset();
		this.session.put("x-status", "-1");
		this.session.put("x-result", "ready-to-send-packet");
		Packet packet = new Packet(this.sid, this.content);
		if(this instanceof MobileTask){
			packet.setSlpt(1); //首次延迟2秒
		}
		this.server.submit(packet);
	}

	public String getSid() {
		return this.sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}	
}
