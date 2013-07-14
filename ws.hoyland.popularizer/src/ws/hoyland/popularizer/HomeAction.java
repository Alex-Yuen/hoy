package ws.hoyland.popularizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;


public class HomeAction extends PopularizerAction {
		
	public HomeAction(IWorkbenchWindow window, String label, String viewId) {
		super(window);
		this.viewId = viewId;
        setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_HOME);
        // Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_HOME);
		setImageDescriptor(ws.hoyland.popularizer.Activator.getImageDescriptor("/icons/home.png"));
	}
	
	public void run() {
		if(window != null) {	
			try {
				IWorkbenchPage page = window.getActivePage();
				page.showView(viewId);
				//window.getActivePage().getViewReferences()[1].getView(false).dispose();
				//window.getActivePage().hideView(window.getActivePage().getViewReferences()[1].getView(false));
				//window.getActivePage().showView(viewId, Integer.toString(instanceNum++), IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException e) {
				MessageDialog.openError(window.getShell(), "Error", "Error opening view:" + e.getMessage());
			}
		}
		hide();
	}
}
