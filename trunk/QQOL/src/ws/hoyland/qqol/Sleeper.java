package ws.hoyland.qqol;

import ws.hoyland.util.Configuration;

public class Sleeper implements Runnable {

	private Task task;
	
	public Sleeper(Task task){
		this.task = task;
	}
	
	@Override
	public void run() {
		try{
			Thread.sleep(1000*60*Integer.parseInt(Configuration.getInstance().getProperty("EX_ITV")));
		}catch(Exception e){
			//e.printStackTrace();
		}
		Engine.getInstance().addTask(task);
	}
}
