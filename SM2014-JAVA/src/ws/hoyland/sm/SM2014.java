package ws.hoyland.sm;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Label;
import swing2swt.layout.BorderLayout;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;

public class SM2014 {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SM2014 window = new SM2014();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(601, 380);
		shell.setText("晒密");
		shell.setLayout(new BorderLayout(0, 0));
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmNewItem = new MenuItem(menu, SWT.NONE);
		mntmNewItem.setText("文件(&F)");
		
		MenuItem mntmt = new MenuItem(menu, SWT.NONE);
		mntmt.setText("工具(&T)");
		
		MenuItem mntmh = new MenuItem(menu, SWT.NONE);
		mntmh.setText("帮助(&H)");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(BorderLayout.SOUTH);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new RowLayout(SWT.VERTICAL));

	}
}
