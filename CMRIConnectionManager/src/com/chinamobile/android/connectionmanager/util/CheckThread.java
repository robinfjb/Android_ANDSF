/*package com.chinamobile.android.connectionmanager.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.controller.MessageListener;
import com.chinamobile.android.connectionmanager.manager.ConnectionManager;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel.TimeAndDate;
import com.chinamobile.android.connectionmanager.model.WifiModel;

public class CheckThread extends Thread{
	private static final String TAG = "CheckThread";
	private static final int THREAD_INTERVAL = 1000;
	private AppApplication app;
	private boolean isRunning = false;
//	private boolean isCheckingStrength = false;
	private int count = 0;
	private int retry = 0;
	private List<? extends NetworkModel> activeNetlist;
	private Map<String, ScanResult> scanResultMaps = new HashMap<String, ScanResult>();
	private Handler timeHander;
	private MessageListener msgListener;
	private boolean needNotify;
	
	public CheckThread(Handler timeHander, MessageListener msgListener) {
		app = AppApplication.getApp();
		this.timeHander = timeHander;
		this.msgListener = msgListener;
		//this.signalHandler = signalHandler;
	}
	
	@Override
	public void run() {
		while(isRunning) {
			if(AppApplication.isDynamic) {
				// signal strength check
				activeNetlist = app.getActiveNetList();
				// if WiFi radio is opened, check the strength of WiFi near the user location
				if(!activeNetlist.isEmpty() && ConnectionManager.isWifiRadioOpen()) {
					if(count > 9) {
						count = 0;
					}
					List<ScanResult> resultList = ConnectionManager.getScanResult();
					if(resultList == null) {
						retry ++;
						if(retry > 3) {//no scan result for over 3 times considered as no WiFi available 
							retry = 0;
						} else {
							count --;
						}
					} else {
						for (ScanResult scanResult : resultList) {
							scanResultMaps.put(scanResult.SSID, scanResult);
						}
						synchronized (activeNetlist) {
							for (int i = 0; i < activeNetlist.size(); i++) {
								NetworkModel net = activeNetlist.get(i);
								if(net.getType() == NetworkModel.TYPE_3G) {
									continue;
								} else {
									WifiModel wifi = (WifiModel) net;
									ScanResult result = scanResultMaps.get(wifi.getSSID());
									if (result == null) {// not exist in scan result list
										wifi.signalRecord[count] = WifiModel.SIGNAL_STATE_NONE;
									} else {
										final int strength = WifiManager.calculateSignalLevel(result.level, 10);
//										Log.d(TAG, "checkSignalThread--->strength---->"+ strength);
										if (strength >= AppApplication.minDBMLevel) {
											wifi.signalRecord[count] = WifiModel.SIGNAL_STATE_VALID;
										} else if (strength == 0) {
											wifi.signalRecord[count] = WifiModel.SIGNAL_STATE_NONE;
										} else {
											wifi.signalRecord[count] = WifiModel.SIGNAL_STATE_NONE;
										}
									}
									if(needNotify && msgListener != null) {
										if(Arrays.binarySearch(wifi.signalRecord, 0) < 0) {// have 10 results 
											needNotify = false;
											msgListener.onNotifySignalSamplingFinish();
										}
									}
								}
							}
						}
						Log.d(TAG, "signal strength check" + activeNetlist.toString());
					}
				}
				
				// time check
				Loop:
					for (PolicyModel policyModel : app.getActivePolicyList()) {
						List<TimeAndDate> timeList = policyModel.getTimeList();
						int size = timeList.size();
						int[] timeStatus = new int[size];
//						int[] removeIndexes = new int[size];
						for (int i = 0; i < size; i++) {
							timeStatus[i] = CommonUtil.validateTime(timeList.get(i));
//							Log.d(TAG, "timeStatus(" + i + "):" + timeStatus[i]);
							if(timeStatus[i] == TimeAndDate.VALID) {

								// send this message only policy with higher priority time arrived
								if ( app.getActivePolicy() != null && 
										policyModel.getRulePriority() < app.getActivePolicy().getRulePriority()) {
									Log.d(TAG, "VALID:" + policyModel.getRulePriority() + "/"
											+ app.getActivePolicy().getRulePriority());
									
									timeHander.sendEmptyMessage(AppApplication.MSG_TIME_ARRIVED);
									break Loop;
								}
							} else if(timeStatus[i] == TimeAndDate.FUTURE) {
								
							} else if(timeStatus[i] == TimeAndDate.EXPIRED) {
//								Log.d(TAG, "EXPIRED:---->policyModel--->" + policyModel);
								//send the expired message only when the policy is current policy 
								if (app.getActivePolicy() != null && policyModel.getRulePriority() 
										== app.getActivePolicy().getRulePriority()) {
									if(i == size - 1) {//get the last one
									// to delete condition must be met:
									//  no valid time and future time in the time list
										if(Arrays.binarySearch(timeStatus, TimeAndDate.VALID) < 0 && 
											Arrays.binarySearch(timeStatus, TimeAndDate.FUTURE) < 0) {
											Log.i(TAG, "EXPIRED: delete");
											Message msg = timeHander.obtainMessage();
											msg.what = AppApplication.MSG_TIME_EXPIRED;
											Bundle bundle = new Bundle();
											bundle.putBoolean("needDelete", true);
											msg.setData(bundle);
											timeHander.sendMessage(msg);
											break Loop;
										}
									} else {
										//without delete conditions must be met: 
										//no valid time in time list
										if(Arrays.binarySearch(timeStatus,
												TimeAndDate.VALID) < 0) {
											Log.i(TAG, "EXPIRED: undelete");
											Message msg = timeHander.obtainMessage();
											msg.what = AppApplication.MSG_TIME_EXPIRED;
											Bundle bundle = new Bundle();
											bundle.putBoolean("needDelete", false);
											msg.setData(bundle);
											timeHander.sendMessage(msg);
											break Loop;
										}
									}
								}
										
							} 
						}
					}
				
				count++;
			}
			
			try {
				Thread.sleep(THREAD_INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void onStart() {
		isRunning = true;
		this.start();
	}
	
	public void onStop() {
		isRunning = false;
		if(!this.isInterrupted()) {
			this.interrupt();
		}
	}
	
	public void setNeedNotify() {
		needNotify = true;
	}

	public void notifyStartCheckingStrength() {
		List<NetworkModel> temp = app.getActiveNetList();
		for (NetworkModel networkModel : temp) {
			if(networkModel.getType() == NetworkModel.TYPE_WIFI) {
				activeNetlist.add(networkModel);
			}
		}
		count = 0;
		retry = 0;
		isCheckingStrength = true;
	}
	
	public void notifyStopCheckingStrength() {
		isCheckingStrength = false;
		count = 0;
		retry = 0;
		activeNetlist.clear();
		scanResultMaps.clear();
	}
}
*/