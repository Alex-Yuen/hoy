package ws.hoyland.qt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;

public class QT {

	protected Shell shlQt;
	private Text txtC;
	private Button button_1;
	private Label label_3;
	private Button btnNewButton;
	private Spinner spinner;

	private ThreadPoolExecutor pool = null;
	private List<String> ns = null;
	private List<String> nsx = null;
	protected boolean flag = false;
	private ProgressBar progressBar;
	private Label lblNewLabel_1;
	private Label lblNewLabel_5;
	private Label lblNewLabel_6;
	private Label label_2;
	private Label lblNewLabel_9;
	private Label label_6;
	private Label label_7;
	private Label lblNewLabel_13;

	private long startTime = 0;
	private List<String> sc = null;
	private List<String> fc = null;
	private List<String> oc = null;
	private List<String> mc = null;
	private List<String> sc0 = null;
	private List<String> sc1 = null;
	private List<String> proxies = null;
	private List<String> tokens = null;
	private String ctk = null;
	private int cctk = 0;
	private int pc = 0;
	private int nc = 0;
	private Text text_1;
	private Button button;
	private Label label_4;
	private Label label_5;
	// private SimpleDateFormat formatter = null;
	private String ipn = null;
	private String[] fn = {"正确.txt", "错误.txt", "过多.txt", "异常.txt", "正确-二代.txt", "正确-无保一代.txt"};
	BufferedWriter[] output = new BufferedWriter[6];
	BufferedWriter bw = null;
	private URL url = QT.class.getClassLoader().getResource("");
	private String path = url.getPath();
	private File fff = null;
	private TimerTask timerTask;
	private Spinner spinner_1;
	private int mctk = 0;
	private Label lblNewLabel_2;
	private int ctimes = 0;
	private int mtimes = 0;
	private Spinner spinner_2;
	private Text text;
	private String ctk2 = null;
	private Button btnCheckButton;
	private boolean up;
	private boolean dna;
	private Button btnDna;
	private Listener listener;
	
	public QT() {
		// formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式
		this.tokens = new ArrayList<String>();
		url = QT.class.getClassLoader().getResource("");
		path = url.getPath();
		listener = new Listener(){

			@Override
			public void handleEvent(Event event) {
				//System.out.println(event.keyCode);
				if ((event.stateMask == SWT.CTRL) && (event.keyCode == SWT.CR||event.keyCode==16777296)){
					//dnaButton.
					if(btnDna.isVisible()){
						btnDna.setVisible(false);
					}else{
						btnDna.setVisible(true);
					}
				}
			}			
		};
	}

	public void setCTK(String ctk){
		this.ctk = ctk;
		this.cctk = 0;
	}
		
	public String getCTK(){
		if(cctk<=mctk&&this.ctk!=null){
			cctk++;
			return this.ctk;
		}else{
			System.out.println(cctk+":"+mctk);
			return null;
		}
	}
	
	public String getCTK2(){
		return this.ctk2;
	}
	
//	public int getCCTK(){
//		return this.cctk;
//	}
		
	public boolean getFlag() {
		return this.flag;
	}

	private String ct(long time) {
		long hour = time / (60 * 60 * 1000);
		long minute = (time - hour * 60 * 60 * 1000) / (60 * 1000);
		long second = (time - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
		if (second >= 60) {
			second = second % 60;
			minute += second / 60;
		}
		if (minute >= 60) {
			minute = minute % 60;
			hour += minute / 60;
		}
		String sh = " ";
		String sm = " ";
		String ss = " ";
		if (hour < 10) {
			sh = "0" + String.valueOf(hour);
		} else {
			sh = String.valueOf(hour);
		}
		if (minute < 10) {
			sm = "0" + String.valueOf(minute);
		} else {
			sm = String.valueOf(minute);
		}
		if (second < 10) {
			ss = "0" + String.valueOf(second);
		} else {
			ss = String.valueOf(second);
		}
		return (sh + ":" + sm + ":" + ss);
	}

//	public void uppx() {
//		lblNewLabel_9.setText(proxies.size() + "/" + pc);
//		lblNewLabel_1.setText(ct(System.currentTimeMillis() - this.startTime));
//	}
	
	public void up(String line, int err, int isdna) {
		
		if (err == 0) {
			// this.button_2.setEnabled(true);
			if(isdna==-1){
				if(output[0]==null){
					fff = new File(path + ipn + "-" + ctimes + "-" + fn[0]);
					try {
						if (!fff.exists()) {
							fff.createNewFile();
						}
						
						output[0] = new BufferedWriter(
								new FileWriter(fff));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				bw = output[0];
				sc.add(line);
			}else if(isdna==1){
				if(output[4]==null){
					fff = new File(path + ipn + "-" + ctimes + "-" + fn[4]);
					try {
						if (!fff.exists()) {
							fff.createNewFile();
						}
						
						output[4] = new BufferedWriter(
								new FileWriter(fff));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				bw = output[4];
				sc0.add(line);
			}else if(isdna==0){
				if(output[5]==null){
					fff = new File(path + ipn + "-" + ctimes + "-" + fn[5]);
					try {
						if (!fff.exists()) {
							fff.createNewFile();
						}
						
						output[5] = new BufferedWriter(
								new FileWriter(fff));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				bw = output[5];
				sc1.add(line);
			}
		} else if (err == 132) {
			// this.button_3.setEnabled(true);
			if(output[1]==null){
				fff = new File(path + ipn + "-" + fn[1]);
				try {
					if (!fff.exists()) {
						fff.createNewFile();
					}
					
					output[1] = new BufferedWriter(
							new FileWriter(fff));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			bw = output[1];
			fc.add(line);
		} else if (err == 136) {
			// this.button_3.setEnabled(true);
			if(output[2]==null){
				fff = new File(path + ipn + "-" + fn[2]);
				try {
					if (!fff.exists()) {
						fff.createNewFile();
					}
					
					output[2] = new BufferedWriter(
							new FileWriter(fff));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			bw = output[2];
			mc.add(line);
		} else {
			// this.button_4.setEnabled(true);
			if(output[3]==null){
				fff = new File(path + ipn + "-" + fn[3]);
				try {
					if (!fff.exists()) {
						fff.createNewFile();
					}
					
					output[3] = new BufferedWriter(
							new FileWriter(fff));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			bw = output[3];
			oc.add(line);
		}
		nsx.remove(line);
		//append?
		try{
			bw.write(line + "\r\n");
			bw.flush();
			//bw.close();
		}catch(Exception ex){

//			System.out.println(isdna);
//			System.out.println(err);
//			System.out.println(fff.getPath());
			ex.printStackTrace();
		}
		
//		int total = sc.size() + fc.size() + mc.size() + oc.size();
//		
//		if (total == progressBar.getMaximum()) {
//			//
//			// lblNewLabel_1.setText(String.valueOf(System.currentTimeMillis()-this.startTime)+" ms");
//			button_1.setText("完成");
//			if(timerTask!=null){
//				timerTask.cancel();
//			}
//		}
		//System.err.println(pool);
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			QT window = new QT();
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
		btnDna.setVisible(false);
		display.addFilter(SWT.KeyDown, listener);
		shlQt.open();
		shlQt.layout();
		while (!shlQt.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlQt = new Shell(Display.getDefault(), SWT.SHELL_TRIM ^ SWT.MAX
				^ SWT.RESIZE);
		shlQt.addShellListener(new ShellAdapter() {
			@Override
			public void shellActivated(ShellEvent e) {
				try {
					//
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void shellClosed(ShellEvent e) {
				if (pool != null) {
					pool.shutdownNow();
				}

				for (int i=0; i < output.length; i++) {
					try{
						if(output[i]!=null){
							output[i].close();
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				Display.getDefault().removeFilter(SWT.KeyDown, listener);
				System.exit(0);
			}
		});
		shlQt.setToolTipText("");
		shlQt.setSize(567, 253);
		shlQt.setText("QT");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shlQt.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlQt.setLocation(x, y);

		txtC = new Text(shlQt, SWT.BORDER);
		txtC.setEditable(false);
		txtC.setText("请指定文件，导入帐号");
		txtC.setBounds(245, 7, 188, 23);

		button_1 = new Button(shlQt, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ("开始".endsWith(button_1.getText())) {
					flag = true;

					// button_2.setEnabled(false);
					// button_3.setEnabled(false);
					// button_4.setEnabled(false);

					sc0 = new ArrayList<String>();
					sc1 = new ArrayList<String>();
					sc = new ArrayList<String>();
					fc = new ArrayList<String>();
					mc = new ArrayList<String>();
					oc = new ArrayList<String>();
					nsx =  new ArrayList<String>(ns);
					
					mctk = Integer.parseInt(spinner_1.getText());
					if(dna){
						lblNewLabel_5.setText(String.valueOf(sc0.size()+sc1.size()));
					}else{
						lblNewLabel_5.setText(String.valueOf(sc.size()));
					}
					lblNewLabel_6.setText(String.valueOf(fc.size()));
					label_5.setText(String.valueOf(mc.size()));
					label_2.setText(String.valueOf(oc.size()));
					lblNewLabel_1.setText("00:00:00");
					progressBar.setSelection(0);
					ctimes = 1;
					mtimes = Integer.parseInt(spinner_2.getText());
					ctk2 = text.getText();
					up = btnCheckButton.getSelection();
					dna = btnDna.getSelection();
					
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							// 线程池 数据库连接池 可联系起来
							int corePoolSize = 512;// minPoolSize
							int maxPoolSize = 1024;
							int maxTaskSize = (1024 + 512) * 100 * 40;// 缓冲队列
							long keepAliveTime = 0L;
							TimeUnit unit = TimeUnit.MILLISECONDS;
							corePoolSize = Integer.parseInt(spinner.getText());
							maxPoolSize = Integer.parseInt(spinner.getText()) * 2;// 最大同时执行的线程
							// maxTaskSize = maxPoolSize;
							// System.out.println(maxTaskSize);
							// 任务队列
							BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(
									maxTaskSize);
							// 饱和处理策略
							RejectedExecutionHandler handler = new AbortPolicy();
							// 创建线程池
							pool = new ThreadPoolExecutor(corePoolSize,
									maxPoolSize, keepAliveTime, unit,
									workQueue, handler);

							startTime = System.currentTimeMillis();
							// System.out.println(ns.size());
							for (int i = 0; i < ns.size(); i++) {
								// if(j==proxy.size()){//代理
								// j=0;
								// }
								// String px = proxy.get(j);
								String[] qp = ns.get(i).split("----");
								// if(i==63636){
								// System.out.println(ns.get(i));
								// System.out.println(ns.get(i+1));
								// System.out.println(ns.get(i+2));
								// }
								try {
									if (qp.length == 2) {
										Task task = new Task(pool, proxies, tokens,
												QT.this, qp[0],
												qp[1]);
										pool.execute(task);
									}
								} catch (ArrayIndexOutOfBoundsException exx) {
									System.out.println(i + ":" + ns.get(i));
								}
							}
							//final boolean up = useProxy();
						    timerTask = new TimerTask() { 					             
					            @Override 
					            public void run() { 
					                Display.getDefault().asyncExec(new Runnable() { 					                     
					                    @Override 
					                    public void run() { 
					                		int total = 0;
					                		if(dna){
					                			total = sc0.size() + sc1.size() + fc.size() + mc.size() + oc.size();
					                		}else{
					                			total = sc.size() + fc.size() + mc.size() + oc.size();
					                		}
					                		if(up){
					                			lblNewLabel_9.setText(proxies.size() + "/" + pc);
					                		}
					                		//label_7.setText(String.valueOf(tokens.size()));
					                		if(ctk!=null){
					                			label_7.setText(ctk);
					                		}else{
					                			label_7.setText("空");
					                		}
					                		progressBar.setSelection(total);
					                		if(dna){
					                			lblNewLabel_5.setText(String.valueOf(sc0.size()+sc1.size()));
					                		}else{
					                			lblNewLabel_5.setText(String.valueOf(sc.size()));
					                		}
					                		lblNewLabel_6.setText(String.valueOf(fc.size()));
					                		label_5.setText(String.valueOf(mc.size()));
					                		label_2.setText(String.valueOf(oc.size()));
					                		label_6.setText(total + "/" + (nc - total));
					                		// System.out.println(System.currentTimeMillis()-this.startTime);
					                		lblNewLabel_1.setText(ct(System.currentTimeMillis() - startTime));
					                		lblNewLabel_13.setText(ctimes+" of");
					                		//System.out.println(pool);
					                		
					                		if(total==progressBar.getMaximum()){
					                			if(ctimes==mtimes){
						                			button_1.setText("完成");
						                			timerTask.cancel();
					                			}else{
					                				try{
						                				output[0].flush();
						                				output[0].close();
						                				output[0] = null;
					                				}catch(Exception exx){
					                					exx.printStackTrace();
					                				}
					                				ctimes++;
					                				ns = new ArrayList<String>(sc);
					                				sc.clear();
//					                				progressBar.setMaximum(ns.size());
//					                				progressBar.setSelection(0);
					                				for (int i = 0; i < ns.size(); i++) {
					    								// if(j==proxy.size()){//代理
					    								// j=0;
					    								// }
					    								// String px = proxy.get(j);
					    								String[] qp = ns.get(i).split("----");
					    								// if(i==63636){
					    								// System.out.println(ns.get(i));
					    								// System.out.println(ns.get(i+1));
					    								// System.out.println(ns.get(i+2));
					    								// }
					    								try {
					    									if (qp.length == 2) {
					    										Task task = new Task(pool, proxies, tokens,
					    												QT.this, qp[0],
					    												qp[1]);
					    										pool.execute(task);
					    									}
					    								} catch (ArrayIndexOutOfBoundsException exx) {
					    									System.out.println(i + ":" + ns.get(i));
					    								}
					    							}
					                			}
					                		}else if(!flag){
					                			timerTask.cancel();
					                		}
					                    } 
					                });  
					            } 
					        }; 
							
					        Timer timer = new Timer();
					        timer.schedule(timerTask, 0, 1000);
						}

					});

					// Display.getCurrent().syncExec(new Runnable(){
					// public void run(){
					// while(flag){
					// try{
					// if(pool.awaitTermination(2, TimeUnit.SECONDS)){
					// System.out.println("OK");
					// break;
					// }
					// }catch(Exception e){
					// e.printStackTrace();
					// }
					// }
					// }
					// });
					progressBar.setMaximum(ns.size());
					btnNewButton.setEnabled(false);
					button.setEnabled(false);
					button_1.setText("结束");
				} else {
					shutdown();
				}
			}
		});
		button_1.setEnabled(false);
		button_1.setText("开始");
		button_1.setBounds(327, 127, 228, 63);

		Label label = new Label(shlQt, SWT.NONE);
		label.setBounds(10, 71, 127, 17);
		label.setText("线程设置 (512~1024):");

		Label lblNewLabel_3 = new Label(shlQt, SWT.NONE);
		lblNewLabel_3.setBounds(10, 104, 61, 17);
		lblNewLabel_3.setText("密码正确:");

		Label lblNewLabel_4 = new Label(shlQt, SWT.NONE);
		lblNewLabel_4.setBounds(10, 138, 61, 17);
		lblNewLabel_4.setText("密码错误:");

		lblNewLabel_5 = new Label(shlQt, SWT.NONE);
		lblNewLabel_5.setBounds(77, 104, 61, 17);
		lblNewLabel_5.setText("0");

		lblNewLabel_6 = new Label(shlQt, SWT.NONE);
		lblNewLabel_6.setBounds(77, 138, 61, 17);
		lblNewLabel_6.setText("0");

		Label label_1 = new Label(shlQt, SWT.NONE);
		label_1.setBounds(10, 10, 61, 17);
		label_1.setText("导入帐号:");

		btnNewButton = new Button(shlQt, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(shlQt, SWT.OPEN);
				// fileDlg.setFilterExtensions(new String[]{"*.torrent"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("选择号码文件");
				String filePath = fileDlg.open();
				if (filePath != null) {
					try {
						ns = new ArrayList<String>();
						ipn = fileDlg.getFileName();
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						while ((line = reader.readLine()) != null) {
							line = line.trim();
							if (!line.equals("")) {
								ns.add(line);
							}
							// System.out.println(line);
						}
						nc = ns.size();
						reader.close();
						isr.close();
						is.close();
						// System.out.println(ns.size());
						if (ns.size() > 0) {
							label_3.setText("共 " + ns.size() + " 条");
							label_6.setText("0/" + nc);
							if ((btnCheckButton.getSelection()&&proxies != null && proxies.size() > 0)||(!btnCheckButton.getSelection())) {
								button_1.setEnabled(true);
							}
							//
						}
						txtC.setText(filePath);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// System.out.println("Path:"+filePath);
				}
			}
		});
		btnNewButton.setBounds(452, 5, 103, 27);
		btnNewButton.setText("导入帐号");

		Label lblNewLabel_8 = new Label(shlQt, SWT.NONE);
		lblNewLabel_8.setBounds(163, 138, 61, 17);
		lblNewLabel_8.setText("未知错误:");

		progressBar = new ProgressBar(shlQt, SWT.SMOOTH);
		progressBar.setBounds(10, 196, 545, 26);

		spinner = new Spinner(shlQt, SWT.BORDER);
		spinner.setMaximum(1024);
		spinner.setMinimum(512);
		spinner.setSelection(512);
		spinner.setBounds(143, 68, 66, 23);

		label_2 = new Label(shlQt, SWT.NONE);
		label_2.setBounds(249, 138, 74, 17);
		label_2.setText("0");

		label_3 = new Label(shlQt, SWT.NONE);
		label_3.setBounds(77, 10, 104, 17);
		label_3.setText("共 0 条");

		lblNewLabel_1 = new Label(shlQt, SWT.NONE);
		lblNewLabel_1.setBounds(249, 173, 61, 17);
		lblNewLabel_1.setText("00:00:00");

		Label lblNewLabel_7 = new Label(shlQt, SWT.NONE);
		lblNewLabel_7.setBounds(10, 39, 61, 17);
		lblNewLabel_7.setText("代理数量:");

		lblNewLabel_9 = new Label(shlQt, SWT.NONE);
		lblNewLabel_9.setBounds(77, 39, 103, 17);
		lblNewLabel_9.setText("0/0");

		text_1 = new Text(shlQt, SWT.BORDER);
		text_1.setEnabled(false);
		text_1.setEditable(false);
		text_1.setText("请指定文件，导入代理");
		text_1.setBounds(245, 36, 188, 23);

		button = new Button(shlQt, SWT.NONE);
		button.setEnabled(false);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(shlQt, SWT.OPEN);
				// fileDlg.setFilterExtensions(new String[]{"*.torrent"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("选择代理文件");
				String filePath = fileDlg.open();
				if (filePath != null) {
					try {
						proxies = new ArrayList<String>();
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						while ((line = reader.readLine()) != null) {
							line = line.trim();
							if (!line.equals("")) {
								proxies.add(line);
							}
							// System.out.println(line);
						}
						pc = proxies.size();

						reader.close();
						isr.close();
						is.close();
						// System.out.println(ns.size());
						if (pc > 0) {
							lblNewLabel_9.setText(proxies.size() + "/" + pc);
							if (ns != null && ns.size() > 0) {
								button_1.setEnabled(true);
							}
						}
						text_1.setText(filePath);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// System.out.println("Path:"+filePath);
				}
			}
		});
		button.setText("导入代理");
		button.setBounds(452, 34, 103, 27);

		label_4 = new Label(shlQt, SWT.NONE);
		label_4.setText("次数过多:");
		label_4.setBounds(163, 104, 61, 17);

		label_5 = new Label(shlQt, SWT.NONE);
		label_5.setText("0");
		label_5.setBounds(249, 104, 74, 17);

		Label lblNewLabel_10 = new Label(shlQt, SWT.NONE);
		lblNewLabel_10.setBounds(10, 172, 61, 17);
		lblNewLabel_10.setText("处理进度:");

		label_6 = new Label(shlQt, SWT.NONE);
		label_6.setText("0/0");
		label_6.setBounds(77, 172, 80, 17);
		
		Label lblNewLabel = new Label(shlQt, SWT.NONE);
		lblNewLabel.setBounds(327, 104, 61, 17);
		lblNewLabel.setText("当前令牌:");
		
		label_7 = new Label(shlQt, SWT.NONE);
		label_7.setText("空");
		label_7.setBounds(307, 81, 0, 0);
		
		Label lblNewLabel_11 = new Label(shlQt, SWT.NONE);
		lblNewLabel_11.setBounds(225, 71, 61, 17);
		lblNewLabel_11.setText("令牌复用:");
		
		spinner_1 = new Spinner(shlQt, SWT.BORDER);
		spinner_1.setMaximum(10000);
		spinner_1.setMinimum(1);
		spinner_1.setSelection(10);
		spinner_1.setBounds(297, 68, 66, 23);
		
		Label lblNewLabel_12 = new Label(shlQt, SWT.NONE);
		lblNewLabel_12.setBounds(380, 71, 96, 17);
		lblNewLabel_12.setText("循环次数(1~20):");
		
		spinner_2 = new Spinner(shlQt, SWT.BORDER);
		spinner_2.setMaximum(20);
		spinner_2.setMinimum(1);
		spinner_2.setSelection(1);
		spinner_2.setBounds(516, 68, 39, 23);
		
		lblNewLabel_13 = new Label(shlQt, SWT.NONE);
		lblNewLabel_13.setAlignment(SWT.RIGHT);
		lblNewLabel_13.setBounds(471, 71, 39, 17);
		lblNewLabel_13.setText("0 of ");
		
		lblNewLabel_2 = new Label(shlQt, SWT.NONE);
		lblNewLabel_2.setBounds(163, 173, 61, 17);
		lblNewLabel_2.setText("耗时:");
		
		text = new Text(shlQt, SWT.BORDER);
		text.setText("7703431606604520");
		text.setBounds(390, 98, 161, 23);
		
		btnCheckButton = new Button(shlQt, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnCheckButton.getSelection()){
					text_1.setEnabled(true);
					button.setEnabled(true);
					if(proxies != null && proxies.size() > 0&&ns!=null&&ns.size()>0){
						button_1.setEnabled(true);
					}else{
						button_1.setEnabled(false);
					}
				}else{
					text_1.setEnabled(false);
					button.setEnabled(false);
					if(ns!=null&&ns.size()>0){
						button_1.setEnabled(true);
					}else{
						button_1.setEnabled(false);
					}
				}
			}
		});
		btnCheckButton.setBounds(188, 39, 52, 17);
		btnCheckButton.setText("使用");
		
		btnDna = new Button(shlQt, SWT.CHECK);
		btnDna.setBounds(188, 10, 52, 17);
		btnDna.setText("dna");

	}

	public void shutdown() {
		pool.shutdownNow();
		//proxies.clear();
		flag = false;
		btnNewButton.setEnabled(true);
		button.setEnabled(true);
		setCTK(null);
		mctk = 0;
		button_1.setText("开始");
		
		fff = new File(path + ipn + "-剩余.txt" );
		try {
			if (!fff.exists()) {
				fff.createNewFile();
			}
			
			bw = new BufferedWriter(
					new FileWriter(fff));
			for(int i=0;i<nsx.size();i++){		
					bw.write(nsx.get(i) + "\r\n");
					bw.flush();
					//bw.close();
			}
			bw.close();			
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	public boolean useProxy(){
		return up;
	}
	
	public boolean dna(){
		return dna;
	}
}
