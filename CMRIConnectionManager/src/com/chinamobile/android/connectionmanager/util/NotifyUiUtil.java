package com.chinamobile.android.connectionmanager.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.ui.HomeActivity;
import com.chinamobile.android.connectionmanager.ui.HotspotActivity;

/**
 * notify in the Android status bar
 */
public class NotifyUiUtil{
	
	/**
	 * notify user apply policy
	 * @param mContext
	 */
	@SuppressWarnings("deprecation")
	public static void notifyUserApplyPolicy(Context mContext) {
		Context context = mContext.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		intent.setClass(mContext, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("from_notify_policy", true);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0x01,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification mNotification = new Notification(R.drawable.cmri_launch_icon,
				null, System.currentTimeMillis());
		if(!CommonUtil.isScreenOn(mContext)) {// if the screen is off
			mNotification.defaults = 16; //meanless value
		} else {
			mNotification.defaults = Notification.DEFAULT_ALL;
		}
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;

		mNotification.setLatestEventInfo(context,
				context.getText(R.string.notification_title),
				context.getText(R.string.notification_policy_msg), contentIntent);
		notificationManager.notify(R.string.notification_title, mNotification);
	}
	
	/**
	 * notify user apply policy and with hotspots count words 
	 * @param mContext
	 * @param count
	 */
	@SuppressWarnings("deprecation")
	public static void notifyUserHotspotAndPolicy(Context mContext, int count) {
		Context context = mContext.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		intent.setClass(mContext, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("from_notify_policy", true);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0x04,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification mNotification = new Notification(R.drawable.cmri_launch_icon,
				null, System.currentTimeMillis());
		if(!CommonUtil.isScreenOn(mContext)) {// if the screen is off
			mNotification.defaults = 16; //meanless value
		} else {
			mNotification.defaults = Notification.DEFAULT_ALL;
		}
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		
		mNotification.setLatestEventInfo(context,
				context.getText(R.string.notification_title),
				context.getText(R.string.notification_policy_msg) + "£¬" + 
				String.format(context.getText(R.string.notification_hotspot_count).toString(), 
						new Object[]{count}), contentIntent);
		
		notificationManager.notify(R.string.notification_title + 3, mNotification);
	}
	
	/**
	 * notify user hotspots count words
	 * @param mContext
	 * @param count
	 */
	@SuppressWarnings("deprecation")
	public static void notifyUserHotspot(Context mContext, int count) {
		Context context = mContext.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0x05,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification mNotification = new Notification(R.drawable.cmri_launch_icon,
				null, System.currentTimeMillis());
		if(!CommonUtil.isScreenOn(mContext)) {// if the screen is off
			mNotification.defaults = 16; //meanless value
		} else {
			mNotification.defaults = Notification.DEFAULT_ALL;
		}
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		
		mNotification.setLatestEventInfo(context,
				context.getText(R.string.notification_title),
				String.format(context.getText(R.string.notification_hotspot_count).toString(), 
						new Object[]{count}), contentIntent);
		
		notificationManager.notify(R.string.notification_title + 4, mNotification);
	}
	
	/**
	 * notify user preferred choose and hotspots count words 
	 * @param mContext
	 * @param count
	 */
	@SuppressWarnings("deprecation")
	public static void notifyUserHotspotAndPrefer(Context mContext, int count) {
		Context context = mContext.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		intent.setClass(mContext, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("from_notify_preferred", true);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0x06,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification mNotification = new Notification(R.drawable.cmri_launch_icon,
				null, System.currentTimeMillis());
		if(!CommonUtil.isScreenOn(mContext)) {// if the screen is off
			mNotification.defaults = 16; //meanless value
		} else {
			mNotification.defaults = Notification.DEFAULT_ALL;
		}
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;

		mNotification.setLatestEventInfo(context,
				context.getText(R.string.notification_title),
				context.getText(R.string.notification_preferred_msg) + "£¬" + 
						String.format(context.getText(R.string.notification_hotspot_count).toString(), 
								new Object[]{count}), contentIntent);
		notificationManager.notify(R.string.notification_title + 5, mNotification);
	}
	
	/**
	 * notify user preferred choose
	 * @param mContext
	 */
	@SuppressWarnings("deprecation")
	public static void notifyUserPreferredWiFi(Context mContext) {
		Context context = mContext.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		intent.setClass(mContext, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("from_notify_preferred", true);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0x02,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification mNotification = new Notification(R.drawable.cmri_launch_icon,
				null, System.currentTimeMillis());
		if(!CommonUtil.isScreenOn(mContext)) {// if the screen is off
			mNotification.defaults = 16; //meanless value
		} else {
			mNotification.defaults = Notification.DEFAULT_ALL;
		}
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;

		mNotification.setLatestEventInfo(context,
				context.getText(R.string.notification_title),
				context.getText(R.string.notification_preferred_msg), contentIntent);
		notificationManager.notify(R.string.notification_title + 1, mNotification);
	}
	
	/**
	 * notify user open mobile data radio
	 * @param mContext
	 * @param g3Net
	 */
	@SuppressWarnings("deprecation")
	public static void notifyUserOpenMobileData(Context mContext, NetworkModel g3Net) {
		Context context = mContext.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent sendIntent = new Intent(Constants.Action.ACTION_OPEN_MOBILE_DATA);
		sendIntent.putExtra("3g_network_open", g3Net);
		PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0x03,
				sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification mNotification = new Notification(R.drawable.cmri_launch_icon,
				null, System.currentTimeMillis());
		if(!CommonUtil.isScreenOn(mContext)) {// if the screen is off
			mNotification.defaults = 16; //meanless value
		} else {
			mNotification.defaults = Notification.DEFAULT_ALL;
		}
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;

		mNotification.setLatestEventInfo(context,
				context.getText(R.string.notification_title),
				context.getText(R.string.notification_mobile_data), contentIntent);
		notificationManager.notify(R.string.notification_title + 2, mNotification);
	}
	
	/**
	 * clean up notifications when new policy arrived
	 * @param mContext
	 */
	public static void cleanPolicyNotification(Context mContext) {
		NotificationManager notificationManager = (NotificationManager) mContext.
				getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.notification_title);
		notificationManager.cancel(R.string.notification_title + 1);
		notificationManager.cancel(R.string.notification_title + 3);
		notificationManager.cancel(R.string.notification_title + 4);
		notificationManager.cancel(R.string.notification_title + 5);
	}
	
	/**
	 * clean up all notifications
	 * @param mContext
	 */
	public static void clearAllNotification(Context mContext) {
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.notification_title);
		notificationManager.cancel(R.string.notification_title + 1);
		notificationManager.cancel(R.string.notification_title + 2);
		notificationManager.cancel(R.string.notification_title + 3);
		notificationManager.cancel(R.string.notification_title + 4);
		notificationManager.cancel(R.string.notification_title + 5);
	}
}
