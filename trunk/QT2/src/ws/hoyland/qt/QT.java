package ws.hoyland.qt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class QT {

	protected Shell shlQt;
	private Text txtC;
	private Label lblNewLabel_7;

	private Map<String, String> ns = new HashMap<String, String>();
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
		shlQt = new Shell();
		shlQt.addShellListener(new ShellAdapter() {
			@Override
			public void shellActivated(ShellEvent e) {
				try{
					File f = new File("c:\\num.txt");
					if(f.exists()){
						lblNewLabel_7.setText("文件存在");
						InputStream is = new FileInputStream(f);
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader reader = new BufferedReader(isr);
						String qq = null;
						while((qq=reader.readLine())!=null){
							ns.put(qq, "0");
						}
						reader.close();
						is.close();
						lblNewLabel_7.setText(String.valueOf(ns.size()));
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		shlQt.setToolTipText("");
		shlQt.setSize(450, 300);
		shlQt.setText("QT");
		
		txtC = new Text(shlQt, SWT.BORDER);
		txtC.setEditable(false);
		txtC.setText("c:\\num.txt");
		txtC.setBounds(23, 10, 264, 23);
		
		Button button_1 = new Button(shlQt, SWT.NONE);
		button_1.setText("验证");
		button_1.setBounds(156, 210, 138, 27);
		
		Label lblNewLabel = new Label(shlQt, SWT.NONE);
		lblNewLabel.setBounds(25, 86, 61, 17);
		lblNewLabel.setText("进度:");
		
		Label lblNewLabel_1 = new Label(shlQt, SWT.NONE);
		lblNewLabel_1.setBounds(92, 86, 110, 17);
		lblNewLabel_1.setText("0/0");
		
		Label label = new Label(shlQt, SWT.NONE);
		label.setBounds(25, 54, 61, 17);
		label.setText("线程:");
		
		Label lblNewLabel_2 = new Label(shlQt, SWT.NONE);
		lblNewLabel_2.setBounds(92, 54, 61, 17);
		lblNewLabel_2.setText("5");
		
		Label lblNewLabel_3 = new Label(shlQt, SWT.NONE);
		lblNewLabel_3.setBounds(25, 126, 61, 17);
		lblNewLabel_3.setText("成功:");
		
		Label lblNewLabel_4 = new Label(shlQt, SWT.NONE);
		lblNewLabel_4.setBounds(25, 164, 61, 17);
		lblNewLabel_4.setText("失败:");
		
		Label lblNewLabel_5 = new Label(shlQt, SWT.NONE);
		lblNewLabel_5.setBounds(92, 126, 61, 17);
		lblNewLabel_5.setText("0");
		
		Label lblNewLabel_6 = new Label(shlQt, SWT.NONE);
		lblNewLabel_6.setBounds(92, 164, 61, 17);
		lblNewLabel_6.setText("0");
		
		Button button_2 = new Button(shlQt, SWT.NONE);
		button_2.setBounds(190, 116, 80, 27);
		button_2.setText("导出");
		
		lblNewLabel_7 = new Label(shlQt, SWT.NONE);
		lblNewLabel_7.setBounds(333, 13, 67, 17);
		lblNewLabel_7.setText("文件不存在");

	}
}
