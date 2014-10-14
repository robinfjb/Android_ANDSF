/*package com.chinamobile.android.connectionmanager.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.ui.DragListView.DragListener;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;

public class ProfileActivity extends Activity{
	private ArrayList<String> strings = new ArrayList<String>();
	private DragListView dragListView;
	private List<NetworkModel> list;
	private DragListener dragListener = new DragListener() {
		@Override
		public void drag(int from, int to) {
			if(from != to) {
				CommonUtil.switchElements(list);
				Intent intent = new Intent(Constants.Action.ACTION_GENERATE_POLICY);
				Bundle bundle = new Bundle();
				bundle.putSerializable("static_network_order", (Serializable) list);
				intent.putExtras(bundle);
				ProfileActivity.this.sendBroadcast(intent);
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.profile);
		dragListView = (DragListView)findViewById(R.id.drag_list);
//		list = AppApplication.getApp().getLocalStaticNetList();
		strings.add(list.get(0).getName());
		strings.add(list.get(1).getName());
		dragListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strings));
		dragListView.setTextFilterEnabled(true);
		dragListView.setDragListener(dragListener);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
*/