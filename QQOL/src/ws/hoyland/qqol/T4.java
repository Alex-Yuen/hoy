package ws.hoyland.qqol;

import java.io.ByteArrayOutputStream;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Cookie;

public class T4 {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Cookie cookie = Cookie.getInstance();
//		
//		//cookie.put("123", "X", "F".getBytes());
//		System.out.println(cookie.get("123"));
		try{
		ByteArrayOutputStream bsofpwd = new ByteArrayOutputStream();
		bsofpwd.write(Converts.MD5Encode("1"));
		bsofpwd.write(new byte[]{
				0x00, 0x00, 0x00, 0x00
		});
		bsofpwd.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf("123456")).toUpperCase()));
		byte[] pwdkey = Converts.MD5Encode(bsofpwd.toByteArray());
		System.out.println(Converts.bytesToHexString(pwdkey));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
