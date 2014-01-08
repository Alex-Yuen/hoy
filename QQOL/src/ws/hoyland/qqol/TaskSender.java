package ws.hoyland.qqol;

import ws.hoyland.util.Configuration;
import ws.hoyland.util.Converts;

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
		Task taskx = null;
		byte x = 0;
		byte itv = 1;
		Engine.getInstance().getAcccounts().get(task.getAccount()).remove(task.getST());
		while(Engine.getInstance().getAcccounts().get(task.getAccount()).get(task.getST())==null&&x<3){
			taskx = new Task(task.getType(), task.getAccount());
			x++;
			itv += Math.pow(2, x);
			System.err.println("X:"+x+"/"+String.valueOf(taskx.getST())+"/"+Converts.bytesToHexString(taskx.getSEQ()));
			//if(x==2){
				//System.err.println(task);
			//}
			Engine.getInstance().addTask(taskx);
			try{
				Thread.sleep(1000*itv);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
