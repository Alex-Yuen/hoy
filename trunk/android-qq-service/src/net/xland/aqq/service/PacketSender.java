package net.xland.aqq.service;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

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
				DatagramChannel dc = server.getDatagramChannel(packet.getSid());
				dc.write(ByteBuffer.wrap(packet.getContent()));
				Thread.sleep(1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
