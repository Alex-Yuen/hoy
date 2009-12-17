package it.hoyland.me.ddz;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class DDZMIDlet extends MIDlet {

	protected Display display;
	private WelcomeCanvas canvas;
	private MainCanvas mainCanvas;
	protected DeskCanvas deskCanvas;
	public Object locker;

	public DDZMIDlet() {
		this.display = Display.getDisplay(this);
		this.locker = new Object();
		this.canvas = new WelcomeCanvas(this);

	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
		// do not need to process
	}

	protected void startApp() throws MIDletStateChangeException {
		// init
		this.canvas.show();
		
		try {
			synchronized (locker) {
				locker.wait();
			}
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.mainCanvas = new MainCanvas(this);
		this.mainCanvas.show();
		this.canvas = null;
		System.gc();
		System.out.println("FINISH!");

	}

}
