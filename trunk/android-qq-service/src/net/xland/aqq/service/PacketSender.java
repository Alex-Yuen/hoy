package net.xland.aqq.service;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PacketSender implements Runnable {
	private QQServer server;
	private boolean rf = true;
	
	public PacketSender(QQServer server){
		this.server = server;		
	}
	
	public void stop(){
		this.rf = false;
	}
	
	@Override
	public void run() {
		while(rf){
			try{
				Packet packet = server.takePacket();
				System.out.println("packet="+packet);
				System.out.println("packet's sid="+packet.getSid());
				SocketChannel sc = server.getSocketChannel(packet.getSid());
				System.out.println("sc="+sc);
//				int count = 0;
//				while(!sc.isConnected()&&count<3){
//					Thread.sleep(1);
//					count++;
//				}
				
				if(sc.isConnected()){
					sc.write(ByteBuffer.wrap(packet.getContent()));
				}else{
					server.submit(packet); //放入队列的最后
				}
				Thread.sleep(5);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
