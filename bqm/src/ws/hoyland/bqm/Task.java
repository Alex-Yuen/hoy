package ws.hoyland.bqm;

import java.util.Map;
import java.util.Random;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

public class Task implements Runnable {

	private TableItem item;
	private ICallback cb;
	private String mail;
	private Map<String, Byte> ss;
	private Random rnd;
	private String[] smtp;
	
	public Task(TableItem item, Map<String, Byte> ss, ICallback cb) {
		this.item = item;
		this.cb = cb;
		this.ss = ss;
		
		this.mail = item.getText(1);
		this.rnd = new Random();
	}

	@Override
	public void run() {
		info("开始发送");
		synchronized (ss) { //获取发送方
			if (ss.size() == 0) {
				return;
			} else {
				String[] sl = new String[ss.size()];
				ss.keySet().toArray(sl);
				
				String key = sl[rnd.nextInt(ss.size())];
				if(ss.get(key)==2){
					ss.remove(key);
				}else{
					ss.put(key, (byte)(ss.get(key)+1));
				}
				smtp = key.split("----");
			}
		}
		
		try{
			//Thread.sleep(2000);
			
			call(ICallback.SUCC, 0); //统计 成功的次数
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
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
	
	private void call(final int key, final int value) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				cb.call(key, value);
			}
		});
	}
}
