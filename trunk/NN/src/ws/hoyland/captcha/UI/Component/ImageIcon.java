package ws.hoyland.captcha.UI.Component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import ws.hoyland.captcha.database.util.DBfactory;
import ws.hoyland.captcha.graphic.util.Common;

import java.util.UUID;

/**
 * 
 * 
 * 
 * @author Administrator
 *
 */

public class ImageIcon extends JComponent implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	private double in[][];
	private int ini[][];

	public ImageIcon(double in[][]) {

		this.in = in;

		this.addMouseListener(this);
	}

	public ImageIcon(int in[][]) {

		this.ini = in;
		this.addMouseListener(this);
	}

	public ImageIcon(String ins) {
		this.in = new double[9][9];
		String[] inta = ins.split(",");
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				this.in[i][j] = Double.valueOf(inta[i * 9 + j]);

			}

		}
	}

	// private static final long serialVersionUID = 1L;

	public void paint(Graphics gr) {

		super.paint(gr);
		if (in != null) {
			int h = in[0].length;// 获取图像的高
			int w = in.length;// 获取图像的宽
			int height = 400;
			int width = 300;
			width = width > w ? w : width;
			height = height > h ? h : height;
			gr.setColor(new Color((int) (Math.random() * 255), (int) (Math
					.random() * 255), (int) (Math.random() * 255)));
			for (int i = 0; i < width; i++) {

				for (int j = 0; j < height; j++) {

					double t = in[i * w / width][j * h / height];

					if (t > 0)

						gr.fillRect(i, j, 1, 1);

				}

			}

		} else if (ini != null) {

			int h = ini[0].length;// 获取图像的高
			int w = ini.length;// 获取图像的宽
			int height = 400;
			int width = 300;
			width = width > w ? w : width;
			height = height > h ? h : height;
			gr.setColor(new Color((int) (Math.random() * 255), (int) (Math
					.random() * 255), (int) (Math.random() * 255)));
			for (int i = 0; i < width; i++) {

				for (int j = 0; j < height; j++) {

					int t = ini[i * w / width][j * h / height];

					if (t > 0)

						gr.fillRect(i, j, 1, 1);

				}

			}

		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

		double t1[][] = new double[1][81];
		for (int k = 0; k < 9; k++) {
			for (int j = 0; j < 9; j++) {
				t1[0][k * 9 + j] = in[k][j];
			}
		}
		String out = Common.arraytoString(t1[0]);

		DBfactory.getDBfactory().insertTsamplevalue(out, "s",
				UUID.randomUUID().toString());
		// DBfactory.getDBfactory().insertTsamplevalue(sample, "", "");

		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		this.repaint();
		this.setBorder(BorderFactory.createRaisedBevelBorder());

	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.repaint();
		this.setBorder(BorderFactory.createLoweredBevelBorder());

		// TODO Auto-generated method stub

	}

}
