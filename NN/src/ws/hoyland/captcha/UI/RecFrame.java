package ws.hoyland.captcha.UI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import ws.hoyland.captcha.UI.Component.GraphicBitPanel;
import ws.hoyland.captcha.UI.Component.GraphicListPanel;
import ws.hoyland.captcha.UI.Component.GraphicPanel;
import ws.hoyland.captcha.UI.Component.ImageIcon;
import ws.hoyland.captcha.UI.painter.ImagePainter;
import ws.hoyland.captcha.database.util.DBfactory;
import ws.hoyland.captcha.graphic.util.Common;
import ws.hoyland.captcha.graphic.util.ConnectedDomain;
import ws.hoyland.captcha.graphic.util.GetConnecteddomain;
import ws.hoyland.captcha.synapesnet.Charaterrecognize;


public class RecFrame extends JInternalFrame implements ActionListener {
	/**
	 * 
	 */

	private GraphicPanel gp;
	private GraphicBitPanel graypanel;
	private GraphicListPanel graplistpanel;

	private static final long serialVersionUID = 1L;
	private JPanel pa;
	JFileChooser filechooser;
	File file;
	Graphics g;
	Graphics panelg;
	Charaterrecognize rognizer;// 字符识别
	ImagePainter painter;
	RecFrame mframe;
	ArrayList<JComponent> list;

	public void reco() {
		if (list == null)
			this.list = new ArrayList<JComponent>();
		else {
			this.graplistpanel.clear();
		}
		if (file == null)
			return;
		gp.setFile(file);
		try {
			BufferedImage bi = ImageIO.read(file);// 通过imageio将图像载入
			int[][] gray = Common.getGray(bi);// 获得灰度图像
			int[][] gray1 = Common.get01bit(gray);
			int[][] temp = Common.transsize(gray1, 400, 400);
			this.graypanel.setBit(temp);
			ImageIcon imgicon = new ImageIcon(gray1);
			imgicon.setPreferredSize(new Dimension(10, 10));
			list.add(imgicon);
			GetConnecteddomain getconnectdomain = new GetConnecteddomain();
			getconnectdomain.setInitail(gray1);
			Hashtable<Integer, ConnectedDomain> connecteddomaintable = getconnectdomain
					.getConnecteddomain();
			Iterator<Integer> itr = connecteddomaintable.keySet().iterator();
			while (itr.hasNext()) {
				Integer index = itr.next();
				ConnectedDomain connecteddomain = connecteddomaintable
						.get(index);
				if (connecteddomain.getPixelcount() < 20)
					continue;
				double[][] gray2 = connecteddomain.getBit();
				double[][] gray4 = Common.transsize(gray2, 9, 9);
				ImageIcon grayimg = new ImageIcon(gray4);
				grayimg.setPreferredSize(new Dimension(20, 20));
				grayimg.setBorder(BorderFactory.createRaisedBevelBorder());
				list.add(grayimg);
				double t1[][] = new double[1][81];
				for (int k = 0; k < 9; k++) {
					for (int j = 0; j < 9; j++) {
						t1[0][k * 9 + j] = gray4[k][j];
					}
				}
				rognizer.setIn(t1);
				double rev = rognizer.recognize();
				String v = DBfactory.getDBfactory().getValuebyDouble(rev);
				JLabel j = new JLabel(v);
				j.setPreferredSize(new Dimension(20, 20));
				list.add(j);
				System.out.println(v);
				this.graplistpanel.setList(list);
				this.pa.repaint();

			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public RecFrame() {

		super("识别", true, true, true, true);
		this.mframe = this;
		rognizer = new Charaterrecognize();
		Object[] rec = DBfactory.getDBfactory().getSampleandvalue();
		rognizer.setDersiredIn((double[][]) rec[0]);
		rognizer.setDeriredOut((double[][]) rec[1]);
		rognizer.trainning();
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("开始(ctrl+f)");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		JMenuItem newMenuItem = new JMenuItem("输入图像(ctrl+n)", KeyEvent.VK_N);
		newMenuItem.addActionListener(new ItemHandler());
		fileMenu.add(newMenuItem);
		pa = new JPanel();
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		this.add(pa);
		pa.setSize(1024, 600);
		pa.setBounds(0, 0, 1024, 768);
		pa.setLayout(new FlowLayout(FlowLayout.LEFT));
		gp = new GraphicPanel();
		gp.setBorder(BorderFactory.createLoweredBevelBorder());
		gp.setPreferredSize(new Dimension(400, 400));
		gp.setBounds(0, 0, 400, 400);
		pa.add(gp);
		graypanel = new GraphicBitPanel();
		graypanel.setBorder(BorderFactory.createCompoundBorder());
		graypanel.setPreferredSize(new Dimension(400, 400));
		graypanel.setBounds(0, 0, 400, 400);
		pa.add(graypanel);
		graplistpanel = new GraphicListPanel();
		graplistpanel.setBorder(BorderFactory.createLoweredBevelBorder());
		graplistpanel.setPreferredSize(new Dimension(400, 400));
		graplistpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		graplistpanel.setBounds(0, 0, 400, 400);
		graplistpanel.setVisible(true);
		pa.add(graplistpanel);
		pa.setVisible(true);
		JButton bt = new JButton("确定");
		bt.addActionListener((ActionListener) this);
		bt.setSize(20, 20);
		bt.setVisible(true);
		pa.add(bt);
		filechooser = new JFileChooser();
		filechooser.setVisible(false);
		this.add(filechooser);
	
		this.setBounds(0, 0, 1024, 768);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		this.pa.repaint();

		if (filechooser == null)
			return;
		reco();
		this.pa.repaint();
		

	}

	public class ItemHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			filechooser.setVisible(true);
			filechooser.setSize(100, 100);
			filechooser.showOpenDialog(mframe);
			file = filechooser.getSelectedFile();
			filechooser.setVisible(false);
		}
	}

}
