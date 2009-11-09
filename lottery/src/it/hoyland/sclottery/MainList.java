package it.hoyland.sclottery;

import it.hoyland.sclottery.util.Properties;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

public class MainList extends List implements CommandListener {

	private Command cmdExit;
	private LotteryMIDlet midlet;
	
	public MainList(LotteryMIDlet lotteryMIDlet, String title, int listType) {
		super(title, listType);
		this.midlet = lotteryMIDlet;
		for(int i=0; i<12; i++){
			this.append(this.midlet.prop("L"+String.valueOf(i+1)), null);
		}
		setSelectedFlags(new boolean[] {false, false, false, false, false, false
				, false, false, false, false, false, false});
		setFitPolicy(1);
		
		this.cmdExit = new Command(this.midlet.prop("L17"), 7, 1);
		addCommand(cmdExit);
		setCommandListener(this);
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		if(cmd==List.SELECT_COMMAND){
			switch(getSelectedIndex()){
				case 0:
					this.midlet.setProp(new Properties());
					break;
				case 1:
					this.midlet.setProp(new Properties("zh_CN"));
					break;
				default:
					break;
			}
		}
		else if(cmd==cmdExit){
			this.midlet.exit();
		}
		//Display.getDisplay(this.midlet).setCurrent(new LoginForm(this.midlet));
	}

	public LotteryMIDlet getMidlet() {
		return midlet;
	}
	
}
