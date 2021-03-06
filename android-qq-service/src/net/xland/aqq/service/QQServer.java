package net.xland.aqq.service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import net.xland.util.Converts;
import net.xland.util.Log4jPrintStream;
import net.xland.util.XLandUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

public class QQServer {
	private BlockingQueue<Packet> queue = null;
	private Map<String, SocketChannel> channels = null;
	private Map<String, Map<String, Object>> sessions = null;
	private ThreadPoolExecutor pe = null;

	private PacketSender ps = null;
	private Monitor monitor = null;

	private static String ip = "202.55.10.141";
	private short seq = 0x1123;

	private static Logger logger = LogManager.getLogger(PacketSender.class.getName());
	
	public QQServer() {
		//重定向Err
		System.setErr(new Log4jPrintStream(System.out));
		// 通知恢复
		sessions = new LinkedHashMap<String, Map<String, Object>>();
		// 待发送数据包的队列
		queue = new ArrayBlockingQueue<Packet>((1024 + 512) * 100 * 10);

		// 保存socket的队列
		channels = new LinkedHashMap<String, SocketChannel>();// HashMap

		// 线程池，用于发送socket请求
		int corePoolSize = 200;// 固定200个线程
		int maxPoolSize = 200;
		int maxTaskSize = (1024 + 512) * 100 * 20;// 缓冲队列
		long keepAliveTime = 0L;
		TimeUnit unit = TimeUnit.MILLISECONDS;

		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
				maxTaskSize);
		RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略

		// 创建线程池
		pe = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
				unit, workQueue, handler);

		ps = new PacketSender(this);
		monitor = new Monitor(this);
	}

	public void start() {
		// 启动Monitor
		new Thread(monitor).start();
		;

		// 启动PacketSender
		new Thread(ps).start();

		// for debug
		// addTask(new MobileTask("13682760033"));

		// 启动Jetty
		Server server = new Server(8084);
		ContextHandler context = new ContextHandler();
		context.setContextPath("/");
		context.setResourceBase(".");
		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		context.setHandler(new RootHandler(this));
		server.setHandler(context);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, SocketChannel> getChannels() {
		return this.channels;
	}

	public static void main(String[] args) {
		new QQServer().start();
	}

	public void addReceiver(Receiver receiver) {// 处理接收到的内容
		try {
			this.pe.execute(receiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized Map<String, Object> addTask(Task task) {
		if (seq >= 0xFEFE) {
			seq = 0x1123;
		}
		seq++;
		String sid = task.getSid();
		Map<String, Object> session = null;
//		System.err.println(sid + " A " + task.getClass().getName());
		if (sid == null) {
			while (sid == null || this.channels.containsKey(sid)) { // 生成sid
				sid = XLandUtil.generateSid();
			}
//			System.err.println(sid + " B " + task.getClass().getName());
			session = new HashMap<String, Object>();
			task.setSid(sid);
			session.put("x-sid", sid);
			sessions.put(sid, session);			
			try {
//				System.err.println(sid + " B1 " + task.getClass().getName());
				SocketAddress sa = new InetSocketAddress(ip, 14000); // 新建dc
				SocketChannel sc = SocketChannel.open();
				//System.err.println(sid + " B2 " + task.getClass().getName());
				sc.configureBlocking(false);
				// System.out.println(sc.connect(sa));
				logger.info(sid+" [CREATE SESSION] ");
				synchronized(this.monitor){
					monitor.setWakeup(true);
					//System.err.println(sid + " B3 " + task.getClass().getName());
					QQSelector.selector.wakeup();
//					System.err.println(sid + " B4 " + task.getClass().getName());
					sc.register(QQSelector.selector, SelectionKey.OP_CONNECT);
//					System.err.println(sid + " B5 " + task.getClass().getName());
					monitor.setWakeup(false);
				}
				//System.err.println(sid + " B6 " + task.getClass().getName());
				sc.connect(sa);
//				System.err.println(sid + " B7 " + task.getClass().getName());

				this.channels.put(sid, sc);
				//System.err.println(sid + " B8 " + task.getClass().getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			session = sessions.get(task.getSid());
//			System.err.println(sid + " C " + task.getClass().getName());
		}

		if (session == null) {// invalid sid
			return null;
		}

		session.put("x-seq",
				Converts.bytesToHexString(Converts.short2Byte(seq)));

		try {
			task.setServer(this);
			task.setSequence(seq);
//			System.err.println(sid + " D " + task.getClass().getName());
//			System.err.println(this.pe.getCompletedTaskCount()+"/"+this.pe.getActiveCount()+"/"+this.pe.getQueue().size());
			this.pe.execute(task);
		} catch (Exception e) {
			e.printStackTrace();
			session = null; // 添加任务失败则result为空，所以没有wait
		}

		return session;
	}

	public Map<String, Object> getSession(String sid) {
		return this.sessions.get(sid);
	}

	// 提交发送任务
//	public void submit(String sid, byte[] content) {
//		submit(new Packet(sid, content));
////		try {
////			queue.put(new Packet(sid, content));
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
//	}

	public void submit(final Packet packet) {
//		new Thread(new Runnable(){
//			@Override
//			public void run() {
//				try{
//					Map<String, Object> session = sessions.get(packet.getSid());
//					Object rjo = session.get("x-rejoin-time");
//					int rj = 1;
//					if(rjo!=null){
//						rj = (Integer)rjo;
//						rj++;
//					}
////					logger.info("A-----"+session.get("x-cmd"));
////					logger.info("A-----"+rj);
//					if(rj<5){
//						session.put("x-rejoin-time", rj);
////						if("mobile".equals(session.get("x-cmd"))&&rj==0){
//////							logger.info("B-----"+session.get("x-cmd"));
//////							logger.info("B-----"+rj);
////							Thread.sleep(1000*2); //首次延迟2秒
////						}else {
////							Thread.sleep(1000*rj); //延迟加入
////						}
//						if("mobile".equals(session.get("x-cmd"))){
//							Thread.sleep(1000*rj);
//						}
//						queue.put(packet);
//					}else{//不超过5次
//						releaseSession(packet.getSid());
//						
//						synchronized(session){
//							try{
//								session.notify();    //恢复等待
//							}catch(Exception e){
//								e.printStackTrace();
//							}
//						}						
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//				}				
//			}			
//		}).start();
		
		try {
			if(packet.getSlpt()>0){
				if(packet.getSlpt()<5){//2, 3, 4
					Thread.sleep(packet.getSlpt()*1000);
				}else{
					Map<String, Object> session = sessions.get(packet.getSid());
					releaseSession(packet.getSid());
					
					synchronized(session){
						try{
							session.notify();    //恢复等待
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					return;//不再放入
				}
			}
			queue.put(packet);
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

	public SocketChannel getSocketChannel(String sid) {
		return channels.get(sid);
	}

	public Collection<Map<String, Object>> sessions() {
		return this.sessions.values();
	}

	public void releaseSession(String sid) {
		// 关闭socket
		// remove channel
		// remove session
		logger.info(sid+" [REALEASE-1] " + sessions.get(sid).get("x-seq"));
		try {
			channels.get(sid).close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		channels.remove(sid);
		sessions.remove(sid);
	}

	public void releaseSession(SocketChannel sc) {
		logger.info("*"+" [REALEASE-2A] " + sc);
		String scsid = null;
		for(String sid: channels.keySet()){
			if(channels.get(sid)==sc){
				scsid = sid;
				break;
			}
		}
		
		logger.info(scsid+" [REALEASE-2B] " + sessions.get(scsid).get("x-seq"));
		try {
			channels.get(scsid).close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		channels.remove(scsid);
		sessions.remove(scsid);
	}
}
