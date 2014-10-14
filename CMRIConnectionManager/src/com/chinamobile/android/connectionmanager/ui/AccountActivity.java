package com.chinamobile.android.connectionmanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.util.Constants;

/**
 * user account activity
 *
 */
public class AccountActivity extends BaseActivity implements OnClickListener, OnEditorActionListener{
	private static final String TAG = "AccountActivity";
	public static final String ACCOUNT_AUTO_FILL_ACTION = "com.chinamobile.android.connectionmanager.ui.sms.auto.fill";
//	private Button saveBtn;
//	private Button cancelBtn;
	private TitleBar titleBar;
	private EditText usernameEdit;
	private EditText passwordEdit;
	private EditText peapUsernameEdit;
	private EditText peapPasswordEdit;
	private TextView link2SMS;
//	private Button sendSMSBtn;
//	private Button changePswBtn;
	private Button saveBtn;
	private Button cancelBtn;
	private CheckBox mCheckbox;
	private SharedPreferences sp;
	private BroadcastReceiver autoFillReceiver;
	private boolean need_complete_cmcc;
	private boolean need_complete_cmcc_auto;
	private boolean ishotspot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		usernameEdit = (EditText) findViewById(R.id.account_input);
		passwordEdit = (EditText) findViewById(R.id.password_input);
		passwordEdit.setTypeface(Typeface.DEFAULT);
		passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
		
		peapUsernameEdit = (EditText) findViewById(R.id.account_peap);
		peapPasswordEdit = (EditText) findViewById(R.id.password_peap);
		peapPasswordEdit.setTypeface(Typeface.DEFAULT);
		peapPasswordEdit.setTransformationMethod(new PasswordTransformationMethod());
		
		link2SMS = (TextView) findViewById(R.id.link_to_sms);
		link2SMS.setText(Html.fromHtml(getResources().getString(R.string.login_how_to_get_account_link)));
		mCheckbox = (CheckBox) findViewById(R.id.use_same_account);
		saveBtn = (Button) findViewById(R.id.save_account_action);
		cancelBtn = (Button) findViewById(R.id.cancel_account_action);
//		sendSMSBtn = (Button) findViewById(R.id.send_sms);
//		changePswBtn = (Button) findViewById(R.id.change_psw_sms);
		
		sp = getSharedPreferences(AppApplication.SP_DATA_NAME, Context.MODE_PRIVATE);

		usernameEdit.setOnClickListener(this);
		passwordEdit.setOnClickListener(this);
		peapUsernameEdit.setOnClickListener(this);
		peapPasswordEdit.setOnClickListener(this);
		mCheckbox.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		link2SMS.setOnClickListener(this);
//		sendSMSBtn.setOnClickListener(this);
//		changePswBtn.setOnClickListener(this);
		usernameEdit.setOnEditorActionListener(this);
		passwordEdit.setOnEditorActionListener(this);
		peapUsernameEdit.setOnEditorActionListener(this);
		peapPasswordEdit.setOnEditorActionListener(this);
		
		titleBar = (TitleBar) findViewById(R.id.TitleBar);
		titleBar.setParameter(R.string.account_title);
		
		autoFillReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(ACCOUNT_AUTO_FILL_ACTION)) {
					CharSequence account = intent.getCharSequenceExtra("fill_account");
					CharSequence password = intent.getCharSequenceExtra("fill_pwd");
					peapUsernameEdit.setText(account);
					peapPasswordEdit.setText(password);
				}
			}
		};
		registerReceiver(autoFillReceiver, new IntentFilter(ACCOUNT_AUTO_FILL_ACTION));
		
		
		BottomMenu bottom = (BottomMenu) findViewById(R.id.account_menu_linearlayout);
		bottom.setWhichSelected(2);
		bottom.setStatusListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent statusIntent = new Intent(AccountActivity.this,
						HomeActivity.class);
				statusIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(statusIntent);
			}
		});
		bottom.setMapListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent setIntent = new Intent(AccountActivity.this,
						HotspotActivity.class);
				setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(setIntent);
			}
		});
		
		need_complete_cmcc = getIntent().getBooleanExtra("need_complete_cmcc", false);
		need_complete_cmcc_auto = getIntent().getBooleanExtra("need_complete_cmcc_auto", false);
		ishotspot = getIntent().getBooleanExtra("is_hot_spot", false);
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId,
			KeyEvent event) {
		if(v.getId() == R.id.account_input) {
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				passwordEdit.requestFocus();
			}
		} else if(v.getId() == R.id.password_input) {
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				if(!mCheckbox.isChecked()) {
					peapUsernameEdit.requestFocus();
				}
			} else if(actionId == EditorInfo.IME_ACTION_DONE) {
//				saveBtn.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(passwordEdit.getWindowToken(),
						0);
			}
		} else if(v.getId() == R.id.account_peap) {
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				if(!mCheckbox.isChecked()) {
					peapPasswordEdit.requestFocus();
				}
			} else if(actionId == EditorInfo.IME_ACTION_DONE) {
//				saveBtn.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(peapUsernameEdit.getWindowToken(),
						0);
			}
		} else if(v.getId() == R.id.password_peap) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
//				saveBtn.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(peapPasswordEdit.getWindowToken(),
						0);
			}
		}
		return true;
	}
	
	@Override
	protected void onResume() {
		usernameEdit.setText(sp.getString("username", ""));
		passwordEdit.setText(sp.getString("password", ""));
		peapUsernameEdit.setText(sp.getString("peap_username", ""));
		peapPasswordEdit.setText(sp.getString("peap_password", ""));
		boolean check = sp.getBoolean("use_same_account", false);
		mCheckbox.setChecked(check);
		if(check) {
			peapUsernameEdit.setEnabled(!check);
			peapUsernameEdit.setFocusable(false);
			peapUsernameEdit.setFocusableInTouchMode(false);
			peapPasswordEdit.setEnabled(!check);
			peapPasswordEdit.setFocusable(false);
			peapPasswordEdit.setFocusableInTouchMode(false);
		}
		
		
		super.onResume();
	}

	
	@Override
	protected void onDestroy() {
		try {
			if(autoFillReceiver != null) {
				unregisterReceiver(autoFillReceiver);
			}
		} catch (Exception e) {
		}
		
		if(need_complete_cmcc && AppApplication.isCMCCAccountEmpty()) {
			Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
			sendIntent.putExtra("account_complete", false);
			sendIntent.putExtra("is_hotspot", ishotspot);
			sendBroadcast(sendIntent);
		}
		if(need_complete_cmcc_auto && AppApplication.isCMCCAccountEmpty()) {
			Intent sendIntent = new Intent(Constants.Action.ACTION_CONTINUE_CONNECTION);
			sendIntent.putExtra("account_complete", false);
			sendIntent.putExtra("is_hotspot", ishotspot);
			sendBroadcast(sendIntent);
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.account_input) {
		} else if(v.getId() == R.id.password_input) {
		} else if(v.getId() == R.id.account_peap) {
		} else if(v.getId() == R.id.password_peap) {
		} else if(v.getId() == R.id.use_same_account) {
			boolean check = mCheckbox.isChecked();
			peapUsernameEdit.setEnabled(!check);
			peapUsernameEdit.setFocusable(!check);
			peapUsernameEdit.setFocusableInTouchMode(!check);
			peapPasswordEdit.setEnabled(!check);
			peapPasswordEdit.setFocusable(!check);
			peapPasswordEdit.setFocusableInTouchMode(!check);
		} else if(v.getId() == R.id.save_account_action) {
			AppApplication.username = usernameEdit.getText().toString();
			AppApplication.password = passwordEdit.getText().toString();
			boolean check = mCheckbox.isChecked();
			if(check) {
				AppApplication.peap_username = usernameEdit.getText().toString();
				AppApplication.peap_password = passwordEdit.getText().toString();
			} else {
				AppApplication.peap_username = peapUsernameEdit.getText().toString();
				AppApplication.peap_password = peapPasswordEdit.getText().toString();
			}
			
			Editor editor = getSharedPreferences(AppApplication.SP_DATA_NAME,
					Context.MODE_PRIVATE).edit();
			editor.putString("username", AppApplication.username);
			editor.putString("password", AppApplication.password);
			editor.putString("peap_username", AppApplication.peap_username);
			editor.putString("peap_password", AppApplication.peap_password);
			editor.putBoolean("use_same_account", check);
			editor.commit();
			finish();
		} else if(v.getId() == R.id.cancel_account_action) {
			finish();
		} else if(v.getId() == R.id.link_to_sms) {
			Intent intent = new Intent(AccountActivity.this, SMSCenterActivity.class);
			startActivity(intent);
//			Intent intent2 = new Intent(AccountActivity.this, SMSCenterActivity2.class);
//			startActivity(intent2);
		}			
//		} else if(v.getId() == R.id.send_sms) {
//			SMSUtil.getInstance(this).send("10086", "KTWLANZD");
//			try {
//				registerReceiver(receiver, filter);
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		} else if(v.getId() == R.id.change_psw_sms) {
//			LayoutInflater factory = LayoutInflater.from(this);
//            final View textEntryView = factory.inflate(R.layout.dialog_modify_password, null);
//            final TextView errorTextView = (TextView) textEntryView.findViewById(R.id.psw_change_error);
//            final EditText oldPsw = (EditText) textEntryView.findViewById(R.id.peap_old_psw);
//            oldPsw.setTypeface(Typeface.DEFAULT);
//            oldPsw.setTransformationMethod(new PasswordTransformationMethod());
//            final EditText newPsw = (EditText) textEntryView.findViewById(R.id.peap_new_psw);
//            newPsw.setTypeface(Typeface.DEFAULT);
//            newPsw.setTransformationMethod(new PasswordTransformationMethod());
//            final EditText confirmPsw = (EditText) textEntryView.findViewById(R.id.peap_confirm_new_psw);
//            confirmPsw.setTypeface(Typeface.DEFAULT);
//            confirmPsw.setTransformationMethod(new PasswordTransformationMethod());
//            
//			new AlertDialog.Builder(AccountActivity.this).setTitle(R.string.new_psw_send_msg)
//			.setView(textEntryView)
//			.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                	final String oldString = oldPsw.getText().toString();
//                    final String newString = newPsw.getText().toString();
//                    final String confirmString = confirmPsw.getText().toString();
//                	if(oldString != null && !oldString.trim().equals("")
//                			&& newString != null && !newString.trim().equals("")
//                			&& confirmString != null && !confirmString.trim().equals("")
//                			&& newString.equals(confirmString)) {
//                		SMSUtil.getInstance(AccountActivity.this).send("10086", "XGWLANZDMM" +
//                				" " + oldString + " " + newString);
////                		if(receiver != null) {
////                			try {
////                				AccountActivity.this.unregisterReceiver(receiver);
////                			} catch (Exception e) {
////								// TODO: handle exception
////							}
////                		}
////            			AccountActivity.this.registerReceiver(receiver, filter);
//                		try { 
//                			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
//                			field.setAccessible(true); 
//                			field.set(dialog, true);
//                		} catch (Exception e) { 
//                			e.printStackTrace(); 
//                		}
//                	} else {
//                		try { 
//                			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
//                			field.setAccessible(true); 
//                			field.set(dialog, false);
//                		} catch (Exception e) { 
//                			e.printStackTrace(); 
//                		}
//                		errorTextView.setVisibility(View.VISIBLE);
//                	}
//                }
//            })
//            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                	try { 
//            			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing"); 
//            			field.setAccessible(true); 
//            			field.set(dialog, true);
//            		} catch (Exception e) { 
//            			e.printStackTrace(); 
//            		}
//                }
//            })
//            .create().show();
//		}
	} 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.setting:
			Intent setIntent = new Intent(this,
					SettingActivity.class);
			startActivity(setIntent);
			return true;
		case R.id.help:
			Intent helpIntent = new Intent(this,
					HelpActivity.class);
			startActivity(helpIntent);
			return true;
		case R.id.about:
			final PopDialog aboutDialog = new PopDialog(this,
					String.format(getString(R.string.about_title), getString(R.string.app_name)),
					String.format(getString(R.string.about_content), Constants.VERSION),
					getString(R.string.close), 
					null,
					false,
					R.style.CMDialog);
			aboutDialog.setLeftListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					aboutDialog.dismiss();
				}
			});
			aboutDialog.show();
			return true;
		}
		return false;
	}
	
	
	
}
