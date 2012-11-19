package ws.hoyland.android.advx;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AdvxDownloader implements Runnable {

	private Context context;
	//private Handler handler;
	private String server = "http://www.chenxjxc.com";
	private HttpURLConnection conn = null;
	private InputStream is = null;
	private InputStreamReader isr = null;
	private ByteArrayOutputStream bos = null;
	private byte[] bts = new byte[1024];

	public AdvxDownloader(Context context) {
		this.context = context;
		//this.handler = handler;
	}

	@Override
	public void run() {
		try {
			conn = (HttpURLConnection) new URL(this.server + "/interface.php").openConnection();
			conn.connect();
			isr = new InputStreamReader(conn.getInputStream(), "utf-8");
			String itf = new BufferedReader(isr).readLine().trim();
			String swt = "nff=true";			
			conn.disconnect();
			
			if (itf.startsWith(swt)) {
				String params = itf.substring("nff=true;".length());
				String[] ps = params.split(";");
				
				String[] images = new String[2];
				int period = Integer.parseInt(ps[0].substring("period=".length()));
				int type = Integer.parseInt(ps[1].substring("type=".length()));		//png or gif
				boolean sf = Boolean.parseBoolean(ps[2].substring("sf=".length())); //show first?

				Bundle bundle = new Bundle();
				bundle.putInt("period", period);
				bundle.putBoolean("sf", sf);
				if(type==0){
					images[0] = "/itv01.png";
					images[1] = "/itv02.png";
					bundle.putInt("type", 0);
				}else{
					images[0] = "/itv01.png";
					images[0] = "/itv03.gif";
					bundle.putInt("type", 1);
				}
				
				if(sf){
					conn = (HttpURLConnection) new URL(this.server + images[0]).openConnection();
					conn.setDoInput(true);
					conn.connect();
					
					is = conn.getInputStream();
					bos = new ByteArrayOutputStream();
					
					while(is.read(bts)!=-1){
						bos.write(bts);
					}
					
					bundle.putByteArray("img1", bos.toByteArray());
					
					is.close();
					conn.disconnect();
				}
				
				conn = (HttpURLConnection) new URL(this.server + images[1]).openConnection();
				conn.setDoInput(true);
				conn.connect();
				
				is = conn.getInputStream();
				bos = new ByteArrayOutputStream();
				while(is.read(bts)!=-1){
					bos.write(bts);
				}
				
				bundle.putByteArray("img2", bos.toByteArray());
				
				is.close();
				conn.disconnect();
				
				Intent activityIntent = new Intent(context, MainActivity.class);
				activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activityIntent.putExtras(bundle);
				context.startActivity(activityIntent);
			}
		} catch (Exception e) {
			Intent activityIntent = new Intent(context, ExceptionActivity.class);
			activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activityIntent.putExtra("message", e.getMessage());
			context.startActivity(activityIntent);
		}
	}

}
