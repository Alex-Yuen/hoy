package ws.hoyland.xplayer.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class OptionDialog extends Dialog {

	protected Object result;

	protected Shell shell;

	public OptionDialog(Shell parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	protected void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(500, 375);
		shell.setText("Option");
		shell.setLocation(
				Display.getCurrent().getClientArea().width / 2
						- shell.getSize().x / 2,
				Display.getCurrent().getClientArea().height / 2
						- shell.getSize().y / 2);
		//
	}

}
