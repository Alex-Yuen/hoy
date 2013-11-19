package ws.hoyland.sszs;

import java.io.BufferedWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class SSZS {

	protected Shell shlSszs;
	
	private BufferedWriter output = null;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SSZS window = new SSZS();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlSszs.open();
		shlSszs.layout();
		while (!shlSszs.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {		
		shlSszs = new Shell(Display.getDefault(), SWT.SHELL_TRIM ^ SWT.MAX
				^ SWT.RESIZE);
		shlSszs.setImage(SWTResourceManager.getImage(SSZS.class, "/ws/hoyland/sszs/logo.ico"));
		
		shlSszs.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try{
					if(output!=null){
						output.close();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				System.exit(0);
			}
		});
		shlSszs.setSize(878, 589);
		shlSszs.setText("sszs");

		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shlSszs.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlSszs.setLocation(x, y);

	}

}
