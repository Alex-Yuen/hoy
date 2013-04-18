package qqmail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

public class Mail {
	private static String title = "test";
	private static String content = "test";
	
	private static Mail instance;
	
	private static final Log logger = LogFactory.getLog(Mail.class);
	
	public static Mail getInstance() {
		if(instance == null) {
			synchronized(Mail.class) {
				if(instance == null) {
					instance = new Mail();
					InputStream is;
					try {
						is = new FileInputStream("邮件模板.txt");
						BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
						title = br.readLine();
						StringBuffer sb = new StringBuffer();
						String line;
						while((line = br.readLine()) != null) {
							sb.append(line + "<br/>");
						}
						content = sb.toString();
					} catch (Exception e) {
					}
					
				}
			}
		}
		
		return instance;
	}
	
	public void sendGroup(Account account, String groupId) {
		try {
			HttpClient client = new HttpClient();
	        client.getHostConfiguration().setHost("i.mail.qq.com", 80, "http");
	        PostMethod post = new PostMethod("http://i.mail.qq.com/cgi-bin/groupmail_send");
	        NameValuePair ssid = new NameValuePair("sid", account.getSid());
	        NameValuePair t = new NameValuePair("t", "mobile_mgr.json");
	        NameValuePair s = new NameValuePair("s", "groupsend");
	        NameValuePair qqgroupid = new NameValuePair("qqgroupid", groupId);
	        NameValuePair fmailid = new NameValuePair("fmailid", "$id$");
	        NameValuePair content__html = new NameValuePair("content__html", Mail.content);
	        NameValuePair subject = new NameValuePair("subject", Mail.title);
	        NameValuePair signadded = new NameValuePair("signadded", "yes");
	        NameValuePair fattachlist = new NameValuePair("fattachlist", "");
	        NameValuePair cattachelist = new NameValuePair("cattachelist", "$cattLst$");
	        NameValuePair devicetoken = new NameValuePair("devicetoken", getRandomToken(187));
	        NameValuePair os = new NameValuePair("os", "android");
	        NameValuePair ReAndFw = new NameValuePair("ReAndFw", "forward");
	        NameValuePair ReAndFwMailid = new NameValuePair("ReAndFwMailid", "");
	        NameValuePair error = new NameValuePair("error", "app");
	        NameValuePair f = new NameValuePair("f", "xhtml");
	        NameValuePair apv = new NameValuePair("os", "0.9.5.2");
	        
	        post.setRequestBody(new NameValuePair[] { ssid, t,s,qqgroupid,fmailid, content__html, subject, signadded,
	        		fattachlist, cattachelist,devicetoken, os, ReAndFw, ReAndFwMailid, error, f, apv });
	        client.executeMethod(post);
	        
	        String response =   new String(post.getResponseBodyAsString().getBytes("8859_1"));
	        if(new JSONObject(response).getInt("errcode") == 0) {
	        	account.setSuccessCount(account.getSuccessCount() + 1);
	        	String info = account.getNumber() + " 成功发送给" + groupId;
	        	logger.info(info);
	        	QQMail.appendLogToUI(info);
	        } else {
	        	account.setFailureCount(account.getFailureCount() + 1);
	        }	        
	        
	        post.releaseConnection();
		} catch(Exception ex) {
			//logger.error(account.getNumber() + " send to " + groupId + " failure, reason: " + ex.getMessage());
		}
	}
	
	private static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	
	private static String getRandomToken(int length) {
		StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
                sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return sb.toString(); 
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return this.content;
	}
}
