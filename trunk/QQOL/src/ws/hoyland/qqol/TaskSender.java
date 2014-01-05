package ws.hoyland.qqol;

public class TaskSender implements Runnable {
	private Task task;
	public TaskSender(Task task){
		this.task = task;
	}
	
	@Override
	public void run() {
		byte x = 0;
		byte itv = 0;
		Engine.getInstance().getAcccounts().get(task.getAccount()).remove(task.getST());
		while(Engine.getInstance().getAcccounts().get(task.getAccount()).get(task.getST())==null&&x<3){
			itv += Math.pow(2, x);
			System.err.println("X:"+x+"/"+String.valueOf(task.getST()));
			Engine.getInstance().addTask(task);
			try{
				Thread.sleep(1000*itv);
			}catch(Exception e){
				e.printStackTrace();
			}
			x++;
		}
	}
}
