package it.hoyland.sclottery;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;

public class PaymentForm extends Form implements ItemCommandListener, CommandListener {

	private LotteryMIDlet midlet;
	
	private TextField txtPayAgent;
	private TextField payAmt;
	private ChoiceGroup cgPayAgent; 
	
	private Command cmdPayAgtClick;
	private Command cmdPayBack;
	private Command cmdPayExit;
	private Command cmdPay;
	private Command cmdPaySearch;
		
	public PaymentForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.txtPayAgent = new TextField(this.midlet.prop("L55"), null, 25, 0xc0000);
		this.payAmt = new TextField(this.midlet.prop("L68"), null, 20, 0xc0005);
		this.cgPayAgent = new ChoiceGroup(this.midlet.prop("L1"), 1, new String[0], new Image[0]);
		this.cmdPayAgtClick = new Command(this.midlet.prop("L63"), 1, 0);
		this.cgPayAgent.addCommand(this.cmdPayAgtClick);
		this.cgPayAgent.setItemCommandListener(this);
		this.cgPayAgent.setSelectedFlags(new boolean[0]);
		this.cgPayAgent.setFitPolicy(1);
		
		append(this.txtPayAgent);
		append(this.payAmt);
		append(this.cgPayAgent);
		
		this.cmdPayBack = new Command(this.midlet.prop("L24"), 2, 3);
		this.cmdPayExit = new Command(this.midlet.prop("L17"), 7, 4);
		this.cmdPay = new Command(this.midlet.prop("L69"), 1, 2);
		this.cmdPaySearch = new Command(this.midlet.prop("L25"), 1, 1);
		
		addCommand(this.cmdPayBack);
		addCommand(this.cmdPayExit);
		addCommand(this.cmdPay);
		addCommand(this.cmdPaySearch);
         
        setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Item item) {
		// TODO Auto-generated method stub
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}

}
