package ws.hoyland.xplayer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ws.hoyland.xplayer.Application;

public class FullScreenHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		Application.SCREEN.setFullScreen(!Application.SCREEN.getMaximized());
		return null;
	}

}
