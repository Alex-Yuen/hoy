package mobi.samov.client.core;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import mobi.samov.client.XMIDlet;

public abstract class XGame extends Canvas implements Runnable {
	private XMIDlet midlet;
	
	public XGame(XMIDlet midlet){
		this.midlet = midlet;
		Thread t = new Thread(this);
		t.start();
	}

	protected abstract void paint(Graphics arg0);	
	public abstract void run();
	public abstract void update(Observable o, Object arg);
	public abstract void commandAction(Command arg0, Displayable arg1);
	
	public void turnTo(XGame xGame){
		this.midlet.turnTo(xGame);
	}
}
