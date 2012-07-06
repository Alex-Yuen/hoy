package ws.hoyland.bcc;

import java.net.*;
import java.util.Arrays;

/**
 * read the flv file and send it to the server.
 * this demo the capture client 
 * @author hoy
 *
 */
public class BCClient {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		InetAddress group = InetAddress.getByName("228.5.6.7");
		MulticastSocket s = new MulticastSocket(6789);
		byte[] arb = new byte[1024];
		s.joinGroup(group);// 加入该组
		while (true) {
			DatagramPacket datagramPacket = new DatagramPacket(arb, arb.length);
			s.receive(datagramPacket);
			//System.out.println(datagramPacket.getLength());
			arb = Arrays.copyOfRange(arb, 0, datagramPacket.getLength());
			System.out.println(new String(arb));
		}

	}

}
