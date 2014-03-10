package ws.hoyland.ps;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;


public class HttpsServer implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			SSLServerSocket serverSocket = null;
	        boolean listening = true;
	
	        int port = 8443;	//default
	
	        try {
//	        	SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
//						.getDefault();
	        	
	        	String keyName = "keystore";  
	            char[] keyStorePwd = "123456".toCharArray();  
	            char[] keyPwd = "123456".toCharArray();  
	            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());  
	          
	            // 装载当前目录下的key store. 可用jdk中的keytool工具生成keystore  
	            InputStream in = null;  
	            keyStore.load(in = this.getClass().getClassLoader().getResourceAsStream(  
	                    keyName), keyStorePwd);
	            in.close();  
	          	            
	            // 初始化key manager factory  
	            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory  
	                    .getDefaultAlgorithm());  
	            kmf.init(keyStore, keyPwd);  
	          
	            // 初始化ssl context  
	            SSLContext context = SSLContext.getInstance("SSL");
	            context.init(kmf.getKeyManagers(),  
null,	                    //new TrustManager[] { new MyX509TrustManager() },  
	                    new SecureRandom());
	            
	            SSLServerSocketFactory sslserversocketfactory = context.getServerSocketFactory();  
	            
				serverSocket = (SSLServerSocket) sslserversocketfactory
						.createServerSocket(port);
				
	            System.out.println("Https Proxy Started on: " + port);
	        } catch (IOException e) {
	            System.err.println("Could not listen on port: 8443");
	            System.exit(-1);
	        }
	
	        while (listening) {
	        	Socket socket = serverSocket.accept();
	        	//System.out.println(socket.getClass().getName());
//	        	if(socket instanceof SSLSocket){
//	        		System.out.println("ssl socket");
//	        	}
	            new ProxyThread(socket).start();
	        }
	        serverSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
