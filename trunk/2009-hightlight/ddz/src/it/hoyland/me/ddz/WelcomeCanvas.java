package it.hoyland.me.ddz;

import it.hoyland.me.core.HLCanvas;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

public class WelcomeCanvas extends HLCanvas {

	private int width, height;
	// ԭʼͼƬ
	private Image srcImage;
	// ԭʼͼƬ����������
	private int[] srcImageRGB;
	// ����ͼƬ����������
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

		// ����ԭʼͼƬ���������飬��һ��int������ÿһ������,��λ��ʾ��ʽ��:0xAARRGGBB
		srcImageRGB = new int[imgWidth * imgHeight];
		// ��ȡԭʼͼƬ���������أ��μ�MIDP APPI�ĵ�
		srcImage.getRGB(srcImageRGB, 0, imgWidth, 0, 0, imgWidth, imgHeight);

		shadowImageRGB = new int[srcImageRGB.length];
		System.arraycopy(srcImageRGB, 0, shadowImageRGB, 0,
				shadowImageRGB.length);
		// ����ͼƬ����������һ��ʼ����ȫ͸����
		for (int i = 0; i < shadowImageRGB.length; i++) {
			shadowImageRGB[i] &= 0x00ffffff;
		}
//		for (int i = 0; i < srcImageRGB.length; i++){// ȫ��͸��
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

		// ���������ص�alphaֵ���ﵽԭʼֵ��,�߳����н���
		while (changed) {
			changed = false;
			// �ı佥��ͼƬ��ÿһ������
			for (int i = 0; i < shadowImageRGB.length; i++) {
				// ��ȡ����ͼƬ��ĳһ���ص�alphaֵ
				int alpha = (shadowImageRGB[i] & 0xff000000) >>> 24;
				// ԭʼͼƬ�Ķ�Ӧ���ص�alphaֵ
				int oldAlpha = (srcImageRGB[i] & 0xff000000) >>> 24;
//				if(oldAlpha==255&&i==1880){
//					info(i+":"+alpha);
//				}
				//info(oldAlpha+":"+alpha);
				if (alpha < oldAlpha) {
					// alphaֵ++
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
