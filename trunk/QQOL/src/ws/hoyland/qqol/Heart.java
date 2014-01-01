package ws.hoyland.qqol;

import java.util.TimerTask;

import ws.hoyland.util.EngineMessage;

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
		long current = System.currentTimeMillis();
		for(String account : Engine.getInstance().getChannels().keySet()){
			if(Engine.getInstance().getAcccounts().get(account).get("login")!=null){//已经登录的才发送心跳包
				if(current-Long.parseLong(new String(Engine.getInstance().getAcccounts().get(account).get("lastatv")))>=1000*60*2){//重新登录
					Engine.getInstance().getChannels().remove(account);
					Engine.getInstance().getAcccounts().get(account).remove("login");
					Engine.getInstance().addTask((new Task(Task.TYPE_0825, account)));
				}else{
					Engine.getInstance().addTask((new Beater(account)));
				}
			}
		}
	}
}

class Beater implements Runnable{
	private String account;	
	private int id;
	
	public Beater(String account){
		this.account = account;
		this.id = Integer.parseInt(new String(Engine.getInstance().getAcccounts().get(account).get("id")));
	}

	@Override
	public void run() {
		byte x = 0;
		Engine.getInstance().getAcccounts().get(account).remove("heart");
		while(Engine.getInstance().getAcccounts().get(account).get("heart")==null&&x<10){
			x++;
			Engine.getInstance().addTask((new Task(Task.TYPE_0058, account)));
			try{
				Thread.sleep(1000*x);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(Engine.getInstance().getAcccounts().get(account).get("heart")==null){//还没反应，考虑断开线程，重新登录
			info("离线");
			Engine.getInstance().addTask((new Task(Task.TYPE_0062, account)));
			
			tf();
			
			Engine.getInstance().getChannels().remove(account);
			Engine.getInstance().getAcccounts().get(account).remove("login");
			Engine.getInstance().addTask((new Task(Task.TYPE_0825, account)));
		}
	}
	
	private void tf() {//task finish
		EngineMessage message = new EngineMessage();
		message.setTid(this.id);
		message.setType(EngineMessageType.IM_TF);
		
		Engine.getInstance().fire(message);		
	}
	
	private void info(String info){
		EngineMessage message = new EngineMessage();
		message.setTid(this.id);
		message.setType(EngineMessageType.IM_INFO);
		message.setData(info);
		
		Engine.getInstance().fire(message);
	}
	
}