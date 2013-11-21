package ws.hoyland.sszs;

import java.util.Observable;
import java.util.Observer;

public class Task implements Runnable, Observer {

	private String line;
	private boolean run = false;
	private boolean fb = false; //break flag;
	private boolean fc = false; //continue flag;
	
	public Task(String line) {
		// TODO Auto-generated constructor stub
		this.line = line;
		this.run = true;
	}

	@Override
	public void run() {
		//System.out.println(line);
		while(run){
			pretreat();
			if(fb){
				break;
			}
			if(fc){
				continue;
			}
		}
	}

	@Override
	public void update(Observable obj, Object arg) {
		// TODO Auto-generated method stub
		// 接收来自Engin的消息
		
	}

	private void pretreat(){
		
	}
}
