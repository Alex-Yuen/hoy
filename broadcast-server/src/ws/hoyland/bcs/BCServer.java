package ws.hoyland.bcs;

import java.net.*;

public class BCServer {

	/**
	 * @param args
	 * @throws UException
	 */
	public static void main(String[] args) throws Exception {
		int port = 6789;
		String sendMessage = "BCA";
		InetAddress inetAddress = InetAddress.getByName("228.5.6.7");
		DatagramPacket datagramPacket = new DatagramPacket(
				sendMessage.getBytes(), sendMessage.length(), inetAddress, port);
		MulticastSocket multicastSocket = new MulticastSocket();
		while(true){
			Thread.sleep(2000);
			multicastSocket.send(datagramPacket);
		}
	}

}
