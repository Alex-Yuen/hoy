package ws.hoyland.qt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.Random;

import org.json.JSONObject;

public class T5 {

	public T5() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		//int cc = 0;
		String token = "1406087124841854";	//手机上的令牌序列号
		BigInteger root = new BigInteger("2");
		BigInteger d = new BigInteger("B8008767A628A4F53BCB84C13C961A55BF87607DAA5BE0BA3AC2E0CB778E494579BD444F699885F4968CD9028BB3FC6FA657D532F1718F581669BDC333F83DC3", 16);

		InputStream is = T5.class.getResourceAsStream("/num2.txt");
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);
		String qq = null;
		//File f = new File("C:\\out.txt");
		//BufferedWriter output = new BufferedWriter(new FileWriter(f));
		while((qq=reader.readLine())!=null){
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
				URL url = new URL("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=4&client_type=2&client_ver=15&local_id=0&config_ver=100&pub_key="+fcpk+"&sys_ver=2.2");
				InputStream in = url.openStream();
				BufferedReader bin = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while((line=bin.readLine())!=null){
					sb.append(line);
					//System.out.println(line);
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
			
			
			
			
			
			
			String[] qs = qq.split("---");
			//1812664241, 09137123939
			//crypt request data
//			String uin = "1812664241";	//要绑定的其他QQ号
//			String password = "0913712393";	//要绑定的其他QQ号密码
			
			String uin = qs[0];
			String password = qs[1];
			
			
			
			
			
			//System.out.println(Converts.MD5EncodeToHex("123456"));
			json = new JSONObject();
			json.put("tkn_seq", token);
			json.put("password", Converts.MD5EncodeToHex(password));
			//System.out.println(json.toString());
			byte[] array = json.toString().getBytes();
			
			Crypter crypter = new Crypter();
			byte[] bb = crypter.encrypt(array, key);
			String data = Converts.bytesToHexString(bb);
			//System.out.println(data);
			
			sb = new StringBuffer();
			try{
				URL url = new URL("http://w.aq.qq.com/cn/mbtoken3/mbtoken3_upgrade_determin_v2?uin="+uin+"&sess_id="+sid+"&data="+data);
				System.out.println(url.toString());
				//output.write(url.toString()+"\n");
	//			InputStream in = url.openStream();
	//			BufferedReader bin = new BufferedReader(new InputStreamReader(in));
	//			String line = null;
	//			while((line=bin.readLine())!=null){
	//				sb.append(line);
	//				//System.out.println(line);
	//			}
	//			bin.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//cc++;
		}
		//output.close();
		
		//System.out.println(sb.toString());
	}

}
