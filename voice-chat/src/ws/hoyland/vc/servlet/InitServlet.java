package ws.hoyland.vc.servlet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import ws.hoyland.util.Util;
import ws.hoyland.vc.Dispatcher;

public class InitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5742532507382683705L;
	protected boolean run = false;
	private int port;//监听端口
	private Dispatcher dispatcher = null;
	private ServerSocketChannel serverChannel = null;
	
	public InitServlet() {
		this.run = true;
	}

	@Override
	public void init(final ServletConfig config) {
		try {
			super.init(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		port = Integer.parseInt(config
				.getInitParameter("port"));
		
		Selector selector = null;
		ByteBuffer bf = ByteBuffer.allocate(1024);
		byte[] buffer = null;
		int size = -1;
		List<SocketChannel> scs = new ArrayList<SocketChannel>();
		Map<String, ByteBuffer> bs = new HashMap<String, ByteBuffer>();
		dispatcher = new Dispatcher(scs, bs);
		new Thread(dispatcher).start();
		
		try{
			//获取一个ServerSocket通道
	        serverChannel = ServerSocketChannel.open();
	        serverChannel.configureBlocking(false);
	        serverChannel.socket().bind(new InetSocketAddress(port));
	        //获取通道管理器
	        selector=Selector.open();
	        //将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
	        //只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
	        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	        System.out.println("Server start on "+port);       
	    }catch(Exception e){
			e.printStackTrace();
		}
		
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
								bs.remove(channel.toString());
								channel.close();
								continue;
							}
							bf.flip();
							
							if(size>0){
								buffer = Util.slice(bf.array(), 0, size);
								
								String sid = channel.toString();
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
																
//								for(SocketChannel sc: scs){ //除本身外，全部转发
//									//if(sc!=channel){
//										sc.write(ByteBuffer.wrap(buffer));
//									//}
//								}
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

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
	}

	@Override
	public void destroy() {
		this.run = false;
		dispatcher.stop();
		
		try {
			serverChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
