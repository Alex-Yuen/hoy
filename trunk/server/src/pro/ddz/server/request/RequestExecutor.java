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
	
				this.currentRequest = null;

				//合法性验证, 执行的时候，先执行if(Request.isExecutable()){
				System.out.println(currentRequest);
			}
		}
	}
}
