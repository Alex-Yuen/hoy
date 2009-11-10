package it.hoyland.sclottery;

import it.hoyland.sclottery.util.CalendarUtil;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
public class WinningForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField winDate;
	
	private Command cmdWinBack;
	private Command cmdWinExit;
	private Command cmdWinShow;
	
	public WinningForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.winDate = new TextField(this.midlet.prop("L57"), CalendarUtil.getMonthAndDate(), 9, 0xc0002);
		append(this.winDate);
		
		this.cmdWinBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdWinExit = new Command(this.midlet.prop("L17"), 7, 1);
		this.cmdWinShow = new Command(this.midlet.prop("L72"), 1, 0);
		
		addCommand(this.cmdWinBack);
		addCommand(this.cmdWinExit);
		addCommand(this.cmdWinShow);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}


}
