package ws.hoyland.qqol;

import ws.hoyland.util.Converts;

public class T3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Converts.bytesToHexString("hx".getBytes()));
		
		System.out.println(Converts.bytesToHexString(Converts.hexStringToByte(Long.toHexString(Long.valueOf("2927238399")).toUpperCase())));
	}

}
