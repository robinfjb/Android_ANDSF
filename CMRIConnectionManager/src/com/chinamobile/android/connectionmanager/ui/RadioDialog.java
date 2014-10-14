package com.chinamobile.android.connectionmanager.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.R;

/**
 * Dialog with radio group adapter
 *
 */
public class RadioDialog extends Dialog{
	private ListView listView;
	private Context context;
	private String title;
	private String[] content;
	private OnItemClickListener listener;
	private CheckBox rememberChk;
	private View checkboxLine;
	private boolean hideCheckbox;
	
	public RadioDialog(Context context, String title, String[] content, OnItemClickListener onItemClickListener
			, int theme) {
		this(context, title, content, onItemClickListener, theme, true);
	}
	
	public RadioDialog(Context context, String title, String[] content, OnItemClickListener onItemClickListener
			, int theme, boolean hideCheckbox) {
		super(context, theme);
		this.context = context;
		this.title = title;
		this.listener = onItemClickListener;
		this.content = content;
		this.hideCheckbox = hideCheckbox;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_radio);
		
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		LayoutParams p = getWindow().getAttributes();
		p.height = (int) (LayoutParams.WRAP_CONTENT);
		p.width = (int) (screenWidth * 0.8);
		getWindow().setAttributes(p);
		
		listView = (ListView) findViewById(R.id.dialog_list_items);
		listView.setOnItemClickListener(listener);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.array_adapter, content);
		listView.setAdapter(adapter);
		
		TextView textView = (TextView) findViewById(R.id.dialog_title_msg);
		textView.setText(title);
		
		checkboxLine = findViewById(R.id.dialog_checkbox_item_line);
				
		rememberChk = (CheckBox) findViewById(R.id.dialog_checkbox_item);
		if(hideCheckbox) {
			rememberChk.setVisibility(View.GONE);
			checkboxLine.setVisibility(View.GONE);
		}
	}
	
	public boolean getCheckBoxStates() {
		return rememberChk.isChecked();
	}
}
