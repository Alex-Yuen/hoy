package ws.hoyland.sszs;

public class PauseObject {
	
	private static PauseObject instance;
	
	private PauseObject(){
		
	}
	
	public static PauseObject getInstance(){
		if(instance==null){
			instance = new PauseObject();
		}
		return instance;
	}
}
