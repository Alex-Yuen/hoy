package ws.hoyland.popularizer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

public class PopularizerAction extends Action {

	protected final IWorkbenchWindow window;
	protected String viewId;
	
	public PopularizerAction(IWorkbenchWindow window) {
		this.window = window;	
	}
	
	public void hide() {
		if(window != null) {	
			try {
				IWorkbenchPage page = window.getActivePage(); 
				IViewReference[] iViewReferences = page.getViewReferences();
				for (IViewReference iViewReference: iViewReferences){
					if (!viewId.equals(iViewReference.getId())&&!NavigationView.ID.equals(iViewReference.getId())){
						page.hideView(iViewReference);
					}
				}
			} catch (Exception e) {
				MessageDialog.openError(window.getShell(), "Error", "Error opening view:" + e.getMessage());
			}
		}
	}
}
