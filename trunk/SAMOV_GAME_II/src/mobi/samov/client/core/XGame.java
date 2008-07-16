package mobi.samov.client.core;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public abstract class XGame extends Canvas implements Runnable {

	protected abstract void paint(Graphics arg0);
	public abstract void run();
}
