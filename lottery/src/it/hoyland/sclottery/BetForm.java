package it.hoyland.sclottery;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class BetForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField[] texts;
	
	private Command cmdBack;
	private Command cmdPlaceBet;
	private Command cmdExit;
	
	public BetForm(LotteryMIDlet midlet, String title) {
		super(title);
		this.midlet = midlet;
		
		this.texts = new TextField[26];
		for(int i=0; i<texts.length; i++){
			
		}
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}


}
