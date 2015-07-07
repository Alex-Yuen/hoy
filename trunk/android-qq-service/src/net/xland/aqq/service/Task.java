package net.xland.aqq.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import net.xland.util.Cryptor;

public abstract class Task implements Runnable {
	protected QQServer server;
	protected short seq;
	protected String sid;
	protected byte[] content;
	protected Map<String, Object> session;
	
	protected ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	protected static Cryptor cryptor = new Cryptor();
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
		this.session.put("x-status", "1");
		this.session.put("x-result", "ready-to-send-packet");
		this.server.submit(this.sid, this.content);
	}

	public String getSid() {
		return this.sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}	
}
