package mobi.samov.client;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import mobi.samov.client.core.Observable;
import mobi.samov.client.core.Observer;
import mobi.samov.client.core.XGame;
import mobi.samov.client.game.Platform;

public class XMIDlet extends MIDlet implements Observer, CommandListener{
	private XGame currentGame;
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		Platform platform = new Platform(this);
		this.currentGame = platform;
		Thread t = new Thread(platform);
		t.start();
		Display.getDisplay(this).setCurrent(platform);
	}

	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		this.currentGame.update(o, arg);
	}

	public void commandAction(Command arg0, Displayable arg1) {
		// TODO Auto-generated method stub
		
	}
}
