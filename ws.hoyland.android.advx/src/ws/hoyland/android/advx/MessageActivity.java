package ws.hoyland.android.advx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

@SuppressLint("HandlerLeak")
public class MessageActivity extends Activity {
	private AutoScrollTextView autoScrollTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		setContentView(R.layout.message);
		String info = getIntent().getStringExtra("info");
		if (info != null) {
			this.autoScrollTextView = (AutoScrollTextView) findViewById(R.id.TextViewNotice);
			this.autoScrollTextView.setText(info);
			this.autoScrollTextView.init(getWindowManager());
			//this.autoScrollTextView.setMarqueeRepeatLimit(2);
			//this.autoScrollTextView.
			this.autoScrollTextView.setActivity(this);
			this.autoScrollTextView.startScroll();
		}

		/**
		Message message = new Message();
		message.obj = this;

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				Activity activity = (Activity) msg.obj;
				activity.finish();
			}
		};

		handler.sendMessageDelayed(message, info == null ? 0
				: info.length() * 1000);**/
	}
}
