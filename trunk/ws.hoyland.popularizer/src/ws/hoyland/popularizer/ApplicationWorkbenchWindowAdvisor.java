package ws.hoyland.popularizer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(720, 480));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
	}

	public void postWindowOpen() {
		super.postWindowOpen();

		IWorkbenchWindow window = getWindowConfigurer().getWindow();

		Shell shell = window.getShell();
		shell.setSize(new Point(720, 480));
		Rectangle screenSize = Display.getDefault().getClientArea();
		Rectangle frameSize = shell.getBounds();
		shell.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);

//		try {
//			IWorkbenchPage page = window.getActivePage();
//			IViewReference[] iViewReferences = page.getViewReferences();
//			for (IViewReference iViewReference : iViewReferences) {
//				if (!HomeView.ID.equals(iViewReference.getId())
//						&& !NavigationView.ID.equals(iViewReference.getId())) {
//					page.hideView(iViewReference);
//				}
//			}
//		} catch (Exception e) {
//			MessageDialog.openError(window.getShell(), "Error",
//					"Error opening view:" + e.getMessage());
//		}
	}
}
