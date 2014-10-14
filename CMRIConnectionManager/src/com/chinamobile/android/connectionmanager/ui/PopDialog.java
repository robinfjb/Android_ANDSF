package com.chinamobile.android.connectionmanager.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.R;

/**
 * Dialog with selection button adapter
 *
 */
public class PopDialog extends Dialog {
	private android.view.View.OnClickListener leftListener;
	private android.view.View.OnClickListener rightListener;
	private TextView titleView;
	private TextView contentView;
	private Button leftBtn;
	private Button rightBtn;
	private Spanned title;
	private Spanned content;
	private String leftBtnMsg;
	private String rightBtnMsg;
	private boolean twoButton;
	private Context context;
	private CheckBox rememberChk;
	private View checkboxLine;
	private boolean hideCheckbox;
	
	public PopDialog(Context context, String title, String content, String leftBtnMsg, String rightBtnMsg, boolean twoButton, int theme, boolean hideCheckbox) {
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
		this.leftBtnMsg = leftBtnMsg;
		this.rightBtnMsg = rightBtnMsg;
		this.twoButton = twoButton;
		this.hideCheckbox = hideCheckbox;
	}
	
	public PopDialog(Context context, String title, String content, String leftBtnMsg, String rightBtnMsg, boolean twoButton, int theme) {
		this(context, title, content, leftBtnMsg, rightBtnMsg, twoButton, theme, true);
	}
	
	public PopDialog(Context context, String content, String leftBtnMsg, String rightBtnMsg, boolean twoButton, int theme) {
		this(context, null, content, leftBtnMsg, rightBtnMsg, twoButton, theme);
	}
	
	public PopDialog(Context context, String content, String leftBtnMsg, String rightBtnMsg, int theme) {
		this(context, null, content, leftBtnMsg, rightBtnMsg, true, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(twoButton) {
			this.setContentView(R.layout.dialog_confirm);
		} else {
			this.setContentView(R.layout.dialog_cancel);
		}
		
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		LayoutParams p = getWindow().getAttributes();
		p.height = (int) (LayoutParams.WRAP_CONTENT);
		p.width = (int) (screenWidth * 0.8);
		getWindow().setAttributes(p);
		
		titleView = (TextView) findViewById(R.id.dialog_title_msg);
		contentView = (TextView) findViewById(R.id.dialog_content_msg);
		leftBtn = (Button) findViewById(R.id.dialog_left_button_msg);
		if(twoButton) {
			rightBtn = (Button) findViewById(R.id.dialog_right_button_msg);
			rightBtn.setText(rightBtnMsg);
			rightBtn.setOnClickListener(rightListener);
		}
		titleView.setText(title);
		contentView.setText(content);
		leftBtn.setText(leftBtnMsg);
		leftBtn.setOnClickListener(leftListener);
		
		rememberChk = (CheckBox) findViewById(R.id.dialog_checkbox_item);
		checkboxLine = findViewById(R.id.dialog_checkbox_item_line);
		if(hideCheckbox) {
			rememberChk.setVisibility(View.GONE);
			checkboxLine.setVisibility(View.GONE);
		}
	}
	
	public boolean getCheckBoxStates() {
		return rememberChk.isChecked();
	}

	public void setLeftListener(android.view.View.OnClickListener leftListener) {
		this.leftListener = leftListener;
	}

	public void setRightListener(android.view.View.OnClickListener rightListener) {
		this.rightListener = rightListener;
	}
}
