package com.chinamobile.android.connectionmanager.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.chinamobile.android.connectionmanager.R;

/**
 * bottom menu view
 *
 */
public class BottomMenu extends LinearLayout {
    private Context context;
    private ImageButton map;
    private ImageButton account;
    private ImageButton status;
    private OnClickListener statusListener;
	private OnClickListener mapListener;
    private OnClickListener accountListener;
//    private OnClickListener settingListener;
    
    public BottomMenu(Context context) {
            super(context);
            this.context = context;
            setView();
            // TODO Auto-generated constructor stub
    }

    public BottomMenu(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;
            setView();
            // TODO Auto-generated constructor stub
    }
    
    private void setView() {
            setTag(BottomMenu.class);
            
            LayoutInflater mInflate = LayoutInflater.from(context);
            final View menu = mInflate.inflate(R.layout.menu_linearlayout, null);
            menu.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,100));
            addView(menu);
            
            map = (ImageButton) findViewById(R.id.menu_map);
            
            
            /*setting = (ImageButton) findViewById(R.id.menu_set);
            if(settingListener != null) {
            	setting.setOnClickListener(settingListener);
            }*/
            
            account = (ImageButton) findViewById(R.id.menu_profile);
            
            
            status = (ImageButton) findViewById(R.id.menu_status);
            
    }
    
    /**
     * set which item selected and invalidate view
     * @param which
     */
	public void setWhichSelected(int which) {
		switch (which) {
		case 0:
			status.setImageResource(R.drawable.cmri_status_icon_pressed);
			map.setImageResource(R.drawable.button_icon_nav_contacts);
			account.setImageResource(R.drawable.button_icon_nav_more);
			break;
		case 1:
			status.setImageResource(R.drawable.button_icon_nav_sessions);
			map.setImageResource(R.drawable.cmri_map_icon_pressed);
			account.setImageResource(R.drawable.button_icon_nav_more);
			break;
		case 2:
			status.setImageResource(R.drawable.button_icon_nav_sessions);
			map.setImageResource(R.drawable.button_icon_nav_contacts);
			account.setImageResource(R.drawable.cmri_account_icon_pressed);
			break;
		}
	}
    
    public void setStatusListener(OnClickListener statusListener) {
		this.statusListener = statusListener;
		status.setOnClickListener(statusListener);
	}

	public void setMapListener(OnClickListener mapListener) {
		this.mapListener = mapListener;
		map.setOnClickListener(mapListener);
	}

	public void setAccountListener(OnClickListener accountListener) {
		this.accountListener = accountListener;
		account.setOnClickListener(accountListener);
	}
}
