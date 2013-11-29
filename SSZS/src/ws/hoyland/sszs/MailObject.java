package ws.hoyland.sszs;

public class MailObject {
	
	private static MailObject instance;
	
	private MailObject(){
		
	}
	
	public static MailObject getInstance(){
		if(instance==null){
			instance = new MailObject();
		}
		return instance;
	}
}
