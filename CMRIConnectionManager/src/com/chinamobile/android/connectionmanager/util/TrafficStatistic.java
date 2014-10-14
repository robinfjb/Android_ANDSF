package com.chinamobile.android.connectionmanager.util;

import android.net.TrafficStats;
import android.os.Build;
import android.util.Log;

public class TrafficStatistic {
	private static final String TAG = "TrafficStatistic";
	public static TrafficStatistic instance;
	private static long init_g3_down_total = -1;// the initial download data of 3g/2g
	private static long init_g3_up_total = -1;// the initial upload data of 3g/2g
	private static long init_wifi_down_total = -1;// the initial download data of wifi
	private static long init_wifi_up_total = -1;// the initial up data of wifi
	private static long init_mrdown_total = -1;// the initial download data of all
	private static long init_mtup_total = -1;// the initial upload data of all
	
	public static TrafficStatistic getInstance() {
		if(instance == null) {
			instance = new TrafficStatistic();
		}
		return instance;
	}
	

	/**
	 * start the traffic data monitor
	 */
	public void start() {
		if(Build.VERSION.RELEASE.compareTo("2.2") >= 0) {
			init_g3_down_total = TrafficStats.getMobileRxBytes();
			init_g3_up_total = TrafficStats.getMobileTxBytes();
			init_mrdown_total = TrafficStats.getTotalRxBytes();
			init_mtup_total = TrafficStats.getTotalTxBytes();
		} else {
			Integer[] recvSentBytes = new Integer[]{-1, -1};
			CommonUtil.getDeviceSentRecv("rmnet0", recvSentBytes);
			init_g3_down_total = recvSentBytes[0];
			init_g3_up_total = recvSentBytes[1];
			recvSentBytes = new Integer[]{-1, -1};
			CommonUtil.getDeviceSentRecv("wlan0", recvSentBytes);
			init_wifi_down_total = recvSentBytes[0];
			init_wifi_up_total = recvSentBytes[1];
		}
		
		TrafficData.reset();
	}
	
	/**
	 * stop the traffic data monitor
	 * <br>calculate the data
	 */
	public void stop() {
		if(Build.VERSION.RELEASE.compareTo("2.2") >= 0) {
			if (init_g3_down_total > -1) {
				TrafficData.g3_down_total = TrafficStats.getMobileRxBytes()
						- init_g3_down_total;
				TrafficData.g3_down_total = TrafficData.g3_down_total / 1024;
			}
			if (init_g3_up_total > -1) {
				TrafficData.g3_up_total = TrafficStats.getMobileTxBytes()
						- init_g3_up_total;
				TrafficData.g3_up_total = TrafficData.g3_up_total / 1024;
			}
			if (init_mrdown_total > -1) {
				TrafficData.mrdown_total = TrafficStats.getTotalRxBytes()
						- init_mrdown_total;
				TrafficData.mrdown_total = TrafficData.mrdown_total / 1024;
			}
			if (init_mtup_total > -1) {
				TrafficData.mtup_total = TrafficStats.getTotalTxBytes()
						- init_mtup_total;
				TrafficData.mtup_total = TrafficData.mtup_total / 1024;
			}
			if (TrafficData.mrdown_total > -1) {
				if(TrafficData.g3_down_total > -1) {
					TrafficData.wifi_down_total = (TrafficData.mrdown_total
							- TrafficData.g3_down_total);
				} else {
					TrafficData.wifi_down_total = TrafficData.mrdown_total;
				}
				
			}
			if (TrafficData.mtup_total > -1) {
				if(TrafficData.g3_up_total > -1) {
					TrafficData.wifi_up_total = (TrafficData.mtup_total
							- TrafficData.g3_up_total);
				} else {
					TrafficData.wifi_up_total = TrafficData.mtup_total;
				}
			}
			Log.i(TAG, "g3_down=" + TrafficData.g3_down_total);
			Log.i(TAG, "g3_up=" + TrafficData.g3_up_total);
			Log.i(TAG, "wifi_down=" + TrafficData.wifi_down_total);
			Log.i(TAG, "wifi_up=" + TrafficData.wifi_up_total);
			Log.i(TAG, "mrdown=" + TrafficData.mrdown_total);
			Log.i(TAG, "mtup=" + TrafficData.mtup_total);
			TrafficData.g3_total = (TrafficData.g3_down_total + TrafficData.g3_up_total);
			TrafficData.wifi_total = (TrafficData.wifi_down_total + TrafficData.wifi_up_total);
			TrafficData.all_total = (TrafficData.mrdown_total + TrafficData.mtup_total);
		} else {
			Integer[] recvSentBytes = new Integer[]{-1, -1};
			CommonUtil.getDeviceSentRecv("rmnet0", recvSentBytes);
			if (init_g3_down_total > -1) {
				TrafficData.g3_down_total = (recvSentBytes[0] - init_g3_down_total) / 1024;
			}
			if (init_g3_up_total > -1) {
				TrafficData.g3_up_total = (recvSentBytes[1] - init_g3_up_total) / 1024;
			}
			recvSentBytes = new Integer[]{-1, -1};
			CommonUtil.getDeviceSentRecv("wlan0", recvSentBytes);
			if (init_wifi_down_total > -1) {
				TrafficData.wifi_down_total = (recvSentBytes[0] - init_wifi_down_total) / 1024;
			}
			if (init_wifi_up_total > -1) {
				TrafficData.wifi_up_total = (recvSentBytes[1] - init_wifi_up_total) / 1024;
			}
			
			if (init_g3_down_total > -1 || init_wifi_down_total > -1) {
				TrafficData.mrdown_total = (TrafficData.g3_down_total + TrafficData.wifi_down_total);
			}
			if (init_g3_up_total > -1 || init_wifi_up_total > -1) {
				TrafficData.mtup_total = (TrafficData.g3_up_total + TrafficData.wifi_up_total);
			}
			TrafficData.g3_total = (TrafficData.g3_down_total + TrafficData.g3_up_total);
			TrafficData.wifi_total = (TrafficData.wifi_down_total + TrafficData.wifi_up_total);
			TrafficData.all_total = (TrafficData.mrdown_total + TrafficData.mtup_total);
		}
		
		reset();
	}
	
	/**
	 * reset
	 */
	public void reset() {
		init_g3_down_total = -1;
		init_g3_up_total = -1;
		init_wifi_down_total = -1;
		init_wifi_up_total = -1;
		init_mrdown_total = -1;
		init_mtup_total = -1;
	}
	
	/**
	 * traffic data model
	 *
	 */
	public static class TrafficData {
		public static long g3_down_total = -1;// received through the mobile interface format in Byte
		public static long g3_up_total = -1;// transmitted through the mobile interface format in Byte
		public static long wifi_down_total = -1; // received through the WiFi interface format in Byte
		public static long wifi_up_total = -1;// transmitted through the WiFi interface format in Byte
		public static long mrdown_total = -1; // received through all network interfaces format in Byte
		public static long mtup_total = -1; // sent through all network interfaces format in Byte
		public static long g3_total = -1;//3g data format in KB
		public static long wifi_total = -1;//wifi data format in KB
		public static long all_total = -1;// all data format in KB
		
		public static void reset() {
			g3_down_total = -1;
			g3_up_total = -1;
			wifi_down_total = -1;
			wifi_up_total = -1;
			mrdown_total = -1;
			mtup_total = -1;
			g3_total = -1;
			wifi_total = -1;
			all_total = -1;
		}
		
		public static boolean isDataValid() {
			return (g3_total > -1 && wifi_total > -1);
		}
	}
}
