package ws.hoyland.xplayer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class FullScreenHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		try {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			IWorkbenchWindow[] ws = window.getWorkbench().getWorkbenchWindows();
			for(IWorkbenchWindow w:ws){
				if("ws.hoyland.xplayer.screenperspective".equals(w.getActivePage().getPerspective().getId())){
					w.getShell().setFullScreen(!w.getShell().getFullScreen());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
