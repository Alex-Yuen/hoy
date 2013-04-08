package ws.hoyland.qt;

public class Task implements Runnable {

	private String uin;
	private String password;
	
	public Task(String uin, String password){
		this.uin = uin;
		this.password = password;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("OK");
	}

}
