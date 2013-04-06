package ws.hoyland.cc;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import org.json.JSONObject;

public class Test {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		StringBuffer sb = new StringBuffer("SK");
//		sb.insert(1, (char)45);
//		System.out.println(sb.toString());
		
		JSONObject json = new JSONObject();
		json.put("tkn_seq", "1406087124841854");
		json.put("password", "e10adc3949ba59abbe56e057f20f883e".toUpperCase());
//		json.put("mobile_code", "123456");
		System.out.println(json.toString());
		byte[] array = json.toString().getBytes();
		System.out.println(array.length); //81 到88之间?
		
		Crypter crypter = new Crypter();
		
		BigInteger bi1 = new BigInteger("-1");
		BigInteger bi2 = new BigInteger("B8008767A628A4F53BCB84C13C961A55BF87607DAA5BE0BA3AC2E0CB778E494579BD444F699885F4968CD9028BB3FC6FA657D532F1718F581669BDC333F83DC3", 16);
		BigInteger bi3 = new BigInteger("-1");
		bi3 = bi3.modPow(bi1, bi2);
		String hex = bi3.toString(16);//.toUpperCase();
		System.out.println("HEX:"+hex);
		
		byte[] key = Converts.MD5Encode(Converts.hexStringToByte(hex));
		//System.out.println(key.length);
		byte[] bb = crypter.encrypt(array, key);
		System.out.println(bb.length);//64, 必须是88才对
		System.out.println(Converts.bytesToHexString(bb));
		
		System.out.println("EEE");
		byte[] kx = Converts.MD5Encode(Converts.hexStringToByte("82C49C3C3F122047F38F06DC38E05A967C573D446E1E428828942BD7248EC301B79A3110C3D1F1357C9A1922C79C424F12CC521585D8E2B31B8DF57AF31C3B32".toLowerCase()));
		System.out.println(kx[0]);
		System.out.println(kx[1]);
	}

}
