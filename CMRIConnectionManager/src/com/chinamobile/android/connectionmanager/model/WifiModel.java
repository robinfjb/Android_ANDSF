package com.chinamobile.android.connectionmanager.model;

import java.util.Arrays;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.util.Constants;

import android.net.wifi.WifiInfo;

/**
 * wifi mode
 *
 */
public class WifiModel extends NetworkModel{
	private static final long serialVersionUID = 5319082837216158608L;
	
	public static final int SIGNAL_STATE_VALID = 1;
	public static final int SIGNAL_STATE_INVALID = 2;
	public static final int SIGNAL_STATE_NONE = 3;
	
	public static final String TYPE_PEAP = "PEAP";
	public static final String TYPE_OPEN = "OPEN";
	public static final String TYPE_SIM = "SIM";

	public int[] signalRecord = new int[10];
	public int signalStrengthStatus;
	public String identity = "";
	public String password = "";
	public String phase2 = "";
	public String anonymous = "";
	private String SSID;
	private String second_SSID;
	private String HESSID;
	private String BSSID;
	private double latitude;//latitude of this wifi to show on map
	private double longitude;//longitude of this wifi to show on map
	private int signalStrength;
	private int position;
	private int plmn;
	private int lac;
	private String authenticationType;
	
	public WifiModel() {
		this.name = NAME_WLAN;
	}
	
	public WifiModel(String SSID) {
		this.SSID = SSID;
		this.name = NAME_WLAN;
	}
	
	public WifiModel(String name, String SSID, Integer priority) {
		this.name = name;
		this.SSID = SSID;
		this.priority = priority;
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	public boolean isAbandoned() {
		return abandoned;
	}

	public String getSSID() {
		return SSID;
	}

	public void setSSID(String sSID) {
		SSID = sSID;
	}

	public String getBSSID() {
		return BSSID;
	}

	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}

	public String getSecond_SSID() {
		return second_SSID;
	}

	public void setSecond_SSID(String second_SSID) {
		this.second_SSID = second_SSID;
	}

	public String getHESSID() {
		return HESSID;
	}

	public void setHESSID(String hESSID) {
		HESSID = hESSID;
	}

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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPlmn() {
		return plmn;
	}

	public void setPlmn(int plmn) {
		this.plmn = plmn;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

	public String getAuthenticationType() {
//		if(SSID == "CMCC-AUTO") {
//			return "PEAP";
//		} else if(SSID == "CMCC"){
//			return "WISPr";
//		} else {
//			return "OTHER";
//		}
		
		if(SSID.equalsIgnoreCase(Constants.CMCC)) {
			return TYPE_OPEN;
		} else if(SSID.equalsIgnoreCase(Constants.CMCC_AUTO)) {
			if(AppApplication.isEapSim) {
				return TYPE_SIM;
			} else {
				return TYPE_PEAP;
			}
		} else {
			return TYPE_OPEN;
		}
//		return authenticationType;
	}

	public void setAuthenticationType(String authenticationType) {
		this.authenticationType = authenticationType;
	}

	public int getType() {
		return TYPE_WIFI;
	}

	public void resetRecord() {
		signalRecord = new int[]{0,0,0,0,0,0,0,0,0,0};
	}
	
	@Override
	public NetworkModel onClone(){
		WifiModel netCopy = new WifiModel();
		netCopy.setAbandoned(abandoned);
		netCopy.setLatitude(latitude);
		netCopy.setLongitude(longitude);
		netCopy.setName(name);
		netCopy.setPosition(position);
		netCopy.setLac(lac);
		netCopy.setPlmn(plmn);
		netCopy.setPriority(priority);
		netCopy.setSignalStrength(signalStrength);
		netCopy.setSSID(SSID);
		netCopy.setHESSID(HESSID);
		netCopy.setSecond_SSID(second_SSID);
		netCopy.setBSSID(BSSID);
		netCopy.signalRecord = this.signalRecord;
		netCopy.signalStrengthStatus = this.signalStrengthStatus;
		netCopy.authenticationType = this.authenticationType;
		netCopy.phase2 = this.phase2;
		netCopy.identity = this.identity;
		netCopy.password = this.password;
		netCopy.anonymous = this.anonymous;
		return netCopy;
	}

	@Override
	public String toString() {
		return "WifiModel [signalRecord=" + Arrays.toString(signalRecord)
				+ ", signalStrengthStatus=" + signalStrengthStatus
				+ ", identity=" + identity + ", password=" + password
				+ ", phase2=" + phase2 + ", anonymous=" + anonymous + ", SSID="
				+ SSID + ", second_SSID=" + second_SSID + ", HESSID=" + HESSID
				+ ", BSSID=" + BSSID + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", signalStrength="
				+ signalStrength + ", position=" + position
				+ ", authenticationType=" + authenticationType + "]";
	}
	
	
}
