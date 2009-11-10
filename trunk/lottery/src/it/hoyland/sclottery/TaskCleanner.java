package it.hoyland.sclottery;

public class TaskCleanner implements Runnable {

	private final TaskExecutorAdapter tet;
	
	public TaskCleanner(TaskExecutorAdapter tet) {
		this.tet = tet;
	}

	public final void run() {
		// System.out.println("f3");
		// 终止线程运行
		DefaultImageCanvas dic = this.tet.getDefaultImageCanvas();
		new TaskActionFirer(dic).run();
		dic.stop();
		
//		CommandListener commandListener = null;
//		if ((commandListener = dic.getListener()) != null) {
//			if (dic.getTaskExecutor() != null && dic.getTaskExecutor().hasException()) {
//				commandListener.commandAction(DefaultImageCanvas.cmdFailure, dic); // 登录失败
//			} else {
//				commandListener.commandAction(DefaultImageCanvas.cmdSuccess, dic); // 登录成功
//			}
//		} else {
//			dic.show(); // 回到上一个页面
//		}
		
//		DefaultImageCanvas.test(TaskExecutorAdapter.getDefaultImageCavas(this.tet), null);
//		DefaultImageCanvas.test(TaskExecutorAdapter.getDefaultImageCavas(this.tet));

	}

}
