package mobi.samov.client.net;

import mobi.samov.client.core.Observable;
import mobi.samov.client.core.Observer;

/**
 * ����bluetooth��lan��socket server
 * @author abc
 *
 */
public class XServer extends Observable implements Runnable {
	public XServer(Observer observer, String IP, String port){
		this.addObserver(observer);
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
