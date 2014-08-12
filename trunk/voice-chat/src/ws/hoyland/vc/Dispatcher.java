package ws.hoyland.vc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

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
		while(run){
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
			}
			
			if(bbs!=null){
				for(SocketChannel sc:scs){
					try {
						sc.write(ByteBuffer.wrap(bbs));
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
}
