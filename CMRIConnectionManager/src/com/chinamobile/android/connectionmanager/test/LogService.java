package com.chinamobile.android.connectionmanager.test;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.chinamobile.android.connectionmanager.util.FileUtil;
import com.chinamobile.android.connectionmanager.util.LogUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LogService extends Service{
	 private final IBinder mBinder = new MyBinder();
	 Timer timer = new Timer();
	 TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			
			LogUtil.saveLog2SD(LogService.this, FileUtil.FORMAT_APPEDN);
		}
	};
	@Override
	public void onCreate() {
		Log.i("LogService", "onCreate");
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i("LogService", "onBind");
		timer.schedule(task, 1000, 5000); 
		return mBinder;
	}

	public class MyBinder extends Binder{
        
        public LogService getService(){
            return LogService.this;
        }
    }

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("LogService", "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.i("LogService", "onDestroy");
		super.onDestroy();
	}
}
