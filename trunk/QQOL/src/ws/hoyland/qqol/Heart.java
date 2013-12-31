package ws.hoyland.qqol;

import java.util.TimerTask;

/*
 * 处理心跳的机制，对于socketland每个的对象，自动发送心跳
 * 每分钟运行一次
 */
public class Heart extends TimerTask {
//	private ThreadPoolExecutor pool = null;
	
	public Heart(){
//		int tc = 100; //100个分批刷新
//		int corePoolSize = tc;// minPoolSize
//		int maxPoolSize = tc;
//		int maxTaskSize = (1024 + 512) * 100 * 40;// 缓冲队列
//		long keepAliveTime = 0L;
//		TimeUnit unit = TimeUnit.MILLISECONDS;
//
//		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
//				maxTaskSize);
//		RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略
//		
//		// 创建线程池
//		pool = new ThreadPoolExecutor(corePoolSize,
//				maxPoolSize, keepAliveTime, unit,
//				workQueue, handler);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.err.println("Heart beat");
		System.gc();
		//读取SocketLand中的Clients
		//每个发送一个心跳包
		for(String account : Engine.getInstance().getChannels().keySet()){
			if(Engine.getInstance().getAcccounts().get(account).get("login")!=null){//已经登录的才发送心跳包
				Engine.getInstance().addTask((new Task(Task.TYPE_0058, account)));
			}
		}
	}
}