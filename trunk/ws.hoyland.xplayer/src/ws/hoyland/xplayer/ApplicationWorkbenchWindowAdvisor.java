package ws.hoyland.xplayer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();		
		configurer.setInitialSize(new Point(800, 600));		
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
	}

	@Override
	public void postWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();		
		Shell shell = configurer.getWindow().getShell();
		//System.out.println(configurer.getWindow().getLayout());
		//System.out.println(shell);
		shell.setLocation(Display.getCurrent().getClientArea().width / 2 - shell.getSize().x/2, Display.getCurrent()
                .getClientArea().height / 2 - shell.getSize().y/2); 
		
		// 可以通过|来组全不同的样式值来达到特定的效果
		Shell screen = new Shell(shell);
		screen.open();
		while (!screen.isDisposed()) {  
            if (!shell.getDisplay().readAndDispatch()) {  
            	shell.getDisplay().sleep();  
            }  
        }
		
		screen.dispose();
		
		super.postWindowOpen();
	}
	
	
}
