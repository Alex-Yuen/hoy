package ws.hoyland.mudos.core.service;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class Service implements Runnable {
	
	private boolean run;
	private Selector selector;
	private LinkedList<SocketChannel> list = null;
	
	public Service(){
		this.run = true;
		this.list = new LinkedList<SocketChannel>();
		
        this.connectSelector = connectSelector;
        this.acceptedConnections = list;

        // open a server socket channel that will be able to create socket channels for
        // incoming connections
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // bind our shiny new socket to the port specified
        InetSocketAddress address = new InetSocketAddress(port);
        ssc.socket().bind(address);

        // DEBUG:
        //System.out.println("Bound to " + address);

        // tell the selector that, when we ask [.select()] we want to know
        // about attempted connections
        ssc.register(this.connectSelector, SelectionKey.OP_ACCEPT);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(run){
			
		}
	}
	
	public void stop(){
		this.run =  false;
	}

}
