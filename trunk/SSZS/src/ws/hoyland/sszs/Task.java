package ws.hoyland.sszs;

import java.util.Observable;
import java.util.Observer;

public class Task implements Runnable, Observer {

	private String line;
	
	public Task(String line) {
		// TODO Auto-generated constructor stub
		this.line = line;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(line);
	}

	@Override
	public void update(Observable obj, Object arg) {
		// TODO Auto-generated method stub
		// 接收来自Engin的消息
		
	}

}
