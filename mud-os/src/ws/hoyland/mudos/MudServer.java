package ws.hoyland.mudos;

import ws.hoyland.mudos.core.service.Service;

public class MudServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Service service = new Service();
		new Thread(service).start();
		System.out.println("Mud Server Started");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		service.stop();
	}
}
