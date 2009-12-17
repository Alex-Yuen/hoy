package it.hoyland.me.ddz;

import javax.microedition.lcdui.Font;
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
		
		g.setColor(100, 50, 50);
		
		g.fillRoundRect(10, 20, 70, 26, 10, 10);
        g.setColor(0x00FF0000);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.FACE_SYSTEM,
                Font.SIZE_LARGE));
        g.drawString("¿ªÊ¼ÓÎÏ·", 45, 45, Graphics.HCENTER|Graphics.BOTTOM);
	}
	
	public void run() {
		// TODO Auto-generated method stub
		repaint();
	}

}
