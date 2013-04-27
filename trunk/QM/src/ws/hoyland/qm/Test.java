package ws.hoyland.qm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String k = "1*12";
		System.out.println(k.replaceAll("\\*", "A"));
		
		try{
			URL url = new URL("http://iframe.ip138.com/ic.asp");
			InputStream is = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "gb2312"));  
	        String line = null;
	        StringBuffer sb = new StringBuffer();
	        while ((line=br.readLine())!= null) {
	        	sb.append(line);
	        }
	
			String ip = sb.toString();
			//System.out.println(ip);
	        int index = ip.indexOf("您的IP是：[");
	        ip = ip.substring(index+7);
	        
	        index = ip.indexOf("]");
	        ip = ip.substring(0, index);																							        
	        System.err.println("ip="+ip);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
