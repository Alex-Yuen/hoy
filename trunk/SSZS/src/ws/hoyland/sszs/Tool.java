package ws.hoyland.sszs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TableColumn;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

public class Tool extends Dialog {

	protected Object result;
	protected Shell shell;
	private Table table;
	private Label label;

	private List<String> mails;
	private Button btnl;

	private BufferedWriter output = null;
	private URL url = Tool.class.getClassLoader().getResource("");
	private String xpath = url.getPath();
	private boolean running = false;
	private ThreadPoolExecutor pool;
	
	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public Tool(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		// Engine.getInstance().addObserver(this);
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		// load();
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

	// public void close(){
	// this.shell.setVisible(false);
	// }

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				st();
				// shell.dispose();
				// System.exit(0);
				// Engine.getInstance().deleteObserver(Tool.this);
			}
		});
		// shell.addShellListener(new ShellAdapter() {
		// @Override
		// public void shellClosed(ShellEvent e) {
		// shell.setVisible(false);
		// e.doit = false;
		// }
		// });

		shell.setSize(629, 393);
		shell.setText("申诉结果读取工具");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);

		btnl = new Button(shell, SWT.NONE);
		btnl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				running = !running;
				if (running) {
					btnl.setText("停止(&S)");
					DateFormat format = new java.text.SimpleDateFormat(
							"yyyy年MM月dd日 hh时mm分ss秒");
					String tm = format.format(new Date());

					File fff = new File(xpath + "申诉结果-" + tm + ".txt");
					try {
						if (!fff.exists()) {
							fff.createNewFile();
						}

						output = new BufferedWriter(new FileWriter(fff));
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					int tc = 1;
					int corePoolSize = tc;// minPoolSize
					int maxPoolSize = tc;
					int maxTaskSize = (1024 + 512) * 100 * 40;// 缓冲队列
					long keepAliveTime = 0L;
					TimeUnit unit = TimeUnit.MILLISECONDS;

					BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
							maxTaskSize);
					RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略
					
					// 创建线程池
					pool = new ThreadPoolExecutor(corePoolSize,
							maxPoolSize, keepAliveTime, unit,
							workQueue, handler);

					for (int i = 0; i < mails.size(); i++) {
						try {
							pool.execute(new TS(output, table, mails.get(i)));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				} else {
					btnl.setText("读取(&R)");
					st();
				}
			}
		});
		btnl.setEnabled(false);
		btnl.setText("读取(&R)");
		btnl.setBounds(465, 289, 148, 65);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 33, 422, 321);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(30);
		tableColumn.setText("ID");

		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("帐号");

		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("密码");

		TableColumn tableColumn_5 = new TableColumn(table, SWT.NONE);
		tableColumn_5.setWidth(100);
		tableColumn_5.setText("回执编号");

		TableColumn tableColumn_6 = new TableColumn(table, SWT.NONE);
		tableColumn_6.setWidth(100);
		tableColumn_6.setText("邮箱帐号");

		TableColumn tableColumn_7 = new TableColumn(table, SWT.NONE);
		tableColumn_7.setWidth(100);
		tableColumn_7.setText("邮箱密码");

		TableColumn tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(55);
		tableColumn_3.setText("成功");

		TableColumn tableColumn_4 = new TableColumn(table, SWT.NONE);
		tableColumn_4.setWidth(55);
		tableColumn_4.setText("失败");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 105, 17);
		lblNewLabel.setText("申诉成功列表文件:");

		label = new Label(shell, SWT.BORDER | SWT.WRAP);
		label.setBounds(121, 10, 252, 17);

		Link link = new Link(shell, 0);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					final FileDialog fileDlg = new FileDialog(shell, SWT.OPEN);
					fileDlg.setFilterPath(null);
					fileDlg.setText("选择邮件列表");
					String filePath = fileDlg.open();
					if (filePath != null) {

						mails = new ArrayList<String>();
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						int i = 1;
						while ((line = reader.readLine()) != null) {
							if (!line.equals("")) {
								line = i + "----" + line;
								mails.add(line);

								List<String> lns = new ArrayList<String>();
								lns.addAll(Arrays.asList(line.split("----")));
								lns.add("0");
								lns.add("0");
								// if (lns.size() == 3) {
								// lns.add("0");
								// lns.add("初始化");
								// //line += "----0----初始化";
								// } else {
								// //line += "----初始化";
								// lns.add("初始化");
								// }

								String[] items = new String[lns.size()];
								lns.toArray(items);

								TableItem tableItem = new TableItem(table,
										SWT.NONE);
								tableItem.setText(items);
								table.setSelection(tableItem);
							}
							i++;
						}

						reader.close();
						isr.close();
						is.close();
						if (mails.size() > 0) {
							table.setSelection(table.getItem(0));
							// table.select(0);
							btnl.setEnabled(true);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});
		link.setText("<a>导入...</a>");
		link.setBounds(396, 10, 36, 17);
	}

	private void st() {
		if(pool!=null){
			pool.shutdownNow();
		}
		
		if (output != null) {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class TS implements Runnable {
	private BufferedWriter output;
	private Table table;
	private String mails;
	
	public TS(BufferedWriter output, Table table, String mails){
		this.output = output;
		this.table = table;
		this.mails = mails;
	}

	@Override
	public void run() {
		try {
			int sc = 0;
			int fc = 0;
			final String[] ms = mails.split(
					"----");

			Display.getDefault().asyncExec(
					new Runnable() {
						@Override
						public void run() {
							TableItem tableItem = table.getItem(Integer
									.parseInt(ms[0]) - 1);
							tableItem
									.setForeground(Display
											.getDefault()
											.getSystemColor(
													SWT.COLOR_BLUE));
							// TableItem tableItem =
							// table.getItem(Integer.parseInt(ms[0])-1);
							// tableItem.setText(4,
							// String.valueOf(ffc));
							table.setSelection(tableItem);
						}
					});

			Properties props = new Properties();
			props.put("mail.imap.host", "imap.163.com");
			props.put("mail.imap.auth.plain.disable",
					"true");

			Session session = Session
					.getDefaultInstance(props);
			session.setDebug(false);
			IMAPStore store = (IMAPStore) session
					.getStore("imap");
			store.connect(ms[4], ms[5]);
			IMAPFolder folder = (IMAPFolder) store
					.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);

			// 全部邮件
			Message[] messages = folder.getMessages();

			// boolean seen = true;
			// System.err.println(messages.length);
			for (int k = messages.length - 1; k >= 0; k--) {
				Message message = messages[k];
				// 删除邮件
				// message.setFlag(Flags.Flag.DELETED,true);

				// Flags flags = message.getFlags();
				// if (flags.contains(Flags.Flag.SEEN)){
				// seen = true;
				// } else {
				// seen = false;
				// }

				String ssct = (String) message
						.getContent();
				String rcl = ssct.substring(
						ssct.indexOf("回执编号[") + 5,
						ssct.indexOf("回执编号[") + 15);

				// if(!seen&&message.getSubject().startsWith("申诉结果")){
				if (message.getSubject().startsWith(
						"申诉结果")
						&& ms[3].equals(rcl)) {

					// boolean isold = false;
					// Flags flags = message.getFlags();
					// Flags.Flag[] flag =
					// flags.getSystemFlags();
					//
					// for (int ix = 0; ix< flag.length;
					// ix++) {
					// if (flag[ix] == Flags.Flag.SEEN)
					// {
					// isold = true;
					// break;
					// }
					// }

					if (ssct.contains("申诉成功")) {
						sc++;
						final int fsc = sc;
						// if(!isold){
						// message.setFlag(Flags.Flag.SEEN,
						// true); // 标记为已读
						// update UI
						Display.getDefault().asyncExec(
								new Runnable() {
									@Override
									public void run() {
										TableItem tableItem = table
												.getItem(Integer
														.parseInt(ms[0]) - 1);
										// tableItem.setBackground(new
										// Color(null,
										// 255, 0, 0));
										tableItem
												.setText(
														6,
														String.valueOf(fsc));
										table.setSelection(tableItem);
									}
								});

						String link = ssct.substring(
								ssct.indexOf("<a style=\"color:red\" href=\"") + 27,
								ssct.indexOf("\" target=\"_blank\"><span>点此重新设置密码"));
						//System.err.println(rcl);
						//System.err.println(link);
						// 写文件
						try {
							if(output!=null){
								output.write(ms[1] + "----"
										//+ rcl + "----"
										+ link + "\r\n");
								output.flush();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// break;
					} else if (ssct
							.contains("申诉未能通过审核")) {
						fc++;
						final int ffc = fc;
						// message.setFlag(Flags.Flag.SEEN,
						// true); // 标记为已读
						// update UI

						Display.getDefault().asyncExec(
								new Runnable() {
									@Override
									public void run() {
										TableItem tableItem = table
												.getItem(Integer
														.parseInt(ms[0]) - 1);
										// tableItem.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
										// TableItem
										// tableItem =
										// table.getItem(Integer.parseInt(ms[0])-1);
										tableItem
												.setText(
														7,
														String.valueOf(ffc));
										table.setSelection(tableItem);
									}
								});
					}
					// }
				}
			}
			folder.close(true);
			store.close();

			Display.getDefault().asyncExec(
					new Runnable() {
						@Override
						public void run() {
							TableItem tableItem = table.getItem(Integer
									.parseInt(ms[0]) - 1);
							tableItem
									.setForeground(Display
											.getDefault()
											.getSystemColor(
													SWT.COLOR_BLACK));
							// TableItem tableItem =
							// table.getItem(Integer.parseInt(ms[0])-1);
							// tableItem.setText(4,
							// String.valueOf(ffc));
							table.setSelection(tableItem);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
