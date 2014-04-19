package ws.hoyland.cs.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import ws.hoyland.util.Converts;
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

	public InitServlet() {
	}

	@Override
	public void init(final ServletConfig config) {
		try {
			super.init(config);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			URL url = this.getClass().getClassLoader().getResource("");
			xpath = url.getPath();

			xpath = xpath.substring(0, xpath.indexOf("/WEB-INF/"));
			xpath = URLDecoder.decode(xpath, "UTF-8");
			System.out.println("xpath=" + xpath);
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
			System.out.println("xa");
//			YDM y = (YDM) Native.loadLibrary(xpath.substring(1)
//					+ "/WEB-INF/lib/yundamaAPI.dll", YDM.class);
			//YDM y = (YDM)JNALoader.load("/WEB-INF/lib/yundamaAPI.dll", YDM.class);
			System.out.println("xb");
			
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
				// 初始化client
				try {
					client = new DefaultHttpClient();
					client.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);
					client.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT, 4000);
					// client.getParams().setParameter(
					// ClientPNames.HANDLE_REDIRECTS, false);
					HttpClientParams.setCookiePolicy(client.getParams(),
							CookiePolicy.BROWSER_COMPATIBILITY);

					HttpHost proxy = new HttpHost("127.0.0.1", 8888);

					
					client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
							proxy);
					
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

				flag = true;

				System.out.println("Servlet 初始化结束");

				// 启动维护线程
				timer = new Timer();
				timer.schedule(new TimerTask() {
					private HttpGet request = null;
					// private HttpPost post = null;
					// private StringEntity se = null;
					private HttpResponse response = null;
					private HttpEntity entity = null;
					private String resp = null;

					private void fill() {
						try {
							System.out.println("开始打码...");

							String[] accs = null;
							if (accounts.size() > 0) {
								int idx = random.nextInt(accounts.size());
								accs = accounts.get(idx).split("----");
							} else {
								// fill();
								System.out.println("没有帐号");
								return;
							}
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

							int fidx = resp.indexOf(",");
							int lidx = resp.lastIndexOf(",");

							String vcode = resp.substring(fidx + 2, lidx - 1);
							System.out.println(vcode);
							String salt = resp.substring(lidx + 2, lidx + 34);
							System.out.println(salt);

							System.out.println("请求验证码");
							request = new HttpGet(
									"https://ssl.captcha.qq.com/getimage?aid=522005705&r="
											+ Math.random() + "&uin=" + accs[0]
											+ "@qq.com");

							response = client.execute(request);
							entity = response.getEntity();
							// resp = EntityUtils.toString(entity);
							DataInputStream in = new DataInputStream(entity
									.getContent());
							
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

							int codeID = YDM.INSTANCE.YDM_DecodeByBytes(by,
									by.length, 1004, resultByte);// result byte
							vcode = new String(resultByte, "UTF-8").trim();

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
								rs[psz + i] = (byte) Integer.parseInt(salts[i],
										16);
							}

							results = md.digest(rs);
							String resultString = Converts.bytesToHexString(
									results).toUpperCase();

							// vcode = "!RQM";
							results = md.digest((resultString + vcode
									.toUpperCase()).getBytes());
							resultString = Converts.bytesToHexString(results)
									.toUpperCase();
							// System.out.println(resultString);
							String ecp = resultString;

							System.out.println("登录(A)");
							request = new HttpGet(
									"https://ssl.ptlogin2.qq.com/login?ptlang=2052&aid=522005705&daid=4&u1=https%3A%2F%2Fmail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwpt%26ft%3Dptlogin%26ss%3D%26validcnt%3D%26clientaddr%3D"+accs[0]+"%40qq.com&from_ui=1&ptredirect=1&h=1&wording=%E5%BF%AB%E9%80%9F%E7%99%BB%E5%BD%95&css=https://mail.qq.com/zh_CN/htmledition/style/fast_login181b91.css&mibao_css=m_ptmail&u_domain=@qq.com&uin="
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
								
								//ptuiCB('0','0','https://ssl.ptlogin2.mail.qq.com/check_sig?pttype=1&uin=2415619507&service=login&nodirect=0&ptsig=jleMTZhwqNcM0NMxfD4D4vH6cGz37v31vDqVCeC9YmA_&s_url=https%3A%2F%2Fmail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwpt%26ft%3Dptlogin%26ss%3D%26validcnt%3D%26clientaddr%3D97046015%40qq.com&f_url=&ptlang=2052&ptredirect=101&aid=522005705&daid=4&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=1&pt_aid=0&pt_aaid=0&pt_light=0
										
								checksigUrl = resp.substring(
										resp.indexOf("http"),
										resp.indexOf("0','1','") + 1);
								System.out.println(checksigUrl);
								System.out.println("登录成功");
							} else {
								if (resp.startsWith("ptuiCB('4'")) { // 验证码错误
									// 报告验证码错误
									System.out.println("验证码错误");
									int reportErrorResult = -1;
									reportErrorResult = YDM.INSTANCE
											.YDM_Report(codeID, false);
									System.out.println("error:"
											+ reportErrorResult);
								} else if (resp.startsWith("ptuiCB('3'")) { // 您输入的帐号或密码不正确，请重新输入
									System.out.println("帐号或密码不正确");
								} else if (resp.startsWith("ptuiCB('19'")) { // 帐号冻结，提示暂时无法登录
									System.out.println("帐号冻结");
								} else {
									// ptuiCB('19' 暂停使用
									// ptuiCB('7' 网络连接异常
									System.out.println("帐号异常");
								}
								fill();
							}

							System.out.println("登录(B)");
							request = new HttpGet(checksigUrl);

							request.setHeader("User-Agent", UAG);
							// get.setHeader("Content-Type", "text/html");
							request.setHeader("Accept",
									"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
							request.setHeader("Accept-Language",
									"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
							request.setHeader("Accept-Encoding",
									"gzip, deflate");
							request.setHeader("Referer",
									"https://mail.qq.com/cgi-bin/loginpage");
							request.setHeader("Connection", "keep-alive");
							// get.setHeader("Cookie", "ptui_version=10060");

							// get.removeHeaders("Cookie2");

							// 自动重定向
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
							
							System.out.println(resp);

							if(resp.indexOf("frame_html?sid=")==-1){
								System.out.println("验证码错误");
								fill();
								return;
							}
							
							String sid = resp.substring(
									resp.indexOf("frame_html?sid=") + 15,
									resp.indexOf("frame_html?sid=") + 31);
							System.out.println("sid=" + sid);

							String r = resp.substring(
									resp.indexOf("targetUrl+=\"&r=") + 15,
									resp.indexOf("targetUrl+=\"&r=") + 47);
							System.err.println("r=" + r);

							System.out.println("登录(C)");
							request = new HttpGet(
									"http://mail.qq.com/cgi-bin/frame_html?sid="
											+ sid + "&r=" + r);

							request.setHeader("User-Agent", UAG);
							// get.setHeader("Content-Type", "text/html");
							request.setHeader("Accept",
									"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
							request.setHeader("Accept-Language",
									"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
							request.setHeader("Accept-Encoding",
									"gzip, deflate");
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
							List<Cookie> cs = client.getCookieStore()
									.getCookies();
							for (Cookie cookie : cs) {
								sb.append(cookie.getName() + "="
										+ cookie.getValue() + "; ");
							}

							sb.delete(sb.length() - 2, sb.length() - 1);
							System.out.println("cookies size:"+Cookies.getInstance().size());
							synchronized (Cookies.getInstance()) {// 保存cookie
								Cookies.getInstance().add(sb.toString());
							}

							System.out.println("cookies size:"+Cookies.getInstance().size());
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("打码结束");
					};

					@Override
					public void run() {
						try {
							Iterator<String> it = null;
							synchronized (Cookies.getInstance()) {
								it = new CopiedIterator(Cookies.getInstance()
										.iterator());
							}

							while (it.hasNext()) {
								String line = (String) it.next();
								try {
									// 验证
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
							}

							// 重新打码
							int csize = Cookies.getInstance().size();
							int size = Integer.parseInt(config
									.getInitParameter("size"));
							if (csize < size) {
								for (int i = 0; i < size - csize; i++) {
									fill();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
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
		// TODO
		// 保存cookie
	}
}
