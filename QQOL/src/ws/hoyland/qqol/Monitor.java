package ws.hoyland.qqol;

import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;

import ws.hoyland.util.Converts;

public class Monitor implements Runnable {
	private boolean run = false;
	private ByteBuffer bf = ByteBuffer.allocate(1024);
	private boolean wakeup = false;
	private byte[] buffer = null;
	private int size = -1;
	private String hd = null;
	private String FHD = "#0825#0836#0828#00BA#00EC#005C#00CE#0062#0058#";
	
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

	public static synchronized void reset(){
		instance = new Monitor();
	}
	
	public static synchronized Monitor getInstance( ){
//		if(instance==null){
//			instance = new Monitor();
//		}
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
						try{
							if (sk.isReadable()) {
								DatagramChannel datagramChannel = (DatagramChannel) sk
										.channel();
								//bf = ByteBuffer.allocate(1024);
								try{//ClosedChannelException by 0017
									size = datagramChannel.read(bf);
								}catch(Exception e){								
									e.printStackTrace();
									continue;
								}
								bf.flip();
	
								buffer = Util.slice(bf
										.array(), 0, size);
								//System.out.println("RECV:"+buffer.length);
								//System.out.println(Converts.bytesToHexString(buffer));
								
								bf.clear();
								
								hd = Converts.bytesToHexString(Util.slice(buffer, 3, 2));
								if(buffer.length>0&&(FHD.contains("#"+hd+"#")||("0017".equals(hd)&&buffer.length==231))){
									Receiver receiver = new Receiver(buffer);
									Engine.getInstance().addReceiver(receiver);
								}
							}
						}catch(CancelledKeyException e){
							sk.cancel();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
