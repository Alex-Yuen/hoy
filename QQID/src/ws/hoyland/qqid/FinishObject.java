package ws.hoyland.qqid;

public class FinishObject {
	
	private static FinishObject instance;
	
	private FinishObject(){
		
	}
	
	public static FinishObject getInstance(){
		if(instance==null){
			instance = new FinishObject();
		}
		return instance;
	}
}
