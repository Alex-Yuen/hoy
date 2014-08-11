package ws.hoyland.vc;

import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Util;

//接收并存到缓冲区
public class Receiver implements Runnable {

	private Selector selector;
	private Player player;
	private boolean run = false;
	private ByteBuffer bf = ByteBuffer.allocate(768+4);
	private boolean wakeup = false;
	private byte[] buffer = null;
	private int size = -1;

	public Receiver(Selector selector, Player player) {
		this.selector = selector;
		this.player = player;
		this.run = true;
	}

	public void wakeup() {
		this.wakeup = true;
	}

	public void sleep() {
		this.wakeup = false;
	}

	@Override
	public void run() {
         
		while (run) {
			//System.out.println("client run...");
			try {
				int ec = 0;
				if (!wakeup) {
					ec = selector.select();
				} else {
					ec = selector.selectNow();
				}
//				ec = selector.select();
				//System.out.println("client check...");

				if (ec > 0) {
					Set<?> selectedKeys = selector.selectedKeys();
					Iterator<?> iterator = selectedKeys.iterator();
					while (iterator.hasNext()) {
						SelectionKey sk = (SelectionKey) iterator.next();
						iterator.remove();
						try {
							if(sk.isConnectable()){
			                    SocketChannel channel=(SocketChannel)sk.channel();
			                    
			                    //如果正在连接，则完成连接
			                    if(channel.isConnectionPending()){
			                        channel.finishConnect();
			                    }
			                    
			                    channel.configureBlocking(false);
			                    //向服务器发送消息
			                    //channel.write(ByteBuffer.wrap(new String("send message to server.").getBytes()));
			                    
			                    //连接成功后，注册接收服务器消息的事件
			                    channel.register(selector, SelectionKey.OP_READ);
			                    System.out.println("Connected to server");
			                }else if (sk.isReadable()) {
			                	bf.clear();
			                	SocketChannel channel = (SocketChannel) sk
										.channel();
								// bf = ByteBuffer.allocate(1024);
								try {// ClosedChannelException by 0017
									size = channel.read(bf);
								} catch (Exception e) {
									e.printStackTrace();
									channel.close();
									continue;
								}
								//System.out.println(size);
								bf.flip();
								buffer = Util.slice(bf.array(), 0, size);
								// System.out.println("RECV:"+buffer.length);
								// System.out.println(Converts.bytesToHexString(buffer));

								//bf.clear();
								String key = Converts.bytesToHexString(Util.slice(buffer, 0, 4));
								if(!player.getBuffer().containsKey(key)){									
									player.getBuffer().put(key, ByteBuffer.allocate(1024*10));
								}
								
								player.getBuffer().get(key).put(Util.slice(buffer, 4, buffer.length-4));
								
								System.out.println(key+"<-"+size);
			                    //String message = new String(buffer);
								// buffer;
//								System.out.println("Client RECV:"+new String(buffer));
								//numBytesRead = playbackInputStream.read(data); 
								//if(y%2==0){
								//System.out.println(line.isRunning());
								//System.out.println(channel.getRemoteAddress().toString());
								//if(buffer[0]==0xF&&buffer[1]==0xD){
//									line.write(buffer, 0, buffer.length);
								//	System.out.println("FFFFFFFFFFFF");
//								}else if (buffer[0]==0xF&&buffer[1]==0xE){
//									linex.write(buffer, 2, buffer.length-2);
//									System.out.println("AAAAAAAAAAAAA");
//								}else{
//									//linex.write(buffer, 0, buffer.length);
//								}
//								if(channel.getRemoteAddress().toString().startsWith("/127")){
//								//
//									
//									System.out.println("from local client");
//								}else{
//										
//								}
				                
								//}else{
//					           linex.write(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12}, 0, 12);
//					           linex.write(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12}, 0, 12);
//					           linex.write(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12}, 0, 12);
					            //linex.write(buffer, 0, buffer.length);
					            //linex.write(Util.slice(buffer, 2, 20), 0, 20);
								//}
								//y++;
//				                new Thread(new Runnable(){
//
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//										try{
//											//Thread.sleep(500);
//										}catch(Exception e){
//											e.printStackTrace();
//										}
//						                linex.write(buffer, 0, buffer.length);
//										
//									}
//				                	
//				                }).start();
			                }
						} catch (CancelledKeyException e) {
							sk.cancel();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		this.run = false;
	}
}
