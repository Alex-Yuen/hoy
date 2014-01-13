package ws.hoyland.qqol;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PacketSender implements Runnable {
	private boolean run = false;
	private static PacketSender instance;
	private BlockingQueue<Packet> queue = new ArrayBlockingQueue<Packet>((1024 + 512) * 100 * 10);
	private Packet packet;
	
	public static synchronized void reset(){
		instance = new PacketSender();
	}
	
	public static synchronized PacketSender getInstance(){
//		if(instance==null){
//			instance = new PacketSender();
//		}
		return instance;
	}
	
	private PacketSender(){
		this.run = true;
	}
	
	public void stop() {
		this.run = false;
	}
	
	@Override
	public void run() {
		while(run){
			try{
				packet = queue.take();
				packet.getDc().write(packet.getBuffer());
				Thread.sleep(5);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void put(Packet packet){
		try {
			queue.put(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
