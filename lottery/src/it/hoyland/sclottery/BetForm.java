package it.hoyland.sclottery;

import it.hoyland.sclottery.util.CalendarUtil;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class BetForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField[] texts;

	private Command cmdBack;
	private Command cmdPlaceBet;
	private Command cmdExit;

	public BetForm(LotteryMIDlet midlet, String title) {
		super(title);
		this.midlet = midlet;

		this.texts = new TextField[26];
		String t = "";
		for (int i = 0; i < texts.length; i++) {
			switch (i) {
			case 0:
				t = "";
				switch (this.midlet.getBint()) {
				case 4: // '\004'
					t = "##";
					break;

				case 5: // '\005'
					t = "#" + CalendarUtil.getDate();
					break;
				}

				this.texts[i] = new TextField("", t, 80, 0xc0000);
				switch (this.midlet.getBint()) {
				case 5: // '\005'
					this.texts[i].setConstraints(3);
					break;
				}
				break;
			case 1:
				t = "";
				switch (this.midlet.getBint()) {
				case 4: // '\004'
					t = "#" + CalendarUtil.getDate();
					break;

				case 5: // '\005'
					t = "+";
					break;
				}
				this.texts[i] = new TextField("", t, 80, 0xc0003);
				switch (this.midlet.getBint()) {
				case 4: // '\005'
					this.texts[i].setConstraints(3);
					break;
				}
				break;
			case 2:
				t = "";
				switch (this.midlet.getBint()) {
				case 4: // '\004'
					t = "+";
					break;
				}
				this.texts[i] = new TextField("", t, 80, 0xc0003);
				switch (this.midlet.getBint()) {
				case 5: // '\005'
					this.texts[i].setConstraints(3);
					break;
				}
				break;

			case 3:
			case 4:
				this.texts[i] = new TextField("", null, 80, 0xc0003);
				this.texts[i].setInitialInputMode("");
				break;
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
				this.texts[i] = new TextField("", null, 80, 0xc0003);
				break;
			default:
				break;
			}
		}

		this.cmdBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdPlaceBet = new Command(this.midlet.prop("L2"), 1, 0);
		this.cmdExit = new Command(this.midlet.prop("L17"), 7, 1);

		addCommand(this.cmdBack);
		addCommand(this.cmdPlaceBet);
		addCommand(this.cmdExit);

		setCommandListener(this);
	}

	public void commandAction(Command cmd, Displayable dsp) {
		if (cmd == this.cmdBack) {
			this.midlet.getDisplay().setCurrent(this.midlet.getMainList());
		} else if (cmd == this.cmdPlaceBet) {
			switch (this.midlet.getBint()) { // ?????
			default:
				break;
			case 4: // '\004'
				if (this.texts[0].getString().trim().length() != 0
						&& this.texts[1].getString().trim().length() != 0
						&& this.texts[2].getString().trim().length() != 0
						&& this.texts[3].getString().trim().length() != 0
						&& this.texts[0].getString().trim().startsWith("##")) {
					if (this.texts[0].getString().trim().substring(2).trim()
							.length() == 0) {
						break;
					}
				}
				break;
			case 5: // '\005'
				if (this.texts[0].getString().trim().length() == 0
						|| this.texts[1].getString().trim().length() == 0
						|| this.texts[2].getString().trim().length() == 0
						|| this.texts[0].getString().trim().startsWith("##")
						|| !this.texts[0].getString().trim().startsWith("#")
						|| this.texts[0].getString().trim().substring(1).trim()
								.length() == 0) {
					break;
				}
				break;
			}
			this.midlet.getDisplay().setCurrent(this.midlet.getDicOfPlaceBet());
		} else if (cmd == this.cmdExit) {
			this.midlet.exit();
		}
	}

}
