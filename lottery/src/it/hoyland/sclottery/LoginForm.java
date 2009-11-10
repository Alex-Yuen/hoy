package it.hoyland.sclottery;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;

public class LoginForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField imie;
	private TextField loginId;
	private TextField loginPass;
	private TextField address;

	private Command cmdLogin;
	private Command cmdExit;

	public LoginForm(LotteryMIDlet midlet, String title) {
		super(title);
		this.midlet = midlet;
		this.imie = new TextField("IMEI", "", 120, 0xc0000);
		this.loginId = new TextField(this.midlet.prop("L14"), "", 120, 0xc0000);
		this.loginPass = new TextField(this.midlet.prop("L10"), "", 50, 0xd0000);
		this.address = new TextField(this.midlet.prop("L15"), "", 120, 0xc0004);

		append(this.imie);
		append(this.loginId);
		append(this.loginPass);
		append(this.address);

		this.cmdLogin = new Command(this.midlet.prop("L16"), 1, 1);
		this.cmdExit = new Command(this.midlet.prop("L17"), 7, 1);

		addCommand(cmdExit);
		addCommand(cmdLogin);
		setCommandListener(this);

	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		// 登录窗口
		if (dsp == this) {
			if (cmd == cmdExit) {
				this.midlet.exit();
			} else if (cmd == cmdLogin) {
				DefaultImageCanvas dic = this.midlet.getDicOfLogin();
				dic.setCommandListener(this);
				this.midlet.getDisplay().setCurrent(dic);
			}
		}

		// 等待窗口
		if (dsp == this.midlet.getDicOfLogin()) {
			try {
				if (cmd == DefaultImageCanvas.cmdFailure && cmd != DefaultImageCanvas.cmdSuccess) { // 登录失败
					Image image = Image.createImage("/D88/alert.png");
					Alert alert = new Alert(this.midlet.prop("L18"), "", image, AlertType.WARNING);
					this.midlet.getDisplay().setCurrent(alert, this);
				} // 成功此处不做处理？
//				else{
//					this.midlet.getDisplay().setCurrent(new ReprintForm(this.midlet, "EF"));
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
