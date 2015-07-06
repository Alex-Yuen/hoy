package net.xland.aqq.service.task;

import net.xland.aqq.service.Task;

public class CodeTask extends Task {
	private String code = null;
	
	public CodeTask(String sid, String code) {
		this.sid = sid;
		this.code = code;
	}

	@Override
	public void run() {
		try{
			this.session = this.server.getSession(this.sid);
			this.session.put("x-cmd", "code");
			
			submit();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
