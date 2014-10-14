package com.chinamobile.android.connectionmanager.ui;

import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.receiver.SMSReceiver;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.SMSUtil;
import com.chinamobile.android.connectionmanager.util._3GUtil;

/**
 * Activity for sms center page
 *
 */
public class SMSCenterActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener{
	private static final String TAG = "SMSCenterActivity";
	
	public static final String ACTION_SEND_SMS = "com.chinamobile.android.connectionmanager.ui.sms.send";
	public static final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
	private static final String SMS_USER_NAME = "[1][\\d]{10}";
	private static final String SMS_PASSWORD = "\\d{6}";
	private static final String SMS_CONFIRM = "您将开通WLAN自动认证专属资费";
	private static final String SMS_HAVE_OPENED = "由于您已办理了WLAN自动认证专属资费，无须重复办理";
	private static final String SMS_OPENED_SUCCESS = "成功开通了WLAN自动认证专属资费";
	private static final String SMS_CHANGE_PSW_SUCCESS = "已成功修改WLAN自动认证密码";
	
//	private RadioGroup radioGroup;
	private RadioButton openButton;
	private RadioButton modifyButton;
	private Button sendBtn;
	private EditText newpwd;
	private EditText oldpwd;
	private EditText confirmpwd;
	private TextView errorTextView;
	private IntentFilter filter;
	private TitleBar titleBar;
//	private Timer timer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sms_center);
		
		titleBar = (TitleBar) findViewById(R.id.TitleBar);
		titleBar.setParameter(R.string.sms_title);
		TextView mTextTitle = (TextView) findViewById(R.id.sms_center_title);
		mTextTitle.setText(Html.fromHtml(getResources().getString(R.string.label_select_set_summary)));
		TextView mTextTitle2 = (TextView) findViewById(R.id.sms_center_title_change_pwd);
		mTextTitle2.setText(Html.fromHtml(getResources().getString(R.string.label_select_set_summary_change)));
		
//		radioGroup = (RadioGroup) findViewById(R.id.sms_action_radio);
		openButton = (RadioButton) findViewById(R.id.sms_action_open_account);
		modifyButton = (RadioButton) findViewById(R.id.sms_action_modify_pwd);
		sendBtn = (Button) findViewById(R.id.send_sms);
		oldpwd = (EditText) findViewById(R.id.peap_old_psw);
		oldpwd.setTypeface(Typeface.DEFAULT);
		oldpwd.setTransformationMethod(new PasswordTransformationMethod());
		newpwd = (EditText) findViewById(R.id.peap_new_psw);
		newpwd.setTypeface(Typeface.DEFAULT);
		newpwd.setTransformationMethod(new PasswordTransformationMethod());
		confirmpwd = (EditText) findViewById(R.id.peap_confirm_new_psw);
		confirmpwd.setTypeface(Typeface.DEFAULT);
		confirmpwd.setTransformationMethod(new PasswordTransformationMethod());
		errorTextView = (TextView) findViewById(R.id.psw_change_error);
		
		sendBtn.setOnClickListener(this);
		openButton.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		modifyButton.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openButton.setChecked(false);
		modifyButton.setChecked(false);
//		radioGroup.setOnCheckedChangeListener((android.widget.RadioGroup.OnCheckedChangeListener) this);
		super.onCreate(savedInstanceState);
	}

	
	
	@Override
	protected void onResume() {
		if(!_3GUtil.isAirplaneModeOn(this) && CommonUtil.getPlmn(this) == 46000) {
			sendBtn.setEnabled(true);
			sendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_login));
		} else {
			sendBtn.setEnabled(false);
			sendBtn.setFocusable(false);
			sendBtn.setFocusableInTouchMode(false);
			sendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_toolbar_disabled));
		}
		super.onResume();
	}


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.send_sms) {
//			int selectId = radioGroup.getCheckedRadioButtonId();
			if(openButton.isChecked()) {
				SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CONTENT);
				SMSReceiver.enable = true;// start the sms receiver
			} else if (modifyButton.isChecked()) {
				final String oldString = oldpwd.getText().toString().trim();
				final String newString = newpwd.getText().toString().trim();
				final String confirmString = confirmpwd.getText().toString().trim();
				
				if (oldString != null && !oldString.equals("")
						&& newString != null && !newString.equals("")
						&& confirmString != null && !confirmString.equals("")
						&& newString.equals(confirmString)) {
					SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS,
							Constants.SMS_MODIFY_CONTENT + " " + oldString + " " + newString);
					SMSReceiver.enable = true;
					errorTextView.setVisibility(View.INVISIBLE);
				} else {
					errorTextView.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	

	/*@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
	}*/



	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView == openButton && openButton.isChecked()) {
			modifyButton.setChecked(false);
		}
		
		if(buttonView == modifyButton && modifyButton.isChecked()) {
			openButton.setChecked(false);
		}
		
//		int selectId = radioGroup.getCheckedRadioButtonId();
		if(openButton.isChecked()) {
			oldpwd.setEnabled(false);
			oldpwd.setFocusable(false);
			oldpwd.setFocusableInTouchMode(false);
			newpwd.setEnabled(false);
			newpwd.setFocusable(false);
			newpwd.setFocusableInTouchMode(false);
			confirmpwd.setEnabled(false);
			confirmpwd.setFocusable(false);
			confirmpwd.setFocusableInTouchMode(false);
			errorTextView.setVisibility(View.INVISIBLE);
			oldpwd.invalidate();
			newpwd.invalidate();
			confirmpwd.invalidate();
		} else if (modifyButton.isChecked()) {
			oldpwd.setEnabled(true);
			oldpwd.setFocusable(true);
			oldpwd.setFocusableInTouchMode(true);
			newpwd.setEnabled(true);
			newpwd.setFocusable(true);
			newpwd.setFocusableInTouchMode(true);
			confirmpwd.setEnabled(true);
			confirmpwd.setFocusable(true);
			confirmpwd.setFocusableInTouchMode(true);
			oldpwd.invalidate();
			newpwd.invalidate();
			confirmpwd.invalidate();
		}
	}
}
