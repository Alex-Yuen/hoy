package net.xland.aqq.service;

import net.xland.util.Cryptor;

public abstract class Task implements Runnable {
	protected QQServer server;
	protected int seq;
	protected String sid;
	protected byte[] content;
	
	protected static Cryptor cryptor = new Cryptor();
	protected static byte[] outterKey = new byte[]{
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
	};
	
	public void setServer(QQServer server) {
		this.server = server;		
	}

	public void setSequence(int seq) {
		this.seq = seq;		
	}
	
	protected void submit(){
		this.server.submit(this.sid, this.content);
	}
}
