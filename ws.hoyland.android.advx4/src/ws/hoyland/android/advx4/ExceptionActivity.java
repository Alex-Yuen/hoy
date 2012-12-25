package ws.hoyland.android.advx4;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ExceptionActivity extends Activity {
	private TextView tv;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.tv = new TextView(this);
		String str = getIntent().getStringExtra("message");
		if (str != null){
			this.tv.setText(str);
		}
		setContentView(this.tv);
	}
}
