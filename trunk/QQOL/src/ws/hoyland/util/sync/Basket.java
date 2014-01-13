package ws.hoyland.util.sync;

/**
 * 生产者，消费者模式
 * 
 * @author hoy
 * 
 */
public class Basket {
	private byte count = 1;
	private static Basket instance;

	private Basket(){
		
	}
	
	public static synchronized Basket getInstance(){
		if(instance==null){
			instance = new Basket();
		}
		return instance;
	}
	
	public synchronized void push() {
		try {
			while (count == 1) {
				this.wait();
			}
			this.notify();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IllegalMonitorStateException e) {
			e.printStackTrace();
		}
		count = 1;
	}

	public synchronized void pop() {
		try {
			while (count == 0) {
				this.wait();
			}
			this.notify();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IllegalMonitorStateException e) {
			e.printStackTrace();
		}
		count = 0;
	}
}
