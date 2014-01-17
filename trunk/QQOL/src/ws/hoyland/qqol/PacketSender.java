package ws.hoyland.qqol;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ws.hoyland.util.Converts;

public class PacketSender implements Runnable {

	private boolean run = false;
	private BlockingQueue<Packet> queue = new ArrayBlockingQueue<Packet>((1024 + 512) * 100 * 10);
	private Packet packet;
	private String account = null;
	private String type = null;
	private String seq = null;
	private int retry = -1;

	private static PacketSender instance;
	private static String FHD = "#0825#0836#00BA#0828#00EC#005C#00CD#0058#";//00CE, 0017, 0062不要
	
	public static synchronized void reset(){
		instance = new PacketSender();
	}
	
	public static synchronized PacketSender getInstance(){
		if(instance==null){
			reset();
		}
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
				byte[] bs = packet.getBuffer().array();
				account = String.valueOf(Long.parseLong(Converts.bytesToHexString(Util.slice(bs, 7, 4)), 16));
				type = Converts.bytesToHexString(Util.slice(bs, 3, 2));
				seq = Converts.bytesToHexString(Util.slice(bs, 5, 2));
				retry = Integer.parseInt(Converts.bytesToHexString(Util.slice(bs, 13, 1)), 16);
				System.err.println("-->["+account+"]("+Util.format(new Date())+")"+type+"["+retry+"]");
				
//				this.account = String.valueOf(Long.parseLong(Converts.bytesToHexString(Util.slice(buffer, 7, 4)), 16));
//				this.details = Engine.getInstance().getAcccounts().get(account);
//				this.seq = Converts.bytesToHexString(Util.slice(buffer, 5, 2));
				if("0017".equals(type)){ //0017发送后关闭连接
					synchronized(Engine.getInstance().getChannels()) {
						try {
							Engine.getInstance().getChannels().get(account).close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						Engine.getInstance().getChannels().remove(account);
					}
				}else{
					//启动检测线程
					if(FHD.contains("#"+type+"#")&&retry<Checker.RT){
						Checker checker = new Checker(account, type, seq, retry);
						Engine.getInstance().addChecker(checker);
					}
				}
				Thread.sleep(1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void put(Packet packet){
		try {
			queue.put(packet);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public int size(){
		return this.queue.size();
	}
	
}
