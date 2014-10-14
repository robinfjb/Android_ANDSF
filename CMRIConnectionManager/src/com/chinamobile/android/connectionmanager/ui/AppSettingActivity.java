package com.chinamobile.android.connectionmanager.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.database.DBAdpter;
import com.chinamobile.android.connectionmanager.model.AppModel;
import com.chinamobile.android.connectionmanager.util.Constants;

/**
 * App setting activity
 *
 */
public class AppSettingActivity extends BaseActivity implements OnClickListener, OnItemLongClickListener{
	private static final String TAG = "AppSettingActivity";
	private TitleBar titleBar;
	private Button saveBtn;
	private Button addBtn;
	private ListView appList;
	private AppAdapter mAdapter;
	private ListDialog listDialog;
	private ProgressBar mProgress;
	private DBAdpter mDbAdapter;
	private Context context;
	private List<AppModel> selectedList = new ArrayList<AppModel>();
	private RelativeLayout guideArea;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_setting);
		context = AppSettingActivity.this;
		titleBar = (TitleBar) findViewById(R.id.TitleBar);
		titleBar.setParameter(R.string.app_policy_title);
		appList = (ListView) findViewById(R.id.select_app_list);
		appList.setOnItemLongClickListener(this);
		mAdapter = new AppAdapter(context);
		appList.setAdapter(mAdapter);
		
		mProgress = (ProgressBar) findViewById(R.id.select_app_progress);
		saveBtn = (Button) findViewById(R.id.save_setting_action);
		saveBtn.setOnClickListener(this);
		saveBtn.setEnabled(false);
		addBtn = (Button) findViewById(R.id.add_app_action);
		addBtn.setOnClickListener(this);
		addBtn.setEnabled(false);
		mDbAdapter = new DBAdpter(context);
		
		final SharedPreferences sp = getSharedPreferences(AppApplication.SP_DATA_NAME,
				Context.MODE_PRIVATE);
		guideArea = (RelativeLayout) findViewById(R.id.app_policy_tip_area);
		guideArea.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				guideArea.setVisibility(View.GONE);
				Editor editor = sp.edit();
				editor.putBoolean("app_need_guide", false);
				editor.commit();
				return true;
			}
		});
		boolean guide = sp.getBoolean("app_need_guide", true);
		if(!guide) {
			guideArea.setVisibility(View.GONE);
		}
		
		updateAppInfo();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.add_app_action) {
			if(listDialog == null) {
				listDialog = new ListDialog(context, 
						getText(R.string.app_select_title).toString(), R.style.CMDialog);
				listDialog.setLeftListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						selectedList.addAll(listDialog.getSelectedAppList());
						listDialog.dismiss();
						mAdapter.notifyDataSetChanged();
					}
				});
				listDialog.setRightListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(listDialog != null) {
							listDialog.dismiss();
						}
					}
				});
			}
			listDialog.show();
			new AsyncTask<Void, Void, List<AppModel>>() {
				@Override
				protected List<AppModel> doInBackground(Void... params) {
					return queryAppInfo();
				}

				@Override
				protected void onPostExecute(List<AppModel> result) {
					super.onPostExecute(result);
					listDialog.setAppList(result);
					listDialog.dismissProgressBar();
				}
			}.execute();
			
			
		} else if(v.getId() == R.id.save_setting_action) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						mDbAdapter.openDatabase();
						mDbAdapter.addAppList(selectedList);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						mDbAdapter.close();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					context.sendBroadcast(new Intent(Constants.Action.ACTION_APP_UPDATE));
					mProgress.setVisibility(View.GONE);
					saveBtn.setEnabled(true);
					super.onPostExecute(result);
				}

				@Override
				protected void onPreExecute() {
					mProgress.setVisibility(View.VISIBLE);
					saveBtn.setEnabled(false);
					super.onPreExecute();
				}
				
			}.execute();
			
			
		} 
	}
	
	RadioDialog deleteDialog = null;
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		
		deleteDialog = new RadioDialog(context,
				getText(R.string.delete_lable).toString(),
				new String[]{getText(R.string.delete_item).toString()},
				new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent,
							View view, int p, long id) {
						selectedList.remove(position);
						mAdapter.notifyDataSetChanged();
						deleteDialog.dismiss();
//						deleteHandler.sendEmptyMessage(0);
					}
				}, R.style.CMDialog);
		deleteDialog.setCanceledOnTouchOutside(false);
		deleteDialog.show();
		
		return true;
	}
	
//	Handler deleteHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			if(msg.what == 0) {
//				new AsyncTask<Void, Void, Void>() {
//					@Override
//					protected Void doInBackground(Void... params) {
//						try {
//							mDbAdapter.openDatabase();
//							mDbAdapter.addAppList(selectedList);
//						} catch (Exception e) {
//							e.printStackTrace();
//						} finally {
//							mDbAdapter.close();
//						}
//						return null;
//					}
//
//					@Override
//					protected void onPostExecute(Void result) {
//						context.sendBroadcast(new Intent(Constants.Action.ACTION_APP_UPDATE));
//						mProgress.setVisibility(View.GONE);
//						saveBtn.setEnabled(true);
//						super.onPostExecute(result);
//					}
//
//					@Override
//					protected void onPreExecute() {
//						mProgress.setVisibility(View.VISIBLE);
//						saveBtn.setEnabled(false);
//						super.onPreExecute();
//					}
//					
//				}.execute();
//			}
//			super.handleMessage(msg);
//		}
//		
//	};
	
	/**
	 * get app info in async task
	 */
	private void updateAppInfo() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					mDbAdapter.openDatabase();
					selectedList = mDbAdapter.readAppList();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mDbAdapter.close();
				}
				List<ResolveInfo> result = getAllApp();
				PackageManager pm = getPackageManager();
				for (AppModel appModel : selectedList) {
					for (ResolveInfo resolveInfo : result) {
						if (appModel.getPkgName()
								.equals(resolveInfo.activityInfo.applicationInfo.packageName)) {
							appModel.setAppIcon(resolveInfo.loadIcon(pm));
						}
					}
				}
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mProgress.setVisibility(View.GONE);
				addBtn.setEnabled(true);
				saveBtn.setEnabled(true);
				mAdapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
			
			

		}.execute();
	}
	
	/**
	 * get all app from system querying
	 * @return
	 */
	private List<ResolveInfo> getAllApp() {
		Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
		mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolves = getPackageManager().queryIntentActivities(
				mIntent, 0);
		/*int size = resolves.size();
		for (int i = 0; i < size; i++) {
			ResolveInfo resolveInfo = resolves.get(i);
			if(resolveInfo.activityInfo.applicationInfo.packageName
					.equals("com.chinamobile.android.connectionmanager")) {
				resolves.remove(i);
			}
		}*/
		return resolves;
	}
	
	/**
	 * query all app info
	 * @return
	 */
	private List<AppModel> queryAppInfo() {
		List<AppModel> mlistAppInfo = new ArrayList<AppModel>();
		List<ResolveInfo> resolves = getAllApp();
		PackageManager pm = getPackageManager();
		for (ResolveInfo resolve : resolves) {
			AppModel appInfo = new AppModel();
			appInfo.setAppLabel((String) resolve.loadLabel(pm));
			appInfo.setAppIcon(resolve.loadIcon(pm));
			appInfo.setPkgName(resolve.activityInfo.applicationInfo.packageName);
			if(!selectedList.contains(appInfo)) {
				mlistAppInfo.add(appInfo);
			}
		}
		return mlistAppInfo;
	}
	
	/**
	 * Adapter view of apps
	 *
	 */
	private class AppAdapter extends BaseAdapter{
		private LayoutInflater inflater = null;
//		private ViewHolder holder = null;
		ImageView appIcon;
		TextView appLabel;
		RadioGroup appRadioGroup;
		RadioButton wifiRadio;
		RadioButton g3Radio;
		public AppAdapter(Context context) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return selectedList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return selectedList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final AppModel appInfo = (AppModel) getItem(position);
			View view = inflater.inflate(R.layout.adapter_select_app_item, null);
			
			this.appIcon = (ImageView) view.findViewById(R.id.app_img);
			this.appLabel = (TextView) view.findViewById(R.id.app_label);
			this.appRadioGroup = (RadioGroup) view.findViewById(R.id.app_radio_group);
			this.wifiRadio = (RadioButton) view.findViewById(R.id.app_radio_group_wifi);
			this.g3Radio = (RadioButton) view.findViewById(R.id.app_radio_group_3g);
			
			this.appIcon.setImageDrawable(appInfo.getAppIcon());
			this.appLabel.setText(appInfo.getAppLabel());
			this.appRadioGroup.check(appInfo.getNetworkPri() - 1);
			if(appInfo.getNetworkPri() == AppModel.WIFI_FIRST) {
				this.wifiRadio.setChecked(true);
			} else if(appInfo.getNetworkPri() == AppModel.G3_FIRST) {
				this.g3Radio.setChecked(true);
			}
			
			this.appRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if(checkedId == wifiRadio.getId()) {
						appInfo.setNetworkPri(AppModel.WIFI_FIRST);
					} else if(checkedId == g3Radio.getId()) {
						appInfo.setNetworkPri(AppModel.G3_FIRST);
					}
				}
			});
			
			return view;
		}
		
//		class ViewHolder {
//			ImageView appIcon;
//			TextView appLabel;
//			RadioGroup appRadioGroup;
//			RadioButton wifiRadio;
//			RadioButton g3Radio;
//
//			
//			public ViewHolder(View view) {
//				this.appIcon = (ImageView) view.findViewById(R.id.app_img);
//				this.appLabel = (TextView) view.findViewById(R.id.app_label);
//				this.appRadioGroup = (RadioGroup) view.findViewById(R.id.app_radio_group);
//				this.wifiRadio = (RadioButton) view.findViewById(R.id.app_radio_group_wifi);
//				this.g3Radio = (RadioButton) view.findViewById(R.id.app_radio_group_3g);
//			}
//		}
	}
}
