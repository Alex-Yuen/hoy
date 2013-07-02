package ws.hoyland.bqm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;

/**
 * TODO:发送TASK, 统计, 界面日志, 文件日志 http://duguyiren3476.iteye.com/blog/1771301
 * 
 * @author Administrator
 * 
 */
public class BQM implements ICallback {

	protected Shell shlBqm;
	private Table table;
	private Text text;
	private Canvas canvas;
	private Spinner spinner;
	private Button button;
	private Label label;
	private List<String> rs; // 收件人列表
	private Map<String, Byte> ss;// 发件人列表
	private Map<String, Byte> sbs;// 主题列表

	//protected String title;
	protected String content;
	private int count; // smtp 数量
	private int sbscount; //主题数量
	private byte status = 0;
	private int sc = 0; // 成功
	private int fc = 0; // 失败

	private ThreadPoolExecutor pool; // 线程池
	private Browser browser;
	private Spinner spinner_1;
	private Spinner spinner_2;
	private StringBuffer bsc;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BQM window = new BQM();
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
		shlBqm.open();
		shlBqm.layout();
		while (!shlBqm.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlBqm = new Shell(Display.getDefault(), SWT.SHELL_TRIM ^ SWT.MAX
				^ SWT.RESIZE);
		shlBqm.setTouchEnabled(true);
		shlBqm.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				System.exit(0);
			}
		});
		shlBqm.setSize(769, 566);
		shlBqm.setText("BQM 0.0.4");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shlBqm.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlBqm.setLocation(x, y);

		InputStream is = BQM.class.getClassLoader().getResourceAsStream(
				"mail.png");
		Image image = new Image(Display.getDefault(), is);
		shlBqm.setImage(image);
		shlBqm.setLayout(null);

		label = new Label(shlBqm, SWT.NONE);
		label.setText("收件人 (共 0 个):");
		label.setBounds(10, 10, 164, 17);

		Link link = new Link(shlBqm, 0);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				final FileDialog fileDlg = new FileDialog(shlBqm, SWT.OPEN);
				fileDlg.setFilterPath(System.getProperty("user.home"));
				fileDlg.setFilterNames(new String[] { "收件人列表文件"});
				fileDlg.setFilterExtensions(new String[]{"*.txt"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("导入收件人");
				
				final String filePath = fileDlg.open();
				if (filePath != null) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								table.removeAll();
								rs = new ArrayList<String>();
								File ipf = new File(filePath);
								FileInputStream is = new FileInputStream(ipf);
								InputStreamReader isr = new InputStreamReader(
										is, Charset.forName("UTF-8"));
								BufferedReader reader = new BufferedReader(isr);
								String line = null;
								int i = 1;
								while ((line = reader.readLine()) != null) {
									// line = line.trim();
									if (!line.equals("")
											&& !line.contains("#FHW#")) {
										rs.add(line);

										line = i + "#FHW#" + line;
										List<String> lns = new ArrayList<String>();
										lns.addAll(Arrays.asList(line
												.split("#FHW#")));
										lns.add("初始化");

										final String[] items = new String[lns
												.size()];
										lns.toArray(items);

										Display.getDefault().asyncExec(
												new Runnable() {
													@Override
													public void run() {
														TableItem tableItem = new TableItem(
																table, SWT.NONE);
														tableItem
																.setText(items);
													}
												});
										i++;
									}
								}
								label.setText("收件人 (共 " + rs.size() + " 个):");
								reader.close();
								isr.close();
								is.close();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							check();
						}
					});
				}
			}
		});
		link.setText("<a>导入...</a>");
		link.setBounds(224, 10, 36, 17);

		table = new Table(shlBqm, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 35, 250, 270);

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(30);
		tableColumn.setText("ID");

		TableColumn tableColumn_1 = new TableColumn(table, SWT.LEFT);
		tableColumn_1.setWidth(120);
		tableColumn_1.setText("帐号");

		TableColumn tableColumn_2 = new TableColumn(table, SWT.CENTER);
		tableColumn_2.setWidth(75);
		tableColumn_2.setText("状态");

		Label label_1 = new Label(shlBqm, SWT.NONE);
		label_1.setText("系统日志:");
		label_1.setBounds(10, 311, 250, 17);

		text = new Text(shlBqm, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL);
		text.setEditable(false);
		text.setBounds(10, 334, 250, 180);

		Label label_2 = new Label(shlBqm, SWT.NONE);
		label_2.setText("模板文件:");
		label_2.setBounds(265, 10, 61, 17);

		Link link_1 = new Link(shlBqm, 0);
		link_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(shlBqm, SWT.OPEN);
				fileDlg.setFilterPath(System.getProperty("user.home"));
				fileDlg.setFilterNames(new String[] { "模板文件"});
				fileDlg.setFilterExtensions(new String[]{"*.html"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("导入模板文件");
				String filePath = fileDlg.open();
				if (filePath != null) {
					try {
						boolean tf = false;
						content = "";
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is,
								Charset.forName("UTF-8"));

						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						bsc = new StringBuffer();
						while ((line = reader.readLine()) != null) {
							// line = line.trim();							
							bsc.append(line + "\r\n");
							if (!tf) {
								// title = line;
								// title = "{*}";
								content += line + "\r\n";
								tf = true;
							} else {
								content += line + "\r\n";
							}
						}
						browser.setText(bsc.toString());
						browser.addProgressListener(new ProgressListener() {

							@Override
							public void changed(ProgressEvent event) {
								// TODO Auto-generated method stub

							}

							@Override
							public void completed(ProgressEvent event) {
								browser.forceFocus();

								Event evt = new Event();

								for (int i = 0; i < 3; i++) {
									evt.type = SWT.KeyDown;
									evt.keyCode = SWT.CTRL;
									browser.getDisplay().post(evt);

									evt.type = SWT.KeyDown;
									evt.keyCode = 16777261;
									browser.getDisplay().post(evt);

									evt.type = SWT.KeyUp;
									evt.keyCode = SWT.CTRL;
									browser.getDisplay().post(evt);

									evt.type = SWT.KeyUp;
									evt.keyCode = 16777261;
									browser.getDisplay().post(evt);
								}
							}
						});

						reader.close();
						isr.close();
						is.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					check();
				}
			}
		});
		link_1.setText("<a>导入...</a>");
		link_1.setBounds(719, 10, 36, 17);

		button = new Button(shlBqm, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (status == 0) {// 当前停止
					button.setText("停止");
					button.setEnabled(true);
					status = 1;

					fc = 0;
					sc = 0;

					int tc = Integer.parseInt(spinner.getText()); // threads
																	// count
					int corePoolSize = tc;// minPoolSize
					int maxPoolSize = tc;

					int maxTaskSize = 1024 * 10 * 5;// 缓冲队列
					long keepAliveTime = 0L;
					TimeUnit unit = TimeUnit.MILLISECONDS;

					// 任务队列
					BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
							maxTaskSize);
					// 饱和处理策略
					RejectedExecutionHandler handler = new AbortPolicy();
					// 创建线程池
					pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
							keepAliveTime, unit, workQueue, handler);

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < table.getItemCount(); i++) {
								try {
									Task task = null;
									task = new Task(table.getItem(i), 
											ss,
											sbs,
											content,
											BQM.this);
									task.set(Integer.parseInt(spinner_1.getText()), Integer.parseInt(spinner_2.getText()));
									pool.execute(task);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});

				} else if (status == 1) {// 当前运行
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < table.getItemCount(); i++) {
								table.getItem(i).setText(2, "初始化");
							}
						}
					});
					button.setText("开始");
					button.setEnabled(true);
					status = 0;

					pool.shutdownNow();
				}
			}
		});
		button.setText("开始");
		button.setEnabled(false);
		button.setBounds(531, 441, 224, 73);

		Label lblHttpwwwhoylandws = new Label(shlBqm, SWT.BORDER);
		lblHttpwwwhoylandws.setText(" http://www.hoyland.ws");
		lblHttpwwwhoylandws.setBounds(0, 520, 763, 17);

		Group grpOption = new Group(shlBqm, SWT.NONE);
		grpOption.setText("设置");
		grpOption.setBounds(531, 311, 224, 124);

		Label label_4 = new Label(grpOption, SWT.NONE);
		label_4.setText("线程数(1~10):");
		label_4.setBounds(10, 26, 89, 17);

		spinner = new Spinner(grpOption, SWT.BORDER);
		spinner.setMaximum(10);
		spinner.setMinimum(1);
		spinner.setSelection(1);
		spinner.setBounds(105, 23, 45, 23);
		
		spinner_1 = new Spinner(grpOption, SWT.BORDER);
		spinner_1.setMaximum(10);
		spinner_1.setMinimum(1);
		spinner_1.setSelection(3);
		spinner_1.setBounds(105, 49, 45, 23);
		
		spinner_2 = new Spinner(grpOption, SWT.BORDER);
		spinner_2.setMaximum(10);
		spinner_2.setMinimum(1);
		spinner_2.setSelection(3);
		spinner_2.setBounds(105, 75, 45, 23);
		
		Label lblSmtp = new Label(grpOption, SWT.NONE);
		lblSmtp.setText("SMTP复用:");
		lblSmtp.setBounds(10, 52, 89, 17);
		
		Label label_5 = new Label(grpOption, SWT.NONE);
		label_5.setText("主题复用:");
		label_5.setBounds(10, 78, 89, 17);

		Label lblNewLabel = new Label(shlBqm, SWT.NONE);
		lblNewLabel.setBounds(266, 311, 71, 17);
		lblNewLabel.setText("状态:");

		Link link_2 = new Link(shlBqm, 0);
		link_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(shlBqm, SWT.OPEN);
				fileDlg.setFilterPath(System.getProperty("user.home"));
				fileDlg.setFilterNames(new String[] { "SMTP帐号文件"});
				fileDlg.setFilterExtensions(new String[]{"*.txt"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("导入SMTP帐号");
				String filePath = fileDlg.open();
				if (filePath != null) {
					try {
						ss = new HashMap<String, Byte>();
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is,
								Charset.forName("UTF-8"));

						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						while ((line = reader.readLine()) != null) {
							if(!line.equals("")){
								ss.put(line, (byte) 0x00);
							}
						}
						count = ss.size();
						canvas.redraw();
						// System.out.println(count);
						reader.close();
						isr.close();
						is.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					check();
				}
			}
		});
		link_2.setText("<a>导入SMTP...</a>");
		link_2.setBounds(454, 311, 71, 17);

		canvas = new Canvas(shlBqm, SWT.NONE);
		canvas.setBounds(266, 334, 257, 180);

		canvas.setBackground(Display.getDefault()
				.getSystemColor(SWT.COLOR_GRAY));

		browser = new Browser(shlBqm, SWT.BORDER);
		browser.setJavascriptEnabled(false);
		browser.setBounds(266, 35, 487, 270);

		Link link_3 = new Link(shlBqm, 0);
		link_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(shlBqm, SWT.OPEN);
				fileDlg.setFilterPath(System.getProperty("user.home"));
				fileDlg.setFilterNames(new String[] { "主题文件"});
				fileDlg.setFilterExtensions(new String[]{"*.txt"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("导入主题文件");
				String filePath = fileDlg.open();
				if (filePath != null) {
					try {
						sbs = new HashMap<String, Byte>();
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is,
								Charset.forName("UTF-8"));

						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						while ((line = reader.readLine()) != null) {
							if(!line.equals("")){
								sbs.put(line, (byte) 0x00);
							}
						}
						sbscount = sbs.size();
						canvas.redraw();

						reader.close();
						isr.close();
						is.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					check();
				}
			}
		});
		link_3.setText("<a>导入主题...</a>");
		link_3.setBounds(387, 311, 61, 17);

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawText("总数: " + String.valueOf(count), 0, 0);
				e.gc.drawText("成功: " + String.valueOf(sc), 0, 30);
				e.gc.drawText("失败: " + String.valueOf(fc), 0, 60);
				
				e.gc.drawText("主题总数: " + String.valueOf(sbscount), 0, 120);
			}
		});
	}

	private void check() {
		if (bsc!=null&&bsc.length()>0 && rs != null && rs.size() > 0
				&& ss != null && ss.size() > 0 &&  sbs != null && sbs.size() > 0) {
			button.setEnabled(true);
		} else {
			button.setEnabled(false);
		}

	}

	@Override
	public void call(int key, int value) {
		switch (key) {
		case ICallback.SUCC:
			sc++;
			this.canvas.redraw();
			break;
		case ICallback.FAIL:
			fc++;
			this.canvas.redraw();
			break;
		default:
			break;
		}

		if((sc+fc)==rs.size()){
			button.setText("完成");
		}
		return;

	}
}
