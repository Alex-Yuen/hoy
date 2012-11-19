package ws.hoyland.android.advx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class ImageSwitcher implements Runnable {

	private MainActivity activity;
	
	public ImageSwitcher(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public void run() {
		try{
			int period = 0;
			if(activity.isSf()){
				activity.getIv().setImageBitmap(activity.getImg2());
				activity.setContentView(activity.getIv());
				period = activity.getPeriod();
			}
			
			Message message = new Message();
			message.obj = activity;
			
			Handler handler = new Handler(){
			    public void handleMessage(Message msg) {
			        Activity activity = (Activity) msg.obj;
			        this.post(new Messenger(activity));
			        activity.finish();
			    }
			};
			
			handler.sendMessageDelayed(message, 1000*period); 
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
