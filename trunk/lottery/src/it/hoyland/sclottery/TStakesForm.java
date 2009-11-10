package it.hoyland.sclottery;

import java.util.Date;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

public class TStakesForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private DateField dtTStake;
	
	private Command cmdTSBack;
	private Command cmdTSExit;
	private Command cmdTSShow;
	
	public TStakesForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.dtTStake = new DateField(this.midlet.prop("L57"), 1);
		this.dtTStake.setDate(new Date());
		append(this.dtTStake);
		
		this.cmdTSBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdTSExit = new Command(this.midlet.prop("L17"), 7, 1);
		this.cmdTSShow = new Command(this.midlet.prop("L25"), 1, 0);
		
		addCommand(this.cmdTSBack);
		addCommand(this.cmdTSExit);
		addCommand(this.cmdTSShow);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		
		
	}


}
