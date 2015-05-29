package ws.hoyland.captcha.UI.Component;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GraphicBitPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[][] bit01;

	public void setBit(int[][] in) {
		this.bit01 = in;

	}

	public void paint(Graphics gr) {
		super.paint(gr);
		if (this.bit01 == null)
			return;

		int h = bit01[0].length;// 获取图像的高
		int w = bit01.length;// 获取图像的宽
		int height = this.getHeight();
		int width = this.getWidth();
		width = width > w ? w : width;
		height = height > h ? h : height;
		gr.setColor(new Color((int) (Math.random() * 255),
				(int) (Math.random() * 255), (int) (Math.random() * 255)));
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				int t = bit01[i * w / width][j * h / height];

				Integer.toHexString(t);
				if (t == 1) {

					gr.fillRect(i, j, 1, 1);
				}
			}

		}
		gr.setColor(Color.white);

	}

}
