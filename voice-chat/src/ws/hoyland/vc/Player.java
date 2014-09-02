package ws.hoyland.vc;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import ws.hoyland.util.MyByteBuffer;

public class Player implements Runnable {
	private SourceDataLine line = null;
	private int size = 1024*10;
	private Map<String, MyByteBuffer> bs = new HashMap<String, MyByteBuffer>();
//	private byte[] bbs = null;
	private byte[] bbs = new byte[1024];
	private boolean run = false;
	private boolean nw = false;
	
	public Player() {
		this.run = true;
		AudioFormat format = new AudioFormat(8000, 16, 2, true, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		
		try {
			line = (SourceDataLine) AudioSystem.getLine(info); // 获得与指定Line.Info对象中的描述匹配的行
			line.open(format, size); // 打开所需系统资源，并使之可操作
			//System.out.println(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		line.start();
		
		while(run){
			int i = 0;
			nw = false;
			
			for(MyByteBuffer bb:bs.values()){
//				synchronized(bb){
					//System.out.println("R1:"+bb.position());
				if(bb.size()>0){
					nw = true;
					if(i==0){
						bb.get(bbs);
//						bbs = new byte[bb.position()];
//						bb.flip();
//						bb.get(bbs);					
//						bb.clear();
					}else{
						bb.get(new byte[1024]);
//						bb.flip();
//						bb.clear();
					}
					//System.out.println("R2:"+bb.position());
				}
				i++;
//				break;
			}
			
			if(nw){
				System.out.println("Playing...["+bs.size()+"]"+bbs.length);
				line.write(bbs, 0, bbs.length);
			}
		}
	}

	public Map<String, MyByteBuffer> getBS(){
		return this.bs;
	}
	
	public void stop() {
		this.run = false;
	}
}
