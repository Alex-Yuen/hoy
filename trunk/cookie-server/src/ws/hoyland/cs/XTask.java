package ws.hoyland.cs;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ws.hoyland.cs.servlet.Cookies;
import ws.hoyland.cs.servlet.InitServlet;

public class XTask implements Runnable {

	private String line = null;
	private InitServlet servlet = null;
	private boolean writed = false;
	private static String UAG = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
//	private static Random random = new Random();
	private static final Logger logger = LogManager.getRootLogger();

	public XTask(InitServlet servlet, String account, boolean writed) {
		this.servlet = servlet;
		this.line = account;
		this.writed = writed;
	}

	@Override
	public void run() {
		String acc = line.substring(line.indexOf("qm_username=")+12);
		acc = acc.substring(0, acc.indexOf(";"));
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			
			HttpGet request = null;
			HttpResponse response = null;
			HttpEntity entity = null;
			String resp = null;
						
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

//				HttpHost proxy = new HttpHost("127.0.0.1", 8888);
//				
//				client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
//						proxy);
				
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
				logger.info(acc+" -> \r\n");
				e.printStackTrace(logger.getStream(Level.INFO));
			}

			//client.getCookieStore().clear();

			// 验证
			logger.info(acc+" -> 正在维护");
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
				// logger.info(cks[i]+"/"+ls.length);
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
				logger.info(acc+" -> removing cookies("+Cookies.getInstance().size()+"):"
						+ acc);
				synchronized (Cookies.getInstance()) {
					if(Cookies.getInstance().containsKey(acc)){
						Cookies.getInstance().remove(acc);
					}
				}
				logger.info(acc+" -> removing cookies("+Cookies.getInstance().size()+"):"
						+ acc);
				if(!writed){
					servlet.fill();//首次维护才会继续打码
				}
			}
		} catch (Exception e) {
			if(e.getMessage().indexOf("Read timed out")!=-1){
				logger.info(acc+" -> \r\n");
				e.printStackTrace(logger.getStream(Level.INFO));
			}else{
				servlet.repair(this.line, writed);
			}
		}
	}
}
