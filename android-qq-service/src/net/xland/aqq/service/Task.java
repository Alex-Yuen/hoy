package net.xland.aqq.service;

import java.io.ByteArrayOutputStream;

import net.xland.util.Cryptor;

public abstract class Task implements Runnable {
	protected QQServer server;
	protected short seq;
	protected String sid;
	protected byte[] content;
	
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
		this.server.submit(this.sid, this.content);
	}

	public String getSid() {
		return this.sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}	
}
