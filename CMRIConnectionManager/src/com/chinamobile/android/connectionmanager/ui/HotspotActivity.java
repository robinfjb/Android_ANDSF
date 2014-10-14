package com.chinamobile.android.connectionmanager.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import com.baidu.mapapi.RouteOverlay;
import com.baidu.mapapi.TransitOverlay;
import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.controller.ServiceController;
import com.chinamobile.android.connectionmanager.database.DBAdpter;
import com.chinamobile.android.connectionmanager.model.HotspotModel;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.LocationUtil;
import com.chinamobile.android.connectionmanager.util.LogUtil;
import com.chinamobile.android.connectionmanager.util.WifiUtil;
import com.chinamobile.android.connectionmanager.util.WifiUtil.WifiRadioStats;

/**
 * hotspot page activity
 *
 */
public class HotspotActivity extends MapActivity {
	private static final String TAG = "HotspotActivity";
	public static final int ID_DIALOG_GPS = 1;
	public static final String ACTION_CONNECT_RESULT = "com.chinamobile.android.connectionmanager.ui.hotspot.connect";
	public static final String ACTION_CONNECT_BREAK = "com.chinamobile.android.connectionmanager.ui.hotspot.break";
	public static final String ACTION_ACCOUNT = "com.chinamobile.android.connectionmanager.ui.hotspot.account";
	private MapView mMapView = null;
	private MyloccationListener mLocationListener = null;
	private MyLocationOverlay mLocationOverlay = null;
	private GeoPoint currentGeo = null;
//	private Location locationGPS;
//	private Location locationNetwork;
//	private BMapManager mBMapMan = null;
//	private AppApplication app;
	private View mPopView = null;
//	private BubbleDialog popupView;
	private Drawable marker;
	private OverItemT overlayItemList;
	private Button mBtnDrive = null;
	private Button mBtnTransit = null;
	private Button mBtnWalk = null;
	private MKSearch mSearch = null;
	private MKSearchListener searchListener = null;
	private ItemizedOverlay routeOverlay;
	private RelativeLayout mLinearLayout;
	private DBAdpter mDb;
	private List<HotspotModel> list = new ArrayList<HotspotModel>();
	private Context context;
	private WifiModel lastConnectedSpot;
	private WifiScanReceiver scanReceiver;
	private WifiRadioReceiver radioReceiver;
	private ServiceReceiver serviceReceiver;
	private Handler routeTimeoutTimer;
	private Runnable routeTimeoutTask;
	private Handler geoTimeoutTimer;
	private Runnable geoTimeoutTask;
	private Handler scanTimeoutTimer;
	private Runnable scanTimeoutTask;
	private boolean isScanning;
	private boolean isOpenningRadio;
	public static boolean isConnecting;
	private Location currentLoc;
	private WifiManager wifiManager;
	private IntentFilter scan_filter;
	private IntentFilter service_filter;
	private String routeTargetName;
	private GeoPoint routeTargetGeo;
	/*private EditText editSt;
	private EditText editEn;*/
//	private ProgressDialog dialogConnecting;
	private ProgressDialog dialogRouting;
	private ProgressDialog dialogGettingGeo;
	private AlertDialog accountDailog;
	private int connectIndex = -1;
	private int popupIndex = -1;
	private long connect_uid = 0;
	private boolean needCloseWifi;
	public static int plmn;
	public static int lac;
	public static int cid;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hotspot);
		context = HotspotActivity.this;
		
		AppApplication.getBMapManager().start();
		super.initMapActivity(AppApplication.getBMapManager());

		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.setBuiltInZoomControls(true);

		mMapView.setDrawOverlayWhenZooming(true);
		MapController mMapController = mMapView.getController();
		mMapController.setZoom(17);

		mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
		
		BottomMenu bottom = (BottomMenu) findViewById(R.id.hotspot_menu_linearlayout);
		bottom.setWhichSelected(1);
		bottom.setStatusListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent statusIntent = new Intent(HotspotActivity.this,
						HomeActivity.class);
				statusIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(statusIntent);
			}
		});
		bottom.setAccountListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent setIntent = new Intent(HotspotActivity.this,
						AccountActivity.class);
				setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(setIntent);
			}
		});
		
		
		mLocationListener = new MyloccationListener();
		routeTimeoutTimer = new Handler();
		geoTimeoutTimer = new Handler();
		scanTimeoutTimer = new Handler();
		
		initalBubble();
		initalRoute();
		initRoutingDialog();
		initConnectingDialog();
		initGettingGeoDialog();
		
		scanReceiver = new WifiScanReceiver();
		radioReceiver = new WifiRadioReceiver();
		serviceReceiver = new ServiceReceiver();
		scan_filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		service_filter = new IntentFilter(ACTION_ACCOUNT);
		service_filter.addAction(ACTION_CONNECT_RESULT);
		service_filter.addAction(ACTION_CONNECT_BREAK);
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		context.registerReceiver(scanReceiver, scan_filter);
		context.registerReceiver(radioReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		context.registerReceiver(serviceReceiver, service_filter);
		plmn = lac = cid = -1;
	}

	/**
	 * inital routing dialog
	 */
	private void initRoutingDialog() {
		dialogRouting = new ProgressDialog(this);
		dialogRouting.setMessage(context.getResources().getString(
				R.string.map_routing_prompt));
		dialogRouting.setIndeterminate(true);
		dialogRouting.setCancelable(true);
	}
	
	/**
	 * inital connecting dialog
	 */
	private void initConnectingDialog() {
//		dialogConnecting = new ProgressDialog(this);
//		dialogConnecting.setIndeterminate(true);
//		dialogConnecting.setCancelable(true);
	}
	
	/**
	 * inital position search dialog
	 */
	private void initGettingGeoDialog() {
		dialogGettingGeo = new ProgressDialog(this);
		dialogGettingGeo.setMessage(context.getResources().getString(
				R.string.map_geoing_prompt));
		dialogGettingGeo.setIndeterminate(true);
		dialogGettingGeo.setCancelable(true);
	}
	
	/**
	 * inital bubble dialog
	 */
	private void initalBubble() {
		marker = getResources().getDrawable(R.drawable.pin32);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		
		mPopView = super.getLayoutInflater().inflate(R.layout.map_popup, null);
		mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		mPopView.setVisibility(View.GONE);
	}
	
	/**
	 * inital route dialog
	 */
	private void initalRoute() {
		searchListener = new MKSearchListener() {
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				Log.i(TAG, "onGetDrivingRouteResult------------");
				if(routeTimeoutTask != null) {
					routeTimeoutTimer.removeCallbacks(routeTimeoutTask);
				} 
				if(dialogRouting != null) {
					dialogRouting.dismiss();
				}
				if (error != 0 || res == null) {
					Toast.makeText(HotspotActivity.this, 
							getResources().getString(R.string.route_wrong),
							Toast.LENGTH_SHORT).show();
					Log.w(TAG, "error: " + error);
					return;
				}
				
				List<Overlay> list = mMapView.getOverlays();
				if(list.contains(routeOverlay)) {
					list.remove(routeOverlay);
				}
				
				routeOverlay = new RouteOverlay(
						HotspotActivity.this, mMapView);
				((RouteOverlay) routeOverlay).setData(res.getPlan(0).getRoute(0));
				mMapView.getOverlays().add(routeOverlay);
				
				mMapView.invalidate();
				mMapView.getController().animateTo(res.getStart().pt);
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if(routeTimeoutTask != null) {
					routeTimeoutTimer.removeCallbacks(routeTimeoutTask);
				} 
				if(dialogRouting != null) {
					dialogRouting.dismiss();
				}
				Log.i(TAG, "onGetTransitRouteResult------------");
				if (error != 0 || res == null) {
					Toast.makeText(HotspotActivity.this, 
							getResources().getString(R.string.route_wrong),
							Toast.LENGTH_SHORT).show();
					Log.w(TAG, "error: " + error);
					return;
				}
				
				List<Overlay> list = mMapView.getOverlays();
				if(list.contains(routeOverlay)) {
					list.remove(routeOverlay);
				}
				
				routeOverlay = new TransitOverlay(
						HotspotActivity.this, mMapView);
				((TransitOverlay) routeOverlay).setData(res.getPlan(0));
				mMapView.getOverlays().add(routeOverlay);
				mMapView.invalidate();

				mMapView.getController().animateTo(res.getStart().pt);
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if(routeTimeoutTask != null) {
					routeTimeoutTimer.removeCallbacks(routeTimeoutTask);
				} 
				if(dialogRouting != null) {
					dialogRouting.dismiss();
				}
				Log.i(TAG, "onGetWalkingRouteResult------------");
				if (error != 0 || res == null) {
					Toast.makeText(HotspotActivity.this, 
							getResources().getString(R.string.route_wrong),
							Toast.LENGTH_SHORT).show();
					Log.w(TAG, "error: " + error);
					return;
				}
				
				List<Overlay> list = mMapView.getOverlays();
				if(list.contains(routeOverlay)) {
					list.remove(routeOverlay);
				}
//				
				routeOverlay = new RouteOverlay(
						HotspotActivity.this, mMapView);
				((RouteOverlay) routeOverlay).setData(res.getPlan(0).getRoute(0));
				mMapView.getOverlays().add(routeOverlay);
				mMapView.invalidate();
				
				mMapView.getController().animateTo(res.getStart().pt);
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
			}
			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
			}
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}
			@Override
			public void onGetRGCShareUrlResult(String arg0, int arg1) {
			}
			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			}
		};
		
		mSearch = new MKSearch();
		
		mLinearLayout = (RelativeLayout) findViewById(R.id.route_area);
		mLinearLayout.setVisibility(View.GONE);

		mBtnDrive = (Button) findViewById(R.id.route_drive);
		mBtnTransit = (Button) findViewById(R.id.route_transit);
		mBtnWalk = (Button) findViewById(R.id.route_walk);
//		editSt = (EditText) findViewById(R.id.start);
//		
//		editSt.setEnabled(false);
//		editSt.setFocusable(false);
//		editSt.setFocusableInTouchMode(false);
//		editEn = (EditText) findViewById(R.id.end);
//		editEn.setEnabled(false);
//		editEn.setFocusable(false);
//		editEn.setFocusableInTouchMode(false);
		ImageView closeBtn = (ImageView) findViewById(R.id.map_route_close);
		closeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setRouteAreaVisibility(false);
			}
		});
		
		OnClickListener clickListener = new OnClickListener() {
			public void onClick(View v) {
				mSearch.init(AppApplication.getBMapManager(), searchListener);
//				showDialog(DIALOG_ROUTING);
				if(dialogRouting == null) {
					initRoutingDialog();
				}
				dialogRouting.show();
//				String str_st = editSt.getText().toString();
//				String str_en = editEn.getText().toString();

				MKPlanNode stNode = new MKPlanNode();
				MKPlanNode enNode = new MKPlanNode();
				stNode.pt = currentGeo;
				enNode.pt = routeTargetGeo;
				/*if(str_st.equals(getResources().getString(R.string.route_search_my_location))) {
					
				} else {
					stNode.name = str_st;
				}
				
				if(str_en.equals(routeTargetName)) {
					
				} else {
					enNode.name = str_en;
				}*/

				if (mBtnDrive.equals(v)) {
					mSearch.drivingSearch(null, stNode, null, enNode);
				} else if (mBtnTransit.equals(v)) {
					mSearch.transitSearch(null, stNode, enNode);
				} else if (mBtnWalk.equals(v)) {
					mSearch.walkingSearch(null, stNode, null, enNode);
				}
				
				if(routeTimeoutTask != null) {
					routeTimeoutTimer.removeCallbacks(routeTimeoutTask);
				} 
				initTimeoutTask();
				routeTimeoutTimer.postDelayed(routeTimeoutTask, Constants.ROUTE_TIMEOUT);
			}
		};

		mBtnDrive.setOnClickListener(clickListener);
		mBtnTransit.setOnClickListener(clickListener);
		mBtnWalk.setOnClickListener(clickListener);
	}
	
	Handler timeoutHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				Toast.makeText(HotspotActivity.this,
						getResources().getString(R.string.route_wrong),
						Toast.LENGTH_SHORT).show();
				mSearch.init(AppApplication.getBMapManager(), null);
				if (dialogRouting != null) {
					dialogRouting.dismiss();
				}
			} else if(msg.what == 1) {
				Toast.makeText(HotspotActivity.this,
						getResources().getString(R.string.geo_wrong),
						Toast.LENGTH_SHORT).show();
				if (dialogGettingGeo != null) {
					dialogGettingGeo.dismiss();
				}
			}
			super.handleMessage(msg);
		}

	};
	
	/**
	 * inital routing timeout timer
	 */
	private void initTimeoutTask() {
		routeTimeoutTask = new Runnable() {
			@Override
			public void run() {
				timeoutHandler.sendEmptyMessage(0);
			}
		};
	}
	
	/**
	 * inital position search timer
	 */
	private void initGeoTimeoutTask() {
		geoTimeoutTask = new Runnable() {
			@Override
			public void run() {
				timeoutHandler.sendEmptyMessage(1);
			}
		};
	}
	
	Handler scanTimeoutHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				if(isScanning && !isConnecting) {
					isScanning = !isScanning;
					try {
						// there could be multi hotspot use same ssid, e.g. CMCC
						List<String> ssidList = new ArrayList<String>();
						List<Integer> strengthList = new ArrayList<Integer>();
						// wifi scan result is already sorted by signal strength
						List<ScanResult> scanResults = wifiManager.getScanResults();
						Log.d(TAG, "scanResults.size()=" + scanResults.size());
						for (ScanResult scanResult : scanResults) {
							ssidList.add(scanResult.SSID);
							strengthList.add(WifiManager.calculateSignalLevel(
									scanResult.level, 4));
						}
						// set the signal strength value
						Iterator iterator = list.iterator();
						while (iterator.hasNext()) {
							HotspotModel hotspot = (HotspotModel) iterator.next();
							String ssid = hotspot.getSsid();
							int index = ssidList.indexOf(ssid);
							if (index > -1) {
								int strength = strengthList.get(index);
								hotspot.signalStrength = strength;
								// remove the used data
								ssidList.remove(index);
								strengthList.remove(index);
							} else {
								hotspot.signalStrength = HotspotModel.STRENGTH_NONE;
							}
						}
						
						if(overlayItemList != null && popupIndex > -1 && !list.isEmpty()) {
							HotspotModel hotspotInfo = list.get(popupIndex);
							overlayItemList.updatePopupStrength(popupIndex);
							if(hotspotInfo.signalStrength < 0) {
								hotspotInfo.status = HotspotModel.CONNECT_FORBIDDEN;
								overlayItemList.updatePopupView(popupIndex);
							} else {
								hotspotInfo.status = HotspotModel.VALID;
								overlayItemList.updatePopupView(popupIndex);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			super.handleMessage(msg);
		}
		
	};
	
	/**
	 * inital WIFI scan timeout timer
	 */
	private void initScanTimeoutTask() {
		scanTimeoutTask = new Runnable() {
			@Override
			public void run() {
				scanTimeoutHandler.sendEmptyMessage(0);
			}
		};
//		scanTask = new Runnable() {
//			@Override
//			public void run() {
//				if(isConnecting) {
//					return;
//				}
//				
//				if(!WifiUtil.isWifiRadioOpen(context)) {
//					wifiManager.setWifiEnabled(true);
//					isOpenningRadio = true;
//					scanTimer.removeCallbacks(scanTask);
//					scanTimer.postDelayed(scanTask, Constants.SCAN_INTERVAL);
//					return;
//				}
//				
//				if(wifiManager.startScan()) {
//					isScanning = true;
//				}
//				
//				if(overlayItemList != null && popupIndex > -1 && !list.isEmpty()) {
//					try {
//						overlayItemList.updatePopupView(list.get(popupIndex));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					
//				}
//			}
//		};
	}
	
	/**
	 * set route view visible
	 * @param show
	 */
	private void setRouteAreaVisibility(boolean show) {
		if(show) {
			mLinearLayout.setVisibility(View.VISIBLE);
		} else {
			mLinearLayout.setVisibility(View.GONE);
			List<Overlay> list = mMapView.getOverlays();
			if(list.contains(routeOverlay)) {
				list.remove(routeOverlay);
			}
			mMapView.invalidate();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
		// mMenu = menu;

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

	@Override
	protected void onPause() {
		if (mLocationOverlay != null) {
			AppApplication.getBMapManager().getLocationManager().removeUpdates(mLocationListener);
			mLocationOverlay.disableMyLocation();
			mLocationOverlay.disableCompass();
			AppApplication.getBMapManager().stop();
		}
		
		if(!ServiceController.is_andsf_start) {
			LocationUtil.getInstance(context).stop();
		}
//		if(overItem != null) {
//			mMapView.getOverlays().remove(overItem);
//		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mLocationOverlay != null) {
			AppApplication.getBMapManager().stop();
			AppApplication.getBMapManager().getLocationManager().requestLocationUpdates(
					mLocationListener);
			AppApplication.getBMapManager().getLocationManager().setNotifyInternal(Constants.MAX_LOCTIONCHANGE_INTERVAL,
					Constants.MIN_LOCTIONCHANGE_INTERVAL);
			mLocationOverlay.enableMyLocation();
			mLocationOverlay.enableCompass();
			AppApplication.getBMapManager().start();
			if(dialogGettingGeo == null) {
				initGettingGeoDialog();
			}
			dialogGettingGeo.show();
			if(geoTimeoutTask != null) {
				geoTimeoutTimer.removeCallbacks(geoTimeoutTask);
			}
			initGeoTimeoutTask();
			geoTimeoutTimer.postDelayed(geoTimeoutTask, Constants.GEO_TIMEOUT);
		}
		
		if(!ServiceController.is_andsf_start) {
			LocationUtil.getInstance(context).start();
		}
//		LocationUtil.checkLocationEnable(this);
		
		
		/*if(!WifiUtil.isWifiRadioOpen(context)) {
			wifiManager.setWifiEnabled(true);
			isOpenningRadio = true;
			scanTimer.removeCallbacks(scanTask);
			scanTimer.postDelayed(scanTask, Constants.SCAN_INTERVAL);
		} else {
			scanTimer.removeCallbacks(scanTask);
			scanTimer.post(scanTask);
		}*/
//		plmn = lac = cid = -1;
		
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		AppApplication.getBMapManager().stop();
		//close the wifi radio if needed
		if(needCloseWifi) {
			wifiManager.setWifiEnabled(false);
		}
		if(scanReceiver != null) {
			try {
				unregisterReceiver(scanReceiver);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(radioReceiver != null) {
			try {
				unregisterReceiver(radioReceiver);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if(serviceReceiver != null) {
			try {
				unregisterReceiver(serviceReceiver);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		//reset the status on status page if account has not been finished yet
//		if(isConnecting && connectIndex > -1 && !list.isEmpty()) {
//			HotspotModel hotspotInfo = list.get(connectIndex);
//			if((hotspotInfo.getSsid().equalsIgnoreCase(Constants.CMCC) && AppApplication.isCMCCAccountEmpty())
//					|| (hotspotInfo.getSsid().equalsIgnoreCase(Constants.CMCC_AUTO) && AppApplication.isCMCCAUTOAccountEmpty())) {
//				sendBroadcast(new Intent(HomeActivity.ACTION));
//				AppApplication.current_network = null;
//				AppApplication.connecting_status = HomeActivity.STATUS_NO_CONNECTION;
//			}
//		}
		super.onDestroy();
	}

	
	@Override
	protected void onStop() {
		if(dialogRouting != null && dialogRouting.isShowing()) {
			dialogRouting.dismiss();
		}
		if(geoTimeoutTask != null) {
			geoTimeoutTimer.removeCallbacks(geoTimeoutTask);
		}
		super.onStop();
	}

	/*@Override
	public void onBackPressed() {
		if(isFromNotification) {
			Intent statusIntent = new Intent(HotspotActivity.this,
					HomeActivity.class);
			statusIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(statusIntent);
		}
		super.onBackPressed();
	}*/
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_BACK:
//			if(accountDailog != null && accountDailog.isShowing()) {
//				accountDailog.dismiss();
//				Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
//	        	sendIntent.putExtra("account_complete", false);
//	        	sendIntent.putExtra("is_hotspot", true);
//	        	sendBroadcast(sendIntent);
//	        	
//	        	if(dialogConnecting != null) {
//					dialogConnecting.dismiss();
//				}
//	        	return true;
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 2) {
			if(!AppApplication.isCMCCAUTOAccountEmpty()){
				Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
				sendIntent.putExtra("account_complete", true);
				sendIntent.putExtra("is_hotspot", true);
				sendBroadcast(sendIntent);

				if(overlayItemList != null && connectIndex > -1 && !list.isEmpty()) {
					HotspotModel hotspotInfo = list.get(connectIndex);
					hotspotInfo.status = HotspotModel.CONNECTING;
					overlayItemList.updatePopupView(popupIndex);
					overlayItemList.updatePopupStrength(popupIndex);
				}
			} else {
				Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
				sendIntent.putExtra("account_complete", false);
				sendIntent.putExtra("is_hotspot", true);
				sendIntent.putExtra("connect_id", connect_uid);
				sendBroadcast(sendIntent);
//				if(dialogConnecting != null) {
//					dialogConnecting.dismiss();
//				}
			}
		} else if(requestCode == 1) {
			if(!AppApplication.isCMCCAccountEmpty()){
				Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
				sendIntent.putExtra("account_complete", true);
				sendIntent.putExtra("is_hotspot", true);
				sendBroadcast(sendIntent);
				
				if(overlayItemList != null && connectIndex > -1 && !list.isEmpty()) {
					HotspotModel hotspotInfo = list.get(connectIndex);
					hotspotInfo.status = HotspotModel.CONNECTING;
					overlayItemList.updatePopupView(popupIndex);
					overlayItemList.updatePopupStrength(popupIndex);
				}
				
			} else {
				Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
				sendIntent.putExtra("account_complete", false);
				sendIntent.putExtra("is_hotspot", true);
				sendIntent.putExtra("connect_id", connect_uid);
				sendBroadcast(sendIntent);
			}
		}
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		SharedPreferences sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("andsf_running", ServiceController.is_andsf_start);
		editor.commit();
		super.onSaveInstanceState(outState);
	}

	/**
	 * WIFI scan complete receiver
	 *
	 */
	private class WifiScanReceiver extends BroadcastReceiver {
		final String ACTION = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
		@Override
		public void onReceive(Context context, Intent intent) {
			if(isScanning && ACTION.equals(intent.getAction()) && !isConnecting) {
				isScanning = !isScanning;
				try {
					setScanedStrength();
					if(overlayItemList != null && popupIndex > -1 && !list.isEmpty()) {
						HotspotModel hotspotInfo = list.get(popupIndex);
						overlayItemList.updatePopupStrength(popupIndex);
						if(hotspotInfo.signalStrength < 0) {
							hotspotInfo.status = HotspotModel.CONNECT_FORBIDDEN;
							overlayItemList.updatePopupView(popupIndex);
						} else {
							hotspotInfo.status = HotspotModel.VALID;
							overlayItemList.updatePopupView(popupIndex);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private void setScanedStrength() {
			// there could be multi hotspot use same ssid, e.g. CMCC
			List<String> ssidList = new ArrayList<String>();
			List<Integer> strengthList = new ArrayList<Integer>();
			// wifi scan result is already sorted by signal strength
			List<ScanResult> scanResults = wifiManager.getScanResults();
			Log.d(TAG, "scanResults.size()=" + scanResults.size());
			for (ScanResult scanResult : scanResults) {
				ssidList.add(scanResult.SSID);
				strengthList.add(WifiManager.calculateSignalLevel(
						scanResult.level, 4));
			}
			// set the signal strength value
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				HotspotModel hotspot = (HotspotModel) iterator.next();
				String ssid = hotspot.getSsid();
				int index = ssidList.indexOf(ssid);
				if (index > -1) {
					int strength = strengthList.get(index);
					hotspot.signalStrength = strength;
					// remove the used data
					ssidList.remove(index);
					strengthList.remove(index);
				} else {
					hotspot.signalStrength = HotspotModel.STRENGTH_NONE;
				}
			}
		}
	}
	
	/**
	 * WIFI radio action complete receiver
	 *
	 */
	private class WifiRadioReceiver extends BroadcastReceiver {
		private boolean isDisablingWifiRadio;
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				WifiRadioStats stats = WifiUtil.checkWifiRadioState(context);
				Log.d(TAG, "wifi radio state:" + stats.name());
				if(stats == WifiRadioStats.WIFI_RADIO_ENABLED && isOpenningRadio && !isConnecting) {
					Log.i(TAG, "wifi radio opened");
					isOpenningRadio = false;
					if(overlayItemList != null && popupIndex > -1 && !list.isEmpty()) {
						HotspotModel hotspotInfo = list.get(popupIndex);
						if(wifiManager.startScan()) {
							isScanning = true;
							hotspotInfo.status = HotspotModel.SCANNING;
							overlayItemList.updatePopupView(popupIndex);
							
							if(scanTimeoutTask != null) {
								scanTimeoutTimer.removeCallbacks(scanTimeoutTask);
							}
							initScanTimeoutTask();
							scanTimeoutTimer.postDelayed(scanTimeoutTask, Constants.TIMEOUT_SCAN);
						} else {
							wifiManager.setWifiEnabled(false);
							hotspotInfo.status = HotspotModel.UNKNOWN;
							overlayItemList.updatePopupView(popupIndex);
						}
					}
				} else if(stats == WifiRadioStats.WIFI_RADIO_DISABLED && isDisablingWifiRadio && !isConnecting) {
					mPopView.setVisibility(View.GONE);
				} else if(stats == WifiRadioStats.WIFI_RADIO_DISABLING && !isConnecting) {
					isDisablingWifiRadio = true;
				}
			}
		}
	}
	
	/**
	 * {@linkplain ServiceController} action receiver
	 *
	 */
	private class ServiceReceiver extends BroadcastReceiver {
		int type;
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			long uid = intent.getLongExtra("result_session_id", 0);
			if(action.equals(ACTION_ACCOUNT) && connect_uid == uid) {
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
				if(accountDailog == null) {
					accountDailog = new AlertDialog.Builder(context)
					.setTitle(R.string.account_warning)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							Intent mInt = new Intent(context, AccountActivity.class);
							if(type == 1) {
								mInt.putExtra("need_complete_cmcc", true);
								mInt.putExtra("is_hot_spot", true);
							} else if(type == 2) {
								mInt.putExtra("need_complete_cmcc_auto", true);
								mInt.putExtra("is_hot_spot", true);
							}
							startActivityForResult(mInt, type);
						}
					})
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
							sendIntent.putExtra("account_complete", false);
							sendIntent.putExtra("is_hotspot", true);
							sendBroadcast(sendIntent);
							
							if(overlayItemList != null && connectIndex > -1 && !list.isEmpty()) {
								HotspotModel hotspotInfo = list.get(connectIndex);
								hotspotInfo.status = HotspotModel.CONNECT_FAILED;
								overlayItemList.updatePopupView(popupIndex);
								overlayItemList.updatePopupStrength(popupIndex);
							}
						}
					}).create();
				}
				accountDailog.setCancelable(false);
				accountDailog.show();
			} else if(action.equals(ACTION_CONNECT_RESULT) && isConnecting && uid == connect_uid) {
				isConnecting = false;
				boolean success = intent.getBooleanExtra("hotspot_result", false);
				if(success) {
					if(overlayItemList != null && connectIndex > -1 && !list.isEmpty()) {
						HotspotModel hotspotInfo = list.get(connectIndex);
						hotspotInfo.status = HotspotModel.CONNECT_SUCCESS;
						overlayItemList.updatePopupView(popupIndex);
					}
					Toast.makeText(context, getResources().
							getString(R.string.map_connect_result_success), Toast.LENGTH_SHORT).show();
				} else {
					if(overlayItemList != null && connectIndex > -1 && !list.isEmpty()) {
						HotspotModel hotspotInfo = list.get(connectIndex);
						hotspotInfo.status = HotspotModel.CONNECT_FAILED;
						overlayItemList.updatePopupView(popupIndex);
					}
					Toast.makeText(context, getResources().
							getString(R.string.map_connect_result_failed), Toast.LENGTH_LONG).show();
				}
			} else if(action.equals(ACTION_CONNECT_BREAK) && uid == connect_uid) {
				if(overlayItemList != null && connectIndex > -1 && !list.isEmpty()) {
					HotspotModel hotspotInfo = list.get(connectIndex);
					hotspotInfo.status = HotspotModel.UNKNOWN;
					hotspotInfo.signalStrength = HotspotModel.UNKNOWN;
					overlayItemList.updatePopupView(popupIndex);
					overlayItemList.updatePopupStrength(popupIndex);
				}
			}
		}
	}
	
	/**
	 * location listener to monitor location change
	 *
	 */
	private class MyloccationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				if(dialogGettingGeo != null) {
					dialogGettingGeo.dismiss();
				}
				if(geoTimeoutTask != null) {
					geoTimeoutTimer.removeCallbacks(geoTimeoutTask);
				}
				
				currentLoc = location;
				GeoPoint pt = new GeoPoint(
						(int) (location.getLatitude() * 1e6),
						(int) (location.getLongitude() * 1e6));
				currentGeo = pt;
				/*Toast.makeText(context, 
						"latitude=" + location.getLatitude() + 
						"|||longitude=" + location.getLongitude(), Toast.LENGTH_SHORT).show();*/
				mMapView.getController().animateTo(pt);
				
				int newPlmn = CommonUtil.getPlmn(context);
				int newLac = CommonUtil.getLac(context);
				int newCid = CommonUtil.getCellId(context);
				// onplmnly apply when cell id change
				if(plmn != newPlmn || lac != newLac || cid != newCid) {
					System.out.println("plmn=" + plmn + "&newPlmn=" + newPlmn
							+ ";lac=" + lac + "&newLac=" + newLac + ";cid=" + cid + "&newCid=" + newCid);
					plmn = newPlmn;
					lac = newLac;
					cid = newCid;
					try {
						mDb = new DBAdpter(HotspotActivity.this);
						mDb.openDatabase();
						list = mDb.readHotspotList(newPlmn, newLac, newCid);
						lastConnectedSpot = mDb.getUserPreferredData(newPlmn, newLac, newCid);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						mDb.close();
					}
					
					// sort list by distance between user location and target location
					Collections.sort((List<HotspotModel>) list, new Comparator<HotspotModel>(){
						@Override
						public int compare(HotspotModel object1,
								HotspotModel object2) {
							double lat = currentLoc.getLatitude();
							double lon = currentLoc.getLongitude();
							double dis1 = LocationUtil.getDistance(object1.getLongitude(),
									object1.getLatitude(), lon, lat);
							double dis2 = LocationUtil.getDistance(object2.getLongitude(),
									object2.getLatitude(), lon, lat);
							if(dis1 < dis2) return -1;
							if(dis1 > dis2) return 1;
							return 0;
						}
					});
					
					if(mPopView != null) {
						mPopView.setVisibility(View.INVISIBLE);
					}
					if(overlayItemList != null) {
						mMapView.getOverlays().remove(overlayItemList);
					}
					overlayItemList = new OverItemT(marker, context);
					mMapView.getOverlays().add(overlayItemList);
					mMapView.invalidate();
				}
		}
		}
	}
	
	/**
	 * hotspot pin layer view
	 *
	 */
	class OverItemT extends ItemizedOverlay<OverlayItem> {

		private List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		private Drawable marker;
		private Context mContext;
		private LinearLayout scanArea;
		private TextView scanningStatus;
		private TextView contentView2;
		private ProgressBar progressBar;
		private Button connectBtn;
		private boolean isconnected;

		public OverItemT(Drawable marker, Context context) {
			super(boundCenterBottom(marker));
			mContext = context;
			this.marker = marker;
			GeoPoint point;
			for (HotspotModel hotspot : list) {
//				final double lat = hotspot.getLatitude() + 0.0060;// match to google map
//				final double lon = hotspot.getLongitude() + 0.0065;// match to google map
				final double lat = hotspot.getLatitude();
				final double lon = hotspot.getLongitude();
				final long radius = hotspot.getRadius();
				if (lastConnectedSpot != null
						&& lastConnectedSpot.getSSID().equalsIgnoreCase(
								hotspot.getSsid())
								&& Math.abs(lastConnectedSpot.getLatitude()- lat) < 0.00000000001
								&& Math.abs(lastConnectedSpot.getLongitude() - lon) < 0.0000000001) {
					hotspot.isLastConnected = true;
				} else {
					hotspot.isLastConnected = false;
				}
				point = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
				mGeoList.add(new OverlayItem(point, "", ""));
			}
			populate();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			Projection projection = mapView.getProjection();
			for (int index = size() - 1; index >= 0; index--) {
				OverlayItem overLayItem = getItem(index);
				String title = overLayItem.getTitle();
				Point point = projection.toPixels(overLayItem.getPoint(), null);
				Paint paintText = new Paint();
				paintText.setColor(Color.BLUE);
				paintText.setTextSize(15);
				canvas.drawText(title, point.x - 30, point.y, paintText);
			}
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mGeoList.get(i);
		}

		@Override
		public int size() {
			return mGeoList.size();
		}

		@Override
		protected boolean onTap(final int i) {
			OverlayItem item = mGeoList.get(i);
			final HotspotModel hotspotInfo = list.get(i);
			setFocus(item);
			popupIndex = i;
			
			TextView titleView = (TextView) findViewById(R.id.map_bubbleTitle);
			TextView contentView = (TextView) findViewById(R.id.map_bubbleText1);
			titleView.setText(context.getResources().getString(R.string.map_pop_title));
			contentView2 = (TextView) findViewById(R.id.map_bubbleText2);
			scanArea = (LinearLayout) findViewById(R.id.scanning_progress);
			scanningStatus = (TextView) findViewById(R.id.scanning_msg_text);
			progressBar = (ProgressBar) findViewById(R.id.scanning_processbar);
			
			StringBuffer content = new StringBuffer();
			content.append(context.getResources().getString(R.string.map_pop_ssid))
			.append(hotspotInfo.getSsid()).append("\n")
			.append(context.getText(R.string.map_pop_last_connection))
			.append(hotspotInfo.isLastConnected ? context.getText(R.string.map_last_connected_yes) 
					: context.getText(R.string.map_last_connected_no));
			contentView.setText(content.toString());
			ImageView image = (ImageView) findViewById(R.id.map_bubbleImage);
			image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPopView.setVisibility(View.GONE);
					if(needCloseWifi) {
						wifiManager.setWifiEnabled(false);
					}
				}
			});
			final Button routeBtn = (Button) findViewById(R.id.map_route);
			routeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					routeTargetName = hotspotInfo.getSsid();
//					routeTargetGeo = new GeoPoint((int) ((hotspotInfo.getLatitude() + + 0.0060) * 1E6),
//							(int) ((hotspotInfo.getLongitude() + + 0.0065) * 1E6));
					routeTargetGeo = new GeoPoint((int) ((hotspotInfo.getLatitude()) * 1E6),
							(int) ((hotspotInfo.getLongitude()) * 1E6));
					setRouteAreaVisibility(true);
					mPopView.setVisibility(View.GONE);
				}
			});
			connectBtn = (Button) findViewById(R.id.map_connect);
			connectBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for (HotspotModel hot : list) {
						hot.isLastConnected = false;
					}
					hotspotInfo.isLastConnected = true;
					connectIndex = i;
					needCloseWifi = false;
					
					final WifiModel hotspot = new WifiModel(hotspotInfo.getSsid());
					hotspot.setPosition((int) hotspotInfo.getG3_cid());
					hotspot.setLac(hotspotInfo.getG3_lac());
					hotspot.setPlmn(hotspotInfo.getG3_plmn());
//					hotspot.setLatitude(hotspotInfo.getLatitude() + 0.0060);
//					hotspot.setLongitude(hotspotInfo.getLongitude() + 0.0065);
					hotspot.setLatitude(hotspotInfo.getLatitude());
					hotspot.setLongitude(hotspotInfo.getLongitude());
					
					// do connect
					isConnecting = true;
					connect_uid = System.currentTimeMillis();
					clearHotspotsStatus();
					
					Intent sendIntent = new Intent(Constants.Action.ACTION_HOTSPOT);
					sendIntent.putExtra("hotspot", hotspot);
					sendIntent.putExtra("hotspot_connect_session_id", connect_uid);
					context.sendBroadcast(sendIntent);
					
					// update status screen
					AppApplication.current_network = hotspot;
					AppApplication.connecting_status = HomeActivity.STATUS_CONNECTING;
					
					// update popup dialog status
					hotspotInfo.status = HotspotModel.CONNECTING;
					overlayItemList.updatePopupView(popupIndex);
					
					/*Intent sendIntent = new Intent(Constants.Action.ACTION_HOTSPOT);
					sendIntent.putExtra("hotspot", hotspot);
					context.sendBroadcast(sendIntent);
					AppApplication.current_network = hotspot;
					AppApplication.connecting_status = 1;
					if(scanTask != null) {
						scanTimer.removeCallbacks(scanTask);
					}
					isConnecting = true;
					if(dialogConnecting == null) {
						initConnectingDialog();
					}
					String ss = getString(R.string.map_connecting_prompt) + hotspot.getSSID();
					dialogConnecting.setMessage(ss);
					dialogConnecting.show();
					
					mPopView.setVisibility(View.GONE);*/
				}
			});
			
			// initial the default screen
			final WifiModel hotspot = new WifiModel(hotspotInfo.getSsid());
			if(hotspot.getAuthenticationType().equals(WifiModel.TYPE_PEAP) 
				|| hotspot.getAuthenticationType().equals(WifiModel.TYPE_SIM)) {
				if(CommonUtil.isNetworkConnected(context, hotspot)) {
					hotspotInfo.status = HotspotModel.ALREADY_CONNECT;
				}
			}
			//start scan or open wifi radio
			if(!WifiUtil.isWifiRadioOpen(context)) {
				wifiManager.setWifiEnabled(true);
				needCloseWifi = true;
				isOpenningRadio = true;
				isConnecting = false;
				hotspotInfo.status = HotspotModel.OPEN_WIFI;
				updatePopupView(i);
			} else {
				if(wifiManager.startScan()) {
					isScanning = true;
					isConnecting = false;
					hotspotInfo.status = HotspotModel.SCANNING;
					
					if(scanTimeoutTask != null) {
						scanTimeoutTimer.removeCallbacks(scanTimeoutTask);
					}
					initScanTimeoutTask();
					scanTimeoutTimer.postDelayed(scanTimeoutTask, Constants.TIMEOUT_SCAN);
					
					updatePopupView(i);
				} else {
					hotspotInfo.status = HotspotModel.UNKNOWN;
					updatePopupView(i);
				}
			}
			hotspotInfo.signalStrength = HotspotModel.STRENGTH_UNKNOW;
			updatePopupStrength(i);
			
			GeoPoint pt = mGeoList.get(i).getPoint();
			mMapView.updateViewLayout(mPopView, new MapView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, pt,
					MapView.LayoutParams.BOTTOM_CENTER));
			mPopView.setVisibility(View.VISIBLE);
			return true;
		}

		/**
		 * clear hotspot status
		 * @see {@linkplain HotspotModel}
		 */
		public void clearHotspotsStatus() {
			for (HotspotModel hotspot : list) {
				hotspot.resetStatus();
			}
		}

		/**
		 * update popup view content
		 * @param i
		 */
		public void updatePopupView(int i) {
			if(i < 0 || list.isEmpty()) {
				return;
			}
			HotspotModel hotspotInfo = list.get(i);
			String status = "";
			try {
				switch (hotspotInfo.status) {
				case HotspotModel.UNKNOWN:
					progressBar.setVisibility(View.GONE);
					status = "";
					connectBtn.setClickable(false);
					connectBtn.setEnabled(false);
					break;
				case HotspotModel.VALID:
					progressBar.setVisibility(View.GONE);
					status = "";
					connectBtn.setClickable(true);
					connectBtn.setEnabled(true);
					break;
				case HotspotModel.SCANNING:
					progressBar.setVisibility(View.VISIBLE);
					status = getText(R.string.map_connect_status_scanning).toString();
					connectBtn.setClickable(false);
					connectBtn.setEnabled(false);
					break;
				case HotspotModel.CONNECTING:
					progressBar.setVisibility(View.VISIBLE);
					status = getText(R.string.map_connect_status_connecting).toString();
					connectBtn.setClickable(false);
					connectBtn.setEnabled(false);
					break;
				case HotspotModel.CONNECT_SUCCESS:
					progressBar.setVisibility(View.GONE);
					status = getText(R.string.map_connect_status_success).toString();
					connectBtn.setClickable(true);
					connectBtn.setEnabled(true);
					break;
				case HotspotModel.CONNECT_FAILED:
					progressBar.setVisibility(View.GONE);
					status = getText(R.string.map_connect_status_failed).toString();
					connectBtn.setClickable(true);
					connectBtn.setEnabled(true);
					break;
				case HotspotModel.ALREADY_CONNECT:
					progressBar.setVisibility(View.GONE);
					status = getText(R.string.map_connect_status_already_connect).toString();
					connectBtn.setClickable(true);
					connectBtn.setEnabled(true);
					break;
				case HotspotModel.OPEN_WIFI:
					progressBar.setVisibility(View.VISIBLE);
					status = getText(R.string.map_connect_status_open_wifi).toString();
					connectBtn.setClickable(false);
					connectBtn.setEnabled(false);
					break;
				case HotspotModel.CONNECT_FORBIDDEN:
					progressBar.setVisibility(View.GONE);
					status = getText(R.string.map_connect_status_connect_forbidden).toString();
					connectBtn.setClickable(false);
					connectBtn.setEnabled(false);
					break;
				default:
					progressBar.setVisibility(View.GONE);
					status = "";
					connectBtn.setClickable(false);
					connectBtn.setEnabled(false);
					break;
				}
				scanningStatus.setText(status);
				populate();
			} catch (Exception e) {
			}
		}
		
		/**
		 *  update popup view signal strength
		 * @param i
		 */
		public void updatePopupStrength(int i) {
			if(i < 0 || list.isEmpty()) {
				return;
			}
			HotspotModel hotspotInfo = list.get(i);
			try {
				StringBuffer content = new StringBuffer();
				content.append(context.getResources().getString(R.string.map_pop_signal_strength));
				switch (hotspotInfo.signalStrength) {
				case HotspotModel.STRENGTH_UNKNOW:
					content.append(context.getText(R.string.map_signal_strength_unknow));
					break;
				case HotspotModel.STRENGTH_NONE:
					content.append(context.getText(R.string.map_signal_strength_nofound));
					break;
				case HotspotModel.STRENGTH_LOW:
					content.append(context.getText(R.string.map_signal_strength_weak));
					break;
				case HotspotModel.STRENGTH_MEDIUM:
					content.append(context.getText(R.string.map_signal_strength_medium));
					break;
				case HotspotModel.STRENGTH_STRONG:
				case HotspotModel.STRENGTH_PERFECT:
					content.append(context.getText(R.string.map_signal_strength_strong));
					break;
				}
				if(contentView2 != null) {
					contentView2.setText(content);
				}
				if(connectBtn != null) {
					if(hotspotInfo.signalStrength == HotspotModel.STRENGTH_NONE
							|| hotspotInfo.signalStrength == HotspotModel.STRENGTH_UNKNOW) {
						connectBtn.setClickable(false);
						connectBtn.setEnabled(false);
					} else {
						connectBtn.setClickable(true);
						connectBtn.setEnabled(true);
					}
				}
				populate();
			} catch (Exception e) {
			}
		}
	}

}
