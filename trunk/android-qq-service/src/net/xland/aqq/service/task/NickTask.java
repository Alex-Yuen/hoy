package net.xland.aqq.service.task;

import net.xland.aqq.service.Task;

public class NickTask extends Task {
	private String nick = null;
	
	public NickTask(String sid, String nick) {
		this.sid = sid;
		this.nick = nick;
	}

	@Override
	public void run() {
		try{
			this.session = this.server.getSession(this.sid);
			this.session.put("x-cmd", "nick");
			
			submit();	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
