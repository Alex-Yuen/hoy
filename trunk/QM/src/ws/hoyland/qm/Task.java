package ws.hoyland.qm;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.json.JSONObject;

import qqmail.QQMail;

public class Task implements Runnable {

	protected QM qm;
	private TableItem item;
	
	public Task(ThreadPoolExecutor pool, List<String> proxies, TableItem item,
			Object object, QM qm) {
		this.qm = qm;
		this.item = item;
	}

	@Override
	public void run() {
		
		info("正在登录", true);
		
		DefaultHttpClient client = new DefaultHttpClient();
		//HttpHost proxy = new HttpHost(ips[0], Integer.parseInt(ips[1]), "http");

		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		//client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		
		PostMethod post = new PostMethod("http://i.mail.qq.com/cgi-bin/login");
		try {
	        client.getHostConfiguration().setHost("i.mail.qq.com", 80, "http");
	        NameValuePair uin = new NameValuePair("uin", this.number);
	        NameValuePair pwd = new NameValuePair("pwd", encrypt(this.password + "\r\n" + new Date().getTime()));
	        NameValuePair aliastype = new NameValuePair("aliastype", "@qq.com");
	        NameValuePair t = new NameValuePair("t", "login_json");
	        NameValuePair dnsstamp = new NameValuePair("dnsstamp", "2012010901");
	        NameValuePair os = new NameValuePair("os", "android");
	        NameValuePair error = new NameValuePair("error", "app");
	        NameValuePair f = new NameValuePair("f", "xhtml");
	        NameValuePair apv = new NameValuePair("apv", "0.9.5.2");
	        if(this.captcha != null && !this.captcha.isEmpty()) {
	        	NameValuePair verifycode = new NameValuePair("verifycode", this.captcha);
	        	NameValuePair vid = new NameValuePair("vid", this.captchaId);
	        	NameValuePair vuin = new NameValuePair("vuin", this.captchaUin);	
	        	NameValuePair vurl = new NameValuePair("vurl", this.captchaUrl);
	        	NameValuePair authtype = new NameValuePair("authtype", this.authtype);
	        	post.setRequestBody(new NameValuePair[] { uin, pwd, aliastype, t, dnsstamp, os, error, f, apv, verifycode, vid, vuin, vurl, authtype});
	        } else {
	        	post.setRequestBody(new NameValuePair[] { uin, pwd, aliastype, t, dnsstamp, os, error, f, apv});
	        }	        
		    
	        client.executeMethod(post);
		    String response = new String(post.getResponseBodyAsString().getBytes("8859_1"));
	    	try {
	    		JSONObject jobject = new JSONObject(response);
		    	if(jobject.has("sid") && !jobject.getString("sid").isEmpty()) {			    	
			    	this.setSid(jobject.getString("sid"));
			    	String info = this.number + " 登录成功"; 
					logger.info(info);
					QQMail.appendLogToUI(info);
					return true;
			    } else if(jobject.has("errtype") && !jobject.getString("errtype").isEmpty() &&
			    		jobject.getString("errtype").equals("1")) {
			    	doLoginFailure(this.number + " 密码错误，跳过此QQ号");
			    } else if(jobject.has("errmsg") && !jobject.getString("errmsg").isEmpty()) {
			    	String[] items = jobject.getString("errmsg").split("&");
			    	boolean needCaptcha = false;
			    	for(String item : items) {
			    		String[] pair = item.split("=");
			    		if(pair.length == 2) {
			    			if(pair[0].equals("vurl")) {
			    				//验证码只提示一次
				    			if(this.captcha != null && !this.captcha.isEmpty()) {
				    				doLoginFailure(this.number + " 验证码输入错误或者此账号已被封，跳过此QQ号");
				    				return false;
				    			} else {
				    				this.captchaUrl = pair[1];	
				    				needCaptcha = true;
				    			}	
			    			} else if(pair[0].equals("vid")) {
			    				this.captchaId = pair[1];
			    			} else if(pair[0].equals("vuin")) {
			    				this.captchaUin = pair[1];
			    			} else if(pair[0].equals("authtype")) {
			    				this.authtype = pair[1];
			    			} 
			    		}			    		
			    	}
			    	if(needCaptcha) {
				    	this.notifyNeedCaptcha(this.captchaUrl + ".gif");
				    } else {
				    	doLoginFailure(this.number + "登录失败，此账号已被封， 跳过此QQ号");
				    }
			    } 
	    	} catch(Exception e) {
	    		doLoginFailure(this.number + " 登录失败, 原因: 账号异常");
	    	}
		    		    
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			post.releaseConnection();
		}		
		
		info("完成", false);		
	}
	
	private void info(final String status, final boolean flag){
		//log
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				item.setText(4, status);
				if(flag){
					item.getParent().setSelection(item);
				}
			}
		});
	}

}
