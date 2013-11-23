package ws.hoyland.sszs;

public class ReconObject {
	
	private static ReconObject instance;
	
	private ReconObject(){
		
	}
	
	public static ReconObject getInstance(){
		if(instance==null){
			instance = new ReconObject();
		}
		return instance;
	}
}
