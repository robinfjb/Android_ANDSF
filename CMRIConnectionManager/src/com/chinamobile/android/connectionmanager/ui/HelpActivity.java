package com.chinamobile.android.connectionmanager.ui;

import com.chinamobile.android.connectionmanager.R;

import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

/**
 * Help page activity
 *
 */
public class HelpActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help);
		
		TitleBar mTitle = (TitleBar) findViewById(R.id.TitleBar);
		mTitle.setParameter(R.string.help_title);
		
		TextView textView = (TextView) findViewById(R.id.help_content);
		textView.setText(Html.fromHtml(getResources().getString(R.string.help_content)));
		super.onCreate(savedInstanceState);
	}

	
}
