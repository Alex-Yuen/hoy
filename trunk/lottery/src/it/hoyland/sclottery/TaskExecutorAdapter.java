package it.hoyland.sclottery;

public class TaskExecutorAdapter implements Runnable {

	private TaskExecutor te;
	private final DefaultImageCanvas dic;

	public TaskExecutorAdapter(DefaultImageCanvas dic, TaskExecutor te) {
		this.dic = dic;
		if (te == null) {
			throw new IllegalArgumentException("Task parameter cannot be null");
		} else {
			this.te = te;
		}
	}

	public void run() {
        te.run();
        dic.getDisplay().callSerially(new TaskCleanner(this));
        
	}

    static DefaultImageCanvas getDefaultImageCanvas(TaskExecutorAdapter tet){
        return tet.dic;
        
    }
}
