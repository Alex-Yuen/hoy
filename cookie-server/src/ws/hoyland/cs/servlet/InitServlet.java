package ws.hoyland.cs.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ws.hoyland.cs.Task;
import ws.hoyland.cs.XTask;
import ws.hoyland.util.CopiedIterator;
import ws.hoyland.util.YDM;

public class InitServlet extends HttpServlet {

	protected boolean flag = false;
	private Timer timer = null;
	private DefaultHttpClient client = new DefaultHttpClient();
	private List<String> accounts = new ArrayList<String>();
	private int score = 0;
	private String xpath = null;

	private static final long serialVersionUID = 407060459247815226L;
//	private static String UAG = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
//	private static Random random = new Random();
	private ThreadPoolExecutor pool = null;
	private ThreadPoolExecutor poolx = null;
	public static int size;//需维护的数量
	private int aidx = -1;
	private boolean login = false;
	private boolean writed = false;
	
	private static final Logger logger = LogManager.getRootLogger();

	public InitServlet() {
	}

	@Override
	public void init(final ServletConfig config) {
		logger.info("InitServlet -> Servlet 初始化(1).");
		try {
			super.init(config);
		} catch (Exception e) {
			e.printStackTrace(logger.getStream(Level.INFO));
		}
		
		size = Integer.parseInt(config
				.getInitParameter("size"));
		logger.info("InitServlet -> size:"+size);
		
		try {
			URL url = this.getClass().getClassLoader().getResource("");
			xpath = url.getPath();

			xpath = xpath.substring(0, xpath.indexOf("/WEB-INF/"));
			xpath = URLDecoder.decode(xpath, "UTF-8");
			logger.info("InitServlet -> xpath=" + xpath);
		} catch (Exception e) {
			e.printStackTrace(logger.getStream(Level.INFO));
		}

		//初始化线程池
		try{
			int tc = Integer.parseInt(config.getInitParameter("threadcount"));
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
		}catch(Exception e){
			e.printStackTrace(logger.getStream(Level.INFO));
		}

		//初始化线程池X
		try{
			int tc = Integer.parseInt(config.getInitParameter("threadcountx"));
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
		}catch(Exception e){
			e.printStackTrace(logger.getStream(Level.INFO));
		}
		
		// 初始化client
		try {
			client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);
			client.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 4000);
			client.getParams().setParameter(
					ClientPNames.HANDLE_REDIRECTS, false);
			HttpClientParams.setCookiePolicy(client.getParams(),
					CookiePolicy.BROWSER_COMPATIBILITY);

//			HttpHost proxy = new HttpHost("127.0.0.1", 8888);
//			
//			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
//					proxy);
			
			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null,
					new TrustManager[] { new X509TrustManager() {

						public void checkClientTrusted(
								java.security.cert.X509Certificate[] arg0,
								String arg1)
								throws CertificateException {
						}

						public void checkServerTrusted(
								java.security.cert.X509Certificate[] arg0,
								String arg1)
								throws CertificateException {
						}

						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return null;
						}
					} }, null);

			SSLSocketFactory ssf = new SSLSocketFactory(sslcontext,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
		} catch (Exception e) {
			e.printStackTrace(logger.getStream(Level.INFO));
		}
		
//		logger.info("Servlet 初始化(1).");
//		logger.info(Proxy.class.getName());
//        Class clazz=Proxy.class;
//        Class clazz1=Proxy.getProxyClass(Collection.class.getClassLoader(), Collection.class);
//        logger.info(clazz);
//        logger.info(clazz1);
        
		timer = new Timer();

		try {
//			logger.info("xa");
//			YDM y = (YDM) Native.loadLibrary(xpath.substring(1)
//					+ "/WEB-INF/lib/yundamaAPI.dll", YDM.class);
			//YDM y = (YDM)JNALoader.load("/WEB-INF/lib/yundamaAPI.dll", YDM.class);
//			logger.info("xb");
			
			YDM.INSTANCE.YDM_SetAppInfo(355, "7fa4407ca4d776d949d2d7962f1770cc");
			int userID = YDM.INSTANCE.YDM_Login(
					config.getInitParameter("username"),
					config.getInitParameter("password"));
			score = YDM.INSTANCE.YDM_GetBalance(
					config.getInitParameter("username"),
					config.getInitParameter("password"));
			if (userID < 0) {
				logger.info("InitServlet -> 登录云打码平台失败:" + userID);
			} else {
				login = true;
				logger.info("InitServlet -> 登录云打码平台成功:" + userID + "=" + score);
			}
		} catch (Exception e) {
			e.printStackTrace(logger.getStream(Level.INFO));
		}

		// 加载cookie
		new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("InitServlet -> Servlet 初始化(2).");
				InputStream is = null;
				BufferedReader br = null;

				// 导入

				// 加载cookies
				try {
					logger.info("InitServlet -> 加载cookies.txt...");
					is = new FileInputStream(new File(xpath
							+ "/WEB-INF/cookies.txt"));
					br = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while ((line = br.readLine()) != null) {
						String acc = line.substring(line.indexOf("qm_username=")+12);
						acc = acc.substring(0, acc.indexOf(";"));
//						logger.info("acc="+acc);						
						synchronized (Cookies.getInstance()) {
							if(!Cookies.getInstance().containsKey(acc)){
								logger.info("InitServlet -> 导入Cookie:"+acc);
								Cookies.getInstance().put(acc, line);
							}else{
								logger.info("InitServlet -> 内存Cookies已经包含:"+acc+", 不再加入");
							}
						}
						// logger.info("adding cookies:" + line);
					}
					logger.info("InitServlet -> 加载cookies.txt...完毕");
				} catch (Exception e) {
					e.printStackTrace(logger.getStream(Level.INFO));
				} finally {
					try {
						if (br != null) {
							br.close();
						}
					} catch (Exception e) {
						e.printStackTrace(logger.getStream(Level.INFO));
					}

					try {
						if (is != null) {
							is.close();
						}
					} catch (Exception e) {
						e.printStackTrace(logger.getStream(Level.INFO));
					}

				}

				// 加载帐号
				try {
					logger.info("InitServlet -> 加载accounts.txt...");
					is = new FileInputStream(new File(xpath
							+ "/WEB-INF/accounts.txt"));
					br = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while ((line = br.readLine()) != null) {
						synchronized (Cookies.getInstance()) {
							accounts.add(line);
						}
						// logger.info("adding cookies:" + line);
					}
					logger.info("InitServlet -> 加载accounts.txt...完毕");
				} catch (Exception e) {
					e.printStackTrace(logger.getStream(Level.INFO));
				} finally {
					try {
						if (br != null) {
							br.close();
						}
					} catch (Exception e) {
						e.printStackTrace(logger.getStream(Level.INFO));
					}

					try {
						if (is != null) {
							is.close();
						}
					} catch (Exception e) {
						e.printStackTrace(logger.getStream(Level.INFO));
					}

				}

				flag = true;
				
				//如果小于既定个数，则加入Task
				int csize = Cookies.getInstance().size();
				logger.info("InitServlet -> current size:"+csize);
				if (csize < size) {
					for (int i = 0; i < size - csize; i++) {						
						fill();
					}
				}				

				logger.info("InitServlet -> Servlet 初始化结束");

				logger.info("InitServlet -> 启动维护线程");
				// 启动维护线程
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							logger.info("维护线程 -> 正在验证Cookie...");
							Iterator<String> it = null;
							synchronized (Cookies.getInstance()) {
								it = new CopiedIterator(Cookies.getInstance().values()
										.iterator());
							}

							while (it.hasNext()&&flag) {
								String line = (String) it.next();
								try {
									XTask task = new XTask(InitServlet.this, line, writed);
									poolx.execute(task);
								} catch (Exception e) {
									e.printStackTrace(logger.getStream(Level.INFO));
								}
							}
						} catch (Exception e) {
							e.printStackTrace(logger.getStream(Level.INFO));
						}finally{
							writed = true;
							//logger.info("维护线程:验证完毕");
						}
					}
				}, 0, 60 * 1000 * 10); // 10分钟维持并验证一次
			}
		}).start();
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
	}

	@Override
	public void destroy() {
		super.destroy();
		flag = false;
		if (timer != null) {
			timer.cancel();
		}

		try{
			pool.shutdown();
		}catch(Exception e){
			e.printStackTrace(logger.getStream(Level.INFO));
		}
		
		try{
			poolx.shutdown();
		}catch(Exception e){
			e.printStackTrace(logger.getStream(Level.INFO));
		}
		
		// 保存cookie
		BufferedWriter output = null;
		try{		
			output = new BufferedWriter(new FileWriter(new File(xpath
					+ "/WEB-INF/cookies.txt")));

			logger.info("InitServlet -> Saving...");
			for(String cookie:Cookies.getInstance().values()){
				output.write(cookie+"\r\n");
			}
			
			logger.info("InitServlet -> Saved "+Cookies.getInstance().size());
			
			output.flush();
		}catch(Exception e){
			e.printStackTrace(logger.getStream(Level.INFO));
		}finally{
			if(output!=null){
				try{
					output.close();
				}catch(Exception e){
					e.printStackTrace(logger.getStream(Level.INFO));
				}
			}
		}
	}
	
	public synchronized void repair(String line, boolean xwrited){
		if(!flag||!login||line==null){
			return;
		}
		XTask task = new XTask(this, line, xwrited);//如果此线程是前面的首次维护线程引发，则也同样继承
		poolx.execute(task);
	}
	
	public synchronized void fill(String line){
		if(!flag||!login||line==null){
			return;
		}
		
		String[] accs = line.split("----");
		int csize = Cookies.getInstance().size();
		if(csize<size&&!Cookies.getInstance().containsKey(accs[0])){
			Task task = new Task(this, line);
			pool.execute(task);
		}
	}
	
	public synchronized void fill(){
		if(!flag||!login||aidx>=accounts.size()-1){
			return; //已经停止
		}
		
		try{			
			//logger.info("fill");
			int csize = Cookies.getInstance().size();
			if(csize<size){
				String account = null;
				String[] accs = null;
				do{
					aidx++;
//					if(aidx==accounts.size()){
//						aidx = 0;
//					}
					if(aidx>=accounts.size()){
						logger.info("InitServlet -> 帐号用完，不再打码");
						break;
					}
					account = accounts.get(aidx);
					accs = account.split("----");
					//logger.info(aidx+"="+accs[0]);
				}while(Cookies.getInstance().containsKey(accs[0]));
				
				//logger.info("run");
				if(account!=null){
					Task task = new Task(this, account);
					pool.execute(task);
				}
			}//否则，无需再次打码获取Cookie			
		}catch(Exception e){
			e.printStackTrace(logger.getStream(Level.INFO));
		}
	}
}
