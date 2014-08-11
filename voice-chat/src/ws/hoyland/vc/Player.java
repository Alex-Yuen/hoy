package ws.hoyland.vc;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Player implements Runnable {

	private boolean run = false;
	private SourceDataLine line = null;
	private int size = 16384;
	private Map<String, ByteBuffer> buffer = null;

	public Player() {
		this.run = true;
		buffer = new HashMap<String, ByteBuffer>();
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

		byte[] tb = new byte[1024];
		
		while(run){
			// mix
			for(ByteBuffer bais:buffer.values()){
				bais.get(tb);
				//mix;
			}
			line.write(tb, 0, tb.length);
		}
		
		line.drain(); 
        line.stop(); 
        line.close(); 
        line = null; 
	}
	
	public void stop() {
		this.run = false;
	}
	
	public Map<String, ByteBuffer> getBuffer(){
		return this.buffer;
	}
}
