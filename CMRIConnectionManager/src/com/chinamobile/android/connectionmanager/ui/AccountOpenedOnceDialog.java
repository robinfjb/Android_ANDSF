package com.chinamobile.android.connectionmanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.chinamobile.android.connectionmanager.R;

/**
 * account opened once dialog
 *
 */
public class AccountOpenedOnceDialog extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.account_open_once_dialog);
		
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		LayoutParams p = getWindow().getAttributes();
		p.height = (int) (LayoutParams.WRAP_CONTENT);
		p.width = (int) (screenWidth * 0.8);
		getWindow().setAttributes(p);
		
		Button cancel = (Button) findViewById(R.id.button_cancel_dialog);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
