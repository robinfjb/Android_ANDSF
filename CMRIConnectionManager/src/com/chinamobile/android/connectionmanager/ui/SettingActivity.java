package com.chinamobile.android.connectionmanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.controller.ServiceController;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.WifiUtil;

/**
 * Activity for setting page
 *
 */
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener{
	private CheckBoxPreference dynamicChkBox;
	private CheckBoxPreference wifiRadioChkBox;
	private CheckBoxPreference enableSignalChkBox;
//	private CheckBoxPreference enable3GChkBox;
	private CheckBoxPreference notifyBeforeApply;
	private CheckBoxPreference autoRun;
	private CheckBoxPreference userPreferred;
	private CheckBoxPreference autoBreak;
//	private CheckBoxPreference hotspotNotify;
//	private PreferenceScreen account;
	private PreferenceScreen app_set;
	private PreferenceScreen static_set;
	private PreferenceScreen log_view;
	private RadioDialog radioDialog;
//	private LinearLayout mLinearLayout;
	private BroadcastReceiver wifiRadioReceiver;
	private IntentFilter wifiRadioFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.setting);
		PreferenceScreen prefSet = getPreferenceScreen();
		
		getListView().setBackgroundResource(R.drawable.background);
		getListView().setCacheColorHint(Color.TRANSPARENT);
		
		dynamicChkBox = (CheckBoxPreference) prefSet.findPreference("dynamic");
		wifiRadioChkBox = (CheckBoxPreference) prefSet.findPreference("wifi_radio");
		enableSignalChkBox = (CheckBoxPreference) prefSet
				.findPreference("signal_check");
		/*enable3GChkBox = (CheckBoxPreference) prefSet
				.findPreference("_3g_continuously");*/
		notifyBeforeApply = (CheckBoxPreference) prefSet.findPreference("notify_check");
		autoRun = (CheckBoxPreference) prefSet.findPreference("auto_run");
		userPreferred = (CheckBoxPreference) prefSet.findPreference("preferred_check");
//		hotspotNotify = (CheckBoxPreference) prefSet.findPreference("hotspot_note");
//		autoBreak = (CheckBoxPreference) prefSet.findPreference("wifi_auto_close");
//		account = (PreferenceScreen) prefSet.findPreference("account_set");
		app_set = (PreferenceScreen) prefSet.findPreference("app_set");
		static_set = (PreferenceScreen) prefSet.findPreference("static_set");
		log_view = (PreferenceScreen) prefSet.findPreference("log_view");
		
		dynamicChkBox.setOnPreferenceClickListener(this);
		wifiRadioChkBox.setOnPreferenceClickListener(this);
		enableSignalChkBox.setOnPreferenceClickListener(this);
		notifyBeforeApply.setOnPreferenceClickListener(this);
		autoRun.setOnPreferenceClickListener(this);
		userPreferred.setOnPreferenceClickListener(this);
//		hotspotNotify.setOnPreferenceClickListener(this);
//		autoBreak.setOnPreferenceClickListener(this);
//		account.setOnPreferenceClickListener(this);
		app_set.setOnPreferenceClickListener(this);
		static_set.setOnPreferenceClickListener(this);
		log_view.setOnPreferenceClickListener(this);
		
		wifiRadioReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
					if(WifiUtil.isWifiRadioOpenOrOpening(SettingActivity.this)) {
						wifiRadioChkBox.setChecked(true);
						wifiRadioChkBox.setSummary(getText(R.string.wifi_radio_on));
					} else {
						wifiRadioChkBox.setChecked(false);
						wifiRadioChkBox.setSummary(getText(R.string.wifi_radio_off));
					}
				}
			}
		};
		wifiRadioFilter = new IntentFilter();
		wifiRadioFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		wifiRadioFilter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
		registerReceiver(wifiRadioReceiver, wifiRadioFilter);
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
//		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}*/

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		SharedPreferences sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
		
		Editor editor = sp.edit();
		editor.putBoolean("andsf_running", ServiceController.is_andsf_start);
		editor.commit();
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onDestroy() {
		if(wifiRadioReceiver != null) {
			try {
				unregisterReceiver(wifiRadioReceiver);
			} catch (Exception e) {
			}
		}
		super.onDestroy();
	}

	
	@Override
	protected void onResume() {
		static_set.setSummary(AppApplication.isWifiFirst ? getResources()
				.getString(R.string.first_wifi) : getResources().getString(
				R.string.first_3g));
		if(WifiUtil.isWifiRadioOpenOrOpening(this)) {
			wifiRadioChkBox.setChecked(true);
			wifiRadioChkBox.setSummary(getText(R.string.wifi_radio_on));
		} else {
			wifiRadioChkBox.setChecked(false);
			wifiRadioChkBox.setSummary(getText(R.string.wifi_radio_off));
		}
		
		super.onResume();
	}
	
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.equals(dynamicChkBox)){
			System.out.println("dynamicChkBox---->" + dynamicChkBox.isChecked());
			Intent sendIntent = new Intent(Constants.Action.ACTION_SETTING_DYNAMIC);
            sendBroadcast(sendIntent);
		} else if(preference.equals(enableSignalChkBox)) {
			System.out.println("enableSignalChkBox---->" + enableSignalChkBox.isChecked());
			Intent sendIntent = new Intent(Constants.Action.ACTION_SETTING_SIGNAL);
            sendBroadcast(sendIntent);
		} else if(preference.equals(autoBreak)) {
			System.out.println("autoBreak---->" + autoBreak.isChecked());
			Intent sendIntent = new Intent(Constants.Action.ACTION_SETTING_AUTO_BREAK);
            sendBroadcast(sendIntent);
		} else if(preference.equals(notifyBeforeApply)) {
			System.out.println("notifyBeforeApply---->" + notifyBeforeApply.isChecked());
			Intent sendIntent = new Intent(Constants.Action.ACTION_SETTING_POLICY_APPLY);
            sendBroadcast(sendIntent);
		} else if(preference.equals(autoRun)) {
			System.out.println("autoRun---->" + autoRun.isChecked());
			Intent sendIntent = new Intent(Constants.Action.ACTION_SETTING_AUTO_RUN);
            sendBroadcast(sendIntent);
		} else if(preference.equals(userPreferred)) {
			System.out.println("userPreferred---->" + userPreferred.isChecked());
			Intent sendIntent = new Intent(Constants.Action.ACTION_SETTING_PREFERED);
            sendBroadcast(sendIntent);
//		} else if(preference.equals(hotspotNotify)) {
//			System.out.println("hotspotNotify---->" + hotspotNotify.isChecked());
//			Intent sendIntent = new Intent(Constants.Action.ACTION_SETTING_HOTSPOT_NOTIFY);
//            sendBroadcast(sendIntent);
//		} else if(preference.equals(account)) {
//			Intent sendIntent = new Intent(this, AccountActivity.class);
//			sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(sendIntent);
		} else if(preference.equals(static_set)) {
			radioDialog = new RadioDialog(SettingActivity.this,
					getResources().getString(R.string.static_set_dialog_title),
					getResources().getStringArray(R.array.static_priority),
					new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							String[] items = getResources().getStringArray(R.array.static_priority);
		                    Toast.makeText(AppApplication.getApp().getApplicationContext(), 
		                    		AppApplication.getApp().getApplicationContext().getText(R.string.toast_msg) + " " + items[position],
		                    		Toast.LENGTH_SHORT).show();
		                    
		                    if(position == 0) {
		                    	AppApplication.isWifiFirst = true;
		                    } else if(position == 1) {
		                    	AppApplication.isWifiFirst = false;
		                    } else {
		                    	return;
		                    }
		                    
		                    static_set.setSummary(AppApplication.isWifiFirst ? getResources()
		            				.getString(R.string.first_wifi) : getResources().getString(
		            				R.string.first_3g));
		                    
		                    Editor sharedata = getSharedPreferences(AppApplication.SP_DATA_NAME,
		        					Context.MODE_PRIVATE).edit();
		        			sharedata.putBoolean("wifi_first", AppApplication.isWifiFirst);
		        			sharedata.commit();
		        			
		        			if(radioDialog != null) {
		        				radioDialog.dismiss();
		        			}
						}
					}, R.style.CMDialog);
			if(!radioDialog.isShowing()) {
				radioDialog.show();
			}
		} else if(preference.equals(log_view)) {
			Intent sendIntent = new Intent(this, LogActivity.class);
            startActivity(sendIntent);
		} else if(preference.equals(app_set)) {
			Intent sendIntent = new Intent(this, AppSettingActivity.class);
            startActivity(sendIntent);
		} else if(preference.equals(wifiRadioChkBox)) {
			if(wifiRadioChkBox.isChecked()) {
				WifiUtil.openWifiRadio(SettingActivity.this);
				wifiRadioChkBox.setSummary(getText(R.string.wifi_radio_on));
			} else {
				WifiUtil.closeWifiRadio(SettingActivity.this);
				wifiRadioChkBox.setSummary(getText(R.string.wifi_radio_off));
			}
		}
		return false;
	}
}
