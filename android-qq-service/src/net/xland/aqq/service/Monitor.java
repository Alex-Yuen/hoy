package net.xland.aqq.service;

import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;

import net.xland.util.XLandUtil;

public class Monitor implements Runnable {

	private QQServer server;
	private boolean wakeup = false;
	private boolean rf = true;
	private ByteBuffer bf = ByteBuffer.allocate(1024+512);
	private byte[] buffer = null;
	private int size = -1;
	
	public Monitor(QQServer server){
		this.server = server;
	}
	
	public void setWakeup(boolean wakeup) {
		this.wakeup = wakeup;
	}
		
	@Override
	public void run() {
		while(rf){
			try{
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
	
								buffer = XLandUtil.slice(bf
										.array(), 0, size);
								//System.out.println("RECV:"+buffer.length);
								//System.out.println(Converts.bytesToHexString(buffer));
								
								bf.clear();
								this.server.addReceiver(new Receiver(this.server, buffer));
							}
						}catch(CancelledKeyException e){
							sk.cancel();
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
	}
}
