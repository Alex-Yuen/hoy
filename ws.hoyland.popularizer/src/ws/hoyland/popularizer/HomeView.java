package ws.hoyland.popularizer;

import java.util.prefs.Preferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class HomeView extends ViewPart {

	public static final String ID = "ws.hoyland.popularizer.homeview";

	/**
	 * The text control that's displaying the content of the email message.
	 */
//	private Text messageText;
	
	private Label status;
	private Link link;
	private Text ss;
	private Preferences pre = Preferences.systemRoot();
	
	public void createPartControl(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);
		// top banner
		Composite banner = new Composite(top, SWT.NONE);
		banner.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false));
		layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.numColumns = 2;
		banner.setLayout(layout);
		
		// setup bold font
		Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);    
		
		Label l = new Label(banner, SWT.WRAP);
		l.setText("用户编号:");
		l.setFont(boldFont);

		
		String key = "WS_HOYLAND_POPULARIZER_PID";//写入的键
		String value = pre.get(key, "0000000000000000");
		
		Text text = new Text(banner, SWT.WRAP);
		text.setText(value);
		text.setEditable(false);
		
		l = new Label(banner, SWT.WRAP);
		l.setText("授权序号:");
		l.setFont(boldFont);

		
		Composite SID = new Composite(banner, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		SID.setLayout(layout);
		
		key = "WS_HOYLAND_POPULARIZER_SID";//写入的键
		value = pre.get(key, "WHPS-XXXX-XXXX-XXXX");
		ss = new Text(SID, SWT.NONE);
		ss.setText(value);
		
		status = new Label(SID, SWT.WRAP);
		status.setText("正在验证...");
		
		link = new Link(SID, SWT.WRAP);
		link.setText("<a>重新验证</a>");
		link.setEnabled(false);
		link.addSelectionListener(new SelectionAdapter() {    
			public void widgetSelected(SelectionEvent e) {
				verify();
			}    
		});
		
		verify();
		// message contents
//		messageText = new Text(top, SWT.MULTI | SWT.WRAP);
//		messageText.setText("HOME This RCP Application was generated from the PDE Plug-in Project wizard. This sample shows how to:\n"+
//						"- add a top-level menu and toolbar with actions\n"+
//						"- add keybindings to actions\n" +
//						"- create views that can't be closed and\n"+
//						"  multiple instances of the same view\n"+
//						"- perspectives with placeholders for new views\n"+
//						"- use the default about dialog\n"+
//						"- create a product definition\n");
//		messageText.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void setFocus() {
//		messageText.setFocus();
	}
	
	private void verify(){

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try{					
					link.setEnabled(false);
					status.setText("正在验证...");
					DefaultHttpClient client = new DefaultHttpClient();
					client.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
					client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
					
					HttpGet httpGet = new HttpGet("http://www.hoyland.ws/bqm/verify.php?sid="+ss.getText().trim());  					
					HttpResponse resp = client.execute(httpGet);		              
		            HttpEntity entity = resp.getEntity();  
		            String rs = EntityUtils.toString(entity , "UTF-8").trim();
		            if(rs.startsWith("1")){
		            	status.setText("合法序号.  ");
		            	pre.put("WS_HOYLAND_POPULARIZER_SID", ss.getText().trim());
		            	ss.setText(ss.getText().trim());
		            }else{
		            	status.setText("非法序号!  ");
		            }
		            link.setEnabled(true);
		            link.setFocus();
		            httpGet.abort();
		            client.getConnectionManager().shutdown();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
}
