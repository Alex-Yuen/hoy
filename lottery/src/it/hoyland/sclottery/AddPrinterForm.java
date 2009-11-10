package it.hoyland.sclottery;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

public class AddPrinterForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private StringItem progress;
	private ChoiceGroup cgBTDiscover;
	
	private Command cmdBTBack;
	private Command cmdBTExit;
	private Command cmdBTSearch;
	private Command cmdBTSave;
	
	public AddPrinterForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.progress = new StringItem(this.midlet.prop("L21"), this.midlet.prop("L22"));
		this.cgBTDiscover = new ChoiceGroup(this.midlet.prop("L23"), 1, new String[0], new Image[0]);
		this.cgBTDiscover.setSelectedFlags(new boolean[0]);
		this.cgBTDiscover.setFitPolicy(1);
		
		append(this.progress);
		append(this.cgBTDiscover);
		
		this.cmdBTBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdBTExit = new Command(this.midlet.prop("L17"), 7, 1);
		this.cmdBTSearch = new Command(this.midlet.prop("L25"), 1, 0);		
		this.cmdBTSave = new Command(this.midlet.prop("L26"), 1, 1);
		
		addCommand(this.cmdBTBack);
		addCommand(this.cmdBTExit);
		addCommand(this.cmdBTSearch);
		addCommand(this.cmdBTSave);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {

		
	}


}
