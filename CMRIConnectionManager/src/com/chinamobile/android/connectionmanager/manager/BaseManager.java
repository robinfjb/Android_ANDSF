package com.chinamobile.android.connectionmanager.manager;

import com.chinamobile.android.connectionmanager.controller.ServiceController;

import android.content.Context;
import android.os.Handler;

/**
 * base manager for notify to {@link ServiceController}
 *
 */
public abstract class BaseManager {
	protected Context context;
	protected Handler handler;
	
	BaseManager(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	/**
	 * start the manager
	 */
	public abstract void onStart();
	/**
	 * stop the manager
	 */
	public abstract void onStop();
	/**
	 * notify to {@link ServiceController} by {@link Handler}
	 * @param message
	 */
	protected void notifyToController(int message){};
}
