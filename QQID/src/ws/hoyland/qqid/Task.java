package ws.hoyland.qqid;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
	private JSONArray array = null;
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
	private List<String> ux = new ArrayList<String>(); //好友列表
	
	private String ldw = null;
	
	private String rc = null; //red code in mail
	private String rcl = null; //回执编号

	protected String mid = null;
	private String mail = null;
	private String mpwd = null;
	
	private boolean sf = false; //stop flag from engine
	private boolean rec = false;//是否准备重拨
	private boolean finish = false;
	
	private boolean nvc = false; //need vc
	private String vcode = null;
	private String salt = null;
	
	private String ecp = null;//encrypted password
	private String checksigUrl = null;
	private String version = "10060";
	
	private boolean dfnvc = false;
	
	//private final String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
	private final String UAG = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
	private boolean pause = false;

	public Task(String line) {
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
		switch (index) {
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
			info("获取版本号");
			try {
				get = new HttpGet("http://ui.ptlogin2.qq.com/ptui_ver.js?v="+Math.random());

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
				get.setHeader("Connection", "keep-alive");				

				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				//",clienti
				version = line.substring(line.indexOf("ptuiV(\"")+7, line.indexOf("\");"));
				System.err.println("ver="+version);
								
				//idx++;
				idx += 3;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 3:	
			//idx++;
			info("提交日志数据");
			//set cookie, bad js repot not yet.
			try {
				get = new HttpGet("http://badjs.qq.com/cgi-bin/js_report?bid=110&level=2&mid=361167&msg=%E7%94%A8%E6%88%B7%E6%B2%A1%E6%9C%89%E7%99%BB%E5%BD%95QQ%7C_%7Chttp%3A%2F%2Fui.ptlogin2.qq.com%2Fcgi-bin%2Flogin%3Fappid%3D1006102%26daid%3D1%26style%3D13%26s_url%3Dhttp%3A%2F%2Fid.qq.com%2Findex.html%7C_%7CMozilla%2F5.0%20(Windows%20NT%206.1%3B%20WOW64%3B%20rv%3A25.0)%20Gecko%2F20100101%20Firefox%2F25.0&v="+Math.random());

				get.setHeader("User-Agent", UAG);
				//get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
				get.setHeader("Connection", "keep-alive");
				//get.setHeader("Cookie", "ptui_version="+version);
				
				//get.removeHeaders("Cookie2");
				
				response = client.execute(get);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);
				System.err.println(line);
								
				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 4:	
			//idx++;
			info("提交监测数据");
			//set cookie, bad js repot not yet.
			try {
				get = new HttpGet("http://ui.ptlogin2.qq.com/cgi-bin/report?id=358191&t="+Math.random());

				get.setHeader("User-Agent", UAG);
				//get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
				get.setHeader("Connection", "keep-alive");
				get.setHeader("Cookie", "ptui_version="+version);
				
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
		case 5:			
			info("检查帐号");
			//get ptui_checkVC
			try {
				get = new HttpGet("http://check.ptlogin2.qq.com/check?regmaster=&uin="+this.account+"&appid=1006102&js_ver="+version+"&js_type=1&login_sig="+loginSig+"&u1=http%3A%2F%2Fid.qq.com%2Findex.html&r="+Math.random());

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
		case 6://请求验证码
			info("下载验证码[登录]");
			try {
				get = new HttpGet("http://captcha.qq.com//getimage?uin="+this.account+"&aid=1006102&"
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
		case 7://识别验证码
			info("识别验证码[登录]");
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
		case 8:
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
				
				
				System.out.println("http://ptlogin2.qq.com/login?u="+this.account+"&p="+ecp+"&verifycode="+vcode+"&aid=1006102&u1=http%3A%2F%2Fid.qq.com%2Findex.html&h=1&ptredirect=1&ptlang=2052&daid=1&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=4-9-"+System.currentTimeMillis()+"&mibao_css=&t=1&g=1&js_ver="+version+"&js_type=1&login_sig="+loginSig+"&pt_rsa=0");
				get = new HttpGet("http://ptlogin2.qq.com/login?u="+this.account+"&p="+ecp+"&verifycode="+vcode+"&aid=1006102&u1=http%3A%2F%2Fid.qq.com%2Findex.html&h=1&ptredirect=1&ptlang=2052&daid=1&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=4-9-"+System.currentTimeMillis()+"&mibao_css=&t=1&g=1&js_ver="+version+"&js_type=1&login_sig="+loginSig+"&pt_rsa=0");

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
		case 9://报告错误验证码
			info("验证码错误，报告异常[登录]");
			try {
				//
				int reportErrorResult = -1;
				if(Engine.getInstance().getCptType()==0){
					reportErrorResult = YDM.INSTANCE.YDM_Report(codeID, false);
				}else{
					reportErrorResult = DM.INSTANCE.uu_reportError(codeID);
				}
				System.err.println(reportErrorResult);
				
				idx = 6; // 重新请求验证码 原来 idx = 0
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 10://成功登录之后
			try {
				info("检查签名");
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
		case 11: //获取好友列表
			//POST /cgi-bin/friends_home
			//ldw=2045554247&ver=20100914&from=mars936027210
			info("获取好友列表");
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
				
				ldw = String.valueOf(encryptSkey(skey));
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

				//System.err.println(line);
				
				json = new JSONObject(line);
				if(json.getInt("ec")==0){	
					//JSONObject finfo = json.getJSONObject("finfo");
					//finfo.
					
					try{
						array = json.getJSONArray("finfo");
					}catch(Exception e){
						info("没有好友");
						idx += 2;
						break;
					}
					//System.out.println("length:"+array.length());
					//uxc = array.length();
					for(int i=0; i<array.length(); i++){
						long user = array.getJSONObject(i).getLong("u");
						ux.add(String.valueOf(user));
						//System.out.println(user);
					}
				}else{
					info("获取好友列表失败, 退出任务");
					run = false;
					break;
				}

				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 12: //删除好友
			info("删除好友");
			try {
				//取50个好友
				String ulist = "";
				int uc = 0;
				for(uc=0;uc<ux.size()&&uc<50;uc++){
					ulist += ux.get(uc)+"-";
					ux.remove(uc);
				}
				
				if(ulist.endsWith("-")){
					ulist = ulist.substring(0, ulist.length()-1);
				}
				System.err.println("ulist:"+ulist);
				
				post = new HttpPost(
						"http://id.qq.com/cgi-bin/friends_del");

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
				nvps.add(new BasicNameValuePair("t", String.valueOf(uc+1)));
				if(dfnvc){
					dfnvc = false;
					nvps.add(new BasicNameValuePair("vc", vcode));
				}else{
					nvps.add(new BasicNameValuePair("vc", ""));
				}
				nvps.add(new BasicNameValuePair("u", ulist));
				nvps.add(new BasicNameValuePair("k", ""));
				nvps.add(new BasicNameValuePair("sppkey", ""));

				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();

				line = EntityUtils.toString(entity);

				json = new JSONObject(line);
				if(json.getInt("ec")==0){	
					//JSONObject finfo = json.getJSONObject("finfo");
					//finfo.
					/////成功
//					try{
//						array = json.getJSONArray("finfo");
//					}catch(Exception e){
//						info("没有好友");
//						idx += 2;
//						break;
//					}
//					System.out.println("length:"+array.length());
//					for(int i=0; i<array.length(); i++){
//						long user = array.getJSONObject(i).getLong("u");
//						System.out.println(user);
//					}
					info("删除成功");
					//判断是继续删除，还是去到idx = 15， 16 (异常，删除单向好友)
					if(ux.size()!=0){
						idx = 12;
						break;
					}else {
						idx = 16; //删除单向好友
						break;
					}
				}else if(json.getInt("ec")==6){//请求验证码
					idx++;
					break;
				}else{
					info("删除好友失败, 退出任务");
					run = false;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			//run = false;
			break;
		case 13: //删除好友出验证码
			info("下载验证码[删除好友]");
			try {
				get = new HttpGet("http://captcha.qq.com//getimage?uin="+this.account+"&aid=2000201&"
						+ Math.random());

				get.setHeader("User-Agent", UAG);
				//get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
				get.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Referer", "http://1.url.cn/id/flash/img/friends_mgr.swf?v=10029");
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
		case 14:
			info("识别验证码[删除好友]");
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
				dfnvc = true; //del f need vc
				idx = 12; //继续删除好友
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 15://报告错误验证码
			info("验证码错误，报告异常[删除好友]");
			try {
				//
				int reportErrorResult = -1;
				if(Engine.getInstance().getCptType()==0){
					reportErrorResult = YDM.INSTANCE.YDM_Report(codeID, false);
				}else{
					reportErrorResult = DM.INSTANCE.uu_reportError(codeID);
				}
				System.err.println(reportErrorResult);
				
				idx = 13; // 重新请求验证码 原来 idx = 0
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 16://获取单向好友
			info("获取单向好友列表");
			try {
				//TODO
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

				//System.err.println(line);
				
				json = new JSONObject(line);
				if(json.getInt("ec")==0){	
					//JSONObject finfo = json.getJSONObject("finfo");
					//finfo.
					
					try{
						array = json.getJSONArray("finfo");
					}catch(Exception e){
						info("没有好友");
						idx += 2;
						break;
					}
					//System.out.println("length:"+array.length());
					//uxc = array.length();
					for(int i=0; i<array.length(); i++){
						long user = array.getJSONObject(i).getLong("u");
						ux.add(String.valueOf(user));
						//System.out.println(user);
					}
				}else{
					info("获取好友列表失败, 退出任务");
					run = false;
					break;
				}

				idx++;
			} catch (Exception e) {
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
