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
		Engine.getInstance().getAcccounts().get(task.getAccount()).remove("heart");
		while(Engine.getInstance().getAcccounts().get(account).get("heart")==null&&x<3){
			x++;
			itv += Math.pow(2, x);
			Engine.getInstance().addTask(task);
			try{
				Thread.sleep(1000*itv);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
