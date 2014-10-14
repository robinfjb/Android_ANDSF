package com.chinamobile.android.connectionmanager;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.chinamobile.android.connectionmanager.controller.ServiceController;
import com.chinamobile.android.connectionmanager.ui.HomeActivity;
import com.chinamobile.android.connectionmanager.util.LogUtil;

public class CrashException implements UncaughtExceptionHandler {

	private Context mContext;

	private Thread.UncaughtExceptionHandler defaultExceptionHandler;

	private static CrashException customException;

	private CrashException() {
	}

	public static CrashException getInstance() {
		if (customException == null) {
			customException = new CrashException();
		}
		return customException;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		// TODO Auto-generated method stub
		if (handleException(exception) && defaultExceptionHandler != null) {
			defaultExceptionHandler.uncaughtException(thread, exception);
			Log.e("uncaughtException--->CrashException", exception == null ? "null" : exception.getMessage());
		}
	}
	
	/**
	 * init parameter
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	/**
	 * notify excetion to user
	 * <br>add exception into log and print on strace
	 * @param ex
	 * @return
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, mContext.getResources().
						getString(R.string.crash_msg), Toast.LENGTH_LONG).show();
				StackTraceElement[] stacks = ex.getStackTrace();
				Log.e("handleException-->CrashException" , msg == null ? "null" : msg);
				LogUtil.add("exception >>>>>>" + msg);
				for (StackTraceElement stack : stacks) {
					LogUtil.add(stack.getClassName() + ":" + stack.getMethodName() + "(" + stack.getLineNumber() + ")");
					Log.e("CrashException", stack.getClassName() + ":" + stack.getMethodName() + "(" + stack.getLineNumber() + ")");
				}
				Looper.loop();
			}

		}.start();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		killProcess();
		return true;
	}

	/**
	 * kill the process
	 * <br>stop service and all activity
	 */
	private void killProcess() {
		Intent intent = new Intent(mContext, ServiceController.class);
		mContext.stopService(intent);
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(startMain);
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
