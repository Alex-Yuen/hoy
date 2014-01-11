package ws.hoyland.qqol;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import ws.hoyland.util.CopiedIterator;

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
		
		System.out.println(Math.pow(2, 5));
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		for(int i=0;i<20;i++){
			map.put(String.valueOf(i), "xx");
		}
		Iterator<String> it = null;
		for(int i=0;i<10;i++){
			it = new CopiedIterator(map.keySet().iterator());
			StringBuffer sb = new StringBuffer();
			while(it.hasNext()){
				sb.append(it.next()+"/");
			}
			System.out.println(sb.toString());
		}
	}

}
