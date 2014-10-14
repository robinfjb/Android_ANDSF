package com.chinamobile.android.connectionmanager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.controller.ServiceController;

/**
 * base activity
 * <p>overwrite <code>onSaveInstanceState</code> to save andsf status
 *
 */
public class BaseActivity extends Activity{

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i("BaseActivity", "onSaveInstanceState");
		SharedPreferences sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);
		
		Editor editor = sp.edit();
		editor.putBoolean("andsf_running", ServiceController.is_andsf_start);
		editor.commit();
		super.onSaveInstanceState(outState);
	}
}
