package ws.hoyland.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;


public class TCPSYNScan {

	static final String srcHost = "192.168.1.21";
	static final int srcPort = 5555;
	static final byte[] src_mac = new byte[]{(byte)0,(byte)0x27,(byte)0x19,(byte)0xa9,(byte)0xd4,(byte)0x51}; 	
	static final long sequence = 3981803911l;
	static final long ackNum = 0;
	
	static final byte[] gateway = new byte[]{(byte)0,(byte)0x23,(byte)0xcd,(byte)0xa6,(byte)0xf4,(byte)0x30};	
	static final byte[] restroom = new byte[]{(byte)0xe0,(byte)0xcb,(byte)0x4e,(byte)0xcc,(byte)0xe7,(byte)0x18};	
	
	static final byte[] dst_mac = restroom;
	
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		//System.out.println(System.getProperty("java.library.path"));
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		final JpcapCaptor captor = JpcapCaptor.openDevice(devices[0], 65535, false, 20);
		captor.setFilter("port " + srcPort, true);
		final JpcapSender sender = captor.getJpcapSenderInstance();
		
//		JpcapSender sender=JpcapSender.openDevice(devices[0]);
		
		new Thread()
		{
			public void run()
			{				
				captor.loopPacket(-1, new PacketPrinter()); 
				
			}
		}.start();		
		
		//sendSYN(sender, "192.168.1.3", 6667);
		//sendSYN(sender, "192.168.1.3", 6668);
		sendSYN(sender, "192.168.1.1", 80);
		//sendSYN(sender, "www.baidu.com", 80);
//		sendSYN(sender, "www.baidu.com", 81);
	}
	 
	public static void sendSYN(JpcapSender sender,String host, int port) throws IOException, InterruptedException
	{			
		sendPacket(sender, srcHost, srcPort, host, port, sequence, ackNum, false, true);			
	}
	
	private static void sendPacket(JpcapSender sender, String srcHost, int srcPort, String host, int port, long sequence, long acknum, boolean bAck, boolean bSyn) throws UnknownHostException
	{		
		TCPPacket tcp = new TCPPacket(srcPort, port, sequence, acknum, false, bAck, false, false, bSyn, false, false, false, 65535, 0);
		
		//TCPPacket tcp=new TCPPacket(12,34,56,78,false,false,false,false,true,true,true,true,10,10);
		tcp.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100,IPPacket.IPPROTO_TCP,
				InetAddress.getByName(srcHost), InetAddress.getByName(host));
//		tcp.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100,IPPacket.IPPROTO_TCP,
//				InetAddress.getByName("www.microsoft.com"),InetAddress.getByName("www.google.com"));
		//tcp.data = new byte[0];
		tcp.data=("dx").getBytes();
		//Set physical layer.
		EthernetPacket ether = new EthernetPacket();
		//set frame type as IP
		ether.frametype = EthernetPacket.ETHERTYPE_IP;
		//set source and destination MAC addresses
		ether.src_mac = src_mac;		
		ether.dst_mac = dst_mac;
		
		//set the datalink frame of the packet syn as ether
		tcp.datalink = ether;	
		
		System.out.println("sending syn connect: " + host + ":" + port);
		sender.sendPacket(tcp);
	}
	
	private static class PacketPrinter implements PacketReceiver {
		
		// this method is called every time Jpcap captures a packet
		public void receivePacket(Packet packet) {
			// just print out a captured packet
//			System.out.println(packet);
			
			TCPPacket tcp = (TCPPacket) packet;
			
			//tcp.
			System.out.println(tcp.src_ip.getHostAddress()+"->"+tcp.dst_ip.getHostAddress()+":"+tcp.rst);
			
			if (tcp.syn && tcp.ack)
			{
				System.out.println("**************" + tcp.src_ip + ":" + tcp.src_port + " PASS" + "**************");				
			}
			else if (tcp.rst && !tcp.src_ip.getHostAddress().equals(srcHost))
			{
//				System.out.println(tcp);
				System.out.println("**************" + tcp.src_ip + ":" + tcp.src_port + " FAIL" + "**************");	
			}
		}
	}
}