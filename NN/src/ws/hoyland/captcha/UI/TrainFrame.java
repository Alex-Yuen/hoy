package ws.hoyland.captcha.UI;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EventObject;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ws.hoyland.captcha.UI.Component.ButtonColumn;
import ws.hoyland.captcha.UI.Component.ImageIcon;
import ws.hoyland.captcha.UI.Component.JTextFieldU;
import ws.hoyland.captcha.database.util.DBfactory;

public class TrainFrame extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable listbox;
	private DBfactory dbf;

	public TrainFrame() {

		super("样本值映射", true, true, true, true);

		String[] headers = { "表头一", "表头二", "表头三" };
		Object[][] cellData = null;

		DefaultTableModel listModel = new DefaultTableModel(cellData, headers) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return true;
			}
		};

		dbf = DBfactory.getDBfactory();
		dbf.fillLIstModel(listModel);

		// listModel.addElement(arg0)
		listbox = new JTable(listModel);

		listbox.getColumnModel().getColumn(0)
				.setCellRenderer(new TableCellRenderer() {

					@Override
					public Component getTableCellRendererComponent(JTable arg0,
							Object arg1, boolean arg2, boolean arg3, int arg4,
							int arg5) {
						// TODO Auto-generated method stub
						return new ImageIcon(arg1.toString());
					}
				});
		listbox.getColumnModel().getColumn(1)
				.setCellRenderer(new TableCellRenderer() {

					@Override
					public Component getTableCellRendererComponent(JTable arg0,
							Object arg1, boolean arg2, boolean arg3, int arg4,
							int arg5) {
						Object[] ov = (Object[]) arg1;
						// TODO Auto-generated method stub
						return new JLabel(ov[0].toString());
					}
				});
		listbox.getColumnModel().getColumn(1)
				.setCellEditor(new TableCellEditor() {

					public Component getTableCellEditorComponent(JTable table,
							Object value, boolean isSelected, int row,
							int column) {
						// TODO Auto-generated method stub
						if (isSelected) {

							System.out.print("123123");
						} else {

							// return JLabel(value.toString());
						}
						Object[] ov = (Object[]) value;
						JTextFieldU jf = new JTextFieldU(ov[0].toString());
						jf.setuuid(ov[1].toString());

						jf.addFocusListener(new FocusListener() {

							@Override
							public void focusGained(FocusEvent arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void focusLost(FocusEvent arg0) {
								// TODO Auto-generated method stub
								System.out.print(((JTextField) arg0
										.getComponent()).getText().toString());
								DBfactory.getDBfactory().updateSampleValue(
										((JTextFieldU) arg0.getComponent())
												.getUUid(),
										((JTextField) arg0.getComponent())
												.getText().toString());

							}

						});

						return jf;

					}

					public void addCellEditorListener(CellEditorListener arg0) {
						// TODO Auto-generated method stub

					}

					public void cancelCellEditing() {
						// TODO Auto-generated method stub

					}

					public Object getCellEditorValue() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean isCellEditable(EventObject arg0) {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public void removeCellEditorListener(CellEditorListener arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean shouldSelectCell(EventObject arg0) {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public boolean stopCellEditing() {
						// TODO Auto-generated method stub
						return true;
					}

				});

		ButtonColumn bc = new ButtonColumn();
		listbox.getColumnModel().getColumn(2).setCellRenderer(bc);
		listbox.getColumnModel().getColumn(2).setCellEditor(bc);
		listbox.setVisible(true);
		this.add(listbox);
		this.setBounds(0, 0, 1024, 768);
		this.setVisible(true);

	}

}
