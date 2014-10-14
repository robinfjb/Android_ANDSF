package com.chinamobile.android.connectionmanager.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.database.PolicyDB.ProfileType;
import com.chinamobile.android.connectionmanager.model.AppModel;
import com.chinamobile.android.connectionmanager.model.HotspotModel;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel.GeoLocation;
import com.chinamobile.android.connectionmanager.model.PolicyModel.TimeAndDate;
import com.chinamobile.android.connectionmanager.model.ReportModel.ReportData;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.model._3GModel;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.LocationUtil;
import com.chinamobile.android.connectionmanager.util.TimeUtil;

public class DBAdpter {
	private static final String TAG = "DBAdpter";
	private static final String DB_NAME = "cmri_andsf_cm_database.db";
	private static final int DB_VERSION = 1;
	private final int PREFERRED_MAX_SIZE = 100;
	private final int REPORT_MAX_SIZE = 20;
	private final int LOG_MAX_SIZE = 50;
	private final int POLICY_MAX_SIZE = 200;
	private final int HOTSPOT_MAX_SIZE = 200;
	private final int APP_MAX_SIZE = 200;
	
	// table name
//	private static final String DB_TABLE_POLICY_LIST = "policy_list";
	final String DB_TABLE_POLICY_TIME = "policy_time";
	private static final String DB_TABLE_USER_PREFERRED = "user_preferred";
	private static final String DB_TABLE_REPORT = "report";
	private static final String DB_TABLE_HOTSPOT = "hotspot";
	private static final String DB_TABLE_WLAN_INFO = "wlan_info";
	private static final String DB_TABLE_LOG = "policy_log";
	private static final String DB_TABLE_APP = "application_setting";
	
	// preferred column name
	private static final String COLUMN_PREFERRED_ID = "preferred_id";
	private static final String COLUMN_PREFERRED_LATITUDE = "preferred_latitude";
	private static final String COLUMN_PREFERRED_LONGITUDE = "preferred_longitude";
	private static final String COLUMN_PREFERRED_SSID = "preferred_ssid";
	private static final String COLUMN_PREFERRED_AUTH_TYPE = "preferred_auth_type";
	private static final String COLUMN_PREFERRED_SIGNAL_STRENGTH = "preferred_signal_strength";
	private static final String COLUMN_PREFERRED_CID = "preferred_cid";
	private static final String COLUMN_PREFERRED_PLMN = "preferred_plmn";
	private static final String COLUMN_PREFERRED_LAC = "preferred_lac";
	
	// report column name
	private static final String COLUMN_REPORT_ID = "report_id";
	private static final String COLUMN_REPROT_NETWORK_TYPE = "report_network_type";
	private static final String COLUMN_REPROT_NETWORK_NAME = "report_network_name";
	private static final String COLUMN_REPROT_TIME = "report_time";
	private static final String COLUMN_REPROT_LOCATION_CID = "report_cell_id";
	private static final String COLUMN_REPROT_LOCATION_PLMN = "report_cell_plmn";
	private static final String COLUMN_REPROT_LOCATION_LAC = "report_cell_lac";
	private static final String COLUMN_REPROT_LOCATION_LAT = "report_latitude";
	private static final String COLUMN_REPROT_LOCATION_LON = "report_longitude";
	private static final String COLUMN_REPROT_WIFI_TRAFFIC_UP = "report_wifi_traffic_up";
	private static final String COLUMN_REPROT_WIFI_TRAFFIC_DOWN = "report_wifi_traffic_down";
	private static final String COLUMN_REPROT_3G_TRAFFIC_UP = "report_3g_traffic_up";
	private static final String COLUMN_REPROT_3G_TRAFFIC_DOWN = "report_3g_traffic_down";
	
	// policy column name
	private static final String COLUMN_POLICY_ID = "policy_id";
	private static final String COLUMN_POLICY_PRIORITY = "policy_priority";
	private static final String COLUMN_POLICY_NET_NAME = "policy_net_name";
	private static final String COLUMN_POLICY_NET_SSID = "policy_net_ssid";
	private static final String COLUMN_POLICY_NET_PRIORITY = "policy_net_prio";
	private static final String COLUMN_POLICY_NET_SEC_SSID = "policy_net_sec_ssid";
	private static final String COLUMN_POLICY_3G_PLMN = "policy_3g_plmn";
	private static final String COLUMN_POLICY_3G_TAC = "policy_3g_tac";
	private static final String COLUMN_POLICY_3G_LAC = "policy_3g_lac";
	private static final String COLUMN_POLICY_3G_GERAN_CI = "policy_3g_grean_ci";
	private static final String COLUMN_POLICY_3G_UTRAN_CI = "policy_3g_utran_ci";
	private static final String COLUMN_POLICY_3G_EUTRA_CI = "policy_3g_eutra_ci";
	private static final String COLUMN_POLICY_WLAN_HESSID = "policy_wlan_hessid";
	private static final String COLUMN_POLICY_WLAN_SSID = "policy_wlan_ssid";
	private static final String COLUMN_POLICY_WLAN_BSSID = "policy_wlan_bssid";
	private static final String COLUMN_POLICY_GEO_LAT = "policy_geo_lat";
	private static final String COLUMN_POLICY_GEO_LON = "policy_geo_lon";
	private static final String COLUMN_POLICY_GEO_RADIUS = "policy_geo_radius";
	private static final String COLUMN_POLICY_TIME_START = "policy_time_start";
	private static final String COLUMN_POLICY_TIME_END = "policy_time_end";
	private static final String COLUMN_POLICY_DATE_START = "policy_date_start";
	private static final String COLUMN_POLICY_DATE_END = "policy_date_end";
	private static final String COLUMN_POLICY_PRIORITY_JOIN = "join_policy_priority";
	private static final String COLUMN_POLICY_UID = "policy_uid";
	private static final String COLUMN_POLICY_NETWORK_TYPE = "policy_network_type";
	
	// hotspot column name
	private static final String COLUMN_HOTSPOT_ID = "hotspot_id";
	private static final String COLUMN_HOTSPOT_NET_TYPE = "hotspot_net_type";
	private static final String COLUMN_HOTSPOT_3G_PLMN = "hotspot_3g_plmn";
	private static final String COLUMN_HOTSPOT_3G_LAC = "hotspot_3g_lac";
	private static final String COLUMN_HOTSPOT_3G_GERAN_CI = "hotspot_3g_grean_ci";
	private static final String COLUMN_HOTSPOT_GEO_LAT = "hotspot_geo_lat";
	private static final String COLUMN_HOTSPOT_GEO_LON = "hotspot_geo_lon";
	private static final String COLUMN_HOTSPOT_GEO_RADIUS = "hotspot_geo_radius";
	private static final String COLUMN_HOTSPOT_NET_REF = "hotspot_net_ref";
	private static final String COLUMN_HOTSPOT_PLMN = "hotspot_plmn";
	private static final String COLUMN_HOTSPOT_SSID = "hotspot_ssid";
	
	// wlan column name
	private static final String COLUMN_WLAN_ID = "wlan_id";
	private static final String COLUMN_WLAN_CONTENT_ID = "wlan_content_id";
	private static final String COLUMN_WLAN_ADDR = "wlan_addr";
	private static final String COLUMN_WLAN_ADDR_TYPE = "wlan_addr_type";
	private static final String COLUMN_WLAN_BEAR_TYPE = "wlan_bear_type";
	private static final String COLUMN_WLAN_SSID = "wlan_ssid";
	private static final String COLUMN_WLAN_URI = "wlan_uri";
	
	// log column name
	private static final String COLUMN_LOG_ID = "log_id";
	private static final String COLUMN_LOG_DATE = "log_date";
	private static final String COLUMN_LOG_TIME = "log_time";
	private static final String COLUMN_LOG_CONTENT = "log_content";
	private static final String COLUMN_LOG_TYPE = "log_type";
	
	// appliction setting
	private static final String COLUMN_APP_ID = "app_id";
	private static final String COLUMN_APP_LABEL = "app_label";
	private static final String COLUMN_APP_PACKET = "app_packet";
	private static final String COLUMN_APP_NETWORK_SET = "app_setting";
	
	// SQL statement to create table
	private static final String CREATE_TABLE_USER_PREFERRED = "create table if not exists "
			+ DB_TABLE_USER_PREFERRED
			+ " ("
			+ COLUMN_PREFERRED_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_PREFERRED_LONGITUDE + " varchar, " 
			+ COLUMN_PREFERRED_LATITUDE + " varchar, " 
			+ COLUMN_PREFERRED_SSID + " varchar ,"
			+ COLUMN_PREFERRED_AUTH_TYPE + " varchar, "
			+ COLUMN_PREFERRED_SIGNAL_STRENGTH + " integer, "
			+ COLUMN_PREFERRED_CID + " varchar, "
			+ COLUMN_PREFERRED_PLMN + " varchar, "
			+ COLUMN_PREFERRED_LAC + " varchar "
			+ ");";
	
	private static final String CREATE_TABLE_REPORT = "create table if not exists "
			+ DB_TABLE_REPORT
			+ " ("
			+ COLUMN_REPORT_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_REPROT_NETWORK_TYPE + " varchar, " 
			+ COLUMN_REPROT_NETWORK_NAME + " varchar, " 
			+ COLUMN_REPROT_TIME + " varchar ,"
			+ COLUMN_REPROT_LOCATION_CID + " integer, "
			+ COLUMN_REPROT_LOCATION_PLMN + " integer, "
			+ COLUMN_REPROT_LOCATION_LAC + " integer, "
			+ COLUMN_REPROT_LOCATION_LAT + " varchar, "
			+ COLUMN_REPROT_LOCATION_LON + " varchar, "
			+ COLUMN_REPROT_WIFI_TRAFFIC_DOWN + " varchar, "
			+ COLUMN_REPROT_WIFI_TRAFFIC_UP + " varchar, "
			+ COLUMN_REPROT_3G_TRAFFIC_DOWN + " varchar, "
			+ COLUMN_REPROT_3G_TRAFFIC_UP + " varchar "
			+ ");";
	
	final String CREATE_TABLE_POLICY_TIME = "create table if not exists "
			+ DB_TABLE_POLICY_TIME
			+ " ("
			+ COLUMN_POLICY_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_POLICY_TIME_START
			+ " varchar, "
			+ COLUMN_POLICY_TIME_END
			+ " varchar, "
			+ COLUMN_POLICY_DATE_START
			+ " varchar, "
			+ COLUMN_POLICY_DATE_END
			+ " varchar, "
			+ COLUMN_POLICY_PRIORITY_JOIN
			+ " varchar, "
			+ COLUMN_POLICY_3G_GERAN_CI
			+ " varchar, " 
			+ COLUMN_POLICY_3G_PLMN
			+ " varchar, " 
			+ COLUMN_POLICY_3G_LAC
			+ " varchar, " 
			+ COLUMN_POLICY_GEO_LAT
			+ " varchar, "
			+ COLUMN_POLICY_GEO_LON
			+ " varchar, "
			+ COLUMN_POLICY_GEO_RADIUS
			+ " varchar, "
			+ COLUMN_POLICY_NETWORK_TYPE
			+ " varchar "
			+ ");";
	
	private static final String CREATE_TABLE_HOTSPOT = "create table if not exists "
			+ DB_TABLE_HOTSPOT
			+ " ("
			+ COLUMN_HOTSPOT_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_HOTSPOT_NET_TYPE + " varchar, " 
			+ COLUMN_HOTSPOT_3G_PLMN + " varchar, " 
			+ COLUMN_HOTSPOT_3G_LAC + " varchar ,"
			+ COLUMN_HOTSPOT_3G_GERAN_CI + " integer, "
			+ COLUMN_HOTSPOT_GEO_LAT + " varchar, "
			+ COLUMN_HOTSPOT_GEO_LON + " varchar, "
			+ COLUMN_HOTSPOT_GEO_RADIUS + " varchar, "
			+ COLUMN_HOTSPOT_NET_REF + " varchar, "
			+ COLUMN_HOTSPOT_PLMN + " varchar, "
			+ COLUMN_HOTSPOT_SSID + " varchar "
			+ ");";
	
	private static final String CREATE_TABLE_WLANINFO = "create table if not exists "
			+ DB_TABLE_WLAN_INFO
			+ " ("
			+ COLUMN_WLAN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_WLAN_CONTENT_ID + " varchar, " 
			+ COLUMN_WLAN_ADDR + " varchar, " 
			+ COLUMN_WLAN_ADDR_TYPE + " varchar ,"
			+ COLUMN_WLAN_BEAR_TYPE + " integer, "
			+ COLUMN_WLAN_SSID + " varchar, "
			+ COLUMN_WLAN_URI + " varchar "
			+ ");";
	
	private static final String CREATE_TABLE_LOG = "create table if not exists "
			+ DB_TABLE_LOG
			+ " ("
			+ COLUMN_LOG_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_LOG_DATE + " varchar, " 
			+ COLUMN_LOG_TIME + " varchar, " 
			+ COLUMN_LOG_CONTENT + " varchar, "
			+ COLUMN_LOG_TYPE+ " integer "
			+ ");";
	
	private static final String CREATE_TABLE_APP = "create table if not exists "
			+ DB_TABLE_APP
			+ " ("
			+ COLUMN_APP_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_APP_LABEL + " varchar, " 
			+ COLUMN_APP_PACKET + " varchar, " 
			+ COLUMN_APP_NETWORK_SET + " integer"
			+ ");";
	
	private SQLiteDatabase mDb;
	private Context mContext;
	private DatabaseHelper dbHelper;
	private Map<Integer, WifiModel> userPreferredCache = Collections.emptyMap();
	private boolean isOpen = false;
//	private PolicyDB policyDb;
	
	public DBAdpter(Context context) {
		mContext = context;
	}
	
	public void openDatabase() throws SQLException {
		dbHelper = new DatabaseHelper(mContext);
		mDb = dbHelper.getWritableDatabase();
	}
	
	// close the database
	public void close() {
		try {
			mDb.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			dbHelper.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * get all user preferred data
	 */
	private void loadUserPreferredData() {
		Cursor cur = null;
		userPreferredCache = new LinkedHashMap<Integer, WifiModel>();
		try {
			cur = mDb.query(DB_TABLE_USER_PREFERRED, null, null, null, null,
					null, COLUMN_PREFERRED_ID);
			while (cur.moveToNext()) {
				Log.w(TAG, "get preferred data");
				WifiModel wifi = new WifiModel();
				final double longtitude = cur.getDouble(cur
						.getColumnIndex(COLUMN_PREFERRED_LONGITUDE));
				wifi.setLongitude(longtitude);
				final double latitude = cur.getDouble(cur
						.getColumnIndex(COLUMN_PREFERRED_LATITUDE));
				wifi.setLatitude(latitude);
				final String ssid = cur.getString(cur
						.getColumnIndex(COLUMN_PREFERRED_SSID));
				wifi.setSSID(ssid);
				final String authType = cur.getString(cur
						.getColumnIndex(COLUMN_PREFERRED_AUTH_TYPE));
				wifi.setAuthenticationType(authType);
				final int sStrength = cur.getInt(cur
						.getColumnIndex(COLUMN_PREFERRED_SIGNAL_STRENGTH));
				wifi.setSignalStrength(sStrength);
				final int position = cur.getInt(cur
						.getColumnIndex(COLUMN_PREFERRED_CID));
				wifi.setPosition(position);
				final int plmn = cur.getInt(cur
						.getColumnIndex(COLUMN_PREFERRED_PLMN));
				wifi.setPlmn(plmn);
				final int lac = cur.getInt(cur
						.getColumnIndex(COLUMN_PREFERRED_LAC));
				wifi.setLac(lac);
				userPreferredCache.put(position, wifi);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
				cur = null;
			}
		}
	}
	
	/**
	 * get spec user perferred data
	 * @param plmn
	 * @param lac
	 * @param position
	 * @return
	 */
	public WifiModel getUserPreferredData(int plmn, int lac, int position) {
		if(plmn <= 0 || lac <= 0 || position <= 0) {
			return null;
		}
		loadUserPreferredData();
		for (Map.Entry<Integer, WifiModel> entry : userPreferredCache.entrySet()) {
			Integer key = entry.getKey();
			WifiModel data = entry.getValue();
			if (key == position && data.getPlmn() == plmn
					&& data.getLac() == lac) {
				return data;
			}
		}
		return null;
	}
	
	/**
	 * add user preferred data
	 * @param wifi
	 * @return
	 */
	public boolean addUserPreferredData(WifiModel wifi) {
		final double longitude = wifi.getLongitude();
		final double latitude = wifi.getLatitude();
		final String ssid = wifi.getSSID();
		final String authType = wifi.getAuthenticationType();
		final int sStrength = wifi.getSignalStrength();
		final int position = wifi.getPosition();
		final int lac = wifi.getLac();
		final int plmn = wifi.getPlmn();
		if(latitude <= 0 || longitude <= 0 || ssid == null || position <= 0
				|| lac <= 0 || plmn <= 0) {
			Log.w(TAG, "addUserPreferredData------->wifi data is wrong");
			return false;
		}
		loadUserPreferredData();
		for (Map.Entry<Integer, WifiModel> entry : userPreferredCache.entrySet()) {
			Integer key = entry.getKey();
			WifiModel data = entry.getValue();
			if(key == position) {//update data in the same position instead of add
				mDb.execSQL("delete from " + DB_TABLE_USER_PREFERRED
						+ " where " + COLUMN_PREFERRED_CID + " = " + position
						+ " and " + COLUMN_PREFERRED_LAC + " = " + lac
						+ " and " + COLUMN_PREFERRED_PLMN + " = " + plmn);
//				userPreferredCache.remove(key);
			}
		}
		
		if (userPreferredCache.size() >= PREFERRED_MAX_SIZE) {
			int removePosition = userPreferredCache.entrySet().iterator().next().getKey();//remove first
//			userPreferredCache.remove(removePosition);
			mDb.execSQL("delete from " + DB_TABLE_USER_PREFERRED + " where "
					+ COLUMN_PREFERRED_CID + " = " + removePosition);
		}
		
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PREFERRED_LONGITUDE, longitude);
		cv.put(COLUMN_PREFERRED_LATITUDE, latitude);
		cv.put(COLUMN_PREFERRED_SIGNAL_STRENGTH, sStrength);
		cv.put(COLUMN_PREFERRED_SSID, ssid);
		cv.put(COLUMN_PREFERRED_AUTH_TYPE, authType);
		cv.put(COLUMN_PREFERRED_CID, position);
		cv.put(COLUMN_PREFERRED_PLMN, plmn);
		cv.put(COLUMN_PREFERRED_LAC, lac);
		mDb.insertOrThrow(DB_TABLE_USER_PREFERRED, null, cv);
//		userPreferredCache.put(position, wifi);
		return true;
	}
	
	/**
	 * get log data
	 * @return
	 */
	public List<String[]> getLogData() {
		
		List<String[]> list = new ArrayList<String[]>();
		Cursor cur = mDb.query(DB_TABLE_LOG, null, null, null, null,
				null, COLUMN_LOG_ID);
		if(cur == null) {
			return null;
		}
		while (cur.moveToNext()) {
			try {
				String[] strArray = new String[3];
				strArray[0] = cur.getString(cur.getColumnIndex(COLUMN_LOG_DATE));
				strArray[1] = cur.getString(cur.getColumnIndex(COLUMN_LOG_TIME));
				strArray[2] = String.valueOf(cur.getInt(cur.getColumnIndex(COLUMN_LOG_TYPE)));
				list.add(strArray);
			}catch (Exception e) {
				continue;
			}
		}
		return list;
	}
	
	/**
	 * add log data
	 * @param type
	 */
	public void addLogData(int type) {
		Date date = new Date();
		SimpleDateFormat formatterDay = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss");
		String day = formatterDay.format(date);
		String time = formatterTime.format(date);
		// set max size

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_LOG_DATE, day);
		cv.put(COLUMN_LOG_TIME, time);
		cv.put(COLUMN_LOG_CONTENT, "");
		cv.put(COLUMN_LOG_TYPE, type);

		Cursor cursor = mDb.rawQuery("select " + COLUMN_LOG_ID + " from "
				+ DB_TABLE_LOG, null);
		if (cursor != null) {
			int size = cursor.getCount();
			if (size >= LOG_MAX_SIZE) {// delete the top record
				cursor.moveToNext();
				int index = cursor.getInt(cursor
						.getColumnIndex(COLUMN_LOG_ID));
				mDb.execSQL("delete from " + DB_TABLE_LOG + " where "
						+ COLUMN_LOG_ID + " = " + index);
			}
			cursor.close();
			cursor = null;
		}
		mDb.insertOrThrow(DB_TABLE_LOG, null, cv);
	}
	
	/**
	 * add report data
	 * @param report
	 * @return
	 */
	public boolean addReportData(ReportData report) {
		final double longitude = report.longitude;
		final double latitude = report.latitude;
		final String networkName = report.networkName;
		final String networkType = report.networkType;
		final String time = report.time;
		final int cellId = report.cellId;
		final int plmn = report.plmn;
		final int lac = report.lac;
		final long wifiTrafficUpload = report.wifiTrafficUpload;
		final long wifiTrafficDownload = report.wifiTrafficDownload;
		final long g3TrafficUpload = report.g3TrafficUpload;
		final long g3TrafficDownload = report.g3TrafficDownload;
		if(time == null || time.trim().equals("")
				|| networkName == null || networkName.trim().equals("")
				|| networkType == null || networkType.trim().equals("")) {
			Log.w(TAG, "addReportData------->report data is wrong");
			return false;
		}
		
		try {
			//update data in the same position instead of add
			Map<String, ReportData> map = loadReportDataFromDB();
			for (Entry<String, ReportData> entry : map.entrySet()) {
				final String timeKey = entry.getKey();
				if(time.equals(timeKey)) {
					mDb.execSQL("delete from " + DB_TABLE_REPORT
							+ " where " + COLUMN_REPROT_TIME+ " = " + time);
				}
			}
			
			if (map.size() >= REPORT_MAX_SIZE) {
				String removeTime = map.entrySet().iterator().next().getKey();//remove first
//				userPreferredCache.remove(removePosition);
				mDb.execSQL("delete from " + DB_TABLE_REPORT + " where "
						+ COLUMN_REPROT_TIME + " = " + removeTime);
			}
			
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_REPROT_LOCATION_CID, cellId);
			cv.put(COLUMN_REPROT_LOCATION_PLMN, plmn);
			cv.put(COLUMN_REPROT_LOCATION_LAC, lac);
			cv.put(COLUMN_REPROT_LOCATION_LAT, latitude);
			cv.put(COLUMN_REPROT_LOCATION_LON, longitude);
			cv.put(COLUMN_REPROT_NETWORK_NAME, networkName);
			cv.put(COLUMN_REPROT_NETWORK_TYPE, networkType);
			cv.put(COLUMN_REPROT_TIME, time);
			cv.put(COLUMN_REPROT_WIFI_TRAFFIC_UP, wifiTrafficUpload);
			cv.put(COLUMN_REPROT_WIFI_TRAFFIC_DOWN, wifiTrafficDownload);
			cv.put(COLUMN_REPROT_3G_TRAFFIC_UP, g3TrafficUpload);
			cv.put(COLUMN_REPROT_3G_TRAFFIC_DOWN, g3TrafficDownload);
			mDb.insertOrThrow(DB_TABLE_REPORT, null, cv);
			
			return true;
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
			return false;
		}
	}
	
	/**
	 * update report data
	 * @param report
	 * @param key
	 * @return
	 */
	public boolean updateReportData(ReportData report, String key) {
		final long wifiTrafficUpload = report.wifiTrafficUpload;
		final long wifiTrafficDownload = report.wifiTrafficDownload;
		final long g3TrafficUpload = report.g3TrafficUpload;
		final long g3TrafficDownload = report.g3TrafficDownload;
		if(key == null || (wifiTrafficUpload <= 0 && wifiTrafficDownload <= 0
				&& g3TrafficUpload <= 0 && g3TrafficDownload <= 0)) {
			Log.w(TAG, "updateReportData------->report data is wrong");
			return false;
		}
		try {
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_REPROT_WIFI_TRAFFIC_UP, wifiTrafficUpload);
			cv.put(COLUMN_REPROT_WIFI_TRAFFIC_DOWN, wifiTrafficDownload);
			cv.put(COLUMN_REPROT_3G_TRAFFIC_UP, g3TrafficUpload);
			cv.put(COLUMN_REPROT_3G_TRAFFIC_DOWN, g3TrafficDownload);
			mDb.update(DB_TABLE_REPORT, cv, COLUMN_REPROT_TIME + "=?", new String[]{key});
			return true;
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
			return false;
		}
	}
	
	/**
	 * get report data list
	 * @return
	 */
	public List<ReportData> getReportData() {
		Map<String, ReportData> map = loadReportDataFromDB();
		List<ReportData> list = new ArrayList<ReportData>();
		if(map.isEmpty()) {
			return null;
		}
		
		for (Entry<String, ReportData> element : map.entrySet()) {
			ReportData data = element.getValue();
			if(data.g3TrafficDownload < 0) data.g3TrafficDownload = 0;
			if(data.g3TrafficUpload < 0) data.g3TrafficUpload = 0;
			if(data.wifiTrafficDownload < 0) data.wifiTrafficDownload = 0;
			if(data.wifiTrafficUpload < 0) data.wifiTrafficUpload = 0;
			if(data.g3TrafficDownload > 0 || data.g3TrafficUpload > 0
					|| data.wifiTrafficDownload > 0 || data.wifiTrafficUpload > 0) {
				list.add(data);
			}
		}
		return list;
	}
	
	/**
	 * clear report data
	 * @return
	 */
	public boolean clearPreviouReportData() {
		try {
			Map<String, ReportData> map = loadReportDataFromDB();
			if(map == null) {
				return false;
			}
			List<ReportData> list = (List<ReportData>) map.values();
			ReportData data = list.get(list.size() - 1);
			String key = data.time;
			mDb.execSQL("delete from " + DB_TABLE_REPORT + " where " + COLUMN_REPROT_TIME + 
					" <> " + key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * get all report data
	 * @return
	 */
	private Map<String, ReportData> loadReportDataFromDB() {
		Map<String, ReportData> reports = new LinkedHashMap<String, ReportData>();
		Cursor cur = mDb.query(DB_TABLE_REPORT, null, null, null, null,
				null, COLUMN_REPORT_ID);
		if(cur == null) {
			return null;
		}
		while (cur.moveToNext()) {
			try {
				ReportData report = new ReportData();
				report.networkType = cur.getString(cur.getColumnIndex(COLUMN_REPROT_NETWORK_TYPE));
				report.networkName = cur.getString(cur.getColumnIndex(COLUMN_REPROT_NETWORK_NAME));
				report.time = cur.getString(cur.getColumnIndex(COLUMN_REPROT_TIME));
				report.cellId = cur.getInt(cur.getColumnIndex(COLUMN_REPROT_LOCATION_CID));
				report.plmn = cur.getInt(cur.getColumnIndex(COLUMN_REPROT_LOCATION_PLMN));
				report.lac = cur.getInt(cur.getColumnIndex(COLUMN_REPROT_LOCATION_LAC));
				report.latitude =cur.getDouble(cur.getColumnIndex(COLUMN_REPROT_LOCATION_LAT));
				report.longitude =cur.getDouble(cur.getColumnIndex(COLUMN_REPROT_LOCATION_LON));
				report.g3TrafficUpload = cur.getLong(cur.getColumnIndex(COLUMN_REPROT_3G_TRAFFIC_UP));
				report.g3TrafficDownload = cur.getLong(cur.getColumnIndex(COLUMN_REPROT_3G_TRAFFIC_DOWN));
				report.wifiTrafficUpload = cur.getLong(cur.getColumnIndex(COLUMN_REPROT_WIFI_TRAFFIC_UP));
				report.wifiTrafficDownload = cur.getLong(cur.getColumnIndex(COLUMN_REPROT_WIFI_TRAFFIC_DOWN));
				
				reports.put(report.time, report);
			} catch (Exception e) {
				continue;
			}
		}
		cur.close();
		cur = null;
		return reports;
	}
	
	/**
	 * add time data
	 * @param policyList
	 */
	public void addTime2DB(List<PolicyModel> policyList) {
//		openDatabase();
		for (PolicyModel policy : policyList) {
			List<TimeAndDate> timeList = policy.getTimeList();
			List<_3GModel> g3List = policy.getLocation3GList();
			List<GeoLocation> geoList = policy.getGeoLocationList();
			CommonUtil.handleDefaultSSID(policy);
			List<NetworkModel> netList = policy.getAccessNetworkList();
			if(netList.isEmpty()) {
				continue;
			}
			Integer policy_priority = policy.getRulePriority();
			ProfileType type = CommonUtil.encodeProfileType(netList);
			
			ContentValues cv;
			
			for (_3GModel g3M : g3List) {
				//delete the repetitive data
				mDb.execSQL("delete from " + DB_TABLE_POLICY_TIME
								+ " where (" + COLUMN_POLICY_3G_GERAN_CI + " = " + g3M.getCid()
								+ " and " + COLUMN_POLICY_3G_PLMN + " = " + g3M.getPlmn()
								+ " and " + COLUMN_POLICY_3G_LAC + " = " + g3M.getLac()
								+ " and " + COLUMN_POLICY_PRIORITY_JOIN + " = " + policy_priority
								+ ")");
				
				for (TimeAndDate timeAndDate : timeList) {
					try {
					cv = new ContentValues();
					cv.put(COLUMN_POLICY_TIME_START, String.valueOf(timeAndDate.getStartTime()));
					cv.put(COLUMN_POLICY_TIME_END, String.valueOf(timeAndDate.getEndTime()));
					cv.put(COLUMN_POLICY_DATE_START, String.valueOf(timeAndDate.getStartDate()));
					cv.put(COLUMN_POLICY_DATE_END, String.valueOf(timeAndDate.getEndDate()));
					cv.put(COLUMN_POLICY_PRIORITY_JOIN, policy_priority);
					cv.put(COLUMN_POLICY_NETWORK_TYPE, type.toString());
					cv.put(COLUMN_POLICY_3G_GERAN_CI, g3M.getCid());
					cv.put(COLUMN_POLICY_3G_PLMN, g3M.getPlmn());
					cv.put(COLUMN_POLICY_3G_LAC, g3M.getLac());
					
					// set max size
					Cursor cursor = mDb.rawQuery("select " + COLUMN_POLICY_ID + " from " + DB_TABLE_POLICY_TIME, null);
					if(cursor != null) {
						int size = cursor.getCount();
						if(size >= POLICY_MAX_SIZE) {
							cursor.moveToNext();
							int index = cursor.getInt(cursor.getColumnIndex(COLUMN_POLICY_ID));
							mDb.execSQL("delete from " + DB_TABLE_POLICY_TIME + " where "
									+ COLUMN_POLICY_ID + " = " + index);
						}
						cursor.close();
						cursor = null;
					}
					
					mDb.insertOrThrow(DB_TABLE_POLICY_TIME, null, cv);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					
				}
			}
			
			for (GeoLocation geo : geoList) {
				//delete the repetitive data
				String deleteSql = String.format("delete from " + DB_TABLE_POLICY_TIME
						+ " where (" + COLUMN_POLICY_GEO_LAT + "='%1$s'"
						+ " and " + COLUMN_POLICY_GEO_LON + "='%2$s'"
						+ " and " + COLUMN_POLICY_GEO_RADIUS + "='%3$s'"
						+ " and " + COLUMN_POLICY_PRIORITY_JOIN + "='%4$s'"
						+ ")", new Object[]{String.valueOf(geo.getLatitude()), String.valueOf(geo.getLongtitude()), 
											String.valueOf(geo.getRadius()),
							policy_priority});
				mDb.execSQL(deleteSql);
				
				for (TimeAndDate timeAndDate : timeList) {
					try {
					cv = new ContentValues();
					cv.put(COLUMN_POLICY_TIME_START, timeAndDate.getStartTime());
					cv.put(COLUMN_POLICY_TIME_END, timeAndDate.getEndTime());
					cv.put(COLUMN_POLICY_DATE_START, timeAndDate.getStartDate());
					cv.put(COLUMN_POLICY_DATE_END, timeAndDate.getEndDate());
					cv.put(COLUMN_POLICY_PRIORITY_JOIN, policy_priority);
					cv.put(COLUMN_POLICY_NETWORK_TYPE, type.toString());
					cv.put(COLUMN_POLICY_GEO_LAT, String.valueOf(geo.getLatitude()));
					cv.put(COLUMN_POLICY_GEO_LON, String.valueOf(geo.getLongtitude()));
					cv.put(COLUMN_POLICY_GEO_RADIUS, geo.getRadius());
					
					// set max size
					Cursor cursor = mDb.rawQuery("select " + COLUMN_POLICY_ID + " from " + DB_TABLE_POLICY_TIME, null);
					if(cursor != null) {
						int size = cursor.getCount();
						if(size >= POLICY_MAX_SIZE) {//delete the top record
							cursor.moveToNext();
							int index = cursor.getInt(cursor.getColumnIndex(COLUMN_POLICY_ID));
							mDb.execSQL("delete from " + DB_TABLE_POLICY_TIME + " where "
									+ COLUMN_POLICY_ID + " = " + index);
						}
						cursor.close();
						cursor = null;
					}
					
					mDb.insertOrThrow(DB_TABLE_POLICY_TIME, null, cv);
					} catch (Exception e) {
//						Log.e("PolicyDB", e.getMessage());
						continue;
					}
				}
			}
		}
	}
	
	/**
	 * get policy filter by location
	 * @param plmn
	 * @param lac
	 * @param cellid
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public Set<PolicyModel> filterByLocation(int plmn, int lac, int cellid, double latitude, double longitude) {
		Log.d(TAG, "cellid: " + cellid + "  latitude: " + latitude + " longitude:" + longitude);
		Set<PolicyModel> list = new LinkedHashSet<PolicyModel>();
		
		//search by GEO
		String sql2 = "select * from "
				+ DB_TABLE_POLICY_TIME + " where (" + COLUMN_POLICY_GEO_LAT + " is not null and "
				+ COLUMN_POLICY_GEO_LAT + " is not null and "
				+ COLUMN_POLICY_GEO_RADIUS + " is not null)";
		Cursor cur2 = mDb.rawQuery(sql2, null);
		if(cur2 != null && longitude > -1 && latitude > -1) {
			while (cur2.moveToNext()) {
				try {
					double lat = Double.valueOf(cur2.getString(cur2.getColumnIndex(COLUMN_POLICY_GEO_LAT)));
					double lon = Double.valueOf(cur2.getString(cur2.getColumnIndex(COLUMN_POLICY_GEO_LON)));
					long rad = cur2.getLong(cur2.getColumnIndex(COLUMN_POLICY_GEO_RADIUS));
					boolean isInRange = LocationUtil.isInRange(lon, lat, longitude, latitude, rad);
					if(!isInRange) {
						continue;
					}
					
					PolicyModel policy = new PolicyModel();
					GeoLocation geo = new GeoLocation();
					geo.setLatitude(lat);
					geo.setLongtitude(lon);
					geo.setRadius(rad);
					ArrayList<GeoLocation> geoList = new ArrayList<PolicyModel.GeoLocation>();
					geoList.add(geo);
					policy.setGeoLocationList(geoList);
					
					TimeAndDate t_d = new TimeAndDate();
					t_d.setStartTime(cur2.getInt(cur2.getColumnIndex(COLUMN_POLICY_TIME_START)));
					t_d.setEndTime(cur2.getInt(cur2.getColumnIndex(COLUMN_POLICY_TIME_END)));
					t_d.setStartDate(cur2.getInt(cur2.getColumnIndex(COLUMN_POLICY_DATE_START)));
					t_d.setEndDate(cur2.getInt(cur2.getColumnIndex(COLUMN_POLICY_DATE_END)));
					ArrayList<TimeAndDate> timeList = new ArrayList<PolicyModel.TimeAndDate>();
					timeList.add(t_d);
					policy.setTimeList(timeList);
					
					policy.setRulePriority(cur2.getInt(cur2.getColumnIndex(COLUMN_POLICY_PRIORITY_JOIN)));
					
					String type = cur2.getString(cur2.getColumnIndex(COLUMN_POLICY_NETWORK_TYPE));
					List<NetworkModel> nets = CommonUtil.decodeProfileType(
							ProfileType.valueOf(type));
					policy.setAccessNetworkList(nets);
					policy.pickByCID = false;
					list.add(policy);
				}catch (Exception e) {
					Log.e(TAG, e.getMessage());
					continue;
				}
			}
			cur2.close();
			cur2 = null;
		}
		
		
		StringBuffer sb = new StringBuffer("select * from "
				+ DB_TABLE_POLICY_TIME + " where (");
		if(plmn == -1) {
			return list;
		} else {
			sb.append(COLUMN_POLICY_3G_PLMN + "='%1$s' or " + COLUMN_POLICY_3G_PLMN + "=-1)");
		}
		if(lac == -1) {
			return list;
		}else {
			sb.append(" and (" + COLUMN_POLICY_3G_LAC + "='%2$s' or " + COLUMN_POLICY_3G_LAC + "=-1)");
		}
		if(cellid == -1) {
			return list;
		} else {
			sb.append(" and (" + COLUMN_POLICY_3G_GERAN_CI + "='%3$s' or " + COLUMN_POLICY_3G_GERAN_CI + "=-1)");
		}
		
		// search by cell id
		String sql1 = String.format(sb.toString(), new Object[] {plmn, lac, cellid});
		Cursor cur1 = mDb.rawQuery(sql1, null);
		if(cur1 != null) {
			while (cur1.moveToNext()) {
				try {
					PolicyModel policy = new PolicyModel();
					
					String latStr = cur1.getString(cur1.getColumnIndex(COLUMN_POLICY_GEO_LAT));
					String lonStr = cur1.getString(cur1.getColumnIndex(COLUMN_POLICY_GEO_LON));
					long radius = cur1.getLong(cur1.getColumnIndex(COLUMN_POLICY_GEO_RADIUS));
					
					if(latStr != null && lonStr != null && radius > 0) {
						GeoLocation geo = new GeoLocation();
						geo.setLatitude(Double.valueOf(latStr));
						geo.setLongtitude(Double.valueOf(lonStr));
						geo.setRadius(radius);
						ArrayList<GeoLocation> geoList = new ArrayList<PolicyModel.GeoLocation>();
						geoList.add(geo);
						policy.setGeoLocationList(geoList);
					}

					TimeAndDate t_d = new TimeAndDate();
					t_d.setStartTime(cur1.getInt(cur1.getColumnIndex(COLUMN_POLICY_TIME_START)));
					t_d.setEndTime(cur1.getInt(cur1.getColumnIndex(COLUMN_POLICY_TIME_END)));
					t_d.setStartDate(cur1.getInt(cur1.getColumnIndex(COLUMN_POLICY_DATE_START)));
					t_d.setEndDate(cur1.getInt(cur1.getColumnIndex(COLUMN_POLICY_DATE_END)));
					ArrayList<TimeAndDate> timeList = new ArrayList<PolicyModel.TimeAndDate>();
					timeList.add(t_d);
					policy.setTimeList(timeList);
					
					policy.setRulePriority(cur1.getInt(cur1.getColumnIndex(COLUMN_POLICY_PRIORITY_JOIN)));
					
					String type = cur1.getString(cur1.getColumnIndex(COLUMN_POLICY_NETWORK_TYPE));
					List<NetworkModel> nets = CommonUtil.decodeProfileType(
							ProfileType.valueOf(type));
					policy.setAccessNetworkList(nets);
					policy.pickByCID = true;
					list.add(policy);
				}catch (Exception e) {
					Log.e(TAG, e.getMessage());
					continue;
				}
			}
			cur1.close();
			cur1 = null;
		}
		return list;
	}
	
	/**
	 * delete expired policy
	 */
	public void deleteExpiredPolicy() {
		try {
			openDatabase();
		
			int currentTime = TimeUtil.getNowTime();
			int currentDate = TimeUtil.getNowDate();
		
			String sql = "delete from " + DB_TABLE_POLICY_TIME + " where ("
					+ COLUMN_POLICY_TIME_END + "<'%1$s'" + " and "
					+ COLUMN_POLICY_DATE_END + ">0 and "
					+ COLUMN_POLICY_DATE_END + "='%2$s'" + ") or ("
					+ COLUMN_POLICY_DATE_END + ">0 and "
					+ COLUMN_POLICY_DATE_END + "<'%2$s')";
			String sql2 = String.format(sql, new Object[] { currentTime,
					currentDate });
			mDb.execSQL(sql2);
//			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
//		return false;
	}

	/**
	 * delete error policy
	 */
	public boolean deleteErrorPolicy(PolicyModel policy) {
		TimeAndDate t_d = policy.getTimeList().get(0);
		final int startTime = t_d.getStartTime();
		final int startDate = t_d.getStartDate();
		final int endTime = t_d.getEndTime();
		final int endDate = t_d.getEndDate();
		try {
			String sql = String.format("delete from " + DB_TABLE_POLICY_TIME
					+ " where (" + COLUMN_POLICY_TIME_START + "='%1$s'"
					+ " and " + COLUMN_POLICY_TIME_END + "='%2$s'" + " and "
					+ COLUMN_POLICY_DATE_START + "='%3$s'" + " and "
					+ COLUMN_POLICY_DATE_END + "='%4$s')", new Object[] {
					startTime, endTime, startDate, endDate });
			mDb.execSQL(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/**
	 * delete spec hotspot list
	 * @param hotspotList
	 */
	private void clearPreviousHotspot(List<HotspotModel> hotspotList) {
		for (HotspotModel hotspotModel : hotspotList) {
			try {
				final long cid = hotspotModel.getG3_cid();
				final int lac = hotspotModel.getG3_lac();
				final int pl = hotspotModel.getG3_plmn();
				mDb.execSQL("delete from " + DB_TABLE_HOTSPOT
						+ " where (" + COLUMN_HOTSPOT_3G_PLMN + " = " + pl
						+ " and " + COLUMN_HOTSPOT_3G_LAC + " = " + lac
						+ " and " + COLUMN_HOTSPOT_3G_GERAN_CI + " = " + cid
						+ ")");
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	/**
	 * add hotspot data
	 * @return
	 */
	public void addHotspot(List<HotspotModel> hotspotList) {
		clearPreviousHotspot(hotspotList);
		ContentValues cv;
		for (HotspotModel hotspotModel : hotspotList) {
			try {
				final int type = hotspotModel.getAccessNetworkType();
				final long cid = hotspotModel.getG3_cid();
				final int lac = hotspotModel.getG3_lac();
				final int pl = hotspotModel.getG3_plmn();
				final double lat = hotspotModel.getLatitude();
				final double lon = hotspotModel.getLongitude();
				final long radius = hotspotModel.getRadius();
				final String ref = hotspotModel.getAccessNetworkInformationRef();
				final int plmn = hotspotModel.getPlmn();
				final String ssid = hotspotModel.getSsid();
				
				cv = new ContentValues();
				cv.put(COLUMN_HOTSPOT_NET_TYPE, type);
				cv.put(COLUMN_HOTSPOT_3G_PLMN, pl);
				cv.put(COLUMN_HOTSPOT_3G_LAC, lac);
				cv.put(COLUMN_HOTSPOT_3G_GERAN_CI, cid);
				cv.put(COLUMN_HOTSPOT_GEO_LAT, lat);
				cv.put(COLUMN_HOTSPOT_GEO_LON, lon);
				cv.put(COLUMN_HOTSPOT_GEO_RADIUS, radius);
				cv.put(COLUMN_HOTSPOT_NET_REF, ref);
				cv.put(COLUMN_HOTSPOT_PLMN, plmn);
				cv.put(COLUMN_HOTSPOT_SSID, ssid);
				
				// set max size
				Cursor cursor = mDb.rawQuery("select " + COLUMN_HOTSPOT_ID + " from " + DB_TABLE_HOTSPOT, null);
				if(cursor != null) {
					int size = cursor.getCount();
					if(size >= HOTSPOT_MAX_SIZE) {
						cursor.moveToNext();
						int index = cursor.getInt(cursor.getColumnIndex(COLUMN_HOTSPOT_ID));
						mDb.execSQL("delete from " + DB_TABLE_HOTSPOT + " where "
								+ COLUMN_HOTSPOT_ID + " = " + index);
					}
					cursor.close();
					cursor = null;
				}
				
				mDb.insertOrThrow(DB_TABLE_HOTSPOT, null, cv);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	/**
	 * get hotspot data by location
	 * @param plmn
	 * @param lac
	 * @param cellid
	 * @return
	 */
	public List<HotspotModel> readHotspotList(int plmn, int lac, int cellid) {
		List<HotspotModel> list = new ArrayList<HotspotModel>();
		String sql;
//		if(!AppApplication.requestServer) {
			sql = String.format("select * from " + DB_TABLE_HOTSPOT + " where ("
					+ COLUMN_HOTSPOT_3G_PLMN + "='%1$s' or " + COLUMN_HOTSPOT_3G_PLMN + "=-1)"
					+ " and (" + COLUMN_HOTSPOT_3G_LAC + "='%2$s' or " + COLUMN_HOTSPOT_3G_LAC + "=-1)"
					+ " and (" + COLUMN_HOTSPOT_3G_GERAN_CI + "='%3$s' or " + COLUMN_HOTSPOT_3G_GERAN_CI + "=-1)"
					, new Object[]{plmn, lac, cellid});
//		} else {
//			sql = String.format("select * from " + DB_TABLE_HOTSPOT + " where ("
//					+ COLUMN_HOTSPOT_3G_PLMN + "='%1$s')"
//					+ " and (" + COLUMN_HOTSPOT_3G_LAC + "='%2$s')"
//					+ " and (" + COLUMN_HOTSPOT_3G_GERAN_CI + "='%3$s')"
//					, new Object[]{plmn, lac, cellid});
//		}
		
		Cursor cur = mDb.rawQuery(sql, null);
		if(cur != null) {
			while (cur.moveToNext()) {
				try {
					HotspotModel hotspot = new HotspotModel();
					hotspot.setAccessNetworkType(cur.getInt(cur.getColumnIndex(COLUMN_HOTSPOT_NET_TYPE)));
					hotspot.setG3_plmn(cur.getInt(cur.getColumnIndex(COLUMN_HOTSPOT_3G_PLMN)));
					hotspot.setG3_lac(cur.getInt(cur.getColumnIndex(COLUMN_HOTSPOT_3G_LAC)));
					hotspot.setG3_cid(cur.getLong(cur.getColumnIndex(COLUMN_HOTSPOT_3G_GERAN_CI)));
					hotspot.setLatitude(cur.getDouble(cur.getColumnIndex(COLUMN_HOTSPOT_GEO_LAT)));
					hotspot.setLongitude(cur.getDouble(cur.getColumnIndex(COLUMN_HOTSPOT_GEO_LON)));
					hotspot.setRadius(cur.getLong(cur.getColumnIndex(COLUMN_HOTSPOT_GEO_RADIUS)));
					hotspot.setAccessNetworkInformationRef(cur.getString(cur.getColumnIndex(COLUMN_HOTSPOT_NET_REF)));
					hotspot.setPlmn(cur.getInt(cur.getColumnIndex(COLUMN_HOTSPOT_PLMN)));
					hotspot.setSsid(cur.getString(cur.getColumnIndex(COLUMN_HOTSPOT_SSID)));
					hotspot.setLatitude(39.902761d);
					hotspot.setLongitude(116.358047d);
					list.add(hotspot);
				}catch (Exception e) {
					Log.e(TAG, e.getMessage());
					continue;
				}
			}
			cur.close();
			cur = null;
		}
		return list;
	}
	
	/**
	 * get app list data
	 * @return
	 */
	public List<AppModel> readAppList() {
		List<AppModel> list = new ArrayList<AppModel>();
		String sql = "select * from " + DB_TABLE_APP;
		Cursor cur = mDb.rawQuery(sql, null);
		if(cur != null) {
			while (cur.moveToNext()) {
				AppModel app = new AppModel();
				app.setAppLabel(cur.getString(cur.getColumnIndex(COLUMN_APP_LABEL)));
				app.setPkgName(cur.getString(cur.getColumnIndex(COLUMN_APP_PACKET)));
				app.setNetworkPri(cur.getInt(cur.getColumnIndex(COLUMN_APP_NETWORK_SET)));
				list.add(app);
			}
			cur.close();
			cur = null;
		}
		
		return list;
	}
	
	/**
	 * add app list data
	 * @param list
	 */
	public void addAppList(List<AppModel> list) {
		ContentValues cv;
		mDb.execSQL("delete from " + DB_TABLE_APP);
		for (AppModel appModel : list) {
			try {
				final String label = appModel.getAppLabel();
				final String pack = appModel.getPkgName();
				final int prio = appModel.getNetworkPri();
				cv = new ContentValues();
				cv.put(COLUMN_APP_LABEL, label);
				cv.put(COLUMN_APP_PACKET, pack);
				cv.put(COLUMN_APP_NETWORK_SET, prio);
				
				// set max size
				Cursor cursor = mDb.rawQuery("select " + COLUMN_APP_ID + " from " + DB_TABLE_APP, null);
				if(cursor != null) {
					int size = cursor.getCount();
					if(size >= APP_MAX_SIZE) {
						cursor.moveToNext();
						int index = cursor.getInt(cursor.getColumnIndex(COLUMN_APP_ID));
						mDb.execSQL("delete from " + DB_TABLE_APP + " where "
								+ COLUMN_APP_ID + " = " + index);
					}
					cursor.close();
					cursor = null;
				}
				
//				int num = mDb.update(DB_TABLE_APP, cv, COLUMN_APP_PACKET + "=?", new String[] {pack});
//				if(num < 0) {
					mDb.insertOrThrow(DB_TABLE_APP, null, cv);
//				}
			}catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * data base utility
	 *
	 */
	private class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_USER_PREFERRED);
			db.execSQL(CREATE_TABLE_POLICY_TIME);
			db.execSQL(CREATE_TABLE_HOTSPOT);
			db.execSQL(CREATE_TABLE_LOG);
			db.execSQL(CREATE_TABLE_REPORT);
			db.execSQL(CREATE_TABLE_APP);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(getClass().toString(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_USER_PREFERRED);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_POLICY_TIME);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_HOTSPOT);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_LOG);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_REPORT);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_APP);
			onCreate(db);
		}
		
		
	}
	
	@Deprecated
	public class TimeAndLocation {
		public TimeAndDate t_d; 
		public int priority;
		public String type;
		public boolean pickByCID;
		@Override
		public String toString() {
			return "TimeAndLocation [t_d=" + t_d + ", priority=" + priority
					+ ", type=" + type + "]";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + priority;
			result = prime * result + ((t_d == null) ? 0 : t_d.hashCode());
			result = prime * result + ((type == null) ? 0 : ProfileType.valueOf(type).ordinal());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TimeAndLocation other = (TimeAndLocation) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (priority != other.priority)
				return false;
			if (t_d == null) {
				if (other.t_d != null)
					return false;
			} else if (!t_d.equals(other.t_d))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
		private DBAdpter getOuterType() {
			return DBAdpter.this;
		}
	}
}
