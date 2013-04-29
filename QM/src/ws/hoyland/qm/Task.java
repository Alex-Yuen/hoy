package ws.hoyland.qm;

import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.json.JSONException;
import org.json.JSONObject;

public class Task implements Runnable {

	protected QM qm;
	private TableItem item;
	private String line;
	private final String UAG = "Dalvik/1.6.0 (Linux; U; Android 4.2; google_sdk Build/JB_MR1)";
	private String captcha;
	private String sid;
	private Image image;
	private String uin;
	private String password;
	private Basket basket;
	private int index;
	private List<String> proxies;
	private String px;
	private Random rnd = new Random();
	private boolean useProxy;
	private String title;
	private String content;
	private final String cs = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private boolean del;
	private boolean iptf;
	private DefaultHttpClient client;
	private byte pos;
	private List<NameValuePair> nvps = null;
	private final byte RS = 7;// request length
	private int gc;//group count
	private String mid;//del mail
	private int idx; //current group
	private int idxc;//sent group
	private int mgc = 2;
	private int gr = 0;

	private String captchaId = null;
	private String captchaUin = null;
	private String authtype = null;
	private String captchaUrl = null;
	private List<String> group = null;
	private String groupid = null;

	private HttpResponse response = null;
	private HttpEntity entity = null;
	private JSONObject json = null;
	private HttpUriRequest request = null;

	public Task(ThreadPoolExecutor pool, List<String> proxies, TableItem item, QM qm, Basket basket) {
		this.proxies = proxies;
		this.qm = qm;
		this.del = qm.del();
		this.basket = basket;
		this.item = item;
		this.uin = item.getText(1);
		this.password = item.getText(2);
		try {
			this.index = Integer.parseInt(item.getText(3));
		} catch (Exception e) {
			this.index = 0;
		}
		this.useProxy = qm.useProxy();
		this.title = qm.getTitle();
		// System.err.println(title);
		this.content = qm.getContent();

		client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
	}

	private void getRequest(byte position) {		
		request = null;
		HttpPost post = null;
		HttpGet get = null;
		
		switch(position){
		case 0:
			info("正在登录");
			setSelection();
			//重置验证码参数
			captchaId = null;
			captchaUin = null;
			authtype = null;
			captchaUrl = null;
			break;
		case 1:
			info("需验证码");
			iptf = false;//重新设置验证码为未验证
			break;
		case 2:
			info("正在登录");
			break;
		case 3:
			info("获取列表");
			idxc = 0;
			group = new ArrayList<String>();
			break;
		case 4:
			info("发送邮件");
			break;
		case 5:
			info("删除邮件");
			break;
		case 6:
			info("正在注销");
			break;
		default:
			info("未知状态");
			break;
		}
		
		if (qm.needReconn()) {//每次执行新的request之前，确定是否需要重拨，如果是，则等待重拨结束
			info("等待重拨");
			qm.report();
			synchronized (qm) {
				try {
					qm.wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			info("重拨结束");
		}
		
		if(position==2){//等待验证码输入
			if(!iptf){//如果验证码未输入，则等待
				//System.err.println(this+">>"+3);
				info("等待输入验证码");
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (Exception e) {
					// normal
				}
				//System.err.println(this+">>"+4);
			}
		}

		switch (position) {
		case 0:
			post = new HttpPost("http://i.mail.qq.com/cgi-bin/login");
			nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("uin", this.uin));
			nvps.add(new BasicNameValuePair("pwd", Util.encrypt(this.password
					+ "\r\n" + new Date().getTime())));
			nvps.add(new BasicNameValuePair("aliastype", "@qq.com"));
			nvps.add(new BasicNameValuePair("t", "login_json"));
			nvps.add(new BasicNameValuePair("dnsstamp", "2012010901"));
			nvps.add(new BasicNameValuePair("os", "android"));
			nvps.add(new BasicNameValuePair("error", "app"));
			nvps.add(new BasicNameValuePair("f", "xhtml"));
			nvps.add(new BasicNameValuePair("apv", "0.9.5.2"));
			try {
				post.setEntity(new UrlEncodedFormEntity(nvps));
				request = post;
			} catch (Exception e) {
				e.printStackTrace();
				request = null;
			}
			break;
		case 1:
			get = new HttpGet("http://vc.gtimg.com/" + captchaUrl + ".gif");
			request = get;
			break;
		case 2:
			post = new HttpPost("http://i.mail.qq.com/cgi-bin/login");
			nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("uin", this.uin));
			nvps.add(new BasicNameValuePair("pwd", Util.encrypt(this.password
					+ "\r\n" + new Date().getTime())));
			nvps.add(new BasicNameValuePair("aliastype", "@qq.com"));
			nvps.add(new BasicNameValuePair("t", "login_json"));
			nvps.add(new BasicNameValuePair("dnsstamp", "2012010901"));
			nvps.add(new BasicNameValuePair("os", "android"));
			nvps.add(new BasicNameValuePair("error", "app"));
			nvps.add(new BasicNameValuePair("f", "xhtml"));
			nvps.add(new BasicNameValuePair("apv", "0.9.5.2"));

			nvps.add(new BasicNameValuePair("verifycode", captcha));
			nvps.add(new BasicNameValuePair("vid", captchaId));
			nvps.add(new BasicNameValuePair("vuin", captchaUin));
			nvps.add(new BasicNameValuePair("vurl", captchaUrl));
			nvps.add(new BasicNameValuePair("authtype", authtype));
			try {
				post.setEntity(new UrlEncodedFormEntity(nvps));
				request = post;
			} catch (Exception e) {
				e.printStackTrace();
				request = null;
			}
			break;
		case 3:
			get = new HttpGet(
					"http://i.mail.qq.com/cgi-bin/grouplist?sid="
							+ this.sid
							+ "&t=grouplist_json&error=app&f=xhtml&apv=0.9.5.2&os=android");
			request = get;
			break;
		case 4:
			post = new HttpPost("http://i.mail.qq.com/cgi-bin/groupmail_send");
			nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("sid", this.sid));
			nvps.add(new BasicNameValuePair("t", "mobile_mgr.json"));
			nvps.add(new BasicNameValuePair("s", "groupsend"));
			nvps.add(new BasicNameValuePair("qqgroupid", groupid));
			nvps.add(new BasicNameValuePair("fmailid", "$id$"));
			nvps.add(new BasicNameValuePair("content__html",
					randomString(this.content)));
			nvps.add(new BasicNameValuePair("subject", randomString(this.title)));
			nvps.add(new BasicNameValuePair("signadded", "yes"));
			nvps.add(new BasicNameValuePair("fattachlist", ""));
			nvps.add(new BasicNameValuePair("cattachelist", "$cattLst$"));
			nvps.add(new BasicNameValuePair("devicetoken", qm.getRandomToken()));
			nvps.add(new BasicNameValuePair("os", "android"));
			nvps.add(new BasicNameValuePair("ReAndFw", "forward"));
			nvps.add(new BasicNameValuePair("ReAndFwMailid", ""));
			nvps.add(new BasicNameValuePair("error", "app"));
			nvps.add(new BasicNameValuePair("f", "xhtml"));
			nvps.add(new BasicNameValuePair("apv", "0.9.5.2"));
			try {
				post.setEntity(new UrlEncodedFormEntity(nvps, "GBK"));
				request = post;
			} catch (Exception e) {
				e.printStackTrace();
				request = null;
			}
			break;
		case 5:
			post = new HttpPost("http://i.mail.qq.com/cgi-bin/mail_mgr");
			post.setHeader("User-Agent", UAG);
			post.setHeader("Connection", "Keep-Alive");

			nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("sid", this.sid));
			nvps.add(new BasicNameValuePair("ef", "js"));
			nvps.add(new BasicNameValuePair("t", "mobile_mgr.json"));
			nvps.add(new BasicNameValuePair("s", "del"));
			nvps.add(new BasicNameValuePair("mailaction", "mail_del"));
			nvps.add(new BasicNameValuePair("mailid", mid));
			nvps.add(new BasicNameValuePair("error", "app"));
			nvps.add(new BasicNameValuePair("f", "xhtml"));
			nvps.add(new BasicNameValuePair("apv", "0.9.5.2"));
			nvps.add(new BasicNameValuePair("os", "android"));

			try {
				post.setEntity(new UrlEncodedFormEntity(nvps));
				request = post;
			} catch (Exception e) {
				e.printStackTrace();
				request = null;
			}
			break;
		case 6:
			get = new HttpGet(
					"http://i.mail.qq.com//cgi-bin/mobile_syn?ef=js&t=mobile_data.json&s=syn&app=yes&invest=3&reg=2&devicetoken=asdf&sid="
							+ this.sid
							+ "&error=app&f=xhtml&apv=0.9.5.2&os=android");
			request = get;
			break;
		default:
			break;
		}

		request.setHeader("User-Agent", UAG);
		request.setHeader("Connection", "Keep-Alive");
	}

	private void releaseConnection(HttpUriRequest request) {
		if (request instanceof HttpGet) {
			((HttpGet) request).releaseConnection();
		} else if (request instanceof HttpPost) {
			((HttpPost) request).releaseConnection();
		}
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
		iptf = true;
	}

	public Image getImage() {
		return this.image;
	}

	private boolean proxy() {// 获取并设置代理
		if (px != null) {
			synchronized (proxies) {
				proxies.remove(px);
			}
		}

		HttpHost proxy = null;

		synchronized (proxies) {
			if (proxies.size() == 0) {
				qm.shutdown();
				return false;
			} else {
				this.px = proxies.get(rnd.nextInt(proxies.size()));
			}
		}
		String[] ips = px.split(":");
		proxy = new HttpHost(ips[0], Integer.parseInt(ips[1]), "http");

		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		return true;
		// }
	}

	@Override
	public void run() {
		if (useProxy) {
			if (!proxy()) {// 取代理不成功
				info("获取代理失败:任务终止");
				return;
			}
		}

		while (true) {
			outter:
			for (byte i = pos; i < RS; i++) {
				try {
					if(!qm.getFlag()){
						return;
					}
					getRequest(i);
					response = client.execute(request);
					entity = response.getEntity();
					
					//处理数据
					if(i!=1){
						line = EntityUtils.toString(entity);
						try{
							json = new JSONObject(line);
							//解析数据
							switch(i){
								case 0:
								case 2:
									if (json.has("sid") && !json.getString("sid").isEmpty()) {
										sid = json.getString("sid");
										info("登录成功");
										update(0);
										if(i==0){
											pos += 3;//直接获取列表
											break outter;
										}
									} else if (json.has("errtype")
											&& !json.getString("errtype").isEmpty()){
										if("1".equals(json.getString("errtype"))) {
											info("登录失败:密码错误");
											update(1);
											return;
										} else if ("4".equals(json.getString("errtype"))) {
											info("登录失败:独立密码");
											update(1);
											return;
										} else if ("7".equals(json.getString("errtype"))) {
											info("登录失败:密码错误(*)");
											update(1);
											return;
										} else {
											System.err.println(line);
											info("登录失败:异常2");
											break outter;
										}
									} else if (json.has("errmsg")
											&& !json.getString("errmsg").isEmpty()) {
										if(i==0){
											String[] items = json.getString("errmsg").split("&");
											for (String item : items) {
												String[] pair = item.split("=");
												if (pair.length == 2) {
													if (pair[0].equals("vurl")) {
														captchaUrl = pair[1];
													} else if (pair[0].equals("vid")) {
														captchaId = pair[1];
													} else if (pair[0].equals("vuin")) {
														captchaUin = pair[1];
													} else if (pair[0].equals("authtype")) {
														authtype = pair[1];
													}
												}
											}
											
											if(captchaUrl == null){
												info("登录失败:帐号被封");
												update(1);
												return;
											}
										}else if(i==2){
											if ("-100".equals(json.getString("app_code"))) {
												info("登录失败:帐号被封");
												update(1);
												return;
											} else {
												System.err.println(line);
												info("登录失败:验证码错误");
												pos--;//需要回到执行上一个获取验证码的任务
												break outter;//重新执行当前任务
											}
										}
									} else{
										System.err.println(line);
										info("登录失败:异常1");
										break outter;
									}
									break;
//								case 1:
//									break;
								case 3:
									JSONObject items = json.getJSONObject("items");
									gc = items.getInt("opcnt");
									String groupStr = items.getString("item").replace("[", "")
											.replace("]", ",");		
									String[] groups = groupStr.split("},");
									for (int k = 0; k < groups.length; k++) {
										if (groups[k].startsWith("{")) {
											String id = new JSONObject(groups[k] + "}")
													.getString("id");
											group.add(id);
										}
									}
									info_gc(gc);
									info_gs();
									if(gc==0){
										info("无可用群");
										pos += 3;//进去注销
										break outter;
									}else{							
										info("获取列表成功");
										update(2, gc);
										
										//设置索引
										if (qm.getConf() != null) {
											mgc = Integer.parseInt(qm.getConf().getProperty(
													"GROUP_QUANTITY"));
										}
				
										//update(2, gc > index ? (gc - index) : 0);// 在索引后的
										if (mgc != -1) {
											gr = (gc - index) > mgc ? (gc - index - mgc) : 0;
											update(5, gr);// 未发送的
										}

										idx = index;//当前要操作的邮件
										
										if((mgc != -1 && idx < group.size() && idxc < mgc)
							|| (mgc == -1 && idx < group.size())){
											groupid = group.get(idx);
											// ignore
										}else{
											pos += 2;//直接跳到注销步骤
											break outter;
										}
									}
									break;
								case 4://send
									try {
										if (json.getInt("errcode") == 0) {
											mid = json.getString("mid");
											info("发送成功:\r\n\t" + groupid);
											info_gs_i();
											update(3);
										} else {
											info("发送失败:" + json.getInt("errcode"));
											update(4);
											return;
										}
									} catch (Exception e) {
										// e.printStackTrace();
										System.err.println(line);
										info("发送失败:非法内容");
										update(4, gc-gr-idxc);
										return;
									}
									break;
								case 5:
									if(mid!=null&&del){//删除的条件
										try {
											if (json.getInt("errcode") == 0) {
												info("删除成功:\r\n\t" + groupid);
											} else {
												info("删除失败:" + json.getInt("errcode"));
											}
										} catch (Exception e) {
											info("删除失败:异常");
										}
									}
									idx++;
									idxc++;
									if((mgc != -1 && idx < group.size() && idxc < mgc)
											|| (mgc == -1 && idx < group.size())){
										groupid = group.get(idx);
										pos = 4;
										break outter;//需要继续发送
									}
									//发送、删除 成功后，更新索引
									break;
								case 6:
									info("注销成功");
									pos = RS;
									break;
								default:
									break;
							}
						}catch(JSONException ex){
							System.err.println(line);
							//ex.printStackTrace();
							info("解析数据失败");
							if(useProxy){
								if (!proxy()) {
									info("获取代理失败:任务终止");
									return;
								}else{
									info("更换代理，重新执行任务");
								}
							}else{
								info("重新执行任务");
							}
							break;
						}
					}else{					
						InputStream input = entity.getContent();
						image = new Image(Display.getDefault(), input);
						info("获取验证码成功");
					}
					
					EntityUtils.consume(entity);
					releaseConnection(request);
					
					if(i==1){ //图像的后续处理
						//System.err.println(this+">>"+1);
						basket.pop();// 消费者
						//System.err.println(this+">>"+2);
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								try {
									qm.showImage(Task.this);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				} catch (SocketTimeoutException ex) {
					if (useProxy) {
						info("连接失败:使用新代理");
						if (!proxy()) {// 取代理不成功
							info("获取代理失败:任务终止");
							return;
						}
						break;//重新执行当前request
					}
				} catch (ClientProtocolException ex) {
					if (useProxy) {
						info("连接失败:使用新代理");
						if (!proxy()) {
							info("获取代理失败:任务终止");
							return;
						}
						break;
					}
				} catch (SocketException ex) { // 包含HttpHostConnectException
					if (useProxy) {
						info("连接失败:使用新代理");
						if (!proxy()) {
							info("获取代理失败:任务终止");
							return;
						}
						break;
					}
				} catch (NoHttpResponseException ex) {
					if (useProxy) {
						info("连接失败:使用新代理");
						if (!proxy()) {
							info("获取代理失败:任务终止");
							return;
						}
						break;
					}
				} catch (ConnectTimeoutException ex) {
					if (useProxy) {
						info("连接失败:使用新代理");
						if (!proxy()) {
							info("获取代理失败:任务终止");
							return;
						}
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}

			if (pos == RS) {// 运行结束，完成所有request
				break;
			}
		}

		client.getConnectionManager().shutdown();
	}

	private void info_gs_i() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				item.setText(6, String.valueOf(Integer.parseInt(item.getText(6))+1));
			}
		});
	}

	private void info_gs() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				item.setText(6, "0");
			}
		});		
	}

	private void info_gc(final int gcx) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				item.setText(5, String.valueOf(gcx));
			}
		});		
	}

	private void info(final String status) {
		// log
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				qm.log(uin + "->" + status + "\r\n");
				item.setText(4, status);
			}
		});
	}
	
	private void setSelection(){
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				item.getParent().setSelection(item);
			}
		});
	}

	private void update(final int type) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (qm.getFlag()) {
					qm.update(type);
				}
			}
		});
	}

	private void update(final int type, final int count) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (qm.getFlag()) {
					qm.update(type, count);
				}
			}
		});
	}

	private String randomString(String ss) {
		StringBuffer sb = null;
		int len = 0;

		String result = ss;
		while (result.contains("{*}")) {
			sb = new StringBuffer();
			len = 8 + rnd.nextInt(3);
			for (int i = 0; i < len; i++) {
				sb.append(cs.charAt(rnd.nextInt(cs.length())));
			}
			result = result.replaceFirst("\\{\\*\\}", sb.toString());
		}
		// System.err.println("2:"+result);
		return result;
	}
}
