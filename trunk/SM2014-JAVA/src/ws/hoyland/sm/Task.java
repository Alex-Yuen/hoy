package ws.hoyland.sm;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import ws.hoyland.util.Configuration;
import ws.hoyland.util.Converts;
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
	private HttpHost proxy = null;
	
	private boolean run = false;
	
	private static Random RND = new Random();
	//private static String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
	private static String UAG = "Opera/9.25 (Windows NT 6.0; U; en)";
	
	private static Configuration CONFIGURATION = Configuration
			.getInstance("config.ini");

	
	public Task(String line) {
		this.line = line;
		
		String[] ls = line.split("----");
		this.id = Integer.parseInt(ls[1]);
		this.account = ls[2];
		this.password = ls[3];
		
		this.run = true;
	}

	@Override
	public void update(Observable obj, Object arg) {
		final EngineMessage msg = (EngineMessage) arg;

		if (msg.getTid() == this.id || msg.getTid()==-1) { //-1, all tasks message
			int type = msg.getType();
			switch(type){
				case EngineMessageType.OM_REQUIRE_PROXY:
					if(msg.getData()!=null){
						String[] ms = (String[]) msg.getData();
						//System.err.println(ms[0] + "/" + ms[1] + "/" + ms[2]);
						this.proxy = new HttpHost(ms[0], Integer.parseInt(ms[1]));;
					}else {
						this.run = false;
					}
					break;
				default:
					break;
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		synchronized(SyncUtil.START_OBJECT){	
			//通知有新线程开始执行
			message = new EngineMessage();
			message.setTid(this.id);
			message.setType(EngineMessageType.IM_START);
			Engine.getInstance().fire(message);
		}
		
		try{
			client = new DefaultHttpAsyncClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 1000*Integer.parseInt(CONFIGURATION.getProperty("TIMEOUT")));
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000*Integer.parseInt(CONFIGURATION.getProperty("TIMEOUT")));
			
			message = new EngineMessage();
			message.setTid(this.id);
			message.setType(EngineMessageType.IM_REQUIRE_PROXY);
			Engine.getInstance().fire(message);
			
			if(run==false&&proxy==null){
				throw new Exception("No Proxy!");
			}
			
			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);

			String url = "http://pt.3g.qq.com/login?act=json&format=2&bid_code=house_touch&r=" + String.valueOf(RND.nextDouble()) + "&qq=" + account + "&pmd5=" + Converts.bytesToHexString(Converts.MD5Encode(password)) + "&go_url=http%3A%2F%2Fhouse60.3g.qq.com%2Ftouch%2Findex.jsp%3Fsid%3DAd_JZ1k2ZviFLkV2nvFt7005%26g_ut%3D3%26g_f%3D15124";
			//url = url.replaceAll("#", String.valueOf(RND.nextDouble()));			
			request = new HttpGet(url);
			request.setHeader("User-Agent", UAG);
			
			client.execute(request, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    //System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
                	HttpEntity entity = null;
                	try{
	                	entity = response.getEntity();
	                	String line = EntityUtils.toString(entity);
	                	
	                    if (line.indexOf("pt.handleLoginResult") == -1)//代理异常
	                    {
	                        Engine.getInstance().removeProxy(proxy.getHostName()+":"+proxy.getPort());
	                    }
	                    else
	                    {
	                        //bool ok = false;
	                        if (line.indexOf("," + account + ",0,") != -1)
	                        {
	                        	Engine.getInstance().log(0, account + "----" + password);//account + " / " + proxy
	                            //task.Abort();
	                        }
	                        else if (line.indexOf(",0,40010,") != -1)
	                        {
	                        	Engine.getInstance().log(1, account + "----" + password);
	                            //task.Abort();
	                        }
	                        else if (line.indexOf(",0,40026,") != -1)
	                        {
	                        	Engine.getInstance().log(2, account + "----" + password);
	                            //task.Abort();
	                        }
	                        else if (line.indexOf("," + account + ",0,") != -1)//验证码
	                        {
	                        	Engine.getInstance().addTask(line);

	                            //不离开当前任务
	                            //Thread.Sleep(1000 * Int32.Parse(cfa.AppSettings.Settings["P_ITV"].Value));//N秒后继续
	                        }
	                        else //代理异常
	                        {
	                            Engine.getInstance().removeProxy(proxy.getHostName()+":"+proxy.getPort());
	                        }
	                    }
                	}catch(Exception ex){
                		ex.printStackTrace();
                		Engine.getInstance().removeProxy(proxy.getHostName()+":"+proxy.getPort());
                	}finally{
                		try{
	                		if (entity != null) {
		    					EntityUtils.consume(entity);
		    				}
                		}catch(Exception e){
                			e.printStackTrace();
                		}
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
                	try{
	                	if (request != null) {
	        				request.releaseConnection();
	        			}
                	}catch(Exception e){
            			e.printStackTrace();
            		}
                	try{
	        			if(client!=null){
	        				client.shutdown();
	        			}
                	}catch(Exception e){
            			e.printStackTrace();
            		}
                }
            });
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
		
		String[] dt = new String[2];
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
