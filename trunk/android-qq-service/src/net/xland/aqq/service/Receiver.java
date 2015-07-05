package net.xland.aqq.service;

import net.xland.util.Converts;
import net.xland.util.Cryptor;
import net.xland.util.XLandUtil;

public class Receiver implements Runnable {

	private QQServer server;
	private byte[] content;
	private byte[] header;
	private byte[] body;
	
	private static Cryptor cryptor = new Cryptor();
	private static byte[] outterKey = new byte[]{
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
	};
	
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
		this.body = cryptor.decrypt(XLandUtil.slice(content, 15, content.length-15), outterKey);
		
		
	}

}
