package ws.hoyland.qm;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Option extends Dialog {

	protected Object result;
	protected Shell shell;

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
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	public void close(){
		this.shell.close();
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText("设置");
		
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(10, 10, 424, 215);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("常规");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 38, 112, 17);
		lblNewLabel.setText("每个号码发送群数:");
		
		Spinner spinner = new Spinner(composite, SWT.BORDER);
		spinner.setMaximum(10);
		spinner.setMinimum(1);
		spinner.setSelection(2);
		spinner.setBounds(128, 35, 47, 23);
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("每个令牌发送群数:");
		label.setBounds(10, 73, 112, 17);
		
		Spinner spinner_1 = new Spinner(composite, SWT.BORDER);
		spinner_1.setMaximum(10);
		spinner_1.setMinimum(1);
		spinner_1.setSelection(2);
		spinner_1.setBounds(128, 70, 47, 23);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Option.this.close();
			}
		});
		btnNewButton.setBounds(252, 235, 80, 27);
		btnNewButton.setText("确定(&O)");
		
		Button btnc = new Button(shell, SWT.NONE);
		btnc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Option.this.close();
			}
		});
		btnc.setText("取消(&C)");
		btnc.setBounds(354, 235, 80, 27);
	}
}
