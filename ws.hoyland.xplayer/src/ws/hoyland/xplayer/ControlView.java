package ws.hoyland.xplayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class ControlView extends ViewPart {

	public ControlView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		//System.out.println(parent.getShell().getLayout());
		Text text = new Text(parent, SWT.BORDER); 
		text.setText("Control pannel here");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
