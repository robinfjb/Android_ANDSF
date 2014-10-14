package com.chinamobile.android.connectionmanager.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.database.DBAdpter;
import com.chinamobile.android.connectionmanager.manager.ConnectionManager;
import com.chinamobile.android.connectionmanager.manager.PolicyManager;
import com.chinamobile.android.connectionmanager.manager.ReportManager;
import com.chinamobile.android.connectionmanager.model.AppModel;
import com.chinamobile.android.connectionmanager.model.HotspotModel;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.model._3GModel;
import com.chinamobile.android.connectionmanager.receiver.ScreenReceiver;
import com.chinamobile.android.connectionmanager.ui.HomeActivity;
import com.chinamobile.android.connectionmanager.ui.HotspotActivity;
import com.chinamobile.android.connectionmanager.ui.PopDialog;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.LogUtil;
import com.chinamobile.android.connectionmanager.util.NotifyUiUtil;
import com.chinamobile.android.connectionmanager.util.TimeUtil;
import com.chinamobile.android.connectionmanager.util.WifiUtil;
import com.chinamobile.android.connectionmanager.util.WifiUtil.WifiRadioStats;

/**
 * ServiceController is a controller and run in Android {@link Service} mode
 * <p>control {@link PolicyManager}, {@link ConnectionManager} and {@link ReportManager}. 
 * <br>receive messages from UI and managers
 * <br>After user install the application and open the application, the service will run in background
 */
public class ServiceController extends Service {
	private static final String TAG = "ServiceController";
	public static boolean is_andsf_start;
	private ConnectionManager connManager;
	private PolicyManager policyManager;
	private ReportManager reportManager;
	private UIMessageReceiver uiReceiver;
	private AccountMessageReceiver accountReceiver;//receiver to receive account complete message
	private ScreenMessageReceiver screenReceiver;//receiver to receive UI screen message
	private ScreenReceiver osReceiver;//receiver to receive system screen events
	private IntentFilter screenFilter;
	private List<NetworkModel> networkList = new ArrayList<NetworkModel>();;
	private Handler signalTrackTimer;
	private Handler priorityTrackTimer;
	private Handler restartConnectTimer;
	private SignalTrackTask signalTrackTask;// signal sample task
	private PriorityTrackTask priorityTrackTask;// ensure the WIFI priority
	private RestartConnectTask restartConnectTask;// restart task
	private PolicyModel activePolicy;
	private int networkIndex = -1;// the index of active network in the network list
	private DBAdpter mDbAdapter;
	private boolean setting_isDynamic;//is get policy dynamic?
	private boolean setting_signalCheck;// need WiFi signal strength check
	private boolean setting_askUser;// need to ask user before 
	private boolean setting_preferred;// user preferred notification enable?
	private ConnectionHandler connectionManagerHandler;
	private PolicyHandler policyManagerHandler;
	private SharedPreferences sharePreferences;
	private SharedPreferences sp;
	private WifiModel hotspot;
	private PopDialog appWarnDialog;
	private final WifiModel tempWifi = new WifiModel();// this is an object for judge whether is waiting WIFI radio open
	private Context mContext;
	private int applyPolicyCount = 0;// apply policy count range from start andsf to stop andsf
	private boolean isApplyingPolicy;
	private boolean needSwitchBack2Policy;
	private PolicyModel tempPolicy;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		
		mContext = this;
		mDbAdapter = new DBAdpter(mContext);
		connectionManagerHandler = new ConnectionHandler();
		connManager = new ConnectionManager(mContext, connectionManagerHandler);
		policyManagerHandler = new PolicyHandler();
		policyManager = new PolicyManager(mContext, policyManagerHandler, mDbAdapter);
		reportManager = new ReportManager(mContext, null, mDbAdapter);
		signalTrackTimer = new Handler();
		priorityTrackTimer = new Handler();
		restartConnectTimer = new Handler();
		restartConnectTask = new RestartConnectTask();
		signalTrackTask = new SignalTrackTask();
		priorityTrackTask = new PriorityTrackTask();
		sharePreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
		IntentFilter uiFilter = new IntentFilter(Constants.Action.ACTION_PREFERED);
		uiFilter.addAction(Constants.Action.ACTION_START);
		uiFilter.addAction(Constants.Action.ACTION_STOP);
		uiFilter.addAction(Constants.Action.ACTION_HOTSPOT);
		uiFilter.addAction(Constants.Action.ACTION_POLICY_APPLY);
		uiFilter.addAction(Constants.Action.ACTION_SETTING_DYNAMIC);
		uiFilter.addAction(Constants.Action.ACTION_SETTING_SIGNAL);
		uiFilter.addAction(Constants.Action.ACTION_SETTING_3GVALID);
		uiFilter.addAction(Constants.Action.ACTION_SETTING_AUTO_BREAK);
		uiFilter.addAction(Constants.Action.ACTION_SETTING_POLICY_APPLY);
		uiFilter.addAction(Constants.Action.ACTION_SETTING_AUTO_RUN);
		uiFilter.addAction(Constants.Action.ACTION_SETTING_PREFERED);
		uiFilter.addAction(Constants.Action.ACTION_SETTING_HOTSPOT_NOTIFY);
		uiFilter.addAction(Constants.Action.ACTION_CLEAN_UP);
		uiFilter.addAction(Constants.Action.ACTION_OPEN_MOBILE_DATA);
		uiFilter.addAction(Constants.Action.ACTION_APP_UPDATE);
		
		screenFilter = new IntentFilter();
		screenFilter.addAction(Constants.Action.ACTION_SCREEN_OFF);
		screenFilter.addAction(Constants.Action.ACTION_SCREEN_ON);
		uiReceiver = new UIMessageReceiver();
		accountReceiver = new AccountMessageReceiver();
		screenReceiver = new ScreenMessageReceiver();
		osReceiver = new ScreenReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(osReceiver, filter);
		registerReceiver(uiReceiver, uiFilter);
		
		reportManager.onStart();
		
//		AppApplication.getApp().displayTestToast("boot_complete:" +AppApplication.start_andsf);
		Log.d(TAG, "boot_complete:" + AppApplication.start_andsf);
		if(AppApplication.start_andsf) {
			AppApplication.start_andsf = false;
			startANDSF();
		}
		super.onCreate();
	}

	/*@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		return super.onStartCommand(intent, START_STICKY , startId);
	}*/
	
	/**
	 * start andsf function
	 */
	private void startANDSF() {
		is_andsf_start = true;
		setting_isDynamic = sharePreferences.getBoolean("dynamic", true);
		setting_signalCheck = sharePreferences.getBoolean("signal_check", true);
		setting_askUser = sharePreferences.getBoolean("notify_check", true);
		setting_preferred = sharePreferences.getBoolean("preferred_check", true);

		policyManager.onStart();
		// if connection manager already has started, stop it first 
		if(connManager.isStart) {
			connManager.onStop(false);
		}
		LogUtil.add("---start---" + TimeUtil.getNowFullTime() + "-------");
		if(setting_isDynamic) {
			policyManager.setIsDynamic(true);
		}
		startSignalTracking();
		applyPolicyCount = 0;
	}
	
	/**
	 * stop andsf function
	 */
	private void stopANDSF() {
		is_andsf_start = false;
		LogUtil.add("---stop---" + TimeUtil.getNowFullTime() + "-------");
		if(mDbAdapter != null) {
			mDbAdapter.close();
		}
		if (policyManager != null) {
			policyManager.onStop();
		}
		if (connManager != null) {
			connManager.onStop(false);
		}
		if(reportManager != null) {
			reportManager.updateReport();
		}
		if (priorityTrackTask != null) {
			priorityTrackTimer.removeCallbacks(priorityTrackTask);
		}
		if (signalTrackTask != null) {
			signalTrackTimer.removeCallbacks(signalTrackTask);
		}
		if (restartConnectTask != null) {
			restartConnectTimer.removeCallbacks(restartConnectTask);
		}
		NotifyUiUtil.clearAllNotification(mContext);
		networkList.clear();
		activePolicy = null;
		tempPolicy = null;
		isApplyingPolicy = false;
		needSwitchBack2Policy = false;
		AppApplication.current_network = null;
		AppApplication.previous_network_msg = null;
		AppApplication.connecting_status = HomeActivity.STATUS_UNDEFINE;
		AppApplication.prevoius_connecting_status = HomeActivity.STATUS_UNDEFINE;
		sendBroadcast(new Intent(HomeActivity.ACTION));
		
		applyPolicyCount = 0;
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		
		if (uiReceiver != null) {
			unregisterReceiver(uiReceiver);
		}
		if (osReceiver != null) {
			unregisterReceiver(osReceiver);
		}
		
		if(reportManager != null) {
			reportManager.onStop();
		}
		
		stopANDSF();
		
		AppApplication.getBMapManager().destroy();
		super.onDestroy();
	}
	
	
	/**
	 * clean the  network status in previous policy
	 */
	private void cleanUpPrevoiusPolicyNets() {
		networkList.clear();
		networkIndex = -1;
		connManager.onStop(false);
		unRegisterAccountMessageReceiver();
		
		if (priorityTrackTask != null) {
			priorityTrackTimer.removeCallbacks(priorityTrackTask);
		}
		if (restartConnectTask != null) {
			restartConnectTimer.removeCallbacks(restartConnectTask);
		}
	}
	
	/**
	 * Apply the policy, before apply the policy, clear the previous connection in previous policy
	 * @param policy
	 */
	private void applyPolicy(PolicyModel policy) {
		applyPolicyCount ++;
		AppApplication.connecting_status = HomeActivity.STATUS_APPLY_POLICY;
		sendBroadcast(new Intent(HomeActivity.ACTION));
		
		cleanUpPrevoiusPolicyNets();
		connManager.onStart(false, new ConnectionHandler());
		
		List<NetworkModel> localNetList = policy.getAccessNetworkList();
		for (int i = 0; i < localNetList.size(); i++) {
			networkList.add(localNetList.get(i).onClone());
		}
		try {
			mDbAdapter.openDatabase();
			mDbAdapter.addLogData(networkList.get(0).getType());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDbAdapter.close();
		}
		hotspot = null;
		if(activePolicy != null) {
			tempPolicy = activePolicy.clone();
		}
		LogUtil.add("is apply policy");
		isApplyingPolicy = true;
		HomeActivity.setNeedShowAutoDailog(false);
		HomeActivity.setNeedShowCmccDailog(false);
		handleNetworkList();
	}
	
	/**
	 * pick a valid network to connect, if no valid network, restart apply policy later
	 */
	private void handleNetworkList() {
		NetworkModel network = pickNetworkToConnect();
		if(network == null) {
			Log.w(TAG, "network is null------------");
			restartConnectLater();
		} else if(network == tempWifi) {
			// if still waiting for WiFi scan result, waiting until callback
		} else {
			doConnect(network);
		}
	}
	
	private void registerAccountMessageReceiver() {
		registerReceiver(accountReceiver, 
				new IntentFilter(Constants.Action.ACTION_CONTINUE_CONNECTION));
	}
	
	private void unRegisterAccountMessageReceiver() {
		if(accountReceiver != null) {
			try {
				unregisterReceiver(accountReceiver);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	/**
	 * do connect network, called {@link ConnectionManager}
	 * @param network
	 */
	private void doConnect(NetworkModel network) {
		reportManager.updateReport();
		networkIndex = networkList.indexOf(network);
		if(networkIndex > -1) {
			connManager.connect(network);
		}
	}
	/**
	 * choose an network to connect
	 * @param index  search form 0 to index-1 and form index+1 to end
	 * @return the target {@link NetworkModel}
	 */
	private NetworkModel pickNetworkToConnect() {
		if(networkList.isEmpty()) {
			return null;
		}
		final int size = networkList.size();
		List<NetworkModel> newList = new ArrayList<NetworkModel>();
		for (int i = 0; i < size; i++) {
			NetworkModel net = networkList.get(i);
			if(net == null) {
				continue;
			}
			
			if(net.isAbandoned()) {
				Log.i(TAG, "index:" + i + "/network is abandoned!");
				LogUtil.add("network is abandoned: index=" + i);
				continue;
			}
			newList.add(networkList.get(i));
		}
		int validCount = 0;
		for (int i = 0; i < newList.size(); i++) {
			NetworkModel activeNetwork = newList.get(i);
			LogUtil.add((activeNetwork.getType() == 2 ? "3G" : 
				"WIFI: " + ((WifiModel) activeNetwork).getSSID()) + 
					" is picked to do signal check");
			Log.d(TAG, (activeNetwork.getType() == 2 ? "3G" : 
				"WIFI: " + ((WifiModel) activeNetwork).getSSID()) + 
				" is picked to do signal check");
			if(activeNetwork.getType() == NetworkModel.TYPE_WIFI) {// is WIFI
				WifiModel wifi = (WifiModel) activeNetwork;
				int result = CommonUtil.validateSignalStrengthNetwork(wifi);
				LogUtil.add(wifi.getSSID() + " signal check reult: "
						+ (result == 0 ? "VALID" : (result == 1 ? "INVALID"
								: (result == 2 ? "NOT FOUND" : "No record"))));
				Log.d(TAG, "pickNetworkToConnect--->signal strength result-->"
						+ result);
				if(setting_signalCheck) {
					if(result == CommonUtil.SIGNAL_VALID) {//valid
						return wifi;
					}
					else if(result == CommonUtil.SIGNAL_INVALID 
							|| result == CommonUtil.SIGNAL_NONE){//invalid/not found
						if(i == newList.size() -1) {// is the last network
							int validIndex = CommonUtil.pickValidWifi(newList);
							if(validIndex > -1) {
								LogUtil.add("no other network, so connect this one even it's signal strength" +
										"is not valid!!");
								return newList.get(validIndex);
							}
						} else {
							validCount ++;
							continue;
						}
					} 
					else if(result == CommonUtil.SIGNAL_NO_RECORD){//no record
						if (!WifiUtil.isWifiRadioOpen(mContext)) {//WiFi radio not opened,waiting for open result callback
							LogUtil.add("open wifi radio first");
							connManager.openWifiRadio();
							Log.i(TAG, "open wifi radio first");
							return tempWifi;
						} else {//WiFi radio opened ,sample signal strength once
							Log.i(TAG, "start scan wifi ap");
							LogUtil.add("start scan wifi ap");
							/*try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}*/
							connManager.startScanResult();
							return tempWifi;
						}
					}
				} else {
					if(result == CommonUtil.SIGNAL_VALID 
							|| result == CommonUtil.SIGNAL_INVALID) {
						return wifi;
					}
					else if(result == CommonUtil.SIGNAL_NONE){//not found
						validCount ++;
						continue;
					}
					else if(result == CommonUtil.SIGNAL_NO_RECORD){//no record
						if (!WifiUtil.isWifiRadioOpen(mContext)) {//WiFi radio not opened,waiting for open result callback
							LogUtil.add("open wifi radio first");
							connManager.openWifiRadio();
							Log.i(TAG, "open wifi radio first");
							return tempWifi;
						} else {//WiFi radio opened ,sample signal strength once
							Log.i(TAG, "start scan wifi ap");
							LogUtil.add("start scan wifi ap");
							connManager.startScanResult();
							return tempWifi;
						}
					}
				}
			} else if(activeNetwork.getType() == NetworkModel.TYPE_3G){// is 3g
				Log.i(TAG, "pickNetworkToConnect--->validCount--->" + validCount);
				if(validCount > 0) {//higher priority WiFi exist in the list, need to sample WiFi signal strength even after connected 3g
					((_3GModel) activeNetwork).setNeedOpenWifiRadio(true);
					LogUtil.add("need to open wifi when connect 3G");
				} else {
					((_3GModel) activeNetwork).setNeedOpenWifiRadio(false);
				}
				return activeNetwork;
			}
		}
		return null;
	}

	/**
	 * get the preferred WiFi from database
	 * @param plmn
	 * @param lac
	 * @param position
	 * @return
	 */
	private NetworkModel getUserPreferredNetwokData(int plmn, int lac, int position) {
		NetworkModel net = null;
		try {
			mDbAdapter.openDatabase();
			net = mDbAdapter.getUserPreferredData(plmn, lac, position);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			mDbAdapter.close();
		}
		
		return net;
	}
	
	/**
	 * handle user preferred network and policy logic
	 * @param policy
	 */
	private void userPreferredHandle(PolicyModel policy) {
		int location = CommonUtil.getCellId(mContext);
		int plmn = CommonUtil.getPlmn(mContext);
		int lac = CommonUtil.getLac(mContext);
		NetworkModel preferredNetwork = getUserPreferredNetwokData(plmn, lac, location);
		if (preferredNetwork == null) {
			if(setting_askUser) {
				if (AppApplication.hotspot_number > 0) {
					NotifyUiUtil.notifyUserHotspotAndPolicy(mContext, AppApplication.hotspot_number);
				} else {
					NotifyUiUtil.notifyUserApplyPolicy(mContext);
				}
				AppApplication.connecting_status = HomeActivity.STATUS_NEW_POLICY;
				sendBroadcast(new Intent(HomeActivity.ACTION));
			} else {
				applyPolicy(policy);
			}
		} else {
			boolean rememberChoice = sharePreferences.getBoolean("preferred_remember", false);
			if (setting_preferred && !rememberChoice) {
				if (AppApplication.hotspot_number > 0) {
					NotifyUiUtil.notifyUserHotspotAndPrefer(mContext, AppApplication.hotspot_number);
				} else {
					NotifyUiUtil.notifyUserPreferredWiFi(mContext);
				}
				AppApplication.connecting_status = HomeActivity.STATUS_NEW_POLICY;
				sendBroadcast(new Intent(HomeActivity.ACTION));
			} else {
				if(setting_askUser) {
					if (AppApplication.hotspot_number > 0) {
						NotifyUiUtil.notifyUserHotspotAndPolicy(mContext, AppApplication.hotspot_number);
					} else {
						NotifyUiUtil.notifyUserApplyPolicy(mContext);
					}
					AppApplication.connecting_status = HomeActivity.STATUS_NEW_POLICY;
					sendBroadcast(new Intent(HomeActivity.ACTION));
				} else {
					applyPolicy(policy);
				}
			}
		}
	}
	
	/**
	 * start wifi signal strength tracking
	 */
	private void startSignalTracking() {
		if(WifiUtil.isWifiRadioOpen(mContext)) {
			signalTrackTimer.removeCallbacks(signalTrackTask);
			signalTrackTimer.postDelayed(signalTrackTask, 100);
		}
	}
	
	/**
	 * start network priority tracking
	 */
	private void startPriorityTracking() {
		if(!networkList.isEmpty() && WifiUtil.isWifiRadioOpen(mContext)) {
			priorityTrackTimer.removeCallbacks(priorityTrackTask);
			priorityTrackTimer.postDelayed(priorityTrackTask, 100);
		}
	}
	
	/**
	 * sample a signal record into {@link WifiModel}
	 * @param scanResultMaps
	 * @param count
	 * @return
	 */
	private boolean samplingStrength(Map<String, ScanResult> scanResultMaps,
			int count, List<ScanResult> resultList) {
		if(resultList != null && !resultList.isEmpty()) {
			scanResultMaps.clear();
			int resultSize = resultList.size();
			for (int i = resultSize - 1; i >= 0; i--) {
				ScanResult scanResult = resultList.get(i);
				scanResultMaps.put(scanResult.SSID, scanResult);
			}
			
			for (int i = 0; i < networkList.size(); i++) {
				NetworkModel net = networkList.get(i);
				if(net.getType() == NetworkModel.TYPE_3G) {
					if(i == networkList.size() - 1) {
						return true;
					}
					continue;
				} else {
					WifiModel wifi = (WifiModel) net;
					ScanResult result = scanResultMaps.get(wifi.getSSID());
					if (result == null) {// not exist in scan result list
						wifi.signalRecord[count] = WifiModel.SIGNAL_STATE_NONE;
					} else {
						final int strength = WifiManager.calculateSignalLevel(result.level, 10);
						if (strength >= Constants.minDBMLevel) {
							wifi.signalRecord[count] = WifiModel.SIGNAL_STATE_VALID;
						} else {
							wifi.signalRecord[count] = WifiModel.SIGNAL_STATE_INVALID;
						}
					}
				}
				if(i == networkList.size() - 1) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * restart connect whole network list later
	 */
	private void restartConnectLater() {
		if(!is_andsf_start) {
			return;
		}
		Log.i(TAG, "restart 15s later");
		LogUtil.add("restart try 15s later");
		
		sendBroadcast(new Intent(HomeActivity.ACTION));
		AppApplication.current_network = null;
		AppApplication.connecting_status = HomeActivity.STATUS_NO_CONNECTION;
		
		connManager.closeWifiRadio();
		
		try {
			registerReceiver(screenReceiver, screenFilter);
		} catch (Exception e) {
		}
		
		signalTrackTimer.removeCallbacks(signalTrackTask);
		priorityTrackTimer.removeCallbacks(priorityTrackTask);
		
		restartConnectTimer.removeCallbacks(restartConnectTask);
		if(CommonUtil.isScreenOn(mContext)) {
			restartConnectTimer.postDelayed(restartConnectTask, Constants.RESTART_CONNECTION_INTEVAL);
		}
		
	}
	
	/**
	 * receiver to receive UI action message
	 *
	 */
	public class UIMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(Constants.Action.ACTION_START)) {
				// only response messages from home page start button action
				boolean from = intent.getBooleanExtra("from_home", false);
				if(from) {
					startANDSF();
				}
			}
			else if(action.equals(Constants.Action.ACTION_STOP)) {
				stopANDSF();
			}
			else if(action.equals(Constants.Action.ACTION_PREFERED)) {
				//get the result from user preferred selection
				int index = intent.getIntExtra("which_wifi", -1);
				if(index == 0) {
					if(activePolicy != null) {
						applyPolicy(activePolicy);
					}
				} else if(index == 1) {//preferred
					int plmn = CommonUtil.getPlmn(mContext);
					int lac = CommonUtil.getLac(mContext);
					int cid = CommonUtil.getCellId(mContext);
					NetworkModel net = getUserPreferredNetwokData(plmn, lac, cid);
					if(net != null) {
						LogUtil.add("start to connect with " + (net.getType() == 2 ? "3G"
								: ("WIFI: " + ((WifiModel) net).getSSID())));
						PolicyModel policy = new PolicyModel();
						List<NetworkModel> list = new ArrayList<NetworkModel>();
						list.add(net);
						policy.setAccessNetworkList(list);
						tempPolicy = policy.clone();
						
						cleanUpPrevoiusPolicyNets();
						connManager.onStart(false, new ConnectionHandler());
						networkList.clear();
						networkList.add(net);
						isApplyingPolicy = true;
						hotspot = null;
						HomeActivity.setNeedShowAutoDailog(false);
						HomeActivity.setNeedShowCmccDailog(false);
						doConnect(net);
					}
				}
			} else if(action.equals(Constants.Action.ACTION_POLICY_APPLY)) {
				if(activePolicy != null) {
					applyPolicy(activePolicy);
					if(activePolicy.resource_from == PolicyModel.TYPE_FORM_STATIC) {
						AppApplication.applyStaticPolicy ++;
					}
				}
			} else if(action.equals(Constants.Action.ACTION_HOTSPOT)) {
				hotspot = (WifiModel) intent.getSerializableExtra("hotspot");
				long uid = intent.getLongExtra("hotspot_connect_session_id", 0);
				cleanUpPrevoiusPolicyNets();
				connManager.onStart(true, new ConnectionHandler(uid));
				HotspotActivity.isConnecting = true;
				reportManager.updateReport();
				connManager.connect(hotspot, true);
				try {
					mDbAdapter.openDatabase();
					mDbAdapter.addUserPreferredData(hotspot);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mDbAdapter.close();
				}
			} else if(action.equals(Constants.Action.ACTION_SETTING_DYNAMIC)) {
				setting_isDynamic = sharePreferences.getBoolean("dynamic", true);
				policyManager.setIsDynamic(setting_isDynamic);
			} else if (action.equals(Constants.Action.ACTION_SETTING_SIGNAL)) {
				setting_signalCheck = sharePreferences.getBoolean("signal_check", true);
			} else if (action.equals(Constants.Action.ACTION_SETTING_POLICY_APPLY)) {
				setting_askUser = sharePreferences.getBoolean("notify_check", true);
			} else if (action.equals(Constants.Action.ACTION_SETTING_PREFERED)) {
				setting_preferred = sharePreferences.getBoolean("preferred_check", false);
			} else if (action.equals(Constants.Action.ACTION_OPEN_MOBILE_DATA)) {
				Log.i(TAG, "continue to do connection after setting 3g");
				NetworkModel g3Net = (NetworkModel) intent.getSerializableExtra("3g_network_open");
				connManager.resume3GConnection(g3Net);
			} else if (action.equals(Constants.Action.ACTION_CLEAN_UP)) {
				connManager.onStop(true);
				unRegisterAccountMessageReceiver();
			} else if (action.equals(Constants.Action.ACTION_APP_UPDATE)) {
//				if(is_andsf_start) {
					policyManager.updateAppList();
//				}
			}
		}
	}

	/**
	 * receiver to receive account complete action message, after finishing handling, it will auto 
	 * unregister itself
	 *
	 */
	public class AccountMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.Action.ACTION_CONTINUE_CONNECTION)) {
				Log.i(TAG, "continue to do connection after setting account");
				final boolean account_complete = intent.getBooleanExtra("account_complete", false);
				final boolean is_hotspot = intent.getBooleanExtra("is_hotspot", false);
				final int index = intent.getIntExtra("network_index", -1);//to check the receiver session
				if(index != networkIndex) {
					return;
				}
				if(account_complete) {
					if(is_hotspot) {
						if(hotspot != null && connManager.isStart) {
							try {
								HotspotActivity.isConnecting = true;
								connManager.connect(hotspot, true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Log.e(TAG, "hotspot is null or connectionmanager is unstarted");
						}
					} else {
						if(!networkList.isEmpty() && networkIndex > -1)
							doConnect(networkList.get(networkIndex));
					}
				} else {
					if(is_hotspot) {
						sendBroadcast(new Intent(HomeActivity.ACTION));
						AppApplication.current_network = null;
						AppApplication.connecting_status = HomeActivity.STATUS_NO_CONNECTION;
						
						Intent intents = new Intent(HotspotActivity.ACTION_CONNECT_RESULT);
						intents.putExtra("hotspot_result", false);
						intents.putExtra("result_session_id", intent.getLongExtra("connect_id", 0));
						sendBroadcast(intents);
					} else {
						if(!networkList.isEmpty() && networkIndex > -1) {
							networkList.get(networkIndex).setAbandoned(true);
							LogUtil.add("after account setting");
							handleNetworkList();
						}
					}
				}
				unRegisterAccountMessageReceiver();
			} 
		}
		
	}
	
	/**
	 * receiver to receive UI screen action message
	 *
	 */
	public class ScreenMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Constants.Action.ACTION_SCREEN_ON) && is_andsf_start) {
				restartConnectTimer.removeCallbacks(restartConnectTask);
				restartConnectTimer.postDelayed(restartConnectTask, Constants.RESTART_CONNECTION_INTEVAL);
			} else if(intent.getAction().equals(Constants.Action.ACTION_SCREEN_OFF) && is_andsf_start) {
				restartConnectTimer.removeCallbacks(restartConnectTask);
				System.gc();
			}
		}
	}
	
	/**
	 * a receiver to receive ConnectionManager message
	 *
	 */
	@SuppressLint("HandlerLeak")
	private final class ConnectionHandler extends Handler {
		private long sessionId;// for hotspot
		public ConnectionHandler() {
			super();
		}
		
		public ConnectionHandler(long sessionId) {
			super();
			this.sessionId = sessionId;
		}
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case Constants.MSG_CONNECTION_OPEN_3G_FAILED:
				NetworkModel g3_network = null;
				try {
					g3_network = (NetworkModel) msg.obj;
				} catch (Exception e) {
				}
				if(g3_network != null) {
					NotifyUiUtil.notifyUserOpenMobileData(mContext, g3_network);
				}
				break;
			case Constants.MSG_CONNECTION_SET_CMCC_ACCOUNT:
				HomeActivity.setNeedShowCmccDailog(true);
				HomeActivity.setNetworkIndex(networkIndex);
				Intent intendCmcc = new Intent(HomeActivity.ACTION_ACCOUNT);
				NetworkModel nCmcc = null;
				try {
					nCmcc = (NetworkModel) msg.obj;
				}catch (Exception e) {
				}
				if(nCmcc != null) {
					intendCmcc.putExtra("network", nCmcc);
					intendCmcc.putExtra("network_index", networkIndex);
					sendBroadcast(intendCmcc);
					registerAccountMessageReceiver();
				}
				break;
			case Constants.MSG_CONNECTION_SET_AUTO_ACCOUNT:
				HomeActivity.setNeedShowAutoDailog(true);
				HomeActivity.setNetworkIndex(networkIndex);
				Intent intendAuto = new Intent(HomeActivity.ACTION_ACCOUNT);
				NetworkModel nAuto = null;
				try {
					nAuto = (NetworkModel) msg.obj;
				} catch (Exception e) {
				}
				if(nAuto != null) {
					intendAuto.putExtra("network", nAuto);
					sendBroadcast(intendAuto);
					registerAccountMessageReceiver();
				}
				break;
			case Constants.MSG_CONNECTION_SET_CMCC_ACCOUNT_HOTSPOT:
			case Constants.MSG_CONNECTION_SET_AUTO_ACCOUNT_HOTSPOT:
				Intent hotIntent = new Intent(HotspotActivity.ACTION_ACCOUNT);
				NetworkModel nHotspot = null;
				try {
					nHotspot = (NetworkModel) msg.obj;
				} catch (Exception e) {
				}
				if(nHotspot != null) {
					hotIntent.putExtra("network", nHotspot);
					hotIntent.putExtra("result_session_id", sessionId);
					sendBroadcast(hotIntent);
					registerAccountMessageReceiver();
				}
				break;
			case Constants.MSG_CONNECTION_SCAN_COMPLETE:
				Log.d(TAG, "scan compelete!!!");
				LogUtil.add("scan compelete!!!");
				try {
					CommonUtil.addWiFiConfig2OS(mContext);
				} catch (Exception e) {
				}
				
				List<ScanResult> results = WifiUtil.getScanResult(mContext);
				for (ScanResult scanResult : results) {
					Log.i(TAG, scanResult.SSID);
				}
				samplingStrength(new HashMap<String, ScanResult>(), 0, results);
				if(results != null) {
					Log.d(TAG, "scan result callback: count=" + results.size());
					LogUtil.add("scan result callback: count=" + results.size());
				}
				handleNetworkList();
				break;
			case Constants.MSG_CONNECTION_RADIO_CHANGE:
				Log.i(TAG, "ConnectionHandler---->MSG_CONNECTION_RADIO_CHANGE");
				
				WifiRadioStats result = WifiUtil.checkWifiRadioState(mContext);
				if(result == WifiRadioStats.WIFI_RADIO_ENABLED) { // WiFi radio opened
					if(is_andsf_start) {
						startSignalTracking();
					}
					LogUtil.add("after opened wifi radio, start scan result");
					if(!connManager.startScanResult()) {
						restartConnectLater();
					}
				}
				break;
			case Constants.MSG_CONNECTION_SUCCESS:
				NetworkModel net = null;
				try {
					net = (NetworkModel) msg.obj;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(net != null) {
					if(networkIndex < 0 || networkList.isEmpty() ||!is_andsf_start) {
						return;
					}
					NetworkModel currentNet = networkList.get(networkIndex);
					if (currentNet != null && currentNet.getType() == net.getType()) {
						if(currentNet.getType() == NetworkModel.TYPE_WIFI) {
							if (CommonUtil.isSameWifi((WifiModel) net, (WifiModel) currentNet)) {
								Log.i(TAG, "wifi connect success");
								LogUtil.add("wifi connect success!!!");
								
								reportManager.sendReport();
								reportManager.creatReport(currentNet);
								
								sendBroadcast(new Intent(HomeActivity.ACTION));
								AppApplication.current_network = currentNet;
								AppApplication.connecting_status = HomeActivity.STATUS_CONNECTED;
								AppApplication.time_base = SystemClock.elapsedRealtime();
							} else {
								Log.w(TAG, "ssid diff, not the same network");
							}
						} else if(currentNet.getType() == NetworkModel.TYPE_3G) {
							Log.i(TAG, "3g connect success");
							LogUtil.add("3g connect success!!!");
							
							// only WIFI will be sent report to server
							reportManager.creatReport(currentNet);
							
							sendBroadcast(new Intent(HomeActivity.ACTION));
							AppApplication.current_network = currentNet;
							AppApplication.connecting_status = HomeActivity.STATUS_CONNECTED;
							
							AppApplication.time_base = SystemClock.elapsedRealtime();
							if (((_3GModel) currentNet)
									.isNeedOpenWifiRadio()) {
								startPriorityTracking();
							}
						}
					} else {
						Log.w(TAG, "getType() diff, not the same network");
					}
				} else {
					Log.w(TAG, "net == null, not the same network");
				}
				
				break;
			case Constants.MSG_CONNECTION_FAILED:
				NetworkModel receivedNet = null;
				try {
					receivedNet = (NetworkModel) msg.obj;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(receivedNet != null) {
					if(networkIndex < 0 || networkList.isEmpty() || !is_andsf_start) {
						return;
					}
					NetworkModel failedNetwork = networkList.get(networkIndex);
					Log.e(TAG, "connection failed--->networkIndex: " + networkIndex);
					if (failedNetwork != null && failedNetwork.getType() == receivedNet.getType()) {
						if(failedNetwork.getType() == NetworkModel.TYPE_WIFI) {
							if (CommonUtil.isSameWifi((WifiModel) receivedNet, (WifiModel) failedNetwork)) {
								failedNetwork.retry ++;
								if(failedNetwork.retry >= Constants.RETRY_TIME) {
									failedNetwork.setAbandoned(true);
									LogUtil.add("wifi connect failed");
									handleNetworkList();
								} else {
									doConnect(failedNetwork);
								}
							} else {
								Log.w(TAG, "ssid diff, not the same network");
							}
						} else if(failedNetwork.getType() == NetworkModel.TYPE_3G) {
							failedNetwork.retry ++;
							if(failedNetwork.retry >= Constants.RETRY_TIME) {
								failedNetwork.setAbandoned(true);
								LogUtil.add("3g connect failed");
								handleNetworkList();
							} else {
								doConnect(failedNetwork);
							}
						}
					} else {
						Log.w(TAG, "getType() diff, not the same network");
					}
				} else {
					Log.w(TAG, "net2 == null, not the same network");
				}
				break;
				
			case Constants.MSG_CONNECTION_BREAK:
				AppApplication.current_network = null;
				AppApplication.connecting_status = HomeActivity.STATUS_NO_CONNECTION;
				sendBroadcast(new Intent(HomeActivity.ACTION));
				
				NetworkModel received = null;
				try {
					received = (NetworkModel) msg.obj;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (received != null) {
					if (networkIndex < 0 || networkList.isEmpty() || !is_andsf_start) {
						return;
					}
					NetworkModel currentNet = networkList.get(networkIndex);
					if (currentNet != null
							&& currentNet.getType() == received.getType()) {
						if (currentNet.getType() == NetworkModel.TYPE_WIFI) {
							if (!CommonUtil.isSameWifi((WifiModel) received, (WifiModel) currentNet)) {
								Log.w(TAG, "ssid diff, not the same network");
								break;
							}
						} else if (currentNet.getType() == NetworkModel.TYPE_3G) {
						}
					} else {
						Log.w(TAG, "getType() diff, not the same network");
						break;
					}
				} else {
					Log.w(TAG, "received == null, not the same network");
					break;
				}
				
				if(!networkList.isEmpty() && is_andsf_start) {
					for (NetworkModel netw : networkList) {
						if(netw.getType() == NetworkModel.TYPE_WIFI) {
							((WifiModel)netw).signalStrengthStatus = 0;
							((WifiModel)netw).resetRecord();
						}
					}
					
					Log.i(TAG, "start to reapply the policy");
					LogUtil.add("start to reapply the policy");
					reportManager.updateReport();
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					handleNetworkList();
				}
				break;
			case Constants.MSG_CONNECTION_ALREDAY_CONNECTED:
				NetworkModel net2 = null;
				try {
					net2 = (NetworkModel) msg.obj;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(net2 != null) {
					if(networkIndex < 0 || networkList.isEmpty() || !is_andsf_start) {
						return;
					}
					NetworkModel currentNet = networkList.get(networkIndex);
					if(currentNet.getType() == NetworkModel.TYPE_3G) {
						if (((_3GModel) currentNet)
								.isNeedOpenWifiRadio()) {
							startPriorityTracking();
						}
					} else if(currentNet.getType() == NetworkModel.TYPE_WIFI) {
						reportManager.sendReport();
					}
					reportManager.creatReport(currentNet);

					sendBroadcast(new Intent(HomeActivity.ACTION));
					AppApplication.connecting_status = HomeActivity.STATUS_CONNECTED;
					AppApplication.current_network = currentNet;
					
					if(applyPolicyCount < 2) {
						AppApplication.time_base = SystemClock.elapsedRealtime();
					}
				}
				break;
				
			case Constants.MSG_CONNECTION_HOTSPOT_SUCCESS:
				NetworkModel hot = null;
				try {
					hot = (NetworkModel) msg.obj;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(hot != null) {
					if (hotspot != null
							&& CommonUtil.isSameWifi((WifiModel) hot, hotspot)) {
						Log.i(TAG, "hotspot connect success");
						sendBroadcast(new Intent(HomeActivity.ACTION));
						AppApplication.current_network = hotspot;
						AppApplication.connecting_status = HomeActivity.STATUS_CONNECTED;
						AppApplication.time_base = SystemClock.elapsedRealtime();
						
						Intent intent = new Intent(HotspotActivity.ACTION_CONNECT_RESULT);
						intent.putExtra("hotspot_result", true);
						intent.putExtra("result_session_id", sessionId);
						sendBroadcast(intent);
						
						reportManager.sendReport();
						reportManager.creatReport(hot);
					} else {

					}
				}
				break;
			case Constants.MSG_CONNECTION_HOTSPOT_FAILED:
				Log.e(TAG, "hotspot connection failed");
				NetworkModel hot2 = null;
				try {
					hot2 = (NetworkModel) msg.obj;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (hot2 != null) {
					if (hotspot != null
							&& CommonUtil.isSameWifi((WifiModel) hot2,
									hotspot)) {
						hotspot.retry++;
						if (hotspot.retry >= Constants.RETRY_TIME) {
							hotspot.retry = 0;

							sendBroadcast(new Intent(HomeActivity.ACTION));
							AppApplication.current_network = null;
							AppApplication.connecting_status = HomeActivity.STATUS_NO_CONNECTION;
							
							Intent intent = new Intent(HotspotActivity.ACTION_CONNECT_RESULT);
							intent.putExtra("hotspot_result", false);
							intent.putExtra("result_session_id", sessionId);
							sendBroadcast(intent);
						} else {
							HotspotActivity.isConnecting = true;
							connManager.connect(hotspot, true);
						}
					} else {
						Log.w(TAG, "ssid diff, not the same hotspot");
					}
				}
				break;
			case Constants.MSG_CONNECTION_HOTSPOT_BREAK:
				Log.i(TAG, "MSG_CONNECTION_HOTSPOT_BREAK");
				NetworkModel hot4 = null;
				try {
					hot4 = (NetworkModel) msg.obj;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (hot4 != null) {
					if (hotspot != null && CommonUtil.isSameWifi((WifiModel) hot4, hotspot)) {
						sendBroadcast(new Intent(HomeActivity.ACTION));
						AppApplication.current_network = null;
						AppApplication.connecting_status = HomeActivity.STATUS_NO_CONNECTION;

						Intent intent = new Intent(HotspotActivity.ACTION_CONNECT_BREAK);
						intent.putExtra("result_session_id", sessionId);
						sendBroadcast(intent);
						
						reportManager.updateReport();
					} else {
						Log.w(TAG, "ssid diff, not the same hotspot");
					}
				}
				break;
			case Constants.MSG_CONNECTION_HOTSPOT_ALREDAY_CONNECTED:
				NetworkModel hot3 = null;
				try {
					hot3 = (NetworkModel) msg.obj;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (hot3 != null) {
					if (hotspot != null
							&& CommonUtil.isSameWifi((WifiModel) hot3, hotspot)) {
						
						sendBroadcast(new Intent(HomeActivity.ACTION));
						AppApplication.current_network = hotspot;
						AppApplication.connecting_status = HomeActivity.STATUS_CONNECTED;
						
						Intent intent = new Intent(HotspotActivity.ACTION_CONNECT_RESULT);
						intent.putExtra("hotspot_result", true);
						intent.putExtra("result_session_id", sessionId);
						sendBroadcast(intent);
						
						reportManager.sendReport();
						reportManager.creatReport(hot3);
					}
				}
				break;
			}
			super.handleMessage(msg);
		}
		
	}
	
	/**
	 * check whether policy network priority is same as app set
	 * @param policy
	 * @param what
	 * @return
	 */
	private boolean isPolicySameAsAppSet(final PolicyModel policy,
			final int what) {
		if (policy == null) {
			return true;
		}
		int appSetRemember = sp.getInt("app_warn_remember", 0);
		final List<NetworkModel> activeList = policy.getAccessNetworkList();
		if (!activeList.isEmpty()) {
			Map<String, Integer> appMap = policyManager.getAppList();
			String currentPckName = policyManager.getCurrentPackageName();
			final NetworkModel topNet = activeList.get(0);
			if (appMap.containsKey(currentPckName)) {
				int priority = appMap.get(currentPckName);
				if (priority == AppModel.WIFI_FIRST) {
					if (topNet.getType() != NetworkModel.TYPE_WIFI) {
						if(activeList.size() == 1 && activeList.get(0).getType() == NetworkModel.TYPE_3G) {
							// network list only have 3g network, do not popup dailog or apply application policy
							return true;
						}
						if (appSetRemember == 1) {// do
							PolicyModel newPolicy = new PolicyModel();
							List<NetworkModel> newList = new ArrayList<NetworkModel>();
							if(activeList.size() == 1) {
								NetworkModel onlyOneNet = activeList.get(0).onClone();
								onlyOneNet.setPriority(0);
								if(onlyOneNet.getType() == NetworkModel.TYPE_WIFI) {
									newList.add(onlyOneNet);
								} else {
									return true;
								}
							} else {
								for (int i = 0, j = 0; i < activeList.size(); i++) {
									NetworkModel netOther = activeList.get(i).onClone();
									if (netOther.getType() == NetworkModel.TYPE_WIFI) {
										netOther.setPriority(j++);
										newList.add(netOther);
									} else if (netOther.getType() == NetworkModel.TYPE_3G) {
										netOther.setPriority(activeList.size() - 1);
										newList.add(netOther);
									}
								}
							}
							newPolicy.setAccessNetworkList(newList);
							applyPolicy(newPolicy);
							needSwitchBack2Policy = true;
							return false;
						} else if (appSetRemember == 2) {// cancel
							return true;
						} else {
							if (appWarnDialog != null) {
								appWarnDialog.dismiss();
								appWarnDialog = null;
							}
							initAppWarnDialog(activeList, what,
									R.string.first_wifi);
							appWarnDialog.show();
							return false;
						}
					}

				} else if (priority == AppModel.G3_FIRST) {
					if (topNet.getType() != NetworkModel.TYPE_3G) {
						if (appWarnDialog != null) {
							appWarnDialog.dismiss();
							appWarnDialog = null;
						}
						if (appSetRemember == 1) {// do
							PolicyModel newPolicy = new PolicyModel();
							List<NetworkModel> newList = new ArrayList<NetworkModel>();
							if(activeList.size() == 1) {
								NetworkModel onlyOneNet = activeList.get(0).onClone();
								if(onlyOneNet.getType() == NetworkModel.TYPE_WIFI) {
									onlyOneNet.setPriority(1);
									_3GModel g3Net = new _3GModel(NetworkModel.NAME_3G, 0);
									newList.add(g3Net);
									newList.add(onlyOneNet);
								} else if(onlyOneNet.getType() == NetworkModel.TYPE_3G){
									onlyOneNet.setPriority(0);
									newList.add(onlyOneNet);
								}
							} else {
								for (int i = 0, j = 1; i < activeList.size(); i++) {
									NetworkModel netOther = activeList.get(i)
											.onClone();
									if (netOther.getType() == NetworkModel.TYPE_WIFI) {
										netOther.setPriority(j++);
										newList.add(netOther);
									} else if (netOther.getType() == NetworkModel.TYPE_3G) {
										netOther.setPriority(0);
										newList.add(netOther);
									}
								}
							}
							newPolicy.setAccessNetworkList(newList);
							applyPolicy(newPolicy);
							needSwitchBack2Policy = true;
							return false;
						} else if (appSetRemember == 2) {// cancel
							return true;
						} else {
							initAppWarnDialog(activeList, what,
									R.string.first_3g);
							appWarnDialog.show();
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * check whether current connected network is same as app set
	 * @param policy
	 */
	private void handleNetworkSameAsAppSet(final PolicyModel policy) {
		if (policy == null) {
			return;
		}
		int appSetRemember = sp.getInt("app_warn_remember", 0);
		int type = CommonUtil.getCurrentNetworkInfo(mContext);
		Map<String, Integer> appMap = policyManager.getAppList();
		String currentPckName = policyManager.getCurrentPackageName();
		if (appMap.containsKey(currentPckName)) {
			int priority = appMap.get(currentPckName);
			if (priority == AppModel.WIFI_FIRST) {
				List<NetworkModel> activeList = policy.getAccessNetworkList();
				if (type != 0) {
					if(activeList.size() == 1 && activeList.get(0).getType() == NetworkModel.TYPE_3G) {
						// network list only have 3g network, do not popup dailog or apply application policy
					} else {
						if (appWarnDialog != null) {
							appWarnDialog.dismiss();
							appWarnDialog = null;
						}
						if(appSetRemember == 1) {//do
							PolicyModel newPolicy = new PolicyModel();
							List<NetworkModel> newList = new ArrayList<NetworkModel>();
							if(activeList.size() == 1) {
								NetworkModel onlyOneNet = activeList.get(0).onClone();
								onlyOneNet.setPriority(0);
								if(onlyOneNet.getType() == NetworkModel.TYPE_WIFI) {
									newList.add(onlyOneNet);
								}
							} else {
								for (int i = 0, j = 0; i < activeList.size(); i++) {
									NetworkModel netOther = activeList.get(i).onClone();
									if(netOther.getType() == NetworkModel.TYPE_WIFI) {
										netOther.setPriority(j++);
										newList.add(netOther);
									} else if(netOther.getType() == NetworkModel.TYPE_3G){
										netOther.setPriority(activeList.size() - 1);
										newList.add(netOther);
									}
								}
							}
							newPolicy.setAccessNetworkList(newList);
							applyPolicy(newPolicy);
							needSwitchBack2Policy = true;
						} else if(appSetRemember == 2) {//cancel
							// do nothing
						} else {
							initAppWarnDialog(policy.getAccessNetworkList(), -1, R.string.first_wifi);
							appWarnDialog.show();
						}
					}
					
				}
			} else if (priority == AppModel.G3_FIRST) {
				if (type != 1) {
					if (appWarnDialog != null) {
						appWarnDialog.dismiss();
						appWarnDialog = null;
					}
					if(appSetRemember == 1) {//do
						PolicyModel newPolicy = new PolicyModel();
						List<NetworkModel> newList = new ArrayList<NetworkModel>();
						List<NetworkModel> activeList = policy.getAccessNetworkList();
						if(activeList.size() == 1) {
							NetworkModel onlyOneNet = activeList.get(0).onClone();
							if(onlyOneNet.getType() == NetworkModel.TYPE_WIFI) {
								onlyOneNet.setPriority(1);
								_3GModel g3Net = new _3GModel(NetworkModel.NAME_3G, 0);
								newList.add(g3Net);
								newList.add(onlyOneNet);
							} else if(onlyOneNet.getType() == NetworkModel.TYPE_3G){
								onlyOneNet.setPriority(0);
								newList.add(onlyOneNet);
							}
						} else {
							for (int i = 0, j = 1; i < activeList.size(); i++) {
								NetworkModel netOther = activeList.get(i).onClone();
								if(netOther.getType() == NetworkModel.TYPE_WIFI) {
									netOther.setPriority(j++);
									newList.add(netOther);
								} else if(netOther.getType() == NetworkModel.TYPE_3G){
									netOther.setPriority(0);
									newList.add(netOther);
								}
							}
						}
						newPolicy.setAccessNetworkList(newList);
						applyPolicy(newPolicy);
						needSwitchBack2Policy = true;
					} else if(appSetRemember == 2) {//cancel
						// do nothing
					} else {
						initAppWarnDialog(policy.getAccessNetworkList(), -1, R.string.first_3g);
						appWarnDialog.show();
					}
				}
			} else {
				if(needSwitchBack2Policy) {
					needSwitchBack2Policy = false;
					applyPolicy(policy);
				}
			}
		} else {
			if(needSwitchBack2Policy) {
				needSwitchBack2Policy = false;
				applyPolicy(policy);
			}
		}
	}
	
	/**
	 * prepare the app set dialog
	 * @param activeList
	 * @param what
	 * @param string
	 */
	private void initAppWarnDialog(final List<NetworkModel> activeList, final int what, final int string) {
		appWarnDialog = new PopDialog(mContext,
				getString(R.string.app_network_select_title),
				String.format(getString(R.string.app_network_select_content), getString(string)),
				getString(R.string.ok), 
				getString(R.string.cancel),
				true,
				R.style.CMDialog, false);
		appWarnDialog.setLeftListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PolicyModel newPolicy = new PolicyModel();
				List<NetworkModel> newList = new ArrayList<NetworkModel>();
				if(string == R.string.first_wifi) {
					if(activeList.size() == 1) {
						NetworkModel onlyOneNet = activeList.get(0).onClone();
						onlyOneNet.setPriority(0);
						if(onlyOneNet.getType() == NetworkModel.TYPE_WIFI) {
							newList.add(onlyOneNet);
						} else {
							appWarnDialog.dismiss();
							return;
						}
					} else {
						for (int i = 0, j = 0; i < activeList.size(); i++) {
							NetworkModel netOther = activeList.get(i).onClone();
							if(netOther.getType() == NetworkModel.TYPE_WIFI) {
								netOther.setPriority(j++);
								newList.add(netOther);
							} else if(netOther.getType() == NetworkModel.TYPE_3G){
								netOther.setPriority(activeList.size() - 1);
								newList.add(netOther);
							}
						}
					}
				} else if(string == R.string.first_3g) {
					if(activeList.size() == 1) {
						NetworkModel onlyOneNet = activeList.get(0).onClone();
						if(onlyOneNet.getType() == NetworkModel.TYPE_WIFI) {
							onlyOneNet.setPriority(1);
							_3GModel g3Net = new _3GModel(NetworkModel.NAME_3G, 0);
							newList.add(g3Net);
							newList.add(onlyOneNet);
						} else if(onlyOneNet.getType() == NetworkModel.TYPE_3G){
							onlyOneNet.setPriority(0);
							newList.add(onlyOneNet);
						}
					} else {
						for (int i = 0, j = 1; i < activeList.size(); i++) {
							NetworkModel netOther = activeList.get(i).onClone();
							if(netOther.getType() == NetworkModel.TYPE_WIFI) {
								netOther.setPriority(j++);
								newList.add(netOther);
							} else if(netOther.getType() == NetworkModel.TYPE_3G){
								netOther.setPriority(0);
								newList.add(netOther);
							}
						}
					}
				}
				newPolicy.setAccessNetworkList(newList);
				applyPolicy(newPolicy);
				appWarnDialog.dismiss();
				
				if(appWarnDialog.getCheckBoxStates()) {
					Editor sharedata = sp.edit();
					sharedata.putInt("app_warn_remember", 1);
					sharedata.commit();
				}
				
				needSwitchBack2Policy = true;
			}
		});
		appWarnDialog.setRightListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				appWarnDialog.dismiss();
				switch (what) {
				case Constants.MSG_REQUEST_SUCCESS:
					userPreferredHandle(activePolicy);
					break;
				case Constants.MSG_REQUEST_FAIL:
					userPreferredHandle(activePolicy);
					break;
				case Constants.MSG_HAS_NEW_POLICY:
					if(setting_askUser) {
						NotifyUiUtil.notifyUserApplyPolicy(mContext);
						AppApplication.connecting_status = HomeActivity.STATUS_NEW_POLICY;
						sendBroadcast(new Intent(HomeActivity.ACTION));
					} else {
						applyPolicy(activePolicy);
					}
					break;
				case Constants.MSG_STATIC_POLICY:
					if(setting_askUser) {
						NotifyUiUtil.notifyUserApplyPolicy(mContext);
						AppApplication.connecting_status = HomeActivity.STATUS_NEW_POLICY;
						sendBroadcast(new Intent(HomeActivity.ACTION));
					} else {
						AppApplication.applyStaticPolicy ++;
						applyPolicy(activePolicy);
					}
					break;
				case Constants.MSG_REQUEST_NO_ACTIVE_POLICY:
					userPreferredHandle(activePolicy);
					break;
				case Constants.MSG_STATIC_POLICY_APPLY:
					applyPolicy(activePolicy);
					break;
				default:
					break;
				}
				
				if(appWarnDialog.getCheckBoxStates()) {
					Editor sharedata = sp.edit();
					sharedata.putInt("app_warn_remember", 2);
					sharedata.commit();
				}
			}
		});
		appWarnDialog.setCancelable(false);
		appWarnDialog.setCanceledOnTouchOutside(false);
		appWarnDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		
	}
	/**
	 * receiver to receive PolicyManager message
	 *
	 */
	@SuppressLint("HandlerLeak")
	private final class PolicyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case Constants.MSG_REQUEST_SUCCESS:// cell id changed and 
													//get a new policy list successfully
				Log.i(TAG, "PolicyHandler------>MSG_REQUEST_SUCCESS");
				activePolicy = policyManager.getActivePolicy();
				if(activePolicy != null) {
					if(isPolicySameAsAppSet(activePolicy, what)) {
						userPreferredHandle(activePolicy);
					}
				} else {
					Log.e(TAG, "PolicyHandler------>no active policy get");
				}
				break;
			case Constants.MSG_REQUEST_FAIL:
				// stop the previous policy first
				Log.i(TAG, "PolicyHandler------>MSG_REQUEST_FAIL");
				// get previous data from local storage
				activePolicy = policyManager.getPreviousPolicy();
				try {
					mDbAdapter.openDatabase();
					List<HotspotModel> list = mDbAdapter.readHotspotList(
							CommonUtil.getPlmn(mContext),
							CommonUtil.getLac(mContext),
							CommonUtil.getCellId(mContext));
					AppApplication.hotspot_number = list.size();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mDbAdapter.close();
				}
				
				if(activePolicy != null) {
					Log.i(TAG, "PolicyHandler------>Previous POLICY");
					if(isPolicySameAsAppSet(activePolicy, what)) {
						userPreferredHandle(activePolicy);
					}
				} else {
					Log.e(TAG, "PolicyHandler------>no active policy get");
				}
				break;
			case Constants.MSG_HAS_NEW_POLICY:
				// stop the previous policy first
				Log.i(TAG, "PolicyHandler------>MSG_HAS_NEW_POLICY");
				NotifyUiUtil.cleanPolicyNotification(mContext);
				activePolicy = policyManager.getActivePolicy();
				if(activePolicy != null) {
					if(isPolicySameAsAppSet(activePolicy, what)) {
						if(setting_askUser) {
							NotifyUiUtil.notifyUserApplyPolicy(mContext);
							AppApplication.connecting_status = HomeActivity.STATUS_NEW_POLICY;
							sendBroadcast(new Intent(HomeActivity.ACTION));
						} else {
							applyPolicy(activePolicy);
						}
					}
				} else {
					Log.e(TAG, "PolicyHandler------>no active policy get");
				}
				break;
			case Constants.MSG_STATIC_POLICY:
				// stop the previous policy first
				Log.i(TAG, "PolicyHandler------>MSG_STATIC_POLICY");
				NotifyUiUtil.cleanPolicyNotification(mContext);
				activePolicy = policyManager.getLocalPolicy();
				if(isPolicySameAsAppSet(activePolicy, what)) {
					if(setting_askUser) {
						NotifyUiUtil.notifyUserApplyPolicy(mContext);
						AppApplication.connecting_status = HomeActivity.STATUS_NEW_POLICY;
						sendBroadcast(new Intent(HomeActivity.ACTION));
					} else {
						AppApplication.applyStaticPolicy ++;
						applyPolicy(activePolicy);
					}
				}
				break;
			case Constants.MSG_REQUEST_NO_ACTIVE_POLICY:
				Log.i(TAG, "PolicyHandler------>MSG_REQUEST_NO_ACTIVE_POLICY");
				NotifyUiUtil.cleanPolicyNotification(mContext);
				activePolicy = policyManager.getLocalPolicy();
				if(isPolicySameAsAppSet(activePolicy, what)) {
					userPreferredHandle(activePolicy);
				}
				break;
			case Constants.MSG_STATIC_POLICY_APPLY:
				Log.i(TAG, "PolicyHandler------>MSG_STATIC_POLICY_APPLY");
				NotifyUiUtil.cleanPolicyNotification(mContext);
				activePolicy = policyManager.getLocalPolicy();
				if(isPolicySameAsAppSet(activePolicy, what)) {
					applyPolicy(activePolicy);
				}
				break;
			case Constants.MSG_CLEAN_UP:
				cleanUpPrevoiusPolicyNets();
				break;
			case Constants.MSG_APP_NET:
				handleNetworkSameAsAppSet(tempPolicy);
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	
	/**
	 * this class is Wifi signal sample task, if the controller just open wifi radio, 
	 * it will call {@link ServiceController#signalStrengthHandler}
	 * to send a message to do asynchronous action 
	 */
	private class SignalTrackTask implements Runnable {
		Map<String, ScanResult> scanResultMaps = new HashMap<String, ScanResult>();
		int count = 0;
		@Override
		public void run() {
			if(WifiUtil.isWifiRadioOpen(mContext)) {
				if(count > 9) {

					count = 0;
				}
				List<ScanResult> results = WifiUtil.getScanResult(mContext);
				if(samplingStrength(scanResultMaps, count, results)) {
					count ++;
				}
			}
			signalTrackTimer.postDelayed(signalTrackTask, Constants.SIGNAL_TIME_INTERVAL);
		}
	}
	
	/**
	 * if the current connection is 3G, and there is any WiFi connection 
	 * which priority is higher than current connection, the controller need to switch back 
	 * to the WiFi connection only if the signal strength is valid
	 */
	private class PriorityTrackTask implements Runnable {

		@Override
		public void run() {

			WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiUtil.disableAllWifi(mContext, mWifiManager);// disable wifi in order to 
															// avoid auto-connect to other wifi
			boolean needRestart = true;
			for (int i = 0; i < networkIndex; i++) {
				NetworkModel network = networkList.get(i);
				if(network.isAbandoned() || network.getType() != NetworkModel.TYPE_WIFI) {
					continue;
				}
				WifiModel wifi = (WifiModel) network;
				
				int len = CommonUtil.getArrayValidLength(wifi.signalRecord);
				Log.i(TAG, "len=" + len);
				if(len < 10) {// the 10 record data must all > 0
					continue;
				} else {
//				wifi.resetRecord();
//				List<ScanResult> results = WifiUtil.getScanResult(mContext);
//				for (ScanResult scanResult : results) {
//					Log.i(TAG, scanResult.SSID);
//				}
//				samplingStrength(new HashMap<String, ScanResult>(), 0, results);
					final int result = CommonUtil.validateSignalStrengthNetwork((WifiModel) network);
					Log.i(TAG, "result=" + result); 
					boolean valid;
					if(setting_signalCheck) {
						valid = (result == 0);
					} else {
						valid = (result == 0 || result == 1);
					}
					if(valid) {
						LogUtil.add("switch back to connect: " + network.toString());
						networkIndex = i;
						doConnect(network);
						needRestart = false;
					}
				}
			}
			if(needRestart)
				priorityTrackTimer.postDelayed(priorityTrackTask, Constants.POLICY_PICK_TIME_INTERVAL);
		}
	}
	
	/**
	 * restart apply policy task
	 *
	 */
	private class RestartConnectTask implements Runnable {
		@Override
		public void run() {
			Log.i(TAG, "start to restart!!!");
			LogUtil.add("start to restart!!!");
			unregisterReceiver(screenReceiver);
			if(activePolicy != null && !networkList.isEmpty()) {
				for (NetworkModel net : networkList) {
					net.setAbandoned(false);
					if(net.getType() == NetworkModel.TYPE_WIFI) {
						((WifiModel)net).signalStrengthStatus = 0;
						((WifiModel)net).retry = 0;
						((WifiModel)net).resetRecord();
					}
				}
				handleNetworkList();
			}
		}
	}
}
