package mobi.samov.client.net;

public class NetworkService implements Runnable{
	//Singleton Object
	//Use this to connect to internet:
	/**
	 * 1. CMWAP-SOCKET
	 * 2. CMWAP-HTTP
	 * 3. CMNET-SOCKET
	 * 4. CMNET-HTTP
	 * 5. BLUETOOTH-SOCKETSERVER
	 * 6. BLUETOOTH-SOCKETCLIENT
	 */
	
	/**
	 * above use ssl connetion.
	 * 暂不考虑线程池技术
	 * 建立连接，发送数据，获取数据
	 */
	private NetworkService(){
		
	}
	
	/**
	 * cmwap://
	 * cmnet://
	 * bluetooth://
	 * @param URL
	 * @return
	 */
	public NetworkService createService(String URL){
		return null;
	}

	public void run() {
		// TODO Auto-generated method stub
		while(true){
//			if(flag=true){
//				send....;
//			}
		}
		
	}
	
	public void send(String content){
		
	}
}
