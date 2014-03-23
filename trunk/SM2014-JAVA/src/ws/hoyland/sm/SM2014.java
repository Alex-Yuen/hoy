package ws.hoyland.sm;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Label;
import swing2swt.layout.BorderLayout;
import ws.hoyland.util.EngineMessage;
import ws.hoyland.util.EngineMessageType;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Text;

public class SM2014 implements Observer {
	
	protected Shell shell;
	private Option option;
	private About about;
	protected Composite composite_4;
	private Button btnNewButton;
	private Label lblNewLabel;
	private Label lblNewLabel_1;
	private Label lblNewLabel_3;
	private Label lblNewLabel_4;
	private Label lblNewLabel_2;
	private Text text;
	private DecimalFormat df  = new DecimalFormat("0.00");
	private Browser browser;
	private int total = 0;
	private static DateFormat format = new java.text.SimpleDateFormat("[yyyy/MM/dd hh:mm:ss] ");

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
		load();
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
		shell = new Shell(SWT.SHELL_TRIM ^ SWT.MAX
				^ SWT.RESIZE);
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				MessageBox dialog = new MessageBox(shell, SWT.OK|SWT.CANCEL);
		        dialog.setText("确认");
		        dialog.setMessage("关闭当前窗口并退出程序？");

	        	e.doit = false;
	        	
		        if(dialog.open()==SWT.OK){
		        	Engine.getInstance().deleteObserver(SM2014.this);
					
					Thread t = new Thread(new Runnable(){
						@Override
						public void run() {
							Engine.getInstance().exit();
						}
						
					});
					t.start();
		        }		        
			}
		});
		shell.setImage(SWTResourceManager.getImage(SM2014.class, "/logo.ico"));
		shell.setSize(601, 380);
		shell.setText("晒密");
		shell.setLayout(new BorderLayout(0, 0));
		
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmf = new MenuItem(menu, SWT.CASCADE);
		mntmf.setText("文件(&F)");
		
		Menu menu_1 = new Menu(mntmf);
		mntmf.setMenu(menu_1);
		
		MenuItem mntma = new MenuItem(menu_1, SWT.NONE);
		mntma.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog fileDlg = new FileDialog(shell, SWT.OPEN);
				fileDlg.setFilterPath(null);
				fileDlg.setText("导入帐号");
				final String filePath = fileDlg.open();
				if(filePath!=null){
					Thread t = new Thread(new Runnable(){
						@Override
						public void run() {
							Engine.getInstance().loadAccount(filePath);
						}						
					});
					t.start();
				}
			}
		});
		mntma.setText("导入帐号(&A)...\tF2");
		mntma.setAccelerator(SWT.F2);
		
		MenuItem mntmp = new MenuItem(menu_1, SWT.NONE);
		mntmp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog fileDlg = new FileDialog(shell, SWT.OPEN);
				fileDlg.setFilterPath(null);
				fileDlg.setText("导入代理");
				final String filePath = fileDlg.open();
				if(filePath!=null){
					Thread t = new Thread(new Runnable(){
						@Override
						public void run() {
							Engine.getInstance().loadProxy(filePath);
						}						
					});
					t.start();
				}
			}
		});
		mntmp.setText("导入代理(&P)...\tF3");
		mntmp.setAccelerator(SWT.F3);
		
		MenuItem menuItem = new MenuItem(menu_1, SWT.SEPARATOR);
		menuItem.setText("-");
		
		MenuItem mntmx = new MenuItem(menu_1, SWT.NONE);
		mntmx.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		mntmx.setText("退出(&X)\tCTRL+Q");
		mntmx.setAccelerator(SWT.CTRL|'Q');
		
		MenuItem mntmt = new MenuItem(menu, SWT.CASCADE);
		mntmt.setText("工具(&T)");
		
		Menu menu_2 = new Menu(mntmt);
		mntmt.setMenu(menu_2);
		
		MenuItem mntmo = new MenuItem(menu_2, SWT.NONE);		
		mntmo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				option = new Option(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				option.open();
			}
		});
		mntmo.setText("选项(&O)...\tF4");
		mntmo.setAccelerator(SWT.F4);
		
		MenuItem mntmh = new MenuItem(menu, SWT.CASCADE);
		mntmh.setText("帮助(&H)");
		
		Menu menu_3 = new Menu(mntmh);
		mntmh.setMenu(menu_3);
		
		MenuItem mntma_1 = new MenuItem(menu_3, SWT.NONE);
		mntma_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				about = new About(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				about.open();
			}
		});
		mntma_1.setText("关于(&A)...\tF10");
		mntma_1.setAccelerator(SWT.F10);
		
		lblNewLabel = new Label(shell, SWT.BORDER);
		lblNewLabel.setText("停止");
		lblNewLabel.setLayoutData(BorderLayout.SOUTH);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
				
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite composite_1 = new Composite(composite_2, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_1.spacing = 1;
		composite_1.setLayout(fl_composite_1);
		
		Composite composite_9 = new Composite(composite_1, SWT.NONE);
		composite_9.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		lblNewLabel_1 = new Label(composite_9, SWT.NONE);
		lblNewLabel_1.setBounds(10, 55, 178, 17);
		lblNewLabel_1.setText("帐号：0");
		
		lblNewLabel_2 = new Label(composite_9, SWT.NONE);
		lblNewLabel_2.setBounds(10, 78, 178, 17);
		lblNewLabel_2.setText("0 / 0 / 0");
		
		Composite composite_10 = new Composite(composite_1, SWT.NONE);
		composite_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		lblNewLabel_3 = new Label(composite_10, SWT.NONE);
		lblNewLabel_3.setBounds(10, 66, 177, 17);
		lblNewLabel_3.setText("代理：0");
		
		Composite composite_11 = new Composite(composite_1, SWT.NONE);
		composite_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		lblNewLabel_4 = new Label(composite_11, SWT.NONE);
		lblNewLabel_4.setBounds(10, 66, 178, 17);
		lblNewLabel_4.setText("0 / 0 = 0%");
		
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setLayout(new BorderLayout(0, 0));
		
		Group group_1 = new Group(composite_3, SWT.NONE);
		group_1.setLayoutData(BorderLayout.CENTER);
		group_1.setText("日志");
		group_1.setLayout(new BorderLayout(0, 0));
		
		text = new Text(group_1, SWT.BORDER | SWT.MULTI);
		//text.setTextLimit(1000);
//		text.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				if(text.getText().length()>10000*5){
//					text.setText("");
//				}
//			}
//		});
		text.setEditable(false);
		text.setLayoutData(BorderLayout.CENTER);
		
		Group group = new Group(composite_3, SWT.NONE);
		group.setLayoutData(BorderLayout.EAST);
		group.setText("操作");
		group.setLayout(new FillLayout(SWT.VERTICAL));
		
		composite_4 = new Composite(group, SWT.NONE);
		
		Composite composite_5 = new Composite(group, SWT.NONE);
		composite_5.setLayout(new BorderLayout(0, 0));
		
		Composite composite_6 = new Composite(composite_5, SWT.NONE);
		composite_6.setLayoutData(BorderLayout.WEST);
		
		Composite composite_7 = new Composite(composite_5, SWT.NONE);
		composite_7.setLayoutData(BorderLayout.CENTER);
		composite_7.setLayout(new BorderLayout(0, 0));
		
		btnNewButton = new Button(composite_7, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Thread t = new Thread(new Runnable(){
					@Override
					public void run() {
						Engine.getInstance().process();
					}					
				});
				
				t.start();
			}
		});
		btnNewButton.setEnabled(false);
		btnNewButton.setLayoutData(BorderLayout.CENTER);
		btnNewButton.setText("       开始       ");
		
		Composite composite_8 = new Composite(composite_5, SWT.NONE);
		composite_8.setLayoutData(BorderLayout.EAST);
		
		browser = new Browser(group, SWT.NONE);
		browser.setVisible(false);
		browser.setUrl("http://www.2345.com/?k68159276");

	}

	private void load(){
		
		Engine.getInstance().addObserver(SM2014.this);
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				//开始JMX服务
				Engine.getInstance().startService();
			}
			
		});
		t.start();
	}
	@Override
	public void update(Observable obj, Object arg) {
		//接收来自Engine的消息
		final EngineMessage msg = (EngineMessage) arg;
		int type = msg.getType();
				
		switch(type){
			case EngineMessageType.OM_READY:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						btnNewButton.setEnabled(true);
					}				
				});
				break;
			case EngineMessageType.OM_UNREADY:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						btnNewButton.setEnabled(false);
					}				
				});
				break;
			case EngineMessageType.OM_RUNNING:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if((Boolean)msg.getData()){
							lblNewLabel.setText("正在运行...");
							btnNewButton.setText("停止");
						}else{
							lblNewLabel.setText("停止");
							btnNewButton.setText("开始");
						}
					}				
				});
				break;
			case EngineMessageType.OM_ACCOUNT_LOADED:
				Display.getDefault().asyncExec(new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						List<String> ls = (List<String>)msg.getData();
						lblNewLabel_1.setText("帐号： " + ls.get(0));	

						total = Integer.parseInt(ls.get(0));
						//lblNewLabel_4.setData(ls.get(0));
						lblNewLabel_4.setText("0 / "+ls.get(0)+" = 0%");
					}
				});
				break;
			case EngineMessageType.OM_PROXY_LOADED:
				Display.getDefault().asyncExec(new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						List<String> ls = (List<String>)msg.getData();
						lblNewLabel_3.setText("代理： " + ls.get(0));
					}
				});
				break;
			case EngineMessageType.OM_INFO:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {						
						String tm = format.format(new Date());						
						text.append(tm + (String)msg.getData()+"\r\n");
					}
				});
				break;
			case EngineMessageType.OM_STATS:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						Object[] objs = (Object[])msg.getData();
						Integer[] stats = (Integer[])objs[0];
						Integer type = (Integer)objs[1];
						String content = (String)objs[2];
						
						String tm = format.format(new Date());
						int success = stats[0]+stats[1]+stats[2];						
						
						text.append(tm + "DETECTED: " + content + " = " + type.intValue()+"\r\n");
						lblNewLabel_2.setText(stats[0]+ " / " + stats[1] + " / " + stats[2] );
						lblNewLabel_4.setText(success+" / "+total+" = "+df.format((double)(100*success)/(double)total)+"%");
					}
				});
				break;
			case EngineMessageType.OM_NO_PROXY:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						Event ex = new Event();
						ex.widget = btnNewButton;
						//主动触发button点击事件				
						btnNewButton.notifyListeners(SWT.Selection, ex);
						
						MessageBox dialog = new MessageBox(shell, SWT.OK);
				        dialog.setText("确认");
				        dialog.setMessage("代理用完，任务结束！");
				        dialog.open();
					}
				});
				break;				
			default:
				break;
		}		
	}
}
