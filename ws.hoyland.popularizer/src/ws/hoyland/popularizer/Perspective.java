package ws.hoyland.popularizer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "ws.hoyland.popularizer.perspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true); 
		
		//layout.addStandaloneView(NavigationView.ID,  false, IPageLayout.LEFT, 0.25f, editorArea);

		IFolderLayout folder = layout.createFolder("operations", IPageLayout.TOP, 0.5f, editorArea);
		folder.addPlaceholder(HomeView.ID + ":*");
		folder.addView(HomeView.ID);
		folder.addView(ProcessView.ID);
		folder.addView(PostView.ID);

		//layout.getViewLayout(NavigationView.ID).setCloseable(false);
		layout.getViewLayout(HomeView.ID).setCloseable(false);
		layout.getViewLayout(HomeView.ID).setMoveable(false);
		layout.getViewLayout(ProcessView.ID).setCloseable(false);
		layout.getViewLayout(ProcessView.ID).setMoveable(false);
		layout.getViewLayout(PostView.ID).setCloseable(false);
		layout.getViewLayout(PostView.ID).setMoveable(false);
		
	}
}
