/*package com.chinamobile.android.connectionmanager.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.model._3GModel;
import com.chinamobile.android.connectionmanager.util.CommonUtil;

public class StaticPolicyActivity extends BaseActivity{
	private TextView priority_1;
	private TextView priority_2;
	private Button button;
	private final String STR_WIFI = "WiFi";
	private final String STR_3G = "3G";
	private SharedPreferences sp;
	private boolean isWifiFirst;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.static_polcy);
		
		TitleBar titleBar = (TitleBar) findViewById(R.id.TitleBar);
		titleBar.setParameter(R.string.static_set_title);
//		TextView titleView = (TextView) findViewById(R.id.static_policy_title);
//		TextPaint tp = titleView.getPaint();
//		tp.setFakeBoldText(true);
//		
//		TextView promptTitleView = (TextView) findViewById(R.id.static_policy_prompt_title);
//		TextPaint tp2 = promptTitleView.getPaint();
//		tp2.setFakeBoldText(true);
		sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
		
		priority_1 = (TextView) findViewById(R.id.static_policy_priority_1);
		priority_2 = (TextView) findViewById(R.id.static_policy_priority_2);
		
		button = (Button) findViewById(R.id.change_priority_btn);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(StaticPolicyActivity.this)
	            .setTitle(R.string.static_set_dialog_title)
	            .setItems(R.array.static_priority, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    String[] items = getResources().getStringArray(R.array.static_priority);
	                    Toast.makeText(StaticPolicyActivity.this, 
	                    		StaticPolicyActivity.this.getText(R.string.toast_msg) + " " + items[which],
	                    		Toast.LENGTH_SHORT).show();
	                    
	                    if(which == 0) {
	                    	priority_1.setText(STR_WIFI);
	            			priority_2.setText(STR_3G);
	            			isWifiFirst = true;
	                    } else if(which == 1) {
	                    	priority_1.setText(STR_3G);
	            			priority_2.setText(STR_WIFI);
	            			isWifiFirst = false;
	            			
	                    } else {
	                    	return;
	                    }
	                    
	                    Editor sharedata = getSharedPreferences(AppApplication.SP_DATA_NAME,
            					Context.MODE_PRIVATE).edit();
	                    AppApplication.isWifiFirst = isWifiFirst;
            			sharedata.putBoolean("wifi_first", isWifiFirst);
            			sharedata.commit();
            			
//	                    List<NetworkModel> list = new ArrayList<NetworkModel>();
//	                    list.add(new WifiModel(NetworkModel.NAME_WLAN, null, isWifiFirst ? 0 : 1));
//	                    list.add(new _3GModel(NetworkModel.NAME_3G, isWifiFirst ? 1 : 0));
//	                    
//	                    CommonUtil.writeIntoLocalXml(StaticPolicyActivity.this, list);
//	    				Intent intent = new Intent(Constants.Action.ACTION_GENERATE_POLICY);
//	    				Bundle bundle = new Bundle();
//	    				bundle.putSerializable("static_network_order", (Serializable) list);
//	    				intent.putExtras(bundle);
//	    				StaticPolicyActivity.this.sendBroadcast(intent);
	                }
	            })
	            .create().show();
			}
		});
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
//		isWifiFirst = sp.getBoolean("wifi_first", true);
		if(AppApplication.isWifiFirst) {
			priority_1.setText(STR_WIFI);
			priority_2.setText(STR_3G);
		} else {
			priority_1.setText(STR_3G);
			priority_2.setText(STR_WIFI);
		}
		super.onResume();
	}
	
	

}
*/