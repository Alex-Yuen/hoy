package ws.hoyland.android.advx4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

/** */
/**
 * 
 * TODO 单行文本跑马灯控件
 * 
 * @author tianlu
 * @version 1.0 Create At : 2010-2-16 下午09:35:03
 */
@SuppressLint("DrawAllocation")
public class AutoScrollTextView extends TextView implements OnClickListener {
	public final static String TAG = AutoScrollTextView.class.getSimpleName();

	private float textLength = 0f;// 文本长度
	private float viewWidth = 0f;
	private float step = 0f;// 文字的横坐标
	private float y = 0f;// 文字的纵坐标
	private float temp_view_plus_text_length = 0.0f;// 用于计算的临时变量
	private float temp_view_plus_two_text_length = 0.0f;// 用于计算的临时变量
	public boolean isStarting = false;// 是否开始滚动
	private Paint paint = null;// 绘图样式
	private String text = "";// 文本内容
	private int t = 0;
	private MessageActivity activity = null;

	private int ts;

	private boolean flag = false;

	public AutoScrollTextView(Context context) {
		super(context);
		initView();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	/** */
	/**
	 * 初始化控件
	 */
	private void initView() {
		setOnClickListener(this);
	}

	/** */
	/**
	 * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下
	 */
	public void init(WindowManager windowManager) {
		paint = getPaint();
		text = getText().toString();
		textLength = paint.measureText(text);
		viewWidth = getWidth();
		if (viewWidth == 0) {
			if (windowManager != null) {
				Display display = windowManager.getDefaultDisplay();
				viewWidth = display.getWidth();
			}
		}
		step = textLength;
		temp_view_plus_text_length = viewWidth + textLength;
		temp_view_plus_two_text_length = viewWidth + textLength * 2;
		y = getTextSize() + getPaddingTop();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);

		ss.step = step;
		ss.isStarting = isStarting;

		return ss;

	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		step = ss.step;
		isStarting = ss.isStarting;

	}

	public static class SavedState extends BaseSavedState {
		public boolean isStarting = false;
		public float step = 0.0f;

		SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeBooleanArray(new boolean[] { isStarting });
			out.writeFloat(step);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}

			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
		};

		private SavedState(Parcel in) {
			super(in);
			boolean[] b = new boolean[1];
			in.readBooleanArray(b);
			if (b != null && b.length > 0)
				isStarting = b[0];
			step = in.readFloat();
		}
	}

	/** */
	/**
	 * 开始滚动
	 */
	public void startScroll() {
		isStarting = true;
		invalidate();
	}

	/** */
	/**
	 * 停止滚动
	 */
	public void stopScroll() {
		isStarting = false;
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
		if (!isStarting) {
			return;
		}
		step += 3.5;
		if (step > temp_view_plus_two_text_length){
			t++;
			step = textLength;
		}
		invalidate();
		
		if(t==ts&&!flag){
			this.flag  = true;
			Message message = new Message();
			message.what = 0x12;
			this.activity.send(message);
		}
	}

	@Override
	public void onClick(View v) {
		if (isStarting)
			stopScroll();
		else
			startScroll();
	}

	public void setActivity(MessageActivity activity) {
		this.activity = activity;
		
	}

	public void setTS(int ts) {
		this.ts = ts;
		
	}	
	
}