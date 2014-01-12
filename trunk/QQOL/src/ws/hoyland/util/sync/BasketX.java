package ws.hoyland.util.sync;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 生产者，消费者模式
 * 
 * @author hoy
 * 
 */
public class BasketX {
	private static BasketX instance;
	private Queue<Object> queue = new LinkedList<Object>();
	
	private BasketX(){
		
	}
	
	public static BasketX getInstance(){
		if(instance==null){
			instance = new BasketX();
		}
		return instance;
	}
	
	public void push() {
//		System.err.println("PUSH");
		try {
			queue.remove();
			if (queue.size() > 0) {
				Object object = queue.peek();
				synchronized(object){
					object.notify();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pop() {
		try {
			Object object = new Object();
			queue.add(object);
			if(object!=queue.peek()){
				synchronized(object){
					object.wait();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
