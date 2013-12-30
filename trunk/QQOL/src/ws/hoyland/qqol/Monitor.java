package ws.hoyland.qqol;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;

import ws.hoyland.util.Configuration;
import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;

public class Monitor implements Runnable {
	private boolean run = false;
	private ByteBuffer bf = null;
	private boolean wakeup = false;
	private byte[] buffer = null;
	
	private static Monitor instance; 
	
	private Monitor() {
		//this.bf = ByteBuffer.allocate(1024);
		this.run = true;
	}

	public void stop() {
		this.run = false;
	}

	public void setWakeup(boolean wakeup) {
		this.wakeup = wakeup;
	}

	public static Monitor getInstance( ){
		if(instance==null){
			instance = new Monitor();
		}
		return instance;
	}
	
	@Override
	public void run() {//对于各种0825之内的做处理
		while (run) {
			try {
				int ec = 0;
				if(!wakeup){
					ec = QQSelector.selector.select();
				}else {
					ec = QQSelector.selector.selectNow();
				}
				
				if (ec > 0) {
					Set<?> selectedKeys = QQSelector.selector.selectedKeys();
					Iterator<?> iterator = selectedKeys.iterator();
					while (iterator.hasNext()) {
						SelectionKey sk = (SelectionKey) iterator.next();
						iterator.remove();
						if (sk.isReadable()) {
							DatagramChannel datagramChannel = (DatagramChannel) sk
									.channel();
							bf = ByteBuffer.allocate(1024);
							datagramChannel.read(bf);
							//bf.flip();

							buffer = Util.pack(bf
									.array());
							System.out.println("RECV:");
							System.out.println(Converts.bytesToHexString(buffer));
							//bf.clear();
							Receiver receiver = new Receiver(buffer);
							Engine.getInstance().addTask(receiver);							
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class Receiver implements Runnable{
	private byte[] buffer;
	private byte[] header = null;
	private byte[] content = null;
	private byte[] decrypt  = null;
	private Crypter crypter = null;
	private String account = null;
	private Task task = null;
	private int status = 1;
	
	public Receiver(byte[] buffer){		
		this.buffer = buffer;
		this.crypter = new Crypter();
		this.status = Integer.parseInt(Configuration.getInstance().getProperty("LOGIN_TYPE"));
	}
	
	@Override
	public void run() {
		try{
			header = Util.slice(buffer, 3, 2);
			account = String.valueOf(Long.parseLong(Converts.bytesToHexString(Util.slice(buffer, 7, 4)), 16));
			
			//根据返回，处理各种消息
			if(header[0]==(byte)0x08&&header[1]==(byte)0x25){
				if(buffer.length==135){
					//重定向
					content = Util.slice(buffer, 14, 120);
					//System.out.println(Converts.bytesToHexString(Engine.getInstance().getAcccounts().get(account).get("key0825")));
					decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("key0825"));
					Engine.getInstance().getAcccounts().get(account).put("ips", Util.slice(decrypt, 95, 4));
					Engine.getInstance().getChannels().remove(account);
					task = new Task(Task.TYPE_0825, account);
				}else{
					//发起0836
					content = Util.slice(buffer, 14, 104);
					decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("key0825"));
					Engine.getInstance().getAcccounts().get(account).put("token", Util.slice(decrypt, 5, 0x38));
					Engine.getInstance().getAcccounts().get(account).put("logintime", Util.slice(decrypt, 67, 4));
					Engine.getInstance().getAcccounts().get(account).put("loginip", Util.slice(decrypt, 71, 4));
					task = new Task(Task.TYPE_0836, account);
				}
	
				Engine.getInstance().addTask(task);
			}else if(header[0]==(byte)0x08&&header[1]==(byte)0x36){
				if(buffer.length==871){//需要验证码
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("key0836x"));
					
					//截取png数据，以及相关key, data
					byte[] ilbs = Util.slice(decrypt, 22+0x38, 2);
					int imglen = ilbs[0]*0x100 + (ilbs[1] & 0xFF);
					Engine.getInstance().getAcccounts().get(account).put("pngfirst", Util.slice(decrypt, 24+0x38, imglen));
					
					boolean dlvc = (Util.slice(decrypt, 25+0x38+imglen, 1)[0]==1);
					
					if(dlvc){//发起00BA
						Engine.getInstance().getAcccounts().get(account).put("dlvc", "T".getBytes());
						Engine.getInstance().getAcccounts().get(account).put("tokenfor00ba", Util.slice(decrypt, 28+0x38+imglen, 0x28));
						Engine.getInstance().getAcccounts().get(account).put("keyfor00ba", Util.slice(decrypt, 32+0x38+0x28+imglen, 0x10));
						
						task = new Task(Task.TYPE_00BA, account);																			
						Engine.getInstance().addTask(task);
					}
				}else if(buffer.length==175||buffer.length==95||buffer.length==247||buffer.length==239){
					byte[] ts = Util.slice(buffer, 14, buffer.length-15);
					ts = crypter.decrypt(ts, Engine.getInstance().getAcccounts().get(account).get("key0836x"));
					//System.out.println(Converts.bytesToHexString(ts));
					//System.out.println(new String(Util.slice(ts, 15, ts.length-15), "utf-8"));
					//info(new String(Util.slice(ts, 15, ts.length-15), "utf-8"));
				}else if(buffer.length==255){
					//info("需要验证密保");
					//run = false;
					//return;
				}else {
					//发起0828
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("key0836"));
					
					//System.out.println(Converts.bytesToHexString(decrypt));
					//需解释出某些值供 0828使用
					Engine.getInstance().getAcccounts().get(account).put("key0828", Util.slice(decrypt, 7, 0x10));
					Engine.getInstance().getAcccounts().get(account).put("tokenfor0828", Util.slice(decrypt, 9+0x10, 0x38));
					//byte[] tokenfor0828 = Util.slice(decrypt, 9+0x10, 0x38);
					String rbof0836 = Converts.bytesToHexString(decrypt);
					
					//查找昵称
					byte[] rdecrypt = Util.reverse(decrypt);
					String rbofrdec = Converts.bytesToHexString(rdecrypt);
					int nickidx = -1;
					do{
						//nickidx = rbofrdec.indexOf("0100")/2;
						rbofrdec = rbofrdec.substring(rbofrdec.indexOf("0100") + 4);//往前查找
					}while(!"0801".equals(rbofrdec.substring(4, 8)));
					//退出循环，当为找到
					nickidx = rbofrdec.length()/2 + 8;
					int nicklen = decrypt[nickidx];
					byte[] nick = new byte[nicklen];
					for(int i=0;i<nicklen;i++){
						nick[i] = decrypt[nickidx+1+i];
					}
					System.out.println("Nick:"+new String(nick, "utf-8"));
					//setNick(new String(nick, "utf-8"));
					
					Engine.getInstance().getAcccounts().get(account).put("key0828recv", Util.slice(decrypt, rbof0836.indexOf("0000003C0002")/2+6, 0x10));
					Engine.getInstance().getAcccounts().get(account).put("logintime", Util.slice(decrypt, rbof0836.indexOf("00880004")/2+4, 4));
					Engine.getInstance().getAcccounts().get(account).put("loginip", Util.slice(decrypt, rbof0836.indexOf("00880004")/2+8, 4));
					Engine.getInstance().getAcccounts().get(account).put("tokenat0078", Util.slice(decrypt, rbof0836.indexOf("000000000078")/2+6, 0x78));
					
					task = new Task(Task.TYPE_0828, account);																			
					Engine.getInstance().addTask(task); 
				}
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0xBA){
				if(Engine.getInstance().getAcccounts().get(account).get("dlvc")!=null){ //继续下载验证码
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("key0836x"));//??????!!!!!! 第二次才是key00ba
					//System.out.println(Converts.bytesToHexString(decrypt));
					byte[] ilbs = Util.slice(decrypt, 10+0x38, 2);
					//System.out.println(Converts.bytesToHexString(imglenbs));
					int imglen = ilbs[0]*0x100 + (ilbs[1] & 0xFF);
					Engine.getInstance().getAcccounts().get(account).put("pngsecond", Util.slice(decrypt, 12+0x38, imglen));
					Engine.getInstance().getAcccounts().get(account).put("tokenfor00ba", Util.slice(decrypt, 10, 0x38));
					
					Engine.getInstance().getAcccounts().get(account).remove("dlvc");
					task = new Task(Task.TYPE_00BA, account);																			
					Engine.getInstance().addTask(task); 
				}else{ //识别结果
					if(buffer.length==95){
						content = Util.slice(buffer, 14, buffer.length-15);
						decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("key00BA"));
						//获取vctoken
						Engine.getInstance().getAcccounts().get(account).put("vctoken", Util.slice(decrypt, 10, 0x38)); 
	//					System.out.println("KK1:");
	//					System.out.println(Converts.bytesToHexString(decrypt));
	//					System.out.println(Converts.bytesToHexString(vctoken));
																
						//再次执行 0836
						task = new Task(Task.TYPE_0836, account);																			
						Engine.getInstance().addTask(task); 										
					}else{
	//					//报告验证码错误
						task = new Task(Task.TYPE_ERR_VC, account);																			
						Engine.getInstance().addTask(task); 
					}
				}
			}else if(header[0]==(byte)0x08&&header[1]==(byte)0x28){
				if(buffer.length==127){
					//System.out.println("您的网络环境可能发生了变化，为了您的帐号安全，请重新登录。");
					//System.out.println("退出任务");
					
					byte[] ts = Util.slice(buffer, 14, buffer.length-15);
					ts = crypter.decrypt(ts, Engine.getInstance().getAcccounts().get(account).get("key0828"));
					System.out.println(Converts.bytesToHexString(ts));
					System.out.println(new String(Util.slice(ts, 15, ts.length-15), "utf-8"));
					//info(new String(Util.slice(ts, 15, ts.length-15), "utf-8"));
				}else{
					//System.out.println("OK");
					//info("获取成功");
					content = Util.slice(buffer, 14, buffer.length-15);
					//System.out.println(Converts.bytesToHexString(key0828recv));
					decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("key0828recv"));
					System.out.println(decrypt.length);
					System.out.println(Converts.bytesToHexString(decrypt));
					
					Engine.getInstance().getAcccounts().get(account).put("sessionkey", Util.slice(decrypt, 63, 0x10));	
					//idx++;		
					//执行00EC
					task = new Task(Task.TYPE_00EC, account); //上线包															
					Engine.getInstance().addTask(task); 
				}
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0xEC){
				task = new Task(Task.TYPE_005C, account); //更新资料													
				Engine.getInstance().addTask(task); 
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0x5C){
				content = Util.slice(buffer, 14, buffer.length-15);
				decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("sessionkey"));
				
				if(decrypt[0]!=(byte)0x88){
					task = new Task(Task.TYPE_005C, account); //继续更新资料													
					Engine.getInstance().addTask(task); 
				}else{
					int level = Util.slice(decrypt, 10, 1)[0];
					int days = Util.slice(decrypt, 16, 1)[0];
					//setProfile(level, days);
					
					//告诉Engine，启动新的线程，执行Queue的下一个
					if(Engine.getInstance().getQueue().size()>0){
						Task task = new Task(Task.TYPE_0825, Engine.getInstance().getQueue().remove());
						Engine.getInstance().addTask(task);
					}
				}
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0xCE){ //反馈
				//byte[] header = Util.slice(buffer, 3, 2);
				content = Util.slice(buffer, 14, buffer.length-15);
				decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("sessionkey"));

				Engine.getInstance().getAcccounts().get(account).put("rh", Util.slice(buffer, 0, 11));
				Engine.getInstance().getAcccounts().get(account).put("rc", Util.slice(decrypt, 0, 0x010));
				
				task = new Task(Task.TYPE_00CE, account); 									
				Engine.getInstance().addTask(task); 
				
				//判断时间
				boolean nmsg = false;
				String rbof00CE = Converts.bytesToHexString(decrypt);
				byte[] cetime = Util.slice(decrypt, rbof00CE.indexOf("4D53470000000000")/2+8, 0x04);
				long lcetime = Long.parseLong(Converts.bytesToHexString(cetime), 16)*1000;
				//System.out.println("P5:"+(System.currentTimeMillis()-lcetime));
				if(System.currentTimeMillis()-lcetime<=1000*60){//一分钟内							
					nmsg = true;
				}
				
				if((status==1||status==2)&&nmsg&&("true".equals(Configuration.getInstance().getProperty("AUTO_REPLY")))){//需要自动回复
					Engine.getInstance().getAcccounts().get(account).put("00CDA", Util.slice(decrypt, 4, 0x04));
					Engine.getInstance().getAcccounts().get(account).put("00CDB", Util.slice(decrypt, 0, 0x04));
					
					task = new Task(Task.TYPE_00CD, account); 									
					Engine.getInstance().addTask(task); 
				}
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0x17){
				if(buffer.length==231&&Engine.getInstance().getAcccounts().get(account).get("0017")==null){//TODO 需要处理多条消息
					Engine.getInstance().getAcccounts().get(account).put("0017", "T".getBytes());
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, client.getSessionKey());
					
					if(decrypt!=null&&decrypt[0]==0x00&&decrypt[1]==0x00){ //00 00 27 10
						//被挤掉下线
						//info("被挤线，等待重新登录");
						task = new Task(Task.TYPE_0017, account); 									
						Engine.getInstance().addTask(task);
					}
					
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}
