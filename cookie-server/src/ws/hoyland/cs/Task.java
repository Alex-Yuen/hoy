package ws.hoyland.cs;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import ws.hoyland.cs.servlet.Cookies;
import ws.hoyland.cs.servlet.InitServlet;
import ws.hoyland.util.Converts;
import ws.hoyland.util.YDM;

public class Task implements Runnable {

	private String line = null;
	private InitServlet servlet = null;
	private static String UAG = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
//	private static Random random = new Random();

	public Task(InitServlet servlet, String account) {
		this.servlet = servlet;
		this.line = account;
	}

	@Override
	public void run() {
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
				e.printStackTrace();
			}

			//client.getCookieStore().clear();
			System.out.println("开始打码...");

			String[] accs = line.split("----");
			System.out.println("检查帐号");
			request = new HttpGet(
					"https://ssl.ptlogin2.qq.com/check?uin="
							+ accs[0]
							+ "@qq.com&appid=522005705&ptlang=2052&js_type=2&js_ver=10009&r="
							+ Math.random());

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
			boolean nvc = resp.charAt(14) == '1' ? true : false;
			int codeID = -1;

			int fidx = resp.indexOf(",");
			int lidx = resp.lastIndexOf(",");

			String vcode = resp.substring(fidx + 2, lidx - 1);
			System.out.println(vcode);
			String salt = resp.substring(lidx + 2, lidx + 34);
			System.out.println(salt);

			if (nvc) {
				System.out.println("需验证码");

				System.out.println("请求验证码");
				request = new HttpGet(
						"https://ssl.captcha.qq.com/getimage?aid=522005705&r="
								+ Math.random() + "&uin=" + accs[0] + "@qq.com");

				response = client.execute(request);
				entity = response.getEntity();
				// resp = EntityUtils.toString(entity);
				DataInputStream in = new DataInputStream(entity.getContent());

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] barray = new byte[1024];
				int size = -1;
				while ((size = in.read(barray)) != -1) {
					baos.write(barray, 0, size);
				}

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

				// 识别验证码
				System.out.println("识别验证码");
				byte[] by = baos.toByteArray();
				byte[] resultByte = new byte[30]; // 为识别结果申请内存空间
				// StringBuffer rsb = new StringBuffer(30);
				String rsb = "0000";
				resultByte = rsb.getBytes();

				codeID = YDM.INSTANCE.YDM_DecodeByBytes(by, by.length, 1004,
						resultByte);// result byte
				vcode = new String(resultByte, "UTF-8").trim();

			} else {
				System.out.println("不需验证码");
			}

			// 计算ECP
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] results = md.digest(accs[1].getBytes());

			int psz = results.length;
			byte[] rs = new byte[psz + 8];
			for (int i = 0; i < psz; i++) {
				rs[i] = results[i];
			}

			String[] salts = salt.substring(2).split("\\\\x");
			// System.out.println(salts.length);
			for (int i = 0; i < salts.length; i++) {
				rs[psz + i] = (byte) Integer.parseInt(salts[i], 16);
			}

			results = md.digest(rs);
			String resultString = Converts.bytesToHexString(results)
					.toUpperCase();

			// vcode = "!RQM";
			results = md
					.digest((resultString + vcode.toUpperCase()).getBytes());
			resultString = Converts.bytesToHexString(results).toUpperCase();
			// System.out.println(resultString);
			String ecp = resultString;

			System.out.println("登录(A)");
			request = new HttpGet(
					"https://ssl.ptlogin2.qq.com/login?ptlang=2052&aid=522005705&daid=4&u1=https%3A%2F%2Fmail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwpt%26ft%3Dptlogin%26ss%3D%26validcnt%3D%26clientaddr%3D"
							+ accs[0]
							+ "%40qq.com&from_ui=1&ptredirect=1&h=1&wording=%E5%BF%AB%E9%80%9F%E7%99%BB%E5%BD%95&css=https://mail.qq.com/zh_CN/htmledition/style/fast_login181b91.css&mibao_css=m_ptmail&u_domain=@qq.com&uin="
							+ accs[0]
							+ "&u="
							+ accs[0]
							+ "@qq.com&p="
							+ ecp
							+ "&verifycode="
							+ vcode
							+ "&fp=loginerroralert&action=4-33-"
							+ System.currentTimeMillis()
							+ "&g=1&t=1&dummy=&js_type=2&js_ver=10009");

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

			String checksigUrl = null;

			if (resp.startsWith("ptuiCB('0'")) { // 成功登录

				// ptuiCB('0','0','https://ssl.ptlogin2.mail.qq.com/check_sig?pttype=1&uin=2415619507&service=login&nodirect=0&ptsig=jleMTZhwqNcM0NMxfD4D4vH6cGz37v31vDqVCeC9YmA_&s_url=https%3A%2F%2Fmail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwpt%26ft%3Dptlogin%26ss%3D%26validcnt%3D%26clientaddr%3D97046015%40qq.com&f_url=&ptlang=2052&ptredirect=101&aid=522005705&daid=4&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=1&pt_aid=0&pt_aaid=0&pt_light=0

				checksigUrl = resp.substring(resp.indexOf("http"),
						resp.indexOf("0','1','") + 1);
				System.out.println(checksigUrl);
				System.out.println("登录成功");
			} else {
				if (resp.startsWith("ptuiCB('4'")) { // 验证码错误
					// 报告验证码错误
					System.out.println("验证码错误");
					int reportErrorResult = -1;
					reportErrorResult = YDM.INSTANCE.YDM_Report(codeID, false);
					System.out.println("error:" + reportErrorResult);
				} else if (resp.startsWith("ptuiCB('3'")) { // 您输入的帐号或密码不正确，请重新输入
					System.out.println("帐号或密码不正确");
				} else if (resp.startsWith("ptuiCB('19'")) { // 帐号冻结，提示暂时无法登录
					System.out.println("帐号冻结");
				} else {
					// ptuiCB('19' 暂停使用
					// ptuiCB('7' 网络连接异常
					System.out.println("帐号异常");
				}
				servlet.fill();
				return;
			}

			System.out.println("登录(B)");
			request = new HttpGet(checksigUrl);

			request.setHeader("User-Agent", UAG);
			// get.setHeader("Content-Type", "text/html");
			request.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.setHeader("Accept-Language",
					"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			request.setHeader("Accept-Encoding", "gzip, deflate");
			request.setHeader("Referer",
					"https://mail.qq.com/cgi-bin/loginpage");
			request.setHeader("Connection", "keep-alive");
			// get.setHeader("Cookie", "ptui_version=10060");

			// get.removeHeaders("Cookie2");

			response = client.execute(request);
			// entity = response.getEntity();
			Header[] hs = response.getHeaders("Location");
			System.out.println("Location=" + hs[0].getValue());
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

			// 302
			request = new HttpGet(hs[0].getValue());
			request.setHeader("User-Agent", UAG);
			// get.setHeader("Content-Type", "text/html");
			request.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.setHeader("Accept-Language",
					"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			request.setHeader("Accept-Encoding", "gzip, deflate");
			request.setHeader("Referer",
					"https://mail.qq.com/cgi-bin/loginpage");
			request.setHeader("Connection", "keep-alive");

			response = client.execute(request);
			entity = response.getEntity();
			// hs = response.getHeaders("Location");
			// System.out.println("Location="+hs[0].getValue());
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

			System.out.println(resp);

			if (resp.indexOf("frame_html?sid=") == -1) {
				// System.out.println(resp.substring(resp.indexOf("errtype="),
				// resp.indexOf("errtype=")+8));
				System.out.println("无法跳转");
				servlet.fill();
				return;
			}

			String sid = resp.substring(resp.indexOf("frame_html?sid=") + 15,
					resp.indexOf("frame_html?sid=") + 31);
			System.out.println("sid=" + sid);

			String r = resp.substring(resp.indexOf("targetUrl+=\"&r=") + 15,
					resp.indexOf("targetUrl+=\"&r=") + 47);
			System.err.println("r=" + r);

			System.out.println("登录(C)");
			request = new HttpGet("http://mail.qq.com/cgi-bin/frame_html?sid="
					+ sid + "&r=" + r);

			request.setHeader("User-Agent", UAG);
			// get.setHeader("Content-Type", "text/html");
			request.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.setHeader("Accept-Language",
					"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			request.setHeader("Accept-Encoding", "gzip, deflate");
			request.setHeader("Referer",
					"https://mail.qq.com/cgi-bin/loginpage");
			request.setHeader("Connection", "keep-alive");
			response = client.execute(request);

			if (request != null) {
				request.releaseConnection();
				request.abort();
			}

			System.out.println("保存Cookie");
			StringBuffer sb = new StringBuffer();
			// 获取cookie
			List<Cookie> cs = client.getCookieStore().getCookies();
			for (Cookie cookie : cs) {
				sb.append(cookie.getName() + "=" + cookie.getValue() + "; ");
			}

			sb.delete(sb.length() - 2, sb.length() - 1);
			System.out.println("cookies size:" + Cookies.getInstance().size());
			synchronized (Cookies.getInstance()) {// 保存cookie
				Cookies.getInstance().add(sb.toString());
			}
			System.out.println("cookies size:" + Cookies.getInstance().size());
			System.out.println("打码结束");
		} catch (Exception e) {
			servlet.fill();
			e.printStackTrace();
		}
	}
}
