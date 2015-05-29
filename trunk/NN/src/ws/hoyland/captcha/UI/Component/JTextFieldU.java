package ws.hoyland.captcha.UI.Component;

import javax.swing.JTextField;

public class JTextFieldU extends JTextField {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7850720684434769923L;
	private String uuid;
	
	public void setuuid(String uuid){
		
		
		this.uuid=uuid;
	}
	public String getUUid(){
		
		
		return this.uuid;
	}
	
	public JTextFieldU(String s){
		
		super(s);
	}
	
	
	
}
