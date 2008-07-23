package mobi.samov.client.net;

import mobi.samov.client.core.Observable;
import mobi.samov.client.core.Observer;

public class XConnection extends Observable implements Runnable {
	//Singleton Object
	//Use this to connect to internet:
	/**
	 * 1. CMWAP-SOCKET
	 * 2. CMWAP-HTTP
	 * 3. CMNET-SOCKET
	 * 4. CMNET-HTTP
	 * 5. BLUETOOTH
	 */
	
	/**
	 * above use ssl connetion.
	 * 暂不考虑线程池技术
	 * 建立连接，发送数据，获取数据
	 */
	private XConnection(String URL){
		
	}
	
	/**
	 * cmwap-s://
	 * cmnet-h://
	 * bluetooth://
	 * @param URL
	 * @return
	 */
	public static XConnection getConnection(Observer observer, String URL){
		XConnection connection = new XConnection(URL);
		connection.addObserver(observer);
		return connection;
	}

	/**
	 * socket/bluetooth 是长连接
	 * http 是短连接， 具体需要反映到run方法来
	 * 如果建立线程池， 则socket/bluetooth可以重用， http呢？
	 * 全部应该从线程池获取。
	 */
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
