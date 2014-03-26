package ws.hoyland.sm;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class Help extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text txtipip;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Help(Shell parent, int style) {
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

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(Help.class, "/help.ico"));
		shell.setSize(400, 250);
		shell.setText("帮助");
		
		Rectangle bounds = getParent().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new BorderLayout(0, 0));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(BorderLayout.SOUTH);
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		
		Button btnNewButton = new Button(composite_2, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnNewButton.setBounds(154, 10, 80, 27);
		btnNewButton.setText("关闭(&C)");
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.CENTER);
		composite_1.setLayout(new BorderLayout(0, 0));
		
		txtipip = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
		txtipip.setText("\r\n1，导入号码，手动导入代理，然后点击开始。\r\n\r\n2，导入号码，设置好自动扫描，设置好定时扫描时间。(建议放入的IP在\r\n200个以内，时间20分钟)然后点击软件界面上的开始，会开始自动扫第一\r\n轮代理，扫完代理后自动导入并开始，后面会定时执行扫IP程序并且自动\r\n更换新代理)");
		txtipip.setEditable(false);
		txtipip.setLayoutData(BorderLayout.CENTER);
	}
}
