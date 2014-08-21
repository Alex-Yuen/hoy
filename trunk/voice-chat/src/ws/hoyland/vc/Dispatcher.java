package ws.hoyland.vc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

@Deprecated
public class Dispatcher implements Runnable {
	private Map<String, ByteBuffer> bs = null;
	private List<SocketChannel> scs = null;
	private boolean run = false;
	
	public Dispatcher(List<SocketChannel> scs, Map<String, ByteBuffer> bs) {
		this.scs = scs;
		this.bs = bs;
		this.run = true;
	}

	@Override
	public void run() {

		byte[] bbs = null;
		byte[] tbbs = null;
		int RMS = 0;
		int maxRMS = 0;
		
		while(run){
			tbbs = null;
			RMS = 0;
			maxRMS = 0;
			
//			if(bs.size()==0){
//				try{
//					Thread.sleep(100);
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
			//mix and send to all channels
			for(ByteBuffer bb:bs.values()){
				synchronized(bb){
					//System.out.println("R1:"+bb.position());
					bbs = new byte[bb.position()];
					bb.flip();
					bb.get(bbs);					
					bb.clear();
					//System.out.println("R2:"+bb.position());
				}
				
				if(bbs.length>0){						
					RMS = calculateRMSLevel(bbs);
					if(RMS>maxRMS){
						maxRMS = RMS;
						tbbs = bbs;
					}
				}
			}
			
			if(tbbs!=null){
				for(SocketChannel sc:scs){
					try {
						System.out.println(sc.toString() + " -> "+tbbs.length);
						sc.write(ByteBuffer.wrap(tbbs));
						//System.out.println(sc.toString()+"D->"+bbs.length);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			bbs = null;
		}
	}
	
	public void stop(){
		this.run = false;
	}
	
	private int calculateRMSLevel(byte[] audioData)
	{ // audioData might be buffered data read from a data line
	    long lSum = 0;
	    for(int i=0; i<audioData.length; i++)
	        lSum = lSum + audioData[i];

	    double dAvg = lSum / audioData.length;

	    double sumMeanSquare = 0d;
	    for(int j=0; j<audioData.length; j++)
	        sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

	    double averageMeanSquare = sumMeanSquare / audioData.length;
	    return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
	}
}
