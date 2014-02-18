package ws.hoyland.nio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author 徐辛波(sinpo.xu@hotmail.com) Oct 19, 2008
 */
public class UDPClient extends Thread {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Selector selector = null;
	private boolean wakeup = false;
	
	public UDPClient(Selector selector){
		this.selector = selector;
	}
	
	public void setWakeup(boolean wakeup) {
		this.wakeup = wakeup;
		if(wakeup){
			selector.wakeup();
		}
	}
	
	public void run() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(100);
		while (true) {
			try {
				int eventsCount = 0;
				if(!wakeup){
					eventsCount = selector.select();
				}else {
					eventsCount = selector.selectNow();
				}
				
				if (eventsCount > 0) {
					Set<?> selectedKeys = selector.selectedKeys();
					Iterator<?> iterator = selectedKeys.iterator();
					while (iterator.hasNext()) {
						SelectionKey sk = (SelectionKey) iterator.next();
						iterator.remove();
						if (sk.isReadable()) {
							DatagramChannel datagramChannel = (DatagramChannel) sk
									.channel();
							datagramChannel.read(byteBuffer);
							byteBuffer.flip();

							System.out.println("["+sdf.format(new Date())+"]<- "+Charset.defaultCharset()
									.decode(byteBuffer).toString());
							byteBuffer.clear();
//							datagramChannel.write(Charset.defaultCharset()
//									.encode("Tell me your time"));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Selector selector = null;
		UDPClient client = null;

		try {
			selector = Selector.open();
			client = new UDPClient(selector);
			client.start();//开始处理消息循环
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<Integer, DatagramChannel> map = new HashMap<Integer, DatagramChannel>();
		DatagramChannel channel = null;
		
		try {
			for(int i=0;i<10;i++){
				channel = DatagramChannel.open();
				channel.configureBlocking(false);
				SocketAddress sa = new InetSocketAddress("112.124.106.47", 8023);//112.124.106.47
				channel.connect(sa);
				
				client.setWakeup(true);
				channel.register(selector, SelectionKey.OP_READ);
				client.setWakeup(false);
				
				map.put(i, channel);
				System.out.println("["+sdf.format(new Date())+"]-> "+i);
				channel.write(Charset.defaultCharset().encode(String.valueOf(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(true){
			try{
				Thread.sleep(1000*20);
			}catch(Exception e){
				e.printStackTrace();
			}
			Iterator<Integer> it = map.keySet().iterator();
			int key = -1;
			
			try{
				while(it.hasNext()){
					key = (Integer)it.next();
					channel = map.get(key);
					System.out.println("["+sdf.format(new Date())+"]-> "+key);
					Thread.sleep(100);
					channel.write(Charset.defaultCharset().encode(String.valueOf(key)));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
}