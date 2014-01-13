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
	
	public static synchronized BasketX getInstance(){
		if(instance==null){
			//System.err.println("new basketx");
			instance = new BasketX();
		}
		return instance;
	}
	
	public void push() {
//		System.err.println("PUSH");
		try {
			//System.err.println("remove"+queue.size());
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
			//System.err.println(this+"add"+queue.size());
			//queue.
			if(object!=queue.peek()){
			//	System.err.println(this+"/2:"+queue.size());
				synchronized(object){
					object.wait();
				}
			}else{
			//	System.err.println(this+"/1:"+queue.size());
			}
			//System.err.println("after"+queue.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
