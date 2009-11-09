package it.hoyland.sclottery;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

public class MessageForm extends Form implements CommandListener {
	
	private LotteryMIDlet midlet;
	private TextField sms;
	private StringItem item;
	
	private Command cmdBack;
	private Command cmdPrint;
	private Command cmdSMS;

	public MessageForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		this.sms = new TextField(this.midlet.prop("L58"), null, 30, 3);
		this.item = new StringItem("", this.midlet.prop("L59"));
		this.item.setFont(Font.getFont(0, 0, 16));
		
		this.append(this.sms);
		this.append(this.item);
		
		this.cmdBack =  new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdPrint =  new Command(this.midlet.prop("L60"), 2, 1);
		this.cmdSMS =  new Command(this.midlet.prop("L61"), 2, 1);
		
		addCommand(cmdBack);
		addCommand(cmdPrint);
		addCommand(cmdSMS);
		
		setCommandListener(this);
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}

}
