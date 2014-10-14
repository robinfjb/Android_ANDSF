package com.chinamobile.android.connectionmanager.receiver;

import com.chinamobile.android.connectionmanager.util.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * receiver to receive system screen action
 */
public class ScreenReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//			Log.e("ScreenReceiver", "ACTION_SCREEN_OFF");
			context.sendBroadcast(new Intent(Constants.Action.ACTION_SCREEN_OFF));
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//			Log.e("ScreenReceiver", "ACTION_SCREEN_ON");
			context.sendBroadcast(new Intent(Constants.Action.ACTION_SCREEN_ON));
		}
	}

}
