package it.hoyland.me.llk;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

public class WelcomeCanvas extends GameCanvas implements Runnable {

	private boolean move;
	private int radius;
	private int diameter;
	private int interval;

	protected WelcomeCanvas() {
		super(true);
		// TODO Auto-generated constructor stub
	}

	public void render(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		g.setColor(183, 251, 121);
		g.fillRect(0, 0, width - 1, height - 1);
		int x = diameter;
		int y = diameter;
		int w = width - diameter * 2;
		int h = height - diameter * 2;
		for (int i = 0; i < 17; i = i + 2) {
			g.setColor(((17 - i) * 15 - 7), 20, ((17 - i) * 15 - 7));
			g.fillArc(x, y, w, h, radius + i * 10, 10);
			g.fillArc(x, y, w, h, (radius + 180) % 360 + i * 10, 10);
		}
	}

	public void start() {
		move = true;
		Thread t = new Thread(this);
		t.start();
	}

	public void stop() {
		move = false;
	}

	public void run() {
		Graphics g = getGraphics();
		while (move) {
			radius = (radius + 1) % 360;
			render(g);
			flushGraphics();
			try {
				Thread.sleep(interval);
			} catch (InterruptedException ie) {
			}
		}
	}

}
