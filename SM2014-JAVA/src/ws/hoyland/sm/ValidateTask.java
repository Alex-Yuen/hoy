package ws.hoyland.sm;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;


public class ValidateTask implements Runnable {
	private StringBuilder sb = null;
	private String line = null;
	
	private static String UAG = "Opera/9.25 (Windows NT 6.0; U; en)";
	
	public ValidateTask(StringBuilder sb, String line) {
		this.sb = sb;
		this.line = line;
	}

	public void run() {
		HttpGet request = null;
//		private HttpPost post = null;
		HttpHost proxy = null;
		
		try{
			String[] ms = line.split(":");
			proxy = new HttpHost(ms[0], Integer.parseInt(ms[1]));
	
			RequestConfig config = RequestConfig.custom()
				 	.setSocketTimeout(4000)
	                .setConnectTimeout(4000)
	                .setConnectionRequestTimeout(4000)
                    .setProxy(proxy)
                    .build();
			
			String ru = "http://pt.3g.qq.com";
			request = new HttpGet(ru);
			request.setHeader("User-Agent", UAG);//
			request.setHeader("Connection", "close");//
			
			request.setConfig(config);
			
			Engine.getInstance().validate(sb, line, request);
				
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
	}
}
