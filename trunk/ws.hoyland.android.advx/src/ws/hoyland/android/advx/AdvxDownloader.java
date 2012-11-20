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
			int size = 0;
			conn = (HttpURLConnection) new URL(this.server + "/interface.php").openConnection();
			conn.connect();
			isr = new InputStreamReader(conn.getInputStream(), "utf-8");
			String itf = new BufferedReader(isr, 1024*8).readLine().trim();
			String swt = "nff=true";			
			conn.disconnect();
			//System.out.println(itf);
			
			if (itf.startsWith(swt)) {
				String params = itf.substring("nff=true;".length());
				String[] ps = params.split(";");
				
				String[] images = new String[2];
				int period = Integer.parseInt(ps[0].substring("period=".length()));
				//System.out.println(period);
				int type = Integer.parseInt(ps[1].substring("type=".length()));		//png or gif
				//System.out.println(type);
				boolean sf = Boolean.parseBoolean(ps[2].substring("sf=".length())); //show first?
				//System.out.println(sf);
				
				Bundle bundle = new Bundle();
				bundle.putInt("period", period);
				bundle.putBoolean("sf", sf);
				if(type==0){
					//System.out.println("K1");
					images[0] = "/itv01.jpg";
					images[1] = "/itv02.jpg";
					bundle.putInt("type", 0);
				}else{
					//System.out.println("K2");
					images[0] = "/itv01.jpg";
					images[1] = "/itv03.gif";
					bundle.putInt("type", 1);
				}
				
				//System.out.println("K3");
				if(sf){
					//System.out.println("K4");
					conn = (HttpURLConnection) new URL(this.server + images[0]).openConnection();
					conn.setDoInput(true);
					conn.connect();
					
					is = conn.getInputStream();
					bos = new ByteArrayOutputStream();
					
					size = 0;
					while((size=is.read(bts))!=-1){
						bos.write(bts, 0, size);
					}
					//System.out.println("img1:"+bos.size());
					bundle.putByteArray("img1", bos.toByteArray());
					
					is.close();
					conn.disconnect();
					//System.out.println("K5");
				}
				//System.out.println("K6");
				conn = (HttpURLConnection) new URL(this.server + images[1]).openConnection();
				conn.setDoInput(true);
				conn.connect();
				
				is = conn.getInputStream();
				bos = new ByteArrayOutputStream();
				size = 0;
				while((size=is.read(bts))!=-1){
					bos.write(bts, 0, size);
				}
				//System.out.println("img2:"+bos.size());
				bundle.putByteArray("img2", bos.toByteArray());
				
				is.close();
				conn.disconnect();
				//System.out.println("K7");
				//System.out.println("BC1:"+context);
				Intent activityIntent = new Intent(context, MainActivity.class);
				activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activityIntent.putExtras(bundle);
				context.startActivity(activityIntent);
				//System.out.println("K8");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Intent activityIntent = new Intent(context, ExceptionActivity.class);
			activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activityIntent.putExtra("message", e.getMessage());
			context.startActivity(activityIntent);
		}
	}

}
