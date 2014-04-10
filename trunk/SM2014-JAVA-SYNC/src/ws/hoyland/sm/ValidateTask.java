package ws.hoyland.sm;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;


public class ValidateTask implements Runnable {
	private StringBuilder sb = null;
	private String line = null;
	
	private static String UAG = "Opera/9.25 (Windows NT 6.0; U; en)";
	
	public ValidateTask(StringBuilder sb, String line) {
		this.sb = sb;
		this.line = line;
	}

	public void run() {
		DefaultHttpClient client = null;
		HttpGet request = null;
//		private HttpPost post = null;
		HttpHost proxy = null;
		HttpResponse response = null;
		HttpEntity entity = null;
		String resp = null;
		
		try{
			String[] ms = line.split(":");
			proxy = new HttpHost(ms[0], Integer.parseInt(ms[1]));
	
			client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 4000);
			client.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 4000);

			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					proxy);
			
			String ru = "http://pt.3g.qq.com";
			request = new HttpGet(ru);
			request.setHeader("User-Agent", UAG);//
			request.setHeader("Connection", "close");//
			
			response = client.execute(request);
			entity = response.getEntity();
			resp = EntityUtils.toString(entity);

			if(resp.indexOf("手机腾讯网")!=-1){
				synchronized(sb){
					sb.append(line+"\r\n");
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if (entity != null) {
					EntityUtils.consume(entity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (request != null) {
				request.releaseConnection();
				request.abort();
			}

			if(client!=null){
				client.getConnectionManager().shutdown();
			}
		}
	}
}
