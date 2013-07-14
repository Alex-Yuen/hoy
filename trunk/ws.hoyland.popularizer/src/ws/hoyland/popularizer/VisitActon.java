package ws.hoyland.popularizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;


public class VisitActon extends PopularizerAction {	
	
	public VisitActon(IWorkbenchWindow window, String label, String viewId) {
		super(window);
		this.viewId = viewId;
        setText(label);
        // The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_VISIT);
        // Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_VISIT);
		setImageDescriptor(ws.hoyland.popularizer.Activator.getImageDescriptor("/icons/sample2.gif"));
	}
	
	public void run() {
		if(window != null) {	
			try {
				IWorkbenchPage page = window.getActivePage();
				page.showView(viewId);
			} catch (PartInitException e) {
				MessageDialog.openError(window.getShell(), "Error", "Error opening view:" + e.getMessage());
			}
		}
		hide();
	}
}
