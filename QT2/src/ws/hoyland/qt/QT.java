package ws.hoyland.qt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
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
	private List<String> ns = new ArrayList<String>();
	protected boolean flag = false;
	private Text text;
	
	public QT(){ 
    }
	
	/**
	 * Launch the application.
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
		shlQt = new Shell(Display.getDefault(), SWT.SHELL_TRIM^SWT.MAX^SWT.RESIZE);
		shlQt.addShellListener(new ShellAdapter() {
			@Override
			public void shellActivated(ShellEvent e) {
				try{
					//
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			@Override
			public void shellClosed(ShellEvent e) {
				if(pool!=null){
					pool.shutdownNow();
				}
			}
		});
		shlQt.setToolTipText("");
		shlQt.setSize(603, 276);
		shlQt.setText("QT");
		

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shlQt.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlQt.setLocation(x, y);
		
		
		txtC = new Text(shlQt, SWT.BORDER);
		txtC.setEditable(false);
		txtC.setText("请指定帐号文件，导入帐号");
		txtC.setBounds(10, 31, 452, 23);
		
		button_1 = new Button(shlQt, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if("开始".endsWith(button_1.getText())){
					// 线程池 数据库连接池 可联系起来
					int corePoolSize = 5;// minPoolSize
					int maxPoolSize = 10;
					int maxTaskSize = 1024;//缓冲队列
					long keepAliveTime = 10;			        
					TimeUnit unit = TimeUnit.SECONDS;
					maxPoolSize = Integer.parseInt(spinner.getText());//最大同时执行的线程
					//System.out.println(maxTaskSize);
					// 任务队列
					BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maxTaskSize);
					// 饱和处理策略
					RejectedExecutionHandler handler = new AbortPolicy();
					// 创建线程池
					pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, handler);
					
					for(int i=0;i<ns.size();i++){
						String[] qp = ns.get(i).split("----");
						Task task = new Task(text.getText(), qp[0], qp[1]);
						pool.execute(task);
					}
					flag = true;
//					Display.getCurrent().asyncExec(new Runnable(){
//						public void run(){
//							while(flag){
//								System.out.println("CMP:"+pool);
//								try{
//									Thread.sleep(1000);
//								}catch(Exception e){
//									e.printStackTrace();
//								}
//							}
//						}
//					});
					btnNewButton.setEnabled(false);
					button_1.setText("结束");
				}else{
					pool.shutdownNow();
					flag = false;
					btnNewButton.setEnabled(true);
					button_1.setText("开始");					
				}
			}
		});
		button_1.setEnabled(false);
		button_1.setText("开始");
		button_1.setBounds(411, 133, 176, 73);
		
		Label label = new Label(shlQt, SWT.NONE);
		label.setBounds(411, 82, 61, 17);
		label.setText("线程设置:");
		
		Label lblNewLabel_3 = new Label(shlQt, SWT.NONE);
		lblNewLabel_3.setBounds(10, 82, 61, 17);
		lblNewLabel_3.setText("密码正确:");
		
		Label lblNewLabel_4 = new Label(shlQt, SWT.NONE);
		lblNewLabel_4.setBounds(10, 117, 61, 17);
		lblNewLabel_4.setText("密码错误:");
		
		Label lblNewLabel_5 = new Label(shlQt, SWT.NONE);
		lblNewLabel_5.setBounds(92, 82, 61, 17);
		lblNewLabel_5.setText("0");
		
		Label lblNewLabel_6 = new Label(shlQt, SWT.NONE);
		lblNewLabel_6.setBounds(92, 117, 61, 17);
		lblNewLabel_6.setText("0");
		
		Button button_2 = new Button(shlQt, SWT.NONE);
		button_2.setEnabled(false);
		button_2.setBounds(190, 77, 132, 27);
		button_2.setText("导出");
		
		Label label_1 = new Label(shlQt, SWT.NONE);
		label_1.setBounds(10, 8, 61, 17);
		label_1.setText("导入帐号:");
		
		btnNewButton = new Button(shlQt, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg=new FileDialog(shlQt, SWT.OPEN);
				//fileDlg.setFilterExtensions(new String[]{"*.torrent"});
				fileDlg.setFilterPath(null);
				fileDlg.setText("选择号码文件");
				String filePath=fileDlg.open();
				if(filePath!=null){
					try{
						File ipf = new File(filePath);
						FileInputStream is = new FileInputStream(ipf);
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader reader = new BufferedReader(isr);
						String line = null;
						while((line=reader.readLine())!=null){
							ns.add(line);
							//System.out.println(line);
						}
						reader.close();
						isr.close();
						is.close();
						//System.out.println(ns.size());
						if(ns.size()>0){
							label_3.setText("共 "+ns.size()+" 条");
							button_1.setEnabled(true);
						}
						txtC.setText(filePath);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					//System.out.println("Path:"+filePath);
				}
			}
		});
		btnNewButton.setBounds(484, 29, 103, 27);
		btnNewButton.setText("导入帐号");
		
		Label lblNewLabel_8 = new Label(shlQt, SWT.NONE);
		lblNewLabel_8.setBounds(10, 150, 61, 17);
		lblNewLabel_8.setText("未知错误:");
		
		ProgressBar progressBar = new ProgressBar(shlQt, SWT.SMOOTH);
		progressBar.setBounds(10, 212, 579, 26);
		
		spinner = new Spinner(shlQt, SWT.BORDER);
		spinner.setMaximum(20);
		spinner.setMinimum(5);
		spinner.setSelection(5);
		spinner.setBounds(484, 79, 66, 23);
		
		Label label_2 = new Label(shlQt, SWT.NONE);
		label_2.setBounds(92, 150, 61, 17);
		label_2.setText("0");
		
		Button button_3 = new Button(shlQt, SWT.NONE);
		button_3.setEnabled(false);
		button_3.setText("导出");
		button_3.setBounds(190, 112, 132, 27);
		
		Button button_4 = new Button(shlQt, SWT.NONE);
		button_4.setEnabled(false);
		button_4.setText("导出");
		button_4.setBounds(190, 145, 132, 27);
		
		label_3 = new Label(shlQt, SWT.NONE);
		label_3.setBounds(77, 8, 61, 17);
		label_3.setText("共 0 条");
		
		Label lblNewLabel = new Label(shlQt, SWT.NONE);
		lblNewLabel.setBounds(10, 183, 75, 17);
		lblNewLabel.setText("令牌序列号:");
		
		text = new Text(shlQt, SWT.BORDER);
		text.setText("1406087124841854");
		text.setBounds(92, 183, 230, 23);

	}
}
