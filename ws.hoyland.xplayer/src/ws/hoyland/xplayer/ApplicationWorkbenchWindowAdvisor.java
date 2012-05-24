package ws.hoyland.xplayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	
	private static boolean INITED = false;
	
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		if(!INITED){
			configurer.setInitialSize(new Point(800, 600));		
			configurer.setShowCoolBar(false);
			configurer.setShowStatusLine(true);
			INITED = true;
		}else{
			configurer.setShowMenuBar(false);
			configurer.setShowStatusLine(false);
			configurer.setShowCoolBar(false);
			configurer.setShellStyle(SWT.NONE);
			//configurer.
		}
		
		
	}

	@Override
	public void postWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();		
		Shell shell = configurer.getWindow().getShell();
		
		if("ws.hoyland.xplayer.perspective".equals(configurer.getWindow().getActivePage().getPerspective().getId())){
			shell.setLocation(Display.getCurrent().getClientArea().width / 2 - shell.getSize().x/2, Display.getCurrent()
	                .getClientArea().height / 2 - shell.getSize().y/2); 
		}else{
			shell.setLocation(0, 0);
			shell.setSize(450, 300);
			//shell.setMaximized(true);
			//shell.setFullScreen(true);
		}
		// 可以通过|来组全不同的样式值来达到特定的效果
//		if(Application.SCREEN==null){
//			Application.SCREEN = new Shell(shell, SWT.MODELESS | SWT.RESIZE | SWT.ON_TOP);
//		}
//		Application.SCREEN.setLocation(0, 0);
//		Application.SCREEN.setSize(600, 450);
//		Application.SCREEN.open();
//		Application.SCREEN.setVisible(false);
//		while (!screen.isDisposed()) {  
//            if (!screen.getDisplay().readAndDispatch()) {  
//            	screen.getDisplay().sleep();  
//            }  
//        }
		
//		 ScreenDialog sd = new ScreenDialog(shell);
//		 sd.getParent().open();
	     //sd.open();
	     //sd.setBlockOnOpen(false);
//	     shell.getDisplay().dispose();
	        
	//	screen.dispose();
		
		super.postWindowOpen();
	}
	
	
}
