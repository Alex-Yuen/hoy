package ws.hoyland.ps;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer implements Runnable {

	@Override
	public void run() { 
		try{
	        ServerSocket serverSocket = null;
	        boolean listening = true;
	
	        int port = 8023;	//default
	
	        try {
	            serverSocket = new ServerSocket(port);
	            System.out.println("Http Proxy Started on: " + port);
	        } catch (IOException e) {
	            System.err.println("Could not listen on port: 8023");
	            System.exit(-1);
	        }
	
	        while (listening) {
	        	Socket socket = serverSocket.accept();
	        	//System.out.println(socket.getClass().getName());
	            new ProxyThread(socket).start();
	        }
	        serverSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
