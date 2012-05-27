package ws.hoyland.xplayer.dialog;

import java.awt.LayoutManager;

import javax.swing.BoxLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ws.hoyland.xplayer.layout.BorderLayout;

public class OptionDialog extends Dialog {

	protected Object result;

	protected Shell shell;

	public OptionDialog(Shell parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	protected void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(500, 375);
		shell.setText("Option");
		shell.setLocation(
				Display.getCurrent().getClientArea().width / 2
						- shell.getSize().x / 2,
				Display.getCurrent().getClientArea().height / 2
						- shell.getSize().y / 2);
		//
		shell.setLayout(new BorderLayout()); 

//		Button buttonWest = new Button(shell, SWT.PUSH); 
//		buttonWest.setText("West"); 
//		buttonWest.setLayoutData(new BorderLayout.BorderData(BorderLayout.WEST)); 
//
//		Button buttonEast = new Button(shell, SWT.PUSH); 
//		buttonEast.setText("East"); 
//		buttonEast.setLayoutData(new BorderLayout.BorderData(BorderLayout.EAST)); 
//		Button buttonNorth = new Button(shell, SWT.PUSH); 
//		buttonNorth.setText("North"); 
//		buttonNorth.setLayoutData(new BorderLayout.BorderData(BorderLayout.NORTH)); 

		Composite center = new Composite(shell, SWT.NONE);
		center.setToolTipText("Center"); 
		center.setLayoutData(new BorderLayout.BorderData(BorderLayout.CENTER)); 

		Composite buttom = new Composite(shell, SWT.NONE);
		buttom.setToolTipText("buttom"); 
		buttom.setBackground(new Color(getParent().getDisplay(), 255, 128, 128));
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.marginTop = 10;
		rowLayout.marginBottom = 10;
		rowLayout.marginLeft = 5;
		rowLayout.marginRight = 5;
		rowLayout.spacing = 10;
		
		buttom.setLayout(rowLayout);
		buttom.setLayoutData(new BorderLayout.BorderData(BorderLayout.SOUTH)); 
		
		Button ok = new Button(buttom, SWT.PUSH); 
		ok.setText("OK");
		ok.setLayoutData(new RowData(80, 30));
		//ok.setSize(80, 30);
		ok.setSelection(true);
		//ok.setLayoutData(new BorderLayout.BorderData(BorderLayout.SOUTH)); 

	}

}
