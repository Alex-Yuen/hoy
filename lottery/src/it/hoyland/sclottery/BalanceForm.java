package it.hoyland.sclottery;

import it.hoyland.sclottery.util.CalendarUtil;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class BalanceForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField dateBalance;
	
	private Command cmdBalance;
	private Command cmdBalanceBack;
	private Command cmdBalanceExit;
	
	public BalanceForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.dateBalance = new TextField(this.midlet.prop("L54"), CalendarUtil.getMonthAndDate(), 100, 3);
		this.dateBalance.setInitialInputMode("");
		
		this.append(this.dateBalance);
		
		this.cmdBalance = new Command(this.midlet.prop("L4"), 1, 0);
		this.cmdBalanceBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdBalanceExit = new Command(this.midlet.prop("L17"), 7, 1);
		
		this.addCommand(this.cmdBalance);
		this.addCommand(this.cmdBalanceBack);
		this.addCommand(this.cmdBalanceExit);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}


}
