package ws.hoyland.qt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Random;

import org.json.JSONObject;

public class T4 {

	public T4() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		BigInteger root = new BigInteger("2");
		BigInteger d = new BigInteger(
				"B8008767A628A4F53BCB84C13C961A55BF87607DAA5BE0BA3AC2E0CB778E494579BD444F699885F4968CD9028BB3FC6FA657D532F1718F581669BDC333F83DC3",
				16);

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
		StringBuffer sb = new StringBuffer();
		try {
			URL url = new URL(
					"http://w.aq.qq.com/cn/mbtoken3/mbtoken3_exchange_key_v2?mobile_type=4&client_type=2&client_ver=15&local_id=0&config_ver=100&pub_key="
							+ fcpk + "&sys_ver=2.2");
			InputStream in = url.openStream();
			BufferedReader bin = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = bin.readLine()) != null) {
				sb.append(line);
				 System.out.println(line);
			}
			bin.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		JSONObject json = new JSONObject(sb.toString());
		String sid = json.getString("sess_id");
		String tcpk = json.getString("pub_key"); // get server's crypt pub key
		// System.out.println(tcpk);

		// caculate the key
		BigInteger btcpk = new BigInteger(tcpk, 16);
		String sk = btcpk.modPow(e, d).toString(16).toUpperCase();
		byte[] key = Converts.MD5Encode(Converts.hexStringToByte(sk));
		// System.out.println(key.length);

		//String imei = "012419002637419";
		String imei = "4615336679060341";
		imei = Converts.bytesToHexString(Converts.MD5Encode(imei.getBytes()));
		// System.out.println(imei);

		// System.out.println(Converts.MD5EncodeToHex("123456"));
		json = new JSONObject();
		json.put("imei", imei);
		// System.out.println(json.toString());
		byte[] array = json.toString().getBytes();

		Crypter crypter = new Crypter();
		byte[] bb = crypter.encrypt(array, key);
		String data = Converts.bytesToHexString(bb);
		// System.out.println(data);

		//gen client-pub-key
		bs = new byte[14];

		bs[0] = (byte) (Math.abs(r.nextInt()) % 64);
		for (int i = 0; i < bs.length; i++) {
			bs[i] = (byte) (Math.abs(r.nextInt()) % 256);
		}

		e = new BigInteger(bs);
		String cpk = root.modPow(e, d).toString(16).toUpperCase();

		sb = new StringBuffer();
		try {
			URL url = new URL(
					"http://w.aq.qq.com/cn/mbtoken3/mbtoken3_activate_token?aq_base_sid="
							+ sid + "&data=" + data + "&clt_pub_key=" + cpk);
			// System.out.print(cc+"\t");
			System.out.println(url.toString());
			// output.write(url.toString()+"\r\n");
			// output.flush();
			InputStream in = url.openStream();
			BufferedReader bin = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = bin.readLine()) != null) {
				sb.append(line);
				System.out.println(line);
			}
			bin.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		json = new JSONObject(sb.toString());
		String spk = json.getString("svc_pub_key");

		btcpk = new BigInteger(spk, 16);
		sk = btcpk.modPow(e, d).toString(16).toUpperCase();

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(Converts.hexStringToByte(sk));
		byte[] tk = md.digest();	//32 return 的处理

//		System.out.println(tk.length);
		//System.out.println(tks.length);

		
		int[] token = new int[16];
		//md = MessageDigest.getInstance("SHA-256");
		md.update(tk);           
		byte[] tks = md.digest();	//32
		//md = MessageDigest.getInstance("SHA-256");
		md.update(tks); 
		tks = md.digest();	//32
//		System.out.println(tks.length);
		
		byte[] tklist = new byte[tks.length * 2];	//64
//		System.out.println(tokenkey.length);
//		System.out.println(tklist.length);
		for(int i=0;i<tks.length;i++){
			tklist[i*2] = (byte)((tks[i]&0xFF) >>> 4);
			tklist[i*2+1] = (byte)(tks[i]&0xF);
		}
		//System.out.println(tklist.length);
		int k = 0;
		
		for(int i=0;i<token.length;i++){
			k = 0;
			for(int j=0;j<4;j++){
				k += tklist[j*16+i];
			}
			token[i] = k%10;
		}
		
		if(token[0]==0){
			token[0]=1;
		}
		
		for(int i=0;i<token.length;i++){
			System.out.print(token[i]);
		}
	}

}
