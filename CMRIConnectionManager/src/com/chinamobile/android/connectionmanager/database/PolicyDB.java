package com.chinamobile.android.connectionmanager.database;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel.GeoLocation;
import com.chinamobile.android.connectionmanager.model.PolicyModel.TimeAndDate;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.model._3GModel;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.LocationUtil;

@Deprecated
public class PolicyDB {
	public static enum ProfileType {
		CMCC, CMCCAUTO, G3,
		CMCC_CMCCAUTO, CMCCAUTO_CMCC, CMCC_G3, CMCCAUTO_G3, G3_CMCC, G3_CMCCAUTO,
		CMCC_CMCCAUTO_G3, CMCC_G3_CMCCAUTO, CMCCAUTO_CMCC_G3, CMCCAUTO_G3_CMCC, G3_CMCC_CMCCAUTO, G3_CMCCAUTO_CMCC
	}

	final String DB_TABLE_POLICY = "policy";
	final String DB_TABLE_POLICY_NET = "policy_net";
	final String DB_TABLE_POLICY_TIME = "policy_time";
	final String DB_TABLE_POLICY_LOCATION_3G = "policy_location_3g";
	final String DB_TABLE_POLICY_LOCATION_WLAN = "policy_location_wlan";
	final String DB_TABLE_POLICY_LOCATION_GEO = "policy_location_geo";

	// policy column name
	final String COLUMN_ID = "id";
	final String COLUMN_POLICY_PRIORITY = "policy_priority";
	final String COLUMN_POLICY_NET_NAME = "policy_net_name";
	final String COLUMN_POLICY_NET_SSID = "policy_net_ssid";
	final String COLUMN_POLICY_NET_PRIORITY = "policy_net_prio";
	final String COLUMN_POLICY_NET_SEC_SSID = "policy_net_sec_ssid";
	final String COLUMN_POLICY_3G_PLMN = "policy_3g_plmn";
	final String COLUMN_POLICY_3G_TAC = "policy_3g_tac";
	final String COLUMN_POLICY_3G_LAC = "policy_3g_lac";
	final String COLUMN_POLICY_3G_GERAN_CI = "policy_3g_grean_ci";
	final String COLUMN_POLICY_3G_UTRAN_CI = "policy_3g_utran_ci";
	final String COLUMN_POLICY_3G_EUTRA_CI = "policy_3g_eutra_ci";
	final String COLUMN_POLICY_WLAN_HESSID = "policy_wlan_hessid";
	final String COLUMN_POLICY_WLAN_SSID = "policy_wlan_ssid";
	final String COLUMN_POLICY_WLAN_BSSID = "policy_wlan_bssid";
	final String COLUMN_POLICY_GEO_LAT = "policy_geo_lat";
	final String COLUMN_POLICY_GEO_LON = "policy_geo_lon";
	final String COLUMN_POLICY_GEO_RADIUS = "policy_geo_radius";
	final String COLUMN_POLICY_TIME_START = "policy_time_start";
	final String COLUMN_POLICY_TIME_END = "policy_time_end";
	final String COLUMN_POLICY_DATE_START = "policy_date_start";
	final String COLUMN_POLICY_DATE_END = "policy_date_end";
	final String COLUMN_POLICY_PRIORITY_JOIN = "join_policy_priority";
	final String COLUMN_POLICY_UID = "policy_uid";
	final String COLUMN_POLICY_CID = "policy_cell_id";
	final String COLUMN_POLICY_KEY = "policy_key";
	final String COLUMN_POLICY_NETWORK_TYPE = "policy_network_type";
	final String COLUMN_POLICY_GEO_LAT_MAX = "policy_geo_lat_max";
	final String COLUMN_POLICY_GEO_LON_MAX = "policy_geo_lon_max";
	final String COLUMN_POLICY_GEO_LAT_MIN = "policy_geo_lat_min";
	final String COLUMN_POLICY_GEO_LON_MIN = "policy_geo_lon_min";
	

	final String CREATE_TABLE_POLICY = "create table if not exists "
			+ DB_TABLE_POLICY
			+ " ("
			+ COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_POLICY_PRIORITY
			+ " varchar, "
			+ COLUMN_POLICY_NET_NAME
			+ " varchar, "
			+ COLUMN_POLICY_NET_SSID
			+ " varchar ,"
			+ COLUMN_POLICY_NET_PRIORITY
			+ " varchar, "
			+ COLUMN_POLICY_NET_SEC_SSID
			+ " varchar "
			+ COLUMN_POLICY_3G_PLMN
			+ " varchar, "
			+ COLUMN_POLICY_3G_TAC
			+ " varchar, "
			+ COLUMN_POLICY_3G_LAC
			+ " varchar, "
			+ COLUMN_POLICY_3G_GERAN_CI
			+ " varchar, "
			+ COLUMN_POLICY_3G_UTRAN_CI
			+ " varchar, "
			+ COLUMN_POLICY_3G_EUTRA_CI
			+ " varchar, "
			+ COLUMN_POLICY_WLAN_HESSID
			+ " varchar, "
			+ COLUMN_POLICY_WLAN_SSID
			+ " varchar, "
			+ COLUMN_POLICY_WLAN_BSSID
			+ " varchar, "
			+ COLUMN_POLICY_GEO_LAT
			+ " varchar, "
			+ COLUMN_POLICY_GEO_LON
			+ " varchar, "
			+ COLUMN_POLICY_GEO_RADIUS
			+ " varchar, "
			+ COLUMN_POLICY_TIME_START
			+ " varchar, "
			+ COLUMN_POLICY_TIME_END
			+ " varchar, "
			+ COLUMN_POLICY_DATE_START
			+ " varchar, " + COLUMN_POLICY_DATE_END + " varchar " + ");";

	final String CREATE_TABLE_POLICY_NET = "create table if not exists "
			+ DB_TABLE_POLICY_NET
			+ " ("
			+ COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_POLICY_NET_NAME
			+ " varchar, "
			+ COLUMN_POLICY_NET_SSID
			+ " varchar ,"
			+ COLUMN_POLICY_NET_PRIORITY
			+ " varchar, "
			+ COLUMN_POLICY_NET_SEC_SSID
			+ " varchar, "
			+ COLUMN_POLICY_PRIORITY_JOIN
			+ " varchar, "
			+ COLUMN_POLICY_UID
			+ " varchar " + ");";

	

	final String CREATE_TABLE_POLICY_LOCATION_3G = "create table if not exists "
			+ DB_TABLE_POLICY_LOCATION_3G
			+ " ("
			+ COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_POLICY_3G_PLMN
			+ " varchar, "
			+ COLUMN_POLICY_3G_TAC
			+ " varchar, "
			+ COLUMN_POLICY_3G_LAC
			+ " varchar, "
			+ COLUMN_POLICY_3G_GERAN_CI
			+ " varchar, "
			+ COLUMN_POLICY_3G_UTRAN_CI
			+ " varchar, "
			+ COLUMN_POLICY_3G_EUTRA_CI
			+ " varchar, "
			+ COLUMN_POLICY_PRIORITY_JOIN
			+ " varchar, "
			+ COLUMN_POLICY_UID
			+ " varchar " + ");";

	final String CREATE_TABLE_POLICY_LOCATION_WLAN = "create table if not exists "
			+ DB_TABLE_POLICY_LOCATION_WLAN
			+ " ("
			+ COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_POLICY_WLAN_HESSID
			+ " varchar, "
			+ COLUMN_POLICY_WLAN_SSID
			+ " varchar, "
			+ COLUMN_POLICY_WLAN_BSSID
			+ " varchar, "
			+ COLUMN_POLICY_PRIORITY_JOIN
			+ " varchar, "
			+ COLUMN_POLICY_UID + " varchar " + ");";

	final String CREATE_TABLE_POLICY_LOCATION_GEO = "create table if not exists "
			+ DB_TABLE_POLICY_LOCATION_GEO
			+ " ("
			+ COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_POLICY_GEO_LAT
			+ " varchar, "
			+ COLUMN_POLICY_GEO_LON
			+ " varchar, "
			+ COLUMN_POLICY_GEO_RADIUS
			+ " varchar, "
			+ COLUMN_POLICY_PRIORITY_JOIN
			+ " varchar, "
			+ COLUMN_POLICY_UID
			+ " varchar " + ");";
	
	private SQLiteDatabase mDb;
	
	PolicyDB(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	public void add3GLocation2DB(PolicyModel policy, String uid) {
		List<_3GModel> list3G = policy.getLocation3GList();
		String policy_priority = String.valueOf(policy.getRulePriority());
		Map<Pair<String, String>, _3GModel> local3gCache = read3GLocationFromDB();
		
		for (Entry<Pair<String, String>, _3GModel> entry : local3gCache.entrySet()) {
			String first = entry.getKey().first;
			String second = entry.getKey().second;
			if(first.equals(uid) && second.equals(policy_priority)) {
				mDb.execSQL("delete from " + DB_TABLE_POLICY_LOCATION_3G
						+ " where " + COLUMN_POLICY_PRIORITY_JOIN + " = " + policy_priority
						+ " and " + COLUMN_POLICY_UID + " = " + uid);
			}
		}
		
		ContentValues cv;
		for (_3GModel _3gModel : list3G) {
			cv = new ContentValues();
			cv.put(COLUMN_POLICY_3G_PLMN, _3gModel.getPlmn());
			cv.put(COLUMN_POLICY_3G_TAC, _3gModel.getTac());
			cv.put(COLUMN_POLICY_3G_LAC, _3gModel.getLac());
			cv.put(COLUMN_POLICY_3G_GERAN_CI, _3gModel.getCid());
			cv.put(COLUMN_POLICY_3G_UTRAN_CI, _3gModel.getUtran_cid());
			cv.put(COLUMN_POLICY_3G_EUTRA_CI, _3gModel.getEutra_cid());
			cv.put(COLUMN_POLICY_PRIORITY_JOIN, policy_priority);
			cv.put(COLUMN_POLICY_UID, uid);
			mDb.insertOrThrow(DB_TABLE_POLICY_LOCATION_3G, null, cv);
		}
	}
	
	public Map<Pair<String, String>, _3GModel> read3GLocationFromDB() {
		Map<Pair<String, String>, _3GModel> _3g_list = new IdentityHashMap<Pair<String, String>, _3GModel>();
		Cursor cur = mDb.query(DB_TABLE_POLICY_LOCATION_3G, null, null, null, null,
				null, COLUMN_ID);
		while (cur.moveToNext()) {
			try {
				_3GModel _3g = new _3GModel();
				_3g.setPlmn(Integer.parseInt(cur.getString(cur.getColumnIndex(COLUMN_POLICY_3G_PLMN))));
				_3g.setTac(Long.parseLong(cur.getString(cur.getColumnIndex(COLUMN_POLICY_3G_TAC))));
				_3g.setLac(Long.parseLong(cur.getString(cur.getColumnIndex(COLUMN_POLICY_3G_LAC))));
				_3g.setCid(Long.parseLong(cur.getString(cur.getColumnIndex(COLUMN_POLICY_3G_GERAN_CI))));
				_3g.setUtran_cid(Long.parseLong(cur.getString(cur.getColumnIndex(COLUMN_POLICY_3G_PLMN))));
				_3g.setEutra_cid(Long.parseLong(cur.getString(cur.getColumnIndex(COLUMN_POLICY_3G_PLMN))));
				final String policy_priority = cur.getString(cur.getColumnIndex(COLUMN_POLICY_PRIORITY_JOIN));
				final String uid = cur.getString(cur.getColumnIndex(COLUMN_POLICY_UID));
				Pair<String, String> pair = Pair.create(uid, policy_priority);
				_3g_list.put(pair, _3g);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return _3g_list;
	}
	
	public void addWlanLocation2Db(PolicyModel policy, String uid) {
		List<WifiModel> listWlan = policy.getLocationWlanList();
		String policy_priority = String.valueOf(policy.getRulePriority());
		Map<Pair<String, String>, WifiModel> localWlanCache = readWlanLocationFromDB();
		for (Entry<Pair<String, String>, WifiModel> entry : localWlanCache.entrySet()) {
			String first = entry.getKey().first;
			String second = entry.getKey().second;
			if(first.equals(uid) && second.equals(policy_priority)) {
				mDb.execSQL("delete from " + DB_TABLE_POLICY_LOCATION_WLAN
						+ " where " + COLUMN_POLICY_PRIORITY_JOIN + " = " + policy_priority
						+ " and " + COLUMN_POLICY_UID + " = " + uid);
			}
		}
		
		ContentValues cv;
		for (WifiModel wifi : listWlan) {
			cv = new ContentValues();
			cv.put(COLUMN_POLICY_WLAN_HESSID, wifi.getHESSID());
			cv.put(COLUMN_POLICY_WLAN_SSID, wifi.getSSID());
			cv.put(COLUMN_POLICY_WLAN_BSSID, wifi.getBSSID());
			cv.put(COLUMN_POLICY_PRIORITY_JOIN, policy_priority);
			cv.put(COLUMN_POLICY_UID, uid);
			mDb.insertOrThrow(DB_TABLE_POLICY_LOCATION_WLAN, null, cv);
		}
	}
	
	public Map<Pair<String, String>, WifiModel> readWlanLocationFromDB() {
		Map<Pair<String, String>, WifiModel> wlan_list = new IdentityHashMap<Pair<String, String>, WifiModel>();
		Cursor cur = mDb.query(DB_TABLE_POLICY_LOCATION_WLAN, null, null, null, null,
				null, COLUMN_ID);
		while (cur.moveToNext()) {
			try {
				WifiModel wifi = new WifiModel();
				wifi.setHESSID(cur.getString(cur.getColumnIndex(COLUMN_POLICY_WLAN_HESSID)));
				wifi.setSSID(cur.getString(cur.getColumnIndex(COLUMN_POLICY_WLAN_SSID)));
				wifi.setBSSID(cur.getString(cur.getColumnIndex(COLUMN_POLICY_WLAN_BSSID)));
				final String policy_priority = cur.getString(cur.getColumnIndex(COLUMN_POLICY_PRIORITY_JOIN));
				final String uid = cur.getString(cur.getColumnIndex(COLUMN_POLICY_UID));
				Pair<String, String> pair = Pair.create(uid, policy_priority);
				wlan_list.put(pair, wifi);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return wlan_list;
	}
	
	public void addGeoLocation2Db(PolicyModel policy, String uid) {
		List<GeoLocation> listGeo = policy.getGeoLocationList();
		String policy_priority = String.valueOf(policy.getRulePriority());
		Map<Pair<String, String>, GeoLocation> localGeoCache = readGeoLocationFromDB();
		for (Entry<Pair<String, String>, GeoLocation> entry : localGeoCache.entrySet()) {
			String first = entry.getKey().first;
			String second = entry.getKey().second;
			if(first.equals(uid) && second.equals(policy_priority)) {
				mDb.execSQL("delete from " + DB_TABLE_POLICY_LOCATION_GEO
						+ " where " + COLUMN_POLICY_PRIORITY_JOIN + " = " + policy_priority
						+ " and " + COLUMN_POLICY_UID + " = " + uid);
			}
		}
		
		ContentValues cv;
		for (GeoLocation geo : listGeo) {
			cv = new ContentValues();
			cv.put(COLUMN_POLICY_GEO_LAT, geo.getLatitude());
			cv.put(COLUMN_POLICY_GEO_LON, geo.getLongtitude());
			cv.put(COLUMN_POLICY_GEO_RADIUS, geo.getRadius());
			cv.put(COLUMN_POLICY_PRIORITY_JOIN, policy_priority);
			cv.put(COLUMN_POLICY_UID, uid);
			mDb.insertOrThrow(DB_TABLE_POLICY_LOCATION_GEO, null, cv);
		}
	}
	
	public Map<Pair<String, String>, GeoLocation> readGeoLocationFromDB() {
		Map<Pair<String, String>, GeoLocation> geo_list = new IdentityHashMap<Pair<String, String>, GeoLocation>();
		Cursor cur = mDb.query(DB_TABLE_POLICY_LOCATION_GEO, null, null, null, null,
				null, COLUMN_ID);
		while (cur.moveToNext()) {
			try {
				GeoLocation geo = new GeoLocation();
				geo.setLatitude(Double.valueOf(cur.getString(cur.getColumnIndex(COLUMN_POLICY_GEO_LAT))));
				geo.setLongtitude(Double.valueOf(cur.getString(cur.getColumnIndex(COLUMN_POLICY_GEO_LON))));
				geo.setRadius(Integer.parseInt(cur.getString(cur.getColumnIndex(COLUMN_POLICY_GEO_RADIUS))));
				final String policy_priority = cur.getString(cur.getColumnIndex(COLUMN_POLICY_PRIORITY_JOIN));
				final String uid = cur.getString(cur.getColumnIndex(COLUMN_POLICY_UID));
				Pair<String, String> pair = Pair.create(uid, policy_priority);
				geo_list.put(pair, geo);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return geo_list;
		
	}
	
	
	
	
	
	public void addNetwork2DB(PolicyModel policy, String uid) {
		List<NetworkModel> list = policy.getAccessNetworkList();
		String policy_priority = String.valueOf(policy.getRulePriority());
		
		Map<Pair<String, String>, ? extends NetworkModel> networkCache = readNetworkFromDB();
		
		for (Entry<Pair<String, String>, ? extends NetworkModel> entry : networkCache.entrySet()) {
			String first = entry.getKey().first;
			String second = entry.getKey().second;
			if(first.equals(uid) && second.equals(policy_priority)) {
				mDb.execSQL("delete from " + DB_TABLE_POLICY_NET
						+ " where " + COLUMN_POLICY_PRIORITY_JOIN + " = " + policy_priority
						+ " and " + COLUMN_POLICY_UID + " = " + uid);
			}
		}

		ContentValues cv;
		String name = null;
		String ssid = null;
		String sec_ssid = null;
		String prio = null;
		for (NetworkModel networkModel : list) {
			cv = new ContentValues();
			if(networkModel.getType() == NetworkModel.TYPE_WIFI) {
				WifiModel wifi = (WifiModel) networkModel;
				name = wifi.getName();
				ssid = wifi.getSSID();
				sec_ssid = wifi.getSecond_SSID();
				prio = String.valueOf(wifi.getPriority());
			} else if(networkModel.getType() == NetworkModel.TYPE_3G) {
				_3GModel _3g = (_3GModel) networkModel;
				name = _3g.getName();
				prio = String.valueOf(_3g.getPriority());
			}
			cv.put(COLUMN_POLICY_NET_NAME, name);
			cv.put(COLUMN_POLICY_NET_SSID, ssid);
			cv.put(COLUMN_POLICY_NET_SEC_SSID, sec_ssid);
			cv.put(COLUMN_POLICY_NET_PRIORITY, prio);
			cv.put(COLUMN_POLICY_PRIORITY_JOIN, policy_priority);
			cv.put(COLUMN_POLICY_UID, uid);
			mDb.insertOrThrow(DB_TABLE_POLICY_NET, null, cv);
		}
	}
	
	public Map<Pair<String, String>, NetworkModel> readNetworkFromDB() {
//		networkCache.clear();
		Map<Pair<String, String>, NetworkModel> networkList = new IdentityHashMap<Pair<String, String>, NetworkModel>();
		Cursor cur = mDb.query(DB_TABLE_POLICY_NET, null, null, null, null,
				null, COLUMN_ID);
		while (cur.moveToNext()) {
			try {
				final String name = cur.getString(cur.getColumnIndex(COLUMN_POLICY_NET_NAME));
				final String prio = cur.getString(cur.getColumnIndex(COLUMN_POLICY_NET_PRIORITY));
				final String ssid = cur.getString(cur.getColumnIndex(COLUMN_POLICY_NET_SSID));
				final String sec_ssid = cur.getString(cur.getColumnIndex(COLUMN_POLICY_NET_SEC_SSID));
				final String policy_priority = cur.getString(cur.getColumnIndex(COLUMN_POLICY_PRIORITY_JOIN));
				final String uid = cur.getString(cur.getColumnIndex(COLUMN_POLICY_UID));
				if(name != null && name.equals(NetworkModel.NAME_WLAN)) {
					WifiModel wifi = new WifiModel();
					wifi.setName(name);
					wifi.setPriority(Integer.parseInt(prio));
					wifi.setSSID(ssid);
					wifi.setSecond_SSID(sec_ssid);
					Pair<String, String> pair = Pair.create(uid, policy_priority);
					networkList.put(pair, wifi);
				} else if(name != null && name.equals(NetworkModel.NAME_3G)) {
					_3GModel _3g = new _3GModel();
					_3g.setName(name);
					_3g.setPriority(Integer.parseInt(prio));
					Pair<String, String> pair = Pair.create(uid, policy_priority);
					networkList.put(pair, _3g);
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
		return networkList;
	}
	
	
}
