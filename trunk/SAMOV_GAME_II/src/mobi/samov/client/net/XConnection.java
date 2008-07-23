package mobi.samov.client.net;

import mobi.samov.client.core.Observable;
import mobi.samov.client.core.Observer;

public class XConnection extends Observable implements Runnable {
	//Singleton Object
	//Use this to connect to internet:
	public static String CMWAP_SOCKET = "cmwap-s";
	public static String CMWAP_HTTP = "cmwap-h";
	public static String CMNET_SOCKET = "cmnet-s";
	public static String CMNET_HTTP = "cmnet-h";
	public static String BLUETOOTH = "bluetooth";
	
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
//		//心跳包是异步的， 手动可以是同步，也可以是异步
		//if (同步 并且 尚未返回） Thread.sleep
	}
}
