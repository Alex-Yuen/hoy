package pro.ddz.server.util;

import java.util.Random;

public class Utility {
	public static int LENGTH = 6;

	public static String getRndDigitals(int length){
		StringBuffer result = new StringBuffer();
		Random rnd = new Random();
		for(int i=0;i<length;i++){
			result.append(rnd.nextInt(10));
		}
		return result.toString();
	}
	
	public static String md5(String original){
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i=0;i<10;i++)
			System.out.println(new Random().nextInt(2));
	}
}
