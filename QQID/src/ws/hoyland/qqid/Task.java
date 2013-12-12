package ws.hoyland.qqid;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import ws.hoyland.qqid.Engine;
import ws.hoyland.qqid.EngineMessage;
import ws.hoyland.qqid.EngineMessageType;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

public class Task implements Runnable, Observer {

	private String line;
	private boolean run = false;
	private boolean fb = false; // break flag;
//	private boolean fc = false; // continue flag;
	private int idx = 0; // method index;
	private Configuration configuration = Configuration.getInstance();
	
	// private boolean block = false;
	// private TaskObject obj = null;

	private DefaultHttpClient client;
	private HttpPost post = null;
	private HttpGet get = null;

	private HttpResponse response = null;
	private HttpEntity entity = null;
	private JSONObject json = null;
	// private HttpUriRequest request = null;
	private List<NameValuePair> nvps = null;

	private String loginSig =null;
	private String sig = null;
	// private byte[] ib = null;
	// private byte[] image = null;

	private EngineMessage message = null;
	private int id = 0;
	private String account = null;
	private String password = null;

	private ByteArrayOutputStream baos = null;
	private int codeID = -1;
	private String result;
	
	private String rc = null; //red code in mail
	private String rcl = null; //回执编号

	protected String mid = null;
	private String mail = null;
	private String mpwd = null;
	
	private boolean sf = false; //stop flag from engine
	private boolean rec = false;//是否准备重拨
	private boolean finish = false;
	
	private int tcconfirm = 0;//try count of confirm
	private int tcback = 0;//try count of 回执
	
	private boolean nvc = false; //need vc
	private String vcode = null;
	private String salt = null;
	
	private String ecp = null;//encrypted password
	private String checksigUrl = null;
	
	//private final String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
	private final String UAG = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
	private boolean pause = false;

	public Task(String line) {
		// TODO Auto-generated constructor stub
		String[] ls = line.split("----");
		this.id = Integer.parseInt(ls[0]);
		this.account = ls[1];
		this.password = ls[2];

		this.run = true;

		client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false); //不自动跳转
		 
		//gzip 过滤器
		client.addResponseInterceptor(new HttpResponseInterceptor() {

			@Override
			public void process(HttpResponse response, HttpContext context)
					throws HttpException, IOException {
				 HttpEntity entity = response.getEntity();  
			        Header ceheader = entity.getContentEncoding();  
			        if (ceheader != null) {  
			            HeaderElement[] codecs = ceheader.getElements();  
			            for (int i = 0; i < codecs.length; i++) {  
			                if (codecs[i].getName().equalsIgnoreCase("gzip")) {  
			                    response.setEntity(new GzipDecompressingEntity(  
			                            response.getEntity()));  
			                    return;  
			                }  
			            }  
			        }  
				
			}  			
		  
		});  
	}

	@Override
	public void run() {
		info("开始运行");
		idx = 1;
		
		if(pause){//暂停
			info("暂停运行");
			synchronized(PauseCountObject.getInstance()){
				message = new EngineMessage();
				message.setType(EngineMessageType.IM_PAUSE_COUNT);
				Engine.getInstance().fire(message);
			}
			
			synchronized(PauseObject.getInstance()){
				try{
					PauseObject.getInstance().wait();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

			
//			//阻塞等待重拨
//			if(rec){
//				info("等待重拨");
//				synchronized(ReconObject.getInstance()){
//					try{
//						ReconObject.getInstance().wait();
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//				info("等待重拨结束， 继续执行");
//			}
	
			if(sf){//如果此时有停止信号，直接返回
				info("初始化(任务取消)");
				return;
			}

		synchronized(StartObject.getInstance()){	
			//通知有新线程开始执行
			message = new EngineMessage();
			message.setType(EngineMessageType.IM_START);
			Engine.getInstance().fire(message);
		}
		
		// System.err.println(line);
		while (run&&!sf) { //正常运行，以及未收到停止信号
			if (fb) {
				break;
			}
//			if (fc) {
//				continue;
//			}

			// if(block){
			// synchronized (obj.getBlock()) {
			// try {
			// obj.getBlock().wait();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			// block = false;
			// }

			process(idx);

			try {
				if (entity != null) {
					EntityUtils.consume(entity);
				}
				if (get != null) {
					get.releaseConnection();
				}
				if (post != null) {
					post.releaseConnection();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//通知Engine: 线程结束
		
		String[] dt = new String[6];

		dt[0] = "0";
		dt[1] = this.account;
		dt[2] = this.password;
		
		if(finish){
			dt[0] = "1";
			dt[3] = this.rcl;
			dt[4] = this.mail;
			dt[5] = this.mpwd;
		}
		

		synchronized(FinishObject.getInstance()){
			message = new EngineMessage();
			message.setTid(this.id);
			message.setType(EngineMessageType.IM_FINISH);
			message.setData(dt);
			Engine.getInstance().fire(message);
		}
		
		Engine.getInstance().deleteObserver(this);
		
	}

	private void process(int index) {
		//int itv = Integer.parseInt(this.configuration.getProperty("MAIL_ITV"));
		int itv = 0;
		switch (index) {
		case 0:
			info("正在请求验证码");
			try {
				get = new HttpGet(
						"http://captcha.qq.com/getsig?aid=523005413&uin=0&"
								+ Math.random());

				get.setHeader("User-Agent", UAG);
				get.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_index");
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				sig = line.substring(20, line.indexOf(";    "));
				// System.err.println(sig);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 1:			
			//获取login sig
			info("正在登录");
			try {
				get = new HttpGet("http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Referer", "http://id.qq.com/login/ptlogin.html");
				get.setHeader("Connection", "keep-alive");				

				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				//",clienti
				loginSig = line.substring(line.indexOf(",login_sig:\"")+12, line.indexOf("\",clientip"));
				System.err.println(loginSig);
								
				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 2:	
			idx++;
//			info("例行报告");
//			//set cookie, bad js repot not yet.
//			try {
//				get = new HttpGet("http://ui.ptlogin2.qq.com/cgi-bin/report?id=358191&t="+Math.random());
//
//				get.setHeader("User-Agent", UAG);
//				//get.setHeader("Content-Type", "text/html");
//				get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
//				get.setHeader("Accept-Encoding", "gzip, deflate");
//				get.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
//				get.setHeader("Connection", "keep-alive");
//				get.setHeader("Cookie", "ptui_version=10060");
//				
//				//get.removeHeaders("Cookie2");
//				
//				response = client.execute(get);
//				entity = response.getEntity();
//
////				line = EntityUtils.toString(entity);
////				System.err.println(line);
//								
//				idx++;
//			} catch (Exception e) {
//				e.printStackTrace();
//				fb = true;
//			}
			break;
		case 3:			
			info("检查帐号");
			//get ptui_checkVC
			try {
				get = new HttpGet("http://check.ptlogin2.qq.com/check?regmaster=&uin="+this.account+"&appid=1006102&js_ver=10060&js_type=1&login_sig="+loginSig+"&u1=http%3A%2F%2Fid.qq.com%2Findex.html&r="+Math.random());

				get.setHeader("User-Agent", UAG);
				//get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "*/*");
				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
				get.setHeader("Connection", "keep-alive");				

				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				System.err.println(line);
				
				nvc = line.charAt(14)=='1'?true:false;
				//没有做RSAKEY检查，默认是应该有KEY，用getEncryption；否则用getRSAEncryption
				
				int fidx = line.indexOf(",");
				int lidx = line.lastIndexOf(",");
				
				vcode = line.substring(fidx+2, lidx-1);
				System.err.println(vcode);
				salt = line.substring(lidx+2, lidx+34);
				System.err.println(salt);
				
//				this.password = "fmdrdorcmu";
//				System.out.println(password);
//				this.salt = "\\x00\\x
				
				
//				rs[idx+0] = (byte)0x00;
//				rs[idx+1] = (byte)0x00;
//				rs[idx+2] = (byte)0x00;
//				rs[idx+3] = (byte)0x00;
//				rs[idx+4] = (byte)0x08;
//				rs[idx+5] = (byte)0x23;
//				rs[idx+6] = (byte)0x9b;
//				rs[idx+7] = (byte)0x86;
				
				//String resultString = byteArrayToHexString(results);
				//resultString = resultString.toUpperCase();
				
				if(nvc){
					//Encryption.getRSAEncryption(K, G)
					idx++; //进入下一步验证码
				}else{
					idx += 3;
				}				
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			//run = false;
			break;
		case 4://请求验证码
			info("下载验证码");
			try {
				get = new HttpGet("http://captcha.qq.com//getimage?uin=782767782&aid=1006102&"
						+ Math.random());

				get.setHeader("User-Agent", UAG);
				//get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
				get.setHeader("Connection", "keep-alive");

				response = client.execute(get);
				entity = response.getEntity();

				DataInputStream in = new DataInputStream(entity.getContent());
				baos = new ByteArrayOutputStream();
				byte[] barray = new byte[1024];
				int size = -1;
				while ((size = in.read(barray)) != -1) {
					baos.write(barray, 0, size);
				}
				ByteArrayInputStream bais = new ByteArrayInputStream(
						baos.toByteArray());

				message = new EngineMessage();
				message.setType(EngineMessageType.IM_IMAGE_DATA);
				message.setData(bais);

				Engine.getInstance().fire(message);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 5://识别验证码
			info("识别验证码");
			try {
				byte[] by = baos.toByteArray();
				byte[] resultByte = new byte[30]; // 为识别结果申请内存空间
//				StringBuffer rsb = new StringBuffer(30);
				String rsb = "0000";
				resultByte = rsb.getBytes();

				if(Engine.getInstance().getCptType()==0){
					codeID = YDM.INSTANCE.YDM_DecodeByBytes(by, by.length, 1004, resultByte);//result byte
//					result = "xxxx";
//					for(int i=0;i<resultByte.length;i++){
//						System.out.println(resultByte[i]);
//					}
//					System.out.println("TTT:"+codeID);
					vcode = new String(resultByte, "UTF-8").trim();
				}else{
					codeID = DM.INSTANCE.uu_recognizeByCodeTypeAndBytesA(by,
							by.length, 1, resultByte); // 调用识别函数,resultBtye为识别结果
					vcode = new String(resultByte, "UTF-8").trim();
				}						
				
				//result = rsb.toString();
				//System.out.println("---"+result);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 6:
			info("提交登录请求");
			try {
				//计算ECP
				MessageDigest md = MessageDigest.getInstance("MD5"); 
				byte[] results = md.digest(this.password.getBytes());
				
				int psz = results.length;
				byte[] rs = new byte[psz+8];
				for(int i=0;i<psz;i++){
					rs[i] = results[i];
				}
				
				String[] salts = this.salt.substring(2).split("\\\\x");
				//System.out.println(salts.length);
				for(int i=0; i<salts.length; i++){
					rs[psz+i] = (byte)Integer.parseInt(salts[i], 16);
				}
				
				results = md.digest(rs); 
				String resultString = byteArrayToHexString(results).toUpperCase();
				
				//vcode = "!RQM";
				results = md.digest((resultString+vcode.toUpperCase()).getBytes()); 				
				resultString = byteArrayToHexString(results).toUpperCase();
				//System.out.println(resultString);
				ecp = resultString;
				
				
				System.out.println("http://ptlogin2.qq.com/login?u="+this.account+"&p="+ecp+"&verifycode="+vcode+"&aid=1006102&u1=http%3A%2F%2Fid.qq.com%2Findex.html&h=1&ptredirect=1&ptlang=2052&daid=1&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=4-9-"+System.currentTimeMillis()+"&mibao_css=&t=1&g=1&js_ver=10060&js_type=1&login_sig="+loginSig+"&pt_rsa=0");
				get = new HttpGet("http://ptlogin2.qq.com/login?u="+this.account+"&p="+ecp+"&verifycode="+vcode+"&aid=1006102&u1=http%3A%2F%2Fid.qq.com%2Findex.html&h=1&ptredirect=1&ptlang=2052&daid=1&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=4-9-"+System.currentTimeMillis()+"&mibao_css=&t=1&g=1&js_ver=10060&js_type=1&login_sig="+loginSig+"&pt_rsa=0");

				get.setHeader("User-Agent", UAG);
				//get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "*/*");
				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
				get.setHeader("Connection", "keep-alive");				
				get.setHeader("Cookie", "ptui_loginuin="+this.account);
				
				//get.removeHeaders("Cookie2");
				
				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				System.err.println(line);
				
				if(line.startsWith("ptuiCB('4'")){ //验证码错误
					idx++;
				}else if(line.startsWith("ptuiCB('0'")){ //成功登录
					checksigUrl = line.substring(line.indexOf("'http:")+1, line.indexOf("0','1','")+1);
					System.err.println(checksigUrl);
					info("登录成功");
					idx += 2;
				}else{
					// ptuiCB('19' 暂停使用
					// ptuiCB('7' 网络连接异常
					info("帐号异常, 退出任务");
					run = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 7://报告错误验证码
			info("验证码错误，报告异常");
			try {
				//
				int reportErrorResult = -1;
				if(Engine.getInstance().getCptType()==0){
					reportErrorResult = YDM.INSTANCE.YDM_Report(codeID, false);
				}else{
					reportErrorResult = DM.INSTANCE.uu_reportError(codeID);
				}
				System.err.println(reportErrorResult);
				
				idx = 4; // 重新请求验证码 原来 idx = 0
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 8://成功登录之后
			try {
				info("Check Sig");
				get = new HttpGet(checksigUrl);

				get.setHeader("User-Agent", UAG);
				//get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
				get.setHeader("Connection", "keep-alive");
				//get.setHeader("Cookie", "ptui_version=10060");
				
				//get.removeHeaders("Cookie2");
				
				response = client.execute(get);
				entity = response.getEntity();

//				line = EntityUtils.toString(entity);
//				System.err.println(line);
								
				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 9: //获取好友列表
			//POST /cgi-bin/friends_home
			//ldw=2045554247&ver=20100914&from=mars936027210
			info("获取好友列表");
			//TODO
			try {
				String skey = null;
				List<Cookie> lc = client.getCookieStore().getCookies();
				for(Cookie ck : lc){
					if("skey".equals(ck.getName())){
						skey = ck.getValue();
						break;
					}
				}
				
				if(skey==null){
					info("登录失效, 退出任务");
					run = false;
					break;
				}
				
				String ldw = String.valueOf(encryptSkey(skey));
//				String swfver = "20100914";
//				String swffrom = "mars"+this.account;				
				
				post = new HttpPost(
						"http://id.qq.com/cgi-bin/friends_home");

				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				post.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				post.setHeader("Accept-Encoding", "gzip, deflate");
				post.setHeader("Connection", "keep-alive");
						
				post.setHeader("Referer",
						"http://1.url.cn/id/flash/img/friends_mgr.swf?v=10029");

				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("ldw", ldw));
				nvps.add(new BasicNameValuePair("ver", "20100914"));
				nvps.add(new BasicNameValuePair("from", "mars"+this.account));

				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);

				JSONObject json = new JSONObject(line);
				if(json.getInt("ec")==0){	
					//JSONObject finfo = json.getJSONObject("finfo");
					//finfo.
					JSONArray array = null;
					try{
						array = json.getJSONArray("finfo");
					}catch(Exception e){
						info("没有好友");
						idx += 2;
						break;
					}
					System.out.println("length:"+array.length());
					for(int i=0; i<array.length(); i++){
						int user = array.getJSONObject(i).getInt("u");
						System.out.println(user);
					}
				}else{
					info("获取好友列表失败, 退出任务");
					run = false;
					break;
				}
				System.err.println(line);

				// 发送消息，提示Engine，需要邮箱
				// obj = new TaskObject();
//				EngineMessage message = new EngineMessage();
//				message.setTid(this.id);
//				message.setType(EngineMessageType.IM_REQUIRE_MAIL);
//				// message.setData(obj);
//				Engine.getInstance().fire(message);

				// block = true;

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			run = false;
			break;
		case 32:
			// 根据情况，阻塞或者提交验证码到UU
			info("正在识别验证码");
			try {
				byte[] by = baos.toByteArray();
				byte[] resultByte = new byte[30]; // 为识别结果申请内存空间
//				StringBuffer rsb = new StringBuffer(30);
				String rsb = "0000";
				resultByte = rsb.getBytes();

				if(Engine.getInstance().getCptType()==0){
					codeID = YDM.INSTANCE.YDM_DecodeByBytes(by, by.length, 1004, resultByte);//result byte
//					result = "xxxx";
//					for(int i=0;i<resultByte.length;i++){
//						System.out.println(resultByte[i]);
//					}
//					System.out.println("TTT:"+codeID);
					result = new String(resultByte, "UTF-8").trim();
				}else{
					codeID = DM.INSTANCE.uu_recognizeByCodeTypeAndBytesA(by,
							by.length, 1, resultByte); // 调用识别函数,resultBtye为识别结果
					result = new String(resultByte, "UTF-8").trim();
				}
				
				
				
				//result = rsb.toString();
				//System.out.println("---"+result);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 33:
			info("开始申诉");
			try {
				get = new HttpGet("http://aq.qq.com/cn2/appeal/appeal_index");

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();

				// line = EntityUtils.toString(entity);
				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 34:
			info("检查申诉帐号");
			try {
				get = new HttpGet(
						"http://aq.qq.com/cn2/appeal/appeal_check_assist_account?UserAccount="
								+ this.account);

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");
				get.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_index");

				response = client.execute(get);
				entity = response.getEntity();

				// line = EntityUtils.toString(entity);
				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 35:
			info("正在验证");
			try {
				get = new HttpGet(
						"http://aq.qq.com/cn2/ajax/check_verifycode?session_type=on_rand&verify_code="
								+ result + "&appid=523005413&CaptchaSig="
								+ URLEncoder.encode(this.sig, "UTF-8"));

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");
				get.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_index");

				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				json = new JSONObject(line);

				// System.err.println(line);
				if ("0".equals(json.getString("Err"))) {
					idx += 2;
				} else {
					// 报错
					idx++;
				}
				// System.err.println(line);

			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 36:
			info("验证码错误，报告异常");
			try {
				//
				int reportErrorResult = -1;
				if(Engine.getInstance().getCptType()==0){
					reportErrorResult = YDM.INSTANCE.YDM_Report(codeID, false);
				}else{
					reportErrorResult = DM.INSTANCE.uu_reportError(codeID);
				}
				System.err.println(reportErrorResult);
				
				idx = 0; // 重新开始
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 37:
			info("填写申诉资料");
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_contact");

				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_index");

				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("qqnum", this.account));
				nvps.add(new BasicNameValuePair("verifycode2", result));
				nvps.add(new BasicNameValuePair("CaptchaSig", this.sig));

				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				// line = EntityUtils.toString(entity);

				// System.err.println(line);

				// 发送消息，提示Engine，需要邮箱
				// obj = new TaskObject();
				EngineMessage message = new EngineMessage();
				message.setTid(this.id);
				message.setType(EngineMessageType.IM_REQUIRE_MAIL);
				// message.setData(obj);
				Engine.getInstance().fire(message);

				// block = true;

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 38:
			info("提交申诉资料");
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_contact_confirm");

				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_contact");

				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("txtLoginUin", this.account));
				nvps.add(new BasicNameValuePair("txtCtCheckBox", "0"));
				nvps.add(new BasicNameValuePair("txtName", Names.getInstance()
						.getName()));
				nvps.add(new BasicNameValuePair("txtAddress", ""));
				nvps.add(new BasicNameValuePair("txtIDCard", ""));
				nvps.add(new BasicNameValuePair("txtContactQQ", ""));
				nvps.add(new BasicNameValuePair("txtContactQQPW", ""));
				nvps.add(new BasicNameValuePair("txtContactQQPW2", ""));
				nvps.add(new BasicNameValuePair("radiobutton", "mail"));
				nvps.add(new BasicNameValuePair("txtContactEmail", this.mail));
				nvps.add(new BasicNameValuePair("txtContactMobile", "请填写您的常用手机"));

				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				
				if(line.contains("申诉过于频繁")){
					//通知出现申诉频繁
					message = new EngineMessage();
					message.setType(EngineMessageType.IM_FREQ);

					//System.err.println("["+this.account+"]"+info);
					Engine.getInstance().fire(message);
					run = false;
					break;
				}

				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 39: // 收邮件 
			info("等待"+itv+"秒，接收邮件[确认]");
			try {
				try{
					Thread.sleep(1000*itv);
				}catch(Exception e){
					sf = true;
					Thread.sleep(1000*4); //意外中断，继续等待
				}
				
				Properties props = new Properties();
//				props.setProperty("mail.store.protocol", "pop3");
//				props.setProperty("mail.pop3.host", "pop3.163.com");
				props.put("mail.imap.host", "imap.163.com");	            
	            props.put("mail.imap.auth.plain.disable", "true");
	             
				Session session = Session.getDefaultInstance(props);
				session.setDebug(false); 
				IMAPStore store = (IMAPStore)session.getStore("imap");
				store.connect(this.mail, this.mpwd);
				IMAPFolder folder = (IMAPFolder)store.getFolder("INBOX");
				folder.open(Folder.READ_WRITE);

				// 全部邮件
				Message[] messages = folder.getMessages();
				
				boolean seen = true;
				//System.err.println(messages.length);
				info("X");
				for (int i = messages.length-1; i >=0; i--) {
					seen = true;
					Message message = messages[i];
					// 删除邮件
					// message.setFlag(Flags.Flag.DELETED,true);
					message.getAllHeaders();
					info("A");
					Flags flags = message.getFlags();
					if (flags.contains(Flags.Flag.SEEN)){
						info("A1");
						seen = true;
					} else {
						info("A2");
						seen = false;
					}
					info("B");
					info(String.valueOf(seen));
					//info(message.get)
					info(message.getSubject());
					if(!seen&&message.getSubject().startsWith("QQ号码申诉联系方式确认")){
						info("C");
//						boolean isold = false;
//				        Flags flags = message.getFlags();
//				        Flags.Flag[] flag = flags.getSystemFlags(); 
//				        
//				        for (int ix = 0; ix< flag.length; ix++) {      
//				            if (flag[ix] == Flags.Flag.SEEN) {      
//				            	isold = true;
//				                break;
//				            }
//				        }

						String ssct = (String)message.getContent();
						message.setFlag(Flags.Flag.SEEN, false);
						if(ssct.contains("[<b>"+account.substring(0, 1))&&ssct.contains(account.substring(account.length()-1)+"</b>]")){
				        //if(!isold){
							info("D");
							message.setFlag(Flags.Flag.SEEN, true);	// 标记为已读
							rc = ssct.substring(ssct.indexOf("<b class=\"red\">")+15, ssct.indexOf("<b class=\"red\">")+23);
								
							System.err.println(rc);
							break;
						}
//						else {
//							info("D1");
//							message.setFlag(Flags.Flag.SEEN, false);
//							info("D2");
//						}
						info("E");
				        //}
					}else{
//						if(!seen){
//							message.setFlag(Flags.Flag.SEEN, false);
//						}
					}
				}
				info("F");
				folder.close(true);
				store.close();
				
				if(rc==null){
					tcconfirm++;					
					idx = 9;
					if(tcconfirm==3){
						info("找不到邮件[确认]，退出("+tcconfirm+")");
						this.run = false;
					}else{
						info("找不到邮件[确认]，继续尝试("+tcconfirm+")");
					}
					
				}else{
					info("找到邮件[确认]");
					idx++;
				}
			} catch (Exception e) {
				info("连接邮箱失败");
				e.printStackTrace();
				fb = true;
			}
			break;
		case 40:
			info("使用激活码继续申诉");
			try {
				get = new HttpGet(
						"http://aq.qq.com/cn2/appeal/appeal_mail_code_verify?VerifyType=0&VerifyCode="
								+ this.rc);

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");
				get.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_contact_confirm");

				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				json = new JSONObject(line);

				System.err.println(line);
				if ("1".equals(json.getString("ret_code"))) {
					// 验证成功
					info("继续申诉成功");
				} else if ("-1".equals(json.getString("ret_code"))){
					// 报错, 重新开始
					info("继续申诉失败: 频繁申诉");
					this.run = false;
					break;
				} else if("2".equals(json.getString("ret_code"))){
					info("继续申诉失败: 激活码错误，重新开始");
					idx = 0;
					
//					if(sf){ //已经收到停止信号
//						this.run = false;
//					}
					break;
				}
				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 41:
			info("提交原始密码和地区");
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_historyinfo_judge");

				post.setHeader("Connection", "keep-alive");
				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_contact_confirm");
										
				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("txtBackToInfo", "1"));
				nvps.add(new BasicNameValuePair("txtEmail", this.mail));
				nvps.add(new BasicNameValuePair("txtUin", this.account));
				nvps.add(new BasicNameValuePair("txtBackFromFd", ""));
				nvps.add(new BasicNameValuePair("txtEmailVerifyCode", this.rc));
				nvps.add(new BasicNameValuePair("txtOldPW1", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW1", ""));
				nvps.add(new BasicNameValuePair("txtOldPW2", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW2", this.password));
				nvps.add(new BasicNameValuePair("txtOldPW3", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW3", ""));
				nvps.add(new BasicNameValuePair("txtOldPW4", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW4", ""));
				nvps.add(new BasicNameValuePair("txtOldPW5", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW5", ""));
				nvps.add(new BasicNameValuePair("txtOldPW6", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW6", ""));
				
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry1", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince1", String.valueOf(Integer.parseInt(configuration.getProperty("P1"))-1)));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity1", Integer.parseInt(configuration.getProperty("C1"))==0?"-1":configuration.getProperty("C1")));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry1", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince1", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity1", "城市"));
				
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry2", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince2", String.valueOf(Integer.parseInt(configuration.getProperty("P2"))-1)));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity2", Integer.parseInt(configuration.getProperty("C2"))==0?"-1":configuration.getProperty("C2")));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry2", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince2", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity2", "城市"));
				
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry3", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince3", String.valueOf(Integer.parseInt(configuration.getProperty("P3"))-1)));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity3", Integer.parseInt(configuration.getProperty("C3"))==0?"-1":configuration.getProperty("C3")));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry3", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince3", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity3", "城市"));
				
				nvps.add(new BasicNameValuePair("ddlLocYear4", ""));
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry4", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince4", "-1"));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity4", "-1"));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry4", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince4", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity4", "城市"));
				
				nvps.add(new BasicNameValuePair("ddlRegType", "0"));
				nvps.add(new BasicNameValuePair("ddlRegYear", ""));
				nvps.add(new BasicNameValuePair("ddlRegMonth", ""));
				nvps.add(new BasicNameValuePair("ddlRegCountry", "0"));
				nvps.add(new BasicNameValuePair("ddlRegProvince", "-1"));
				nvps.add(new BasicNameValuePair("ddlRegCity", "-1"));
				nvps.add(new BasicNameValuePair("txtRegCountry", "国家"));
				nvps.add(new BasicNameValuePair("txtRegProvince", "省份"));
				nvps.add(new BasicNameValuePair("txtRegCity", "城市"));
				nvps.add(new BasicNameValuePair("txtRegMobile", ""));
				nvps.add(new BasicNameValuePair("ddlRegPayMode", "0"));
				nvps.add(new BasicNameValuePair("txtRegPayAccount", ""));
				
				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);

//				System.err.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 42:
			info("正在转向");
			try {
				get = new HttpGet(
						"http://aq.qq.com/cn2/appeal/appeal_mb2verify");

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();

//				line = EntityUtils.toString(entity);
//				System.err.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 43:
			info("进入好友辅助");
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_invite_friend");

				post.setHeader("Connection", "keep-alive");
				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_mb2verify");
						
				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("txtUserChoice", "2"));
				nvps.add(new BasicNameValuePair("txtOldDNAEmailSuffix", ""));
				nvps.add(new BasicNameValuePair("txtOldDNAAnswer3", ""));
				nvps.add(new BasicNameValuePair("txtOldDNAAnswer2", ""));
				nvps.add(new BasicNameValuePair("txtOldDNAAnswer1", ""));
				nvps.add(new BasicNameValuePair("txtBackToInfo", "1"));
				nvps.add(new BasicNameValuePair("txtBackFromFd", ""));
				nvps.add(new BasicNameValuePair("OldDNAMobile", ""));
				nvps.add(new BasicNameValuePair("OldDNAEmail", ""));
				nvps.add(new BasicNameValuePair("OldDNACertCardID", ""));
				
				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				// line = EntityUtils.toString(entity);

				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 44:
			info("跳过好友辅助");
			try {
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_end");

				post.setHeader("Connection", "keep-alive");
				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_invite_friend");
					
				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("txtPcMgr", "1"));
				nvps.add(new BasicNameValuePair("txtUserPPSType", "1"));
				nvps.add(new BasicNameValuePair("txtBackFromFd", "1"));
				nvps.add(new BasicNameValuePair("txtBackToInfo", "1"));
				nvps.add(new BasicNameValuePair("usernum", this.account));
				nvps.add(new BasicNameValuePair("FriendQQNum1", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum2", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum3", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum4", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum5", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum6", ""));
				nvps.add(new BasicNameValuePair("FriendQQNum7", ""));

				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);

				//System.err.println(line);
				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			
			break;
		case 45:
			//获取回执编号			
			info("等待"+itv+"秒，接收邮件[回执]");
			try {
				try{
					Thread.sleep(1000*itv);
				}catch(Exception e){
					sf = true;
					Thread.sleep(1000*4); //意外中断，继续等待
				}
				
				Properties props = new Properties();
//				props.setProperty("mail.store.protocol", "pop3");
//				props.setProperty("mail.pop3.host", "pop3.163.com");
				props.put("mail.imap.host", "imap.163.com");	            
	            props.put("mail.imap.auth.plain.disable", "true");
	             
				Session session = Session.getDefaultInstance(props);
				session.setDebug(false); 
				IMAPStore store = (IMAPStore)session.getStore("imap");
				store.connect(this.mail, this.mpwd);
				IMAPFolder folder = (IMAPFolder)store.getFolder("INBOX");
				folder.open(Folder.READ_WRITE);

				// 全部邮件
				Message[] messages = folder.getMessages();
				
				boolean seen = true;
				//System.err.println(messages.length);
				for (int i = messages.length-1; i >=0; i--) {
					seen = true;
					Message message = messages[i];
					// 删除邮件
					// message.setFlag(Flags.Flag.DELETED,true);

					Flags flags = message.getFlags();    
					if (flags.contains(Flags.Flag.SEEN)){
						seen = true;    
					} else {    
						seen = false;    
					}
		               
					if(!seen&&message.getSubject().startsWith("QQ号码申诉单已受理")){
						
//						boolean isold = false;      
//				        Flags flags = message.getFlags();      
//				        Flags.Flag[] flag = flags.getSystemFlags();      
//				        
//				        for (int ix = 0; ix< flag.length; ix++) {      
//				            if (flag[ix] == Flags.Flag.SEEN) {      
//				            	isold = true;
//				                break;
//				            }
//				        }

						String ssct = (String)message.getContent();
						message.setFlag(Flags.Flag.SEEN, false);
						if(ssct.contains("[<b>"+account.substring(0, 1))&&ssct.contains(account.substring(account.length()-1)+"</b>]")){
				        //if(!isold){
							message.setFlag(Flags.Flag.SEEN, true);	// 标记为已读
							rcl = ssct.substring(ssct.indexOf("<b class=\"red\">")+15, ssct.indexOf("<b class=\"red\">")+25);
							
							System.err.println(rcl);
							break;
						} 
//						else {
//							message.setFlag(Flags.Flag.SEEN, false);
////							message.getFlags().remove(Flags.Flag.SEEN);
//						}
				        //}
					}else{
//						if(!seen){
//							message.setFlag(Flags.Flag.SEEN, false);
//							//message.getFlags().remove(Flags.Flag.SEEN);
//						}
					}
				}
				folder.close(true);
				store.close();
				
				if(rcl==null){					
					tcback++;					
					idx = 15;
					if(tcback==3){
						info("找不到邮件[回执]，退出("+tcback+")->"+this.mail+"----"+this.mpwd);
						this.run = false;
					}else{
						info("找不到邮件[回执]，继续尝试("+tcback+")");
					}
				}else{
					info("找到邮件[回执]");
					idx++;
					this.finish = true; 
					info("申诉成功");
					this.run = false; //结束运行
				}
			} catch (Exception e) {
				info("连接邮箱失败");
				e.printStackTrace();
				fb = true;
			}

			break;
		default:
			break;
		}
	}

	private void info(String info){
		message = new EngineMessage();
		message.setTid(this.id);
		message.setType(EngineMessageType.IM_INFO);
		message.setData(info);

		DateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		String tm = format.format(new Date());
		
		System.err.println("["+this.account+"]"+info+"("+tm+")");
		Engine.getInstance().fire(message);
	}
	
	@Override
	public void update(Observable obj, Object arg) {
		final EngineMessage msg = (EngineMessage) arg;

		if (msg.getTid() == this.id || msg.getTid()==-1) { //-1, all tasks message
			int type = msg.getType();

			switch (type) {
			case EngineMessageType.OM_REQUIRE_MAIL:
				if(msg.getData()!=null){
					String[] ms = (String[]) msg.getData();
					System.err.println(ms[0] + "/" + ms[1] + "/" + ms[2]);
					this.mid = ms[0];
					this.mail = ms[1];
					this.mpwd = ms[2];
				}else {
					info("没有可用邮箱, 退出任务");
					this.run = false;
					
					//通知引擎
					EngineMessage message = new EngineMessage();
					//message.setTid(this.id);
					message.setType(EngineMessageType.IM_NO_EMAILS);
					// message.setData(obj);
					Engine.getInstance().fire(message);
				}
				break;
			case EngineMessageType.OM_RECONN: //系统准备重拨
				//System.err.println("TASK RECEIVED RECONN:"+rec);
				rec = !rec;
				break;
			case EngineMessageType.OM_PAUSE:
				pause  = !pause;
				break;
			default:
				break;
			}
		}
	}
	
	 private String byteArrayToHexString(byte[] b){  
	        StringBuffer resultSb = new StringBuffer();  
	        for (int i = 0; i < b.length; i++){  
	            resultSb.append(byteToHexString(b[i]));  
	        }  
	        return resultSb.toString();  
	    }  
	 private String byteToHexString(byte b){  
	        int n = b;  
	        if (n < 0)  
	            n = 256 + n;  
	        int d1 = n / 16;  
	        int d2 = n % 16;  
	        return hexDigits[d1] + hexDigits[d2];  
	    }  
	   private final static String[] hexDigits = {"0", "1", "2", "3", "4",  
	        "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};  
	   
	   private long encryptSkey(String sKey){
		   long i = 5381;
		   int j = 0;
		   int k = sKey.length();
//		   System.out.println(6190419136L&0XFFFFFFFFL);
		   while(j<k){
			   long n = i<<5;
			   if((n&0x80000000L)==0x80000000L){//最高位为1
				   n = n | 0xFFFFFFFF00000000L;
			   }else {
				   n = n & 0XFFFFFFFFL;
			   }
//			   System.out.println(i);
//			   System.out.println(n);
//			   System.out.println((byte)sKey.charAt(j));
			   i = i + (n + (byte)sKey.charAt(j));
			   //System.out.println((byte)sKey.charAt(j));
//			   System.out.println("------->"+i);
			   j++;
		   }
		   return i & 2147483647L;
	   }
}
