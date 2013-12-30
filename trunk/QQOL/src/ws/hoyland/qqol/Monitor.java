package ws.hoyland.qqol;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;

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
		this.bf = ByteBuffer.allocate(1024);
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
							datagramChannel.read(bf);
							bf.flip();

							buffer = Util.pack(bf
									.array());
//							System.out.println(Converts.bytesToHexString(Util.pack(buffer
//									.array())));
							bf.clear();
							
							header = Util.slice(buffer, 3, 2);
							account = String.valueOf(Long.parseLong(Converts.bytesToHexString(Util.slice(buffer, 7, 4)), 16));
							
							//根据返回，处理各种消息
							if(header[0]==(byte)0x08&&header[1]==(byte)0x25){
								if(buffer.length==135){
									//重定向
									content = Util.slice(buffer, 14, 120);
									decrypt = crypter.decrypt(content, Engine.getInstance().getAcccounts().get(account).get("key0825"));
									Engine.getInstance().getAcccounts().get(account).put("ips", Util.slice(decrypt, 95, 4));
									task = new Task(Task.TYPE_0825, account);
									Engine.getInstance().addTask(task);
								}else{
									//发起0836
									
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
