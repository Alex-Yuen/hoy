package net.xland.aqq.service;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import net.xland.util.Converts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketSender implements Runnable {
	private QQServer server;
	private boolean rf = true;
	
	private static Logger logger = LogManager.getLogger(PacketSender.class.getName());
	
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
				//System.out.println("packet="+packet);
				//System.out.println("packet's sid="+packet.getSid());
				SocketChannel sc = server.getSocketChannel(packet.getSid());
				//System.out.println("sc="+sc);
//				int count = 0;
//				while(!sc.isConnected()&&count<3){
//					Thread.sleep(1);
//					count++;
//				}
				
				if(sc==null){
//					logger.info(packet.getSid()+" [SCNULL1] " + sc);
					logger.info(packet.getSid()+" [SCNULL] ");
				}else{
					if(sc.isConnected()){
	//					System.out.println("SEND:"+packet.getSid());
//						logger.info(packet.getSid()+" [SCNULL3] " + sc);
						logger.info(packet.getSid()+" [SEND] " + Converts.bytesToHexString(packet.getContent()));
						sc.write(ByteBuffer.wrap(packet.getContent()));
					}else{
						//如果Monitor检查到关闭，那么这里如何处理
						//logger.info(packet.getSid()+" [SCNULL4] " + sc);
						logger.info(packet.getSid()+" [REJOIN] ");
						packet.setSlpt(packet.getSlpt()+1);
						server.submit(packet); //放入队列的最后
					}
				}
				Thread.sleep(5);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
