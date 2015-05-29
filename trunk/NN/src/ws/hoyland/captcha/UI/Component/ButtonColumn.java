package ws.hoyland.captcha.UI.Component;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ws.hoyland.captcha.database.util.DBfactory;

public class ButtonColumn extends AbstractCellEditor implements
		TableCellRenderer, TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8561306185397765278L;
	JButton renderButton;
	JButton editButton;
	String text;

	public ButtonColumn() {
		renderButton = new JButton();
		editButton = new JButton();
		editButton.setFocusPainted(false);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (hasFocus) {
			renderButton.setForeground(table.getForeground());
			renderButton
					.setBackground(UIManager.getColor("Button.background "));
		} else if (isSelected) {
			renderButton.setForeground(table.getSelectionForeground());
			renderButton.setBackground(table.getSelectionBackground());
		} else {
			renderButton.setForeground(table.getForeground());
			renderButton
					.setBackground(UIManager.getColor("Button.background "));
		}

		renderButton.setText((value == null) ? " " : "删除");
		return renderButton;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		text = (value == null) ? " " : value.toString();
		if (value != null) {
			DBfactory.getDBfactory().delSampleandvalue(text);

		}
		editButton.setText("删除");
		return editButton;
	}

	public Object getCellEditorValue() {
		return text;
	}
}
