package ws.hoyland.qm;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

public class Task implements Runnable {

	protected QM qm;
	private TableItem item;
	
	public Task(ThreadPoolExecutor pool, List<String> proxies, TableItem item,
			Object object, QM qm) {
		this.qm = qm;
		this.item = item;
	}

	@Override
	public void run() {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				item.setText(4, "正在检查");
				item.getParent().setSelection(item);
				//System.out.println("OK1:"+item.getText(0));
			}
		});
		

		try{
			Thread.sleep(1000*2);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				item.setText(4, "完成");
				//System.out.println("OK2:"+item.getText(0));
			}
		});
		
	}

}
