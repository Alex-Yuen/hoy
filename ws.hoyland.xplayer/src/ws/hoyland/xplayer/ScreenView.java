package ws.hoyland.xplayer;

import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class ScreenView extends ViewPart {

//	private static Text text;
	
	public ScreenView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		//System.out.println(parent.getShell().getLayout());
		Random rnd = new Random();
		//if(text==null){
			Text text = new Text(parent, SWT.BORDER); 
			text.setText("screen here:"+rnd.nextInt(100));
		//}else{
	//		text.redraw();
		//}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
