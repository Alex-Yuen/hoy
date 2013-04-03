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
		json.put("password", "981019.*");
		byte[] array = json.toString().getBytes();
		
		Crypter crypter = new Crypter();
				
		BigInteger bi1 = new BigInteger("-1");
		BigInteger bi2 = new BigInteger("B8008767A628A4F53BCB84C13C961A55BF87607DAA5BE0BA3AC2E0CB778E494579BD444F699885F4968CD9028BB3FC6FA657D532F1718F581669BDC333F83DC3", 16);
		BigInteger bi3 = new BigInteger("-1");
		bi3 = bi3.modPow(bi1, bi2);
		String hex = bi3.toString(16);//.toUpperCase();
		byte[] kk = Converts.hexStringToByte(hex);
		byte[] key = Converts.MD5Encode(kk);
		//System.out.println(key.length);
		byte[] bb = crypter.encrypt(array, key);
		System.out.println(Converts.bytesToHexString(bb));
	}

}
