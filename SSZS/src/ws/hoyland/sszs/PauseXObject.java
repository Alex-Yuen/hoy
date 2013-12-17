package ws.hoyland.sszs;

public class PauseXObject {
	
	private static PauseXObject instance;
	
	private PauseXObject(){
		
	}
	
	public static PauseXObject getInstance(){
		if(instance==null){
			instance = new PauseXObject();
		}
		return instance;
	}
}
