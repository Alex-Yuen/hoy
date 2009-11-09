package it.hoyland.sclottery;

public class TaskExecutor implements Runnable {

	private Task task;
	private Throwable exception;

	public TaskExecutor() {
		// TODO Auto-generated constructor stub
	}

	public boolean hasException() {
		return exception != null;
	}

	public void setTask(Task task) {
		this.task = task;
		this.exception = null;
	}

	public void run() {
		this.exception = null;
		if (this.task != null) {
			try {
				this.task.execute();
			} catch (Throwable throwable) {
				exception = throwable;
			}
		}
	}

}
