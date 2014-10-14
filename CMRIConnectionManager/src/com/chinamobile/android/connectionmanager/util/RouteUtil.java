package com.chinamobile.android.connectionmanager.util;

/**
 * add route table into /system/xbin
 *
 */
public class RouteUtil {
	public native static int addRoute();
	public native static int deleteRoute();
}
