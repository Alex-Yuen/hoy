package it.hoyland.sclottery;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class NumMeaningForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField number;
	private TextField words;
	
	private Command meaningNumber;
	private Command meaningWord;
	private Command meaningExit;
	private Command meaningBack;
	
	public NumMeaningForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.number = new TextField(this.midlet.prop("L56"), null, 6, 2);
		this.words = new TextField(this.midlet.prop("L64"), null, 120, 0);
		
		append(this.number);
		append(this.words);
		
		this.meaningNumber = new Command(this.midlet.prop("L56"), 1, 0);
		this.meaningWord = new Command(this.midlet.prop("L64"), 1, 1);
		this.meaningExit = new Command(this.midlet.prop("L17"), 7, 1);
		this.meaningBack = new Command(this.midlet.prop("L24"), 2, 1);
		
		addCommand(this.meaningNumber);
		addCommand(this.meaningWord);
		addCommand(this.meaningExit);
		addCommand(this.meaningBack);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}


}
