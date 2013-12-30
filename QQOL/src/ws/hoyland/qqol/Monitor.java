package ws.hoyland.qqol;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;
import ws.hoyland.util.DM;
import ws.hoyland.util.YDM;

public class Monitor implements Runnable {
	private boolean run = false;
	private ByteBuffer bf = null;
	private boolean wakeup = false;
	private byte[] buffer = null;
	private byte[] header = null;
	private byte[] content = null;
	private byte[] decrypt  = null;
	private Crypter crypter = null;
	private String account = null;
	private Task task = null;
	
	private static Monitor instance; 
	
	private Monitor() {
		//this.bf = ByteBuffer.allocate(1024);
		this.run = true;
		this.crypter = new Crypter();
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
								}else {
									//发起0828
									
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
//										System.out.println("KK1:");
//										System.out.println(Converts.bytesToHexString(decrypt));
//										System.out.println(Converts.bytesToHexString(vctoken));
										
										//TODO
										//执行 0828
										
										//idx++; //not need 要验证后才确定是否 idx++
									}else{
//										//执行 REPORT——ERROR
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
