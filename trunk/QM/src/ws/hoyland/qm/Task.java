package ws.hoyland.qm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.json.JSONObject;

public class Task implements Runnable {

	protected QM qm;
	private TableItem item;
	private String line;
	private final String UAG = "Dalvik/1.2.0 (Linux; U; Android 2.2; sdk Build/FRF91)";
	private String captcha;
	protected String sid;
	private Image image;
	private String uin;
	private String password;
	private Basket basket;

	public Task(ThreadPoolExecutor pool, List<String> proxies, TableItem item,
			Object object, QM qm, Basket basket) {
		this.qm = qm;
		this.basket = basket;
		this.item = item;
		this.uin = item.getText(1);
		this.password = item.getText(2);
	}
	
	public void setCaptcha(String captcha){
		this.captcha = captcha;
	}
	
	public Image getImage(){
		return this.image;
	}
	
	@Override
	public void run() {
		
		info("正在登录", true);
		
		DefaultHttpClient client = new DefaultHttpClient();
		//HttpHost proxy = new HttpHost(ips[0], Integer.parseInt(ips[1]), "http");

		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		//client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		
		HttpResponse response = null;
		HttpEntity entity = null;
		JSONObject json = null;
		HttpPost post = null;
		
		try{
			post = new HttpPost("http://i.mail.qq.com/cgi-bin/login");
			post.setHeader("User-Agent", UAG);
			post.setHeader("Connection", "Keep-Alive");
			
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("uin", this.uin));
            
            nvps.add(new BasicNameValuePair("pwd", Util.encrypt(this.password + "\r\n" + new Date().getTime())));
            nvps.add(new BasicNameValuePair("aliastype", "@qq.com"));
            nvps.add(new BasicNameValuePair("t", "login_json"));
            nvps.add(new BasicNameValuePair("dnsstamp", "2012010901"));
            nvps.add(new BasicNameValuePair("os", "android"));
            nvps.add(new BasicNameValuePair("error", "app"));
            nvps.add(new BasicNameValuePair("f", "xhtml"));
            nvps.add(new BasicNameValuePair("apv", "0.9.5.2"));

            post.setEntity(new UrlEncodedFormEntity(nvps));            
			response = client.execute(post);
			entity = response.getEntity();

			line = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
		}catch(Exception e){
			info("连接异常", false);
			e.printStackTrace();
		}finally {
			post.releaseConnection();
			//client.getConnectionManager().shutdown();
        }
		
		String captchaId = null;
		String captchaUin = null;
		String authtype = null;
		String captchaUrl = null;
		
		try {
			json = new JSONObject(line);
	    	if(json.has("sid") && !json.getString("sid").isEmpty()) {			    	
		    	sid = json.getString("sid");
		    	info("登录成功", false);
		    } else if(json.has("errtype") && !json.getString("errtype").isEmpty() &&
		    		json.getString("errtype").equals("1")) {
		    	info("登录失败:密码错误", false);
		    } else if(json.has("errmsg") && !json.getString("errmsg").isEmpty()) {
		    	String[] items = json.getString("errmsg").split("&");
		    	boolean needCaptcha = false;
		    	for(String item : items) {
		    		String[] pair = item.split("=");
		    		if(pair.length == 2) {
		    			if(pair[0].equals("vurl")) {
		    				needCaptcha = true;
			    			captchaUrl = pair[1];	
		    			} else if(pair[0].equals("vid")) {
		    				captchaId = pair[1];
		    			} else if(pair[0].equals("vuin")) {
		    				captchaUin = pair[1];
		    			} else if(pair[0].equals("authtype")) {
		    				authtype = pair[1];
		    			}
		    		}
		    	}
		    	
		    	if(needCaptcha) {
		    		try{
//		    			Display.getDefault().asyncExec(new Runnable(){
//		    				@Override
//		    				public void run() {
//				    			qm.help(Task.this);
//		    				}
//		    			});
		    			HttpGet get = null;

		    			try {
		    				get = new HttpGet("http://vc.gtimg.com/" + captchaUrl
		    						+ ".gif");
		    				get.setHeader("Connection", "Keep-Alive");

		    				response = client.execute(get);
		    				entity = response.getEntity();

		    				InputStream input = entity.getContent();
		    				image = new Image(Display.getDefault(), input);
		    				EntityUtils.consume(entity);
		    			} catch (Exception e) {
		    				e.printStackTrace();
		    			} finally {
		    				get.releaseConnection();
		    			}

						basket.pop();//消费者
						
		    			Display.getDefault().asyncExec(new Runnable(){
		    				@Override
		    				public void run() {
		    					try{
		    						//System.out.println("SI1:"+Task.this);
		    						//System.out.println("SI2:"+Task.this);
		    						qm.showImage(Task.this);
		    						//System.out.println("SI3:"+Task.this);
		    					}catch(Exception e){
		    						e.printStackTrace();
		    					}
		    				}
		    			});
		    			
		    			synchronized(this){
		    				//System.out.println(this+" wait");
		    				this.wait();
		    			}
		    			
		    			post = new HttpPost("http://i.mail.qq.com/cgi-bin/login");
		    			post.setHeader("User-Agent", UAG);
		    			post.setHeader("Connection", "Keep-Alive");
		    			
		    			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		                nvps.add(new BasicNameValuePair("uin", this.uin));
		                
		                nvps.add(new BasicNameValuePair("pwd", Util.encrypt(this.password + "\r\n" + new Date().getTime())));
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
			        	
		                post.setEntity(new UrlEncodedFormEntity(nvps));            
		    			response = client.execute(post);
		    			entity = response.getEntity();

		    			line = EntityUtils.toString(entity);
		    			EntityUtils.consume(entity);
		    			
		    			json = new JSONObject(line);
		    	    	if(json.has("sid") && !json.getString("sid").isEmpty()) {			    	
		    		    	sid = json.getString("sid");
		    		    	info("登录成功", false);
		    		    } else if(json.has("errtype") && !json.getString("errtype").isEmpty() &&
		    		    		json.getString("errtype").equals("1")) {
		    		    	info("登录失败:密码错误", false);
		    		    } else if(json.has("errmsg") && !json.getString("errmsg").isEmpty()) {
		    		    	info("登录失败:验证码错误或者帐号被封", false);
		    		    }else{
		    		    	info("登录失败:异常3", false);
		    		    }
		    		}catch(Exception e){
		    			info("登录失败:连接异常", false);
		    			e.printStackTrace();
		    		}finally {
		    			post.releaseConnection();
		            }
			    } else {
			    	info("登录失败:账号被封", false);
			    }
		    }else{
		    	info("登录失败:异常2", false);
		    }
    	} catch(Exception e) {
    		info("登录失败:异常1", false);
    	}
		
		client.getConnectionManager().shutdown();
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
