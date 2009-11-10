package it.hoyland.sclottery;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class PasswordForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField oldPass;
	private TextField newPass;
	
	private Command cmdPassChange;
	private Command cmdPassBack;
	private Command cmdPassExit;
		
	public PasswordForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.oldPass = new TextField(this.midlet.prop("L65"), null, 25, 0xc0000);
		this.newPass = new TextField(this.midlet.prop("L66"), null, 25, 0xc0000);
		
		append(this.oldPass);
		append(this.newPass);
		
		this.cmdPassChange = new Command(this.midlet.prop("L67"), 1, 0);
		this.cmdPassBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdPassExit = new Command(this.midlet.prop("L17"), 7, 1);
		
		addCommand(this.cmdPassChange);
		addCommand(this.cmdPassBack);
		addCommand(this.cmdPassExit);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}

}
