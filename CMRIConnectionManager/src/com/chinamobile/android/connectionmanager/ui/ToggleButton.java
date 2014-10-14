package com.chinamobile.android.connectionmanager.ui;

import com.chinamobile.android.connectionmanager.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
@Deprecated
public class ToggleButton extends View implements OnTouchListener {
	private Bitmap switch_on_Bkg, switch_off_Bkg;
	private boolean isSwitchOn = false;
	private OnSwitchListener onSwitchListener;
	
	// to prevent user touch many times in a short time
	private Handler keyHander = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				isSwitchOn = !isSwitchOn;
				onSwitchListener.onSwitched(isSwitchOn);
				invalidate();
			}
			super.handleMessage(msg);
		}
		
	};

	public ToggleButton(Context context) {
		super(context);
		init();
	}

	public ToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void setImageResource() {
		switch_on_Bkg = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_toggle_on);
		switch_off_Bkg = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_toggle_off);
	}

	private void init() {
		setOnTouchListener(this);
		setImageResource();
	}

	protected void setSwitchState(boolean switchState) {
		isSwitchOn = switchState;
		invalidate();
	}

	protected boolean getSwitchState() {
		return isSwitchOn;
	}

	@SuppressLint({ "ResourceAsColor", "DrawAllocation" })
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int height = getHeight();
		int width = getWidth();

		Paint paint = new Paint();
		Paint textPaint = new Paint();
		textPaint.setColor(0xff474a4b);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(getResources().getDimension(R.dimen.toggle_textsize));
		FontMetrics fontMetrics = textPaint.getFontMetrics(); 
		float fontHeight = fontMetrics.bottom - fontMetrics.top;
		float textBaseY = height - (height - fontHeight) / 2 - fontMetrics.bottom; 

		if (isSwitchOn) {
			canvas.drawBitmap(switch_on_Bkg,
					(width - switch_on_Bkg.getWidth()) / 2,
					(height - switch_on_Bkg.getHeight()) / 2,
					paint);
			String wifiOn = getResources().getString(R.string.wifi_toggle_on);
			canvas.drawText(wifiOn,
					(width - textPaint.measureText(wifiOn)) / 2 - 5,
					textBaseY,
					textPaint);
		} else {
			canvas.drawBitmap(switch_off_Bkg,
					(width - switch_off_Bkg.getWidth()) / 2,
					(height - switch_off_Bkg.getHeight()) / 2,
					paint);
			String wifiOff = getResources().getString(R.string.wifi_toggle_off);
			canvas.drawText(wifiOff,
					(width - textPaint.measureText(wifiOff)) / 2 + 5,
					textBaseY,
					textPaint);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		// ËÉ¿ª
		case MotionEvent.ACTION_UP:
			keyHander.removeMessages(0);
			keyHander.sendEmptyMessageDelayed(0, 500);
			break;
		default:
			break;
		}
		return true;
	}

	public void setOnSwitchListener(OnSwitchListener listener) {
		onSwitchListener = listener;
	}

	public interface OnSwitchListener {
		abstract void onSwitched(boolean isSwitchOn);
	}

}
