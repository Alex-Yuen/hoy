package ws.hoyland.vc;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Sender implements Runnable {

	private SocketChannel sc;
	private boolean run;
	
	public Sender(SocketChannel sc) {
		this.sc = sc;
		this.run = true;
	}
	
	public void stop() {
		this.run = false;
	}

	@Override
	public void run() {
		while(run){
			try{
				sc.write(ByteBuffer.wrap(new String("from client").getBytes()));
				this.wait(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
