package ws.hoyland.ps;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class HttpsServer implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			SSLServerSocket serverSocket = null;
	        boolean listening = true;
	
	        int port = 8443;	//default
	
	        try {
	        	SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
						.getDefault();
				serverSocket = (SSLServerSocket) sslserversocketfactory
						.createServerSocket(port);
				
	            System.out.println("Https Proxy Started on: " + port);
	        } catch (IOException e) {
	            System.err.println("Could not listen on port: 8443");
	            System.exit(-1);
	        }
	
	        while (listening) {
	            new ProxyThread(serverSocket.accept()).start();
	        }
	        serverSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
