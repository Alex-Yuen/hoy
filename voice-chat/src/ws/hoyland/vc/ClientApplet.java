package ws.hoyland.vc;

import java.applet.Applet;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ClientApplet extends Applet {
	public ClientApplet() {
	}

	private Receiver receiver;
	private Selector selector;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3481124422734904591L;

	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		System.out.println("init...");
		try{
			selector = Selector.open();
			receiver = new Receiver(selector);			
			new Thread(receiver).start();
			
			SocketAddress sa = new InetSocketAddress("127.0.0.1", 8000);
			SocketChannel sc = SocketChannel.open();
			sc.configureBlocking(false);
			sc.connect(sa);
			//System.err.println("getReceiveBufferSize:"+dc.socket().getReceiveBufferSize());
			//dc.socket().setReceiveBufferSize(arg0)
			//receiver.wakeup();
			//selector.wakeup();
			sc.register(selector, SelectionKey.OP_READ);	
			//receiver.sleep();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
