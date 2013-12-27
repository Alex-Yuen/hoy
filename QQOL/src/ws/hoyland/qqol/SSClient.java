package ws.hoyland.qqol;

import java.net.DatagramSocket;

public class SSClient {
	private int id;
	private String account;
	private byte[] sessionKey;
	private String ip;
	private DatagramSocket ds;

	public SSClient(int id, String account, DatagramSocket ds, String ip,
			byte[] sessionKey) {
		this.id = id;
		this.account = account;
		this.ds = ds;
		this.ip = ip;
		this.sessionKey = sessionKey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public byte[] getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(byte[] sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public DatagramSocket getDs() {
		return ds;
	}

	public void setDs(DatagramSocket ds) {
		this.ds = ds;
	}

}
