package ws.hoyland.android.advx4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class MessageActivity extends Activity {
	private AutoScrollTextView autoScrollTextView;
	private Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		setContentView(R.layout.message);
		String info = getIntent().getStringExtra("info");
		int ts = getIntent().getIntExtra("ts", 2);
		if (info != null) {
			this.autoScrollTextView = (AutoScrollTextView) findViewById(R.id.TextViewNotice);
			this.autoScrollTextView.setText(info);
			this.autoScrollTextView.init(getWindowManager());
			//this.autoScrollTextView.setMarqueeRepeatLimit(2);
			//this.autoScrollTextView.
			this.autoScrollTextView.setTS(ts);
			this.autoScrollTextView.setActivity(this);
			this.autoScrollTextView.startScroll();
		}

		this.handler = new Handler() {
			public void handleMessage(Message msg) {
				if(msg.what==0x12){
					Activity activity = (Activity) msg.obj;
					activity.finish();
				}
			}
		};

		/**
		Message message = new Message();
		message.obj = this;

		handler.sendMessageDelayed(message, info == null ? 0
				: info.length() * 1000);**/
	}
	
	public void send(Message message){		
		message.obj = this;
		handler.sendMessage(message);
	}
}
