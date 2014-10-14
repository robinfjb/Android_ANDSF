package com.chinamobile.android.connectionmanager.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

@Deprecated
public class CMEditText extends EditText{
	private Paint mPaint;
	private String osVersion;
	int paddingX;
	int paddingY;
	public CMEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(2);
		
		osVersion = Build.VERSION.RELEASE;
		paddingX = 5;
		paddingY = 5;
//		if(osVersion.compareTo("4.0") >= 0) {
//			setBackgroundDrawable(null);
//			setPadding(paddingX, paddingY, paddingX, paddingY);
//		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
//		if(osVersion.compareTo("4.0") >= 0) {
//			if(isEnabled() && isFocusable() && isFocusableInTouchMode()) {
//				mPaint.setColor(0xFF4cb8db);
//			} else {
//				mPaint.setColor(Color.GRAY);
//			}
//			canvas.drawLine(paddingX, getHeight() - 1 - paddingY, getWidth() - 1 - paddingX, getHeight() - 1 - paddingY, mPaint);
//			canvas.drawLine(paddingX, getHeight() - 6 - paddingY, paddingX, getHeight() - 1 - paddingY, mPaint);
//			canvas.drawLine(getWidth() - 1 - paddingX, getHeight() - 6 - paddingY, getWidth() - 1 - paddingX, getHeight() - 1 - paddingY, mPaint);
//		}
		super.onDraw(canvas);
	}
}