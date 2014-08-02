package ws.hoyland.sc;

import java.applet.Applet;
import java.awt.HeadlessException;
import java.awt.BorderLayout;
import java.awt.Panel;
import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ClientApplet extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5083414067012956556L;

	public ClientApplet() throws HeadlessException {
		setLayout(new BorderLayout(0, 0));
		
		Panel panel = new Panel();
		add(panel, BorderLayout.WEST);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		Panel panel_3 = new Panel();
		panel.add(panel_3);
		panel_3.setLayout(new GridLayout(0, 1, 0, 0));
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Group 1");
		panel_3.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Group 3");
		panel_3.add(rdbtnNewRadioButton_1);
		
		Panel panel_4 = new Panel();
		panel.add(panel_4);
		panel_4.setLayout(new GridLayout(0, 1, 0, 0));
		
		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("Group 2");
		panel_4.add(rdbtnNewRadioButton_2);
		
		JRadioButton rdbtnNewRadioButton_3 = new JRadioButton("Group 4");
		panel_4.add(rdbtnNewRadioButton_3);
		
		Panel panel_1 = new Panel();
		add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		JButton btnNewButton = new JButton("按键发音");
		panel_1.add(btnNewButton);
		
		Panel panel_2 = new Panel();
		add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("麦克风");
		panel_2.add(lblNewLabel_1, BorderLayout.WEST);
		
		JLabel lblNewLabel = new JLabel("听  筒");
		panel_2.add(lblNewLabel, BorderLayout.EAST);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
		
		//建立NIO连接
		soundSender = new SoundSender("231.0.0.1",10001,1024);  
	    soundReceiver = new SoundReceiver("231.0.0.1",10001,1024);  
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}

	
}
