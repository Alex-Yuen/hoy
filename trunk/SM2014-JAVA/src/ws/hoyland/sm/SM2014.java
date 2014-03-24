package ws.hoyland.sm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import swing2swt.layout.BorderLayout;
import ws.hoyland.security.ClientDetecter;
import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;
import ws.hoyland.util.Util;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SM2014 {

	protected Shell shell;
	private Text text;

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
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(SWT.CLOSE | SWT.TITLE);
		shell.setImage(SWTResourceManager.getImage(SM2014.class, "/logo.ico"));
		shell.setSize(467, 571);
		shell.setText("免责声明和许可协议");
		shell.setLayout(new BorderLayout(0, 0));
		
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new BorderLayout(0, 0));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(BorderLayout.SOUTH);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		
		Button btnNewButton = new Button(composite_1, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread(new Runnable(){

					@Override
					public void run() {
						int expire = 0;
						try{
							//URL
			                URL url = new URL("http://www.y3y4qq.com/ge");
			                
							Crypter crypt = new Crypter();
			                byte[] mid = Converts.hexStringToByte(ClientDetecter.getMachineID("SMZS"));
	
			                //String url = "http://www.y3y4qq.com/ge";
			                byte[] key = Util.genKey();
			                String header = Converts.bytesToHexString(key).toUpperCase() + Converts.bytesToHexString(crypt.encrypt(mid, key)).toUpperCase();
			                //Console.WriteLine(byteArrayToHexString(key).ToUpper());
			                //Console.WriteLine(content);
			                //client.UploadString(url, content);
			                //client.UploadString(url, 
			                //client.Encoding = Encoding.UTF8;
			                
			                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			                connection.setDoOutput(true);//允许连接提交信息
			                connection.setRequestMethod("POST");//网页提交方式“GET”、“POST”
			                //connection.setRequestProperty("User-Agent", "Mozilla/4.7 [en] (Win98; I)");
			                connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			                StringBuffer sb = new StringBuffer();
			                sb.append(header);
			                OutputStream os = connection.getOutputStream();
			                os.write(sb.toString().getBytes());
			                os.flush();
			                os.close();
	
			                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	
			                String line = br.readLine();
	
			                if (line != null) {	
			                	byte[] bs = Converts.hexStringToByte(line);
			                	bs = crypt.decrypt(bs, key);
			                	expire = Integer.parseInt(new String(bs));	
			                }
						}catch(Exception e){
							e.printStackTrace();
						}
							
						System.err.println("EXPIRE:"+expire);
						if(expire>0){
							final int ex = expire;
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									shell.dispose();
									new Main(ex).open();
								}				
							});
						}else{
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									shell.dispose();
									new ExpireInfo().open();
								}
							});
						}						
					}					
				}).start();
			}
		});
		btnNewButton.setBounds(133, 10, 80, 27);
		btnNewButton.setText("同意(&A)");
		
		Button btnNewButton_1 = new Button(composite_1, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
				System.exit(0);
			}
		});
		btnNewButton_1.setBounds(246, 10, 80, 27);
		btnNewButton_1.setText("不同意(&D)");
		
		text = new Text(shell, SWT.READ_ONLY | SWT.MULTI);
		text.setText("\t一、任何使用本软件的用户均应仔细阅读本声明和协议，用户可选择不使用本软\r\n件，用户使用本软件的行为将被视为对本声明和协议全部内容的认可。\r\n\r\n\t二、本软件运行需要一定的软硬件环境支持，并且不保证没有错误。用户须提供\r\n软件运行所需软件硬件环境。\r\n\r\n\t三、购买壹份本软件后，用户只能在唯一一台经过合法注册的电脑使用。\r\n\r\n\t四、本软件不承诺永久使用期,不承诺免费升级。\r\n\r\n\t五、注册用户在遵守法律及本声明和协议的前提下可使用本软件。用户无权实施\r\n包括但不限于下列行为：\r\n\t\t1、修改本软件版权的信息、内容；\r\n\t\t2、对本软件进行反向工程、反向汇编、反向编译等；\r\n\t\t3、利用本“软件”查询、盗取、传播、储存侵害他人知识产权、商业秘\r\n密权、个人财产权、隐私权、公开权等合法权利。\r\n\t\t4、传送或散布以其他方式实现传送含有受到知识产权法律保护的图像、\r\n相片、软件或其他资料的文件，作为举例（但不限于此）：包括版权或商标法（或隐私\r\n权或公开权），除非您拥有或控制着相应的权利或已得到所有必要的认可。\r\n\t\t5、使用本“软件”必须遵守国家有关法律和政策等，并遵守本声明和协\r\n议。对于用户违法或违反本声明和协议的使用而引起的一切责任，由用户负全部负责，\r\n一概与本软件开发者及销售者无关。而且，软件开发者有权视用户的行为性质，在不事\r\n先通知用户的情况下，采取包括但不限于中断使用许可、停止提供服务、限制使用、法\r\n律追究等措施。\r\n\t\t6、用户使用第三方插件与本软件作者及销售无关。\r\n\r\n\t郑重声明：本软件只供管理个人号码使用！禁止用于非法活动！违者后果自负！\r");
		text.setEditable(false);
		text.setLayoutData(BorderLayout.CENTER);

	}

}
