package com.chinamobile.android.connectionmanager.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.manager.PolicyManager;
import com.chinamobile.android.connectionmanager.model.AppModel;
import com.chinamobile.android.connectionmanager.ui.ListDialog;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.FileUtil;
import com.chinamobile.android.connectionmanager.util.LogUtil;

public class TestTriggerActivity extends Activity {
	// private PolicyManager policyManager;
	private Handler requestHandler = new Handler();
	private SeekBar mSeekBar;
	private SeekBar mSeekBar2;
	private TextView mProgressText;
	private TextView mProgressText2;
	Uri uri = Uri.parse("content://telephony/carriers");
	PackageManager pm;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScrollView scroll = new ScrollView(this);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		scroll.addView(layout);
		setContentView(scroll);
		LinearLayout.LayoutParams paramfull = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		paramfull.setMargins(20, 20, 20, 20);
		
		
		
		
		/*final EditText edit = new EditText(this);
		edit.setHint("输入请求间隔（秒）");
		edit.setKeyListener(new DigitsKeyListener(false, true));
		layout.addView(edit, paramfull);
		Button confirm = new Button(this);
		layout.addView(confirm, paramfull);
		confirm.setText("确定");
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppApplication.test_request_time = Integer.parseInt(edit.getText()
						.toString()) * 1000;
				finish();
			}
		});*/
		
		
		CheckBox server = new CheckBox(this);
		server.setText("checked--->from server             unchecked--->from local sdcard xml file");
		server.setChecked(AppApplication.requestServer);
		server.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.w("Test", "isChecked:" + isChecked);
				AppApplication.requestServer = isChecked;
			}
		});
		layout.addView(server, paramfull);
		
		Button requestStartBtn = new Button(this);
		requestStartBtn.setText("change cell id");
		requestStartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TelephonyManager telManager = (TelephonyManager) TestTriggerActivity.this
							.getSystemService(Context.TELEPHONY_SERVICE);
				telManager.listen(PolicyManager.cidListener, PhoneStateListener.LISTEN_CELL_LOCATION);
				PolicyManager.cellId = -1000;
			}
		});
		layout.addView(requestStartBtn, paramfull);

//		Button g3_en_Btn = new Button(this);
//		g3_en_Btn.setText("resume 3G");
//		layout.addView(g3_en_Btn, paramfull);
//		g3_en_Btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				openAPN();
//			}
//		});
//
//		Button wifi_disable_Btn = new Button(this);
//		wifi_disable_Btn.setText("Clear user guide flag");
//		layout.addView(wifi_disable_Btn, paramfull);
//		wifi_disable_Btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				SharedPreferences sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
//				Editor editor = sp.edit();
//				editor.putBoolean("looked_userguide", false);
//				editor.commit();
//			}
//		});
//		
//		Button wifi_en_Btn = new Button(this);
//		wifi_en_Btn.setText("Turn On Wifi Ap");
//		layout.addView(wifi_en_Btn, paramfull);
//		wifi_en_Btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
////				AppApplication.ap_on = true;
//			}
//		});
//		
		mSeekBar = new SeekBar(this);
//		layout.addView(mSeekBar, paramfull);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int level = progress / 10;
				mProgressText.setText("wifi1 signal strength level:" + level);
				Constants.minDBMLevel = level;
			}
		});

//		mProgressText = new TextView(this);
//		mProgressText.setText("wifi1 signal strength level:0");
//		layout.addView(mProgressText, paramfull);
//
//		mSeekBar2 = new SeekBar(this);
//		layout.addView(mSeekBar2, paramfull);
//		mSeekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//				int level = progress / 10;
//				mProgressText2.setText("wifi2 signal strength level:" + level);
////				Constants.minDBMLevel2 = level;
//			}
//		});
//
//		mProgressText2 = new TextView(this);
//		mProgressText2.setText("wifi2 signal strength level:0");
//		layout.addView(mProgressText2, paramfull);
		
		TextView serverText = new TextView(this);
		serverText.setText("Configure the server address here");
		layout.addView(serverText, paramfull);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		TextView headText = new TextView(this);
		headText.setText("http://");
		linearLayout.addView(headText);
		final EditText editServer = new EditText(this);
//		editServer.setEnabled(false);
		editServer.setText(Constants.POLICY_SERVER_URL);
		editServer.setHint("input server url");
//		linearLayout.addView(editServer, paramfull);
		layout.addView(linearLayout);
		
		Button save = new Button(this);
		save.setText("Save Url");
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Constants.POLICY_SERVER_URL = editServer.getText().toString();
			}
		});
		
		layout.addView(save, paramfull);
		
		final EditText edit = new EditText(this);
		edit.setHint("cell id(ms)");
		edit.setEnabled(false);
		edit.setKeyListener(new DigitsKeyListener(false, true));
//		layout.addView(edit, paramfull);
		Button confirm = new Button(this);
//		layout.addView(confirm, paramfull);
		confirm.setText("set");
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Constants.INTERVAL_TIME = Integer.parseInt(edit.getText()
						.toString());
			}
		});

		Button btn = new Button(this);
		layout.addView(btn, paramfull);
		btn.setText("View Log");
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestTriggerActivity.this,
						LogActivity.class);
				startActivity(intent);
			}
		});

		Button clear = new Button(this);
		layout.addView(clear, paramfull);
		clear.setText("Clear Log");
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtil.tempStr = new StringBuffer();
				LogUtil.saveLog2SD(TestTriggerActivity.this,
						FileUtil.FORMAT_OVERWRITE);
			}
		});

	}

	@Override
	protected void onStart() {
		// policyManager = new PolicyManager(getApplicationContext(), null);
		// policyManager.onStart();
		super.onStart();
	}

	public void closeAPN() {
		/*
		 * List<APN> list = getAPNList(); for (APN apn : list) { ContentValues
		 * cv = new ContentValues(); cv.put("apn", ""); cv.put("type", "");
		 * cv.put("mcc", ""); getContentResolver().update(uri, cv, "_id=?", new
		 * String[]{apn.id});
		 * 
		 * }
		 */

		this.getContentResolver().delete(uri, null, null);
	}

	/*public void openAPN() {
		List<APN> list = getAPNList();
		for (APN apn : list) {
			ContentValues cv = new ContentValues();
			cv.put("apn", matchAPN(apn.apn));
			cv.put("type", matchAPN(apn.type));
			getContentResolver().insert(uri, cv);

		}
	}

	private List<APN> getAPNList() {
		String projection[] = { "_id,apn,type,mcc,current" };
		Cursor cr = this.getContentResolver().query(uri, projection, null,
				null, null);

		List<APN> list = new ArrayList<APN>();

		while (cr != null && cr.moveToNext()) {
			APN a = new APN();
			a.id = cr.getString(cr.getColumnIndex("_id"));
			a.apn = cr.getString(cr.getColumnIndex("apn"));
			a.type = cr.getString(cr.getColumnIndex("type"));
			a.mcc = cr.getString(cr.getColumnIndex("mcc"));
			list.add(a);
		}
		if (cr != null)
			cr.close();
		return list;
	}

	public static class APN {
		String id;
		String apn;
		String type;
		String mcc;
	}

	public static class APNNet {
		*//**
		 * 中国移动cmwap
		 *//*
		public static String CMWAP = "cmwap";

		*//**
		 * 中国移动cmnet
		 *//*
		public static String CMNET = "cmnet";

		// 中国联通3GWAP设置 中国联通3G因特网设置 中国联通WAP设置 中国联通因特网设置
		// 3gwap 3gnet uniwap uninet

		*//**
		 * 3G wap 中国联通3gwap APN
		 *//*
		public static String GWAP_3 = "3gwap";

		*//**
		 * 3G net 中国联通3gnet APN
		 *//*
		public static String GNET_3 = "3gnet";

		*//**
		 * uni wap 中国联通uni wap APN
		 *//*
		public static String UNIWAP = "uniwap";
		*//**
		 * uni net 中国联通uni net APN
		 *//*
		public static String UNINET = "uninet";
	}

	public String matchAPN(String currentName) {
		if ("".equals(currentName) || null == currentName) {
			return "";
		}
		currentName = currentName.toLowerCase();
		if (currentName.startsWith(APNNet.CMNET))
			return APNNet.CMNET;
		else if (currentName.startsWith(APNNet.CMWAP))
			return APNNet.CMWAP;
		else if (currentName.startsWith(APNNet.GNET_3))
			return APNNet.GNET_3;
		else if (currentName.startsWith(APNNet.GWAP_3))
			return APNNet.GWAP_3;
		else if (currentName.startsWith(APNNet.UNINET))
			return APNNet.UNINET;
		else if (currentName.startsWith(APNNet.UNIWAP))
			return APNNet.UNIWAP;
		else if (currentName.startsWith("default"))
			return "default";
		else
			return "";
		// return currentName.substring(0, currentName.length() -
		// SUFFIX.length());
	}*/

}
