package ws.hoyland.bcs;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class BCServer {

	/**
	 * @param args
	 * @throws UException
	 */
	public static void main(String[] args) throws Exception {
		//TODO
		//build a nio socket server, and buffer it, and then broadcast.
		int port = 6789;
		InetAddress inetAddress = InetAddress.getByName("228.5.6.7");
		DatagramPacket datagramPacket = null;
		MulticastSocket multicastSocket = new MulticastSocket();
		
		String start = "[HOYLAND.WS/START]";
		String end = "[HOYLAND.WS/END]";
		FileInputStream is = new FileInputStream("D:\\dilang.flv");
		byte[] buffer = new byte[1024];
		byte[] bf = null;
		int length = 0;
		byte[] startTag = start.getBytes();
		byte[] endTag = end.getBytes();
		
		while(true){
			datagramPacket = new DatagramPacket(startTag, startTag.length, inetAddress, port);
			multicastSocket.send(datagramPacket);
			
			while (((length=is.read(buffer, 0, buffer.length)) != -1) ) {
				bf = Arrays.copyOfRange(buffer, 0, length);
				datagramPacket = new DatagramPacket(bf, bf.length, inetAddress, port);
				multicastSocket.send(datagramPacket);
			}
			
			datagramPacket = new DatagramPacket(endTag, endTag.length, inetAddress, port);
			multicastSocket.send(datagramPacket);
			
			Thread.sleep(2000);
			is = new FileInputStream("D:\\dilang.flv");
		}

	}

}
