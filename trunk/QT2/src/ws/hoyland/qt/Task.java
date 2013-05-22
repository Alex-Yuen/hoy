package ws.hoyland.qt;

import java.math.BigInteger;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.widgets.Display;
import org.json.JSONObject;

public class Task implements Runnable {
	
	private ThreadPoolExecutor pool;
	private List<String> proxies;
	private List<String> tokens;
	private String token;
	private String uin;
	private String password;
	private QT qt;
	private int err = -1;
	private int tkn_usable = -1;
	private Random rnd = new Random();
	private String px;
	private String line;
	private boolean useProxy;
	private boolean dna;
	private int isdna = -1;
	//private final String UAG = "Dalvik/1.2.0 (Linux; U; Android 2.2; sdk Build/FRF91)";
	private final String UAG = "QQMobileToken/4.7 CFNetwork/548.1.4 Darwin/11.0.0";	
	private int model = 0;
//	private long st;
//	private String time;
	
	public Task(ThreadPoolExecutor pool, List<String> proxies, List<String> tokens, QT qt, String uin, String password) {
		this.pool = pool;
		this.proxies = proxies;
		this.qt = qt;
		this.useProxy = qt.useProxy();
		this.dna = qt.dna();
		this.model = qt.model();
		this.tokens = tokens;
		//this.token = "1406087124841854";
//		this.token = "1475688552139964";
		//this.token = "6980777939050726";
		this.uin = uin;
		this.password = password;
		if(useProxy){
			synchronized(proxies){
				if(proxies.size()==0){
					qt.shutdown();
				}else{
					this.px = proxies.get(rnd.nextInt(proxies.size()));
				}
			}
		}
	}

	@Override
	public void run() {
//		this.st = System.currentTimeMillis();
		//String token = "1406087124841854"; // 手机上的令牌序列号
//		if(useProxy){
//		}
//		if(ips.length<1){
//			System.out.println(px);
//		}
//		System.getProperties().put("proxySet", "true");
//      System.getProperties().setProperty("http.proxyHost", ips[0]);
//      System.getProperties().setProperty("http.proxyPort", ips[1]);
        DefaultHttpClient httpclient = new DefaultHttpClient();

        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
//        HttpMethodParams
        //CoreConnectionPNames.;
        //CoreConnectionPNames.
        HttpHost proxy = null;
//        proxy = new HttpHost("1.50.213.245", 6668, "http");
//    	httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    	
        if(useProxy){
    		String[] ips = px.split(":");
            proxy = new HttpHost(ips[0], Integer.parseInt(ips[1]), "http");
        	httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        
		BigInteger root = new BigInteger("2");
		BigInteger d = new BigInteger(
				"B8008767A628A4F53BCB84C13C961A55BF87607DAA5BE0BA3AC2E0CB778E494579BD444F699885F4968CD9028BB3FC6FA657D532F1718F581669BDC333F83DC3",
				16);

		// if(cc>50) break;
		// generate random e
		byte[] bs = new byte[14];
		Random r = new Random();

		bs[0] = (byte) (Math.abs(r.nextInt()) % 64);
		for (int i = 0; i < bs.length; i++) {
			bs[i] = (byte) (Math.abs(r.nextInt()) % 256);
		}

		BigInteger e = new BigInteger(bs);

		// generate my crypt-pub-key
		String fcpk = root.modPow(e, d).toString(16).toUpperCase();
		//time = "[1]"+(System.currentTimeMillis()-this.st)+"->";
		// System.out.println(fcpk);
//		StringBuffer sb = new StringBuffer();
		try {
//			URL url = new URL(
//					"http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=4&client_type=2&client_ver=15&local_id=0&config_ver=100&pub_key="
//							+ fcpk + "&sys_ver=2.2");
//			InputStream in = url.openStream();
//			BufferedReader bin = new BufferedReader(new InputStreamReader(in));
//			while ((line = bin.readLine()) != null) {
//				sb.append(line);
//				// System.out.println(line);
//			}
//			bin.close();
			boolean gt = false;
			String ctk = null;
			
			synchronized(qt){
				if((ctk=qt.getCTK2())!=null){//原是getCTK()
					token = ctk;
				}else{
					gt = true;
				}
			}
			
//			synchronized(tokens){
//				if(tokens.size()!=0){
//					token = tokens.get(rnd.nextInt(tokens.size()));
//				}else{
//					gt = true;
//				}
//			}
			
			Crypter crypter = new Crypter();
			HttpGet httpGet = null;
			HttpResponse response = null;
			HttpEntity entity = null;
			JSONObject json = null;
			
			if(gt){ //need get a new token
				httpGet = new HttpGet("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=4&client_type=2&client_ver=15&local_id=0&config_ver=100&pub_key="
						+ fcpk + "&sys_ver=2.2");
				httpGet.setHeader("User-Agent", UAG);
				httpGet.setHeader("Connection", "Keep-Alive");
				
				response = httpclient.execute(httpGet);
				entity = response.getEntity();
				// do something useful with the response body
				// and ensure it is fully consumed
				line = EntityUtils.toString(entity);
				EntityUtils.consume(entity);
				httpGet.releaseConnection();
				
				if(!line.contains("sess_id")){
					//err ! ++;
					// err = 106; //操作失败 { "uin": 2474713063, "err": 106, "info": "操作失败，请重试。" }
					if(useProxy){
						synchronized(proxies){//删除代理
							proxies.remove(px);
							if(!this.pool.isShutdown()&&proxies.size()!=0){
								Task task = new Task(pool, proxies, tokens, qt, uin, password);			
								this.pool.execute(task);
							}
						}
					}else{
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
					return;
				}else{
					json = new JSONObject(line);
					String sid = json.getString("sess_id");
					String tcpk = json.getString("pub_key"); // get server's crypt pub key
					// System.out.println(tcpk);
	
					// caculate the key
					BigInteger btcpk = new BigInteger(tcpk, 16);
					String sk = btcpk.modPow(e, d).toString(16).toUpperCase();
					byte[] key = Converts.MD5Encode(Converts.hexStringToByte(sk));
					// System.out.println(key.length);
	
					String imei = "012419002637419";
					imei = Converts.bytesToHexString(Converts.MD5Encode(imei.getBytes()));
					// System.out.println(imei);
	
					// System.out.println(Converts.MD5EncodeToHex("123456"));
					json = new JSONObject();
					json.put("imei", imei);
					// System.out.println(json.toString());
					byte[] array = json.toString().getBytes();
	
					byte[] bb = crypter.encrypt(array, key);
					String data = Converts.bytesToHexString(bb);
					// System.out.println(data);
	
					//gen client-pub-key
					bs = new byte[14];
	
					bs[0] = (byte) (Math.abs(r.nextInt()) % 64);
					for (int i = 0; i < bs.length; i++) {
						bs[i] = (byte) (Math.abs(r.nextInt()) % 256);
					}
	
					line = null;
					e = new BigInteger(bs);
					String cpk = root.modPow(e, d).toString(16).toUpperCase();
					
					httpclient.getCookieStore().clear();
					httpGet = new HttpGet("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_activate_token?aq_base_sid="
							+ sid + "&data=" + data + "&clt_pub_key=" + cpk);
					httpGet.setHeader("User-Agent", UAG);
					httpGet.setHeader("Connection", "Keep-Alive");
					
					response = httpclient.execute(httpGet);
					//System.out.println(response1.getStatusLine());
					entity = response.getEntity();
					// do something useful with the response body
					// and ensure it is fully consumed
					line = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					//entity.get
					httpGet.releaseConnection();
					
					if(!line.contains("svc_pub_key")){
						//err ! ++;
						// err = 106; //操作失败 { "uin": 2474713063, "err": 106, "info": "操作失败，请重试。" }
						if(useProxy){
							synchronized(proxies){//删除代理
								proxies.remove(px);
								if(!this.pool.isShutdown()&&proxies.size()!=0){
									Task task = new Task(pool, proxies, tokens, qt, uin, password);			
									this.pool.execute(task);
								}
							}
						}else{
							Task task = new Task(pool, proxies, tokens, qt, uin, password);			
							this.pool.execute(task);
						}
						return;
					}else{
						json = new JSONObject(line);
						String spk = json.getString("svc_pub_key");
		
						btcpk = new BigInteger(spk, 16);
						sk = btcpk.modPow(e, d).toString(16).toUpperCase();
		
						MessageDigest md = MessageDigest.getInstance("SHA-256");
						md.update(Converts.hexStringToByte(sk));
						byte[] tk = md.digest();	//32 return 的处理
		
		//				System.out.println(tk.length);
						//System.out.println(tks.length);
						
						int[] tokenx = new int[16];
						//md = MessageDigest.getInstance("SHA-256");
						md.update(tk);           
						byte[] tks = md.digest();	//32
						//md = MessageDigest.getInstance("SHA-256");
						md.update(tks); 
						tks = md.digest();	//32
		//				System.out.println(tks.length);
						
						byte[] tklist = new byte[tks.length * 2];	//64
		//				System.out.println(tokenkey.length);
		//				System.out.println(tklist.length);
						for(int i=0;i<tks.length;i++){
							tklist[i*2] = (byte)((tks[i]&0xFF) >>> 4);
							tklist[i*2+1] = (byte)(tks[i]&0xF);
						}
						//System.out.println(tklist.length);
						int k = 0;
						
						for(int i=0;i<tokenx.length;i++){
							k = 0;
							for(int j=0;j<4;j++){
								k += tklist[j*16+i];
							}
							tokenx[i] = k%10;
						}
						
						if(tokenx[0]==0){
							tokenx[0]=1;
						}
						
						token = "";
						for(int i=0;i<tokenx.length;i++){
							token += tokenx[i];
							//System.out.print(tokenx[i]);
						}
						
						synchronized(qt){
							qt.setCTK(token);
						}
//						synchronized(tokens){
//							tokens.add(token);
//						}
					}
				}
			}//end get token
			
			line = null;
			e = new BigInteger(bs);
//			httpGet = new HttpGet("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=4&client_type=2&client_ver=15&local_id=0&config_ver=100&pub_key="
//					+ fcpk + "&sys_ver=2.2");
			httpclient.getCookieStore().clear();
			//httpGet = new HttpGet("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=4&client_type=2&client_ver=18&local_id=0&config_ver=100&tkn_seq="+token+"&ill_priv=android.permission.GET_TASKS&pub_key="+fcpk+"&sys_ver=2.2");
			
			httpGet = new HttpGet("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=1&client_type=2&client_ver=14&local_id=0&sys_ver=5.1.1_1&pub_key="+fcpk+"&tkn_seq="+token+"&config_ver=0");
			httpGet.setHeader("User-Agent", UAG);
			httpGet.setHeader("Connection", "Keep-Alive");
			
			response = httpclient.execute(httpGet);
			//System.out.println(response1.getStatusLine());
			entity = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			line = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
			//entity.get
			httpGet.releaseConnection();
			//time += "[2]"+(System.currentTimeMillis()-this.st)+"->";
			if(!line.contains("sess_id")){
				//err ! ++;
				// err = 106; //操作失败 { "uin": 2474713063, "err": 106, "info": "操作失败，请重试。" }
				if(useProxy){
					synchronized(proxies){//删除代理
						proxies.remove(px);
						if(!this.pool.isShutdown()&&proxies.size()!=0){
							Task task = new Task(pool, proxies, tokens, qt, uin, password);			
							this.pool.execute(task);
						}
					}
				}else{
					Task task = new Task(pool, proxies, tokens, qt, uin, password);			
					this.pool.execute(task);
				}
//				Display.getDefault().asyncExec(new Runnable(){
//					@Override
//					public void run() {
//						if(qt.getFlag()){
//							qt.uppx();
//						}
//					}						
//				});
				return;
			}else{
				//Thread.sleep(5*1000);
				json = new JSONObject(line);
				//System.out.println(json);
				String sid = json.getString("sess_id");
				String tcpk = json.getString("pub_key"); // get server's crypt pub key
				// System.out.println(tcpk);
		
				try{
					line = null;
					httpclient.getCookieStore().clear();
					httpGet = new HttpGet(
							"http://w.aq.qq.com/cn/mbtoken3/mbtoken3_query_captcha?aq_base_sid="+sid+"&uin="+uin+"&scenario_id=1");
					httpGet.setHeader("User-Agent", UAG);
					httpGet.setHeader("Connection", "Keep-Alive");
					response = httpclient.execute(httpGet);
					entity = response.getEntity();
					// do something useful with the response body
					// and ensure it is fully consumed
					line = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					httpGet.releaseConnection();
				}catch(Exception ex){
					//ex.printStackTrace();
					if(useProxy){
						synchronized(proxies){//删除代理
							proxies.remove(px);
							if(!this.pool.isShutdown()&&proxies.size()!=0){
								Task task = new Task(pool, proxies, tokens, qt, uin, password);			
								this.pool.execute(task);
							}
						}
					}else{
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
					return;
				}
				
//				System.out.println(line);
//				System.out.println();
				
				
				// caculate the key
				BigInteger btcpk = new BigInteger(tcpk, 16);
				String sk = btcpk.modPow(e, d).toString(16).toUpperCase();
				byte[] key = Converts.MD5Encode(Converts.hexStringToByte(sk));
				// System.out.println(key.length);
		
				// System.out.println(Converts.MD5EncodeToHex("123456"));
				json = new JSONObject();
				json.put("tkn_seq", token);
				json.put("password", Converts.MD5EncodeToHex(password));
				// System.out.println(json.toString());
				byte[] array = json.toString().getBytes();
		
				byte[] bb = crypter.encrypt(array, key);
				String data = Converts.bytesToHexString(bb);
				// System.out.println(data);
				//time += "[3]"+(System.currentTimeMillis()-this.st)+"->";
				//sb = new StringBuffer();
				httpclient.getCookieStore().clear();
				httpGet = new HttpGet("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_upgrade_determin_v2?uin="
						+ uin + "&sess_id=" + sid + "&data=" + data);
				httpGet.setHeader("User-Agent", UAG);
				httpGet.setHeader("Connection", "Keep-Alive");
//				httpGet.removeHeaders("Proxy-Connection");
				
//				url = new URL(
//							"http://w.aq.qq.com/cn/mbtoken3/mbtoken3_upgrade_determin_v2?uin="
//									+ uin + "&sess_id=" + sid + "&data=" + data);
					// System.out.print(cc+"\t");
					// System.out.println(url.toString());
					// output.write(url.toString()+"\r\n");
					// output.flush();
//				in = url.openStream();
//				bin = new BufferedReader(new InputStreamReader(in));
				line = null;
				response = httpclient.execute(httpGet);
				//System.out.println(response1.getStatusLine());
				entity = response.getEntity();
				// do something useful with the response body
				// and ensure it is fully consumed
				line = EntityUtils.toString(entity);
				EntityUtils.consume(entity);
				//entity.get
				httpGet.releaseConnection();
				//time += "[4]"+(System.currentTimeMillis()-this.st)+"->";
				json = new JSONObject(line);
				err = json.getInt("err");
				
				if(err==120){ //网络异常
					if(useProxy){
						synchronized(proxies){//删除代理
							proxies.remove(px);
							if(!this.pool.isShutdown()&&proxies.size()!=0){
								Task task = new Task(pool, proxies, tokens, qt, uin, password);			
								this.pool.execute(task);
							}
						}
					}else{
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
//					Display.getDefault().asyncExec(new Runnable(){
//						@Override
//						public void run() {
//							if(qt.getFlag()){
//								qt.uppx();
//							}
//						}
//					});
					//System.out.println(120);
					return;
				}else if(err==106){//操作错误, 网络波动 IP重复
					//System.out.println("ERR="+err+":"+line);
					if(useProxy){
						synchronized(proxies){//删除代理
							proxies.remove(px);
							if(!this.pool.isShutdown()&&proxies.size()!=0){
								Task task = new Task(pool, proxies, tokens, qt, uin, password);			
								this.pool.execute(task);
							}
						}
					}else{
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
//					Display.getDefault().asyncExec(new Runnable(){
//						@Override
//						public void run() {
//							if(qt.getFlag()){
//								qt.uppx();
//							}
//						}
//					});
					//System.out.println("[106]C");
					return;
				}else if(err==140&&model==2){
					//donoting, 计入错误
					
				}else if(err==140||err==141||err==142||err==201||err==122){//操作错误, 网络波动 token重复, 201 操作失败,122安全中心绑定失败,140验证码
					System.out.println("ERR="+err+":"+line);
//					synchronized(tokens){
//						tokens.remove(token); //删除当前token
//					}
					
					synchronized(qt){
						qt.setCTK(null);
					}
					
					if(useProxy){
						synchronized(proxies){//删除代理
							if(err==140&&model==1){
								//do nothing
							}else{
								proxies.remove(px);
							}
							
							if(!this.pool.isShutdown()&&proxies.size()!=0){
								Task task = new Task(pool, proxies, tokens, qt, uin, password);			
								this.pool.execute(task);
							}
						}
					}else{
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
					return;
				}else if(err==0){
					tkn_usable = json.getInt("tkn_usable");
					if(dna){
						isdna = json.getInt("isdna");
					}
					
					if(tkn_usable==0){ //令牌无效
//						synchronized(tokens){
//							tokens.remove(token); //删除当前token
//						}
						
						synchronized(qt){
							qt.setCTK(null);
						}
						
						if(useProxy){
							synchronized(proxies){//删除代理
								proxies.remove(px);
								if(!this.pool.isShutdown()&&proxies.size()!=0){
									Task task = new Task(pool, proxies, tokens, qt, uin, password);			
									this.pool.execute(task);
								}
							}
						}else{
							Task task = new Task(pool, proxies, tokens, qt, uin, password);			
							this.pool.execute(task);
						}
						return;
					}
				}
				
//				while ((line = bin.readLine()) != null) {
//						//sb.append(line);
//					json = new JSONObject(line);
//					err = json.getInt("err");
//		//				if(err==0){
//		//					
//		//				}else if(err==136){
//		//					
//		//				}else{
//		//					
//		//				}
//		//				if(err==120){
//		//					System.out.println(line);
//		//				}
//		//				System.out.println(line);
//				}
//				bin.close();
			}
		}
		catch(SocketTimeoutException ex){
			//System.out.println(this.time);
			err = -6;
			if(useProxy){
				synchronized(proxies){//删除代理
					proxies.remove(px);
					if(!this.pool.isShutdown()&&proxies.size()!=0){
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
				}
			}else{
				Task task = new Task(pool, proxies, tokens, qt, uin, password);			
				this.pool.execute(task);
			}
//			Display.getDefault().asyncExec(new Runnable(){
//				@Override
//				public void run() {
//					if(qt.getFlag()){
//						qt.uppx();
//					}
//				}						
//			});
			//System.out.println(-5);
			//add new task
			return;//not need update
		}
		catch(ClientProtocolException ex){
			err = -5;
			if(useProxy){
				synchronized(proxies){//删除代理
					proxies.remove(px);
					if(!this.pool.isShutdown()&&proxies.size()!=0){
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
				}
			}else{
				Task task = new Task(pool, proxies, tokens, qt, uin, password);			
				this.pool.execute(task);
			}
//			Display.getDefault().asyncExec(new Runnable(){
//				@Override
//				public void run() {
//					if(qt.getFlag()){
//						qt.uppx();
//					}
//				}						
//			});
			//System.out.println(-5);
			//add new task
			return;//not need update
		}
		catch(SocketException ex){ //包含HttpHostConnectException
			//System.out.println(this.time);
			err = -4;
			//System.out.print("SocketException:"+proxies.size()+"->");
			if(useProxy){
				synchronized(proxies){//删除代理
					proxies.remove(px);
					if(!this.pool.isShutdown()&&proxies.size()!=0){
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
				}
			}else{
				Task task = new Task(pool, proxies, tokens, qt, uin, password);			
				this.pool.execute(task);
			}
//			Display.getDefault().asyncExec(new Runnable(){
//				@Override
//				public void run() {
//					if(qt.getFlag()){
//						qt.uppx();
//					}
//				}						
//			});
			//System.out.println(-4);
			//add new task
			return;//not need update
		}
		catch(NoHttpResponseException ex){
			//System.out.println(this.time);
			err = -3;
			//System.out.print("NoHttpResponseException:"+proxies.size()+"->");
			if(useProxy){
				synchronized(proxies){//删除代理
					proxies.remove(px);
					if(!this.pool.isShutdown()&&proxies.size()!=0){
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
				}
			}else{
				Task task = new Task(pool, proxies, tokens, qt, uin, password);			
				this.pool.execute(task);
			}
//			Display.getDefault().asyncExec(new Runnable(){
//				@Override
//				public void run() {
//					if(qt.getFlag()){
//						qt.uppx();
//					}
//				}						
//			});
			//System.out.println(-3);
			//add new task
			return;//not need update
		}catch(ConnectTimeoutException ex){
			//System.out.println(this.time);
			err = -2;
			if(useProxy){
				synchronized(proxies){//删除代理
					proxies.remove(px);
					if(!this.pool.isShutdown()&&proxies.size()!=0){
						Task task = new Task(pool, proxies, tokens, qt, uin, password);			
						this.pool.execute(task);
					}
				}
			}else{
				Task task = new Task(pool, proxies, tokens, qt, uin, password);			
				this.pool.execute(task);
			}
//			Display.getDefault().asyncExec(new Runnable(){
//				@Override
//				public void run() {
//					if(qt.getFlag()){
//						qt.uppx();
//					}
//				}						
//			});
			//System.out.println(-2);
			//add new task
			return;//not need update
		}
//		catch(HttpHostConnectException ex){//代理超时
//			err = -2;
//			synchronized(proxies){//删除代理
//				proxies.remove(px);
//			}
//			System.out.println(-2);
//			//add new task
//			Task task = new Task(pool, proxies, qt, token, uin, password);
//			this.pool.execute(task);
//			return;//not need update
//		}
		catch (Exception ex) {
			err = -1;
			System.out.println(-1);
			ex.printStackTrace();
		}finally{
			httpclient.getConnectionManager().shutdown();
		}
		//time += "[5]"+(System.currentTimeMillis()-this.st)+"->";
		//System.out.println("ERR:"+err);
		Display.getDefault().asyncExec(new Runnable(){

			@Override
			public void run() {
				//if(qt.getFlag()){

				if(err!=132&&err!=0&&err!=136&&err!=140){
					System.err.println(err+"->"+uin+":"+password+"->"+px+"->"+line);
				}
				//if(err==0){
				//System.out.println(line);
				//}
//				if(err==136){
//					System.out.println(line);
//				}
				if(qt.getFlag()){
					//System.err.print(time);
					qt.up(uin+"----"+password, err, isdna);
				}
				//}
			}
			
		});
	}
}
