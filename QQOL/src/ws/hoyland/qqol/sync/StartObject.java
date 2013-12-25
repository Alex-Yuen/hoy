package ws.hoyland.qqol.sync;

public class StartObject {
	
	private static StartObject instance;
	
	private StartObject(){
		
	}
	
	public static StartObject getInstance(){
		if(instance==null){
			instance = new StartObject();
		}
		return instance;
	}
}
