package com.chinamobile.android.connectionmanager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.LogUtil;
import com.chinamobile.android.connectionmanager.util.StringUtil;

public class AppApplication extends Application{
	private static final String TAG = "AppApplication";
	public static final String SP_DATA_NAME = "config_memo";//global SharedPreferences name
	private static AppApplication self;
	public static NetworkModel current_network;
	public static NetworkModel previous_network_msg;//previous network for if current network cancel
	private static BMapManager mBMapMan = null;//BaiduMap manager
	private static MyGeneralListener listener = null;
	public static int connecting_status = -1;//connect status flag
	public static int prevoius_connecting_status = -1;//previous network connect status flag
	public static String username;
	public static String password;
	public static String peap_username;
	public static String peap_password;
	public static boolean start_andsf;
	public static boolean isEapSim = false;
	public static boolean requestServer = true;
	public static int cellIdChangeTimes = 0;// cell id change count
	public static int applyStaticPolicy = 0;// static policy apply count
	public static boolean isWifiFirst = true;// flag for static policy
	public static long time_base;
	public static boolean modile_data_notify = false;
	public static int hotspot_number = -1;
	public static boolean isTiming = false;
	private static boolean DEVELOPER_MODE = true;

	/**
	 * is cmcc account empty?
	 * @return
	 */
	public static boolean isCMCCAccountEmpty() {
		return (AppApplication.username == null 
				|| AppApplication.username.trim().equals("")
				|| AppApplication.password == null 
				|| AppApplication.password.trim().equals(""));
	}

	/**
	 * is cmcc auto account empty?
	 * @return
	 */
	public static boolean isCMCCAUTOAccountEmpty() {
		return (AppApplication.peap_username == null 
				|| AppApplication.peap_username.trim().equals("")
				|| AppApplication.peap_password == null 
				|| AppApplication.peap_password.trim().equals(""));
	}
	
	@Override
	public void onCreate() {
		self = this;
		Log.i(TAG, "onCreate()");
		super.onCreate();
				
		CrashException customException = CrashException.getInstance();
		customException.init(getApplicationContext());

		SharedPreferences sp = getSharedPreferences(AppApplication.SP_DATA_NAME,
				Context.MODE_PRIVATE);
		username = sp.getString("username", "");
		password = sp.getString("password", "");
		peap_username = sp.getString("peap_username", "");
		peap_password = sp.getString("peap_password", "");
		isWifiFirst = sp.getBoolean("wifi_first", true);
		modile_data_notify = sp.getBoolean("mobile_data_notify", true);
		isEapSim = CommonUtil.isDeviceSupportEapSim(this);
		
		mBMapMan = new BMapManager(getApp().getApplicationContext());
		mBMapMan.init(Constants.mStrKey, getListener());
	}
	
	/**
	 * get {@link BMapManager} instance
	 * @return
	 */
	public static BMapManager getBMapManager() {
		if(mBMapMan == null) {
			mBMapMan = new BMapManager(getApp().getApplicationContext());
			mBMapMan.init(Constants.mStrKey, getListener());
		}
		return mBMapMan;
	}
	
	/**
	 * get {@link MyGeneralListener} instance
	 * @return
	 */
	public static MyGeneralListener getListener() {
		if(listener == null) {
			listener = self.new MyGeneralListener();
		}
		return listener;
	}
	
	@Override
	public void onLowMemory() {
		LogUtil.add("Low Memory");
		super.onLowMemory();
	}


	@Override
	public void onTerminate() {
		self = null;
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		Log.i(TAG, "onTerminate()");
	}
	
	/**
	 *  get {@link AppApplication} instance
	 * @return
	 */
	public static AppApplication getApp() {
		return self;
	}
	
	/**
	 * <b>BaiduMap</b> general listener
	 *
	 */
	public class MyGeneralListener implements MKGeneralListener {
			@Override
			public void onGetNetworkState(int iError) {
//				displayTestToast(getText(R.string.conn_wrong));
			}

			@Override
			public void onGetPermissionState(int iError) {
				if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
					displayTestToast(getText(R.string.permission_wrong));
				}
			}
	};

	public void displayTestToast(CharSequence chars) {
		Toast.makeText(getApplicationContext(), chars, Toast.LENGTH_LONG).show();
	}
}
