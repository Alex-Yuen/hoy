package it.hoyland.sclottery;

import javax.microedition.lcdui.CommandListener;

public class TaskActionFirer implements Runnable {

	private final DefaultImageCanvas dic;
	
	public TaskActionFirer(DefaultImageCanvas dic) {
		this.dic = dic;
	}

	public void run() {
		//DefaultImageCanvas.test(this.dic);
		CommandListener commandListener = null;
		if ((commandListener = dic.getListener()) != null) {
			if (dic.getTaskExecutor() != null && dic.getTaskExecutor().hasException()) {
				commandListener.commandAction(DefaultImageCanvas.cmdFailure, dic); // 登录失败
			} else {
				commandListener.commandAction(DefaultImageCanvas.cmdSuccess, dic); // 登录成功
			}
		} else {
			dic.show(); // 回到上一个页面
		}

	}

}
