package it.hoyland.sclottery;

import it.hoyland.sclottery.util.CalendarUtil;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class ResultForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField resultDate;
	private TextField resultDraw;
	
	private Command cmdResultBack;
	private Command cmdResultExit;
	private Command cmdResultShow;
	
	public ResultForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.resultDate = new TextField(this.midlet.prop("L54"), CalendarUtil.getMonthAndDate(), 9, 2);
		this.resultDraw = new TextField(this.midlet.prop("L71"), "1234", 15, 2);
		
		this.append(this.resultDate);
		this.append(this.resultDraw);
		
		this.cmdResultBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdResultExit = new Command(this.midlet.prop("L17"), 7, 1);
		this.cmdResultShow = new Command(this.midlet.prop("L7"), 1, 0);
		
		addCommand(this.cmdResultBack);
		addCommand(this.cmdResultExit);
		addCommand(this.cmdResultShow);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}


}
