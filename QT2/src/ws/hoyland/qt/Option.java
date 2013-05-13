package ws.hoyland.qt;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;

public class Option extends Dialog {

	protected Object result;
	protected Shell shlOption;
	private Text text;
	private Text text_1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Option(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlOption.open();
		shlOption.layout();
		Display display = getParent().getDisplay();
		while (!shlOption.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlOption = new Shell(getParent(), getStyle());
		shlOption.setSize(424, 236);
		shlOption.setText("Option");
		
		Button btnCheckButton = new Button(shlOption, SWT.CHECK);
		btnCheckButton.setBounds(47, 41, 98, 17);
		btnCheckButton.setText("单一代理");
		
		text = new Text(shlOption, SWT.BORDER);
		text.setBounds(47, 86, 136, 23);
		
		text_1 = new Text(shlOption, SWT.BORDER);
		text_1.setBounds(207, 86, 63, 23);
		
		Label lblNewLabel = new Label(shlOption, SWT.NONE);
		lblNewLabel.setBounds(189, 89, 12, 17);
		lblNewLabel.setText("：");
		
		Button btnNewButton = new Button(shlOption, SWT.NONE);
		btnNewButton.setBounds(328, 159, 80, 27);
		btnNewButton.setText("&OK");

	}
}
