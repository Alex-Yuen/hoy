package ws.hoyland.qqol;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ws.hoyland.util.Configuration;
import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;
import ws.hoyland.util.EngineMessage;

public class Monitor implements Runnable {
	private boolean run = false;
	private ByteBuffer bf = ByteBuffer.allocate(1024);
	private boolean wakeup = false;
	private byte[] buffer = null;
	private int size = -1;
	private String hd = null;
	private String FHD = "#0825#0836#0828#00BA#00EC#005C#00CE#0017#0062#0058#";
	
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
							//bf = ByteBuffer.allocate(1024);
							size = datagramChannel.read(bf);
							bf.flip();

							buffer = Util.slice(bf
									.array(), 0, size);
							//System.out.println("RECV:"+buffer.length);
							//System.out.println(Converts.bytesToHexString(buffer));
							
							bf.clear();
							
							hd = Converts.bytesToHexString(Util.slice(buffer, 3, 2));
							if(buffer.length>0&&FHD.contains("#"+hd+"#")){
								Receiver receiver = new Receiver(buffer);
								Engine.getInstance().addTask(receiver);
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

class Receiver implements Runnable{
	private byte[] buffer;
	private byte[] header = null;
	private byte[] content = null;
	private byte[] decrypt  = null;
	private Crypter crypter = null;
	private String account = null;
	private Task task = null;
	private int status = 1;
	private static String[] STS = new String[]{"在线", "离开", "忙碌", "隐身"}; 
	private int id = 0;
	private Map<String, byte[]> details = null;
	private EngineMessage message = null;
	
//	private static DateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public Receiver(byte[] buffer){
		this.buffer = buffer;
		this.crypter = new Crypter();
		this.status = Integer.parseInt(Configuration.getInstance().getProperty("LOGIN_TYPE"));
		//Engine.getInstance().getAcccounts().get(account)
	}
	
	@Override
	public void run() {
		try{
			this.header = Util.slice(buffer, 3, 2);
			this.account = String.valueOf(Long.parseLong(Converts.bytesToHexString(Util.slice(buffer, 7, 4)), 16));
			this.details = Engine.getInstance().getAcccounts().get(account);
			this.id = Integer.parseInt(new String(details.get("id")));

			System.err.println("\t\t<-["+account+"]"+Converts.bytesToHexString(header));
			//最后活动时间
			infoact();
			
			//根据返回，处理各种消息
			if(header[0]==(byte)0x08&&header[1]==(byte)0x25){
				//info("正在登录");
				if(buffer.length==135){
					//重定向
					content = Util.slice(buffer, 14, 120);
					//System.out.println(Converts.bytesToHexString(details.get("key0825")));
					decrypt = crypter.decrypt(content, details.get("key0825"));
					details.put("ips", Util.slice(decrypt, 95, 4));
					Engine.getInstance().getChannels().remove(account);
					info("重定向");
					task = new Task(Task.TYPE_0825, account);
				}else{
					//发起0836
					content = Util.slice(buffer, 14, 104);
					decrypt = crypter.decrypt(content, details.get("key0825"));
					details.put("token", Util.slice(decrypt, 5, 0x38));
					details.put("logintime", Util.slice(decrypt, 67, 4));
					details.put("loginip", Util.slice(decrypt, 71, 4));
					info("验证身份");
					task = new Task(Task.TYPE_0836, account);
				}
	
				Engine.getInstance().addTask(task);
			}else if(header[0]==(byte)0x08&&header[1]==(byte)0x36){
				if(buffer.length==871){//需要验证码
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, details.get("key0836x"));
					
					//截取png数据，以及相关key, data
					byte[] ilbs = Util.slice(decrypt, 22+0x38, 2);
					int imglen = ilbs[0]*0x100 + (ilbs[1] & 0xFF);
					details.put("pngfirst", Util.slice(decrypt, 24+0x38, imglen));
					
					boolean dlvc = (Util.slice(decrypt, 25+0x38+imglen, 1)[0]==1);
					
					if(dlvc){//发起00BA
						details.put("dlvc", "T".getBytes());
						details.put("tokenfor00ba", Util.slice(decrypt, 28+0x38+imglen, 0x28));
						details.put("keyfor00ba", Util.slice(decrypt, 32+0x38+0x28+imglen, 0x10));

						info("下载验证码");
						task = new Task(Task.TYPE_00BA, account);																			
						Engine.getInstance().addTask(task);
					}
				}else if(buffer.length==175||buffer.length==95||buffer.length==247||buffer.length==239){
					byte[] ts = Util.slice(buffer, 14, buffer.length-15);
					ts = crypter.decrypt(ts, details.get("key0836x"));
					//System.out.println(Converts.bytesToHexString(ts));
					//System.out.println(new String(Util.slice(ts, 15, ts.length-15), "utf-8"));
					info(new String(Util.slice(ts, 15, ts.length-15), "utf-8"));
					Engine.getInstance().getChannels().get(account).close(); //关闭
					Engine.getInstance().getChannels().remove(account);
					next();
				}else if(buffer.length==255){
					info("需要验证密保");
					Engine.getInstance().getChannels().get(account).close();
					Engine.getInstance().getChannels().remove(account);
					next();
					//run = false;
					//return;
				}else {
					//发起0828
					info("验证身份成功");
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, details.get("key0836"));
					
					//System.out.println(Converts.bytesToHexString(decrypt));
					//需解释出某些值供 0828使用
					details.put("key0828", Util.slice(decrypt, 7, 0x10));
					details.put("tokenfor0828", Util.slice(decrypt, 9+0x10, 0x38));
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
					//System.out.println("Nick:"+new String(nick, "utf-8"));
					setNick(new String(nick));
					
					details.put("key0828recv", Util.slice(decrypt, rbof0836.indexOf("0000003C0002")/2+6, 0x10));
					details.put("logintime", Util.slice(decrypt, rbof0836.indexOf("00880004")/2+4, 4));
					details.put("loginip", Util.slice(decrypt, rbof0836.indexOf("00880004")/2+8, 4));
					details.put("tokenat0078", Util.slice(decrypt, rbof0836.indexOf("000000000078")/2+6, 0x78));

					info("获取会话密钥");
					task = new Task(Task.TYPE_0828, account);	//获取sessioinkey						
					Engine.getInstance().addTask(task); 
				}
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0xBA){
				if(details.get("dlvc")!=null){ //继续下载验证码
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, details.get("key0836x"));//??????!!!!!! 第二次才是key00ba
					//System.out.println(Converts.bytesToHexString(decrypt));
					byte[] ilbs = Util.slice(decrypt, 10+0x38, 2);
					//System.out.println(Converts.bytesToHexString(imglenbs));
					int imglen = ilbs[0]*0x100 + (ilbs[1] & 0xFF);
					details.put("pngsecond", Util.slice(decrypt, 12+0x38, imglen));
					details.put("tokenfor00ba", Util.slice(decrypt, 10, 0x38));
					
					details.remove("dlvc");
					info("识别并提交验证码");
					task = new Task(Task.TYPE_00BA, account);																			
					Engine.getInstance().addTask(task); 
				}else{ //识别结果
					if(buffer.length==95){
						//info("提交验证码");
						content = Util.slice(buffer, 14, buffer.length-15);
						decrypt = crypter.decrypt(content, details.get("key00BA"));
						//获取vctoken
						details.put("vctoken", Util.slice(decrypt, 10, 0x38)); 
	//					System.out.println("KK1:");
	//					System.out.println(Converts.bytesToHexString(decrypt));
	//					System.out.println(Converts.bytesToHexString(vctoken));
																
						//再次执行 0836
						info("验证身份");
						task = new Task(Task.TYPE_0836, account);																			
						Engine.getInstance().addTask(task); 										
					}else{
	//					//报告验证码错误
						info("报告验证码错误");
						task = new Task(Task.TYPE_ERR_VC, account);																			
						Engine.getInstance().addTask(task); 
						
						//重新执行任务
						//details.clear();
						Engine.getInstance().getChannels().remove(account);
						info("重新登录");
						task = new Task(Task.TYPE_0825, account);
						Engine.getInstance().addTask(task); 
					}
				}
			}else if(header[0]==(byte)0x08&&header[1]==(byte)0x28){ //获取sessioinkey结果
				if(buffer.length==127){
					//System.out.println("您的网络环境可能发生了变化，为了您的帐号安全，请重新登录。");
					//System.out.println("退出任务");
					
					byte[] ts = Util.slice(buffer, 14, buffer.length-15);
					ts = crypter.decrypt(ts, details.get("key0828"));
					//System.out.println(Converts.bytesToHexString(ts));
					//System.out.println(new String(Util.slice(ts, 15, ts.length-15), "utf-8"));
					info(new String(Util.slice(ts, 15, ts.length-15), "utf-8"));
				}else{
					//System.out.println("OK");
					info("获取成功");
					content = Util.slice(buffer, 14, buffer.length-15);
					//System.out.println(Converts.bytesToHexString(key0828recv));
					decrypt = crypter.decrypt(content, details.get("key0828recv"));
					//System.out.println(decrypt.length);
					//System.out.println(Converts.bytesToHexString(decrypt));
					
					//details.clear();//清空
					details.put("sessionkey", Util.slice(decrypt, 63, 0x10));	
					//idx++;		
					//执行00EC
					info("上线");
					task = new Task(Task.TYPE_00EC, account); //上线包															
					Engine.getInstance().addTask(task); 
				}
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0xEC){ //上线包	之后发送更新资料请求
				info("更新资料");
				task = new Task(Task.TYPE_005C, account); 								
				Engine.getInstance().addTask(task); 
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0x5C){ //更新资料
				content = Util.slice(buffer, 14, buffer.length-15);
				decrypt = crypter.decrypt(content, details.get("sessionkey"));
				
				if(decrypt[0]!=(byte)0x88){
					info("继续更新资料");
					task = new Task(Task.TYPE_005C, account); //继续更新资料													
					Engine.getInstance().addTask(task); 
				}else{
					int level = Util.slice(decrypt, 10, 1)[0];
					int days = Util.slice(decrypt, 16, 1)[0];
					setProfile(level, days);
					info(STS[status]+((status==1||status==2)&&"true".equals(Configuration.getInstance().getProperty("AUTO_REPLY"))?"[自动回复]":""));
					details.put("login", "T".getBytes());
					
					next();
				}
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0xCE){ //收到消息
				//byte[] header = Util.slice(buffer, 3, 2);
				content = Util.slice(buffer, 14, buffer.length-15);
				decrypt = crypter.decrypt(content, details.get("sessionkey"));

				details.put("rh00CE", Util.slice(buffer, 0, 11));
				details.put("rc00CE", Util.slice(decrypt, 0, 0x010));
				
				task = new Task(Task.TYPE_00CE, account); 									
				Engine.getInstance().addTask(task); 
				
				//判断时间
				boolean nmsg = false;
				String rbof00CE = Converts.bytesToHexString(decrypt);
				byte[] cetime = Util.slice(decrypt, rbof00CE.indexOf("4D53470000000000")/2+8, 0x04);
				long lcetime = Long.parseLong(Converts.bytesToHexString(cetime), 16)*1000;
				//System.out.println("P5:"+(System.currentTimeMillis()-lcetime));
				if(System.currentTimeMillis()-lcetime<=1000*5){//5秒内							
					nmsg = true;
				}
				
				if((status==1||status==2)&&nmsg&&("true".equals(Configuration.getInstance().getProperty("AUTO_REPLY")))){//需要自动回复
					details.put("00CDA", Util.slice(decrypt, 4, 0x04));
					details.put("00CDB", Util.slice(decrypt, 0, 0x04));
					
					//info("自动回复");
					task = new Task(Task.TYPE_00CD, account); 									
					Engine.getInstance().addTask(task); 
				}
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0x17){
				if(buffer.length==231&&details.get("0017")==null){// 被挤线的处理
					details.put("0017", "T".getBytes());
					content = Util.slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, details.get("sessionkey"));
					
					if(decrypt!=null&&decrypt[0]==0x00&&decrypt[1]==0x00){ //00 00 27 10
						//被挤掉下线
						details.put("rh0017", Util.slice(buffer, 0, 11));
						details.put("rc0017", Util.slice(decrypt, 0, 0x010));

						info("被挤线，等待重新登录");
						tf();
						task = new Task(Task.TYPE_0017, account); 									
						Engine.getInstance().addTask(task);
						
						Engine.getInstance().getChannels().remove(account);
						
						try{
							Thread.sleep(1000*60*Integer.parseInt(Configuration.getInstance().getProperty("EX_ITV")));
						}catch(Exception e){
							e.printStackTrace();
						}
					
						//details.clear();
						details.remove("login");
						info("重新登录");
						task = new Task(Task.TYPE_0825, account);
						Engine.getInstance().addTask(task);
					}					
				}				
			}else if(header[0]==(byte)0x00&&header[1]==(byte)0x58){
				Engine.getInstance().getAcccounts().get(account).put("heart", "T".getBytes());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	private void tf() {//task finish
		message = new EngineMessage();
		message.setTid(this.id);
		message.setType(EngineMessageType.IM_TF);
		
		Engine.getInstance().fire(message);		
	}

	private void next() {
		//告诉Engine，启动新的线程，执行Queue的下一个
		if(Engine.getInstance().getQueue().size()>0){
			Task task = new Task(Task.TYPE_0825, Engine.getInstance().getQueue().remove());
			Engine.getInstance().addTask(task);
		}else{						
			EngineMessage msg = new EngineMessage();
			msg.setType(EngineMessageType.IM_COMPLETE);
			Engine.getInstance().fire(msg);
		}
		
	}

	private void info(String info){
		message = new EngineMessage();
		message.setTid(this.id);
		message.setType(EngineMessageType.IM_INFO);
		message.setData(info);

		//DateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String tm = Util.format(new Date());
		
		System.err.println("["+this.account+"]"+info+"("+tm+")");
		Engine.getInstance().fire(message);
	}
	
	private void infoact(){
		//DateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		details.put("lastatv", String.valueOf(System.currentTimeMillis()).getBytes());//设置最后活动时间
		String tm = Util.format(new Date());
		
		message = new EngineMessage();
		message.setTid(this.id);
		message.setType(EngineMessageType.IM_INFOACT);
		message.setData(tm);

		
		//System.err.println("["+this.account+"]ACT("+tm+")");
		Engine.getInstance().fire(message);
	}
	
	private void setNick(String nick){
		
		message = new EngineMessage();
		message.setTid(this.id);
		message.setType(EngineMessageType.IM_NICK);
		message.setData(nick);

		
		//System.err.println("["+this.account+"]ACT("+tm+")");
		Engine.getInstance().fire(message);
	}
	
	private void setProfile(int level, int days){
		message = new EngineMessage();
		message.setTid(this.id);
		message.setType(EngineMessageType.IM_PROFILE);
		message.setData(level+":"+days);
		
		Engine.getInstance().fire(message);
	}
}
