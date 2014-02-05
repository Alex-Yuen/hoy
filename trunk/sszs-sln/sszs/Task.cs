using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ws.hoyland.util;
using System.IO;
using System.Text.RegularExpressions;
using System.Threading;
using System.Configuration;
using Newtonsoft.Json;
using System.Net;

namespace ws.hoyland.sszs
{
    class Task : Observer
    {
        	private String line;
	private bool runx = false;
	private bool fb = false; // break flag;
//	private bool fc = false; // continue flag;
	private int idx = 0; // method index;
	
	// private bool block = false;
	// private TaskObject obj = null;

    private static Random random = new Random();

	private HttpClient client;
	private string url = null;
	//private HttpGet get = null;

    private Stream data = null;
    private StreamReader reader = null;
	// private HttpUriRequest request = null;
    private string content = null;
    private byte[] bs = null;
    private string resp = null;

	private String sig = null;
	// private byte[] ib = null;
	// private byte[] image = null;

	private EngineMessage message = null;
	private int id = 0;
	private String account = null;
	private String password = null;
	private String[] pwds = null;

	private MemoryStream baos = null;
	private int codeID = -1;
	private String result;
	
	private String rc = null; //red code in mail
	private String rcl = null; //回执编号

	protected String mid = null;
	private String mail = null;
	private String mpwd = null;
	
	private bool sf = false; //stop flag from engine
	private bool rec = false;//是否准备重拨
	private bool np = false;//是否准备暂停
	private bool finish = false;

    private Configuration cfa = null;

	private int tcconfirm = 0;//try count of confirm
	private int tcback = 0;//try count of 回执
	
	private const String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
	private bool pause = false;
	private bool standard = true;

    private int size = -1;
    private byte[] bytes = new byte[4096];
    private StringBuilder pCodeResult = null;
    private int nCaptchaId = 0;

	public Task(String line) {
		// TODO Auto-generated constructor stub
        String[] ls = Regex.Split(line, "----");
		this.id = Int32.Parse(ls[1]);
		this.account = ls[2];
		this.password = ls[3];
		
		if("H".Equals(ls[0])){
			standard = false;
		}

		pwds = new String[ls.Length-3];
		for(int i=0;i<pwds.Length;i++){
			pwds[i] = ls[i+3];
		}
		
		this.runx = true;

		client = new HttpClient();
        client.Headers.Add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

        cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
	}

	
	public void run() {
		info("开始运行");

        ConfigurationManager.RefreshSection("appSettings");

		if(pause){//暂停
			info("暂停运行");
			lock(PauseCountObject.getInstance()){
				message = new EngineMessage();
				message.setType(EngineMessageType.IM_PAUSE_COUNT);
				Engine.getInstance().fire(message);
			}
			
			lock(PauseObject.getInstance()){
				try{
                    Monitor.Wait(PauseObject.getInstance());
				}catch(Exception e){
					throw e;
				}
			}
		}

		//阻塞等待暂停
		if(np){
			info("等待系统暂停");
			lock(PauseXObject.getInstance()){
				try{
					Monitor.Wait(PauseXObject.getInstance());
				}catch(Exception e){
					throw e;
				}
			}
			info("等待系统暂停结束， 继续执行");
		}
		
			
			//阻塞等待重拨
			if(rec){
				info("等待重拨");
				lock(ReconObject.getInstance()){
					try{
						Monitor.Wait(ReconObject.getInstance());
					}catch(Exception e){
						throw e;
					}
				}
				info("等待重拨结束， 继续执行");
			}
	
			if(sf){//如果此时有停止信号，直接返回
				info("初始化(任务取消)");
				return;
			}

		lock(StartObject.getInstance()){	
			//通知有新线程开始执行
			message = new EngineMessage();
			message.setType(EngineMessageType.IM_START);
			Engine.getInstance().fire(message);
		}
		
		// System.err.println(line);
		while (runx&&!sf) { //正常运行，以及未收到停止信号
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
			// throw e;
			// }
			// }
			// block = false;
			// }

			process(idx);

			try {
				if (data != null) {
					data.Close();
				}
				if (reader != null) {
					reader.Close();
				}
			} catch (Exception e) {
				throw e;
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
		

		lock(FinishObject.getInstance()){
			message = new EngineMessage();
			message.setTid(this.id);
			message.setType(EngineMessageType.IM_FINISH);
			message.setData(dt);
			Engine.getInstance().fire(message);
		}
		
		Engine.getInstance().removeObserver(this);
		
	}

	private void process(int index) {
		int itv = Int32.Parse(cfa.AppSettings.Settings["MAIL_ITV"].Value);
		switch (index) {
		case 0:
			info("正在请求验证码");
			try {
				url = 
						"http://captcha.qq.com/getsig?aid=523005413&uin=0&"
								+ random.NextDouble();

                data = client.OpenRead(url);
                reader = new StreamReader(data);
				line = reader.ReadToEnd();
				sig = line.Substring(20, line.IndexOf(";    ")-20);
				// System.err.println(sig);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 1:
			try {
				url = "http://captcha.qq.com/getimgbysig?sig="
						+ Util.UrlEncode(this.sig);

				 data = client.OpenRead(url);
                reader = new StreamReader(data);

				 bytes = new byte[4096];
                    size = data.Read(bytes, 0, bytes.Length);

                MemoryStream ms = new MemoryStream();
                ms.Write(bytes, 0, size);

				message = new EngineMessage();
				message.setType(EngineMessageType.IM_IMAGE_DATA);
				message.setData(ms);

				Engine.getInstance().fire(message);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 2:
			// 根据情况，阻塞或者提交验证码到UU
			info("正在识别验证码");
			try {
				int nCodeType;
                    pCodeResult = new StringBuilder("0000000000"); // 分配30个字节存放识别结果


                    // 例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型 http://www.yundama.com/price.html
                    nCodeType = 1004;

                    // 返回验证码ID，大于零为识别成功，返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
                    if (Engine.getInstance().getCptType() == 0)
                    {
                        nCaptchaId = YDMWrapper.YDM_DecodeByBytes(bytes, size, nCodeType, pCodeResult);
                    }
                    else
                    {
                        nCaptchaId = UUWrapper.uu_recognizeByCodeTypeAndBytes(bytes, size, nCodeType, pCodeResult);
                    }

                result = pCodeResult.ToString();
				
				
				//result = rsb.toString();
				//System.out.println("---"+result);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 3:
			info("开始申诉");
			try {
				url = "http://aq.qq.com/cn2/appeal/appeal_index";


				 data = client.OpenRead(url);

				// line = EntityUtils.toString(entity);
				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 4:
			info("检查申诉帐号");
			try {
				url = 
						"http://aq.qq.com/cn2/appeal/appeal_check_assist_account?UserAccount="
								+ this.account;

				 data = client.OpenRead(url);

				// line = EntityUtils.toString(entity);
				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 5:
			info("正在验证");
			try {
				url = 
						"http://aq.qq.com/cn2/ajax/check_verifycode?session_type=on_rand&verify_code="
								+ result + "&appid=523005413&CaptchaSig="
								+ Util.UrlEncode(this.sig);

				 data = client.OpenRead(url);
                reader = new StreamReader(data);
				line = reader.ReadToEnd();

				JsonTextReader jtr = new JsonTextReader(new StringReader(s));
                    jtr.Read();
                    jtr.Read();
                    jtr.Read();

				// System.err.println(line);
				if (jtr.Value.ToString().Equals("0")) {
					idx += 2;
				} else {
					// 报错
					idx++;
				}
				// System.err.println(line);

			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 6:
			info("验证码错误，报告异常");
			try {
				//
				int reportErrorResult = -1;
                    if (Engine.getInstance().getCptType() == 0)
                    {
                        reportErrorResult = YDMWrapper.YDM_Report(nCaptchaId, false);
                    }
                    else
                    {
                        reportErrorResult = UUWrapper.uu_reportError(nCaptchaId);
                    }
				
				idx = 0; // 重新开始
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 7:
			info("填写申诉资料");
			try {
				url = 
						"http://aq.qq.com/cn2/appeal/appeal_contact";


                content = "qqnum="+this.account+"&verifycode2=" + result + "&CaptchaSig=" + this.sig;
                
                 client.Headers[HttpRequestHeader.ContentType] = "application/x-www-form-urlencoded";

				bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                    //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();
                    resp = Encoding.UTF8.GetString(bs);

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
				throw e;
				fb = true;
			}
			break;
		case 8:
			info("提交申诉资料");
			try {
				url = 
						"http://aq.qq.com/cn2/appeal/appeal_contact_confirm";

                content = "txtLoginUin="+this.account+"&txtCtCheckBox=0&txtName="+Names.getInstance()
						.getName()+"&txtAddress=&txtIDCard=&txtContactQQ=&txtContactQQPW=&txtContactQQPW2=&radiobutton=mail&txtContactEmail"+this.mail+"&txtContactMobile=请填写您的常用手机";
				

				bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                    //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();
                    line = Encoding.UTF8.GetString(bs);
				
				if(line.IndexOf("申诉过于频繁")!=-1){
					//通知出现申诉频繁
					message = new EngineMessage();
					message.setType(EngineMessageType.IM_FREQ);

					//System.err.println("["+this.account+"]"+info);
					Engine.getInstance().fire(message);
					runx = false;
					break;
				}

				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 9: // 收邮件 
			info("等待"+itv+"秒，接收邮件[确认]");
			try {
				try{
					Thread.Sleep(1000*itv);
				}catch(Exception e){
					sf = true;
					Thread.Sleep(1000*4); //意外中断，继续等待
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
				
				bool seen = true;
				//System.err.println(messages.Length);
				info("X");
				for (int i = messages.Length-1; i >=0; i--) {
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
//						bool isold = false;
//				        Flags flags = message.getFlags();
//				        Flags.Flag[] flag = flags.getSystemFlags(); 
//				        
//				        for (int ix = 0; ix< flag.Length; ix++) {      
//				            if (flag[ix] == Flags.Flag.SEEN) {      
//				            	isold = true;
//				                break;
//				            }
//				        }

						String ssct = (String)message.getContent();
						message.setFlag(Flags.Flag.SEEN, false);
						if(ssct.contains("[<b>"+account.substring(0, 1))&&ssct.contains(account.substring(account.Length()-1)+"</b>]")){
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
						this.runx = false;
					}else{
						info("找不到邮件[确认]，继续尝试("+tcconfirm+")");
					}
					
				}else{
					info("找到邮件[确认]");
					idx++;
				}
			} catch (Exception e) {
				info("连接邮箱失败");
				throw e;
				fb = true;
			}
			break;
		case 10:
			info("使用激活码继续申诉");
			try {
				url = 
						"http://aq.qq.com/cn2/appeal/appeal_mail_code_verify?VerifyType=0&VerifyCode="
								+ this.rc;

				 data = client.OpenRead(url);
                reader = new StreamReader(data);
				line = reader.ReadToEnd();

				JsonTextReader jtr = new JsonTextReader(new StringReader(s));
                    jtr.Read();
                    jtr.Read();
                    jtr.Read();

				// System.err.println(line);

				//System.err.println(line);
				if ("1".Equals(jtr.Value.ToString())) {
					// 验证成功
					info("继续申诉成功");
				} else if ("-1".Equals(jtr.Value.ToString())){
					// 报错, 重新开始
					info("继续申诉失败: 频繁申诉");
					this.runx = false;
					break;
				} else if("2".Equals(jtr.Value.ToString())){
					info("继续申诉失败: 激活码错误，重新开始");
					idx = 0;
					
//					if(sf){ //已经收到停止信号
//						this.runx = false;
//					}
					break;
				}
				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 11:
			info("提交原始密码和地区");
			try {
				url = 
						"http://aq.qq.com/cn2/appeal/appeal_historyinfo_judge";
                 content = "txtLoginUin="+this.account+"&txtCtCheckBox=0&txtName="+Names.getInstance()
						.getName()+"&txtAddress=&txtIDCard=&txtContactQQ=&txtContactQQPW=&txtContactQQPW2=&radiobutton=mail&txtContactEmail"+this.mail+"&txtContactMobile=请填写您的常用手机";
				

				bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                    //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();
                    line = Encoding.UTF8.GetString(bs);

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
				nvps.add(new BasicNameValuePair("pwdOldPW2", (this.pwds.Length>1&&!standard)?this.pwds[1]:""));
				nvps.add(new BasicNameValuePair("pwdHOldPW3", ""));
				nvps.add(new BasicNameValuePair("txtOldPW3", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW3", (this.pwds.Length>2&&!standard)?this.pwds[2]:""));
				nvps.add(new BasicNameValuePair("pwdHOldPW4", ""));
				nvps.add(new BasicNameValuePair("txtOldPW4", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW4", (this.pwds.Length>3&&!standard)?this.pwds[3]:""));
				nvps.add(new BasicNameValuePair("pwdHOldPW5", ""));
				nvps.add(new BasicNameValuePair("txtOldPW5", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW5", (this.pwds.Length>4&&!standard)?this.pwds[4]:""));
				nvps.add(new BasicNameValuePair("pwdHOldPW6", ""));
				nvps.add(new BasicNameValuePair("txtOldPW6", ""));
				nvps.add(new BasicNameValuePair("pwdOldPW6", (this.pwds.Length>5&&!standard)?this.pwds[5]:""));
				
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
				
//				System.err.println(line);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 12:
			info("正在转向");
			try {
				url = 
						"http://aq.qq.com/cn2/appeal/appeal_mb2verify";

				
                data = client.OpenRead(url);

//				line = EntityUtils.toString(entity);
//				System.err.println(line);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 13:
			info("进入好友辅助");
			try {
				url = 
						"http://aq.qq.com/cn2/appeal/appeal_invite_friend";

				content = "txtLoginUin="+this.account+"&txtCtCheckBox=0&txtName="+Names.getInstance()
						.getName()+"&txtAddress=&txtIDCard=&txtContactQQ=&txtContactQQPW=&txtContactQQPW2=&radiobutton=mail&txtContactEmail"+this.mail+"&txtContactMobile=请填写您的常用手机";
				

				bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                    //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();
                    line = Encoding.UTF8.GetString(bs);

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
				
				// line = EntityUtils.toString(entity);

				// System.err.println(line);

				idx++;
			} catch (Exception e) {
				throw e;
				fb = true;
			}
			break;
		case 14:
			info("跳过好友辅助");
			try {
				url = 
						"http://aq.qq.com/cn2/appeal/appeal_end";
                content = "txtLoginUin=" + this.account + "&txtCtCheckBox=0&txtName=" + Names.getInstance()
                        .getName() + "&txtAddress=&txtIDCard=&txtContactQQ=&txtContactQQPW=&txtContactQQPW2=&radiobutton=mail&txtContactEmail" + this.mail + "&txtContactMobile=请填写您的常用手机";


                bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();
                line = Encoding.UTF8.GetString(bs);

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
                
				//System.err.println(line);
				idx++;
			} catch (Exception e) {
				throw e;
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
				
				bool seen = true;
				//System.err.println(messages.Length);
				for (int i = messages.Length-1; i >=0; i--) {
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
						
//						bool isold = false;      
//				        Flags flags = message.getFlags();      
//				        Flags.Flag[] flag = flags.getSystemFlags();      
//				        
//				        for (int ix = 0; ix< flag.Length; ix++) {      
//				            if (flag[ix] == Flags.Flag.SEEN) {      
//				            	isold = true;
//				                break;
//				            }
//				        }

						String ssct = (String)message.getContent();
						message.setFlag(Flags.Flag.SEEN, false);
						if(ssct.contains("[<b>"+account.substring(0, 1))&&ssct.contains(account.substring(account.Length()-1)+"</b>]")){
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
						this.runx = false;
					}else{
						info("找不到邮件[回执]，继续尝试("+tcback+")");
					}
				}else{
					info("找到邮件[回执]");
					idx++;
					this.finish = true; 
					info("申诉成功");
					this.runx = false; //结束运行
				}
			} catch (Exception e) {
				info("连接邮箱失败");
				throw e;
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
	
	
	public void update(Observable obj, Object arg) {
		const EngineMessage msg = (EngineMessage) arg;

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
					this.runx = false;
					
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
}
