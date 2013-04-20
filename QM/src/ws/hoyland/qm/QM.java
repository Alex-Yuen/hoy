package ws.hoyland.qm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class QM {

	protected Shell shlQqmail;
	private Table table;
	private Text text_1;
	private Option option;
	private Text text_2;
	private Link link;
	private Link link_4;
	private Label label_3;
	private Label label_4;
	private Link link_2;
	private Button button;
	private Label label_1;
	private Text text;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			QM window = new QM();
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
		shlQqmail.open();
		shlQqmail.layout();
		while (!shlQqmail.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlQqmail = new Shell(Display.getDefault(), SWT.SHELL_TRIM ^ SWT.MAX
				^ SWT.RESIZE);
		shlQqmail.setSize(878, 589);
		shlQqmail.setText("QQMail");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shlQqmail.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlQqmail.setLocation(x, y);
		
		Label label = new Label(shlQqmail, SWT.NONE);
		label.setBounds(10, 10, 164, 17);
		label.setText("帐号列表 (共 0 条):");
		
		table = new Table(shlQqmail, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 33, 474, 320);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnId = new TableColumn(table, SWT.NONE);
		tblclmnId.setWidth(30);
		tblclmnId.setText("ID");
		
		TableColumn tblclmnAccount = new TableColumn(table, SWT.NONE);
		tblclmnAccount.setWidth(67);
		tblclmnAccount.setText("Account");
		
		TableColumn tblclmnPassword = new TableColumn(table, SWT.NONE);
		tblclmnPassword.setWidth(74);
		tblclmnPassword.setText("Password");
		
		link = new Link(shlQqmail, SWT.NONE);
		link.setBounds(447, 10, 36, 17);
		link.setText("<a>导入...</a>");
		
		Label lblNewLabel = new Label(shlQqmail, SWT.BORDER | SWT.WRAP);
		lblNewLabel.setBounds(180, 10, 254, 17);
		
		Button btnCheckButton = new Button(shlQqmail, SWT.CHECK);
		btnCheckButton.setBounds(499, 290, 117, 17);
		btnCheckButton.setText("使用代理发送邮件");
		
		label_3 = new Label(shlQqmail, SWT.NONE);
		label_3.setEnabled(false);
		label_3.setText("导入代理:");
		label_3.setBounds(499, 313, 61, 17);
		
		label_4 = new Label(shlQqmail, SWT.BORDER | SWT.WRAP);
		label_4.setEnabled(false);
		label_4.setBounds(566, 313, 254, 17);
		
		link_2 = new Link(shlQqmail, 0);
		link_2.setEnabled(false);
		link_2.setText("<a>导入...</a>");
		link_2.setBounds(826, 313, 36, 17);
		
		button = new Button(shlQqmail, SWT.CHECK);
		button.setSelection(true);
		button.setText("发送完删除");
		button.setBounds(499, 336, 81, 17);
		
		text_1 = new Text(shlQqmail, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		text_1.setEditable(false);
		text_1.setBounds(499, 36, 363, 248);
		
		Group group = new Group(shlQqmail, SWT.NONE);
		group.setText("工作区");
		group.setBounds(373, 359, 489, 176);
		
		label_1 = new Label(group, SWT.CENTER);
		label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		label_1.setBounds(10, 103, 149, 73);
		
		text = new Text(group, SWT.BORDER | SWT.CENTER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 18, SWT.NORMAL));
		text.setBounds(174, 103, 149, 73);
		
		Button button_1 = new Button(group, SWT.NONE);
		button_1.setText("开始");
		button_1.setBounds(340, 103, 149, 73);
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setBounds(10, 37, 41, 17);
		lblNewLabel_1.setText("代理:");
		
		Label lblMaxOfMax = new Label(group, SWT.NONE);
		lblMaxOfMax.setAlignment(SWT.RIGHT);
		lblMaxOfMax.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblMaxOfMax.setText("0");
		lblMaxOfMax.setBounds(63, 37, 96, 17);
		
		Label label_7 = new Label(group, SWT.NONE);
		label_7.setText("登录:");
		label_7.setBounds(174, 37, 41, 17);
		
		Label lblOfMax = new Label(group, SWT.NONE);
		lblOfMax.setAlignment(SWT.RIGHT);
		lblOfMax.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblOfMax.setText("0:0");
		lblOfMax.setBounds(227, 37, 96, 17);
		
		Label label_9 = new Label(group, SWT.NONE);
		label_9.setText("成功:");
		label_9.setBounds(10, 60, 41, 17);
		
		Label label_10 = new Label(group, SWT.NONE);
		label_10.setText("0");
		label_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_10.setAlignment(SWT.RIGHT);
		label_10.setBounds(63, 60, 96, 17);
		
		Label label_11 = new Label(group, SWT.NONE);
		label_11.setText("失败:");
		label_11.setBounds(174, 60, 41, 17);
		
		Label label_12 = new Label(group, SWT.NONE);
		label_12.setText("0");
		label_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_12.setAlignment(SWT.RIGHT);
		label_12.setBounds(227, 60, 96, 17);
		
		Label label_8 = new Label(group, SWT.NONE);
		label_8.setText("群数:");
		label_8.setBounds(340, 37, 41, 17);
		
		Label label_13 = new Label(group, SWT.NONE);
		label_13.setText("0");
		label_13.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_13.setAlignment(SWT.RIGHT);
		label_13.setBounds(393, 37, 96, 17);
		
		Label label_14 = new Label(group, SWT.NONE);
		label_14.setText("保留:");
		label_14.setBounds(340, 60, 41, 17);
		
		Label label_15 = new Label(group, SWT.NONE);
		label_15.setText("0");
		label_15.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		label_15.setAlignment(SWT.RIGHT);
		label_15.setBounds(393, 60, 96, 17);
		
		Link link_1 = new Link(shlQqmail, SWT.NONE);
		link_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				option.open();
				//System.out.println(e.text);
			}
		});
		link_1.setBounds(809, 337, 53, 17);
		link_1.setText("<a>高级设置</a>");
		
		Link link_3 = new Link(shlQqmail, SWT.NONE);
		link_3.setBounds(10, 541, 852, 17);
		link_3.setText("<a>http://www.hoyland.ws</a>");
		
		Label label_2 = new Label(shlQqmail, SWT.NONE);
		label_2.setText("系统日志:");
		label_2.setBounds(499, 10, 363, 17);
		
		Label label_5 = new Label(shlQqmail, SWT.NONE);
		label_5.setText("模板文件:");
		label_5.setBounds(10, 360, 61, 17);
		
		Label label_6 = new Label(shlQqmail, SWT.BORDER | SWT.WRAP);
		label_6.setBounds(77, 359, 241, 17);
		
		link_4 = new Link(shlQqmail, 0);
		link_4.setText("<a>导入...</a>");
		link_4.setBounds(331, 360, 36, 17);
		
		option = new Option(shlQqmail, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		text_2 = new Text(shlQqmail, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		text_2.setEditable(false);
		text_2.setBounds(10, 383, 357, 152);
	}
}
