package it.hoyland.sclottery;

import it.hoyland.sclottery.util.Properties;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class LotteryMIDlet extends MIDlet {

	private boolean inited;
	private Properties prop;
	private char status;
	private char subStatus;
	private DefaultImageCanvas dicOfLogin;	// login
	private DefaultImageCanvas dicOfPlaceBet; // place bet
	private Display display;
	private LangList langList;
	private MainList mainList;
	private MessageForm messageForm;

	public LotteryMIDlet() {
		// TODO Auto-generated constructor stub
		this.inited = false;
		this.status = 'Z';
		this.subStatus = '\0';

	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		if (!this.inited) {
			try {
				this.display = Display.getDisplay(this);

				Image image = Image.createImage("/sandglass.png");
				TaskExecutor te = new TaskExecutor();
				te.setTask(new LoginTask(this));

				this.dicOfLogin = new DefaultImageCanvas(this.display);
				this.dicOfLogin.setTitle(prop("L30"));
				this.dicOfLogin.setContent(prop("L53"));
				this.dicOfLogin.setImage(image);
				this.dicOfLogin.setExecutor(te);

				te = new TaskExecutor();
				te.setTask(new PlaceBetTask(this));
				this.dicOfPlaceBet = new DefaultImageCanvas(this.display);
				this.dicOfPlaceBet.setTitle(prop("L30"));
				this.dicOfPlaceBet.setContent(prop("L73"));
				this.dicOfPlaceBet.setImage(image);
				this.dicOfPlaceBet.setExecutor(te);
								
				this.mainList = new MainList(this, prop("L0"), List.IMPLICIT);
				this.messageForm = new MessageForm(this, "");
				
				this.langList = new LangList(this, "Y!", List.IMPLICIT);
				this.display.setCurrent(this.langList);
				this.inited = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setProp(Properties prop) {
		this.prop = prop;
	}

	public Display getDisplay() {
		return this.display;
	}

	public String prop(String key) {
		return (String) this.prop.get(key);
	}

	public void exit() {
		try {
			this.display.setCurrent(null);
			destroyApp(true);
			notifyDestroyed();
		} catch (MIDletStateChangeException e) {
			e.printStackTrace();
		}
	}

	public char getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	public char getSubStatus() {
		return this.subStatus;
	}

	public DefaultImageCanvas getDicOfLogin() {
		return this.dicOfLogin;
	}
	
	public DefaultImageCanvas getDicOfPlaceBet() {
		return this.dicOfPlaceBet;
	}
	
	public MainList getMainList(){
		return this.mainList;
	}
	
	public MessageForm getMessageForm(){
		return this.messageForm;
	}
}
