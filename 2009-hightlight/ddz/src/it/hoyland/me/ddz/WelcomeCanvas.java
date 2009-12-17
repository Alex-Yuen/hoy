package it.hoyland.me.ddz;

import it.hoyland.me.core.HLCanvas;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

public class WelcomeCanvas extends HLCanvas {

	private int width, height;
	// 原始图片
	private Image srcImage;
	// 原始图片的像素数组
	private int[] srcImageRGB;
	// 渐变图片的像素数组
	private int[] shadowImageRGB;
	private int imgWidth, imgHeight;
	private Image image;

	protected WelcomeCanvas(MIDlet midlet) {
		super(midlet);
		setFullScreenMode(true);

		width = this.getWidth();
		height = this.getHeight();
		try {
			srcImage = Image.createImage("/logo-big.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
		imgWidth = srcImage.getWidth();
		imgHeight = srcImage.getHeight();

		// 制造原始图片的像素数组，用一个int来代表每一个像素,按位表示方式是:0xAARRGGBB
		srcImageRGB = new int[imgWidth * imgHeight];
		// 获取原始图片的所有像素，参见MIDP APPI文档
		srcImage.getRGB(srcImageRGB, 0, imgWidth, 0, 0, imgWidth, imgHeight);

		shadowImageRGB = new int[srcImageRGB.length];
		System.arraycopy(srcImageRGB, 0, shadowImageRGB, 0,
				shadowImageRGB.length);
		// 渐变图片的所有像素一开始都是全透明的
		for (int i = 0; i < shadowImageRGB.length; i++) {
			shadowImageRGB[i] &= 0x00ffffff;
		}
//		for (int i = 0; i < srcImageRGB.length; i++){// 全不透明
//			srcImageRGB[i] |= 0xff000000;
//		}
	}

	public void paint(Graphics g) {
		image = Image.createRGBImage(shadowImageRGB, imgWidth, imgHeight, true);

		//g.setColor(24, 92, 175);
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, width, height);
		g.drawImage(image, width / 2, height / 2, Graphics.VCENTER
				| Graphics.HCENTER);

	}

	public void run() {
		boolean changed = true;

		// 当所有像素的alpha值都达到原始值后,线程运行结束
		while (changed) {
			changed = false;
			// 改变渐变图片的每一个像素
			for (int i = 0; i < shadowImageRGB.length; i++) {
				// 获取渐变图片的某一像素的alpha值
				int alpha = (shadowImageRGB[i] & 0xff000000) >>> 24;
				// 原始图片的对应像素的alpha值
				int oldAlpha = (srcImageRGB[i] & 0xff000000) >>> 24;
//				if(oldAlpha==255&&i==1880){
//					info(i+":"+alpha);
//				}
				//info(oldAlpha+":"+alpha);
				if (alpha < oldAlpha) {
					// alpha值++
					if(alpha==0xfe){
						shadowImageRGB[i] = (shadowImageRGB[i] | 0xff000000);
					}else{
						shadowImageRGB[i] = ((alpha + 2) << 24)
								| (shadowImageRGB[i] & 0x00ffffff);
					}
					changed = true;
				}
			}
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
			repaint();
		}

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Object locker = ((DDZMIDlet) midlet).locker;
		synchronized (locker) {
			locker.notifyAll();
		}
	}

}
