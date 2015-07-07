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
				session.put("x-status", "0");
				session.put("x-result", "finish-mobile-task");
				server.addTask(new BindTask(this.sid));
			}else if(content.length==247){//对不起，您输入的手机号已经绑定过号码。
				session.put("x-status", "-3");
				session.put("x-result", "mobile-have-already-bind");
				server.releaseSession(sid);
				nf = true;
			}else{
				session.put("x-status", "-2");
				session.put("x-result", "can't-process-mobile-task");
				server.releaseSession(sid);
				nf = true;
			}
		}else{
			if("bind".equals(session.get("x-cmd"))){
				if(content.length==191){//正常的情况，获取10位的标志
					byte[] ibody = cryptor.decrypt(XLandUtil.slice(body, 69, body.length-69-1), (byte[])session.get("x-sk"));
					byte[] bf = XLandUtil.slice(ibody, 38, 10);//bind-flag			
					System.out.println("bind flag:"+Converts.bytesToHexString(bf));		
					session.put("x-bf", bf);
					session.put("x-status", "0");
					session.put("x-result", "finish-bind-task");
				}else if(content.length==207){//需要发短信
					session.put("x-status", "-3");
					session.put("x-result", "need-send-sms");
					server.releaseSession(sid);
				}else {//其他情况
					session.put("x-status", "-2");
					session.put("x-result", "can't-process-bind-task");
					server.releaseSession(sid);
				}
			}else if("code".equals(session.get("x-cmd"))){
				if(content.length==183){//验证码正确
					session.put("x-status", "0");
					session.put("x-result", "finish-code-task");
				}else{
					session.put("x-status", "-2");
					session.put("x-result", "can't-process-bind-task");
					server.releaseSession(sid);
				}
			}else if("nick".equals(session.get("x-cmd"))){
				if(content.length==335){
					byte[] ibody = cryptor.decrypt(XLandUtil.slice(body, 69, body.length-69-1), (byte[])session.get("x-sk"));
					byte qbodylength = ibody[0x12];
					byte[] qbody = cryptor.decrypt(XLandUtil.slice(ibody, 0x13, qbodylength), (byte[])session.get("x-ck"));//QQ body
					
					byte[] xqq = XLandUtil.slice(qbody, 32, 4);
					int qqnumber = Integer.parseInt(Converts.bytesToHexString(xqq), 16); 
					session.put("x-status", "0");
					session.put("x-result", "finish-nick-task");
					session.put("x-qqnumber", String.valueOf(qqnumber));
				}else{
					session.put("x-status", "-2");
					session.put("x-result", "can't-process-nick-task");
				}
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
