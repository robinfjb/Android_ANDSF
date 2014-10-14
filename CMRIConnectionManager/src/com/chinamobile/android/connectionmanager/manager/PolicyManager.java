package com.chinamobile.android.connectionmanager.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.controller.ServiceController;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.database.DBAdpter;
import com.chinamobile.android.connectionmanager.http.CMHttpHandler;
import com.chinamobile.android.connectionmanager.http.CMHttpListener;
import com.chinamobile.android.connectionmanager.http.CMRequest;
import com.chinamobile.android.connectionmanager.http.CMResponse;
import com.chinamobile.android.connectionmanager.http.OMARequest;
import com.chinamobile.android.connectionmanager.http.OMAResponse;
import com.chinamobile.android.connectionmanager.model.AppModel;
import com.chinamobile.android.connectionmanager.model.HotspotModel;
import com.chinamobile.android.connectionmanager.model.HotspotModel.WlanInfo;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel.GeoLocation;
import com.chinamobile.android.connectionmanager.model.PolicyModel.TimeAndDate;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.model._3GModel;
import com.chinamobile.android.connectionmanager.omadm.ProtocolFormatter;
import com.chinamobile.android.connectionmanager.omadm.ProtocolManager;
import com.chinamobile.android.connectionmanager.omadm.ProtocolParser;
import com.chinamobile.android.connectionmanager.omadm.ProtocolParserException;
import com.chinamobile.android.connectionmanager.omadm.ProtocolUtil;
import com.chinamobile.android.connectionmanager.omadm.protocol.Item;
import com.chinamobile.android.connectionmanager.omadm.protocol.ItemizedCommand;
import com.chinamobile.android.connectionmanager.omadm.protocol.Replace;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncML;
import com.chinamobile.android.connectionmanager.ui.HomeActivity;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.FileUtil;
import com.chinamobile.android.connectionmanager.util.LocationUtil;
import com.chinamobile.android.connectionmanager.util.LogUtil;
import com.chinamobile.android.connectionmanager.util.NotifyUiUtil;
import com.chinamobile.android.connectionmanager.util.TimeUtil;

/**
 * The manager manage policies, provide interface of below:
 * <p>1.request to policy server to get policy list
 * <br>2.get the active policy
 * <br>3.track the policy time and notify to 
 * {@link ServiceController}
 * </p>
 */
public class PolicyManager extends BaseManager implements CMHttpListener{
	private static final String TAG = "PolicyManager";

	private TelephonyManager telManager;
	public static CIDPhoneStateListener cidListener;
	private IntervalTask intervalTask;
	private PolicyTimeTask timeExpireTask, timeArriveTask;
	private PolicyPickTask policyPickTask;
	private PolicyLocationTask policyLocationTask;
	private List<PolicyModel> policyList = new ArrayList<PolicyModel>();
	private List<PolicyModel> serverPolicyList = new ArrayList<PolicyModel>();
	private PolicyModel activePolicy;
	private AppApplication app;
	public static int cellId = Integer.MIN_VALUE;
	private boolean isStarting;// after policy manager start, it will request new policy, 
								//this flag is to judge whether policy manager just start
	private Handler requestHandler = new Handler();
	private Handler pickPolicyHandler = new Handler();
	private Handler timeHandler = new Handler();
	private Handler locationHander = new Handler();
	private CMHttpHandler mHttpHandler;
	private DBAdpter mDbAdapter;
	private boolean isDynamic;
	private ProtocolManager manager;
	private long unique;//request session
	private ProtocolParser parser;
	private SyncML synclP1 = null;
	private SyncML synclP3 = null;
	private String policyXml = null;
	private String discoXml = null;
	private Map<String, String> wlanInfo = new LinkedHashMap<String, String>();
	private TopAppCheckThread appCheckThread;
	private Map<String, Integer> appList = new HashMap<String, Integer>();
	private String currentPackageName;
	
	/**
	 * get config app list from DB
	 * @return
	 */
	public Map<String, Integer> getAppList() {
		appList.clear();
		List<AppModel> readList = new ArrayList<AppModel>();
		try {
			mDbAdapter.openDatabase();
			readList = mDbAdapter.readAppList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDbAdapter.close();
		}
		for (AppModel appModel : readList) {
			appList.put(appModel.getPkgName(), appModel.getNetworkPri());
		}
		return appList;
	}
	
	/**
	 * get top activity of task package name
	 * @return
	 */
	public String getCurrentPackageName() {
		if(currentPackageName == null) {
			ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> list = mActivityManager.getRunningTasks(1);
			RunningTaskInfo taskInfo = list.get(0);
			currentPackageName = taskInfo.topActivity.getPackageName();
		}
		return currentPackageName;
	}
	
	public PolicyManager(Context context, Handler handler, DBAdpter mDbAdapter) {
		super(context, handler);
		app = AppApplication.getApp();
		telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		cidListener = new CIDPhoneStateListener();
		intervalTask = new IntervalTask();
		timeExpireTask = new PolicyTimeTask(PolicyTimeTask.EXPIRED);
		timeArriveTask = new PolicyTimeTask(PolicyTimeTask.ARRIVED);
		policyPickTask = new PolicyPickTask();
		policyLocationTask = new PolicyLocationTask();
		this.mDbAdapter = mDbAdapter;
		manager = new ProtocolManager(context);
		appCheckThread = new TopAppCheckThread();
		appCheckThread.start();
	}
	
	/**
	 * initial parameters, start/resume timeThread and register listener
	 */
	@Override
	public void onStart() {
		Log.i(TAG, "onStart");
		isStarting = true;
		registerCellIdListener();
		
		cellId = Integer.MIN_VALUE;
		mDbAdapter.deleteExpiredPolicy();
		
		cleanUp();
		notifyToController(Constants.MSG_CLEAN_UP);
		AppApplication.cellIdChangeTimes = 0;
		AppApplication.applyStaticPolicy = 0;
		LocationUtil.getInstance(context).start();
		
		mHttpHandler = new CMHttpHandler(this);
		appCheckThread.startThread();
		updateAppList();
	}
	
	/**
	 * pause timeThread and unregister listener
	 */
	@Override
	public void onStop() {
		Log.i(TAG, "onStop");

		unregisterCellIdListener();
		isStarting = false;
		cellId = Integer.MIN_VALUE;
		LocationUtil.getInstance(context).stop();
		
		if(intervalTask != null) {
			requestHandler.removeCallbacks(intervalTask);
		}
		
		cleanUp();
		notifyToController(Constants.MSG_CLEAN_UP);
		
		if(mHttpHandler != null) {
			mHttpHandler.stop();
			mHttpHandler = null;
		}
		AppApplication.cellIdChangeTimes = 0;
		AppApplication.applyStaticPolicy = 0;
		
		Intent hotNitifyIntent = new Intent(HomeActivity.ACTION_HOTSPOT_NOTIFICATION);
		hotNitifyIntent.putExtra("hotspot_number", -1);
		context.sendBroadcast(hotNitifyIntent);
		AppApplication.hotspot_number = -1;
		
		appCheckThread.stopThread();
	}
	
	/**
	 * set is dynamic policy
	 * @param isDynamic
	 */
	public void setIsDynamic(boolean isDynamic) {
		this.isDynamic = isDynamic;
	}
	
	/**
	 * get the active policy
	 * @param policyList
	 * @return PolicyModel
	 */
	public PolicyModel getActivePolicy() {
		if(activePolicy == null) {
			return null;
		}
		CommonUtil.handleDefaultSSID(activePolicy);
		
		//===============for log========start
		LogUtil.add("the network list:");
		int count = 0;
		for (NetworkModel i : activePolicy.getAccessNetworkList()) {
			count++;
			LogUtil.add(count + "." + ((i.getName().equals("3"))? "WIFI:" + ((WifiModel)i).getSSID(): "3G"));
		}
		//===============for log========end
		
		return activePolicy;
	}
	
	
	@Override
	protected void notifyToController(int message) {
		if(message == Constants.MSG_HAS_NEW_POLICY) {
			if(activePolicy != null) {
				handler.sendEmptyMessage(message);
			} else {
				LogUtil.add("no policy is active!!!!");
			}
		} else if(message == Constants.MSG_REQUEST_FAIL
				|| message == Constants.MSG_STATIC_POLICY
				|| message == Constants.MSG_STATIC_POLICY_APPLY
				|| message == Constants.MSG_CLEAN_UP
				|| message == Constants.MSG_REQUEST_NO_ACTIVE_POLICY
				|| message == Constants.MSG_APP_NET) {
			handler.sendEmptyMessage(message);
		} else if(message == Constants.MSG_REQUEST_SUCCESS) {
			if(activePolicy != null && policyList != null && !policyList.isEmpty())
				handler.sendEmptyMessage(message);
		}
	}
	
	/**
	 * request to policy server to get the policy
	 */
	public void requestPolicyList() {
		AppApplication.connecting_status = HomeActivity.STATUS_DOWNLOADING_XML;
		context.sendBroadcast(new Intent(HomeActivity.ACTION));
		requestPolicyPacket1();
		
		if(AppApplication.requestServer) {
			return;
		}
		//for test
		InputStream stream = FileUtil.readStreamFormSDCard(context, "policy_test.xml");
		if(stream == null) {
			notifyToController(Constants.MSG_REQUEST_FAIL);
		} else {
			OMAResponse reponse = new OMAResponse();
			reponse.content = stream;
			completed(reponse);
		}
	}
	
	
	/**
	 * get the local static policy instead of policy from policy server
	 * @return
	 */
	public PolicyModel getLocalPolicy() {
//		cleanUp();
		
		PolicyModel policy = new PolicyModel();
		List<NetworkModel> netList = new ArrayList<NetworkModel>();
		netList.add(new WifiModel(NetworkModel.NAME_WLAN, null, AppApplication.isWifiFirst ? 0 : 1));
		netList.add(new _3GModel(NetworkModel.NAME_3G, AppApplication.isWifiFirst ? 1 : 0));
		policy.setAccessNetworkList(netList);
		CommonUtil.handleDefaultSSID(policy);
		policy.resource_from = PolicyModel.TYPE_FORM_STATIC;
		return policy;
	}
	
	/**
	 * get the policy from database, if get failed, return static policy
	 * @return {@link PolicyModel}
	 */
	public PolicyModel getPreviousPolicy() {
		cleanUp();
		policyList.clear();
		policyList.addAll(searchFromDB());
		activePolicy = pickActivePolicy(policyList);
		if(activePolicy == null) {
			return getLocalPolicy();
		} else {
			return getActivePolicy();
		}
	}
	
	/**
	 * get policy list from database via current location
	 * @return {@link List}
	 */
	public List<PolicyModel> searchFromDB() {
		List<PolicyModel> list = new ArrayList<PolicyModel>();
		int cellid = CommonUtil.getCellId(context);
		int plmn = CommonUtil.getPlmn(context);
		int lac = CommonUtil.getLac(context);
		Location loc = LocationUtil.getInstance(app).getLocation();
		double latitude;
		double longitude;
		if(loc != null) {
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();
		} else {
			latitude = -1;
			longitude = -1;
		}
		
		Set<PolicyModel> sets = null;
		try {
			mDbAdapter.openDatabase();
			sets = mDbAdapter.filterByLocation(plmn, lac, cellid, latitude, longitude);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			mDbAdapter.close();
		}
		
		if(sets == null) {
			return null;
		}
		
		for (PolicyModel policy : sets) {
			policy.resource_from = PolicyModel.TYPE_FORM_PREVIOUS;
			list.add(policy);
		}

		Collections.sort(list);
		return list;
	}
	
	
	/**
	 * filter the policy list by location and sort by {@link TimeAndDate#getStartDateTime()}
	 * @param policyList
	 */
	private void filterByLocation(List<PolicyModel> policyList) {
		List<PolicyModel> validList = new ArrayList<PolicyModel>();
		int cellId = CommonUtil.getCellId(context);
		int plmn = CommonUtil.getPlmn(context);
		int lac = CommonUtil.getLac(context);
		final Location location = LocationUtil.getInstance(context).getLocation();
		final double latitude = location == null ?  -1 : location.getLatitude();
		final double longitude = location == null ?  -1 : location.getLongitude();
		Log.i(TAG, "plmn=" + plmn + " || lac=" + lac + 
				" || cellId=" + cellId + " || latitude=" + latitude + " || longitude=" + longitude);
		LogUtil.add("check by location now");
		LogUtil.add("plmn=" + plmn + " || lac=" + lac + 
				" || cellId=" + cellId + " || latitude=" + latitude + " || longitude=" + longitude);
		
		
		for (PolicyModel policyModel : policyList) {
			List<NetworkModel> netList = policyModel.getAccessNetworkList();
			if(netList == null || netList.isEmpty()) {
				continue;
			}
			read_data: 
			{
				//check 3gpp location
				List<_3GModel> _3gList = policyModel.getLocation3GList();
				for (_3GModel _3g : _3gList) {
					final long cellIdT = _3g.getCid();
					final int plmnT = _3g.getPlmn();
					final long lacT = _3g.getLac();
					if(cellIdT == -1) cellId = -1;
					if(plmnT == -1) plmn = -1;
					if(lacT == -1) lac = -1;
					if(cellIdT == cellId && plmnT == plmn && lacT == lac) {
						Log.i(TAG, "filterByLocation-------->3GPP matched");
						LogUtil.add("3gpp location matched");
						validList.add(policyModel);
						break read_data;
					}
				}
				
				//check geo location
				List<GeoLocation> locationList = policyModel.getGeoLocationList();
				for (GeoLocation geoLocation : locationList) {
					boolean inrange = LocationUtil.isInRange(geoLocation.getLongtitude(),
							geoLocation.getLatitude(), longitude, latitude, geoLocation.getRadius());
					if(inrange) {
						Log.i(TAG, "filterByLocation-------->GEO matched");
						LogUtil.add("geo location matched");
						policyModel.pickByCID = false;
						validList.add(policyModel);
						break read_data;
					}
				}
			}
		}
		policyList.clear();
		for (PolicyModel policyModel : validList) {
			policyList.addAll(CommonUtil.splitPolicy(policyModel));
		}
		
		Collections.sort(policyList);
	}
	
	/**
	 * get the active policy
	 * <p>get the policies in the valid time and sort them by priority, pick the first one and return
	 * If the picked policy has not highest priority, start a {@link PolicyPickTask}
	 * @param newList
	 * @return {@link PolicyModel}
	 */
	private PolicyModel pickActivePolicy(final List<PolicyModel> newList) {
		if(newList == null || newList.isEmpty()) {
			Log.w(TAG, "pickActivePolicy--->newList is null/empty");
			LogUtil.add("no matched filter by location ");
			return null;
		}
		
		List<PolicyModel> list = new ArrayList<PolicyModel>();
		pickPolicyHandler.removeCallbacks(policyPickTask);
		timeHandler.removeCallbacks(timeExpireTask);
		
		final int size = newList.size();
		int[] priorityArray = new int[size];
		final long nowTime = TimeUtil.getNowFullTime();
		for (int i = 0; i < size; i++) {
			final PolicyModel policy = newList.get(i);
			final TimeAndDate timeDate = policy.getTimeList().get(0);
			try {
				TimeUtil.checkTimeData(timeDate);
			} catch (Throwable e) {
				Log.w(TAG, "index:" + i + "/msg:" + e.getMessage());
				LogUtil.add("Time And Day data error!!!!");
				try {
					mDbAdapter.openDatabase();
					mDbAdapter.deleteErrorPolicy(policy);
				} catch (Exception ex) {
					ex.printStackTrace();
				}finally {
					mDbAdapter.close();
				}
				continue;
			}

			priorityArray[i] = policy.getRulePriority();
			
			if(nowTime >= timeDate.getStartDateTime()
					&& nowTime < timeDate.getEndDateTime()) {
				list.add(policy);
			}
		}
		
		if(list.isEmpty()) {
			// find the closest start time policy
			int index = -1;
			final int size2 = newList.size();
			for (int j = 0; j < size2; j++) {
				final PolicyModel policy = newList.get(j);
				final TimeAndDate tad = policy.getTimeList().get(0);
				final long start = tad.getStartDateTime();
				final long endTime = tad.getEndDateTime();
				if(start > nowTime) {
					index = j;
					break;
				}
			}
			
			if(index > -1) {
				PolicyModel futurePolicy = newList.get(index);
				long minStartTime = futurePolicy.getTimeList().get(0).getStartDateTime();
				if(minStartTime > nowTime) {
					timeHandler.removeCallbacks(timeArriveTask);
					futurePolicy.startTimeDistance = TimeUtil
							.calculateBySecondsCompareNow(minStartTime);
					LogUtil.add("all time not arrived, " + futurePolicy.startTimeDistance/1000 + "s remain to start");
					timeHandler.postDelayed(timeArriveTask, futurePolicy.startTimeDistance);
				} 
			} else {
				LogUtil.add("no valid time!!!!");
			}
			return null;
		}
		
		// sort by priority
		Collections.sort(list, new Comparator<PolicyModel>(){
			@Override
			public int compare(PolicyModel object1, PolicyModel object2) {
				return object1.getRulePriority().compareTo(object2.getRulePriority());
			}
		});
		
		PolicyModel finalPolicy = list.get(0);
		final long endTimeDate = finalPolicy.getTimeList().get(0).getEndDateTime();
		finalPolicy.endTimeDistance =  TimeUtil.calculateBySecondsCompareNow(endTimeDate);
		timeHandler.removeCallbacks(timeExpireTask);
		timeHandler.postDelayed(timeExpireTask, finalPolicy.endTimeDistance);
		
		LogUtil.add("picked up policy:" + finalPolicy.toString());
		LogUtil.add("the policy's will expired after " + finalPolicy.endTimeDistance / 1000 + "s");
		
		// if the finalPolicy has not highest priority, start a time task to track
		Arrays.sort(priorityArray);
		if(finalPolicy.getRulePriority() > priorityArray[0]) {
			pickPolicyHandler.postDelayed(policyPickTask, Constants.POLICY_PICK_TIME_INTERVAL);
			LogUtil.add("there are higher priority policy exsit, start the monitor");
		}
		
		if(!finalPolicy.pickByCID) {
			locationHander.removeCallbacks(policyLocationTask);
			locationHander.postDelayed(policyLocationTask, Constants.LOCATION_CHECK_INTEVAL);
		}
		
		return finalPolicy;
	}
	
	/**
	 * register cell s listener
	 */
	private void registerCellIdListener() {
		if (telManager == null) {
			telManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
		}
		if (cidListener == null) {
			cidListener = new CIDPhoneStateListener();
		}
		telManager.listen(cidListener, PhoneStateListener.LISTEN_CELL_LOCATION);
	}

	/**
	 * unregister cell s listener
	 */
	private void unregisterCellIdListener() {
		if (telManager != null && cidListener != null) {
			telManager.listen(cidListener, PhoneStateListener.LISTEN_NONE);
		}
	}
	
	/**
	 * clean up
	 */
	private void cleanUp() {
		Log.i(TAG, "cleanUpTask");
		if(timeExpireTask != null) {
			timeHandler.removeCallbacks(timeExpireTask);
		}
		
		if(timeArriveTask != null) {
			timeHandler.removeCallbacks(timeArriveTask);
		}
		
		if(policyPickTask != null) {
			pickPolicyHandler.removeCallbacks(policyPickTask);
		}
		if(policyLocationTask != null) {
			locationHander.removeCallbacks(policyLocationTask);
		}
		
		policyList.clear();
		serverPolicyList.clear();
		activePolicy = null;
	}
	
	/**
	 * request to server and send packet1
	 */
	public void requestPolicyPacket1() {
		manager.start();
		unique = System.currentTimeMillis();
		synclP1 = null;
		synclP3 = null;
		policyXml = null;
		discoXml = null;
		wlanInfo.clear();
		
		ProtocolFormatter formatter = manager.getFormatter();
		if(formatter != null) {
			String result = formatter.buildPacket1Xml();
			synclP1 = manager.initalPacket1Data();
			result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
					.replaceAll("<?xml version='1.0' ?>", "");
			OMARequest request = new OMARequest(Constants.POLICY_SERVER_URL);
			request.postParams = result;
			
			Hashtable<String, String> requestProperties = new Hashtable<String, String>(2);
			requestProperties.put("Content-Length",
					String.valueOf(result.getBytes().length));
			requestProperties.put("Content-Type", "text/xml; charset=utf-8");
			request.requestProperties = requestProperties;
			request.uid = unique + 0x01;
			Log.i(TAG, "p1------uid:" + request.uid);
			if(AppApplication.requestServer) {
				mHttpHandler.sendRequest(request);
			}
		}
	}
	
	/**
	 * request to server and send packet3
	 * @param sync
	 */
	public void requestPolicyPacket3(SyncML sync) {
		ProtocolFormatter formatter = manager.getFormatter();
		if(formatter != null) {
			String result = formatter.buildPacket3Xml(sync);
			synclP3 = manager.initalPacket3Data(sync, false);
			result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
					.replaceAll("<?xml version='1.0'?>", "");
			OMARequest request = new OMARequest(Constants.POLICY_SERVER_URL);
			request.postParams = result;
			
			Hashtable<String, String> requestProperties = new Hashtable<String, String>(2);
			requestProperties.put("Content-Length",
					String.valueOf(result.getBytes().length));
			requestProperties.put("Content-Type", "text/xml; charset=utf-8");
			request.requestProperties = requestProperties;
			request.uid = unique + 0x02;
			Log.i(TAG, "p3------uid:" + request.uid);
			if(AppApplication.requestServer) {
				mHttpHandler.sendRequest(request);
			}
		}
	}
	
	@Override
	public void completed(CMResponse resp) {
		OMAResponse response = (OMAResponse) resp;
		InputStream results = response.getContent();
		int which = (int) (response.getUid() - unique);
		if(!AppApplication.requestServer) {
			which = 1;
		}
		parser = new ProtocolParser(null, results);
		switch (which) {
		case 0x01:
			try {
				SyncML sml = parser.parsePacket2();
				if(!AppApplication.requestServer ||
						ProtocolUtil.checkSyncMLValid(context, synclP1, sml)) {
					requestPolicyPacket3(sml);
					
					parseSyncML(sml);
					
					if(discoXml != null) {
						Map<String, String> temp = new HashMap<String, String>();
						for (Entry<String, String> wlanEntity : wlanInfo.entrySet()) {
							WlanInfo wlanInfo = CommonUtil.parseWlan(wlanEntity.getValue());
							temp.put(wlanEntity.getKey(), wlanInfo.ssid);
						}
						List<HotspotModel> list = CommonUtil.parseHotspot(discoXml);
						List<HotspotModel> validList = new ArrayList<HotspotModel>();// the hotspots list in the valid location
						int plmn = CommonUtil.getPlmn(context);
						int lac = CommonUtil.getLac(context);
						int cid = CommonUtil.getCellId(context);
						for (HotspotModel hotspotModel : list) {
							String ssid = null;
							if(false) {
								ssid = hotspotModel.wlanSsid;
							} else {
								ssid = temp.get(hotspotModel.getAccessNetworkInformationRef());
							}
							if(ssid != null) {
								hotspotModel.setSsid(ssid);
							}
//							if(!AppApplication.requestServer) {
								if(hotspotModel.getG3_plmn() == -1) plmn = -1;
								if(hotspotModel.getG3_lac() == -1) lac = -1;
								if(hotspotModel.getG3_cid() == -1) cid = -1;
//							}
							if (plmn == hotspotModel.getG3_plmn()
									&& lac == hotspotModel.getG3_lac()
									&& cid == hotspotModel.getG3_cid()) {
								validList.add(hotspotModel);
							}
						}
						
						int size = validList.size();
						AppApplication.hotspot_number = size;
						
						if(list.size() > 0) {
							try {
								mDbAdapter.openDatabase();
								mDbAdapter.addHotspot(list);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								mDbAdapter.close();
							}
						}
					}
					
					if(policyXml != null) {
						serverPolicyList = CommonUtil.parseXml(policyXml);
						if(serverPolicyList.isEmpty()) {
							notifyToController(Constants.MSG_REQUEST_FAIL);
							return;
						}
						try {
							mDbAdapter.openDatabase();
							mDbAdapter.addTime2DB(serverPolicyList);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							mDbAdapter.close();
						}
						
						for (PolicyModel policy : serverPolicyList) {
							policy.resource_from = PolicyModel.TYPE_FORM_NETWORK;
						}
						policyList.clear();
						policyList.addAll(serverPolicyList);
						filterByLocation(policyList);
						activePolicy = pickActivePolicy(policyList);
						if(activePolicy == null) {
							notifyToController(Constants.MSG_REQUEST_NO_ACTIVE_POLICY);
						} else {
							notifyToController(Constants.MSG_REQUEST_SUCCESS);
						}
					} else {
						notifyToController(Constants.MSG_REQUEST_FAIL);
					}
				} else {
					notifyToController(Constants.MSG_REQUEST_FAIL);
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				notifyToController(Constants.MSG_REQUEST_FAIL);
			} catch (IOException e) {
				e.printStackTrace();
				notifyToController(Constants.MSG_REQUEST_FAIL);
			} catch (ProtocolParserException e) {
				e.printStackTrace();
				notifyToController(Constants.MSG_REQUEST_FAIL);
			} catch (Exception e) {
				e.printStackTrace();
				notifyToController(Constants.MSG_REQUEST_FAIL);
			} finally {
				
			}
			
			break;
		case 0x02:
			try {
				SyncML sml = parser.parsePacket4();
				if(!ProtocolUtil.checkFinalSyncMLValid(context, synclP3, sml)) {
					Log.e(TAG, "packet4 error!!!!");
				}
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		
	}

	@Override
	public void exception(Exception e, CMRequest request) {
		Log.e(TAG, "HTTP connection Exception:" + e.getMessage());
		LogUtil.add("HTTP connection Exception:" + e.getMessage());
		int which = (int) (request.getUid() - unique);
		if(which == 1) {
			notifyToController(Constants.MSG_REQUEST_FAIL);
		}
	}

	@Override
	public boolean acceptResponse(CMRequest request) {
		// TODO Auto-generated method stub
		return true;
	}
	
	private void parseSyncML(SyncML sml) {
		Vector<ItemizedCommand> commands = sml.getSyncBody().getCommands();
		for (ItemizedCommand itemizedCommand : commands) {
			if(itemizedCommand.getName().equalsIgnoreCase("replace")) {
				Replace replace = (Replace) itemizedCommand;
				String type = replace.getMeta().getType();
				// -------------------------------for Huawei Server Start-----------------------
				if(replace.getItem().getTarget().getLocURI()
						.equalsIgnoreCase(ProtocolManager.URL_DISCOVERY)) {
					discoXml = replace.getItem().getData().getData();
				} else if(type != null && type.equalsIgnoreCase(ProtocolManager.TYPE_WLAN)) {
					Vector<Item> items = replace.getItems();
					for (Item item : items) {
						wlanInfo.put(item.getTarget().getLocURI(), item.getData().getData());
					}
				} else {
					if(replace.getItem().getTarget().getLocURI()
							.equalsIgnoreCase(ProtocolManager.URL_POLICY)) {
						policyXml = replace.getItem().getData().getData();
					}
				}
//				if(type != null && type.equalsIgnoreCase(ProtocolManager.TYPE_DISCOVERY)) {
//					discoXml = replace.getItem().getData().getData();
//				} else if(type != null && type.equalsIgnoreCase(ProtocolManager.TYPE_WLAN)) {
//					Vector<Item> items = replace.getItems();
//					for (Item item : items) {
//						wlanInfo.put(item.getTarget().getLocURI(), item.getData().getData());
//					}
//				} else {
//					if(replace.getItem().getTarget().getLocURI()
//							.equalsIgnoreCase(ProtocolManager.URL_POLICY)) {
//						policyXml = replace.getItem().getData().getData();
//					}
//				}
				// -------------------------------for Huawei Server End-------------------------
			}
		}
	}
	
	/**
	 * A listener class for monitor cell id change
	 *
	 */
	private class CIDPhoneStateListener extends PhoneStateListener{

		@Override
		public void onCellLocationChanged(CellLocation location) {
			int newId = 0;
			if (location instanceof GsmCellLocation) {
				newId = ((GsmCellLocation) location).getCid();
			} else if(location instanceof CdmaCellLocation) {
				newId = ((CdmaCellLocation) location).getBaseStationId();
			}
			
			if(cellId != newId) {
				Log.i(TAG, "cell id changed!!!");
				LogUtil.add("cell id changed!!!");
				cellId = newId;
				requestHandler.removeCallbacks(intervalTask);
				requestHandler.postDelayed(intervalTask, isStarting ? 100 : Constants.INTERVAL_TIME);
			}
			super.onCellLocationChanged(location);
		}
	}
	
	
	/**
	 * interval task for stable time, to avoid cell id change too often in a short time
	 *
	 */
	private class IntervalTask implements Runnable {
		@Override
		public void run() {
			isStarting = false;
			
			NotifyUiUtil.cleanPolicyNotification(context);

			AppApplication.cellIdChangeTimes ++;
			cleanUp();
			if(isDynamic) {
				requestPolicyList();
			} else {
				if(AppApplication.cellIdChangeTimes > 1 && AppApplication.applyStaticPolicy > 0) {// is not the first time
					notifyToController(Constants.MSG_STATIC_POLICY_APPLY);
				} else {
					notifyToController(Constants.MSG_STATIC_POLICY);
				}
			}
		}
	}
	
	/**
	 * a task to monitor higher policy than current policy
	 *
	 */
	private class PolicyPickTask implements Runnable {

		@Override
		public void run() {
			Log.i(TAG, "policy pick start!!!!!!!");
			PolicyModel newPolicy = pickActivePolicy(policyList);
			if(activePolicy != null && newPolicy != null 
					&& activePolicy.getRulePriority() > newPolicy.getRulePriority()) {
				Log.d(TAG, "higher prio policy: " + activePolicy.toString());
				LogUtil.add("higher priority policy time arrived, Policy: " + newPolicy.toString());
				activePolicy = newPolicy;
				// notify controller to apply new policy
				notifyToController(Constants.MSG_HAS_NEW_POLICY);
			} else {
				pickPolicyHandler.removeCallbacks(policyPickTask);
				pickPolicyHandler.postDelayed(policyPickTask, Constants.POLICY_PICK_TIME_INTERVAL);
			}
		}
		
	}
	
	/**
	 * a task to monitor policy's time
	 *
	 */
	private class PolicyTimeTask implements Runnable {
		static final int EXPIRED = 1;
		static final int ARRIVED = 2;
		int what;
		PolicyTimeTask(int i) {
			what = i;
		}
		@Override
		public void run() {
			Log.i(TAG, "policy expired or arrived");
			if(what == EXPIRED) {
				if(policyList != null) {
					LogUtil.add("time expired, " + TimeUtil.getNowFullTime());
					activePolicy = pickActivePolicy(policyList);
					if(activePolicy == null) {
						notifyToController(Constants.MSG_STATIC_POLICY);
					} else {
						notifyToController(Constants.MSG_HAS_NEW_POLICY);
					}
				}
			} else if(what == ARRIVED) {
				if(policyList != null) {
					LogUtil.add("time arrived, " + TimeUtil.getNowFullTime());
					activePolicy = pickActivePolicy(policyList);
					if(activePolicy == null) {
						notifyToController(Constants.MSG_STATIC_POLICY);
					} else {
						notifyToController(Constants.MSG_HAS_NEW_POLICY);
					}
				}
			}
		}
	}
	
	/**
	 * a task to monitor geo location
	 *
	 */
	private class PolicyLocationTask implements Runnable {

		@Override
		public void run() {
			if(activePolicy == null || policyList.isEmpty()) {
				return;
			}
			
			final Location location = LocationUtil.getInstance(context).getLocation();
			if(location == null) {
				return;
			}
			final double latitude = location.getLatitude();
			final double longitude = location.getLongitude();
			Log.i(TAG, "PolicyLocationTask>>>>>>>>>latitude=" + latitude + " || longitude=" + longitude);
			
			//check if in the range of network policy
			policyList.clear();
			policyList.addAll(serverPolicyList);
			int size = policyList.size();
			for (int i = 0; i < size; i++) {
				PolicyModel networkPolicy = policyList.get(i);
				List<GeoLocation> geoList = networkPolicy.getGeoLocationList();
				int geoCount = geoList.size();
				for (int j = 0; j < geoCount; j++) {
					GeoLocation geoLocation = geoList.get(j);
					boolean inrange = LocationUtil.isInRange(geoLocation.getLongtitude(),
							geoLocation.getLatitude(), longitude, latitude, geoLocation.getRadius());
					Log.d(TAG, "net policy >>>>>>>> i=" + i + "/j=" + j + "/inrange=" + inrange);
					if(inrange) {
						LogUtil.add("net policy is in range: longitude=" + longitude + "&latitude=" + latitude);
						
						if (activePolicy.resource_from == PolicyModel.TYPE_FORM_PREVIOUS) {
							LogUtil.add(">>>>>>switch db policy to net policy");
							filterByLocation(policyList);
							activePolicy = pickActivePolicy(policyList);
							if (activePolicy == null) {
								notifyToController(Constants.MSG_STATIC_POLICY);
							} else {
								notifyToController(Constants.MSG_HAS_NEW_POLICY);
							}
						}
						
						//restart the time task, make it run as looper
						locationHander.removeCallbacks(policyLocationTask);
						locationHander.postDelayed(policyLocationTask, Constants.LOCATION_CHECK_INTEVAL);
						return;
					}
				}
			}
			
			
			//if GEO out of network policy's range
			//then check whether in previous data range
			policyList.clear();
			policyList.addAll(searchFromDB());
			size = policyList.size();
			for (int i = 0; i < size; i++) {
				PolicyModel networkPolicy = policyList.get(i);
				List<GeoLocation> geoList = networkPolicy.getGeoLocationList();
				int geoCount = geoList.size();
				for (int j = 0; j < geoCount; j++) {
					GeoLocation geoLocation = geoList.get(j);
					boolean inrange = LocationUtil.isInRange(geoLocation.getLongtitude(),
							geoLocation.getLatitude(), longitude, latitude, geoLocation.getRadius());
					Log.d(TAG, "db policy >>>>>> i=" + i + "/j=" + j + "/inrange=" + inrange);
						if(inrange) {
							LogUtil.add("db policy is in range: longitude=" + longitude + "&latitude=" + latitude);
							// if the current policy from net is invalid, change to
							// use policy in DB
							if (activePolicy.resource_from == PolicyModel.TYPE_FORM_NETWORK) {
								LogUtil.add(">>>>>>switch net policy to db policy");
								filterByLocation(policyList);
								activePolicy = pickActivePolicy(policyList);
								if (activePolicy == null) {
									notifyToController(Constants.MSG_STATIC_POLICY);
								} else {
									notifyToController(Constants.MSG_HAS_NEW_POLICY);
								}
							}
							// restart the time task, make it run as looper
							locationHander.removeCallbacks(policyLocationTask);
							locationHander.postDelayed(policyLocationTask, Constants.LOCATION_CHECK_INTEVAL);
							return;
						} else {
							if (activePolicy.resource_from == PolicyModel.TYPE_FORM_PREVIOUS) {
								LogUtil.add(">>>>>>db policy not valid any more");
								filterByLocation(policyList);
								activePolicy = pickActivePolicy(policyList);
								if (activePolicy == null) {
									notifyToController(Constants.MSG_STATIC_POLICY);
								} else {
									notifyToController(Constants.MSG_HAS_NEW_POLICY);
								}
								locationHander.removeCallbacks(policyLocationTask);
								locationHander.postDelayed(policyLocationTask, Constants.LOCATION_CHECK_INTEVAL);
								return;
							}
						}
					}
				}
			
			//if GEO out of database policy's range
			notifyToController(Constants.MSG_STATIC_POLICY);
		}
	}
	
	/**
	 * get config app list from DB in async task
	 */
	public void updateAppList() {
		new AsyncTask<Void, Void, List<AppModel>>() {
			@Override
			protected List<AppModel> doInBackground(Void... params) {
				List<AppModel> readList = new ArrayList<AppModel>();
				try {
					mDbAdapter.openDatabase();
					readList = mDbAdapter.readAppList();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mDbAdapter.close();
				}
				return readList;
			}

			@Override
			protected void onPostExecute(List<AppModel> result) {
				appList.clear();
				for (AppModel appModel : result) {
					appList.put(appModel.getPkgName(), appModel.getNetworkPri());
				}
				super.onPostExecute(result);
			}
		}.execute();
	}

	/**
	 * get top activity of task package name
	 */
	Handler threadHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningTaskInfo> list = mActivityManager.getRunningTasks(1);
				RunningTaskInfo taskInfo = list.get(0);
				String localCurrentPackageName = taskInfo.topActivity.getPackageName();
//				Log.v(TAG, "localCurrentPackageName------>" + localCurrentPackageName);
				if(!localCurrentPackageName.equals(currentPackageName)) {
					currentPackageName = localCurrentPackageName;
					notifyToController(Constants.MSG_APP_NET);
					Log.i("LogService", "packageName--->" + currentPackageName);
				}
			}
			super.handleMessage(msg);
		}
		
	};
	
	/**
	 * check the top activity of task in a loop
	 */
	class TopAppCheckThread extends Thread {
		boolean isRun;
		@Override
		public void run() {
			while (true) {
				if(isRun) {
					threadHandler.sendEmptyMessage(0);
				}
				try {
					Thread.sleep(Constants.TOP_APP_INTERVAL);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		
		public void startThread() {
			isRun = true;
		}
		
		public void stopThread() {
			isRun = false;
		}
	}
		
	
}
