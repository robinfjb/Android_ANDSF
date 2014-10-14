package com.chinamobile.android.connectionmanager.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.database.DBAdpter;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.util.FileUtil;

/**
 * log activity for log page
 *
 */
public class LogActivity extends BaseActivity{
	public static String LOG_FILE = "log_save.txt";
	private TextView mText;
	private LogAsyncTask task;
	boolean run = true;
	private DBAdpter mAdpter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.log);
		TitleBar mTitle = (TitleBar) findViewById(R.id.TitleBar);
		mTitle.setParameter(R.string.log_title);
		mText = (TextView) findViewById(R.id.log_text);
		mAdpter = new DBAdpter(this);
		task = new LogAsyncTask();
		task.execute();
		super.onCreate(savedInstanceState);
	}
	
	private class LogAsyncTask extends AsyncTask<Void, Void, Void> {
		StringBuffer sb = new StringBuffer();
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				mAdpter.openDatabase();
				List<String[]> list = mAdpter.getLogData();
				for (String[] strings : list) {
					sb.append("<p>" + strings[0] + "  " + strings[1] + " " + getText(R.string.log_get_policy_message)
							+ ": ");
					if(Integer.parseInt(strings[2]) == NetworkModel.TYPE_WIFI) {
						sb.append(getText(R.string.first_wifi));
					} else if(Integer.parseInt(strings[2]) == NetworkModel.TYPE_3G) {
						sb.append(getText(R.string.first_3g));
					} 
				}	sb.append("</p>");
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				mAdpter.close();
			}
			
//			InputStream in = FileUtil.readStreamFormSDCard(LogActivity.this, LOG_FILE);
//			if(in != null) {
//				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//				String temp = null;
//				sb = new StringBuffer();
//				try {
//					while ((temp = reader.readLine()) != null) {
//						sb.append(temp);
//					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
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
