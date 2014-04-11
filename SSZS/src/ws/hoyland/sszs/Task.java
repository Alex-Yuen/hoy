package ws.hoyland.sszs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import ws.hoyland.util.Converts;
import ws.hoyland.util.IdentificationCardCodeUtil;

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

	private String sig = null;
	// private byte[] ib = null;
	// private byte[] image = null;

	private EngineMessage message = null;
	private int id = 0;
	private String account = null;
	private String password = null;
	private String[] pwds = null;

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
	private boolean np = false;//是否准备暂停
	private boolean finish = false;
	
	private int tcconfirm = 0;//try count of confirm
	private int tcback = 0;//try count of 回执
	
	private final String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
	private boolean pause = false;
	private boolean standard = true;

	private List<String> friends = null;
	private int helpcount = 0;
	private String loginsig = null;
	private String vcode = null;
	private String salt = null;
	private String[] fs = null;//当前好友信息
	
	private static String VERSION = "10074";
	static{
		HttpURLConnection connection = null;
		InputStream input = null;
		try{
			URL url = new URL("http://ui.ptlogin2.qq.com/ptui_ver.js?v="+Math.random());
			connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			
			input = connection.getInputStream();
			byte[] bs = new byte[input.available()];
			input.read(bs);
			String resp = new String(bs);
			VERSION = resp.substring(resp.indexOf("ptuiV(\"")+7, resp.indexOf("\");"));
			System.err.println("version:"+VERSION);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(input!=null){
				try{
					input.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(connection!=null){
				try{
					connection.disconnect();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}		
	}
	
	public Task(String line) {
		String[] ls = line.split("----");
		this.id = Integer.parseInt(ls[1]);
		this.account = ls[2];
		this.password = ls[3];
		this.friends = new ArrayList<String>();
				
		if("H".equals(ls[0])){
			standard = false;
		}else{ //标准导入，支持好友申诉
			for(int i=4;i<ls.length;i+=2){
				this.friends.add(ls[i]+"----"+ls[i+1]);
			}
		}

		pwds = new String[ls.length-3];
		for(int i=0;i<pwds.length;i++){
			pwds[i] = ls[i+3];
		}
		
		this.run = true;

		client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
	}

	@Override
	public void run() {
		info("开始运行");
		
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

		//阻塞等待暂停
		if(np){
			info("等待系统暂停");
			synchronized(PauseXObject.getInstance()){
				try{
					PauseXObject.getInstance().wait();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			info("等待系统暂停结束， 继续执行");
		}
		
			
			//阻塞等待重拨
			if(rec){
				info("等待重拨");
				synchronized(ReconObject.getInstance()){
					try{
						ReconObject.getInstance().wait();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				info("等待重拨结束， 继续执行");
			}
	
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
		int itv = Integer.parseInt(this.configuration.getProperty("MAIL_ITV"));
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
			try {
				get = new HttpGet("http://captcha.qq.com/getimgbysig?sig="
						+ URLEncoder.encode(this.sig, "UTF-8"));

				get.setHeader("User-Agent", UAG);
				get.setHeader("Content-Type", "text/html");
				get.setHeader("Accept", "text/html, */*");

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
		case 2:
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
		case 3:
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
		case 4:
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
		case 5:
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
		case 6:
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
		case 7:
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
		case 8:
			info("提交申诉资料");
			try {
				String code = IdentificationCardCodeUtil.getRandomAreaCode() + IdentificationCardCodeUtil.getRandomBirthdayCode(1980, 2000)
						+ IdentificationCardCodeUtil.getRandomSequenceCode();
				String randomCardCode = code + IdentificationCardCodeUtil.calculateVerifyCode(code);
				
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
				nvps.add(new BasicNameValuePair("txtIDCard", randomCardCode));
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
					info("申诉过于频繁");
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
		case 9: // 收邮件 
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
		case 10:
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
		case 11:
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
										
				/**
				 	txtUin:	1079066920
					txtRegProvince:	省份
					txtRegPayAccount:	
					txtRegMobile:	
					txtRegCountry:	国家
					txtRegCity:	城市
					txtOldPW6:	
					txtOldPW5:	
					txtOldPW4:	
					txtOldPW3:	
					txtOldPW2:	
					txtOldPW1:	
					txtLoginLocProvince4:	省份
					txtLoginLocProvince3:	省份
					txtLoginLocProvince2:	省份
					txtLoginLocProvince1:	省份
					txtLoginLocCountry4:	国家
					txtLoginLocCountry3:	国家
					txtLoginLocCountry2:	国家
					txtLoginLocCountry1:	国家
					txtLoginLocCity4:	城市
					txtLoginLocCity3:	城市
					txtLoginLocCity2:	城市
					txtLoginLocCity1:	城市
					txtHRegType:	0
					txtHRegTimeYear:	
					txtHRegTimeMonth:	
					txtHRegPayType:	0
					txtHRegPayAccount:	
					txtHRegMobile:	
					txtHRegLocationProvince:	-1
					txtHRegLocationCountry:	0
					txtHRegLocationCity:	-1
					txtHLoginLocYear4:	
					txtHLoginLocYear3:	
					txtHLoginLocYear2:	
					txtHLoginLocYear1:	
					txtHLoginLocProvince4:	-1
					txtHLoginLocProvince3:	0
					txtHLoginLocProvince2:	32
					txtHLoginLocProvince1:	12
					txtHLoginLocCountry4:	0
					txtHLoginLocCountry3:	0
					txtHLoginLocCountry2:	0
					txtHLoginLocCountry1:	0
					txtHLoginLocCity4:	-1
					txtHLoginLocCity3:	0
					txtHLoginLocCity2:	0
					txtHLoginLocCity1:	9
					txtEmailVerifyCode:	14567217
					txtEmail:	hoyzhang@163.com
					txtBackToInfo:	1
					txtBackFromFd:	1
					pwdOldPW6:	
					pwdOldPW5:	
					pwdOldPW4:	
					pwdOldPW3:	
					pwdOldPW2:	
					pwdOldPW1:	
					pwdHOldPW6:	
					pwdHOldPW5:	
					pwdHOldPW4:	
					pwdHOldPW3:	
					pwdHOldPW2:	
					pwdHOldPW1:	
					ddlRegYear:	
					ddlRegType:	0
					ddlRegProvince:	-1
					ddlRegPayMode:	0
					ddlRegMonth:	
					ddlRegCountry:	0
					ddlRegCity:	-1
					ddlLoginLocProvince4:	-1
					ddlLoginLocProvince3:	0
					ddlLoginLocProvince2:	32
					ddlLoginLocProvince1:	12
					ddlLoginLocCountry4:	0
					ddlLoginLocCountry3:	0
					ddlLoginLocCountry2:	0
					ddlLoginLocCountry1:	0
					ddlLoginLocCity4:	-1
					ddlLoginLocCity3:	0
					ddlLoginLocCity2:	0
					ddlLoginLocCity1:	9
					ddlLocYear4:	
				 * **/
				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("txtBackToInfo", "1"));
				nvps.add(new BasicNameValuePair("txtEmail", this.mail));
				nvps.add(new BasicNameValuePair("txtUin", this.account));
				nvps.add(new BasicNameValuePair("txtBackFromFd", ""));
				nvps.add(new BasicNameValuePair("txtEmailVerifyCode", this.rc));
				nvps.add(new BasicNameValuePair("pwdHOldPW1", ""));
				nvps.add(new BasicNameValuePair("txtOldPW1", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW1", this.password));
				nvps.add(new BasicNameValuePair("pwdHOldPW2", ""));
				nvps.add(new BasicNameValuePair("txtOldPW2", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW2", (this.pwds.length>1&&!standard)?this.pwds[1]:""));
				nvps.add(new BasicNameValuePair("pwdHOldPW3", ""));
				nvps.add(new BasicNameValuePair("txtOldPW3", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW3", (this.pwds.length>2&&!standard)?this.pwds[2]:""));
				nvps.add(new BasicNameValuePair("pwdHOldPW4", ""));
				nvps.add(new BasicNameValuePair("txtOldPW4", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW4", (this.pwds.length>3&&!standard)?this.pwds[3]:""));
				nvps.add(new BasicNameValuePair("pwdHOldPW5", ""));
				nvps.add(new BasicNameValuePair("txtOldPW5", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW5", (this.pwds.length>4&&!standard)?this.pwds[4]:""));
				nvps.add(new BasicNameValuePair("pwdHOldPW6", ""));
				nvps.add(new BasicNameValuePair("txtOldPW6", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW6", (this.pwds.length>5&&!standard)?this.pwds[5]:""));
				
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry1", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince1", String.valueOf(Integer.parseInt(configuration.getProperty("P1"))-1)));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity1", Integer.parseInt(configuration.getProperty("C1"))==0?"-1":configuration.getProperty("C1")));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry1", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince1", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity1", "城市"));
				nvps.add(new BasicNameValuePair("txtHLoginLocCountry1", "0"));
				nvps.add(new BasicNameValuePair("txtHLoginLocProvince1", String.valueOf(Integer.parseInt(configuration.getProperty("P1"))-1)));
				nvps.add(new BasicNameValuePair("txtHLoginLocCity1", Integer.parseInt(configuration.getProperty("C1"))==0?"-1":configuration.getProperty("C1")));
				
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry2", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince2", String.valueOf(Integer.parseInt(configuration.getProperty("P2"))-1)));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity2", Integer.parseInt(configuration.getProperty("C2"))==0?"-1":configuration.getProperty("C2")));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry2", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince2", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity2", "城市"));
				nvps.add(new BasicNameValuePair("txtHLoginLocCountry2", "0"));
				nvps.add(new BasicNameValuePair("txtHLoginLocProvince2", String.valueOf(Integer.parseInt(configuration.getProperty("P2"))-1)));
				nvps.add(new BasicNameValuePair("txtHLoginLocCity2", Integer.parseInt(configuration.getProperty("C2"))==0?"-1":configuration.getProperty("C2")));
			
				nvps.add(new BasicNameValuePair("ddlLoginLocCountry3", "0"));
				nvps.add(new BasicNameValuePair("ddlLoginLocProvince3", String.valueOf(Integer.parseInt(configuration.getProperty("P3"))-1)));
				nvps.add(new BasicNameValuePair("ddlLoginLocCity3", Integer.parseInt(configuration.getProperty("C3"))==0?"-1":configuration.getProperty("C3")));
				nvps.add(new BasicNameValuePair("txtLoginLocCountry3", "国家"));
				nvps.add(new BasicNameValuePair("txtLoginLocProvince3", "省份"));
				nvps.add(new BasicNameValuePair("txtLoginLocCity3", "城市"));
				nvps.add(new BasicNameValuePair("txtHLoginLocCountry2", "0"));
				nvps.add(new BasicNameValuePair("txtHLoginLocProvince3", String.valueOf(Integer.parseInt(configuration.getProperty("P3"))-1)));
				nvps.add(new BasicNameValuePair("txtHLoginLocCity3", Integer.parseInt(configuration.getProperty("C3"))==0?"-1":configuration.getProperty("C3")));
				
				nvps.add(new BasicNameValuePair("ddlLocYear4", ""));
				nvps.add(new BasicNameValuePair("txtHLoginLocCountry4", "0"));
				nvps.add(new BasicNameValuePair("txtHLoginLocProvince4", "-1"));
				nvps.add(new BasicNameValuePair("txtHLoginLocCity4", "-1"));
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
				
				nvps.add(new BasicNameValuePair("txtHLoginLocYear1", ""));
				nvps.add(new BasicNameValuePair("txtHLoginLocYear2", ""));				
				nvps.add(new BasicNameValuePair("txtHLoginLocYear3", ""));
				nvps.add(new BasicNameValuePair("txtHLoginLocYear4", ""));
				
				nvps.add(new BasicNameValuePair("txtHRegType", "0"));
				nvps.add(new BasicNameValuePair("txtHRegTimeYear", ""));				
				nvps.add(new BasicNameValuePair("txtHRegTimeMonth", ""));
				nvps.add(new BasicNameValuePair("txtHRegPayType", "0"));
				nvps.add(new BasicNameValuePair("txtHRegPayAccount", ""));
				nvps.add(new BasicNameValuePair("txtHRegMobile", ""));				
				nvps.add(new BasicNameValuePair("txtHRegLocationProvince", "-1"));
				nvps.add(new BasicNameValuePair("txtHRegLocationCountry", "0"));
				nvps.add(new BasicNameValuePair("txtHRegLocationCity", "-1"));
				
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
		case 12:
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
		case 13:
			info("选择好友辅助");
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
		case 14:
			info("填写好友辅助");
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
				int i = 0;
				for(;i<friends.size();i++){
					fs = friends.get(i).split("----");
					nvps.add(new BasicNameValuePair("FriendQQNum"+(i+1), fs[0]));	
				}
				for(;i<7;i++){
					nvps.add(new BasicNameValuePair("FriendQQNum"+(i+1), ""));
				}
//				nvps.add(new BasicNameValuePair("FriendQQNum2", ""));
//				nvps.add(new BasicNameValuePair("FriendQQNum3", ""));
//				nvps.add(new BasicNameValuePair("FriendQQNum4", ""));
//				nvps.add(new BasicNameValuePair("FriendQQNum5", ""));
//				nvps.add(new BasicNameValuePair("FriendQQNum6", ""));
//				nvps.add(new BasicNameValuePair("FriendQQNum7", ""));

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
		case 15:
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
					info("进入好友辅助申诉");
					//this.finish = true; 
					//info("申诉成功");
					//this.run = false; //结束运行
				}
			} catch (Exception e) {
				info("连接邮箱失败");
				e.printStackTrace();
				fb = true;
			}

			break;
		case 16:
			try {
				info("正在登录好友["+helpcount+"]");
				fs = friends.get(helpcount).split("----"); //当前好友信息
				
				client.getCookieStore().clear();//清空cookie
				get = new HttpGet(
						"https://ui.ptlogin2.qq.com/cgi-bin/login?appid=2001601&no_verifyimg=1&f_url=loginerroralert&lang=0&target=top&hide_title_bar=1&s_url=http%3A//aq.qq.com/cn2/index&qlogin_jumpname=aqjump&qlogin_param=aqdest%3Dhttp%253A//aq.qq.com/cn2/index&css=https%3A//aq.qq.com/v2/css/login.css");

//				get.setHeader("User-Agent", UAG);
//				get.setHeader("Content-Type", "text/html");
//				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();				
				line = EntityUtils.toString(entity);
				
				loginsig = line.substring(line.indexOf("var g_login_sig=encodeURIComponent") + 36).substring(0, 64);
				
				idx++;
			} catch (Exception e) {
				info("好友辅助申诉失败");
				e.printStackTrace();
				fb = true;
			}
			break;
		case 17:
			try {
				info("检查帐号["+helpcount+"]");
				get = new HttpGet("https://ssl.ptlogin2.qq.com/check?uin=" + fs[0] + "&appid=2001601&js_ver=" + VERSION + "&js_type=0&login_sig=" + loginsig + "&u1=http%3A%2F%2Faq.qq.com%2Fcn2%2Findex&r=" + Math.random());

//				get.setHeader("User-Agent", UAG);
//				get.setHeader("Content-Type", "text/html");
//				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();				
				line = EntityUtils.toString(entity);
				System.out.println("XXK:"+line);
				
				boolean nvc = line.charAt(14) == '1' ? true : false;
                 //没有做RSAKEY检查，默认是应该有KEY，用getEncryption；否则用getRSAEncryption

                 int fidx = line.indexOf(",");
                 int lidx = line.lastIndexOf(",");

                 vcode = line.substring(fidx + 2, fidx + 6);
                 salt = line.substring(lidx + 2, lidx + 34);

                 if (nvc)
                 {
                     //Encryption.getRSAEncryption(K, G)
                     idx++; //进入下一步验证码
                 }
                 else
                 {
                     idx += 5;
                 }
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 18:
			try {
				info("下载验证码");
				get = new HttpGet("https://ssl.captcha.qq.com/getimage?aid=2001601&" + Math.random() + "&uin=" + fs[0]);

//				get.setHeader("User-Agent", UAG);
//				get.setHeader("Content-Type", "text/html");
//				get.setHeader("Accept", "text/html, */*");

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
		case 19:
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
		case 20:
			try {
				info("提交验证码");
				get = new HttpGet("https://aq.qq.com/cn2/ajax/check_verifycode?session_type=on_rand&verify_code=" + result);

//				get.setHeader("User-Agent", UAG);
//				get.setHeader("Content-Type", "text/html");
//				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();				
				line = EntityUtils.toString(entity);
				
				json = new JSONObject(line);

				System.err.println(result);
				System.err.println("json="+line);
				 if ("0".equals(json.getString("Err")))
                 {
                     info("验证码正确");
                     vcode = result;
                     idx += 2;
                 }
                 else
                 {
                     info("验证码错误");
                     idx++;
                 }
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 21:
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
				
				idx = 16; // 重新开始登录 
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 22:
			try{
				info("提交登录请求"); 
				
				//"password=" + this.password + "&salt=" + this.salt + "&vcode="+this.vcode;
				
				//generate ECP
				byte[] rsx = Converts.MD5Encode(fs[1].getBytes());
				int psz = rsx.length;
	
				byte[] rsb = new byte[psz + 8];
				for (int i = 0; i < psz; i++) {
					rsb[i] = rsx[i];
				}
	
				salt = salt.substring(2);
	
				String[] salts = salt.split("\\\\x");
	
				for (int i = 0; i < salts.length; i++) {
					rsb[psz + i] = (byte) Integer.parseInt(salts[i], 16);
				}
	
				rsx = Converts.MD5Encode(rsb);
				String ecp = Converts.bytesToHexString(rsx).toUpperCase();
				rsx = Converts.MD5Encode((ecp + vcode.toUpperCase())
						.getBytes());
	
				ecp = Converts.bytesToHexString(rsx).toUpperCase();
				//gen ECP end
				
				get = new HttpGet("https://ssl.ptlogin2.qq.com/login?u=" + fs[0] + "&p=" + ecp + "&verifycode=" + vcode + "&aid=2001601&u1=http%3A%2F%2Faq.qq.com%2Fcn2%2Findex&h=1&ptredirect=1&ptlang=2052&from_ui=1&dumy=&fp=loginerroralert&action=4-14-" + System.currentTimeMillis() + "&mibao_css=&t=1&g=1&js_type=0&js_ver=" + VERSION + "&login_sig=" + loginsig);

//				get.setHeader("User-Agent", UAG);
//				get.setHeader("Content-Type", "text/html");
//				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();				
				line = EntityUtils.toString(entity);
				
				if (line.startsWith("ptuiCB('4'"))
                { //验证码错误
                    info("验证码错误");
                    idx = 16;
                }
                else if (line.startsWith("ptuiCB('0'"))
                { //成功登录
                    if (line.indexOf("haoma") != -1)
                    {
                        info("需要激活靓号");
                        run = false;
                    }
                    else
                    {
                        info("登录成功");
                        idx++;
                    }
                }
                else if (line.startsWith("ptuiCB('3'"))
                { //您输入的帐号或密码不正确，请重新输入
                    //finish = 2;
                    info("帐号或密码不正确, 退出任务");
                    run = false;
                }
                else if (line.startsWith("ptuiCB('19'"))
                { //帐号冻结，提示暂时无法登录
                    //finish = 3;
                    info("帐号冻结");
                    run = false;
                }
                else
                {
                    // ptuiCB('19' 暂停使用
                    // ptuiCB('7' 网络连接异常
                    info("帐号异常, 退出任务");
                    run = false;
                }
				
			}catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 23:
			try {
				info("打开辅助申诉页面["+helpcount+"]");
				get = new HttpGet(
						"http://aq.qq.com/cn2/appeal/appeal_fdappro");

//				get.setHeader("User-Agent", UAG);
//				get.setHeader("Content-Type", "text/html");
//				get.setHeader("Accept", "text/html, */*");

				response = client.execute(get);
				entity = response.getEntity();				
				//line = EntityUtils.toString(entity);
								
				idx++;
			} catch (Exception e) {
				e.printStackTrace();
				fb = true;
			}
			break;
		case 24:
			try {
				info("填写回执编号["+helpcount+"]");
				post = new HttpPost(
						"http://aq.qq.com/cn2/appeal/appeal_fdappro_end");

				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
//				post.setHeader("Accept", "text/html, */*");
				post.setHeader("Referer",
						"http://aq.qq.com/cn2/appeal/appeal_fdappro");

				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("checkbox1", "on"));
				nvps.add(new BasicNameValuePair("receipt1", rcl));
				nvps.add(new BasicNameValuePair("protocol", "on"));

				post.setEntity(new UrlEncodedFormEntity(nvps));

				response = client.execute(post);
				entity = response.getEntity();
				
				line = EntityUtils.toString(entity);
				if(line.indexOf("好友辅助申诉提交成功")!=-1){
					System.err.println("success:"+helpcount);
				}else{
					System.err.println("fail:"+helpcount);
					run = false;//没有必要继续辅助
				}
				
				if(helpcount<friends.size()){//继续辅助申诉
					helpcount++;
					idx = 16;
				}else{
					idx++;
				}
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
			case EngineMessageType.OM_NP: //系统准备暂停
				//System.err.println("TASK RECEIVED RECONN:"+rec);
				np = !np;
				break;
			case EngineMessageType.OM_PAUSE:
				pause  = !pause;
				break;
			default:
				break;
			}
		}
	}
}
