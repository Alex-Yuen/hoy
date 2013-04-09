package ws.hoyland.qt;

import java.math.BigInteger;
import java.net.SocketException;
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
	private String token;
	private String uin;
	private String password;
	private QT qt;
	private int err = -1;
	private Random rnd = new Random();
	private String px;
	
	public Task(ThreadPoolExecutor pool, List<String> proxies, QT qt, String token, String uin, String password) {
		this.pool = pool;
		this.proxies = proxies;
		this.qt = qt;
		this.token = token;
		this.uin = uin;
		this.password = password;
		synchronized(proxies){
			this.px = proxies.get(rnd.nextInt(proxies.size()));
		}
	}

	@Override
	public void run() {
		//String token = "1406087124841854"; // 手机上的令牌序列号
		String[] ips = px.split(":");
//		if(ips.length<1){
//			System.out.println(px);
//		}
//		System.getProperties().put("proxySet", "true");
//      System.getProperties().setProperty("http.proxyHost", ips[0]);
//      System.getProperties().setProperty("http.proxyPort", ips[1]);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpHost proxy = new HttpHost(ips[0], Integer.parseInt(ips[1]), "http");

        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000); 
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        
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

		// System.out.println(fcpk);
//		StringBuffer sb = new StringBuffer();
		try {
//			URL url = new URL(
//					"http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=4&client_type=2&client_ver=15&local_id=0&config_ver=100&pub_key="
//							+ fcpk + "&sys_ver=2.2");
//			InputStream in = url.openStream();
//			BufferedReader bin = new BufferedReader(new InputStreamReader(in));
			String line = null;
//			while ((line = bin.readLine()) != null) {
//				sb.append(line);
//				// System.out.println(line);
//			}
//			bin.close();
			
			HttpGet httpGet = new HttpGet("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=4&client_type=2&client_ver=15&local_id=0&config_ver=100&pub_key="
					+ fcpk + "&sys_ver=2.2");
			HttpResponse response = httpclient.execute(httpGet);
			//System.out.println(response1.getStatusLine());
			HttpEntity entity = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			line = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
			//entity.get
			httpGet.releaseConnection();
			
			if(!line.contains("sess_id")){
				//err ! ++;
				err = 106; //操作失败 { "uin": 2474713063, "err": 106, "info": "操作失败，请重试。" }
			}else{
				
				JSONObject json = new JSONObject(line);
				//System.out.println(json);
				String sid = json.getString("sess_id");
				String tcpk = json.getString("pub_key"); // get server's crypt pub key
				// System.out.println(tcpk);
		
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
		
				Crypter crypter = new Crypter();
				byte[] bb = crypter.encrypt(array, key);
				String data = Converts.bytesToHexString(bb);
				// System.out.println(data);
		
				//sb = new StringBuffer();
				httpGet = new HttpGet("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_upgrade_determin_v2?uin="
						+ uin + "&sess_id=" + sid + "&data=" + data);
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
				
				json = new JSONObject(line);
				err = json.getInt("err");
				
				if(err==120){ //网络异常
					synchronized(proxies){//删除代理
						proxies.remove(px);
					}
					//System.out.println(120);
					if(!this.pool.isShutdown()){
						Task task = new Task(pool, proxies, qt, token, uin, password);			
						this.pool.execute(task);
					}
					return;
				}
				
				if(err!=132&&err!=0&&err!=136){
					System.out.println(err+"->"+line);
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
		catch(ClientProtocolException ex){
			err = -5;
			synchronized(proxies){//删除代理
				proxies.remove(px);
			}
			//System.out.println(-5);
			//add new task
			if(!this.pool.isShutdown()){
				Task task = new Task(pool, proxies, qt, token, uin, password);			
				this.pool.execute(task);
			}
			return;//not need update
		}
		catch(SocketException ex){ //包含HttpHostConnectException
			err = -4;
			synchronized(proxies){//删除代理
				proxies.remove(px);
			}
			//System.out.println(-4);
			//add new task
			if(!this.pool.isShutdown()){
				Task task = new Task(pool, proxies, qt, token, uin, password);			
				this.pool.execute(task);
			}
			return;//not need update
		}
		catch(NoHttpResponseException ex){
			err = -3;
			synchronized(proxies){//删除代理
				proxies.remove(px);
			}
			//System.out.println(-3);
			//add new task
			if(!this.pool.isShutdown()){
				Task task = new Task(pool, proxies, qt, token, uin, password);			
				this.pool.execute(task);
			}
			return;//not need update
		}catch(ConnectTimeoutException ex){
			err = -2;
			synchronized(proxies){//删除代理
				proxies.remove(px);
			}
			//System.out.println(-2);
			//add new task
			if(!this.pool.isShutdown()){
				Task task = new Task(pool, proxies, qt, token, uin, password);			
				this.pool.execute(task);
			}
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
		//System.out.println("ERR:"+err);
		Display.getDefault().asyncExec(new Runnable(){

			@Override
			public void run() {
				if(qt.getFlag()){
					qt.up(uin+"----"+password, err);
				}
			}
			
		});
	}
}
