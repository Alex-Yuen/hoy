package it.hoyland.me.core;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public abstract class HLCanvas extends Canvas implements Runnable {
	protected final MIDlet midlet;
	
	public HLCanvas(MIDlet midlet) {
		super();
		this.midlet = midlet;
	}

	public void show() {
		Display display = Display.getDisplay(midlet);
		new Thread(this).start();
		display.setCurrent(this);
	}

	public void info(String info){
		System.out.println(info);
	}
}
