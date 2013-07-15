package ws.hoyland.popularizer;

import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.part.ViewPart;

public class HomeView extends ViewPart {

	public static final String ID = "ws.hoyland.popularizer.homeview";

	/**
	 * The text control that's displaying the content of the email message.
	 */
//	private Text messageText;
	
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

		Preferences pre = Preferences.systemRoot();//得到跟节点
		String key = "WS_HOYLAND_POPULARIZER_PID";//写入的键
		String value = pre.get(key, "0000000000000000");
		
		l = new Label(banner, SWT.WRAP);
		l.setText(value);
		
		l = new Label(banner, SWT.WRAP);
		l.setText("授权序号:");
		l.setFont(boldFont);
    
		key = "WS_HOYLAND_POPULARIZER_SID";//写入的键
		value = pre.get(key, "WHPS-XXXX-XXXX-XXXX");
		l = new Label(banner, SWT.NONE);
		l.setText(value);
    
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
}
