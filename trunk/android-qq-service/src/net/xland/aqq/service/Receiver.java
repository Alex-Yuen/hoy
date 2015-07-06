package net.xland.aqq.service;

import java.util.Map;

import net.xland.aqq.service.task.BindTask;
import net.xland.util.Converts;
import net.xland.util.Cryptor;
import net.xland.util.XLandUtil;

public class Receiver implements Runnable {

	private QQServer server;
	private byte[] content;
//	private byte[] header;
	private byte[] body;
	private byte[] xseq;
	private String sseq;
	private String sid;
	
	private Map<String, Object> session;
	
	private static Cryptor cryptor = new Cryptor();
	private static byte[] outterkey = new byte[16];
	
	public Receiver(QQServer server, byte[] content){
		this.server = server;
		this.content = content;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//mobileTask之后，自动添加BindTask
		//NickTask之后，自动disconnect DC，removeDC
		//getfuture
		System.out.println(content.length);
		//mobile:247 00 05 该手机号码已经与其他QQ号码绑定，并且尚未设置密码，无法使用该手机号码继续注册。
		//mobile:191 00 01 对不起，您输入的手机号已经绑定过号码。
		//mobile:143? wrong ECDH
		
//		this.header = XLandUtil.slice(content, 2, 2);
//		String strHeader = Converts.bytesToHexString(this.header); 
		this.body = cryptor.decrypt(XLandUtil.slice(content, 15, content.length-15), outterkey);
		xseq = XLandUtil.slice(body, 6, 2);
		sseq = Converts.bytesToHexString(xseq);
		
		for(Map<String, Object>ts:server.sessions()){
			System.out.println(ts);
			if(sseq.equals(ts.get("x-seq"))){
				this.session = ts;
				sid = (String)ts.get("x-sid");
				break;
			}
		}
		System.out.println("from receiver..............");
		System.out.println(sseq);
		System.out.println(sid);
		System.out.println(Converts.bytesToHexString(body));
		
		boolean nf = false;//notify flag
		
		if("mobile".equals(session.get("x-cmd"))){			
			if(content.length==191){//不通知恢复，而是再次执行BindTask
				server.addTask(new BindTask(this.sid, (String)session.get("x-mobile")));
			}else{
				server.releaseSession(sid);
				nf = true;
			}
		}else{
			if("nick".equals(session.get("x-cmd"))){
				server.releaseSession(sid);
			}
			nf = true;
		}
		
		if(nf){
			synchronized(this.session){
				try{
					session.notify();    //恢复等待
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
