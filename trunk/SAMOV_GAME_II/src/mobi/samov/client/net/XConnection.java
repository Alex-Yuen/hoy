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
	 * �ݲ������̳߳ؼ���
	 * �������ӣ��������ݣ���ȡ����
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
	 * socket/bluetooth �ǳ�����
	 * http �Ƕ����ӣ� ������Ҫ��ӳ��run������
	 * ��������̳߳أ� ��socket/bluetooth�������ã� http�أ�
	 * ȫ��Ӧ�ô��̳߳ػ�ȡ��
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
