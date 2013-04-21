package ws.hoyland.qm;

/**
 * 生产者，消费者模式
 * 
 * @author hoy
 * 
 */
public class Basket {
	private byte count = 0;

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
