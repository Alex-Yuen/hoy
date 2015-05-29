package ws.hoyland.captcha.UI.Component;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class GraphicListPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<JComponent> list;

	public void setList(ArrayList<JComponent> list) {

		this.list = list;
		for (int i = 0; i < list.size(); i++)
			this.add(list.get(i));
		this.doLayout();
		this.repaint();
	}

	public void clear() {
		if (list == null)
			return;
		for (int i = 0; i < list.size(); i++) {
			this.remove(list.get(i));
		}
		list.clear();
	}

	public GraphicListPanel() {

	}

}
