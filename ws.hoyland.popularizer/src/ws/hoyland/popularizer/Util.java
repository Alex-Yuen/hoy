package ws.hoyland.popularizer;

import java.util.Random;

public class Util {
	private static String CS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static Random RND = new Random();
	
	public static String PID(){
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
