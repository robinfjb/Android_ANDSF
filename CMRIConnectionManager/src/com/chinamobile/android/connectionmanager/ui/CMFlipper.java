package com.chinamobile.android.connectionmanager.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * view flipper handled IllegalArgumentException
 *
 */
public class CMFlipper extends ViewFlipper{

	public CMFlipper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public CMFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		try {
			super.onDetachedFromWindow();
		} catch (IllegalArgumentException e) {
			stopFlipping();
		}
	}
}
