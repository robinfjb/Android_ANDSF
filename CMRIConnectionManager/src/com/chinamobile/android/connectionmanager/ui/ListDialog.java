package com.chinamobile.android.connectionmanager.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.model.AppModel;

/**
 * Dialog with list adapter
 *
 */
public class ListDialog extends Dialog {
	private ListView listView;
	private Context context;
	private String title;
	private TextView titleView;
	private AppAdapter adapter;
	private Button leftBtn;
	private Button rightBtn;
	private ProgressBar mProgressBar;
	private android.view.View.OnClickListener leftListener;
	private android.view.View.OnClickListener rightListener;

	public ListDialog(Context context, String title, int theme) {
		super(context, theme);
		this.context = context;
		this.title = title;
		adapter = new AppAdapter(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.dialog_list);

		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		LayoutParams p = getWindow().getAttributes();
		p.height = (int) (screenHeight * 0.8);
		p.width = (int) (screenWidth * 0.8);
		getWindow().setAttributes(p);

		titleView = (TextView) findViewById(R.id.dialog_title_msg);
		titleView.setText(title);

		listView = (ListView) findViewById(R.id.dialog_list_items);
		listView.setAdapter(adapter);
		listView.setFastScrollEnabled(true);
		
		leftBtn = (Button) findViewById(R.id.dialog_left_button_msg);
		rightBtn = (Button) findViewById(R.id.dialog_right_button_msg);
		leftBtn.setOnClickListener(leftListener);
		rightBtn.setOnClickListener(rightListener);
		
		mProgressBar = (ProgressBar) findViewById(R.id.drag_list_progress);
		
		adapter.notifyDataSetChanged();
	}

	public void setAppList(List<AppModel> list) {
		adapter.updateAppList(list);
	}

	public void setLeftListener(android.view.View.OnClickListener leftListener) {
		this.leftListener = leftListener;
	}

	public void setRightListener(android.view.View.OnClickListener rightListener) {
		this.rightListener = rightListener;
	}
	
	public void dismissProgressBar() {
		if(mProgressBar != null) {
			mProgressBar.setVisibility(View.GONE);
		}
	}
	
	public List<AppModel> getSelectedAppList() {
		List<AppModel> selectedList = new ArrayList<AppModel>();
		for (AppModel appModel : adapter.mlistAppInfo) {
			if(appModel.isChecked()) {
				selectedList.add(appModel);
			}
		}
		return selectedList;
	}

	private class AppAdapter extends BaseAdapter {
		private LayoutInflater inflater = null;
		public List<AppModel> mlistAppInfo = new ArrayList<AppModel>();

		public AppAdapter(Context context) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void updateAppList(List<AppModel> list) {
			mlistAppInfo.clear();
			mlistAppInfo.addAll(list);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mlistAppInfo.size();
		}

		@Override
		public Object getItem(int position) {
			return mlistAppInfo.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;
			if (convertView == null || convertView.getTag() == null) {
				view = inflater.inflate(R.layout.adapter_app_item, null);
				holder = new ViewHolder(view);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) convertView.getTag();
			}
			AppModel appInfo = (AppModel) getItem(position);
			holder.appIcon.setImageDrawable(appInfo.getAppIcon());
			holder.appLabel.setText(appInfo.getAppLabel());
			holder.appBox.setChecked(appInfo.isChecked());
			holder.appBox.setOnClickListener(new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AppModel app =  mlistAppInfo.get(position);
					CheckBox appBox = (CheckBox) v.findViewById(R.id.app_checkbox_dailog);
					app.setChecked(appBox.isChecked());
				}
			});
			return view;
		}

		class ViewHolder {
			ImageView appIcon;
			TextView appLabel;
			CheckBox appBox;

			public ViewHolder(View view) {
				this.appIcon = (ImageView) view.findViewById(R.id.app_img_dailog);
				this.appLabel = (TextView) view.findViewById(R.id.app_label_dailog);
				this.appBox = (CheckBox) view.findViewById(R.id.app_checkbox_dailog);
			}
		}
	}

}
