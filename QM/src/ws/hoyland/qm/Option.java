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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public class Option extends Dialog {

	protected Object result;
	protected Shell shell;
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
		this.shell.setVisible(false);
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
		
		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("高级");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(composite_1);
		
		Button btnCheckButton = new Button(composite_1, SWT.CHECK);
		btnCheckButton.setBounds(10, 24, 69, 17);
		btnCheckButton.setText("群数重拨");
		
		Button btnCheckButton_1 = new Button(composite_1, SWT.CHECK);
		btnCheckButton_1.setBounds(10, 54, 69, 17);
		btnCheckButton_1.setText("帐号重拨");
		
		Spinner spinner_2 = new Spinner(composite_1, SWT.BORDER);
		spinner_2.setMinimum(1);
		spinner_2.setSelection(10);
		spinner_2.setEnabled(false);
		spinner_2.setBounds(92, 21, 47, 23);
		
		Spinner spinner_3 = new Spinner(composite_1, SWT.BORDER);
		spinner_3.setMinimum(1);
		spinner_3.setSelection(10);
		spinner_3.setEnabled(false);
		spinner_3.setBounds(92, 51, 47, 23);
		
		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setBounds(10, 84, 61, 17);
		lblNewLabel_1.setText("宽带连接:");
		
		Combo combo = new Combo(composite_1, SWT.NONE);
		combo.setBounds(91, 80, 88, 25);
		
		Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		lblNewLabel_2.setBounds(10, 110, 61, 17);
		lblNewLabel_2.setText("宽带帐号:");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setBounds(92, 107, 139, 23);
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setText("宽带密码:");
		label_1.setBounds(10, 136, 61, 17);
		
		text_1 = new Text(composite_1, SWT.BORDER);
		text_1.setBounds(92, 133, 139, 23);
		
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
