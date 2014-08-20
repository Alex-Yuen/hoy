package ws.hoyland.vc;

import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import ws.hoyland.util.Util;

//接收并存到缓冲区
public class Receiver implements Runnable {

	private Selector selector;
	private boolean run = false;
	private ByteBuffer bf = ByteBuffer.allocate(1024);
	private boolean wakeup = false;
	private byte[] buffer = null;
	private int size = -1;
	
	private SourceDataLine line = null;
	private int bfsize = 16384;

	public Receiver(Selector selector) {
		this.selector = selector;
		this.run = true;
		
		AudioFormat format = new AudioFormat(8000, 16, 2, true, true);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);		

		try {
			line = (SourceDataLine) AudioSystem.getLine(info); // 获得与指定Line.Info对象中的描述匹配的行
			line.open(format, bfsize); // 打开所需系统资源，并使之可操作
			//System.out.println(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void wakeup() {
		this.wakeup = true;
	}

	public void sleep() {
		this.wakeup = false;
	}

	@Override
	public void run() {
		line.start();
		
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
								try {
									size = channel.read(bf);
								} catch (Exception e) {
									e.printStackTrace();
									channel.close();
									continue;
								}

								bf.flip();
								buffer = Util.slice(bf.array(), 0, size);
								System.out.println(channel.toString()+" <- "+buffer.length);
								line.write(buffer, 0, buffer.length);
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
