package ws.hoyland.android.advx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

public class BootBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			try {
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
				while (true) {
					NetworkInfo info = cm.getActiveNetworkInfo();
					if ((info != null)&&(info.getState()==NetworkInfo.State.CONNECTED)) {
						Thread.sleep(4000);
						Handler handler = new Handler();
						handler.post(new AdvxDownloader(context));
						break;
					}else{
						Thread.sleep(200);
					}
				}
			} catch (Exception e) {
				Intent activityIntent = new Intent(context, ExceptionActivity.class);
				activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activityIntent.putExtra("message", e.getMessage());
				context.startActivity(activityIntent);
			}
		}
	}
}