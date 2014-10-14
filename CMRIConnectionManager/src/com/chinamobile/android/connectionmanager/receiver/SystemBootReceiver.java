package com.chinamobile.android.connectionmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.controller.ServiceController;
import com.chinamobile.android.connectionmanager.util.Constants;

/**
 * receiver to receive system boot complete
 */
public class SystemBootReceiver extends BroadcastReceiver{
	private static final String TAG = "SystemBootReceiver";
	private static final String ACTION = Intent.ACTION_BOOT_COMPLETED;
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			SharedPreferences sharePreferences = PreferenceManager.getDefaultSharedPreferences(context);
			boolean auto_run = sharePreferences.getBoolean("auto_run", true);
			Log.e(TAG, "auto_run=" + auto_run);
			if(auto_run) {
				AppApplication.start_andsf = true;
				Intent bootStart = new Intent(context, ServiceController.class);
				context.startService(bootStart);
			}
		}
	}

}
