package pro.ddz.server.request;

public class RequestExecutor implements Runnable {
	private Request currentRequest;
	private RequestQueue requestQueue;
	
	public RequestExecutor(RequestQueue requestQueue){
		this.requestQueue = requestQueue;
	}

	@Override
	public void run() {
		while(true){
			synchronized(requestQueue){
				//lookup the RequestQueue, and deal with them.
				this.currentRequest = requestQueue.poll();
				//�Ϸ�����֤
				if(this.currentRequest!=null&&this.currentRequest.isExecutable()){
					this.currentRequest.execute();
				}
			}
		}
	}
}
