package ws.hoyland.android.advx4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;

public class BootBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().penaltyLog().build());  
	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()  
	            .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());  
	    
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Handler handler = new Handler();
			handler.postDelayed(new AdvxDownloader(context), 4000);
		}
	}
}