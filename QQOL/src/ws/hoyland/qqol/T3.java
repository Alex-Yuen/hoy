package ws.hoyland.qqol;

import java.util.Random;

import ws.hoyland.util.Converts;

public class T3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		System.out.println(Converts.bytesToHexString("hx".getBytes()));
//		
//		System.out.println(Converts.bytesToHexString(Converts.hexStringToByte(Long.toHexString(Long.valueOf("2927238399")).toUpperCase())));
		
		Random rnd = new Random();
		
		for(int i=0;i<20;i++){
			System.out.println(Integer.toHexString( (short)(rnd.nextInt(0xFFFF)&0xFFFF)));
			//rnd.nex
		}
	}

}
