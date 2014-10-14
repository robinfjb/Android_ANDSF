/*package com.chinamobile.android.connectionmanager.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

import com.chinamobile.android.connectionmanager.R;

public class TimerBackgroundView extends LinearLayout{
	Paint paint; 
	public TimerBackgroundView (Context context){
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShadowLayer(5f, 5.0f, 5.0f, Color.BLACK);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    }
	
	@Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int posX = 20;
        int posY = 20;
        int PicWidth,PicHegiht; 
        
        Drawable drawable = getResources().getDrawable(R.drawable.timer_bg);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.timer_bg);
        PicWidth = drawable.getIntrinsicWidth();
        PicHegiht = drawable.getIntrinsicHeight();
        canvas.drawColor(Color.WHITE);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        //Rect rect = new Rect(2*posX + PicWidth, 2*posY + PicHegiht, 2*posX + 2*PicWidth, 2*posY + 2*PicHegiht);//此为理论上的阴影图坐标
        Rect rect = new Rect(0, 0, PicWidth + 0, posY);
        
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, 10f, 10f, paint);
        canvas.restore();
    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("error mode.");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("error mode.");
		}
	}
	
	
}
*/