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
		this.te.run();
		new TaskCleanner(this).run();
		// dic.getDisplay().callSerially(this.te);
        // dic.getDisplay().callSerially(new TaskCleanner(this));
        
	}

    public DefaultImageCanvas getDefaultImageCanvas(){
        return this.dic;
        
    }
}
