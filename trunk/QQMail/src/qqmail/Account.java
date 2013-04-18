package qqmail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.crypto.Cipher;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

public class Account {
	private String number;
	private String password;
	private String sid;
	private int totalGroupCount;
	private List<String> groupList;
	private int currentGroupIndex;
	private boolean isFinished = false;
	
	private String captcha;
	private String captchaId;
	private String captchaUin;
	private String captchaUrl;
	private String authtype;
	
	
	private static PublicKey publicKey = null;
	private static final Log logger = LogFactory.getLog(Account.class);
	private int successCount = 0;
	private int failureCount = 0;
	
	public void start() {
		loadPublicKey();
		
		if(login()) {
			String info = "";
			if(getGroupList()) {
				info = this.number  + " 开始发送"; 
				logger.info(info);
				QQMail.appendLogToUI(info);
				
				if(this.groupList.size() <= this.currentGroupIndex) {
					this.setCurrentGroupIndex(0);
				}
				
				if(QQMail.getSentToAllGroups()) {
					for(String groupId : groupList) {
						Mail.getInstance().sendGroup(this, groupId);
					}
					this.setCurrentGroupIndex(groupList.size());
				} else {
					for(int i = 0; i < QQMail.getCountToSendPerAccount(); i++) {
						if(this.currentGroupIndex + i >= this.totalGroupCount) {
							this.setCurrentGroupIndex(0);
							break;
						}
						Mail.getInstance().sendGroup(this, groupList.get(this.currentGroupIndex + i));
						
					}
					this.setCurrentGroupIndex(this.currentGroupIndex + QQMail.getCountToSendPerAccount());
				}
				
				QQMail.addSuccessAccount(this);
				
				info = this.number + " 发送结束, " + this.successCount + "封发送成功";
				logger.info(info);
				QQMail.appendLogToUI(info);
				this.isFinished = true;
			}
			
			logout();
		}
		
		if(this.isFinished) {
			QQMail.addFinishedAccountNumber();
		}
	}
	
	private void loadPublicKey() {
		if(publicKey == null) {
			synchronized(Account.class) {
				if(publicKey == null) {
					try {
				      CertificateFactory localCertificateFactory = CertificateFactory.getInstance("X.509");
				      InputStream localInputStream = new FileInputStream("Theoservice.cer");
				      Certificate localCertificate = localCertificateFactory.generateCertificate(localInputStream);
				      localInputStream.close();
				      publicKey = localCertificate.getPublicKey();
				    } catch (Exception localException) {
				    	logger.error(localException.getMessage());
				    }
				}
			}
		}
		
	}
	
	private String encrypt(String paramString) {
		try
	    {
	      Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	      localCipher.init(1, publicKey);
	      byte[] arrayOfByte = localCipher.doFinal(paramString.getBytes());
	      String str = com.tencent.qqmail.Utilities.p03i.C325a.MPa(arrayOfByte, arrayOfByte.length);
	      return str;
	    }
	    catch (Exception localException)
	    {
	      return paramString;
	    }
	}
	
	private boolean login() {
		PostMethod post = new PostMethod("http://i.mail.qq.com/cgi-bin/login");
		try {
			HttpClient client = new HttpClient();
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
		
		return false;
	}
	
	private void doLoginFailure(String info) {
		logger.info(info);
		QQMail.appendLogToUI(info);
		QQMail.addLoginFailureAccount(this);
		this.isFinished = true;
	}
	
	private boolean notifyNeedCaptcha(String imgUrl) {
		String info = this.number + " 需要验证码";
		logger.info(info);
		QQMail.appendLogToUI(info);
		
		OutputStream os;
		String captchaFileName = new Date().getTime() + ".gif";
		try {		
			HttpClient client = new HttpClient();
	        client.getHostConfiguration().setHost("vc.gtimg.com/", 80, "http");
	        HttpMethod method = new GetMethod(imgUrl);
		    client.executeMethod(method);
		    
		    os = new FileOutputStream(new File(captchaFileName));
		    os.write(method.getResponseBody());
		    os.close();
		} catch (Exception e) {
		}
		
		QQMail.notifyNeedCaptcha(this, captchaFileName);
		return false;
	}
	
	private boolean logout() {
		try {
			HttpClient client = new HttpClient();
	        client.getHostConfiguration().setHost("i.mail.qq.com", 80, "http");
	        HttpMethod method = new GetMethod("http://i.mail.qq.com//cgi-bin/mobile_syn?ef=js&t=mobile_data.json&s=syn&app=yes&invest=3&reg=2&devicetoken=asdf&sid=" + this.sid + "&error=app&f=xhtml&apv=0.9.5.2&os=android");		    
	        client.executeMethod(method); 
		    method.releaseConnection();		    
		    return true;
		} catch(Exception ex) {
			//logger.error(this.number + " logout failure, reason: " + ex.getMessage());
		}
		
		this.isFinished = true;
		return false;
	}
	
	private boolean getGroupList() {
		try {
			HttpClient client = new HttpClient();
	        client.getHostConfiguration().setHost("i.mail.qq.com", 80, "http");
	        HttpMethod method = new GetMethod("http://i.mail.qq.com/cgi-bin/grouplist?sid="+ this.sid + "&t=grouplist_json&error=app&f=xhtml&apv=0.9.5.2&os=android");
		    client.executeMethod(method);
		    
		    this.groupList = new ArrayList<String>();
		    String response = method.getResponseBodyAsString();		    
		    JSONObject items =  new JSONObject(response).getJSONObject("items");
		    this.totalGroupCount = items.getInt("opcnt");
		    String groupStr = items.getString("item").replace("[", "").replace("]", ",");		    
		    
		    String[] groups = groupStr.split("},");
		    for(int i = 0; i < groups.length; i++) {
		    	String id = new JSONObject(groups[i] + "}").getString("id");
		    	this.groupList.add(id);
		    }
		    
		    String info = this.number + " 成功获取群列表"; 
			logger.error(info);
			QQMail.appendLogToUI(info);
		    method.releaseConnection();
		    if(this.totalGroupCount == 0) {
		    	logger.info(this.number + "没有加入任何开通了群邮件的QQ群！");
		    	QQMail.addNoQQMailGroupAccount(this);
		    	this.isFinished = true;
		    	return false;
		    }
		    
		    return true;
		} catch(Exception ex) {
			this.isFinished = true;
		}
		
		return false;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getNumber() {
		return this.number;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setSid(String sid) {
		this.sid = sid;
	}
	
	public String getSid() {
		return this.sid;
	}
	
	public void setTotalGroupCount(int totalGroupCount) {
		this.totalGroupCount = totalGroupCount;
	}
	
	public int getTotalGroupCount() {
		return this.totalGroupCount;
	}
	
	public void setCurrentGroupIndex(int currentGroupIndex) {
		this.currentGroupIndex = currentGroupIndex;
	}
	
	public int getCurrentGroupIndex() {
		return this.currentGroupIndex;
	}
	
	public String toString() {
		return this.number + " " + this.password;
	}
	
	public void setSuccessCount(int count) {
		this.successCount = count;
	}
	
	public int getSuccessCount() {
		return this.successCount;
	}
	
	public void setFailureCount(int count) {
		this.failureCount = count;
	}
	
	public int getFailureCount() {
		return this.failureCount;
	}
	
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
}
