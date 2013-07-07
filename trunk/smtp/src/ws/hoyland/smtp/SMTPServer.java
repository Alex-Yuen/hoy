package ws.hoyland.smtp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SMTPServer extends Thread {

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
