package ws.hoyland.vc;

import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import ws.hoyland.util.Util;

//接收并播放（合成）来自服务器的声音
public class Receiver implements Runnable {

	private Selector selector;
	private boolean run = false;
	private ByteBuffer bf = ByteBuffer.allocate(1024 + 512);
	private boolean wakeup = false;
	private byte[] buffer = null;
	private int size = -1;

	public Receiver(Selector selector) {
		this.selector = selector;
		this.run = true;
	}

	public void wakeup() {
		this.wakeup = true;
	}

	public void sleep() {
		this.wakeup = false;
	}

	@Override
	public void run() {
		while (run) {
			try {
				int ec = 0;
				if (!wakeup) {
					ec = selector.select();
				} else {
					ec = selector.selectNow();
				}

				if (ec > 0) {
					Set<?> selectedKeys = selector.selectedKeys();
					Iterator<?> iterator = selectedKeys.iterator();
					while (iterator.hasNext()) {
						SelectionKey sk = (SelectionKey) iterator.next();
						iterator.remove();
						try {
							if (sk.isReadable()) {
								DatagramChannel datagramChannel = (DatagramChannel) sk
										.channel();
								// bf = ByteBuffer.allocate(1024);
								try {// ClosedChannelException by 0017
									size = datagramChannel.read(bf);
								} catch (Exception e) {
									e.printStackTrace();
									continue;
								}
								bf.flip();

								buffer = Util.slice(bf.array(), 0, size);
								// System.out.println("RECV:"+buffer.length);
								// System.out.println(Converts.bytesToHexString(buffer));

								bf.clear();

								// buffer;
								System.out.println("RECV:"+buffer.length);
							}
						} catch (CancelledKeyException e) {
							sk.cancel();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
