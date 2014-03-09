package ws.hoyland.ps;

import java.io.*;

public class ProxyServer {
	public static String CODE = "00";
	
    public static void main(String[] args) throws IOException {
    	new Thread(new HttpServer()).start();
    	new Thread(new HttpsServer()).start();
    }
}
