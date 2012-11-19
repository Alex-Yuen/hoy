package ws.hoyland.android.advx;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MainActivity extends Activity {

	private ImageView iv;
    private Bitmap img1 = null;
    private Bitmap img2 = null;
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
			this.img2 = BitmapFactory.decodeByteArray(bm, 0, bm.length);
		}
		
        if(this.sf){
        	bm = intent.getByteArrayExtra("img1");
    		if (bm != null) {
    			this.img1 = BitmapFactory.decodeByteArray(bm, 0, bm.length);
    			iv.setImageBitmap(img1);
    			setContentView(iv);
    		}
        }else{
        	iv.setImageBitmap(img2);
			setContentView(iv);
        }

		Handler handler = new Handler();
		handler.postDelayed(new ImageSwitcher(this), 1000*this.period);
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
