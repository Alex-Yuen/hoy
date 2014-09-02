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
	private byte[] bbx = new byte[1024];
	private boolean run = false;
//	private boolean nw = false;
	
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
//			nw = false;
			byte[][] bbs = new byte[bs.size()][1024];
			
//			System.out.println("bs size:"+bs.size());
			System.out.println("Playing: "+bbs.length);
			if(bbs.length>0){	
				int i = 0;			
				for(MyByteBuffer bb:bs.values()){
//					synchronized(bb){
						//System.out.println("R1:"+bb.position());
					if(bb.size()>0){
//						nw = true;
//						if(i==0){
							bb.get(bbs[i]);
//							bbs = new byte[bb.position()];
//							bb.flip();
//							bb.get(bbs);					
//							bb.clear();
//						}else{
//							bb.get(new byte[1024]);
////							bb.flip();
////							bb.clear();
//						}
						//System.out.println("R2:"+bb.position());
					}
					i++;
//					break;
				}
				
				for(int m=0;m<bbx.length;m++){
					int total = 0;
					for(int k=0;k<bbs.length;k++){
						total += bbs[k][m];
					}
					bbx[m] = (byte)(total/bbs.length);
				}
			
//			if(nw){
//				System.out.println("Playing...["+bs.size()+"]"+bbx.length);
				line.write(bbx, 0, bbx.length);
//			}
			}else{
				try{
					synchronized(this){
						this.wait(100);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
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
