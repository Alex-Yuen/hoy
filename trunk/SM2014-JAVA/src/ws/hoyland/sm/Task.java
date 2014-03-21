package ws.hoyland.sm;

import java.util.Observable;
import java.util.Observer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.util.EntityUtils;

import ws.hoyland.util.EngineMessage;
import ws.hoyland.util.EngineMessageType;
import ws.hoyland.util.SyncUtil;

public class Task implements Runnable, Observer {
	private int id = 0;
	private String account = null;
	private String password = null;
	private String line;
	
	private DefaultHttpAsyncClient client = null;
	private HttpGet request = null; 
	private EngineMessage message = null;
	
	//private boolean run = false;
	
	private static String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
	
	public Task(String line) {
		this.line = line;
		
		String[] ls = line.split("----");
		this.id = Integer.parseInt(ls[1]);
		this.account = ls[2];
		this.password = ls[3];
		
		//this.run = true;
	}

	@Override
	public void update(Observable obj, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		synchronized(SyncUtil.START_OBJECT){	
			//通知有新线程开始执行
			message = new EngineMessage();
			message.setType(EngineMessageType.IM_START);
			Engine.getInstance().fire(message);
		}
		
		try{
			client = new DefaultHttpAsyncClient();
			
			String url = "http://pt.3g.qq.com/login?act=json&format=2&bid_code=house_touch&r=#" + "&qq=" + account + "&pmd5=" + Util.UMD5X(password) + "&go_url=http%3A%2F%2Fhouse60.3g.qq.com%2Ftouch%2Findex.jsp%3Fsid%3DAd_JZ1k2ZviFLkV2nvFt7005%26g_ut%3D3%26g_f%3D15124";
			url = url.replaceAll("#", arg1);
			
			request = new HttpGet(url);

			client.execute(request, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    //System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
                	HttpEntity entity = response.getEntity();
                	if (entity != null) {
    					EntityUtils.consume(entity);
    				}
                	
                	release();
                }

                public void failed(final Exception ex) {
                    ex.printStackTrace();
                    release();
                }

                public void cancelled() {
                	release();
                }
                
                private void release(){
                	if (request != null) {
        				request.releaseConnection();
        			}
        			
        			if(client!=null){
        				client.shutdown();
        			}
                }
            });
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
		
		String[] dt = new String[6];
		dt[0] = "0";
		
		Engine.getInstance().deleteObserver(this);
		
		synchronized(SyncUtil.FINISH_OBJECT){
			message = new EngineMessage();
			message.setTid(this.id);
			message.setType(EngineMessageType.IM_FINISH);
			message.setData(dt);
			Engine.getInstance().fire(message);
		}
	}
}
