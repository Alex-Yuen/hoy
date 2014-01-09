package ws.hoyland.qqol;

public class Checker implements Runnable {
	private Task task;
	
	public Checker(Task task){
		this.task = task;
	}
	@Override
	public void run() {
		//休眠一段时间之后，进行检测
		double itv = Math.pow(2, task.getRetry()+1)+1;
		try{
			Thread.sleep((long)(1000*itv));
		}catch(Exception e){
			e.printStackTrace();
		}
		if(Engine.getInstance().getAcccounts().get(task.getAccount()).get(task.getST())==null){
			Task taskx = new Task(task.getType(), task.getAccount());
			taskx.setRetry((byte)(task.getRetry()+1));
			Engine.getInstance().addTask(taskx);
		}
	}
}
