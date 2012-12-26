package ws.hoyland.android.advx4;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {

	private ImageView iv;
    private Bitmap img1 = null;
    private Bitmap img2 = null;
    private byte[] img2bs = null;
    private int type = 0;
    private boolean sf = false;
    private int period = 5;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        iv = new ImageView(this);
        Intent intent = getIntent();
        this.period = intent.getIntExtra("period", 5);
        this.sf = intent.getBooleanExtra("sf", true);
        this.type = intent.getIntExtra("type", 0);

    	byte[] bm = intent.getByteArrayExtra("img2");
		if (bm != null) {
			if(this.type!=0){
				this.img2bs = bm.clone();
			}
			this.img2 = BitmapFactory.decodeByteArray(bm, 0, bm.length);
		}
		//System.out.println("A");
        if(this.sf){
        	//System.out.println("B");
        	bm = intent.getByteArrayExtra("img1");
    		if (bm != null) {
    			//System.out.println("C");
    			this.img1 = BitmapFactory.decodeByteArray(bm, 0, bm.length);
    			iv.setImageBitmap(img1);
    			setContentView(iv);
    			//System.out.println("D");
    		}
        }else{
        	if(type==0){
	        	iv.setImageBitmap(img2);
				setContentView(iv);
        	}else{
        		GifView gif = new GifView(this);
				//System.out.println(gif); 
				gif.setGifImage(getImg2bs()); // 添加监听器 //
				//gif.setOnClickListener(this); // 设置显示的大小，拉伸或者压缩
				//gif.set
				//gif.set
				//img2.getWidth()/img2.getHeight() = dm.widthPixels/x;
				//x = dm.widthPixels*img2.getHeight()/img2.getWidth()
				DisplayMetrics dm = new DisplayMetrics();  
				getWindowManager().getDefaultDisplay().getMetrics(dm);   
				gif.setShowDimension(dm.widthPixels, dm.heightPixels); // 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
				gif.setGifImageType(GifImageType.COVER); //R.layout.activity_main
				setContentView(gif);
        	}
        }
        //System.out.println("BC2:"+this.getBaseContext());
        //System.out.println("BC2:"+this.getParent().getBaseContext());
		Handler handler = new Handler();
		handler.postDelayed(new ImageSwitcher(this), 1000*this.period);
    }
    
	@Override
	protected void onDestroy() {
		//System.out.println("EEFG");
		/**
		//System.out.println("H11");
		Message message = new Message();
		message.obj = this;
		
		Handler handler = new Handler(){
		    public void handleMessage(Message msg) {
		    	//System.out.println("H12");
		        Activity activity = (Activity) msg.obj;
		        this.post(new Messenger(activity.getApplicationContext()));
		        //System.out.println("H13");
		    }
		};
		//System.out.println("H14");
		handler.sendMessage(message);
		//System.out.println("H15");**/
		Handler handler = new Handler();
		handler.post(new Messenger(MainActivity.this.getApplicationContext(), null));
		super.onDestroy();
	}


	public byte[] getImg2bs() {
		return img2bs;
	}

	public void setImg2bs(byte[] img2bs) {
		this.img2bs = img2bs;
	}

	public ImageView getIv() {
		return iv;
	}

	public void setIv(ImageView iv) {
		this.iv = iv;
	}

	public Bitmap getImg1() {
		return img1;
	}

	public void setImg1(Bitmap img1) {
		this.img1 = img1;
	}

	public Bitmap getImg2() {
		return img2;
	}

	public void setImg2(Bitmap img2) {
		this.img2 = img2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isSf() {
		return sf;
	}

	public void setSf(boolean sf) {
		this.sf = sf;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}
	
	
	
}
