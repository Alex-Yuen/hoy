package ws.hoyland.popularizer;

import java.util.Random;

public class Util {
	private static String CS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String DS = "1234567890";
	
	private static Random RND = new Random();
	
	public static String PID(){
		StringBuffer sb = new StringBuffer();
		
		for(int i=0;i<16;i++){
			sb.append(DS.charAt(RND.nextInt(10)));
		}
		
		return sb.toString();
	}
	
	public static String SID(){
		StringBuffer sb = new StringBuffer();
		
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				sb.append(CS.charAt(RND.nextInt(26)));
			}
			if(i!=3){
				sb.append('-');
			}
		}
		
		return sb.toString();
	}
}
