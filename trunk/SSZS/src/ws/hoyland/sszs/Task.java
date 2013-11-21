package ws.hoyland.sszs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Task implements Runnable {

	private String line;
	private boolean run = false;
	private boolean fb = false; //break flag;
	private boolean fc = false; //continue flag;
	private int idx = 0; //method index;
	
	private boolean block = false;
	private TaskObject obj = null;
	
	private DefaultHttpClient client;
	private HttpPost post = null;
	private HttpGet get = null;
	
	private HttpResponse response = null;
	private HttpEntity entity = null;
	private JSONObject json = null;
//	private HttpUriRequest request = null;
	private List<NameValuePair> nvps = null;
	
	private String sig = null;
//	private byte[] ib = null;
//	private byte[] image = null;
	
	private EngineMessage message = null;
	private int id = 0;
	private String account = null;
	private String password = null;
	
	private ByteArrayOutputStream baos = null;
	private int codeID = -1;
	private String result;
	
	private final String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
	
	public Task(String line) {
		// TODO Auto-generated constructor stub
		String[] ls = line.split("----");
		this.id = Integer.parseInt(ls[0]);
		this.account = ls[1];
		this.password = ls[2];
		
		this.run = true;
		
		client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
	}

	@Override
	public void run() {
		//System.out.println(line);
		while(run){
			if(fb){
				break;
			}
			if(fc){
				continue;
			}
			
			if(block){			
				synchronized (obj.getBlock()) {
					try {
						obj.getBlock().wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				block = false;
			}
			
			process(idx);

			try {
				if(entity!=null){
					EntityUtils.consume(entity); 
				}
				if(get!=null){
					get.releaseConnection();
				}
				if(post!=null){
					post.releaseConnection();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void process(int index){
		switch(index){
			case 0:				
				try{
					get = new HttpGet(
							"http://captcha.qq.com/getsig?aid=523005413&uin=0&"
									+ Math.random());
					
					get.setHeader("User-Agent", UAG);
					get.setHeader("Referer", "http://aq.qq.com/cn2/appeal/appeal_index");
					get.setHeader("Content-Type", "text/html");
					get.setHeader("Accept", "text/html, */*");
					
					response = client.execute(get);
					entity = response.getEntity();
					
					line = EntityUtils.toString(entity);
					sig = line.substring(20, line.indexOf(";    "));
					//System.out.println(sig);

					idx++;
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			case 1:				
				try{
					get = new HttpGet(
							"http://captcha.qq.com/getimgbysig?sig="
									+ URLEncoder.encode(this.sig, "UTF-8"));
					
					get.setHeader("User-Agent", UAG);
					get.setHeader("Content-Type", "text/html");
					get.setHeader("Accept", "text/html, */*");
					
					response = client.execute(get);
					entity = response.getEntity();
					
					DataInputStream in = new DataInputStream(entity.getContent());
					baos = new ByteArrayOutputStream();
					byte[] barray = new byte[1024];
					int size = -1;
					while((size=in.read(barray))!=-1){
						baos.write(barray, 0, size);
					}
					ByteArrayInputStream bais = new ByteArrayInputStream (baos.toByteArray());
					
					message = new EngineMessage();
					message.setType(EngineMessageType.IM_IMAGE_DATA);
					message.setData(bais);
					
					Engine.getInstance().fire(message);

					idx++;
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			case 2:
				//根据情况，阻塞或者提交验证码到UU
				try{
					byte[] by = baos.toByteArray();
					byte[] resultByte=new byte[30];		//为识别结果申请内存空间
					codeID = DM.INSTANCE.uu_recognizeByCodeTypeAndBytesA(by, by.length, 1, resultByte);	//调用识别函数,resultBtye为识别结果
					result = new String(resultByte, "UTF-8").trim();
										
					idx++;
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			case 3:
				try{
					get = new HttpGet(
							"http://aq.qq.com/cn2/appeal/appeal_index");
					
					get.setHeader("User-Agent", UAG);
					get.setHeader("Content-Type", "text/html");
					get.setHeader("Accept", "text/html, */*");
					
					response = client.execute(get);
					entity = response.getEntity();
					
					//line = EntityUtils.toString(entity);
					//System.out.println(line);
					
					idx++;
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			case 4:
				try{
					get = new HttpGet(
							"http://aq.qq.com/cn2/appeal/appeal_check_assist_account?UserAccount="+this.account);
					
					get.setHeader("User-Agent", UAG);
					get.setHeader("Content-Type", "text/html");
					get.setHeader("Accept", "text/html, */*");
					get.setHeader("Referer", "http://aq.qq.com/cn2/appeal/appeal_index");
					
					response = client.execute(get);
					entity = response.getEntity();
					
					//line = EntityUtils.toString(entity);
					//System.out.println(line);
					
					idx++;
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			case 5:
				try{
					get = new HttpGet(
							"http://aq.qq.com/cn2/ajax/check_verifycode?session_type=on_rand&verify_code="+result+"&appid=523005413&CaptchaSig="+URLEncoder.encode(this.sig, "UTF-8"));
					
					get.setHeader("User-Agent", UAG);
					get.setHeader("Content-Type", "text/html");
					get.setHeader("Accept", "text/html, */*");
					get.setHeader("Referer", "http://aq.qq.com/cn2/appeal/appeal_index");
					
					response = client.execute(get);
					entity = response.getEntity();
					
					line = EntityUtils.toString(entity);
					json = new JSONObject(line);
					
					//System.out.println(line);
					if("0".equals(json.getString("Err"))){
						idx += 2;
					} else {
						//报错
						idx++;
					}
					//System.out.println(line);
					
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			case 6:
				try{
					int reportErrorResult = DM.INSTANCE.uu_reportError(codeID);
					//TODO, send to UI
					idx = 0; //重新开始
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			case 7:
				try{
					post = new HttpPost(
							"http://aq.qq.com/cn2/appeal/appeal_contact");
					
					post.setHeader("User-Agent", UAG);
					post.setHeader("Content-Type", "application/x-www-form-urlencoded");
					post.setHeader("Accept", "text/html, */*");
					post.setHeader("Referer", "http://aq.qq.com/cn2/appeal/appeal_index");
										
					nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("qqnum", this.account));
					nvps.add(new BasicNameValuePair("verifycode2", result));
					nvps.add(new BasicNameValuePair("CaptchaSig", this.sig));
					
					post.setEntity(new UrlEncodedFormEntity(nvps));
					
					response = client.execute(post);
					entity = response.getEntity();
					
					//line = EntityUtils.toString(entity);

					//System.out.println(line);
					
					//发送消息，提示Engine，需要邮箱
					obj = new TaskObject();
					EngineMessage message = new EngineMessage();
					message.setType(EngineMessageType.IM_REQUIRE_MAIL);
					message.setData(obj);
					sendMessage(message);
					
					block = true;
					
					idx++;
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			case 8:
				try{
					System.out.println(obj.getData()+">>>>");
					post = new HttpPost(
							"http://aq.qq.com/cn2/appeal/appeal_contact_confirm");
					
					post.setHeader("User-Agent", UAG);
					post.setHeader("Content-Type", "application/x-www-form-urlencoded");
					post.setHeader("Accept", "text/html, */*");
					post.setHeader("Referer", "http://aq.qq.com/cn2/appeal/appeal_contact");
										
					nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("txtLoginUin", this.account));
					nvps.add(new BasicNameValuePair("txtCtCheckBox", "0"));
					nvps.add(new BasicNameValuePair("txtName", Names.getInstance().getName()));
					nvps.add(new BasicNameValuePair("txtAddress", ""));
					nvps.add(new BasicNameValuePair("txtIDCard", ""));
					nvps.add(new BasicNameValuePair("txtContactQQ", ""));
					nvps.add(new BasicNameValuePair("txtContactQQPW", ""));
					nvps.add(new BasicNameValuePair("txtContactQQPW2", ""));
					nvps.add(new BasicNameValuePair("radiobutton", "mail"));
					nvps.add(new BasicNameValuePair("txtContactEmail", "hoyzhang@163.com"));
					nvps.add(new BasicNameValuePair("txtContactMobile", "请填写您的常用手机"));
					
					post.setEntity(new UrlEncodedFormEntity(nvps));
					
					response = client.execute(post);
					entity = response.getEntity();
					
					//line = EntityUtils.toString(entity);

					//System.out.println(line);
					
					idx++;					
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			case 9: //收邮件
				try{
					
				}catch(Exception e){
					e.printStackTrace();
					fb = true;
				}
				break;
			default:
				break;
		}		
	}
	
	private void sendMessage(final EngineMessage message){
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				Engine.getInstance().fire(message);
			}			
		});
		t.start();
	}
}
