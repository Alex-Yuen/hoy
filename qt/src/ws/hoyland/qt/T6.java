package ws.hoyland.qt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.Random;

import org.json.JSONObject;

public class T6 {

	public T6() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		//int cc = 0;
		//String token = "1406087124841854";	//手机上的令牌序列号
		String token = "1508699085860441";
		token = "7058222321099474";
		token = "6630566601880940";
		token = "1321369602234168";
		token = "8431897550167556";
		//1161481585011854
		//7509235189224527
//		/2919474605578726
		//2762685361045300
		BigInteger root = new BigInteger("2");
		BigInteger d = new BigInteger("B8008767A628A4F53BCB84C13C961A55BF87607DAA5BE0BA3AC2E0CB778E494579BD444F699885F4968CD9028BB3FC6FA657D532F1718F581669BDC333F83DC3", 16);

		InputStream is = T5.class.getResourceAsStream("/num4.txt");
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);
		String qq = null;
		File f = new File("d:\\out.txt");
		BufferedWriter output = new BufferedWriter(new FileWriter(f));
		int cc = 0;
		while((qq=reader.readLine())!=null){
			if(cc>0&&cc%50==0) Thread.sleep(1000*5); //每59个休眠5秒，避免服务器繁忙
			//if(cc>50) break;
			//generate random e
			byte[] bs = new byte[14];
			Random r = new Random();
			
			bs[0] = (byte)(Math.abs(r.nextInt()) % 64);
			for(int i=0;i<bs.length;i++){
				bs[i] = (byte)(Math.abs(r.nextInt()) % 256);
			}
			
			BigInteger e = new BigInteger(bs);
			
			//generate my crypt-pub-key
			String fcpk = root.modPow(e, d).toString(16).toUpperCase();
			
			//System.out.println(fcpk);
			StringBuffer sb = new StringBuffer();
			try{
				URL url = new URL("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=4&client_type=2&client_ver=18&local_id=0&config_ver=100&tkn_seq="+token+"&ill_priv=android.permission.GET_TASKS&pub_key="+fcpk+"&sys_ver=2.2");
				System.out.println(url.toString());
				InputStream in = url.openStream();
				BufferedReader bin = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while((line=bin.readLine())!=null){
					sb.append(line);
					System.out.println(line);
				}
				bin.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			JSONObject json = new JSONObject(sb.toString());
			String sid = json.getString("sess_id");
			String tcpk = json.getString("pub_key"); //get server's crypt pub key
			//System.out.println(tcpk);
			
			//caculate the key
			BigInteger btcpk = new BigInteger(tcpk, 16);
			String sk = btcpk.modPow(e, d).toString(16).toUpperCase();
			byte[] key = Converts.MD5Encode(Converts.hexStringToByte(sk));		
			//System.out.println(key.length);
			
			

			
			String[] qs = qq.split("----");
			//1812664241, 09137123939
			//crypt request data
//			String uin = "1812664241";	//要绑定的其他QQ号
//			String password = "0913712393";	//要绑定的其他QQ号密码
			
			String uin = qs[0];
			String password = qs[1];
			
			
			System.out.println("");
			sb = new StringBuffer();
			try{
				//long ss = System.currentTimeMillis();
				URL url = new URL("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_query_captcha?aq_base_sid="+sid+"&uin="+uin+"&scenario_id=2");
//				System.out.print(cc+"\t");
				System.out.println(url.toString());
				//output.write(url.toString()+"\r\n");
				//output.flush();
				InputStream in = url.openStream();
				BufferedReader bin = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while((line=bin.readLine())!=null){
					sb.append(line);
					System.out.println(line);
				}
				System.out.println();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
			
			//System.out.println(Converts.MD5EncodeToHex("123456"));
			json = new JSONObject();
			json.put("tkn_seq", token);
			json.put("uin", Long.parseLong(uin));
//			json.put("uin", 68159276);
			json.put("pwd", Converts.MD5EncodeToHex(password));
			json.put("scenario_id", 2);
			System.out.println(json.toString());
			System.out.println("");
			byte[] array = json.toString().getBytes();
			
			Crypter crypter = new Crypter();
			byte[] bb = crypter.encrypt(array, key);
			String data = Converts.bytesToHexString(bb);
			//System.out.println(data);
			
			sb = new StringBuffer();
			try{
				//long ss = System.currentTimeMillis();
				URL url = new URL("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_verify_pwd?ap_base_sid="+sid+"&data="+data);
				//System.out.print(cc+"\t");
				System.out.println(url.toString());
				//output.write(url.toString()+"\r\n");
				//output.flush();
				InputStream in = url.openStream();
				BufferedReader bin = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while((line=bin.readLine())!=null){
					sb.append(line);
					System.out.println(line);
				}
				System.out.println();
				//System.out.println(System.currentTimeMillis()-ss);
				bin.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			cc++;
		}
		output.close();
		
		//System.out.println(sb.toString());
	}

}
