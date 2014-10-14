package com.chinamobile.android.connectionmanager.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.R;

/**
 * top title bar view
 *
 */
public class TitleBar extends LinearLayout{
	private Button leftBtn;
	private Button rightBtn;
	private TextView titleText;
//	private String titleStr;
//	private boolean showLeftBtn;
//	private boolean showRightBtn;
	
	public TitleBar(Context context) {
		super(context);
		init(context);
	}
	
	public void setParameter(int titleStr) {
		this.setParameter(titleStr, 0, 0, false, false);
	}
	
	public void setParameter(int titleStr, int leftStr, int rightStr, boolean showLeftBtn,
			boolean showRightBtn) {
		Resources resource = getResources();
		titleText.setText(resource.getString(titleStr));
		if(leftStr != 0) {
			leftBtn.setText(resource.getString(leftStr));
		}
		if(rightStr != 0) {
			rightBtn.setText(resource.getString(rightStr));
		}
		leftBtn.setVisibility(showLeftBtn ? View.VISIBLE : View.GONE);
		rightBtn.setVisibility(showRightBtn ? View.VISIBLE : View.GONE);
		invalidate();
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void setLeftOnClickListener(OnClickListener listener) {
		leftBtn.setOnClickListener(listener);
	}
	
	public void setRightOnClickListener(OnClickListener listener) {
		rightBtn.setOnClickListener(listener);
	}
	
	private void init(Context context) {
		this.setGravity(Gravity.CENTER_VERTICAL);
		this.setBackgroundResource(R.drawable.title_background);
		
		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(infService);
		li.inflate(R.layout.titlebar, this);
		
		leftBtn = (Button) findViewById(R.id.cancel_ation);
		rightBtn = (Button) findViewById(R.id.save_action);
		titleText = (TextView) findViewById(R.id.titlebar_title);
		
	}
}
