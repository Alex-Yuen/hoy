package ws.hoyland.vc.servlet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

//import ws.hoyland.vc.PCMSender;
//import ws.hoyland.vc.Dispatcher;
import ws.hoyland.vc.VCServer;

public class InitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5742532507382683705L;
	protected boolean run = false;
	private int port;//监听端口
//	private Dispatcher dispatcher = null;
	private ServerSocketChannel serverChannel = null;
	private VCServer server = null;
//	private PCMSender sender = null;
	
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
		List<SocketChannel> scs = new ArrayList<SocketChannel>();
//		Map<String, ByteBuffer> bs = new HashMap<String, ByteBuffer>();
//		dispatcher = new Dispatcher(scs, bs);
//		new Thread(dispatcher).start();
		
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
		
//		server = new VCServer(selector, scs, bs);
		server = new VCServer(selector, scs);
		new Thread(server).start();
		
//		sender = new PCMSender(scs);
//		new Thread(sender).start();
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
	}

	@Override
	public void destroy() {
//		sender.stop();
		server.stop();
//		dispatcher.stop();
		
		try {
			serverChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
