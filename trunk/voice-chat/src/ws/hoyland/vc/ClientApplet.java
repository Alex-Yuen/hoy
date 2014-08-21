package ws.hoyland.vc;

import java.applet.Applet;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.awt.Color;
import javax.swing.JLabel;

public class ClientApplet extends Applet {
	public ClientApplet() {
		setBackground(Color.BLUE);
		
		JLabel lblClientApplet = new JLabel("Client Applet");
		lblClientApplet.setForeground(Color.WHITE);
		add(lblClientApplet);
	}

	private Receiver receiver;
	private Sender sender;
	private Player player;
	private Selector selector;
	private SocketChannel sc;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3481124422734904591L;

	@Override
	public void init() {
		super.init();
		System.out.println("Init...");
		try{
			player = new Player();
			new Thread(player).start();
			
			selector = Selector.open();
			receiver = new Receiver(selector, player);
			new Thread(receiver).start();
			
			int port = Integer.parseInt(getParameter("port"));
			System.out.println("Client parameter<port>: "+port);
			
			SocketAddress sa = new InetSocketAddress(getParameter("server"), port);
			sc = SocketChannel.open();
			sc.configureBlocking(false);
			//System.out.println("c1");
			sc.connect(sa);
			//System.out.println("c2");
			//System.err.println("getReceiveBufferSize:"+dc.socket().getReceiveBufferSize());
			//dc.socket().setReceiveBufferSize(arg0)
			receiver.wakeup();
			selector.wakeup();
			sc.register(selector, SelectionKey.OP_CONNECT);
			receiver.sleep();
			//System.out.println("c3");
			sender = new Sender(sc);
			new Thread(sender).start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		sender.stop();
		receiver.stop();
		player.stop();
		
		if(sc!=null){
			try{
				sc.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}