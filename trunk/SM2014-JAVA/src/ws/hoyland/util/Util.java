package ws.hoyland.util;

import java.util.Random;

public class Util {

	public Util() {
		// TODO Auto-generated constructor stub
	}

	public static byte[] genKey(){
		return genKey(16);
	}
	
	public static byte[] genKey(int length){
		byte[] rs = new byte[length];
		Random rnd = new Random();
		rnd.nextBytes(rs);
		return rs;
	}
}
