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

				byte[] bs = packet.getBuffer().array();
				account = String.valueOf(Long.parseLong(Converts.bytesToHexString(Util.slice(bs, 7, 4)), 16));
				type = Converts.bytesToHexString(Util.slice(bs, 3, 2));
				seq = Converts.bytesToHexString(Util.slice(bs, 5, 2));
				retry = Integer.parseInt(Converts.bytesToHexString(Util.slice(bs, 13, 1)), 16);
				
				try{
					packet.getDc().write(packet.getBuffer());
				}catch(Exception ex){//连接可能被0017所关闭
					//因为0017已经有开始新的登录线程，此时不做任何处理
					ex.printStackTrace();
					continue;
					//新建任务
//					Engine.getInstance().getAcccounts().get(account).remove("login");
//					Engine.getInstance().getAcccounts().get(account).remove("0058DOING");
//					Task task = new Task(Task.TYPE_0825, account);
//					//Engine.getInstance().addTask(task);
//					Engine.getInstance().addSleeper(new Sleeper(task));
				}
				System.err.println("->["+account+"]("+Util.format(new Date())+")"+type+"["+retry+"]");
				
//				this.account = String.valueOf(Long.parseLong(Converts.bytesToHexString(Util.slice(buffer, 7, 4)), 16));
//				this.details = Engine.getInstance().getAcccounts().get(account);
//				this.seq = Converts.bytesToHexString(Util.slice(buffer, 5, 2));
				if("0017".equals(type)&&Engine.getInstance().getAcccounts().get(account).get("boot")!=null){ //0017发送后关闭连接?
					//被踢之后，需要这么处理
					synchronized(Engine.getInstance().getChannels()) {
						try {
							Engine.getInstance().getChannels().get(account).close();
							//info("连接关闭");
						} catch (Exception e) {
							e.printStackTrace();
						}
						Engine.getInstance().getChannels().remove(account);
						
						//重新登录
						Engine.getInstance().getAcccounts().get(account).remove("boot");
						Task task = new Task(Task.TYPE_0825, account);
						Engine.getInstance().addSleeper(new Sleeper(task));
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
