package it.hoyland.sclottery;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;

public class DefaultImageCanvas extends ImageCanvas {

	private TaskExecutor executor;
	private Thread thd;

	public static final Command cmdSuccess = new Command("Success", 4, 0);
	public static final Command cmdFailure = new Command("Failure", 4, 0);

	public DefaultImageCanvas(Display display) {
		super(display);

	}

	public final void setExecutor(TaskExecutor executor) {
		this.executor = executor;
		
	}

	public final TaskExecutor getTaskExecutor(){
		return this.executor;
		
	}
	
	protected final void showNotify() {
		super.showNotify();
		if (this.executor != null) {
			if (this.thd == null) {
				this.thd = new Thread(new TaskExecutorAdapter(this, this.executor));
				this.thd.start();
			}
		} else {
			getDisplay().callSerially(new TaskActionFirer(this));
		}
	}

	public void stop() {
		this.thd = null;
		
	}
	
}
