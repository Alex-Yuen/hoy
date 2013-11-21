package ws.hoyland.sszs;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SSZS implements Observer{

	protected Shell shlSszs;
	
	private Table table;
	private Text text_2;
	private Table table_1;
	private Option option;
	private Label status;
	private Label label;
	private Label label_1;
	private Label label_5;
	private Button button_2;
	private Text text_1;
	private Text text_3;
	private Button btnDenglu;
	private Button btnUu;
	private Label label_2;
	private Label label_3;
	private Button button_1;
	private Button button_3;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SSZS window = new SSZS();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SSZS(){
		Engine.getInstance().addObserver(this);
	}
	
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlSszs.open();

		EngineMessage message = new EngineMessage();
		message.setType(EngineMessageType.IM_CAPTCHA_TYPE);
		message.setData(btnUu.getSelection());
		Engine.getInstance().fire(message);
		
		shlSszs.layout();
		while (!shlSszs.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {		
		shlSszs = new Shell(Display.getDefault(), SWT.SHELL_TRIM ^ SWT.MAX
				^ SWT.RESIZE);
		shlSszs.setImage(SWTResourceManager.getImage(SSZS.class, "/ws/hoyland/sszs/logo.ico"));
		
		shlSszs.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				Engine.getInstance().deleteObserver(SSZS.this);
				System.exit(0);
			}
		});
		shlSszs.setSize(713, 499);
		shlSszs.setText("申诉助手");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shlSszs.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlSszs.setLocation(x, y);
		
		status = new Label(shlSszs, SWT.BORDER);
		status.setBounds(0, 450, 706, 20);
		
		label = new Label(shlSszs, SWT.NONE);
		label.setText("帐号列表:");
		label.setBounds(0, 1, 60, 17);
		
		label_1 = new Label(shlSszs, SWT.BORDER | SWT.WRAP);
		label_1.setBounds(66, 1, 225, 17);
		
		Link link = new Link(shlSszs, 0);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog fileDlg = new FileDialog(shlSszs, SWT.OPEN);
				fileDlg.setFilterPath(null);
				fileDlg.setText("选择帐号文件");
				String filePath = fileDlg.open();
				if(filePath!=null){
					EngineMessage message = new EngineMessage();
					message.setType(EngineMessageType.IM_LOAD_ACCOUNT);
					message.setData(filePath);
					Engine.getInstance().fire(message);
				}
			}
		});
		link.setText("<a>导入...</a>");
		link.setBounds(314, 1, 36, 17);
		
		table = new Table(shlSszs, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(0, 24, 350, 238);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(30);
		tableColumn.setText("ID");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("帐号");
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("密码");
		
		TableColumn tableColumn_4 = new TableColumn(table, SWT.NONE);
		tableColumn_4.setWidth(98);
		tableColumn_4.setText("状态");
		
		label_5 = new Label(shlSszs, SWT.BORDER | SWT.WRAP);
		label_5.setBounds(422, 1, 225, 17);
		
		Link link_3 = new Link(shlSszs, 0);
		link_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog fileDlg = new FileDialog(shlSszs, SWT.OPEN);
				fileDlg.setFilterPath(null);
				fileDlg.setText("选择邮件列表");
				String filePath = fileDlg.open();
				if(filePath!=null){
					EngineMessage message = new EngineMessage();
					message.setType(EngineMessageType.IM_LOAD_MAIL);
					message.setData(filePath);
					Engine.getInstance().fire(message);
				}
			}
		});
		link_3.setText("<a>导入...</a>");
		link_3.setBounds(670, 1, 36, 17);
		
		Group group = new Group(shlSszs, SWT.NONE);
		group.setText("工作区");
		group.setBounds(217, 268, 489, 176);
		
		Label label_6 = new Label(group, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_6.setForeground(SWTResourceManager.getColor(0, 0, 0));
		label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		label_6.setBounds(10, 103, 149, 73);
		
		text_2 = new Text(group, SWT.CENTER);
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 18, SWT.NORMAL));
		text_2.setEnabled(false);
		text_2.setBackground(SWTResourceManager.getColor(255, 255, 255));
		text_2.setBounds(200, 124, 96, 34);
		
		button_2 = new Button(group, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EngineMessage message = new EngineMessage();
				message.setType(EngineMessageType.IM_PROCESS);
				Engine.getInstance().fire(message);
			}
		});
		button_2.setText("开始");
		button_2.setEnabled(false);
		button_2.setBounds(340, 103, 149, 73);
		
		Label lblAb = new Label(group, SWT.NONE);
		lblAb.setText("XA:");
		lblAb.setBounds(10, 37, 41, 17);
		
		Label label_8 = new Label(group, SWT.NONE);
		label_8.setText("0/0");
		label_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_8.setAlignment(SWT.RIGHT);
		label_8.setBounds(63, 37, 96, 17);
		
		Label lblXb = new Label(group, SWT.NONE);
		lblXb.setText("YA:");
		lblXb.setBounds(174, 37, 41, 17);
		
		Label label_10 = new Label(group, SWT.NONE);
		label_10.setText("0:0");
		label_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_10.setAlignment(SWT.RIGHT);
		label_10.setBounds(227, 37, 96, 17);
		
		Label lblYa = new Label(group, SWT.NONE);
		lblYa.setText("XB:");
		lblYa.setBounds(10, 60, 41, 17);
		
		Label label_12 = new Label(group, SWT.NONE);
		label_12.setText("0");
		label_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_12.setAlignment(SWT.RIGHT);
		label_12.setBounds(63, 60, 96, 17);
		
		Label lblYb = new Label(group, SWT.NONE);
		lblYb.setText("YB:");
		lblYb.setBounds(174, 60, 41, 17);
		
		Label label_14 = new Label(group, SWT.NONE);
		label_14.setText("0");
		label_14.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_14.setAlignment(SWT.RIGHT);
		label_14.setBounds(227, 60, 96, 17);
		
		Label lblXc = new Label(group, SWT.NONE);
		lblXc.setText("ZA:");
		lblXc.setBounds(340, 37, 41, 17);
		
		Label label_16 = new Label(group, SWT.NONE);
		label_16.setText("0");
		label_16.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_16.setAlignment(SWT.RIGHT);
		label_16.setBounds(393, 37, 96, 17);
		
		Label lblYc = new Label(group, SWT.NONE);
		lblYc.setText("ZB:");
		lblYc.setBounds(340, 60, 41, 17);
		
		Label label_18 = new Label(group, SWT.NONE);
		label_18.setText("0");
		label_18.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_18.setAlignment(SWT.RIGHT);
		label_18.setBounds(393, 60, 96, 17);
		
		Label label_19 = new Label(group, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_19.setForeground(SWTResourceManager.getColor(0, 0, 0));
		label_19.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		label_19.setBounds(174, 103, 149, 73);
		
		Label label_20 = new Label(shlSszs, SWT.NONE);
		label_20.setText("邮箱列表:");
		label_20.setBounds(356, 1, 60, 17);
		
		table_1 = new Table(shlSszs, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		table_1.setBounds(356, 24, 350, 238);
		
		TableColumn tableColumn_7 = new TableColumn(table_1, SWT.NONE);
		tableColumn_7.setWidth(30);
		tableColumn_7.setText("ID");
		
		TableColumn tableColumn_8 = new TableColumn(table_1, SWT.NONE);
		tableColumn_8.setWidth(100);
		tableColumn_8.setText("帐号");
		
		TableColumn tableColumn_9 = new TableColumn(table_1, SWT.NONE);
		tableColumn_9.setWidth(100);
		tableColumn_9.setText("密码");
		
		TableColumn tableColumn_11 = new TableColumn(table_1, SWT.NONE);
		tableColumn_11.setWidth(98);
		tableColumn_11.setText("使用次数");
		
		Group group_1 = new Group(shlSszs, SWT.NONE);
		group_1.setText("识别方式");
		group_1.setBounds(0, 268, 213, 176);
		
		btnUu = new Button(group_1, SWT.CHECK);
		btnUu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnUu.getSelection()){
					label_2.setEnabled(true);
					label_3.setEnabled(true);
					text_3.setEnabled(true);
					text_1.setEnabled(true);
					button_1.setEnabled(true);
					button_3.setEnabled(true);
					btnDenglu.setEnabled(true);
				}else{
					label_2.setEnabled(false);
					label_3.setEnabled(false);
					text_3.setEnabled(false);
					text_1.setEnabled(false);
					button_1.setEnabled(false);
					button_3.setEnabled(false);
					btnDenglu.setEnabled(false);					
				}
				
				EngineMessage message = new EngineMessage();
				message.setType(EngineMessageType.IM_CAPTCHA_TYPE);
				message.setData(btnUu.getSelection());
				Engine.getInstance().fire(message);
				//ready();
			}
		});
		btnUu.setText("使用UU输入验证码:");
		btnUu.setSelection(true);
		btnUu.setBounds(10, 21, 132, 17);
		
		label_2 = new Label(group_1, SWT.NONE);
		label_2.setText("帐号:");
		label_2.setBounds(10, 46, 43, 17);
		
		label_3 = new Label(group_1, SWT.NONE);
		label_3.setText("密码:");
		label_3.setBounds(10, 70, 43, 17);
		
		button_1 = new Button(group_1, SWT.CHECK);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!button_1.getSelection()){
					button_3.setSelection(false);
				}
			}
		});
		button_1.setText("记住密码");
		button_1.setBounds(10, 92, 69, 17);
		
		button_3 = new Button(group_1, SWT.CHECK);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(button_3.getSelection()){
					button_1.setSelection(true);
				}
			}
		});
		button_3.setText("自动登录");
		button_3.setBounds(135, 92, 69, 17);

		text_1 = new Text(group_1, SWT.BORDER);
		text_1.setBounds(59, 44, 139, 20);
		
		text_3 = new Text(group_1, SWT.BORDER | SWT.PASSWORD);
		text_3.setBounds(59, 68, 139, 20);		
		
		btnDenglu = new Button(group_1, SWT.NONE);
		btnDenglu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> params = new ArrayList<String>();
				params.add(text_1.getText());
				params.add(text_3.getText());
				params.add(String.valueOf(button_1.getSelection()));
				params.add(String.valueOf(button_3.getSelection()));
				
				EngineMessage message = new EngineMessage();
				message.setType(EngineMessageType.IM_USERLOGIN);
				message.setData(params);
				
				Engine.getInstance().fire(message);
			}
		});
		btnDenglu.setText("登录");
		btnDenglu.setBounds(41, 118, 129, 48);
		
		Link link_1 = new Link(group_1, 0);
		link_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				option = new Option(shlSszs, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				option.open();
			}
		});
		link_1.setBounds(174, 21, 24, 17);
		link_1.setText("<a>设置</a>");

	}

	@Override
	public void update(Observable obj, Object arg) {
		//接收来自Engine的消息
		final EngineMessage msg = (EngineMessage) arg;
		int type = msg.getType();
		
		switch(type){
			case EngineMessageType.OM_LOGINING:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						status.setText("正在登录");
					}				
				});
				break;
			case EngineMessageType.OM_LOGINED:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						status.setText("登录成功: ID="+msg.getData());
					}				
				});
				break;
			case EngineMessageType.OM_LOGIN_ERROR:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						status.setText("登录失败: ERR="+msg.getData());
					}				
				});
				break;
			case EngineMessageType.OM_CLEAR_ACC_TBL:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						table.removeAll();
					}				
				});
				break;
			case EngineMessageType.OM_ADD_ACC_TBIT:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						TableItem tableItem = new TableItem(
								table, SWT.NONE);
						tableItem.setText((String[])msg.getData());
						table.setSelection(tableItem);
					}
				});
				break;
			case EngineMessageType.OM_ACCOUNT_LOADED:
				Display.getDefault().asyncExec(new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						List<String> ls = (List<String>)msg.getData();
//						label.setText("帐号列表 (共 " + ls.get(0)
//								+ " 条):");
						label_1.setText(ls.get(1));
					}
				});
				break;
			case EngineMessageType.OM_CLEAR_MAIL_TBL:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						table_1.removeAll();
					}				
				});
				break;
			case EngineMessageType.OM_ADD_MAIL_TBIT:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						TableItem tableItem = new TableItem(
								table_1, SWT.NONE);
						tableItem.setText((String[])msg.getData());
						table_1.setSelection(tableItem);
					}
				});
				break;
			case EngineMessageType.OM_MAIL_LOADED:
				Display.getDefault().asyncExec(new Runnable() {
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						List<String> ls = (List<String>)msg.getData();
//						label.setText("帐号列表 (共 " + ls.get(0)
//								+ " 条):");
						label_5.setText(ls.get(1));
					}
				});
				break;
			case EngineMessageType.OM_READY:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						button_2.setEnabled(true);
					}				
				});
				break;
			case EngineMessageType.OM_UNREADY:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						button_2.setEnabled(false);
					}				
				});
				break;
			case EngineMessageType.OM_RUNNING:
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						if((Boolean)msg.getData()){
							status.setText("正在运行...");
							button_2.setText("停止");
						}else{
							status.setText("运行停止");
							button_2.setText("开始");
						}
					}				
				});
				break;
			default:
				break;
		}
	}

}
