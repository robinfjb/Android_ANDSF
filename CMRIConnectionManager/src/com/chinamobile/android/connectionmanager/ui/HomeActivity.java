package com.chinamobile.android.connectionmanager.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.controller.ServiceController;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.test.LogService;
import com.chinamobile.android.connectionmanager.test.LogService.MyBinder;
import com.chinamobile.android.connectionmanager.test.TestTriggerActivity;
import com.chinamobile.android.connectionmanager.ui.ToggleButton.OnSwitchListener;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.WifiUtil;

/**
 * Main page activity
 *
 */
public class HomeActivity extends BaseActivity {
	private static final String TAG = "HomeActivity";
	public static final String SERVICE_NAME = "com.chinamobile.android.connectionmanager."
			+ "controller.ServiceController";
	public static final String ACTION = "com.chinamobile.android.connectionmanager.ui.home";
	public static final String ACTION_HOTSPOT_NOTIFICATION= "com.chinamobile.android.connectionmanager.ui.home.hotspot.notification";
//	public static final String ACTION_WIFI_RADIO_CHANGE= "com.chinamobile.android.connectionmanager.ui.home.wifi.radio.change";
	public static final String ACTION_ACCOUNT = "com.chinamobile.android.connectionmanager.ui.home.account";
	private SharedPreferences sp;
	private Button startBtn;
	private boolean is_start;
	private TextView statusText;
//	private TextView hotspotText;
	private CMChronometer mChronometer;
	private StatusReveiver receiver;
	private IntentFilter filter;
	private RadioDialog preferredAlert;
	private BottomMenu bottom;
	private PopDialog policyApplyAlert;
	private PopDialog accountAlertCmcc;
	private PopDialog accountAlertCmccAuto;
//	private ToggleButton wifiToggle;
//	private BroadcastReceiver wifiRadioReceiver;
//	private IntentFilter wifiRadioFilter;
	private static boolean needShowCmccDailog = false;
	private static boolean needShowAutoDailog = false;

	public static final int STATUS_UNDEFINE = 0x01;
	public static final int STATUS_DOWNLOADING_XML = 0x02;
	public static final int STATUS_NO_CONNECTION = 0x03;
	public static final int STATUS_APPLY_POLICY = 0x04;
	public static final int STATUS_CONNECTING = 0x05;
	public static final int STATUS_CONNECTED = 0x06;
	public static final int STATUS_NEW_POLICY = 0x07;
	public static final int STATUS_SCANNING = 0x08;
	public static final int STATUS_NO_SCAN_RESULT = 0x09;
	public static final int STATUS_APPLY_STATIC = 0x10;
	public static final int STATUS_ALREADY_CONNECTED = 0x11;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		statusText = (TextView) findViewById(R.id.status_msg);
		mChronometer = (CMChronometer) findViewById(R.id.time_chromometer);
		sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
		
		Intent intent = getIntent();
		boolean isPreferredNotify = intent.getBooleanExtra("from_notify_preferred", false);
		int rememberChoice = sp.getInt("preferred_remember", -1);
		if((isPreferredNotify && ServiceController.is_andsf_start)) {
			if(rememberChoice == 0) {
				if(preferredAlert != null && preferredAlert.isShowing()) {
					preferredAlert.dismiss();
				}
				Intent sendIntent = new Intent(Constants.Action.ACTION_PREFERED);
				sendIntent.putExtra("which_wifi", 0);
				sendBroadcast(sendIntent);
			} else if(rememberChoice == 1) {
				if(preferredAlert != null && preferredAlert.isShowing()) {
					preferredAlert.dismiss();
				}
				Intent sendIntent = new Intent(Constants.Action.ACTION_PREFERED);
				sendIntent.putExtra("which_wifi", 1);
				sendBroadcast(sendIntent);
			} else {
				if (preferredAlert == null) {
					preferredAlert = new RadioDialog(HomeActivity.this,
							getResources().getString(
									R.string.preferred_dialog_title),
							getResources().getStringArray(R.array.wifi_choice),
							new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									String[] items = getResources().getStringArray(
											R.array.wifi_choice);
									Toast.makeText(
											HomeActivity.this,
											HomeActivity.this
													.getText(R.string.toast_msg)
													+ " " + items[position],
											Toast.LENGTH_SHORT).show();

									Intent sendIntent = new Intent(Constants.Action.ACTION_PREFERED);
									sendIntent.putExtra("which_wifi", position);
									sendBroadcast(sendIntent);
									
									if(preferredAlert.getCheckBoxStates()) {
										Editor sharedata = sp.edit();
										sharedata.putInt("preferred_remember", position);
										sharedata.commit();
									}
									
									if(preferredAlert != null) {
										preferredAlert.dismiss();
									}
								}
							}, R.style.CMDialog, false);
					preferredAlert.setCanceledOnTouchOutside(false);
					preferredAlert.setCancelable(false);
				}
				if(!preferredAlert.isShowing()) {
					preferredAlert.show();
				}
			}
		}
		
		
		
		boolean policyNotify = intent.getBooleanExtra("from_notify_policy", false);
		Log.i(TAG, "policyNotify:" + policyNotify);
		if(policyNotify && ServiceController.is_andsf_start) {
				policyApplyAlert = new PopDialog(HomeActivity.this,
						getResources().getString(R.string.policy_dialog_title),
						getResources().getString(R.string.policy_dialog_content),
						getResources().getString(R.string.ok),
						getResources().getString(R.string.cancel),
						true,
						R.style.CMDialog);
				policyApplyAlert.setLeftListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent sendIntent = new Intent(Constants.Action.ACTION_POLICY_APPLY);
						sendBroadcast(sendIntent);
						policyApplyAlert.dismiss();
						recoverStatusTxt();
					}
				});
				policyApplyAlert.setRightListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						policyApplyAlert.dismiss();
						recoverStatusTxt();
					}
				});
				policyApplyAlert.setCancelable(false);
				policyApplyAlert.setCanceledOnTouchOutside(false);
			if(!policyApplyAlert.isShowing()) {
				policyApplyAlert.show();
			}
		}
		
		startBtn = (Button) findViewById(R.id.start_service);
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				is_start = !is_start;
				if(is_start) {
					Intent intent = new Intent(Constants.Action.ACTION_START);
					intent.putExtra("from_home", true);
					sendBroadcast(intent);
					startBtn.setText(HomeActivity.this.getResources().getString(R.string.stop));
					
					SharedPreferences sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putBoolean("andsf_running", true);
					editor.commit();
				} else {
					Intent intent = new Intent(Constants.Action.ACTION_STOP);
					sendBroadcast(intent);
					startBtn.setText(HomeActivity.this.getResources().getString(R.string.start));
					
					SharedPreferences sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putBoolean("andsf_running", false);
					editor.commit();
				}
			}
		});
		
		
//		hotspotText = (TextView) findViewById(R.id.hotspot_notification);
		
		ImageView titleText = (ImageView) findViewById(R.id.cmri_logo);
		titleText.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Intent statusIntent = new Intent(HomeActivity.this, TestTriggerActivity.class);
				startActivity(statusIntent);
				return true;
			}
		});
		
		
		
		bottom = (BottomMenu) findViewById(R.id.home_menu_linearlayout);
		bottom.setWhichSelected(0);
		bottom.setMapListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mapIntent = new Intent(HomeActivity.this,
						HotspotActivity.class);
				mapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mapIntent);
			}
		});
		bottom.setAccountListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent setIntent = new Intent(HomeActivity.this,
						AccountActivity.class);
				setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(setIntent);
			}
		});
				
//		wifiToggle = (ToggleButton) findViewById(R.id.wifi_toggle);
//		wifiToggle.setOnSwitchListener(new OnSwitchListener() {
//			@Override
//			public void onSwitched(boolean isSwitchOn) {
//				if(isSwitchOn) {
//					WifiUtil.openWifiRadio(HomeActivity.this);
//				} else {
//					WifiUtil.closeWifiRadio(HomeActivity.this);
//				}
//			}
//		});
		/*boolean isLooked_home = sp.getBoolean("looked_home", false);
		if (!isLooked_home) {
			final TextView help_home = (TextView) findViewById(R.id.help_home);
			help_home.setVisibility(View.VISIBLE);
			help_home.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					help_home.setVisibility(View.GONE);
				}
			});
			Editor editor = sp.edit();
			editor.putBoolean("looked_home", true);
			editor.commit();
		}*/
		
		receiver = new StatusReveiver();
		filter = new IntentFilter(ACTION);
		filter.addAction(ACTION_ACCOUNT);
		filter.addAction(ACTION_HOTSPOT_NOTIFICATION);
//		filter.addAction(ACTION_WIFI_RADIO_CHANGE);
		registerReceiver(receiver, filter);
		
		Intent intents = new Intent(this, LogService.class);
		bindService(intents, conn, Context.BIND_AUTO_CREATE);
		
		/*wifiRadioReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
					wifiToggle.setSwitchState(WifiUtil.isWifiRadioOpenOrOpening(HomeActivity.this));
				}
			}
		};
		wifiRadioFilter = new IntentFilter();
		wifiRadioFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		wifiRadioFilter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
		registerReceiver(wifiRadioReceiver, wifiRadioFilter);*/
	}
	
	
	@Override
	protected void onStart() {
		boolean isServiceStarted = CommonUtil.isServiceStarted(this, SERVICE_NAME);
		Log.i(TAG, "isServiceStarted:" + isServiceStarted);
		if(!isServiceStarted) {
			Log.i(TAG, "start service");
			AppApplication.start_andsf = false;
			Intent serviceIntent = new Intent(HomeActivity.this, ServiceController.class);
			startService(serviceIntent);
		}
		
		is_start = ServiceController.is_andsf_start;//CommonUtil.isServiceStarted(this, SERVICE_NAME);
		startBtn.setText(is_start ? R.string.stop : R.string.start);
		
		updateStatusTxt();
		
		if(needShowAutoDailog && AppApplication.isCMCCAUTOAccountEmpty()) {
			showAccountDialog(2);
			needShowAutoDailog = false;
			needShowCmccDailog = false;
		}
		
		if(needShowCmccDailog && AppApplication.isCMCCAccountEmpty()) {
			showAccountDialog(1);
			needShowAutoDailog = false;
			needShowCmccDailog = false;
		}
		super.onStart();
	}

	
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
//		wifiToggle.setSwitchState(WifiUtil.isWifiRadioOpenOrOpening(this));
		super.onResume();
	}


	@Override
	protected void onStop() {
		AppApplication.time_base = mChronometer.getBase();
		Log.i(TAG, "onStop----->AppApplication.time_base=" + AppApplication.time_base);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		
		if(receiver != null) {
			try {
				unregisterReceiver(receiver);
			} catch (Exception e) {
			}
		}
		
		/*if(wifiRadioReceiver != null) {
			try {
				unregisterReceiver(wifiRadioReceiver);
			} catch (Exception e) {
			}
		}*/
		
		unbindService(conn);
		if(preferredAlert != null) {
			preferredAlert.dismiss();
		}
		if(policyApplyAlert != null) {
			policyApplyAlert.dismiss();
		}
		
		if(accountAlertCmcc != null) {
			if(accountAlertCmcc.isShowing()) {
				setNeedShowCmccDailog(true);
			}
			accountAlertCmcc.dismiss();
		}
		
		if(accountAlertCmccAuto != null) {
			if(accountAlertCmccAuto.isShowing()) {
				setNeedShowAutoDailog(true);
			}
			accountAlertCmccAuto.dismiss();
		}
		
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.setting:
			Intent setIntent = new Intent(this,
					SettingActivity.class);
			startActivity(setIntent);
			return true;
		case R.id.help:
			Intent helpIntent = new Intent(this,
					HelpActivity.class);
			startActivity(helpIntent);
			return true;
		case R.id.about:
			final PopDialog aboutDialog = new PopDialog(this,
					String.format(getString(R.string.about_title), getString(R.string.app_name)),
					String.format(getString(R.string.about_content), Constants.VERSION),
					getString(R.string.close), 
					null,
					false,
					R.style.CMDialog);
			aboutDialog.setLeftListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					aboutDialog.dismiss();
				}
			});
			aboutDialog.show();
			return true;
		}
		return false;
	}
	
	/**
	 * update the running status text
	 */
	private void updateStatusTxt() {
		Resources resource = getResources();
		switch (AppApplication.connecting_status) {
		case STATUS_APPLY_POLICY:
			Log.i(TAG, "STATUS_APPLY_POLICY");
			statusText.setText(resource.getString(R.string.status_apply_policy));
			if(AppApplication.isTiming) {
				mChronometer.setBase(AppApplication.time_base);
				mChronometer.start();
			}
			break;
		case STATUS_CONNECTED:
			Log.i(TAG, "STATUS_CONNECTED");
			if(AppApplication.current_network != null) {
				String messageConnected = null;
				if(AppApplication.current_network.getName().equals(NetworkModel.NAME_3G)) {
					messageConnected = resource.getString(R.string.status_Connected) + "3G";
					
				} else if(AppApplication.current_network.getName().equals(NetworkModel.NAME_WLAN)) {
					String ssid = ((WifiModel)AppApplication.current_network).getSSID();
					messageConnected = resource.getString(R.string.status_Connected)+ ssid;
				}
				statusText.setText(messageConnected);
				AppApplication.prevoius_connecting_status = STATUS_CONNECTED;
				AppApplication.previous_network_msg = AppApplication.current_network.onClone();
			}
			Log.i(TAG, "AppApplication.time_base=" + AppApplication.time_base);
			mChronometer.setBase(AppApplication.time_base);
			mChronometer.start();
			break;
		case STATUS_ALREADY_CONNECTED:
			Log.i(TAG, "STATUS_ALREADY_CONNECTED");
			if(AppApplication.current_network != null) {
				String messageConnected = null;
				if(AppApplication.current_network.getName().equals(NetworkModel.NAME_3G)) {
					messageConnected = resource.getString(R.string.status_Connected) + "3G";
					
				} else if(AppApplication.current_network.getName().equals(NetworkModel.NAME_WLAN)) {
					String ssid = ((WifiModel)AppApplication.current_network).getSSID();
					messageConnected = resource.getString(R.string.status_Connected)+ ssid;
				}
				statusText.setText(messageConnected);
				AppApplication.prevoius_connecting_status = STATUS_CONNECTED;
				AppApplication.previous_network_msg = AppApplication.current_network.onClone();
			}
			Log.i(TAG, "AppApplication.time_base=" + AppApplication.time_base);
			mChronometer.setBase(AppApplication.time_base);
			mChronometer.start();
			break;
		case STATUS_CONNECTING:
			Log.i(TAG, "STATUS_CONNECTING");
//			AppApplication.needCleanTimeTrack = true;
			mChronometer.stop();
			if(AppApplication.current_network != null) {
				String messageConnecting = null;
				if(AppApplication.current_network.getName().equals(NetworkModel.NAME_3G)) {
					messageConnecting = resource.getString(R.string.status_Connecting) + "3G";
				} else if(AppApplication.current_network.getName().equals(NetworkModel.NAME_WLAN)) {
					String ssid = ((WifiModel)AppApplication.current_network).getSSID();
					messageConnecting = resource.getString(R.string.status_Connecting)+ ssid;
				}
				statusText.setText(messageConnecting);
				AppApplication.prevoius_connecting_status = STATUS_CONNECTING;
				AppApplication.previous_network_msg = AppApplication.current_network.onClone();
			}
			break;
		case STATUS_DOWNLOADING_XML:
			Log.i(TAG, "STATUS_DOWNLOADING_XML");
			statusText.setText(resource.getString(R.string.status_dowload_xml));
			if(AppApplication.isTiming) {
				mChronometer.setBase(AppApplication.time_base);
				mChronometer.start();
			}
			break;
		case STATUS_NEW_POLICY:
			Log.i(TAG, "STATUS_NEW_POLICY");
			statusText.setText(resource.getString(R.string.status_new_policy));
			if(AppApplication.isTiming) {
				mChronometer.setBase(AppApplication.time_base);
				mChronometer.start();
			}
			break;
		case STATUS_NO_CONNECTION:
			Log.i(TAG, "STATUS_NO_CONNECTION");
			statusText.setText(resource.getString(R.string.status_no_connect));
//			AppApplication.needCleanTimeTrack = true;
			mChronometer.stop();
			AppApplication.prevoius_connecting_status = STATUS_NO_CONNECTION;
			break;
		case STATUS_SCANNING:
			Log.i(TAG, "STATUS_SCANNING");
			statusText.setText(resource.getString(R.string.status_scanning));
			AppApplication.prevoius_connecting_status = STATUS_SCANNING;
			if(AppApplication.isTiming) {
				mChronometer.setBase(AppApplication.time_base);
				mChronometer.start();
			}
			break;
		case STATUS_NO_SCAN_RESULT:
			Log.i(TAG, "STATUS_NO_SCAN_RESULT");
			statusText.setText(resource.getString(R.string.status_scan_failed));
			if(AppApplication.isTiming) {
				mChronometer.setBase(AppApplication.time_base);
				mChronometer.start();
			}
			break;
		case STATUS_APPLY_STATIC:
			Log.i(TAG, "STATUS_APPLY_STATIC");
			statusText.setText(resource.getString(R.string.status_request_failed));
			if(AppApplication.isTiming) {
				mChronometer.setBase(AppApplication.time_base);
				mChronometer.start();
			}
			break;
		case STATUS_UNDEFINE:
			Log.i(TAG, "STATUS_UNDEFINE");
			statusText.setText(resource.getString(R.string.status_undefine));
			mChronometer.stop();
			break;
		default:
			Log.i(TAG, "default");
			statusText.setText(resource.getString(R.string.status_undefine));
			mChronometer.stop();
			break;
		}
	}
	
	/**
	 * resume the running status text after actions
	 */
	private void recoverStatusTxt() {
		Resources resource = getResources();
		switch (AppApplication.prevoius_connecting_status) {
		case STATUS_CONNECTED:
			Log.i(TAG, "RESUME_STATUS_CONNECTED");
			if(AppApplication.previous_network_msg != null && CommonUtil.isNetworkConnected(this, AppApplication.previous_network_msg)) {
				String messageConnected = null;
				if(AppApplication.previous_network_msg.getName().equals(NetworkModel.NAME_3G)) {
					messageConnected = resource.getString(R.string.status_Connected) + "3G";
					
				} else if(AppApplication.previous_network_msg.getName().equals(NetworkModel.NAME_WLAN)) {
					String ssid = ((WifiModel)AppApplication.previous_network_msg).getSSID();
					messageConnected = resource.getString(R.string.status_Connected)+ ssid;
				}
				statusText.setText(messageConnected);
				mChronometer.setBase(AppApplication.time_base);
				mChronometer.start();
				AppApplication.connecting_status = STATUS_CONNECTED;
			} else {
				statusText.setText(resource.getString(R.string.status_no_connect));
				mChronometer.stop();
				AppApplication.connecting_status = STATUS_NO_CONNECTION;
			}
			break;
		case STATUS_CONNECTING:
		case STATUS_NO_CONNECTION:
		case STATUS_UNDEFINE:
		case STATUS_SCANNING:
			Log.i(TAG, "RESUME_NO_CONNECTION");
			statusText.setText(resource.getString(R.string.status_no_connect));
			mChronometer.stop();
			AppApplication.connecting_status = STATUS_NO_CONNECTION;
			break;
		default:
			statusText.setText(resource.getString(R.string.status_no_connect));
			mChronometer.stop();
			AppApplication.connecting_status = AppApplication.prevoius_connecting_status;
			break;
		}
	}
	
	/**
	 * show user account dialog
	 * @param type
	 */
	private void showAccountDialog(int type) {
		if(type == 1) {
			if(accountAlertCmcc == null) {
				accountAlertCmcc = new PopDialog(HomeActivity.this,
						getResources().getString(R.string.account_warning_title),
						getResources().getString(R.string.account_warning),
						getResources().getString(R.string.ok),
						getResources().getString(R.string.cancel),
						true,
						R.style.CMDialog);
				accountAlertCmcc.setLeftListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent mInt = new Intent(HomeActivity.this, AccountActivity.class);
						mInt.putExtra("need_complete_cmcc", true);
						startActivityForResult(mInt, 1);
						accountAlertCmcc.dismiss();
					}
				});
				accountAlertCmcc.setRightListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
	                	sendIntent.putExtra("account_complete", false);
	                	sendIntent.putExtra("is_hotspot", false);
	                	sendIntent.putExtra("network_index", networkIndex);
	                	sendBroadcast(sendIntent);
	                	accountAlertCmcc.dismiss();
					}
				});
				accountAlertCmcc.setCancelable(false);
				accountAlertCmcc.setCanceledOnTouchOutside(false);
			}
			if(!accountAlertCmcc.isShowing()) {
				accountAlertCmcc.show();
			}
		} else if(type == 2) {
			if(accountAlertCmccAuto == null) {
				accountAlertCmccAuto = new PopDialog(HomeActivity.this,
						getResources().getString(R.string.account_warning_title),
						getResources().getString(R.string.account_warning),
						getResources().getString(R.string.ok),
						getResources().getString(R.string.cancel),
						true,
						R.style.CMDialog);
				accountAlertCmccAuto.setLeftListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent mInt = new Intent(HomeActivity.this, AccountActivity.class);
						mInt.putExtra("need_complete_cmcc_auto", true);
						startActivityForResult(mInt, 2);
						accountAlertCmccAuto.dismiss();
					}
				});
				accountAlertCmccAuto.setRightListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
	                	sendIntent.putExtra("account_complete", false);
	                	sendIntent.putExtra("is_hotspot", false);
	                	sendIntent.putExtra("network_index", networkIndex);
	                	sendBroadcast(sendIntent);
	                	accountAlertCmccAuto.dismiss();
					}
				});
				accountAlertCmccAuto.setCancelable(false);
				accountAlertCmccAuto.setCanceledOnTouchOutside(false);
			}
			if(!accountAlertCmccAuto.isShowing()) {
				accountAlertCmccAuto.show();
			}
		}
	}
	
	/*private void updateHotspotNotification(int number) {
		if(number < 0) {
			hotspotText.setText(null);
		} else if(number == 0) {
			hotspotText.setText(getText(R.string.notification_hotspot_count));
		} else {
			hotspotText.setText(String.format(getText(R.string.notification_hotspot_count).toString(), number));
		}
	}*/
	
	private static int networkIndex = -1;
	public static void setNetworkIndex(int index) {
		networkIndex = index;
	}
	
	/**
	 * receiver of running status
	 *
	 */
	private class StatusReveiver extends BroadcastReceiver {
		String action;
		int type;
		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if(action.equals(ACTION)) {
				updateStatusTxt();
			} else if(action.equals(ACTION_ACCOUNT)) {
				type = 0;
				try {
					final WifiModel net = (WifiModel)((NetworkModel) intent.getSerializableExtra("network"));
					if(net.getAuthenticationType().equalsIgnoreCase(WifiModel.TYPE_OPEN)) {
						type = 1;
					} else if(net.getAuthenticationType().equalsIgnoreCase(WifiModel.TYPE_PEAP)){
						type = 2;
					} else {
						return;
					}
				} catch (Exception e) {
				}
				showAccountDialog(type);
				needShowAutoDailog = false;
				needShowCmccDailog  =false;
			} else if(action.equals(ACTION_HOTSPOT_NOTIFICATION)) {
//				int number = intent.getIntExtra("hotspot_number", 0);
//				updateHotspotNotification(number);
//				AppApplication.hotspot_number = number;
			}
		}
	}
	
	public static void setNeedShowCmccDailog(boolean bool) {
		needShowCmccDailog = bool;
	}
	
	public static void setNeedShowAutoDailog(boolean bool) {
		needShowAutoDailog = bool;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1) {
			Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
			sendIntent.putExtra("is_hotspot", false);
			sendIntent.putExtra("network_index", networkIndex);
			if(!AppApplication.isCMCCAccountEmpty()){
				sendIntent.putExtra("account_complete", true);
			} else {
				sendIntent.putExtra("account_complete", false);
			}
			sendBroadcast(sendIntent);
		} else if(requestCode == 2) {
			Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
			sendIntent.putExtra("is_hotspot", false);
			sendIntent.putExtra("network_index", networkIndex);
			if(!AppApplication.isCMCCAUTOAccountEmpty()){
				sendIntent.putExtra("account_complete", true);
			} else {
				sendIntent.putExtra("account_complete", false);
			}
			sendBroadcast(sendIntent);
		} 
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private ServiceConnection conn = new ServiceConnection() {
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        	bindService = null;
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            MyBinder binder = (MyBinder)service;
            bindService = binder.getService();
        }
    };
    LogService bindService;
}
