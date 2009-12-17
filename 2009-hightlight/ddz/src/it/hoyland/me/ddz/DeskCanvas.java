package it.hoyland.me.ddz;

import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import it.hoyland.me.core.HLCanvas;

public class DeskCanvas extends HLCanvas {
	
	private byte state;
	private boolean run;

	public DeskCanvas(MIDlet midlet) {
		super(midlet);
		setFullScreenMode(true);
		this.run = true;
		this.state = 0;
		
	}

	public void run() {
		while(run){
			switch(this.state){
				case 0:
					break;
				case 1:
					init();
					break;
				default:
					break;
			}
			
			try{
				Thread.sleep(100);
			}catch(Exception e){
				e.printStackTrace();
			}
			repaint();
		}
	}

	protected void paint(Graphics g) {
		// TODO Auto-generated method stub
		g.setColor(31, 176, 31);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	protected void keyPressed(int keyCode) {
		
	}
	
	private void init(){ // 开始新的牌局
		
	}
}
