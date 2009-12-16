package it.hoyland.me.llk;

import it.hoyland.me.llk.WelcomeCanvas;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class LLKMIDlet extends MIDlet {
	
	private Display display;
	private WelcomeCanvas canvas;
	
	public LLKMIDlet() {
		this.display = Display.getDisplay(this);
		canvas = new WelcomeCanvas();
		
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		this.canvas.start();
		this.display.setCurrent(this.canvas);

	}

}
