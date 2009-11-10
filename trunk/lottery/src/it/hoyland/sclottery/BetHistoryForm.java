package it.hoyland.sclottery;

import java.util.Date;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class BetHistoryForm extends Form implements CommandListener {
	
	private LotteryMIDlet midlet;
	private TextField historyName;
	private TextField historyNum;
	private DateField draw;

	private Command historyNumber;
	private Command historyDate;
	private Command historyBack;
	private Command historyExit;
	
	public BetHistoryForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		this.historyName = new TextField(this.midlet.prop("L55"), null, 25, 0xc0000);
		this.historyNum = new TextField(this.midlet.prop("L56"), null, 6, 2);
		this.draw = new DateField(this.midlet.prop("L57"), DateField.DATE);
		this.draw.setDate(new Date());
		
		append(this.historyName);
		append(this.historyNum);
		append(this.draw);
		
		this.historyNumber = new Command(this.midlet.prop("L56"), 1, 0);
		this.historyDate = new Command(this.midlet.prop("L57"), 1, 1);
		this.historyBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.historyExit = new Command(this.midlet.prop("L17"), 7, 1);
		
		addCommand(this.historyNumber);
		addCommand(this.historyDate);
		addCommand(this.historyBack);
		addCommand(this.historyExit);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}

}
