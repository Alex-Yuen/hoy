package ws.hoyland.captcha.UI.painter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

///
public class ImagePainter {
	private Graphics gr;
	int offset = 0;
	int topoffset = 0;
	int temptopoffset = 0;

	public ImagePainter(Graphics gr) {

		this.gr = gr;

	}

	private void setOffset(int w, int h) {

		this.offset = this.offset + w;
		this.temptopoffset = this.temptopoffset < h ? h : this.temptopoffset;
		if (this.offset > 800) {
			this.offset = 100;
			this.topoffset += this.temptopoffset;
			this.temptopoffset = 0;
		}
		if (this.topoffset > 800)

			this.topoffset = 0;

	}

	public void paint(BufferedImage bi, int width, int height) {
		int h = bi.getHeight();// 获取图像的高
		int w = bi.getWidth();// 获取图像的宽

		for (int i = 0; i < 400; i++) {

			for (int j = 0; j < 300; j++) {

				int t = bi.getRGB(j * w / width, i * h / width);

				Integer.toHexString(t);

				gr.setColor(new Color(t));

				gr.fillRect(j + this.offset, i + this.topoffset, 1, 1);

			}

		}

		this.offset = this.offset + 400;
	}

	public void paint(BufferedImage bi) {
		int h = bi.getHeight();// 获取图像的高
		int w = bi.getWidth();// 获取图像的宽
		int height = 400;
		int width = 300;
		width = width > w ? w : width;
		height = height > h ? h : height;
		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {

				int t = bi.getRGB(j * w / width, i * h / height);

				Integer.toHexString(t);

				gr.setColor(new Color(t));

				gr.fillRect(j + this.offset, i + this.topoffset, 1, 1);

			}

		}
		gr.setColor(Color.white);

		this.setOffset(width, height);

	}
	
	public void paint(double t) {
		gr.setColor(Color.black);
		gr.drawString(""+t, this.offset, this.topoffset);
		
		this.setOffset(40, 40);
		
		
		
	}

	public void paint(int[][] bit01) {

		int h = bit01[0].length;// 获取图像的高
		int w = bit01.length;// 获取图像的宽
		int height = 400;
		int width = 300;
		width = width > w ? w : width;
		height = height > h ? h : height;
		gr.setColor(new Color((int) (Math.random() * 255),
				(int) (Math.random() * 255), (int) (Math.random() * 255)));
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				int t = bit01[i * w / width][j * h / height];

				Integer.toHexString(t);
				if (t == 1) {

					gr.fillRect(i + this.offset, j + this.topoffset, 1, 1);
				}
			}

		}
		gr.setColor(Color.white);
		this.setOffset(width, height);
		// this.offset=this.offset+width;
	}

	public void paint(double[][] bit01) {

		int h = bit01[0].length;// 获取图像的高
		int w = bit01.length;// 获取图像的宽
		int height = 400;
		int width = 300;
		width = width > w ? w : width;
		height = height > h ? h : height;
		gr.setColor(new Color((int) (Math.random() * 255),
				(int) (Math.random() * 255), (int) (Math.random() * 255)));
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				double t = bit01[i * w / width][j * h / height];

				if (t > 0)

					gr.fillRect(i + this.offset, j + this.topoffset, 1, 1);

			}

		}
		gr.setColor(Color.WHITE);
		this.setOffset(width, height);
		// TODO Auto-generated method stub

	}

}
