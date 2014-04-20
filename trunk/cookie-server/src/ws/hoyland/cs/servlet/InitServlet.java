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
import java.util.Random;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import ws.hoyland.cs.Task;
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
	private static String UAG = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	private static Random random = new Random();
	private ThreadPoolExecutor pool = null;
	private int size;//需维护的数量
	private boolean tmflag = false;

	public InitServlet() {
	}

	@Override
	public void init(final ServletConfig config) {
		System.out.println("Servlet 初始化(1).");
		try {
			super.init(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		size = Integer.parseInt(config
				.getInitParameter("size"));
		System.out.println("size:"+size);
		
		try {
			URL url = this.getClass().getClassLoader().getResource("");
			xpath = url.getPath();

			xpath = xpath.substring(0, xpath.indexOf("/WEB-INF/"));
			xpath = URLDecoder.decode(xpath, "UTF-8");
			System.out.println("xpath=" + xpath);
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		
//		System.out.println("Servlet 初始化(1).");
//		System.out.println(Proxy.class.getName());
//        Class clazz=Proxy.class;
//        Class clazz1=Proxy.getProxyClass(Collection.class.getClassLoader(), Collection.class);
//        System.out.println(clazz);
//        System.out.println(clazz1);
        
		timer = new Timer();

		try {
//			System.out.println("xa");
//			YDM y = (YDM) Native.loadLibrary(xpath.substring(1)
//					+ "/WEB-INF/lib/yundamaAPI.dll", YDM.class);
			//YDM y = (YDM)JNALoader.load("/WEB-INF/lib/yundamaAPI.dll", YDM.class);
//			System.out.println("xb");
			
			YDM.INSTANCE.YDM_SetAppInfo(355, "7fa4407ca4d776d949d2d7962f1770cc");
			int userID = YDM.INSTANCE.YDM_Login(
					config.getInitParameter("username"),
					config.getInitParameter("password"));
			score = YDM.INSTANCE.YDM_GetBalance(
					config.getInitParameter("username"),
					config.getInitParameter("password"));
			if (userID < 0) {
				System.out.println("登录云打码平台失败:" + userID);
			} else {
				System.out.println("登录云打码平台成功:" + userID + "=" + score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 加载cookie
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Servlet 初始化(2).");
				InputStream is = null;
				BufferedReader br = null;

				// 导入

				// 加载cookies
				try {
					System.out.println("加载cookies.txt...");
					is = new FileInputStream(new File(xpath
							+ "/WEB-INF/cookies.txt"));
					br = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while ((line = br.readLine()) != null) {
						synchronized (Cookies.getInstance()) {
							Cookies.getInstance().add(line);
						}
						// System.out.println("adding cookies:" + line);
					}
					System.out.println("加载cookies.txt...完毕");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null) {
							br.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						if (is != null) {
							is.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				// 加载帐号
				try {
					System.out.println("加载accounts.txt...");
					is = new FileInputStream(new File(xpath
							+ "/WEB-INF/accounts.txt"));
					br = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while ((line = br.readLine()) != null) {
						synchronized (Cookies.getInstance()) {
							accounts.add(line);
						}
						// System.out.println("adding cookies:" + line);
					}
					System.out.println("加载accounts.txt...完毕");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null) {
							br.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						if (is != null) {
							is.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				//如果小于既定个数，则加入Task
				int csize = Cookies.getInstance().size();
				System.out.println("csize:"+csize);
				if (csize < size) {
					for (int i = 0; i < size - csize; i++) {						
						fill();
					}
				}
				
				flag = true;

				System.out.println("Servlet 初始化结束");

				System.out.println("启动维护线程");
				// 启动维护线程
				timer = new Timer();
				timer.schedule(new TimerTask() {
					private HttpGet request = null;
					// private HttpPost post = null;
					// private StringEntity se = null;
					private HttpResponse response = null;
					private HttpEntity entity = null;
					private String resp = null;
					
					@Override
					public void run() {
						if(tmflag){
							return;
						}else{
							tmflag = true;
						}
						
						try {
							System.out.println("维护线程:正在验证Cookie...");
							Iterator<String> it = null;
							synchronized (Cookies.getInstance()) {
								it = new CopiedIterator(Cookies.getInstance()
										.iterator());
							}

							int idx = 0;
							while (it.hasNext()) {
								String line = (String) it.next();
								try {
									// 验证
									System.out.println("维护:"+idx);
									client.getCookieStore().clear();
									request = new HttpGet(
											"https://mail.qq.com/cgi-bin/login?vt=passport&vm=wsk&delegate_url=");

									request.setHeader("User-Agent", UAG);
									request.setHeader("Content-Type",
											"text/html");
									request.setHeader("Accept",
											"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
									request.setHeader("Accept-Language",
											"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
									request.setHeader("Accept-Encoding",
											"gzip, deflate");
									request.setHeader("Referer",
											"https://mail.qq.com/cgi-bin/loginpage");
									request.setHeader("Connection",
											"keep-alive");

									CookieStore cs = client.getCookieStore();

									String[] cks = line.split(" ");
									for (int i = 0; i < cks.length; i++) {
										String[] lsx = cks[i].substring(0,
												cks[i].length() - 1).split("=");
										// System.out.println(cks[i]+"/"+ls.length);
										BasicClientCookie cookie = null;
										if (lsx.length == 1) {
											cookie = new BasicClientCookie(
													lsx[0], "");
										} else {
											// System.err.println(lsx[0]+"="+lsx[1]);
											// System.err.println(lsx[1]);
											cookie = new BasicClientCookie(
													lsx[0], lsx[1]);
										}
										cookie.setDomain("mail.qq.com");
										cookie.setPath("/");

										cs.addCookie(cookie);
									}
									client.setCookieStore(cs);

									response = client.execute(request);
									entity = response.getEntity();

									resp = EntityUtils.toString(entity);

									try {
										if (entity != null) {
											EntityUtils.consume(entity);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									if (request != null) {
										request.releaseConnection();
										request.abort();
									}

									if (resp.indexOf("frame_html?sid=") == -1) {
										synchronized (Cookies.getInstance()) {
											Cookies.getInstance().remove(line);
										}
										System.out.println("removing cookies:"
												+ line);
										
										fill();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								idx++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							tmflag = false;
							System.out.println("维护线程:验证完毕");
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
			e.printStackTrace();
		}
		
		// 保存cookie
		BufferedWriter output = null;
		try{		
			output = new BufferedWriter(new FileWriter(new File(xpath
					+ "/WEB-INF/cookies.txt")));

			for(String cookie:Cookies.getInstance()){
				output.write(cookie+"\r\n");
			}
			
			output.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(output!=null){
				try{
					output.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public void fill(){
		try{			
			int csize = Cookies.getInstance().size();
			if(csize<size){
				int idx = random.nextInt(accounts.size());
				String account = accounts.get(idx);
				Task task = new Task(this, account);
				pool.execute(task);
			}//否则，无需再次打码获取Cookie			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
