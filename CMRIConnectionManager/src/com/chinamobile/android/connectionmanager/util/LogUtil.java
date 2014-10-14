package com.chinamobile.android.connectionmanager.util;

import android.content.Context;
import android.text.Html;

public class LogUtil {
	public static StringBuffer tempStr = new StringBuffer();
	
	/**
	 * add log as <code>String</code>
	 * @param log
	 */
	public static void add(String log) {
		tempStr.append(TimeUtil.getNowTime() + " : " + log + "<br/>");
	}
	
	/**
	 * save log into sdcard
	 * @param context
	 * @param format
	 */
	public static void saveLog2SD(Context context, int format) {
		FileUtil.saveFile2SdCard(context, "log.txt", tempStr.toString(), format);
		tempStr = new StringBuffer();
	}
}
