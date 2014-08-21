package ws.hoyland.vc;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Player implements Runnable {
	private SourceDataLine line = null;
	private int size = 1024*10;
	private Map<String, ByteBuffer> bs = new HashMap<String, ByteBuffer>();
	private byte[] bbs = null;
	private boolean run = false;
	
	public Player() {
		this.run = true;
		AudioFormat format = new AudioFormat(8000, 16, 2, true, true);
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
			bbs = null;
			for(ByteBuffer bb:bs.values()){
				synchronized(bb){
					//System.out.println("R1:"+bb.position());
					bbs = new byte[bb.position()];
					bb.flip();
					bb.get(bbs);					
					bb.clear();
					//System.out.println("R2:"+bb.position());
				}
				break;
			}
			
			if(bbs!=null){
				System.out.println("Playing... "+bbs.length);
				line.write(bbs, 0, bbs.length);
			}
		}
	}

	public Map<String, ByteBuffer> getBS(){
		return this.bs;
	}
	
	public void stop() {
		this.run = false;
	}
}
