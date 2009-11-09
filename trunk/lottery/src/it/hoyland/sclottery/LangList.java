package it.hoyland.sclottery;

import it.hoyland.sclottery.util.Properties;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

public class LangList extends List implements CommandListener {

	private LotteryMIDlet midlet;
	
	public LangList(LotteryMIDlet lotteryMIDlet, String title, int listType) {
		super(title, listType);
		this.midlet = lotteryMIDlet;
		this.append("English", null);
		this.append("中文", null);
		setCommandListener(this);
		setSelectedFlags(new boolean[] {false, false});
	}

	public void commandAction(Command cmd, Displayable arg1) {
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
		this.midlet.init();
		Display.getDisplay(this.midlet).setCurrent(this.midlet.getLoginForm());
	}

	public LotteryMIDlet getMidlet() {
		return midlet;
	}
	
}
