package pro.ddz.server.core;

import java.util.Queue;

public class RequestExecutor implements Runnable {
	private Request currentRequest;
	private Queue<Request> requestQueue;
	
	public RequestExecutor(Queue<Request> requestQueue){
		this.requestQueue = requestQueue;
	}

	@Override
	public void run() {
		while(true){
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			synchronized(requestQueue){
				//System.out.println("HERE");
				//lookup the RequestQueue, and deal with them.
				//System.out.println("size:"+requestQueue.size());
				this.currentRequest = requestQueue.poll();
				//System.out.println(this.currentRequest);
				//合法性验证
				if(this.currentRequest!=null&&this.currentRequest.isExecutable()){
					System.out.println("[EXCUTE]");
					this.currentRequest.execute();
				}
			}
		}
	}
}
