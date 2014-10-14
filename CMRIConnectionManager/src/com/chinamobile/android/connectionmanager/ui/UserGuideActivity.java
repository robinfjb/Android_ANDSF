package com.chinamobile.android.connectionmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;

/**
 * Activity for user guide page
 *
 */
public class UserGuideActivity extends BaseActivity implements OnGestureListener{
	private ImageView[] tv_points = new ImageView[4];
	private int imageId[]= {
			R.drawable.guide_account,
			R.drawable.guide_home,
			R.drawable.guide_map_pin,
			R.drawable.guide_map_pop};
	private int[] pointsId ={
			R.id.point_1,
			R.id.point_2,
			R.id.point_3,
			R.id.point_4};
	private float mScreenHeight ;
	private float mScreenWidth;
	private Handler mHandler;
	private GestureDetector detector;
	private CMFlipper flipper;
	private Button startBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
		boolean userGuideBool = sp.getBoolean("looked_userguide", false);
		if(userGuideBool) {
			startActivity(new Intent(UserGuideActivity.this, HomeActivity.class));
			UserGuideActivity.this.finish();
			return;
		}
		
		setContentView(R.layout.user_guide);
		DisplayMetrics dm =  new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenHeight = dm.heightPixels;
		mScreenWidth = dm.widthPixels;
		
		if (!userGuideBool) {
			Editor editor = sp.edit();
			editor.putBoolean("looked_userguide", true);
			editor.commit();
		}
		
		for (int i = 0; i < tv_points.length; i++) {
			tv_points[i] = (ImageView) findViewById(pointsId[i]);
		}
		
		detector = new GestureDetector(this);
		flipper = (CMFlipper) this.findViewById(R.id.viewFlipper);

		for (int i = 0; i < imageId.length; i++) {
			flipper.addView(addImageView(imageId[i]));
		}
		
		startBtn = (Button) findViewById(R.id.rl_goback);
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserGuideActivity.this, HomeActivity.class));
				UserGuideActivity.this.finish();
			}
		});
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				for (int i = 0; i < tv_points.length; i++) {
				tv_points[i].setImageResource(R.drawable.page_gray);
			}
				tv_points[msg.what].setImageResource(R.drawable.page_cover);
				if(msg.what == 3) {
					startBtn.setVisibility(View.VISIBLE);
				} else {
					startBtn.setVisibility(View.GONE);
				}
				super.handleMessage(msg);
			}
			
		};
	}

	/**
	 * add image view
	 * @param resId
	 * @return
	 */
	private ImageView addImageView(int resId) {
		ImageView img = new ImageView(this);
		img.setImageResource(resId);
		img.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		return img;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.detector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP ) {   
			if (flipper.getDisplayedChild() == 3 ) {
//				if (isNormalScreen) {
//					if ((mScreenHeight - event.getY()) < 180 && (mScreenHeight- event.getY())>90 ){
//						UserGuideActivity.this.finish();
//						return true;
//					} 
//				}else {
//					if ((mScreenHeight - event.getY()) < 85 && (mScreenHeight- event.getY())>40 ){
//						UserGuideActivity.this.finish();
//						return true;
//					} 
//				}
			}
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			UserGuideActivity.this.finish();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 70) {
			if (flipper.getDisplayedChild() == 3) {
				return false;
			}
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
			this.flipper.showNext();
			mHandler.sendEmptyMessage(flipper.getDisplayedChild());
			return true;
		} else if (e1.getX() - e2.getX() < -70) {
			if (flipper.getDisplayedChild() == 0) {
				return false;
			}
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
			this.flipper.showPrevious();
			mHandler.sendEmptyMessage(flipper.getDisplayedChild());
			return true;
		}
		return false;
	}

}
