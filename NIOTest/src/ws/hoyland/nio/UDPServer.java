package ws.hoyland.nio;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
//import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 徐辛波(sinpo.xu@hotmail.com) Oct 19, 2008
 */
public class UDPServer extends Thread {
	public void run() {
		Selector selector = null;
		try {
			DatagramChannel channel = DatagramChannel.open();
			DatagramSocket socket = channel.socket();
			channel.configureBlocking(false);
			socket.bind(new InetSocketAddress(8023));

			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		while (true) {
			try {
				int eventsCount = selector.select();
				if (eventsCount > 0) {
					Set<?> selectedKeys = selector.selectedKeys();
					Iterator<?> iterator = selectedKeys.iterator();
					while (iterator.hasNext()) {
						SelectionKey sk = (SelectionKey) iterator.next();
						iterator.remove();
						if (sk.isReadable()) {
							DatagramChannel datagramChannel = (DatagramChannel) sk
									.channel();
							SocketAddress sa = datagramChannel
									.receive(byteBuffer);
							//datagramChannel.receive(byteBuffer);
							byteBuffer.flip();

							// 测试：通过将收到的ByteBuffer首先通过缺省的编码解码成CharBuffer 再输出
							CharBuffer charBuffer = Charset.defaultCharset()
									.decode(byteBuffer);
							System.err.println("Receive message:"
									+ charBuffer.toString());
							byteBuffer.clear();

							String echo = "This is the reply message from server: "+charBuffer.toString();
							ByteBuffer buffer = Charset.defaultCharset()
									.encode(echo);
							datagramChannel.send(buffer, sa);
							//datagramChannel.write(buffer);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new UDPServer().start();
	}
}