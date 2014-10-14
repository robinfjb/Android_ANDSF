package com.chinamobile.android.connectionmanager.model;

import java.io.Serializable;
import java.util.List;

/**
 * report mode
 */
public class ReportModel implements Serializable{
	private static final long serialVersionUID = -3829444554371681709L;
	
	private List<ReportData> data;
	private String deviceId;
	private String androidOsVersion;
	private String andsfAppVersion;
	private String andsfAppName;
	
	public static class ReportData {
		public String networkType;
		public String networkName;
		public String time;
		public int cellId;
		public int plmn;
		public int lac;
		public double latitude;
		public double longitude;
		public long wifiTrafficUpload;
		public long wifiTrafficDownload;
		public long g3TrafficUpload;
		public long g3TrafficDownload;
	}

	public List<ReportData> getData() {
		return data;
	}

	public void setData(List<ReportData> data) {
		this.data = data;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getAndroidOsVersion() {
		return androidOsVersion;
	}

	public void setAndroidOsVersion(String androidOsVersion) {
		this.androidOsVersion = androidOsVersion;
	}

	public String getAndsfAppVersion() {
		return andsfAppVersion;
	}

	public void setAndsfAppVersion(String andsfAppVersion) {
		this.andsfAppVersion = andsfAppVersion;
	}

	public String getAndsfAppName() {
		return andsfAppName;
	}

	public void setAndsfAppName(String andsfAppName) {
		this.andsfAppName = andsfAppName;
	}
}
