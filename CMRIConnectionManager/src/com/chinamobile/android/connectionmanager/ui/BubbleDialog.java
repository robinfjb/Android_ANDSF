/*package com.chinamobile.android.connectionmanager.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.R;

public class BubbleDialog extends Dialog{

	private android.view.View.OnClickListener leftListener;
	private android.view.View.OnClickListener rightListener;
	private TextView titleView;
	private TextView persistContentView;
	private TextView activeContentView;
	private ImageButton closeBtn;
	private Button leftBtn;
	private Button rightBtn;
	private Spanned title;
	private Spanned content;
	private Spanned contentActive;
	private String leftBtnMsg;
	private String rightBtnMsg;
	private Context context;
	
	
	public BubbleDialog(Context context, String title, String content, String contentActive, 
			String leftBtnMsg, String rightBtnMsg, int theme) {
		super(context, theme);
		this.context = context;
		if(title == null) {
			this.title = null;
		} else {
			this.title = Html.fromHtml(title);
		}
		if(content == null) {
			this.content = null;
		} else {
			this.content = Html.fromHtml(content);
		}
		if(contentActive == null) {
			this.contentActive = null;
		} else {
			this.contentActive = Html.fromHtml(contentActive);
		}
		
		this.leftBtnMsg = leftBtnMsg;
		this.rightBtnMsg = rightBtnMsg;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_confirm);
		
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		LayoutParams p = getWindow().getAttributes();
		p.height = (int) (LayoutParams.WRAP_CONTENT);
		p.width = (int) (screenWidth * 0.8);
		getWindow().setAttributes(p);
		
		titleView = (TextView) findViewById(R.id.dialog_title_msg);
		persistContentView = (TextView) findViewById(R.id.map_bubble_presist);
		activeContentView = (TextView) findViewById(R.id.map_bubble_active);
		leftBtn = (Button) findViewById(R.id.dialog_left_button_msg);
		rightBtn = (Button) findViewById(R.id.dialog_right_button_msg);
		closeBtn = (ImageButton) findViewById(R.id.map_bubbleImage);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		leftBtn.setOnClickListener(leftListener);
		rightBtn.setOnClickListener(rightListener);
		
		rightBtn.setText(rightBtnMsg);
		titleView.setText(title);
		persistContentView.setText(content);
		activeContentView.setText(contentActive);
		leftBtn.setText(leftBtnMsg);
	}

	public void setLeftListener(android.view.View.OnClickListener leftListener) {
		this.leftListener = leftListener;
	}

	public void setRightListener(android.view.View.OnClickListener rightListener) {
		this.rightListener = rightListener;
	}
}
*/