package ws.hoyland.sm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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
import org.eclipse.swt.widgets.Text;

public class Option extends Dialog {

	protected Object result;
	protected Shell shell;
	private Configuration configuration = Configuration.getInstance("config.ini");
	private Spinner spinner;
	private Spinner spinner_1;
	private Text text;
	private Button btnCheckButton;
	private Spinner spinner_2;
	private Button button_2;
	
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
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		
		Button button = new Button(composite_2, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//shell.close();
				save();
				Option.this.shell.dispose();
				Engine.getInstance().ready();
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
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("扫描");
		
		Composite composite_4 = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_4);
		
		btnCheckButton = new Button(composite_4, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(btnCheckButton.getSelection()){
					spinner_2.setEnabled(true);
					text.setEnabled(true);
					button_2.setEnabled(true);
				}else{
					spinner_2.setEnabled(false);
					text.setEnabled(false);
					button_2.setEnabled(false);
				}
			}
		});
		btnCheckButton.setBounds(10, 10, 129, 17);
		btnCheckButton.setText("启动自动扫描，间隔");
		
		spinner_2 = new Spinner(composite_4, SWT.BORDER);
		spinner_2.setEnabled(false);
		spinner_2.setMaximum(1500);
		spinner_2.setMinimum(1);
		spinner_2.setBounds(145, 4, 44, 23);
		
		Label lblNewLabel_3 = new Label(composite_4, SWT.NONE);
		lblNewLabel_3.setBounds(195, 10, 29, 17);
		lblNewLabel_3.setText("分钟");
		
		text = new Text(composite_4, SWT.BORDER | SWT.MULTI);
		text.setEnabled(false);
		text.setBounds(10, 33, 214, 95);
		
		button_2 = new Button(composite_4, SWT.CHECK);
		button_2.setText("验证代理");
		button_2.setBounds(230, 10, 69, 17);
	}
		
	private void load(){
		//load & show
		try{
			if(this.configuration.size()>0){
				spinner.setSelection(Integer.parseInt(this.configuration.getProperty("THREAD_COUNT")));
				spinner_1.setSelection(Integer.parseInt(this.configuration.getProperty("TIMEOUT")));
				
				if("true".equals(this.configuration.getProperty("SCAN"))){
					btnCheckButton.setSelection(true);
					spinner_2.setEnabled(true);
					text.setEnabled(true);
					button_2.setEnabled(true);
				}else{
					btnCheckButton.setSelection(false);
					spinner_2.setEnabled(false);
					text.setEnabled(false);
					button_2.setEnabled(false);
				}
				
				if("true".equals(this.configuration.getProperty("VALIDATE"))){
					button_2.setSelection(true);
				}else{
					button_2.setSelection(false);
				}
				spinner_2.setSelection(Integer.parseInt(this.configuration.getProperty("SCAN_ITV")));
				//text.setText(this.configuration.getProperty("IPS"));				
			}
			
			String path = Engine.getInstance().getXPath()+"ip.txt";
			//path = URLDecoder.decode(path, "UTF-8");
			File f = new File(path);
			if(!f.exists()){
				f.createNewFile();
			}
			InputStream input = new FileInputStream(new File(Engine.getInstance().getXPath()+"ip.txt"));//this.getClass().getResourceAsStream("/ip.txt");
			if(input!=null){
				BufferedReader br = new BufferedReader(new InputStreamReader(input));
				String line = null;
				while((line=br.readLine())!=null){
					text.append(line+"\r\n");
				}
				br.close();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void save(){
		try{
			this.configuration.put("THREAD_COUNT", spinner.getText());
			this.configuration.put("TIMEOUT", spinner_1.getText());
			this.configuration.put("SCAN", String.valueOf(btnCheckButton.getSelection()));
			this.configuration.put("SCAN_ITV", spinner_2.getText());
			this.configuration.put("VALIDATE", String.valueOf(button_2.getSelection()));
			//this.configuration.put("IPS", text.getText());
			this.configuration.save();
			
			String path = Engine.getInstance().getXPath()+"ip.txt";
			//path = URLDecoder.decode(path, "UTF-8");
			File f = new File(path);
			if(!f.exists()){
				f.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			bw.write(text.getText());
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

