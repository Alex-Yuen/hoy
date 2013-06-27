package ws.hoyland.bqm;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Canvas;

public class BQM {

	protected Shell shell;
	private Table table;
	private Text text;
	private Text text_1;

	/**
	 * Launch the application.
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
		shell = new Shell(Display.getDefault(), SWT.SHELL_TRIM ^ SWT.MAX
				^ SWT.RESIZE);
		shell.setSize(769, 596);
		shell.setText("Batch QQ Mail");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		
		InputStream is = BQM.class.getClassLoader().getResourceAsStream("mail.png");
		Image image = new Image(Display.getDefault(), is);
		shell.setImage(image);
		shell.setLayout(null);
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("接收方 (共 0 个):");
		label.setBounds(10, 10, 164, 17);
		
		Link link = new Link(shell, 0);
		link.setText("<a>导入...</a>");
		link.setBounds(224, 10, 36, 17);
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 35, 250, 270);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(30);
		tableColumn.setText("ID");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(120);
		tableColumn_1.setText("帐号");
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(76);
		tableColumn_2.setText("状态");
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("系统日志:");
		label_1.setBounds(10, 311, 250, 17);
		
		text = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		text.setEditable(false);
		text.setBounds(10, 334, 250, 210);
		
		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setText("模板文件:");
		label_2.setBounds(276, 10, 61, 17);
		
		text_1 = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		text_1.setEditable(false);
		text_1.setBounds(276, 35, 479, 270);
		
		Link link_1 = new Link(shell, 0);
		link_1.setText("<a>导入...</a>");
		link_1.setBounds(719, 10, 36, 17);
		
		Button button = new Button(shell, SWT.NONE);
		button.setText("开始");
		button.setEnabled(false);
		button.setBounds(540, 471, 213, 73);
		
		Label label_3 = new Label(shell, SWT.BORDER);
		label_3.setBounds(0, 550, 763, 17);
		
		Group grpOption = new Group(shell, SWT.NONE);
		grpOption.setText("设置");
		grpOption.setBounds(542, 311, 213, 154);
		
		Label label_4 = new Label(grpOption, SWT.NONE);
		label_4.setText("线程数(1~10):");
		label_4.setBounds(10, 26, 89, 17);
		
		Spinner spinner = new Spinner(grpOption, SWT.BORDER);
		spinner.setMaximum(10);
		spinner.setMinimum(1);
		spinner.setSelection(1);
		spinner.setBounds(105, 23, 45, 23);
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(276, 311, 71, 17);
		lblNewLabel.setText("SMTP池:");
		
		Link link_2 = new Link(shell, 0);
		link_2.setText("<a>导入...</a>");
		link_2.setBounds(486, 311, 36, 17);
		
		Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.setBounds(276, 334, 247, 210);
		
	}
}
