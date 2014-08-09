package ws.hoyland.vc;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ws.hoyland.util.Util;

public class VCServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		System.out.println("OK");
		Selector selector = null;
		ByteBuffer bf = ByteBuffer.allocate(1024 + 512);
		byte[] buffer = null;
		int size = -1;
		List<SocketChannel> scs = new ArrayList<SocketChannel>();
		
		
		try{
			//获取一个ServerSocket通道
	        ServerSocketChannel serverChannel = ServerSocketChannel.open();
	        serverChannel.configureBlocking(false);
	        serverChannel.socket().bind(new InetSocketAddress(8000));
	        //获取通道管理器
	        selector=Selector.open();
	        //将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
	        //只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
	        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	        System.out.println("服务器端启动成功: 8000");       
	    }catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			while(selector!=null){
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
	                    
	                    System.out.println("客户端请求连接事件");
	                }else if(key.isReadable()){//有可读数据事件
	                    //获取客户端传输数据可读取消息通道。
	                    SocketChannel channel = (SocketChannel)key.channel();
	                    if(channel.isConnected()){
	                    	
		                    //创建读取数据缓冲器
		                    try {// ClosedChannelException by 0017
								size = channel.read(bf);
								//System.out.println(channel.isConnectionPending());
							} catch (Exception e) {
								e.printStackTrace();
								channel.close();
								continue;
							}
							bf.flip();
							
							if(size>0){
								buffer = Util.slice(bf.array(), 0, size);
								// System.out.println("RECV:"+buffer.length);
								// System.out.println(Converts.bytesToHexString(buffer));
								for(SocketChannel sc: scs){
									if(sc!=channel){
										sc.write(ByteBuffer.wrap(buffer));
									}
								}
								//System.out.println("Server RECV:"+new String(buffer));
							}
							bf.clear();
	
		                    //String message = new String(buffer);
							// buffer;
	                    }
	                    //System.out.println("receive message from client, size:" + buffer.position() + " msg: " + message);
//	                    ByteBuffer outbuffer = ByteBuffer.wrap(("server.".concat(msg)).getBytes());
//	                    channel.write(outbuffer);
	                }
	            }
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
