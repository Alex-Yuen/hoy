package it.hoyland.me.ddz;

import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import it.hoyland.me.core.HLCanvas;

public class MainCanvas extends HLCanvas {

	public MainCanvas(MIDlet midlet) {
		super(midlet);
		// TODO Auto-generated constructor stub
	}

	public void paint(Graphics g) {
		g.setColor(56, 100, 100);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	public void run() {
		// TODO Auto-generated method stub
		repaint();
	}

}
