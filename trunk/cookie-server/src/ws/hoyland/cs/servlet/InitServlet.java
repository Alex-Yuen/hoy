package ws.hoyland.cs.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import ws.hoyland.util.CopiedIterator;
import ws.hoyland.util.YDM;

public class InitServlet extends HttpServlet {

	protected boolean flag = false;
	private Timer timer = null;
	private DefaultHttpClient client = new DefaultHttpClient();
	private List<String> accounts = new ArrayList<String>();

	private static final long serialVersionUID = 407060459247815226L;
	private static String UAG = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	private static Random random = new Random();
	private static Base64 base64 = new Base64();
	
	public InitServlet() {
	}

	@Override
	public void init(final ServletConfig config) {
		try {
			super.init(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Servlet 初始化(1).");
		timer = new Timer();

		// 加载cookie
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Servlet 初始化(2).");
				InputStream is = null;
				BufferedReader br = null;
				String xpath = null;

				// 导入
				try {
					URL url = this.getClass().getClassLoader().getResource("");
					xpath = url.getPath();

					xpath = xpath.substring(0, xpath.indexOf("/WEB-INF/"));
					xpath = URLDecoder.decode(xpath, "UTF-8");
					System.out.println("xpath=" + xpath);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 加载cookies
				try {
					System.out.println("加载cookies.txt...");
					is = new FileInputStream(new File(xpath + "/WEB-INF/cookies.txt"));
					br = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while ((line = br.readLine()) != null) {
						synchronized (Cookies.getInstance()) {
							Cookies.getInstance().add(line);
						}
//						System.out.println("adding cookies:" + line);
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
					is = new FileInputStream(new File(xpath + "/WEB-INF/accounts.txt"));
					br = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while ((line = br.readLine()) != null) {
						synchronized (Cookies.getInstance()) {
							accounts.add(line);
						}
//						System.out.println("adding cookies:" + line);
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
					client.getParams().setParameter(
							ClientPNames.HANDLE_REDIRECTS, false);
					HttpClientParams.setCookiePolicy(client.getParams(),
							CookiePolicy.BROWSER_COMPATIBILITY);

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
					private HttpPost post = null;
					private StringEntity se = null;
					private HttpResponse response = null;
					private HttpEntity entity = null;
					private String resp = null;

					private void fill() {
						try {
							System.out.println("开始打码...");
							String ts = null;
							request = new HttpGet("https://w.mail.qq.com/");
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
							System.out.println("开始打码(1)");
							if (resp.indexOf("name=\"ts\"") != -1) {
								ts = resp.substring(
										resp.indexOf("name=\"ts\"") - 12,
										resp.indexOf("name=\"ts\"") - 2);
							} else {
								System.out.println("ts 为空");
								return;
							}
							System.out.println("开始打码(2)");
							String[] accs = null;
							if(accounts.size()>0){
								int idx = random.nextInt(accounts.size());
								accs = accounts.get(idx).split("----");
							}else{
								//fill();
								System.out.println("没有帐号");
								return;
							}
							//计算ECP
							Cipher cipher = Cipher.getInstance("RSA");
							RSAPublicKey pbk = null;
							KeyFactory keyFac = null;
							try {
								keyFac = KeyFactory.getInstance("RSA");
							} catch (NoSuchAlgorithmException ex) {
								throw new Exception(ex.getMessage());
							}

							RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
									new BigInteger("CF87D7B4C864F4842F1D337491A48FFF54B73A17300E8E42FA365420393AC0346AE55D8AFAD975DFA175FAF0106CBA81AF1DDE4ACEC284DAC6ED9A0D8FEB1CC070733C58213EFFED46529C54CEA06D774E3CC7E073346AEBD6C66FC973F299EB74738E400B22B1E7CDC54E71AED059D228DFEB5B29C530FF341502AE56DDCFE9", 16), 
									new BigInteger("10001", 16));
							
							pbk = (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
							cipher.init(Cipher.ENCRYPT_MODE, pbk);					
							byte[] encrypted = cipher.doFinal((accs[1]+"\n"+ts+"\n").getBytes());							
							
							post = new HttpPost(
									"https://w.mail.qq.com/cgi-bin/login?sid=");
							se = new StringEntity(
									"device=&ts="
											+ ts
											+ "&p="
											+ URLEncoder.encode(base64.encodeToString(encrypted), "UTF-8")
											+ "&f=xhtml&delegate_url=&action=&https=true&tfcont=&uin="
											+ accs[0]
											+ "&aliastype=%40qq.com&pwd=&mss=1&btlogin=+%E7%99%BB%E5%BD%95+");
							
							post.setEntity(se);
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
							if (post != null) {
								post.releaseConnection();
								post.abort();
							}
							
							if (resp.indexOf("errtype=3") != -1){//需要验证码
								System.out.println("需要验证码");
								if (resp.indexOf("url=https:") != -1)
	                            {
									resp = resp.substring(resp.indexOf("url=https:") + 12);
	                            }
	                            else
	                            {
	                            	resp = resp.substring(resp.indexOf("url=http:") + 11);
	                            }

								resp = resp.substring(0, resp.indexOf("\"/>"));
	                            String url = "https://"+resp.replace("f=xhtmlmp", "f=xhtml");

	                            String vurl = resp.substring(resp.indexOf("vurl=") + 5);
	                            vurl = vurl.substring(0, vurl.indexOf("&vid"));
	                            String vurlx = vurl;
	                            String vid = vurl.substring(20, 52);
	                            String vuin = url.substring(url.indexOf("vuin=") + 5);
	                            vuin = vuin.substring(0, vuin.indexOf("&"));	                            
	                            vurl = vurl.endsWith("gif") ? vurl : vurl + ".gif";
	                            
	                            //请求验证码
	                            try {
	                            	System.out.println("请求验证码");
	                				request = new HttpGet(vurl);

	                				response = client.execute(request);
	                				entity = response.getEntity();
//	                				resp = EntityUtils.toString(entity);
	    							DataInputStream in = new DataInputStream(entity.getContent());
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
	                				
	    							ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    							byte[] barray = new byte[1024];
	    							int size = -1;
	    							while ((size = in.read(barray)) != -1) {
	    								baos.write(barray, 0, size);
	    							}
	    							
	    							//识别验证码
	    							System.out.println("识别验证码");
	    							byte[] by = baos.toByteArray();
	    							byte[] resultByte = new byte[30]; // 为识别结果申请内存空间
	    							// StringBuffer rsb = new StringBuffer(30);
	    							String rsb = "0000";
	    							resultByte = rsb.getBytes();

	    							int codeID = YDM.INSTANCE.YDM_DecodeByBytes(by, by.length,
	    										1004, resultByte);// result byte
	    							String result = new String(resultByte, "UTF-8").trim();
	    							
	    							//继续登录
	    							System.out.println("继续登录");
	    							request = new HttpGet("https://w.mail.qq.com/");
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

	    							if (resp.indexOf("name=\"ts\"") != -1) {
	    								ts = resp.substring(
	    										resp.indexOf("name=\"ts\"") - 12,
	    										resp.indexOf("name=\"ts\"") - 2);
	    							} else {
	    								System.out.println("ts 为空");
	    								fill();
	    								return;
	    							}

	    							post = new HttpPost(
	    									"https://w.mail.qq.com/cgi-bin/login?sid=");
	    							se = new StringEntity("device=&ts=" + ts + "&p=&f=xhtml&delegate_url=&action=&https=true&tfcont=22%2520serialization%3A%3Aarchive%25205%25200%25200%252010%25200%25200%25200%25208%2520authtype%25201%25208%25209%2520clientuin%25209%2520" + accs[0] + "%25209%2520aliastype%25207%2520%40qq.com%25206%2520domain%25206%2520qq.com%25202%2520ts%252010%25201392345223%25201%2520f%25205%2520xhtml%25205%2520https%25204%2520true%25203%2520uin%25209%2520" + accs[0] + "%25203%2520mss%25201%25201%25207%2520btlogin%25206%2520%2520%E7%99%BB%E5%BD%95%2520&verifycode=" + result + "&vid=" + vid + "&vuin=" + vuin + "&vurl=" + URLEncoder.encode(vurlx, "UTF-8").replace("%2e", ".") + "&mss=1&btlogin=+%E7%99%BB%E5%BD%95+");
	    							post.setEntity(se);
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
	    							if (post != null) {
	    								post.releaseConnection();
	    								post.abort();
	    							}
	    							
	    							//登录成功
	    							
	    							if (resp.indexOf("errtype=") == -1 && (resp.indexOf("today") != -1)){
	    								System.out.println("登录成功");
	    								StringBuffer sb = new StringBuffer();
	    								//获取cookie
	    								List<Cookie>  cs = client.getCookieStore().getCookies();
	    								for(Cookie cookie:cs){
	    									sb.append(cookie.getName()+"="+cookie.getValue()+"; ");
	    								}
	    								
	    								sb.delete(sb.length()-2, sb.length()-1);
	    								
	    								synchronized (Cookies.getInstance()) {//保存cookie
	    									Cookies.getInstance().add(sb.toString());
	    								}
	    								System.out.println("保存Cookie");
	    							}else{
	    								System.out.println("登录失败");
	    								//报告验证码错误
	    								try {
	    									//
	    									int reportErrorResult = -1;
	    									reportErrorResult = YDM.INSTANCE.YDM_Report(codeID, false);
	    									System.out.println("error:"+reportErrorResult);
	    									fill();
	    								} catch (Exception e) {
	    									e.printStackTrace();
	    								}
	    							}
	                				
	                			} catch (Exception e) {
	                				e.printStackTrace();
	                			}
							}else{
								//fill();
								System.out.println("无需验证码");
								return;
							}
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
