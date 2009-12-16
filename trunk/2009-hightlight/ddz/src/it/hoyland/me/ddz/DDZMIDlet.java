package it.hoyland.me.ddz;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class DDZMIDlet extends MIDlet {

	private Display display;
	private WelcomeCanvas canvas;
	
	public DDZMIDlet() {
		this.display = Display.getDisplay(this);
		canvas = new WelcomeCanvas();
		
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
		// do not need to process
	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		// Welcome Canvas
		// Then go to the Desk
		//new Thread(this.canvas).start();
		this.canvas.start();
		this.display.setCurrent(this.canvas);
	}

}
