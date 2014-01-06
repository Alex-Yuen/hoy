package ws.hoyland.qqol;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import ws.hoyland.util.CopiedIterator;
import ws.hoyland.util.EngineMessage;

/*
 * 处理心跳的机制，对于socketland每个的对象，自动发送心跳
 * 每分钟运行一次
 */
public class Heart extends TimerTask {
	private ThreadPoolExecutor pool = null;
//	private Timer timer = new Timer();
	
	public Heart(){
		try{
			int tc = 2; //100个分批刷新
			int corePoolSize = tc;// minPoolSize
			int maxPoolSize = tc;
			int maxTaskSize = (1024 + 512) * 100 * 40;// 缓冲队列
			long keepAliveTime = 0L;
			TimeUnit unit = TimeUnit.MILLISECONDS;
	
			BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
					maxTaskSize);
			RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略
			
			// 创建线程池
			pool = new ThreadPoolExecutor(corePoolSize,
					maxPoolSize, keepAliveTime, unit,
					workQueue, handler);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		System.err.println("Heart beat["+Util.format(new Date())+"]:"+Engine.getInstance().getActiveCount()+"/"+Engine.getInstance().getQueueCount()+"----"+pool.getActiveCount()+"/"+pool.getQueue().size());
		System.gc();
		//读取SocketLand中的Clients
		//每个发送一个心跳包
		long current = System.currentTimeMillis();
		Iterator<String> it = null;
		synchronized(Engine.getInstance().getChannels()) {
			it = new CopiedIterator(Engine.getInstance().getChannels().keySet().iterator());
		}
		  
		//Iterator<String> it = Engine.getInstance().getChannels().keySet().iterator();
		int idx = 0;
//		float delay = 60000f/Engine.getInstance().getChannels().size();
		while(it.hasNext()){
			String account = (String)it.next();
			float itv = 1.5f;
//			if(Engine.getInstance().getAcccounts().get(account).get("login")==null){ //若是未登录，则缩短判断时间
//				itv = 1.0f;
//			}
			if((current-Long.parseLong(new String(Engine.getInstance().getAcccounts().get(account).get("lastatv")))>=1000*60*itv)){//重新登录
				//it.remove();
				//1.5 分钟
				tf(account);
				info(account, "超时, 重新登录");
				synchronized(Engine.getInstance().getChannels()) {
					try {
						Engine.getInstance().getChannels().get(account).close();
						info(account, "channel close");
					} catch (IOException e) {
						e.printStackTrace();
					}
					Engine.getInstance().getChannels().remove(account);
				}
//				if(Engine.getInstance().getAcccounts().get(account).get("login")!=null){
//					//已经登录的，登录完后不再next
//					Engine.getInstance().getAcccounts().get(account).put("landt", "T".getBytes());
//				}
				Engine.getInstance().getAcccounts().get(account).remove("login");
				
				//Engine.getInstance().addTask((new Task(Task.TYPE_0825, account)));
			}else{
				if(Engine.getInstance().getAcccounts().get(account).get("login")!=null){//已经登录的才发送心跳包
					//Engine.getInstance().addTask((new Beater(account, (int)delay*idx)));
					Engine.getInstance().send(new TaskSender(new Task(Task.TYPE_0058, account)), 5*idx);
					//pool.execute(new Beater(account));
					//不加入pool，避免pool过于阻塞
					//timer.schedule(new Beater(account), (delay++%20)*1000);
				}
			}
			idx++;
//			if(Engine.getInstance().getAcccounts().get(account).get("login")!=null){//已经登录的才发送心跳包
//				if(current-Long.parseLong(new String(Engine.getInstance().getAcccounts().get(account).get("lastatv")))>=1000*60*2){//重新登录
//					//it.remove();
//						try {
//							Engine.getInstance().getChannels().get(account).close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					synchronized(Engine.getInstance().getChannels()) {
//						Engine.getInstance().getChannels().remove(account);
//					}
//					Engine.getInstance().getAcccounts().get(account).remove("login");
//					Engine.getInstance().addTask((new Task(Task.TYPE_0825, account)));
//				}else{
//					Engine.getInstance().addTask((new Beater(account)));
//				}
//			}
		}
	}
	
	private void tf(String account) {//task finish
		int id = Integer.parseInt(new String(Engine.getInstance().getAcccounts().get(account).get("id")));
		EngineMessage message = new EngineMessage();
		message.setTid(id);
		message.setType(EngineMessageType.IM_TF);
		String tm = Util.format(new Date());		
		System.err.println("["+account+"]"+"TF任务完成"+"("+tm+")");
		Engine.getInstance().fire(message);		
	}
	
	private void info(String account, String info){
		int id = Integer.parseInt(new String(Engine.getInstance().getAcccounts().get(account).get("id")));
		
		EngineMessage message = new EngineMessage();
		message.setTid(id);
		message.setType(EngineMessageType.IM_INFO);
		message.setData(info);
		
		String tm = Util.format(new Date());		
		System.err.println("["+account+"]"+info+"("+tm+")");
		Engine.getInstance().fire(message);
	}
	
}

//class Beater implements Runnable{
//	private String account;	
////	private int id;
//	
//	public Beater(String account){
//		this.account = account;
////		this.id = Integer.parseInt(new String(Engine.getInstance().getAcccounts().get(account).get("id")));
//	}
//
//	@Override
//	public void run() {
////		try{
////			Thread.sleep(delay);
////		}catch(Exception e){
////			e.printStackTrace();
////		}
//		byte x = 0;
//		byte itv = 0;
//		Engine.getInstance().getAcccounts().get(account).remove("heart");
//		while(Engine.getInstance().getAcccounts().get(account).get("heart")==null&&x<5){
//			x++;
//			itv += Math.pow(2, x);
//			Engine.getInstance().addTask((new Task(Task.TYPE_0058, account)));
//			try{
//				Thread.sleep(1000*itv);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//		/** 暂不做处理
//		if(Engine.getInstance().getAcccounts().get(account).get("heart")==null){//还没反应，考虑断开线程，重新登录
//			info("离线");
//			Engine.getInstance().addTask((new Task(Task.TYPE_0062, account)));
//			
//			tf();
//			try {
//						Engine.getInstance().getChannels().get(account).close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//			synchronized(Engine.getInstance().getChannels()) {
//				Engine.getInstance().getChannels().remove(account);
//			}
//			Engine.getInstance().getAcccounts().get(account).remove("login");
//			Engine.getInstance().addTask((new Task(Task.TYPE_0825, account)));
//		}**/
//	}
//}