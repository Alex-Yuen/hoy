package net.xland.aqq.service;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<Integer, StringBuffer> futures =  new LinkedHashMap<Integer, StringBuffer>();
		StringBuffer sb = new StringBuffer();
//		sb.append("1234");
		futures.put(0x1123, sb);
		
		sb.append("KKK");
		
		System.out.println(futures.get(0x1123).toString());
	}

}
