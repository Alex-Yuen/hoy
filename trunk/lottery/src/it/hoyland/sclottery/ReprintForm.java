package it.hoyland.sclottery;

import it.hoyland.sclottery.util.CalendarUtil;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class ReprintForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField date;
	private TextField page;
	
	private Command cmdReprintBack;
	private Command cmdReprint;
	private Command cmdReprintExit;
	
	public ReprintForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.date = new TextField(this.midlet.prop("L54"), CalendarUtil.getMonthAndDate(), 10, 2);
		this.page = new TextField(this.midlet.prop("L70"), null, 20, 3);
		
		this.append(this.date);
		this.append(this.page);
		
		this.cmdReprintBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdReprint = new Command(this.midlet.prop("L3"), 1, 0);
		this.cmdReprintExit = new Command(this.midlet.prop("L17"), 7, 2);
		
		addCommand(this.cmdReprintBack);
		addCommand(this.cmdReprint);
		addCommand(this.cmdReprintExit);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}


}
