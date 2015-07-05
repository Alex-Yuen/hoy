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
			//计算任务，然后交给发送引擎来发送
			submit();	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
