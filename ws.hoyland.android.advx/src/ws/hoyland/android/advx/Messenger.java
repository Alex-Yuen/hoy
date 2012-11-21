package ws.hoyland.android.advx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class Messenger implements Runnable {
	private Context context;
	private String server = "http://www.chenxjxc.com";
	private HttpURLConnection conn = null;
	private InputStreamReader isr = null;
	private String message = null;
	
	public Messenger(Context context, String message) {
		this.context = context;
		this.message = message;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			conn = (HttpURLConnection) new URL(this.server + "/interface2.php").openConnection();
			conn.connect();
			isr = new InputStreamReader(conn.getInputStream(), "utf-8");
			String itf = new BufferedReader(isr, 1024*8).readLine().trim();
			String swt = "nffm=true";			
			conn.disconnect();
			//System.out.println(itf);
				
			if (itf.startsWith(swt)) {
				//System.out.println("T1");
				String params = itf.substring("nffm=true;".length());
				String[] ps = params.split(";");
				String msg = ps[0].substring("message=".length());
				int ts = Integer.parseInt(ps[1].substring("ts=".length()));
				
				if(message==null||!message.equals(msg)){
					//System.out.println("T2");
					message = msg;
					Intent activityIntent = new Intent(this.context, MessageActivity.class);
					activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activityIntent.putExtra("info", message);
					activityIntent.putExtra("ts", ts);
					this.context.startActivity(activityIntent);
					//this.context.get
					//System.out.println("T3");
				}
			}
			Handler handler = new Handler();
			handler.postDelayed(new Messenger(this.context, this.message), 1000*60*2);
			//System.out.println("T4");
		}catch (Exception e) {
			e.printStackTrace();
			Intent activityIntent = new Intent(this.context, ExceptionActivity.class);
			activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activityIntent.putExtra("message", e.getMessage());
			this.context.startActivity(activityIntent);
		}
		
	}

}
