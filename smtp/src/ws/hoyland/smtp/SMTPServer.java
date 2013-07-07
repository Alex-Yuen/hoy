package ws.hoyland.smtp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SMTPServer extends Thread {

	public static void main(String[] args) {
		Thread t = new Thread(new SMTPServer());
		t.start();
	}

	public void run() {
		try {
			ServerSocket ss = new ServerSocket(25);
			while (true) {
				Socket s = ss.accept();
				new SMTPSession(s).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
