package ws.hoyland.xplayer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ScreenHandler extends AbstractHandler {
	
	private static IWorkbenchPage SCREEN;
	/**
	 * The constructor.
	 */
	public ScreenHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//			System.out.println(window.getActivePage().getPerspective().getId());
			if(SCREEN==null){
				//System.out.println(window);
				
				SCREEN = window.openPage("ws.hoyland.xplayer.screenperspective", null);
				//SCREEN.getWorkbenchWindow()
				//System.out.println(SCREEN.getWorkbenchWindow());
				//SCREEN.getWorkbenchWindow().
				//SCREEN.getWorkbenchWindow().
//				Shell shell = SCREEN.getWorkbenchWindow().getShell();
//				shell.setMenuBar(null);
//				SCREEN.getWorkbenchWindow().
				//shell.set
//				shell.setLocation(0, 0);
//				shell.setSize(450, 300);
				//shell.setFullScreen(true);
			}else{
				SCREEN.getWorkbenchWindow().close();
				SCREEN = null;
				
			}
		} catch (WorkbenchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		 MessageDialog.openInformation(
//		 window.getShell(),
//		 "XPlayer",
//		 "Hello, Eclipse world");
//		if(!Application.SCREEN.isVisible()){
//			Application.SCREEN.setVisible(true);
//		}else{
//			Application.SCREEN.setVisible(false);
//		}
		return null;
	}
}
