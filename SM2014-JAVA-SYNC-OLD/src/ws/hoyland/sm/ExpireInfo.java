package ws.hoyland.sm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import swing2swt.layout.BorderLayout;
import ws.hoyland.security.ClientDetecter;

import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;

public class ExpireInfo {

	protected Shell shell;
	private Text text;

	/**
	 * Launch the application.
	 * @param args
	 */
//	public static void main(String[] args) {
//		try {
//			ExpireInfo window = new ExpireInfo();
//			window.open();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(SWT.CLOSE | SWT.TITLE);
		shell.setImage(SWTResourceManager.getImage(ExpireInfo.class, "/logo.ico"));
		shell.addShellListener(new ShellAdapter() {
			public void shellActivated(ShellEvent e) {
				text.setText(ClientDetecter.getMachineID("SMZS"));
			}
			public void shellClosed(ShellEvent e) {
				shell.dispose();
				System.exit(0);
			}
		});
		shell.setSize(347, 85);
		shell.setText("此机器授权已经过期");
		
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.setLayout(new BorderLayout(0, 0));
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(BorderLayout.NORTH);
		
		text = new Text(shell, SWT.BORDER | SWT.CENTER);
		text.setLayoutData(BorderLayout.CENTER);
		text.setEditable(false);
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setLayoutData(BorderLayout.SOUTH);

	}
}
