package ws.hoyland.xplayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import ws.hoyland.xplayer.dialog.OptionDialog;

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
		configurer.setInitialSize(new Point(800, 600));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);

	}

	@Override
	public void postWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		final Shell shell = configurer.getWindow().getShell();

		Display.getCurrent().addFilter(SWT.KeyDown, new Listener() {
			public void handleEvent(Event e) {
				if ((e.stateMask == SWT.CTRL)
						&& (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR)) {
					Application.SCREEN.setFullScreen(!Application.SCREEN
							.getFullScreen());

				}else if ((e.stateMask == SWT.CTRL)&& (e.keyCode == '`')){
					Application.SCREEN.setVisible(!Application.SCREEN.isVisible());
				}else if ((e.stateMask == SWT.CTRL)&& (e.keyCode == 'o')){
					//new OptionHandler().execute(null);
					if(shell.getShells().length==1){
						OptionDialog dialog = new OptionDialog(shell);						
						dialog.open();
					}
				}
			}
		});

		// if("ws.hoyland.xplayer.perspective".equals(configurer.getWindow().getActivePage().getPerspective().getId())){
		shell.setLocation(
				Display.getCurrent().getClientArea().width / 2
						- shell.getSize().x / 2,
				Display.getCurrent().getClientArea().height / 2
						- shell.getSize().y / 2);

		// 可以通过|来组全不同的样式值来达到特定的效果
		// if(Application.SCREEN==null){
		Application.SCREEN = new Shell(shell, 33554432);
		// }

		Listener listener = new Listener() {
			int startX, startY;

			public void handleEvent(Event e) {
				if (e.type == SWT.MouseDown && e.button == 1) {
					startX = e.x;
					startY = e.y;
				}
				if (e.type == SWT.MouseMove && (e.stateMask & SWT.BUTTON1) != 0) {
					Point p = Application.SCREEN.toDisplay(e.x, e.y);
					p.x -= startX;
					p.y -= startY;
					Application.SCREEN.setLocation(p);
				}
			}
		};
		Application.SCREEN.addListener(SWT.MouseDown, listener);
		Application.SCREEN.addListener(SWT.MouseMove, listener);

		Application.SCREEN.addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				// 如果按的是Esc键就不执行操作
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
				}
			}

		});

		Application.SCREEN.setLocation(0, 0);
		Application.SCREEN.setSize(450, 300);

		Application.SCREEN.open();
		Application.SCREEN.setVisible(false);

		// Application.SCREEN.setLocation(0, 0);
		// Application.SCREEN.setSize(600, 450);
		// Application.SCREEN.open();
		// Application.SCREEN.setVisible(false);
		// while (!screen.isDisposed()) {
		// if (!screen.getDisplay().readAndDispatch()) {
		// screen.getDisplay().sleep();
		// }
		// }

		// ScreenDialog sd = new ScreenDialog(shell);
		// sd.getParent().open();
		// sd.open();
		// sd.setBlockOnOpen(false);
		// shell.getDisplay().dispose();

		// screen.dispose();
		// shell.setMenuBar(null);
		// SCREEN.getWorkbenchWindow().
		// shell.set

		//填充propertysheet
		
		super.postWindowOpen();
	}

}
