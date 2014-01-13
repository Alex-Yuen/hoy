package ws.hoyland.qqol;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Packet {
	private DatagramChannel dc;
	private ByteBuffer buffer;

	public Packet(DatagramChannel dc, ByteBuffer buffer) {
		this.dc = dc;
		this.buffer = buffer;
	}

	public DatagramChannel getDc() {
		return dc;
	}

	public void setDc(DatagramChannel dc) {
		this.dc = dc;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

}
