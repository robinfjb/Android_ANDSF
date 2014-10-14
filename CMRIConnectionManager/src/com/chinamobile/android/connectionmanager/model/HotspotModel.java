package com.chinamobile.android.connectionmanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chinamobile.android.connectionmanager.util.LocationUtil;

/**
 * hotspot mode
 */
public class HotspotModel implements Serializable{
	private static final long serialVersionUID = 4553872499892661104L;
	public static final int STRENGTH_NONE = -1;
	public static final int STRENGTH_LOW = 0;
	public static final int STRENGTH_MEDIUM = 1;
	public static final int STRENGTH_STRONG = 2;
	public static final int STRENGTH_PERFECT = 3;
	public static final int STRENGTH_UNKNOW = -2;
	
	public static final int UNKNOWN = 10;
	public static final int SCANNING = 11;
	public static final int CONNECT_SUCCESS = 12;
	public static final int CONNECT_FAILED = 13;
	public static final int OPEN_WIFI = 14;
	public static final int CONNECTING = 15;
	public static final int CONNECT_FORBIDDEN = 16;
	public static final int ALREADY_CONNECT = 17;
	public static final int VALID = 18;
	
	private int accessNetworkType = -1;
	private int g3_plmn = -1;
	private int g3_lac = -1;
	private long g3_cid = -1;
	private double latitude = -1;
	private double longitude = -1;
//	private List<double[]> geoData = new ArrayList<double[]>();
	private long radius = -1;
	private String accessNetworkInformationRef;
	private int plmn = -1;
	private String ssid;
	public int signalStrength = STRENGTH_UNKNOW;
	public boolean isLastConnected;
	public int status = UNKNOWN;
	public String wlanSsid;
	
	public void resetStatus() {
		status = UNKNOWN;
	}
	
	public int getAccessNetworkType() {
		return accessNetworkType;
	}

	public void setAccessNetworkType(int accessNetworkType) {
		this.accessNetworkType = accessNetworkType;
	}

	public int getG3_plmn() {
		return g3_plmn;
	}

	public void setG3_plmn(int g3_plmn) {
		this.g3_plmn = g3_plmn;
	}

	public int getG3_lac() {
		return g3_lac;
	}

	public void setG3_lac(int g3_lac) {
		this.g3_lac = g3_lac;
	}

	public long getG3_cid() {
		return g3_cid;
	}

	public void setG3_cid(long g3_cid) {
		this.g3_cid = g3_cid;
	}

//	public List<double[]> getGeoData() {
//		return geoData;
//	}
//
//	public void setGeoData(List<double[]> geoData) {
//		this.geoData = geoData;
//	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getRadius() {
		return radius;
	}

	public void setRadius(long radius) {
		this.radius = radius;
	}

	public String getAccessNetworkInformationRef() {
		return accessNetworkInformationRef;
	}

	public void setAccessNetworkInformationRef(String accessNetworkInformationRef) {
		this.accessNetworkInformationRef = accessNetworkInformationRef;
	}

	public int getPlmn() {
		return plmn;
	}

	public void setPlmn(int plmn) {
		this.plmn = plmn;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	
	public static class WlanInfo {
		public String id;
		public String addr;
		public String addr_type;
		public String bearer_type;
		public String ssid;
	}
	
	public HotspotModel clone() {
		HotspotModel hotspot = new HotspotModel();
		hotspot.setAccessNetworkType(this.accessNetworkType);
		hotspot.setAccessNetworkInformationRef(this.accessNetworkInformationRef);
		hotspot.setG3_cid(this.g3_cid);
		hotspot.setG3_lac(this.g3_lac);
		hotspot.setG3_plmn(this.g3_plmn);
		hotspot.setLatitude(this.latitude);
		hotspot.setLongitude(this.longitude);
		hotspot.setPlmn(this.plmn);
		hotspot.setRadius(this.radius);
		hotspot.setSsid(this.ssid);
		hotspot.wlanSsid = wlanSsid;
		return hotspot;
	}
}
