package ws.hoyland.util.ooyear;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//saasfun.gl
		String[] cs = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();    
		ThreadPoolExecutor executor = new ThreadPoolExecutor(100, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, queue);    
		try{
			int c = 0;
			for(int m=0;m<cs.length;m++){
			for(int i=0;i<cs.length;i++){
				for(int j=0;j<cs.length;j++){
					for(int k=0;k<cs.length;k++){
						if(c%50==0){
							Thread.sleep(1000*10);
						}
						while(queue.size()>=200){
							Thread.sleep(5000);
						}
						Thread.sleep(100);
						executor.execute(new Poster(cs[m]+cs[i]+cs[j]+cs[k]));
						c++;
					}
				}
			}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			executor.shutdown();
		}
	}
}
