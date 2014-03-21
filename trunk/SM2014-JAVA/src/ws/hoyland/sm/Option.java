package ws.hoyland.sm;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import swing2swt.layout.BorderLayout;
import ws.hoyland.util.Configuration;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class Option extends Dialog {

	protected Object result;
	protected Shell shell;
	private Configuration configuration = Configuration.getInstance("config.ini");
	private Spinner spinner;
	private Spinner spinner_1;
	
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

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellActivated(ShellEvent e) {
				load();
			}
		});
		shell.setImage(SWTResourceManager.getImage(Option.class, "/option.ico"));
		shell.setSize(400, 250);
		shell.setText("设置");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new BorderLayout(0, 0));
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		
		Button button = new Button(composite_2, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//shell.close();
				save();
				Option.this.shell.dispose();
			}
		});
		button.setText("确定(&O)");
		button.setBounds(209, 10, 80, 27);
		
		Button button_1 = new Button(composite_2, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Option.this.shell.dispose();
			}
		});
		button_1.setText("取消(&C)");
		button_1.setBounds(304, 10, 80, 27);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(BorderLayout.SOUTH);
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.CENTER);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(composite_1, SWT.NONE);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("常规");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_3);
		composite_3.setLayout(null);
		
		Label lblNewLabel_1 = new Label(composite_3, SWT.NONE);
		lblNewLabel_1.setBounds(10, 13, 78, 17);
		lblNewLabel_1.setText("线程：");
		
		Label lblNewLabel_2 = new Label(composite_3, SWT.NONE);
		lblNewLabel_2.setBounds(10, 43, 78, 17);
		lblNewLabel_2.setText("超时（秒）：");
		
		spinner = new Spinner(composite_3, SWT.BORDER);
		spinner.setMaximum(1000);
		spinner.setMinimum(1);
		spinner.setBounds(120, 10, 52, 23);
		
		spinner_1 = new Spinner(composite_3, SWT.BORDER);
		spinner_1.setMaximum(10);
		spinner_1.setMinimum(1);
		spinner_1.setBounds(120, 40, 52, 23);
	}
		
	private void load(){
		//load & show
		try{
			if(this.configuration.size()>0){
				spinner.setSelection(Integer.parseInt(this.configuration.getProperty("THREAD_COUNT")));
				spinner_1.setSelection(Integer.parseInt(this.configuration.getProperty("TIMEOUT")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void save(){
		try{
			this.configuration.put("THREAD_COUNT", spinner.getText());
			this.configuration.put("TIMEOUT", spinner_1.getText());			
			this.configuration.save();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

