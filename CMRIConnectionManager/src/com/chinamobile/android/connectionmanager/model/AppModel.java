package com.chinamobile.android.connectionmanager.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * App model
 *
 */
public class AppModel {
	public static final int UNDEFINE = 0;
	public static final int WIFI_FIRST = 1;
	public static final int G3_FIRST = 2;
	private String appLabel;
	private Drawable appIcon;
	private Intent intent;
	private String pkgName;
	private boolean isChecked;
	private int networkPri;

	public AppModel() {
	}

	public String getAppLabel() {
		return appLabel;
	}

	public void setAppLabel(String appName) {
		this.appLabel = appName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public int getNetworkPri() {
		return networkPri;
	}

	public void setNetworkPri(int networkPri) {
		this.networkPri = networkPri;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		AppModel other = (AppModel) o;
		if (!appLabel.equals(other.appLabel))
			return false;
		if (!pkgName.equals(other.pkgName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AppModel [appLabel=" + appLabel + ", appIcon=" + appIcon
				+ ", intent=" + intent + ", pkgName=" + pkgName
				+ ", isChecked=" + isChecked + ", networkPri=" + networkPri
				+ "]";
	}
}
