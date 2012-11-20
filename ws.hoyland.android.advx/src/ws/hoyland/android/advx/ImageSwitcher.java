package ws.hoyland.android.advx;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

@SuppressLint("HandlerLeak")
public class ImageSwitcher implements Runnable {

	private MainActivity activity;
	
	public ImageSwitcher(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public void run() {
		try{
			int period = 0;
			if(activity.isSf()){
				if(activity.getType()==0){
					//System.out.println("E");
					activity.getIv().setImageBitmap(activity.getImg2());
					activity.setContentView(activity.getIv());
				}else{
					//System.out.println("F");
					/**
					 * LayoutInflater inflater = (LayoutInflater)
					 * getSystemService(LAYOUT_INFLATER_SERVICE); RelativeLayout parent =
					 * (RelativeLayout) inflater.inflate(R.layout.activity_main, null);
					 * GifView gif = (GifView)parent.findViewById(R.id.gif);
					 * parent.removeView(gif); gif.setGifImage(R.drawable.abc); // 添加监听器 //
					 * gif.setOnClickListener(this); // 设置显示的大小，拉伸或者压缩
					 * gif.setShowDimension(300, 300); // 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
					 * gif.setGifImageType(GifImageType.COVER);
					 * 
					 * setContentView(gif);
					 **/
					//activity.fin
					//GifView gif = (GifView) findViewById(0);
					GifView gif = new GifView(activity);
					//System.out.println(gif); 
					gif.setGifImage(activity.getImg2bs()); // 添加监听器 //
					//gif.setOnClickListener(this); // 设置显示的大小，拉伸或者压缩
					//gif.set
					DisplayMetrics dm = new DisplayMetrics();  
					activity.getWindowManager().getDefaultDisplay().getMetrics(dm);   
					gif.setShowDimension(dm.widthPixels, dm.heightPixels); // 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
					gif.setGifImageType(GifImageType.WAIT_FINISH); //R.layout.activity_main
					activity.setContentView(gif);
					//System.out.println("F1");
				}
				period = activity.getPeriod();
				//System.out.println("F2");
			}
			
			Message message = new Message();
			message.obj = activity;
			
			Handler handler = new Handler(){
			    public void handleMessage(Message msg) {
			    	//System.out.println("H01");
			        Activity activity = (Activity) msg.obj;
			        //System.out.println("H02");
			        //this.post(new Messenger(activity.getApplicationContext()));
			        activity.finish();
			        //System.out.println("H03");
			    }
			};
			
			handler.sendMessageDelayed(message, 1000*period); 
			//System.out.println("F3");
		}catch(Exception e){
			//System.out.println("G:"+e.getMessage());
			e.printStackTrace();
		}
	}

}
