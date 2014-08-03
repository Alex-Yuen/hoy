package ws.hoyland.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

public class Util {
	public static byte[] genKey(int length) {
		byte[] rs = new byte[length];
		Random rnd = new Random();
		rnd.nextBytes(rs);
		return rs;
	}

	public static byte[] slice(byte[] src, int start, int length) {
		byte[] bs = new byte[length];
		for (int i = 0; i < length; i++) {
			bs[i] = src[start + i];
		}
		return bs;
	}

	public static byte[] pack(byte[] src) {
		int index = -1;
		for (int i = src.length; i > 0; i--) {
			if (src[i - 1] != 0x00) {
				index = i - 1;
				break;
			}
		}
		byte[] rs = new byte[index + 1];
		for (int i = 0; i < rs.length; i++) {
			rs[i] = src[i];
		}
		return rs;
	}

	public static byte[] reverse(byte[] src) {
		byte[] rs = new byte[src.length];
		for (int i = 0; i < rs.length; i++) {
			rs[i] = src[src.length - 1 - i];
		}
		return rs;
	}

	public static String genHostName(int length) {
		String cs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(cs.charAt(rnd.nextInt(cs.length())));
		}
		return sb.toString();
	}
	
	public static String format(Date date){
		return format.format(date);
	}
	
	private static DateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
}
