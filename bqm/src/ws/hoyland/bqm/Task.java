package ws.hoyland.bqm;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

public class Task implements Runnable {

	protected TableItem item;
	protected ICallback cb;
	private String mail;
	
	public Task(TableItem item, ICallback cb) {
		this.item = item;
		this.cb = cb;
		
		this.mail = item.getText(1);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//System.out.println("TASK");
		info("开始发送");
		
		info("发送结束");
		setSelection();
	}
	
	private void info(final String status) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				//qm.log(uin + "->" + status + "\r\n");
				item.setText(2, status);
			}
		});
	}
	
	private void setSelection(){
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				item.getParent().setSelection(item);
			}
		});
	}
}
