package it.hoyland.me.ddz;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import it.hoyland.me.core.HLCanvas;

public class MainCanvas extends HLCanvas {

	protected byte index = 0;
	private String startGame = "开始游戏";
	
	public MainCanvas(MIDlet midlet) {
		super(midlet);
		setFullScreenMode(true);
		// TODO Auto-generated constructor stub
	}

	public void paint(Graphics g) {
		g.setColor(192, 24, 24);
		g.fillRect(0, 0, getWidth(), getHeight());
//		
//		g.setColor(100, 50, 50);
//		
//		g.fillRoundRect(10, 20, 70, 26, 10, 10);
        g.setColor(0x1616C0);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.FACE_SYSTEM,
                Font.SIZE_LARGE));
        int len = g.getFont().stringWidth(startGame);
        g.drawString(startGame, (getWidth()-len)/2, getHeight()*2/3, Graphics.HCENTER|Graphics.BOTTOM);
	}
		
	protected void keyPressed(int keyCode) {
		// TODO Auto-generated method stub
		super.keyPressed(keyCode);
		System.out.println((char)keyCode);
		if((char)keyCode=='5'){
			((DDZMIDlet)midlet).goDesk(); //进入桌面
		}
		
	}

	public void run() {
		// TODO Auto-generated method stub
		while(true){
			repaint();
			try{
				Thread.sleep(100);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
