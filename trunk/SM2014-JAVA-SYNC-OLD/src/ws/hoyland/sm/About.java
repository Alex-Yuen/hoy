package ws.hoyland.sm;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import swing2swt.layout.BorderLayout;
import ws.hoyland.security.ClientDetecter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class About extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text txtD;
	protected Label lblNewLabel_5;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public About(Shell parent, int style) {
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
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellActivated(ShellEvent e) {
				txtD.setText(ClientDetecter.getMachineID("SMZS"));
			}
		});
		shell.setSize(400, 250);
		shell.setText("关于");

		//Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
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
		
		Button btnc = new Button(composite_2, SWT.NONE);
		btnc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				About.this.shell.dispose();
			}
		});
		btnc.setText("关闭(&C)");
		btnc.setBounds(304, 10, 80, 27);
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_1.setLayoutData(BorderLayout.CENTER);
		composite_1.setLayout(new BorderLayout(0, 0));
		
		Composite composite_3 = new Composite(composite_1, SWT.NONE);
		composite_3.setLayoutData(BorderLayout.WEST);
		composite_3.setLayout(new BorderLayout(0, 0));
		
		Label lblNewLabel_1 = new Label(composite_3, SWT.NONE);
		lblNewLabel_1.setImage(SWTResourceManager.getImage(About.class, "/about.png"));
		
		Composite composite_4 = new Composite(composite_1, SWT.NONE);
		composite_4.setLayoutData(BorderLayout.CENTER);
		composite_4.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite composite_5 = new Composite(composite_4, SWT.NONE);
		FillLayout fl_composite_5 = new FillLayout(SWT.VERTICAL);
		fl_composite_5.marginWidth = 4;
		fl_composite_5.marginHeight = 4;
		fl_composite_5.spacing = 4;
		composite_5.setLayout(fl_composite_5);
		
		Label lblNewLabel_3 = new Label(composite_5, SWT.NONE);
		lblNewLabel_3.setText("微风晒密(代理版)");
		
		Label lblNewLabel_2 = new Label(composite_5, SWT.NONE);
		lblNewLabel_2.setText("1.1");
		
		Label lblNewLabel_4 = new Label(composite_5, SWT.NONE);
		lblNewLabel_4.setText("Copyright © 2014");
		
		lblNewLabel_5 = new Label(composite_5, SWT.NONE);
		
		Composite composite_6 = new Composite(composite_4, SWT.NONE);
		FillLayout fl_composite_6 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_6.spacing = 2;
		fl_composite_6.marginWidth = 2;
		composite_6.setLayout(fl_composite_6);
		
		txtD = new Text(composite_6, SWT.BORDER | SWT.MULTI);
		txtD.setEditable(false);
	}
}
