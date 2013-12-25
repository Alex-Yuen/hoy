package ws.hoyland.qqol.sync;

public class PauseCountObject {
	
	private static PauseCountObject instance;
	
	private PauseCountObject(){
		
	}
	
	public static PauseCountObject getInstance(){
		if(instance==null){
			instance = new PauseCountObject();
		}
		return instance;
	}
}
