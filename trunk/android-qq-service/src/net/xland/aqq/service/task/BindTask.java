package net.xland.aqq.service.task;

import net.xland.aqq.service.Task;

public class BindTask extends Task {
	private String mobile = null;
	
	public BindTask(String sid, String mobile) {
		this.sid = sid;
		this.mobile = mobile;
	}

	@Override
	public void run() {
		try{
			this.session = this.server.getSession(this.sid);
			this.session.put("x-cmd", "bind");
			
			submit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
