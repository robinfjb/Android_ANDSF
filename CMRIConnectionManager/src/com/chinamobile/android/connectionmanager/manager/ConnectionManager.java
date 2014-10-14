package com.chinamobile.android.connectionmanager.manager;

import java.util.ArrayList;
import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.controller.ServiceController;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.model._3GModel;
import com.chinamobile.android.connectionmanager.ui.HomeActivity;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.LogUtil;
import com.chinamobile.android.connectionmanager.util.WifiUtil;
import com.chinamobile.android.connectionmanager.util.WifiUtil.WifiRadioStats;
import com.chinamobile.android.connectionmanager.util._3GUtil;
import com.chinamobile.android.connectionmanager.util._3GUtil.MobileDataException;

/**
 * the manager manage network connection
 * <p>
 * receive WiFi or 3G status from system and notify to {@link ServiceController}
 * <br>can connect WiFi and 3G
 * <br>can open WiFi radio and scan
 */
public class ConnectionManager extends BaseManager {
	private static final String TAG = "ConnectionManager";
	private static final String ACTION_WIFI_RADIO = WifiManager.WIFI_STATE_CHANGED_ACTION;
	private static final String ACTION_WIFI = WifiManager.NETWORK_STATE_CHANGED_ACTION;
	private static final String ACTION_3G = ConnectivityManager.CONNECTIVITY_ACTION;
	private static WifiManager mWifiManager;
	private List<WifiConfiguration> configList;// the list of WIFI configured networks
	private NetworkStateReceiver networkReceiver;
	private WifiScanReceiver scanReceiver;
	private ConnectivityManager connManager;
	private boolean isWaitingWifiRadio;
	private boolean isHotspot;
	private static boolean isScanning;
	private Handler connectTimeoutTimer;
	private Runnable connectTimeoutTask;
	private Handler scanTimeoutTimer;
	private Runnable scanTimeoutTask;
	private NetworkModel connectingNetwork;// the network is connecting
	private NetworkModel connectedNetwork;// the network is connected
	public boolean isStart;
	private boolean disablingWifiRadio;//when disablingWifiRadio, 3g status will receive disconnect message, so need a boolean to control

	public ConnectionManager(Context context, Handler handler) {
		super(context, handler);
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkReceiver = new NetworkStateReceiver();
		scanReceiver = new WifiScanReceiver();
		connectTimeoutTimer = new Handler();
		scanTimeoutTimer = new Handler();
		initConnectTimeout();
		initScanTimeout();
	}

	@Override
	public void onStart() {
	}
	
	@Override
	public void onStop() {
	}
	
	public void onStart(boolean isHotspot, Handler handler) {
		Log.i(TAG, "onStart");
		this.isHotspot = isHotspot;
		initConnectTimeout();
		initScanTimeout();
		
		connectingNetwork = null;
		connectedNetwork = null;
		
		registerNetworkReceiver();
		this.handler = handler;
		if(!isHotspot) {
			registerScanReceiver();
		}
		isStart = true;
		disablingWifiRadio = false;
	}

	public void onStop(boolean isHotspot) {
		Log.i(TAG, "onStop");

		unregisterNetworkReceiver();
		unregisterScanReceiver();

		connectingNetwork = null;
		connectedNetwork = null;
		handler = null;
		if(connectTimeoutTask != null) {
			connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
		}
		if(scanTimeoutTask != null) {
			scanTimeoutTimer.removeCallbacks(scanTimeoutTask);
		}
		
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.notification_title + 2);
		
		isStart = false;
		disablingWifiRadio = false;
	}
	
	/**
	 * register receivers
	 */
	private void registerNetworkReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_WIFI);
		filter.addAction(ACTION_3G);
		filter.addAction(ACTION_WIFI_RADIO);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		context.registerReceiver(networkReceiver, filter);
	}
	
	/**
	 * unregister receivers
	 */
	private void unregisterNetworkReceiver() {
		if(context != null && networkReceiver != null) {
			try {
				context.unregisterReceiver(networkReceiver);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * register wifi scan receiver
	 */
	private void registerScanReceiver() {
		IntentFilter scan_filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		context.registerReceiver(scanReceiver, scan_filter);
	}
	
	/**
	 * unregister wifi scan receiver
	 */
	private void unregisterScanReceiver() {
		if(context != null && scanReceiver != null) {
			try {
				context.unregisterReceiver(scanReceiver);
			} catch (Exception e) {
			}
		}
	}
	/**
	 * initial a time task to listen connection timeout
	 */
	private void initConnectTimeout() {
		connectTimeoutTask = new Runnable() {
			@Override
			public void run() {
				//connection timeout, send failed message
				Log.e(TAG, "connection timeout!!!");
				LogUtil.add("connection timeout!!!!!");
				WifiUtil.disableAllWifi(context, mWifiManager);
				if(isHotspot) {
					notifyToController(Constants.MSG_CONNECTION_HOTSPOT_FAILED);
				} else {
					notifyToController(Constants.MSG_CONNECTION_FAILED);
				}
			}
		};
	}
	
	/**
	 * initial wifi scan time out timer
	 */
	private void initScanTimeout() {
		scanTimeoutTask = new Runnable() {
			
			@Override
			public void run() {
				if(isScanning) {
					Log.w(TAG, "wifi scan timeout!!!");
					isScanning = !isScanning;
					notifyToController(Constants.MSG_CONNECTION_SCAN_COMPLETE);
				}
			}
		};
	}
	
	@Override
	protected void notifyToController(int message) {
		if(message == Constants.MSG_CONNECTION_FAILED
				|| message == Constants.MSG_CONNECTION_HOTSPOT_FAILED
				|| message == Constants.MSG_CONNECTION_SUCCESS
				|| message == Constants.MSG_CONNECTION_ALREDAY_CONNECTED
				|| message == Constants.MSG_CONNECTION_HOTSPOT_SUCCESS
				|| message == Constants.MSG_CONNECTION_HOTSPOT_ALREDAY_CONNECTED
				|| message == Constants.MSG_CONNECTION_OPEN_3G_FAILED
				|| message == Constants.MSG_CONNECTION_SET_AUTO_ACCOUNT
				|| message == Constants.MSG_CONNECTION_SET_CMCC_ACCOUNT
				|| message == Constants.MSG_CONNECTION_SET_AUTO_ACCOUNT_HOTSPOT
				|| message == Constants.MSG_CONNECTION_SET_CMCC_ACCOUNT_HOTSPOT) {
			Message msg = handler.obtainMessage(message);
			if (connectingNetwork != null) {
				msg.obj = connectingNetwork.onClone();
				connectingNetwork = null;
			}
			handler.sendMessage(msg);
		} else if(message == Constants.MSG_CONNECTION_BREAK
				|| message == Constants.MSG_CONNECTION_HOTSPOT_BREAK) {
			Message msg = handler.obtainMessage(message);
			if (connectedNetwork != null) {
				msg.obj = connectedNetwork.onClone();
				connectedNetwork = null;
			}
			handler.sendMessage(msg);
		}
		else if(message == Constants.MSG_CONNECTION_RADIO_CHANGE
				|| message == Constants.MSG_CONNECTION_SCAN_COMPLETE
				|| message == Constants.MSG_CONNECTION_HOTSPOT_SCAN_COMPLETE) {
			handler.sendEmptyMessage(message);
		}
	}
	
	/**
	 * do connect of WIFI/3G
	 * <br> split policy network and hotspot network
	 * <br> if already connected, notify to {@link ServerController} already connected message
	 * <br> 3G connection include WiFi radio on and WiFi radio off, to Ophone cannot open 3G radio,
	 * so notify to user open instead
	 * @param network
	 */
	public void connect(NetworkModel network) {
		connect(network, false);
	}
	
	/**
	 * do connect of WIFI/3G
	 * <br> split policy network and hotspot network
	 * <br> if already connected, notify to {@link ServerController} already connected message
	 * <br> 3G connection include WiFi radio on and WiFi radio off, to Ophone cannot open 3G radio,
	 * so notify to user open instead
	 * @param network
	 * @param isHotspot
	 */
	public void connect(NetworkModel network, boolean isHotspot) {
		this.isHotspot = isHotspot;
		connectingNetwork = network;
		connectedNetwork = null;
		LogUtil.add("start to connect with " + (network.getType() == 2 ? "3G"
				: ("WIFI: " + ((WifiModel) network).getSSID())));
		isWaitingWifiRadio = false;

		if (network.getType() == NetworkModel.TYPE_WIFI) {
			if(isNetworkConnected(network)) {
				WifiModel currentWifi = getCurrentConnectionWifi();
				if(currentWifi != null 
						&& CommonUtil.isSameWifi(currentWifi, (WifiModel)network)) {
					if(currentWifi.getAuthenticationType().equalsIgnoreCase("OPEN")) {
						// OPEN need to be disconnect first
						Log.w(TAG, "disconnect the current wifi!");
						mWifiManager.disconnect();
						currentWifi = null;
					} else {
						Log.w(TAG, "the wifi is already connected!");
						LogUtil.add("the wifi is already connected!");
						connectedNetwork = connectingNetwork.onClone();
						if(isHotspot) {
							notifyToController(Constants.MSG_CONNECTION_HOTSPOT_ALREDAY_CONNECTED);
						} else {
							notifyToController(Constants.MSG_CONNECTION_ALREDAY_CONNECTED);
						}
						return;
					}
				}
			}
			if (!WifiUtil.isWifiRadioOpen(context)) {
				if(isHotspot) {
					notifyToController(Constants.MSG_CONNECTION_HOTSPOT_FAILED);
				} else {
					openWifiRadio();
				}
			} else {
				wifiConnect((WifiModel) network);
			}
		} else if (network.getType() == NetworkModel.TYPE_3G) {
			if(((_3GModel) network).isNeedOpenWifiRadio()) {
				_3GConnectWithWifiRadio(network);
			} else {
				_3GConnectOffWifiRadio(network);
			}
		}
	}

	/**
	 * WiFi connection method include PEAP and OPEN
	 * @param wifi
	 */
	private void wifiConnect(WifiModel wifi) {
		AppApplication.current_network = wifi;
		AppApplication.connecting_status = HomeActivity.STATUS_CONNECTING;
		context.sendBroadcast(new Intent(HomeActivity.ACTION));
		// check account
		if(wifi.getAuthenticationType().equalsIgnoreCase("PEAP")) {
			if(AppApplication.isCMCCAUTOAccountEmpty()) {
				if(isHotspot) {
					notifyToController(Constants.MSG_CONNECTION_SET_AUTO_ACCOUNT_HOTSPOT);
				} else {
					notifyToController(Constants.MSG_CONNECTION_SET_AUTO_ACCOUNT);
				}
				return;
			}
		} else if(wifi.getAuthenticationType().equalsIgnoreCase("OPEN")) {
			if(AppApplication.isCMCCAccountEmpty()) {
				if(isHotspot) {
					notifyToController(Constants.MSG_CONNECTION_SET_CMCC_ACCOUNT_HOTSPOT);
				} else {
					notifyToController(Constants.MSG_CONNECTION_SET_CMCC_ACCOUNT);
				}
				return;
			}
		}
		String netSSID = wifi.getSSID();//wifiInfo.getSSID();
		configList = mWifiManager.getConfiguredNetworks();
		List<String> ssidList = new ArrayList<String>();
		for (WifiConfiguration config : configList) {
			ssidList.add(config.SSID.substring(1, config.SSID.length() - 1));//remove ""
		}
		
		Log.i(TAG, "netSSID--->" + netSSID + "/size:" + configList.size());
		connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
		connectTimeoutTimer.postDelayed(connectTimeoutTask, Constants.TIMEOUT_WIFI);
		
		if (ssidList.contains(netSSID)) { // exist in the set of WIFI configured networks
			Log.i(TAG, "connect in configured");
			int index = ssidList.indexOf(netSSID);
			WifiConfiguration wiCon = configList.get(index);
			if(wifi.getAuthenticationType().equalsIgnoreCase("PEAP")) {
				WifiUtil.updatePeapConfig(wiCon, AppApplication.peap_username, AppApplication.peap_password);
			}else if(wifi.getAuthenticationType().equalsIgnoreCase("OPEN")) {
				WifiUtil.updateWepConfig(wiCon);
			}else if(wifi.getAuthenticationType().equalsIgnoreCase("SIM")) {
				WifiUtil.updateEapSimConfig(wiCon);
			}
			Log.d(TAG, "WifiConfiguration--->" + wiCon.toString());
			LogUtil.add("WifiConfiguration--->" + wiCon.toString());
			int updateIndex = mWifiManager.updateNetwork(wiCon);
			Log.d(TAG, "wifiConnect--->mWifiManager.updateNetwork(--->" + updateIndex);
			if(updateIndex < 0) {
				mWifiManager.removeNetwork(index);
				if (!AddNewWifi(wiCon)) {
					Log.e(TAG, "wifiConnect--->step1: connect wifi failed");
					if(isHotspot) {
						notifyToController(Constants.MSG_CONNECTION_HOTSPOT_FAILED);
					} else {
						notifyToController(Constants.MSG_CONNECTION_FAILED);
						
					}
					connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
				}
			} else {
				if (!connectConfiguration(wiCon)) {
					Log.e(TAG, "wifiConnect--->step1: connect wifi failed");
					if(isHotspot) {
						notifyToController(Constants.MSG_CONNECTION_HOTSPOT_FAILED);
					} else {
						notifyToController(Constants.MSG_CONNECTION_FAILED);
						
					}
					connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
				}
				mWifiManager.reconnect();
			}
		} else {// add a new WiFi configuration
			Log.i(TAG, "add a new configuration");
			WifiConfiguration wc = null;
			configList = mWifiManager.getConfiguredNetworks();
			
			if(wifi.getAuthenticationType().equalsIgnoreCase("PEAP")) {
				wc = WifiUtil.getPeapConfig(netSSID, AppApplication.peap_username, AppApplication.peap_password);
			}else if(wifi.getAuthenticationType().equalsIgnoreCase("OPEN")) {
				wc = WifiUtil.getNewWepConfig(netSSID);
			}else if(wifi.getAuthenticationType().equalsIgnoreCase("SIM")) {
				wc = WifiUtil.getEapSimConfig(netSSID);
			}
			
			Log.d(TAG, "WifiConfiguration--->" + wc.toString());
			LogUtil.add("WifiConfiguration--->" + wc.toString());
			if (!AddNewWifi(wc)) {
				Log.e(TAG, "wifiConnect---->step1: add wifi failed");
				// connect failed, send failed message
				if(isHotspot) {
					notifyToController(Constants.MSG_CONNECTION_HOTSPOT_FAILED);
				} else {
					notifyToController(Constants.MSG_CONNECTION_FAILED);
					
				}
				connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
			}
		}
	}

	/**
	 * turn off wifi radio and connect 3g
	 * @param network
	 */
	private void _3GConnectOffWifiRadio(NetworkModel network) {
		if (WifiUtil.isWifiRadioOpen(context)) {
			closeWifiRadio();
		}

		if (isNetworkConnected(network)) {
			Log.w(TAG, "OffWifiRadio --> the 3g is already connected!");
			LogUtil.add("3g is already connected!");
			connectedNetwork = network.onClone();
			notifyToController(Constants.MSG_CONNECTION_ALREDAY_CONNECTED);
			return;
		}
		
		AppApplication.current_network = network;
		AppApplication.connecting_status = HomeActivity.STATUS_CONNECTING;
		context.sendBroadcast(new Intent(HomeActivity.ACTION));
		
		try {
			if (!_3GUtil.getMobileDataStatus(context)) {
				Log.i(TAG, "open 3g radio");
				_3GUtil.toggleMobileData(context, true);
			}
		} catch (MobileDataException e) {
			e.printStackTrace();
			if (AppApplication.modile_data_notify) {
				notifyToController(Constants.MSG_CONNECTION_OPEN_3G_FAILED);
				AppApplication.modile_data_notify = false;
				SharedPreferences sp = context.getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putBoolean("mobile_data_notify", false);
				editor.commit();
				return;
			}
		}
		Log.i(TAG, "_3GConnect--->startTimeTask");
		connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
		connectTimeoutTimer.postDelayed(connectTimeoutTask, Constants.TIMEOUT_3G);
	}
	
	/**
	 * keep wifi radio open and remove all wifi configurations
	 * @param network
	 */
	private void _3GConnectWithWifiRadio(NetworkModel network) {
		WifiUtil.disableAllWifi(context, mWifiManager);
		if(isNetworkConnected(network)) {
			Log.w(TAG, "OnWifiRadio -->the 3g is already connected!");
			LogUtil.add("3g is already connected!");
			connectedNetwork = network.onClone();
			notifyToController(Constants.MSG_CONNECTION_ALREDAY_CONNECTED);
			return;
		}
		
		AppApplication.current_network = network;
		AppApplication.connecting_status = HomeActivity.STATUS_CONNECTING;
		context.sendBroadcast(new Intent(HomeActivity.ACTION));

		try {
			if (!_3GUtil.getMobileDataStatus(context)) {
				Log.i(TAG, "open 3g radio");
				_3GUtil.toggleMobileData(context, true);
			}
		} catch (MobileDataException e) {
			e.printStackTrace();
			if (AppApplication.modile_data_notify) {
				notifyToController(Constants.MSG_CONNECTION_OPEN_3G_FAILED);
				AppApplication.modile_data_notify = false;
				SharedPreferences sp = context.getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putBoolean("mobile_data_notify", false);
				editor.commit();
				return;
			}
		}
		Log.i(TAG, "_3GConnect--->startTimeTask");
		connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
		connectTimeoutTimer.postDelayed(connectTimeoutTask, Constants.TIMEOUT_3G);
	}
	
	/**
	 * reconnect 3g network
	 * @param network
	 */
	public void resume3GConnection(NetworkModel network) {
		Log.i(TAG, "_3GConnect--->resumed");
		connectingNetwork = network.onClone();
		connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
		connectTimeoutTimer.postDelayed(connectTimeoutTask, Constants.TIMEOUT_3G);
		
		// if the 3g is already connected
		if(connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState().toString()
				.equals(State.CONNECTED.toString())) {
			notifyToController(Constants.MSG_CONNECTION_SUCCESS);
			connectedNetwork = network.onClone();
			connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
		}
	}
	
	/**
	 * Add a new WiFi network exist in the configured list
	 * @param index int
	 * @return true if this action is success
	 */
	private boolean connectConfiguration(WifiConfiguration config) {
		if (config == null) {
			return false;
		}
//		if(isHotspot) {
			mWifiManager.saveConfiguration();
//		}
		return mWifiManager.enableNetwork(config.networkId, true);
	}
	
	/**
	 * Add a new WiFi network description to the set of configured networks
	 * @param {@link WifiConfiguration} objects
	 * @return true if this action is success
	 */
	private boolean AddNewWifi(WifiConfiguration config) {
		int NetId = mWifiManager.addNetwork(config);
//		if (isHotspot) {
			mWifiManager.saveConfiguration();
//		}
		return mWifiManager.enableNetwork(NetId, true);
	}

	/**
	 * return the current connection information
	 * @return {@link WifiModel}
	 */
	private WifiModel getCurrentConnectionWifi() {
		WifiInfo currentInfo = mWifiManager.getConnectionInfo();
		if(currentInfo == null || currentInfo.getSSID() == null) {
			return null;
		}
		if(!AppApplication.requestServer) {
			if(currentInfo.getSSID().equals(Constants.CMCC)) {
				return WifiUtil.readWepConfig(context, currentInfo.getSSID());
			} else if(currentInfo.getSSID().equals(Constants.CMCC_AUTO)) {
				if(AppApplication.isEapSim) {
					return WifiUtil.readEapSimConfig(context, currentInfo.getSSID());
				} else {
					return WifiUtil.readEapConfig(context, currentInfo.getSSID());
				}
			} else {
				return WifiUtil.readWepConfig(context, currentInfo.getSSID());
			}
		} else {
			if(currentInfo.getSSID().equals(Constants.CMCC)) {
				return WifiUtil.readWepConfig(context, currentInfo.getSSID());
			} else if(currentInfo.getSSID().equals(Constants.CMCC_AUTO)) {
				if(AppApplication.isEapSim) {
					return WifiUtil.readEapSimConfig(context, currentInfo.getSSID());
				} else {
					return WifiUtil.readEapConfig(context, currentInfo.getSSID());
				}
			}
		}
		return null;
	}
	
	/**
	 * open WIFI radio
	 */
	public void openWifiRadio() {
		Log.i(TAG, "open wifi radio");
		isWaitingWifiRadio = true;
		mWifiManager.setWifiEnabled(true);
	}
	
	/**
	 * close WIFI radio
	 */
	public void closeWifiRadio() {
		Log.i(TAG, "close wifi radio");
		mWifiManager.setWifiEnabled(false);
	}
	
	/**
	 * scan WIFI and return the result
	 * 
	 * @return list of ScanResult
	 */
	public boolean startScanResult() {
		Log.i(TAG, "start scan");
		isScanning = true;
		
		scanTimeoutTimer.removeCallbacks(scanTimeoutTask);
		scanTimeoutTimer.postDelayed(scanTimeoutTask, Constants.TIMEOUT_SCAN);
		
		AppApplication.connecting_status = HomeActivity.STATUS_SCANNING;
		context.sendBroadcast(new Intent(HomeActivity.ACTION));
		
		if(CommonUtil.isOphone()) {
			mWifiManager.startScan();// Ophone 2.0 is always false here
			return true;
		} else {
			if(!mWifiManager.startScan()) {
				Log.w(TAG, "start scan false!!!");
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * get network immediate connect status
	 * @return
	 */
	private boolean isNetworkConnected(NetworkModel net) {
		
		State netState = null;
		if(net.getType() == NetworkModel.TYPE_WIFI) {
			netState = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			WifiModel wifiInfo = getCurrentConnectionWifi();
			if(wifiInfo != null && netState.toString().equals(State.CONNECTED.toString()) 
					&& CommonUtil.isSameWifi(wifiInfo, (WifiModel) net)) {
				return true;
			} else {
				return false;
			}
		} else if(net.getType() == NetworkModel.TYPE_3G) {
			netState = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		}
		 
		return netState.toString().equals(State.CONNECTED.toString());
	}
	
	/**
	 * CMCC need to be authenticated
	 * @return
	 */
	private boolean cmccAuthentication() {
		return true;
//		return (AppApplication.username.toLowerCase().equals("cmcc") && AppApplication.password.toLowerCase().equals("cmcc"));
	}
	
	/**
	 * The class for receive network change state, include WiFi and 3g. After received
	 * broadcast, it will call {@link ConnectionManager#notifyToController(int)}
	 */
	private class NetworkStateReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_WIFI_RADIO)) {
				//send WiFi radio open message
				WifiRadioStats stats = WifiUtil.checkWifiRadioState(context);
				Log.d(TAG, "wifi radio state:" + stats.name());
				
				if(isWaitingWifiRadio && stats == WifiRadioStats.WIFI_RADIO_ENABLED) {
					isWaitingWifiRadio = false;
					notifyToController(Constants.MSG_CONNECTION_RADIO_CHANGE);
				} else if(stats == WifiRadioStats.WIFI_RADIO_DISABLED
						&& connectedNetwork != null
						&& connectedNetwork.getType() == NetworkModel.TYPE_WIFI) {
					Log.d(TAG, "NetworkStateReceiver---->WIFI----->radio off!!!!!!!");
					LogUtil.add("wifi breaked!!!!!!!!!!!!!!!!");
					disablingWifiRadio = false;
					if(isHotspot) {
						notifyToController(Constants.MSG_CONNECTION_HOTSPOT_BREAK);
					} else {
						notifyToController(Constants.MSG_CONNECTION_BREAK);
					}
				} else if(stats == WifiRadioStats.WIFI_RADIO_DISABLING
						&& connectedNetwork != null
						&& connectedNetwork.getType() == NetworkModel.TYPE_WIFI) {
					disablingWifiRadio = true;
				}
			} else if(intent.getAction().equals(ACTION_WIFI)) {
				//send WiFi connect success message
				NetworkInfo infoWlan = connManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI);
				if(infoWlan == null) {
					return;
				}
				State wifiStat = infoWlan.getState();
				Log.d(TAG, "wifiStat:" + wifiStat.name());
				LogUtil.add("wifi state:" + wifiStat);
				if (wifiStat == State.CONNECTED) {
					if(connectingNetwork !=null && connectingNetwork.getType() == NetworkModel.TYPE_WIFI) {
						if(isNetworkConnected(connectingNetwork)) {
							Log.i(TAG, "NetworkStateReceiver---->wifi connection success");
							// connect success, send success message to controller
							if(((WifiModel) connectingNetwork).getAuthenticationType().equalsIgnoreCase("PEAP")) {
								connectedNetwork = connectingNetwork.onClone();
								if(isHotspot) {
									notifyToController(Constants.MSG_CONNECTION_HOTSPOT_SUCCESS);
								} else {
									notifyToController(Constants.MSG_CONNECTION_SUCCESS);
								}
								connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
							} else if(((WifiModel) connectingNetwork).getAuthenticationType().equalsIgnoreCase("OPEN")) {
								if(cmccAuthentication()) {
									connectedNetwork = connectingNetwork.onClone();
									if(isHotspot) {
										notifyToController(Constants.MSG_CONNECTION_HOTSPOT_SUCCESS);
									} else {
										notifyToController(Constants.MSG_CONNECTION_SUCCESS);
									}
									connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
								} else {
									if(isHotspot) {
										notifyToController(Constants.MSG_CONNECTION_HOTSPOT_FAILED);
									} else {
										notifyToController(Constants.MSG_CONNECTION_FAILED);
									}
									connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
								}
								
							} else if(((WifiModel) connectingNetwork).getAuthenticationType().equalsIgnoreCase("SIM")) {
								connectedNetwork = connectingNetwork.onClone();
								if(isHotspot) {
									notifyToController(Constants.MSG_CONNECTION_HOTSPOT_SUCCESS);
								} else {
									notifyToController(Constants.MSG_CONNECTION_SUCCESS);
								}
								connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
							}
						} 
					} else if(connectedNetwork !=null && connectedNetwork.getType() == NetworkModel.TYPE_WIFI) {
						if(!isNetworkConnected(connectedNetwork)) {
							Log.d(TAG, "CONNECTED---->WIFI----->breaked!!!!!!!");
							LogUtil.add("other wifi connected, wifi breaked!!!!!!!!!!!!!!!!");
							if(isHotspot) {
								notifyToController(Constants.MSG_CONNECTION_HOTSPOT_BREAK);
							} else {
								notifyToController(Constants.MSG_CONNECTION_BREAK);
							}
						}
					}
				}
				// broken by outer action
				else if (wifiStat == State.DISCONNECTED 
						&& connectedNetwork != null
						&& connectedNetwork.getType() == NetworkModel.TYPE_WIFI) {
					Log.d(TAG, "NetworkStateReceiver----> WIFI----->breaked!!!!!!!");
					LogUtil.add(((WifiModel) connectedNetwork).getSSID() + " breaked!!!!!!!!!!!!!!!!");
					if(isHotspot) {
						notifyToController(Constants.MSG_CONNECTION_HOTSPOT_BREAK);
					} else {
						notifyToController(Constants.MSG_CONNECTION_BREAK);
					}
				}
			} else if (intent.getAction().equals(ACTION_3G)) {
				//send 3g connect success message
				NetworkInfo info3G = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if(info3G == null) {
					return;
				}
				State _3gStat = info3G.getState();
				Log.d(TAG, "_3gStat:" + _3gStat.name());
				if (_3gStat == State.CONNECTED
						&& connectingNetwork != null 
						&& connectingNetwork.getType() == NetworkModel.TYPE_3G) {
					if(isNetworkConnected(connectingNetwork)) {
						connectedNetwork = connectingNetwork.onClone();
						Log.i(TAG, "NetworkStateReceiver---->3g connection success");
						notifyToController(Constants.MSG_CONNECTION_SUCCESS);
						connectTimeoutTimer.removeCallbacks(connectTimeoutTask);
					}
				} else if (_3gStat == State.DISCONNECTED
						&& connectedNetwork != null 
						&& connectedNetwork.getType() == NetworkModel.TYPE_3G
						&& !disablingWifiRadio) {
					Log.d(TAG, "NetworkStateReceiver---->3g----->breaked!!!!!!!");
					LogUtil.add("3g breaked!!!!!!!!!!!!!!!!");
					notifyToController(Constants.MSG_CONNECTION_BREAK);
				}
			}
		}
		
	}
	
	/**
	 * WIFI scan complete receiver
	 *
	 */
	private class WifiScanReceiver extends BroadcastReceiver {
		final String ACTION = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if(isScanning && ACTION.equals(intent.getAction())) {
					if(scanTimeoutTimer != null && scanTimeoutTask != null) {
						scanTimeoutTimer.removeCallbacks(scanTimeoutTask);
					}
					isScanning = !isScanning;
					Log.i(TAG, "MSG_CONNECTION_SCAN_COMPLETE)");
					notifyToController(Constants.MSG_CONNECTION_SCAN_COMPLETE);
				}
			} catch (Exception e) {
			}
		}
	}
	
}
