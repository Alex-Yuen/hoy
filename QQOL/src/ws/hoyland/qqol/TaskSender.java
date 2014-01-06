package ws.hoyland.qqol;

import ws.hoyland.util.Configuration;

public class TaskSender implements Runnable {
	private Task task;
	public TaskSender(Task task){
		this.task = task;
	}
	
	@Override
	public void run() {
		if(Engine.getInstance().getAcccounts().get(task.getAccount()).get("nw")!=null){//need wait
			Engine.getInstance().getAcccounts().get(task.getAccount()).remove("nw");
			try{
				Thread.sleep(1000*60*Integer.parseInt(Configuration.getInstance().getProperty("EX_ITV")));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		byte x = 0;
		byte itv = 0;
		Engine.getInstance().getAcccounts().get(task.getAccount()).remove(task.getST());
		while(Engine.getInstance().getAcccounts().get(task.getAccount()).get(task.getST())==null&&x<3){
			x++;
			itv += Math.pow(2, x);
			System.err.println("X:"+x+"/"+String.valueOf(task.getST()));
			Engine.getInstance().addTask(task);
			try{
				Thread.sleep(1000*itv);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
