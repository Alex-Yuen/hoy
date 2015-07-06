package net.xland.aqq.service;

import java.util.Map;

import net.xland.util.Converts;
import net.xland.util.Cryptor;
import net.xland.util.XLandUtil;

public class Receiver implements Runnable {

	private QQServer server;
	private byte[] content;
	private byte[] header;
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
		this.header = XLandUtil.slice(content, 2, 2);
//		String strHeader = Converts.bytesToHexString(this.header); 
		this.body = cryptor.decrypt(XLandUtil.slice(content, 15, content.length-15), outterkey);
		xseq = XLandUtil.slice(body, 6, 2);
		sseq = Converts.bytesToHexString(xseq);
		
		for(Map<String, Object>ts:server.sessions()){
			if(sseq.equals(ts.get("x-seq"))){
				this.session = ts;
				sid = (String)ts.get("sid");
				break;
			}
		}
		System.out.println("from receiver..............");
		System.out.println(sseq);
		System.out.println(sid);
		System.out.println(Converts.bytesToHexString(body));
		
		synchronized(this.session){
			try{
				session.notify();    //恢复等待
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
