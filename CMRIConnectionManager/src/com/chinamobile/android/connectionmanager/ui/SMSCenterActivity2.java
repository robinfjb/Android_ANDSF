package com.chinamobile.android.connectionmanager.ui;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.receiver.SMSReceiver;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.SMSUtil;
import com.chinamobile.android.connectionmanager.util._3GUtil;

@Deprecated
public class SMSCenterActivity2 extends BaseActivity implements OnClickListener, OnCheckedChangeListener{
	private static final String TAG = "SMSCenterActivity";
	
	public static final String ACTION_SEND_SMS = "com.chinamobile.android.connectionmanager.ui.sms.send";
	public static final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
	private static final String SMS_USER_NAME = "[1][\\d]{10}";
	private static final String SMS_PASSWORD = "\\d{6}";
	private static final String SMS_CONFIRM = "您将开通WLAN自动认证专属资费";
	private static final String SMS_HAVE_OPENED = "由于您已办理了WLAN自动认证专属资费，无须重复办理";
	private static final String SMS_OPENED_SUCCESS = "成功开通了WLAN自动认证专属资费";
	private static final String SMS_CHANGE_PSW_SUCCESS = "已成功修改WLAN自动认证密码";
	
	private RadioButton openAutoButton;
	private RadioButton modifyAutoButton;
	private RadioButton openCmccButton;
	private RadioButton openCmcc5Button;
	private RadioButton openCmcc10Button;
	private RadioButton openCmcc20Button;
	private RadioButton openCmcc30Button;
	private RadioButton openCmcc50Button;
	private RadioButton openCmcc100Button;
	private RadioButton openCmcc200Button;
	private RadioButton modifyCmccButton;
	private Button sendBtn;
	private Button autoBtn;
	private Button cmccBtn;
	private LinearLayout autoArea;
	private LinearLayout cmccArea;
	private EditText autoNewpwd;
	private EditText autoOldpwd;
	private EditText autoConfirmpwd;
	private EditText cmccNewpwd;
	private EditText cmccOldpwd;
	private EditText cmccConfirmpwd;
	private TextView errorTextView;
	private TextView errorTextView2;
	private IntentFilter filter;
	private TitleBar titleBar;
	private boolean isAutoClicked;
	private boolean isCmccClicked;
	private List<RadioButton> allRadios = new ArrayList<RadioButton>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sms_center2);
		
		titleBar = (TitleBar) findViewById(R.id.TitleBar);
		titleBar.setParameter(R.string.sms_title);
		TextView mTextTitle = (TextView) findViewById(R.id.sms_center_title);
		mTextTitle.setText(Html.fromHtml(getResources().getString(R.string.label_select_set_summary)));
		TextView mTextTitle2 = (TextView) findViewById(R.id.sms_center_title_change_pwd);
		mTextTitle2.setText(Html.fromHtml(getResources().getString(R.string.label_select_set_summary_change)));
		
		sendBtn = (Button) findViewById(R.id.send_sms);
		autoBtn = (Button) findViewById(R.id.traffic_btn);
		cmccBtn = (Button) findViewById(R.id.duration_btn);
		autoArea = (LinearLayout) findViewById(R.id.sms_cmcc_auto_option);
		cmccArea = (LinearLayout) findViewById(R.id.sms_cmcc_option);
		openAutoButton = (RadioButton) findViewById(R.id.sms_action_open_auto_account);
		modifyAutoButton = (RadioButton) findViewById(R.id.sms_action_modify_auto_pwd);
		openCmccButton = (RadioButton) findViewById(R.id.sms_action_open_cmcc_account_0);
		openCmcc5Button = (RadioButton) findViewById(R.id.sms_action_open_cmcc_account_5);
		openCmcc10Button = (RadioButton) findViewById(R.id.sms_action_open_cmcc_account_10);
		openCmcc20Button = (RadioButton) findViewById(R.id.sms_action_open_cmcc_account_20);
		openCmcc30Button = (RadioButton) findViewById(R.id.sms_action_open_cmcc_account_30);
		openCmcc50Button = (RadioButton) findViewById(R.id.sms_action_open_cmcc_account_50);
		openCmcc100Button = (RadioButton) findViewById(R.id.sms_action_open_cmcc_account_100);
		openCmcc200Button = (RadioButton) findViewById(R.id.sms_action_open_cmcc_account_200);
		modifyCmccButton = (RadioButton) findViewById(R.id.sms_action_modify_cmcc_pwd);
		allRadios.add(openAutoButton);
		allRadios.add(modifyAutoButton);
		allRadios.add(openCmccButton);
		allRadios.add(openCmcc5Button);
		allRadios.add(openCmcc10Button);
		allRadios.add(openCmcc20Button);
		allRadios.add(openCmcc30Button);
		allRadios.add(openCmcc50Button);
		allRadios.add(openCmcc100Button);
		allRadios.add(openCmcc200Button);
		allRadios.add(modifyCmccButton);
		autoOldpwd = (EditText) findViewById(R.id.peap_old_psw);
		autoOldpwd.setTypeface(Typeface.DEFAULT);
		autoOldpwd.setTransformationMethod(new PasswordTransformationMethod());
		autoNewpwd = (EditText) findViewById(R.id.peap_new_psw);
		autoNewpwd.setTypeface(Typeface.DEFAULT);
		autoNewpwd.setTransformationMethod(new PasswordTransformationMethod());
		autoConfirmpwd = (EditText) findViewById(R.id.peap_confirm_new_psw);
		autoConfirmpwd.setTypeface(Typeface.DEFAULT);
		autoConfirmpwd.setTransformationMethod(new PasswordTransformationMethod());
		errorTextView = (TextView) findViewById(R.id.psw_change_error);
		cmccOldpwd = (EditText) findViewById(R.id.cmcc_old_psw);
		cmccOldpwd.setTypeface(Typeface.DEFAULT);
		cmccOldpwd.setTransformationMethod(new PasswordTransformationMethod());
		cmccNewpwd = (EditText) findViewById(R.id.cmcc_new_psw);
		cmccNewpwd.setTypeface(Typeface.DEFAULT);
		cmccNewpwd.setTransformationMethod(new PasswordTransformationMethod());
		cmccConfirmpwd = (EditText) findViewById(R.id.cmcc_confirm_new_psw);
		cmccConfirmpwd.setTypeface(Typeface.DEFAULT);
		cmccConfirmpwd.setTransformationMethod(new PasswordTransformationMethod());
		errorTextView2 = (TextView) findViewById(R.id.cmcc_psw_change_error);
		
		sendBtn.setOnClickListener(this);
		autoBtn.setOnClickListener(this);
		cmccBtn.setOnClickListener(this);
		openAutoButton.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		modifyAutoButton.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openCmccButton.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openCmcc5Button.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openCmcc10Button.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openCmcc20Button.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openCmcc30Button.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openCmcc50Button.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openCmcc100Button.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openCmcc200Button.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		modifyCmccButton.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
		openAutoButton.setChecked(false);
		modifyAutoButton.setChecked(false);
		openCmccButton.setChecked(false);
		openCmcc5Button.setChecked(false);
		openCmcc10Button.setChecked(false);
		openCmcc20Button.setChecked(false);
		openCmcc30Button.setChecked(false);
		openCmcc50Button.setChecked(false);
		openCmcc100Button.setChecked(false);
		openCmcc200Button.setChecked(false);
		modifyCmccButton.setChecked(false);
		
		autoBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_clicked_bg));
		autoBtn.setTextColor(getResources().getColor(R.color.white));
		cmccArea.setVisibility(View.GONE);
		autoArea.setVisibility(View.VISIBLE);
		isAutoClicked = true;
		isCmccClicked = false;
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

	void sendAutoMessage() {
		if(openAutoButton.isChecked()) {
			SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CONTENT);
			SMSReceiver.enable = true;// start the sms receiver
		} else if (modifyAutoButton.isChecked()) {
			final String oldString = autoOldpwd.getText().toString().trim();
			final String newString = autoNewpwd.getText().toString().trim();
			final String confirmString = autoConfirmpwd.getText().toString().trim();
			
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
	
	void sendCmccMessage() {
		if(openCmccButton.isChecked()) {
			SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CMCC_NORMAL);
			SMSReceiver.enable = true;// start the sms receiver
		} else if (openCmcc5Button.isChecked()) {
			SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CMCC_5);
			SMSReceiver.enable = true;// start the sms receiver
		} else if (openCmcc10Button.isChecked()) {
			SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CMCC_10);
			SMSReceiver.enable = true;// start the sms receiver
		} else if (openCmcc20Button.isChecked()) {
			SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CMCC_20);
			SMSReceiver.enable = true;// start the sms receiver
		} else if (openCmcc30Button.isChecked()) {
			SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CMCC_30);
			SMSReceiver.enable = true;// start the sms receiver
		} else if (openCmcc50Button.isChecked()) {
			SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CMCC_50);
			SMSReceiver.enable = true;// start the sms receiver
		} else if (openCmcc100Button.isChecked()) {
			SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CMCC_100);
			SMSReceiver.enable = true;// start the sms receiver
		} else if (openCmcc200Button.isChecked()) {
			SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS, Constants.SMS_CMCC_200);
			SMSReceiver.enable = true;// start the sms receiver
		} else if (modifyCmccButton.isChecked()) {
			final String oldString = cmccOldpwd.getText().toString().trim();
			final String newString = cmccNewpwd.getText().toString().trim();
			final String confirmString = cmccConfirmpwd.getText().toString().trim();
			
			if (oldString != null && !oldString.equals("")
					&& newString != null && !newString.equals("")
					&& confirmString != null && !confirmString.equals("")
					&& newString.equals(confirmString)) {
				SMSUtil.getInstance(this).send(Constants.SMS_ADDRESS,
						Constants.SMS_MODIFY_CMCC_CONTENT + " " + oldString + " " + newString);
				SMSReceiver.enable = true;
				errorTextView2.setVisibility(View.INVISIBLE);
			} else {
				errorTextView2.setVisibility(View.VISIBLE);
			}
		}
	}

	void radioButtonCheck(int index) {
		if(index == 1) {
			autoOldpwd.setEnabled(true);
			autoOldpwd.setFocusable(true);
			autoOldpwd.setFocusableInTouchMode(true);
			autoNewpwd.setEnabled(true);
			autoNewpwd.setFocusable(true);
			autoNewpwd.setFocusableInTouchMode(true);
			autoConfirmpwd.setEnabled(true);
			autoConfirmpwd.setFocusable(true);
			autoConfirmpwd.setFocusableInTouchMode(true);
			autoOldpwd.invalidate();
			autoNewpwd.invalidate();
			autoConfirmpwd.invalidate();
			cmccOldpwd.setEnabled(false);
			cmccOldpwd.setFocusable(false);
			cmccOldpwd.setFocusableInTouchMode(false);
			cmccNewpwd.setEnabled(false);
			cmccNewpwd.setFocusable(false);
			cmccNewpwd.setFocusableInTouchMode(false);
			cmccConfirmpwd.setEnabled(false);
			cmccConfirmpwd.setFocusable(false);
			cmccConfirmpwd.setFocusableInTouchMode(false);
			cmccOldpwd.invalidate();
			cmccNewpwd.invalidate();
			cmccConfirmpwd.invalidate();
		} else if(index == 10) {
			cmccOldpwd.setEnabled(true);
			cmccOldpwd.setFocusable(true);
			cmccOldpwd.setFocusableInTouchMode(true);
			cmccNewpwd.setEnabled(true);
			cmccNewpwd.setFocusable(true);
			cmccNewpwd.setFocusableInTouchMode(true);
			cmccConfirmpwd.setEnabled(true);
			cmccConfirmpwd.setFocusable(true);
			cmccConfirmpwd.setFocusableInTouchMode(true);
			cmccOldpwd.invalidate();
			cmccNewpwd.invalidate();
			cmccConfirmpwd.invalidate();
			autoOldpwd.setEnabled(false);
			autoOldpwd.setFocusable(false);
			autoOldpwd.setFocusableInTouchMode(false);
			autoNewpwd.setEnabled(false);
			autoNewpwd.setFocusable(false);
			autoNewpwd.setFocusableInTouchMode(false);
			autoConfirmpwd.setEnabled(false);
			autoConfirmpwd.setFocusable(false);
			autoConfirmpwd.setFocusableInTouchMode(false);
			errorTextView.setVisibility(View.INVISIBLE);
			autoOldpwd.invalidate();
			autoNewpwd.invalidate();
			autoConfirmpwd.invalidate();
		} else {
			autoOldpwd.setEnabled(false);
			autoOldpwd.setFocusable(false);
			autoOldpwd.setFocusableInTouchMode(false);
			autoNewpwd.setEnabled(false);
			autoNewpwd.setFocusable(false);
			autoNewpwd.setFocusableInTouchMode(false);
			autoConfirmpwd.setEnabled(false);
			autoConfirmpwd.setFocusable(false);
			autoConfirmpwd.setFocusableInTouchMode(false);
			errorTextView.setVisibility(View.INVISIBLE);
			autoOldpwd.invalidate();
			autoNewpwd.invalidate();
			autoConfirmpwd.invalidate();
			cmccOldpwd.setEnabled(false);
			cmccOldpwd.setFocusable(false);
			cmccOldpwd.setFocusableInTouchMode(false);
			cmccNewpwd.setEnabled(false);
			cmccNewpwd.setFocusable(false);
			cmccNewpwd.setFocusableInTouchMode(false);
			cmccConfirmpwd.setEnabled(false);
			cmccConfirmpwd.setFocusable(false);
			cmccConfirmpwd.setFocusableInTouchMode(false);
			cmccOldpwd.invalidate();
			cmccNewpwd.invalidate();
			cmccConfirmpwd.invalidate();
		}
		
		for (int i = 0; i < allRadios.size(); i++) {
			RadioButton radio = allRadios.get(i);
			if(i == index) {
				radio.setChecked(true);
			} else {
				radio.setChecked(false);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.send_sms) {
			if(isAutoClicked) {
				sendAutoMessage();
			} else if(isCmccClicked) {
				sendCmccMessage();
			}
		} else if(v.getId() == R.id.traffic_btn) {
			autoBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_clicked_bg));
			autoBtn.setTextColor(getResources().getColor(R.color.white));
			cmccBtn.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
			cmccBtn.setTextColor(getResources().getColor(R.color.color_dark));
			cmccArea.setVisibility(View.GONE);
			autoArea.setVisibility(View.VISIBLE);
			isAutoClicked = true;
			isCmccClicked = false;
		} else if(v.getId() == R.id.duration_btn) {
			cmccBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_clicked_bg));
			cmccBtn.setTextColor(getResources().getColor(R.color.white));
			autoBtn.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
			autoBtn.setTextColor(getResources().getColor(R.color.color_dark));
			cmccArea.setVisibility(View.VISIBLE);
			autoArea.setVisibility(View.GONE);
			isAutoClicked = false;
			isCmccClicked = true;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView == openAutoButton && openAutoButton.isChecked()) {
			radioButtonCheck(0);
		} else if(buttonView == modifyAutoButton && modifyAutoButton.isChecked()) {
			radioButtonCheck(1);
		} else if(buttonView == openCmccButton && openCmccButton.isChecked()) {
			radioButtonCheck(2);
		} else if(buttonView == openCmcc5Button && openCmcc5Button.isChecked()) {
			radioButtonCheck(3);
		} else if(buttonView == openCmcc10Button && openCmcc10Button.isChecked()) {
			radioButtonCheck(4);
		} else if(buttonView == openCmcc20Button && openCmcc20Button.isChecked()) {
			radioButtonCheck(5);
		} else if(buttonView == openCmcc30Button && openCmcc30Button.isChecked()) {
			radioButtonCheck(6);
		} else if(buttonView == openCmcc50Button && openCmcc50Button.isChecked()) {
			radioButtonCheck(7);
		} else if(buttonView == openCmcc100Button && openCmcc100Button.isChecked()) {
			radioButtonCheck(8);
		} else if(buttonView == openCmcc200Button && openCmcc200Button.isChecked()) {
			radioButtonCheck(9);
		} else if(buttonView == modifyCmccButton && modifyCmccButton.isChecked()) {
			radioButtonCheck(10);
		}
	}
}
