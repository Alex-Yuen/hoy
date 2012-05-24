package ws.hoyland.xplayer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ws.hoyland.xplayer.Application;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ScreenHandler extends AbstractHandler {
	
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
//		try {
//			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
////			System.out.println(window.getActivePage().getPerspective().getId());
//				//SCREEN.getWorkbenchWindow()
//				//System.out.println(SCREEN.getWorkbenchWindow());
//				//SCREEN.getWorkbenchWindow().
//				//SCREEN.getWorkbenchWindow().
//				Shell shell = window.getShell();
//
//				//shell.setFullScreen(true);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 MessageDialog.openInformation(
//		 window.getShell(),
//		 "XPlayer",
//		 "Hello, Eclipse world");
		Application.SCREEN.setVisible(!Application.SCREEN.isVisible());
		return null;
	}
}
