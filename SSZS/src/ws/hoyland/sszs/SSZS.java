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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SSZS implements Observer{

	protected Shell shlSszs;
	
	private Table table;
	private Text text;
	private Text text_2;
	private Table table_1;
	private Option option;
	private Text text_1;
	private Text text_3;
	private Button btnUu;
	private Label label_3;
	private Label label_4;
	private Button button;
	private Button button_1;
	private Button btnDenglu;
	private Label status;

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
		shlSszs.setSize(859, 574);
		shlSszs.setText("申诉助手");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shlSszs.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlSszs.setLocation(x, y);
		
		status = new Label(shlSszs, SWT.BORDER);
		status.setBounds(0, 531, 852, 17);
		
		Label label = new Label(shlSszs, SWT.NONE);
		label.setText("帐号列表 (共 0 条):");
		label.setBounds(0, 2, 164, 17);
		
		Label label_1 = new Label(shlSszs, SWT.BORDER | SWT.WRAP);
		label_1.setBounds(170, 2, 254, 17);
		
		Link link = new Link(shlSszs, 0);
		link.setText("<a>导入...</a>");
		link.setBounds(437, 2, 36, 17);
		
		Label label_2 = new Label(shlSszs, SWT.NONE);
		label_2.setText("系统日志:");
		label_2.setBounds(489, 2, 363, 17);
		
		table = new Table(shlSszs, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(0, 23, 474, 320);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(30);
		tableColumn.setText("ID");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(125);
		tableColumn_1.setText("帐号");
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(141);
		tableColumn_2.setText("密码");
		
		TableColumn tableColumn_4 = new TableColumn(table, SWT.NONE);
		tableColumn_4.setWidth(120);
		tableColumn_4.setText("状态");
		
		text = new Text(shlSszs, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		text.setEditable(false);
		text.setBounds(489, 26, 363, 223);
		
		btnUu = new Button(shlSszs, SWT.CHECK);
		btnUu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnUu.getSelection()){
					label_3.setEnabled(true);
					label_4.setEnabled(true);
					text_1.setEnabled(true);
					text_3.setEnabled(true);					
					button.setEnabled(true);
					button_1.setEnabled(true);					
					btnDenglu.setEnabled(true);					
				}else{
					label_3.setEnabled(false);
					label_4.setEnabled(false);
					text_1.setEnabled(false);
					text_3.setEnabled(false);					
					button.setEnabled(false);
					button_1.setEnabled(false);					
					btnDenglu.setEnabled(false);				
				}
			}
		});
		btnUu.setSelection(true);
		btnUu.setText("使用UU输入验证码:");
		btnUu.setBounds(489, 255, 132, 17);
		
		label_3 = new Label(shlSszs, SWT.NONE);
		label_3.setText("帐号:");
		label_3.setBounds(489, 280, 43, 17);
		
		Link link_2 = new Link(shlSszs, 0);
		link_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				option = new Option(shlSszs, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				option.open();
			}
		});
		link_2.setText("<a>高级设置</a>");
		link_2.setBounds(799, 255, 53, 17);
		
		Label label_5 = new Label(shlSszs, SWT.BORDER | SWT.WRAP);
		label_5.setBounds(67, 351, 241, 17);
		
		Link link_3 = new Link(shlSszs, 0);
		link_3.setText("<a>导入...</a>");
		link_3.setBounds(321, 351, 36, 17);
		
		Group group = new Group(shlSszs, SWT.NONE);
		group.setText("工作区");
		group.setBounds(363, 349, 489, 176);
		
		Label label_6 = new Label(group, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_6.setForeground(SWTResourceManager.getColor(0, 0, 0));
		label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		label_6.setBounds(10, 103, 149, 73);
		
		text_2 = new Text(group, SWT.CENTER);
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 18, SWT.NORMAL));
		text_2.setEnabled(false);
		text_2.setBackground(SWTResourceManager.getColor(255, 255, 255));
		text_2.setBounds(200, 124, 96, 34);
		
		Button button_2 = new Button(group, SWT.NONE);
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
		label_20.setBounds(0, 351, 61, 17);
		
		table_1 = new Table(shlSszs, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		table_1.setBounds(0, 373, 357, 152);
		
		TableColumn tableColumn_7 = new TableColumn(table_1, SWT.NONE);
		tableColumn_7.setWidth(30);
		tableColumn_7.setText("ID");
		
		TableColumn tableColumn_8 = new TableColumn(table_1, SWT.NONE);
		tableColumn_8.setWidth(128);
		tableColumn_8.setText("帐号");
		
		TableColumn tableColumn_9 = new TableColumn(table_1, SWT.NONE);
		tableColumn_9.setWidth(114);
		tableColumn_9.setText("密码");
		
		TableColumn tableColumn_11 = new TableColumn(table_1, SWT.NONE);
		tableColumn_11.setWidth(67);
		tableColumn_11.setText("使用次数");
		
		btnDenglu = new Button(shlSszs, SWT.NONE);
		btnDenglu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> message = new ArrayList<String>();
				message.add(text_1.getText());
				message.add(text_3.getText());
				message.add(String.valueOf(button.getSelection()));
				message.add(String.valueOf(button_1.getSelection()));
				
				Engine.getInstance().fire(EngineMessage.IM_USERLOGIN, message);
			}
		});
		btnDenglu.setText("登录");
		btnDenglu.setBounds(707, 278, 145, 65);
		
		label_4 = new Label(shlSszs, SWT.NONE);
		label_4.setText("密码:");
		label_4.setBounds(489, 304, 43, 17);
		
		button = new Button(shlSszs, SWT.CHECK);
		button.setText("记住密码");
		button.setBounds(489, 326, 69, 17);
		
		button_1 = new Button(shlSszs, SWT.CHECK);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(button_1.getSelection()){
					button.setSelection(true);
				}
			}
		});
		button_1.setText("自动登录");
		button_1.setBounds(614, 326, 69, 17);
		
		text_1 = new Text(shlSszs, SWT.BORDER);
		text_1.setBounds(538, 278, 139, 20);
		
		text_3 = new Text(shlSszs, SWT.BORDER | SWT.PASSWORD);
		text_3.setBounds(538, 302, 139, 20);

	}

	@Override
	public void update(Observable obj, Object arg) {
		//接收来自Engine的消息
		final String msg = (String) arg;
		
		if(msg.startsWith(String.valueOf(EngineMessage.OM_LOGINING))){
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					status.setText("正在登录");
				}				
			});
		}else if(msg.startsWith(String.valueOf(EngineMessage.OM_LOGINED))){
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					status.setText("登录完成: 结果为"+msg.split(":")[1]);
				}				
			});
		}
	}

}
