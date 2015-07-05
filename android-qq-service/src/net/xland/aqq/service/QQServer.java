package net.xland.aqq.service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import net.xland.util.XLandUtil;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

public class QQServer {
	private BlockingQueue<Packet> queue = null;
	private HashMap<String, DatagramChannel> channels = null;
	private HashMap<Integer, StringBuffer> futures = null;
	private ThreadPoolExecutor pe = null;

	private PacketSender ps = null;
	private Monitor monitor = null;
	
	private static String ip = "202.55.10.141";
	private int seq = 0x1123;
	
	public QQServer(){
		//通知恢复
		futures =  new LinkedHashMap<Integer, StringBuffer>();
		//待发送数据包的队列
		queue = new ArrayBlockingQueue<Packet>((1024 + 512) * 100 * 10);
		
		//保存socket的队列
		channels = new LinkedHashMap<String, DatagramChannel>();//HashMap
		
		//线程池，用于发送socket请求
		int corePoolSize = 200;// 固定200个线程
		int maxPoolSize = 200;
		int maxTaskSize = (1024 + 512) * 100 * 20;// 缓冲队列
		long keepAliveTime = 0L;
		TimeUnit unit = TimeUnit.MILLISECONDS;

		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
				maxTaskSize);
		RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略
		
		// 创建线程池
		pe = new ThreadPoolExecutor(corePoolSize,
				maxPoolSize, keepAliveTime, unit,
				workQueue, handler);
		
		ps = new PacketSender(this);
		monitor = new Monitor(this);
	}
	
	public void start(){
		//启动Monitor
		new Thread(monitor).start();;
		
		//启动PacketSender
		new Thread(ps).start();;
		
		//启动Jetty
	    Server server = new Server(8084);
	    ContextHandler context = new ContextHandler();  
        context.setContextPath("/");  
        context.setResourceBase(".");  
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setHandler(new RootHandler(this));        
        server.setHandler(context);
        try{
	        server.start();  
	        server.join();
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	public HashMap<String, DatagramChannel> getChannels(){
		return this.channels;
	}
	
	public static void main(String[] args) {
		new QQServer().start();
	}

	public void addReceiver(Receiver receiver) {//处理接收到的内容
		try{
			this.pe.execute(receiver);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public StringBuffer addTask(Task task) {
		if(seq>=0xFEFE){
			seq = 0x1123;
		}
		seq++;
		StringBuffer future = new StringBuffer();
		try{
			task.setServer(this);
			task.setSequence(seq);
			this.pe.execute(task);
		}catch(Exception e){
			e.printStackTrace();
			future = null; //添加任务失败则result为空，所以没有wait
		}
		
		if(future!=null){
			this.futures.put(seq,  future);
		}
		
		return future;
	}

	public StringBuffer getFuture(int seq){ //用于恢复等待
		StringBuffer future = this.futures.get(seq);
//		if(future!=null){
//			this.futures.remove(seq);
//		}
		return future;
	}
	
	//提交发送任务
	public void submit(String sid, byte[] content) {
		try {
			queue.put(new Packet(sid, content));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Packet takePacket() {
		Packet packet = null;
		try {
			packet = queue.take();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packet;
	}

	public DatagramChannel getDatagramChannel(String sid) {
		DatagramChannel dc = null;		
		if(sid==null){
			try{
				SocketAddress sa = new InetSocketAddress(ip, 14000);
				dc = DatagramChannel.open();
				dc.configureBlocking(false);
				dc.connect(sa);
				
				monitor.setWakeup(true);
				QQSelector.selector.wakeup();
				dc.register(QQSelector.selector, SelectionKey.OP_READ);
				monitor.setWakeup(false);
				
				if(dc!=null){
					while(sid==null||this.channels.containsKey(sid)){
						sid = XLandUtil.generateSid();
					}
					this.channels.put(sid, dc);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			dc = channels.get(sid);
		}
		return dc;
	}
}
