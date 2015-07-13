package net.xland.aqq.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
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
							if (sk.isConnectable()) {  
//			                    System.out.println("正在连接");  
			                    SocketChannel sc = (SocketChannel) sk.channel();  //
			                    // 判断此通道上是否正在进行连接操作。  
			                    // 完成套接字通道的连接过程。  
			                    if (sc.isConnectionPending()) { 
			                        //完成连接的建立（TCP三次握手）
			                        sc.finishConnect();  
//			                        System.out.println("完成连接");
				                    
			                        synchronized(server){
					                    setWakeup(true);
					    				QQSelector.selector.wakeup();
					    				sc.register(QQSelector.selector, SelectionKey.OP_READ);
					    				setWakeup(false);
			                        }
			                    }
							}else if (sk.isReadable()) {
								SocketChannel sc = (SocketChannel) sk
										.channel();
								//bf = ByteBuffer.allocate(1024);
								try{//ClosedChannelException by 0017
									size = sc.read(bf);
								}catch(IOException e){//need to remove from channels?//TODO
									e.printStackTrace();
									System.err.println("CLOSE-EXCEPTION-----"+sc);
									sk.cancel();
									//sc.close();
									server.releaseSession(sc);
									continue;
								}catch(Exception e){
									e.printStackTrace();
									continue;
								}
								
								if(size<0){
									System.err.println("CLOSE-EXCEPTION(-1)-----"+sc);
									sk.cancel();
									//sc.close();
									server.releaseSession(sc);
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
