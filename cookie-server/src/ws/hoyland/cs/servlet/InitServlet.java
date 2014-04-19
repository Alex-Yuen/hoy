package ws.hoyland.cs.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import ws.hoyland.util.CopiedIterator;

public class InitServlet extends GenericServlet {

	protected boolean flag = false;
	private Timer timer = null;
	private DefaultHttpClient client = new DefaultHttpClient();	

	private static final long serialVersionUID = 407060459247815226L;
	private static String UAG = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	
	public InitServlet() {
		timer = new Timer();
		
		// 加载cookie
		new Thread(new Runnable(){
			@Override
			public void run() {
				
				InputStream is = null;
				BufferedReader br = null;
				
				//导入
				try {
					URL url = this.getClass().getClassLoader().getResource("");
					String xpath = url.getPath();

					xpath = xpath.substring(0, xpath.indexOf("/WEB-INF/"))
							+ "/WEB-INF/cookies.txt";
					xpath = URLDecoder.decode(xpath, "UTF-8");
					System.out.println("xpath=" + xpath);
					is = new FileInputStream(new File(xpath));
					br = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while ((line = br.readLine()) != null) {
						synchronized(Cookies.getInstance()){
							Cookies.getInstance().add(line);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}				

				//初始化client
				try{
					client = new DefaultHttpClient();
					client.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT,
							4000);
					client.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT,
							4000);
					client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);					
					HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
					
					SSLContext sslcontext = SSLContext.getInstance("SSL");
					sslcontext.init(null, new TrustManager[]{
							new X509TrustManager() {
			
								public void checkClientTrusted(
										java.security.cert.X509Certificate[] arg0, String arg1)
										throws CertificateException {
								}
			
								public void checkServerTrusted(
										java.security.cert.X509Certificate[] arg0, String arg1)
										throws CertificateException {
								}
			
								public java.security.cert.X509Certificate[] getAcceptedIssuers() {
									return null;
								}
							}
					}, null);
					
			        SSLSocketFactory ssf = new    SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			        ClientConnectionManager ccm = client.getConnectionManager();
			        SchemeRegistry sr = ccm.getSchemeRegistry();
			        sr.register(new Scheme("https", 443, ssf));
				}catch(Exception e){
					e.printStackTrace();
				}

				flag = true;
				
				// 启动维护线程
				timer = new Timer();
				timer.schedule(new TimerTask() {
					private HttpGet request = null;
					private HttpResponse response =null;
					private HttpEntity entity = null;
					private String resp = null;
					
					@Override
					public void run() {
						try {
							Iterator<String> it = null;
							synchronized(Cookies.getInstance()) {
								it = new CopiedIterator(Cookies.getInstance().iterator());
							}
							
							while(it.hasNext()){
								String line = (String)it.next();
								try{
								//验证
									request = new HttpGet("https://mail.qq.com/cgi-bin/login?vt=passport&vm=wsk&delegate_url=");
			
									request.setHeader("User-Agent", UAG);
									request.setHeader("Content-Type", "text/html");
									request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
									request.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
									request.setHeader("Accept-Encoding", "gzip, deflate");
									request.setHeader("Referer", "https://mail.qq.com/cgi-bin/loginpage");
									request.setHeader("Connection", "keep-alive");				
			
									CookieStore  cs = client.getCookieStore();

									String[] cks = line.split(" ");
									for(int i=0;i<cks.length;i++){
										String[] lsx = cks[i].substring(0, cks[i].length()-1).split("=");
										//System.out.println(cks[i]+"/"+ls.length);
										BasicClientCookie cookie = null;
										if(lsx.length==1){
											cookie = new BasicClientCookie(lsx[0], "");
										}else{
											//System.err.println(lsx[0]+"="+lsx[1]);
											//System.err.println(lsx[1]);
											cookie = new BasicClientCookie(lsx[0], lsx[1]);
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
			
									if(resp.indexOf("frame_html?sid=")==-1){
										synchronized(Cookies.getInstance()){
											Cookies.getInstance().remove(line);
										}
									}
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 0, 60 * 1000 * 10); //10分钟维持并验证一次
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
	}
}
