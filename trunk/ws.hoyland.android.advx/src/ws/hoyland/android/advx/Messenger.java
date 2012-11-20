package ws.hoyland.android.advx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;

public class Messenger implements Runnable {
	private Activity activity;
	private String server = "http://www.chenxjxc.com";
	private HttpURLConnection conn = null;
	private InputStreamReader isr = null;
	
	public Messenger(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			conn = (HttpURLConnection) new URL(this.server + "/interface2.php").openConnection();
			conn.connect();
			isr = new InputStreamReader(conn.getInputStream(), "utf-8");
			String itf = new BufferedReader(isr).readLine().trim();
			String swt = "nffm=true";			
			conn.disconnect();
			System.out.println(itf);
			
			if (itf.startsWith(swt)) {
				String param = itf.substring("nffm=true;".length());
				//String[] ps = params.split(";");
				String message = param.substring("message=".length());
				
				Intent activityIntent = new Intent(this.activity.getApplicationContext(), MessageActivity.class);
				activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activityIntent.putExtra("info", message);
				this.activity.getApplicationContext().startActivity(activityIntent);
			}
			Thread.sleep(1000*60);
		}catch (Exception e) {
			e.printStackTrace();
			Intent activityIntent = new Intent(this.activity.getApplicationContext(), ExceptionActivity.class);
			activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activityIntent.putExtra("message", e.getMessage());
			this.activity.getApplicationContext().startActivity(activityIntent);
		}
		
	}

}
