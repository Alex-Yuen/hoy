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

		if (sid == null) {
			while (sid == null || this.channels.containsKey(sid)) { // 生成sid
				sid = XLandUtil.generateSid();
			}
			session = new HashMap<String, Object>();
			task.setSid(sid);
			session.put("x-sid", sid);
			sessions.put(sid, session);
			logger.info(sid+" [CREATE SESSION] ");
			try {
				SocketAddress sa = new InetSocketAddress(ip, 14000); // 新建dc
				SocketChannel sc = SocketChannel.open();
				sc.configureBlocking(false);
				// System.out.println(sc.connect(sa));

				monitor.setWakeup(true);
				QQSelector.selector.wakeup();
				sc.register(QQSelector.selector, SelectionKey.OP_CONNECT);
				monitor.setWakeup(false);
				sc.connect(sa);

				this.channels.put(sid, sc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			session = sessions.get(task.getSid());
		}

		if (session == null) {// invalid sid
			return null;
		}

		session.put("x-seq",
				Converts.bytesToHexString(Converts.short2Byte(seq)));

		try {
			task.setServer(this);
			task.setSequence(seq);
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
	public void submit(String sid, byte[] content) {
		try {
			queue.put(new Packet(sid, content));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void submit(Packet packet) {
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					Map<String, Object> session = sessions.get(packet.getSid());
					Object rjo = session.get("x-rejoin-time");
					int rj = 1;
					if(rjo!=null){
						rj = (Integer)rjo;
						rj++;
					}
					
					if(rj<6){
						session.put("x-rejoin-time", rj);
						Thread.sleep(1000*rj); //延迟加入
						queue.put(packet);
					}else{//不超过5次
						releaseSession(packet.getSid());
						
						synchronized(session){
							try{
								session.notify();    //恢复等待
							}catch(Exception e){
								e.printStackTrace();
							}
						}						
					}
				}catch(Exception e){
					e.printStackTrace();
				}				
			}			
		}).start();
//		try {
//			queue.put(packet);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
		try {
			channels.get(sid).close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(sid+" [REALEASE]");
		}

		channels.remove(sid);
		sessions.remove(sid);
	}
}
