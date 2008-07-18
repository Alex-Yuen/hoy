package mobi.samov.client.core;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import mobi.samov.client.XMIDlet;

public abstract class XGame extends Canvas implements Runnable {
	private XMIDlet midlet;
	
	public XGame(XMIDlet midlet){
		this.midlet = midlet;
	}
	
	protected abstract void paint(Graphics arg0);	
	public abstract void run();
	public abstract void update(Observable o, Object arg);
}
