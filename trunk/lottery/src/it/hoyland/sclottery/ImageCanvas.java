package it.hoyland.sclottery;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class ImageCanvas extends Canvas {

	private Display display;
	private Image image;
	private String content;
	private Displayable displayable;
	private CommandListener listener;
	private ColorInfo info;

	private Font fntA;
	private Font fntB;

	public ImageCanvas(Display display) {
		this(display, null);
	}

	public ImageCanvas(Display display, ColorInfo info) {
		fntA = Font.getFont(0);
		fntB = fntA;

		if (display == null) {
			throw new IllegalArgumentException(
					"Display parameter cannot be null.");
		}
		this.display = display;

		if (info != null) {
			this.info = info;
		} else {
			this.info = DefaultColorInfo.getInstance(display);
		}
	}

	public final void setContent(String content) {
		this.content = content;
		repaint();
	}

	public final void setImage(Image image) {
		this.image = image;
		repaint();
	}

	protected final CommandListener getListener() {
		return this.listener;
	}

	public void setCommandListener(CommandListener listener) {
		super.setCommandListener(listener);
		this.listener = listener;
	}

	protected void paint(Graphics g1) {
		ImageCanvas ic = this;
		ColorInfo ci = ic.info;
		Graphics g2 = g1;
		int i = 1;

		if (g2 == null) {
			throw new IllegalArgumentException("Graphics parameter cannot be null");
		}

		int k = g2.getColor();
		int l = g2.getClipX();
		int i1 = g2.getClipY();
		int j1 = g2.getClipWidth();
		int k1 = g2.getClipHeight();
		g2.setColor(ci.getColor(0));
		g2.fillRect(l, i1, j1, k1);
		g2.setColor(k);
		g1.setColor(ci.getColor(1));
		g1.setFont(ic.fntB);

		i = g1.getClipWidth() / 2 + g1.getClipX();
		int j = g1.getClipHeight() / 2 + g1.getClipY();
		if (this.image != null) {
			g1.drawImage(this.image, i, j, 3);
		}
		if (this.content != null) {
			g1.drawString(this.content, i, j, 65);
		}

	}

	protected void sizeChanged(int i, int j) {
		repaint();
	}

	protected final Display getDisplay() {
		return this.display;
	}

	protected final void show() {
		if (this.displayable != null) {
			this.display.setCurrent(this.displayable);
		}
	}

	protected void showNotify() {
		ImageCanvas ic = this;
		this.displayable = ic.display.getCurrent(); // 缓存本Canvas显示之前的上一个画面？
		super.showNotify();
	}

}
