package ws.hoyland.captcha.UI.Component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.io.File;

public class GraphicPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File file = null;

	public GraphicPanel() {

	}

	public void setFile(File file) {

		this.file = file;
	}

	public void paint(Graphics gr) {
		super.paint(gr);
		try {
			BufferedImage bi = ImageIO.read(file);
			int h = bi.getHeight();
			int w = bi.getWidth();
			for (int i = 0; i < this.getWidth(); i++) {
				for (int j = 0; j < this.getHeight(); j++) {
					int c = bi.getRGB(i * w / this.getWidth(),
							j * h / this.getHeight());
					gr.setColor(new Color(c));
					gr.fillRect(i, j, 1, 1);
				}
			}
		} catch (Exception e) {
		}
	}

}
