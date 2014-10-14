package com.chinamobile.android.connectionmanager.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.util.FileUtil;
import com.chinamobile.android.connectionmanager.util.TimeUtil;

public class LogActivity extends Activity {
	public static String LOG_FILE = "log.txt";
	private TextView mText ;
	LogAsyncTask task;
	boolean run = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ScrollView scorll = new ScrollView(this);
		LayoutParams paramfull = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		scorll.addView(layout);
		setContentView(scorll);
//		final Button btn = new Button(this);
//		btn.setText("stop");
//		btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(!run) {
//					new Thread(new Runnable() {
//						@Override
//						public void run() {
//							while(run) {
//								mHandler.sendEmptyMessage(0);
//								try {
//									Thread.sleep(2000);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}
//						}
//					}).start();
//					btn.setText("stop");
//				} else {
//					btn.setText("resume");
//				}
//				run = !run;
//			}
//		});
//		layout.addView(btn, paramfull);
		
		mText = new TextView(this);
		layout.addView(mText, paramfull);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(run) {
					mHandler.sendEmptyMessage(0);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		super.onCreate(savedInstanceState);
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			task = new LogAsyncTask();
			task.execute();
			super.handleMessage(msg);
		}
		
	};
	
	private class LogAsyncTask extends AsyncTask<Void, Void, Void> {
		StringBuffer sb;
		
		@Override
		protected Void doInBackground(Void... params) {
			InputStream in = FileUtil.readStreamFormSDCard(LogActivity.this, LOG_FILE);
			if(in == null) {
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String temp = null;
			sb = new StringBuffer();
			try {
				while ((temp = reader.readLine()) != null) {
					sb.append(temp);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(sb != null) {
//				mText.setText(String.valueOf(TimeUtil.getNowTime()));
				mText.setText(Html.fromHtml(sb.toString()));
			}
//			super.onPostExecute(result);
		}
	}
	
}
