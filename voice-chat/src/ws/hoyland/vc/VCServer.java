package ws.hoyland.vc;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import ws.hoyland.util.Util;

public class VCServer implements Runnable {

	private boolean run = false;
	private Selector selector = null;

	private ByteBuffer bf = ByteBuffer.allocate(1024+4);
	private byte[] buffer = null;
	private int size = -1;
	private List<SocketChannel> scs = null;
//	private Map<String, ByteBuffer> bs = null;
	
	public VCServer(Selector selector, List<SocketChannel> scs){//, Map<String, ByteBuffer> bs){
		this.selector = selector;
		this.scs = scs;
//		this.bs = bs;
		this.run = true;
	}
	
	public void stop(){
		this.run = false;
	}
	@Override
	
	public void run() {
		try{
			while(selector!=null&&run){
	            //当有注册的事件到达时，方法返回，否则阻塞。
	            selector.select();
	            
	            //获取selector中的迭代器，选中项为注册的事件
	            Iterator<SelectionKey> ite=selector.selectedKeys().iterator();
	            
	            while(ite.hasNext()){
	                SelectionKey key = ite.next();
	                //删除已选key，防止重复处理
	                ite.remove();
	                //客户端请求连接事件
	                if(key.isAcceptable()){
	                    ServerSocketChannel server = (ServerSocketChannel)key.channel();
	                    //获得客户端连接通道
	                    SocketChannel channel = server.accept();
	                    scs.add(channel);
	                    channel.configureBlocking(false);
	                    //向客户端发消息
	                    //channel.write(ByteBuffer.wrap(new String("from server").getBytes()));
	                    //在与客户端连接成功后，为客户端通道注册SelectionKey.OP_READ事件。
	                    channel.register(selector, SelectionKey.OP_READ);
	                    
	                    System.out.println("Client connected");
	                }else if(key.isReadable()){//有可读数据事件
	                    //获取客户端传输数据可读取消息通道。
	                    SocketChannel channel = (SocketChannel)key.channel();
	                    if(channel.isConnected()){
//	                    	System.out.println("W00");
	                    	bf.clear();
		                    //创建读取数据缓冲器
		                    try {
								size = channel.read(bf);;
							} catch (Exception e) {
								e.printStackTrace();
								scs.remove(channel); //断开则删除
//								bs.remove(channel.toString());
								channel.close();
								continue;
							}
							bf.flip();
							
							if(size>0){
								buffer = Util.slice(bf.array(), 0, size);
								/**
//								String sid = channel.toString();
								String sid = channel.socket().getRemoteSocketAddress().toString();
								System.out.println(channel.toString() + " <- "+size);
								if(!bs.containsKey(sid)){									
									bs.put(sid, ByteBuffer.allocate(1024*15));
								}
																
//								System.out.println("W01");
								synchronized(bs.get(sid)){
									//System.out.println("W1:"+bs.get(sid).position());
									bs.get(sid).put(buffer);
									//System.out.println("W2:"+bs.get(sid).position());
									//System.out.println(sid+"<-S"+buffer.length);
								}
													
								**/
								System.out.println(channel.toString() + " <-> "+size);
								for(SocketChannel sc: scs){ //除本身外，全部转发
									//if(sc!=channel){
										sc.write(ByteBuffer.wrap(buffer));
									//}
								}
								//System.out.println("Server RECV:"+new String(buffer));
							}else{
//								System.out.println("W02");
							}
	                    }
	                }
	            }
	        }
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
