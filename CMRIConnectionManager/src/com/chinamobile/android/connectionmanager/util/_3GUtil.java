package com.chinamobile.android.connectionmanager.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class _3GUtil {
	/**
	 * open/close 3G/2G data radio
	 * (not work on Ophone)
	 * @param context
	 * @param enabled
	 *            true-open; false-close
	 * @throws ClassNotFoundException 
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 */
	public static void toggleMobileData(Context context, boolean enabled)
			throws MobileDataException{
		try {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class<?> conMgrClass = Class.forName(connManager.getClass().getName());
		Field iConMgrField = conMgrClass.getDeclaredField("mService");
		iConMgrField.setAccessible(true);
		Object iConMgr = iConMgrField.get(connManager);
		Class<?> iConMgrClass = Class.forName(iConMgr.getClass().getName());
		Method setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
				"setMobileDataEnabled", Boolean.TYPE);
		setMobileDataEnabledMethod.setAccessible(true);
		setMobileDataEnabledMethod.invoke(iConMgr, enabled);
		}
		catch (ClassNotFoundException e) {
			throw new MobileDataException(e);
		} catch (NoSuchFieldException e) {
			throw new MobileDataException(e);
		} catch (IllegalArgumentException e) {
			throw new MobileDataException(e);
		} catch (IllegalAccessException e) {
			throw new MobileDataException(e);
		} catch (NoSuchMethodException e) {
			throw new MobileDataException(e);
		} catch (InvocationTargetException e) {
			throw new MobileDataException(e);
		}
	}
	
	
	/**
	 * get the status of 3G/2G data
	 * 
	 * @param context
	 * @return true if 3g/2g radio open, false if 3g/2g radio close
	 * @throws ClassNotFoundException 
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 */
	public static boolean getMobileDataStatus(Context context)
			throws MobileDataException{
		try {
			
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class<?> conMgrClass;
		conMgrClass = Class.forName(connManager.getClass().getName());
		Field iConMgrField = conMgrClass.getDeclaredField("mService");
		iConMgrField.setAccessible(true);
		Object iConMgr = iConMgrField.get(connManager);
		Class<?> iConMgrClass = Class.forName(iConMgr.getClass().getName());
		Method getMobileDataEnabledMethod = iConMgrClass
				.getDeclaredMethod("getMobileDataEnabled");
		getMobileDataEnabledMethod.setAccessible(true);
		return (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
		}
		catch (ClassNotFoundException e) {
			throw new MobileDataException(e);
		} catch (NoSuchFieldException e) {
			throw new MobileDataException(e);
		} catch (IllegalArgumentException e) {
			throw new MobileDataException(e);
		} catch (IllegalAccessException e) {
			throw new MobileDataException(e);
		} catch (NoSuchMethodException e) {
			throw new MobileDataException(e);
		} catch (InvocationTargetException e) {
			throw new MobileDataException(e);
		}
	}
	
	/**
	 * get the current network type
	 * @param context
	 * @return
	 */
	public static String NetType(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (null == info)
				return null;
			String typeName = info.getTypeName().toLowerCase();
			if (typeName.equals("wifi")) {

			} else {
				typeName = info.getExtraInfo().toLowerCase();
			}
			return typeName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * get the connection proxy and port
	 * @param context
	 * @return
	 */
	public static String[] getHostAndProxy(Context context) {
		Uri uri = Uri.parse("content://telephony/carriers/preferapn");
		Cursor mCursor = context.getContentResolver().query(uri, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToNext();
			String proxyStr = mCursor
					.getString(mCursor.getColumnIndex("proxy"));
			String port = mCursor.getString(mCursor.getColumnIndex("port"));
			return new String[] { proxyStr, port };
		}
		return null;
	}
	
	/**
	 * Exception class for mobile data exception
	 *
	 */
	public static class MobileDataException extends Exception{
		public MobileDataException(Throwable throwable) {
			super(throwable);
		}
	}
	
	/**
	 * is air plane mode?
	 * @param context
	 * @return
	 */
	public static boolean isAirplaneModeOn(Context context) {
		return android.provider.Settings.System.getInt(
				context.getContentResolver(),
				android.provider.Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}
	
	@Deprecated
	public static boolean isCMCCSimCard(Context context) {
		TelephonyManager teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return teleManager.getSimOperator().equals("46000");
	}
}
