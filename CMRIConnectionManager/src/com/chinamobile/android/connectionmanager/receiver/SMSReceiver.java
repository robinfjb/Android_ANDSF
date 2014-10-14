package com.chinamobile.android.connectionmanager.receiver;

//import java.util.Timer;
//import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.ui.AccountActivity;
import com.chinamobile.android.connectionmanager.ui.AccountIsOpenDialog;
import com.chinamobile.android.connectionmanager.ui.AccountOpenedOnceDialog;
import com.chinamobile.android.connectionmanager.ui.SMSCenterActivity;
import com.chinamobile.android.connectionmanager.util.SMSUtil;

/**
 * receiver to receive sms
 *
 */
public class SMSReceiver extends BroadcastReceiver {
	public static final String ACTION_SEND_SMS = "com.chinamobile.android.connectionmanager.ui.sms.send";
	public static final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
	private static final String SMS_USER_NAME = "[1][\\d]{10}";
	private static final String SMS_PASSWORD = "\\d{6}";
	private static final String SMS_CONFIRM = "您将开通WLAN自动认证专属资费";
	private static final String SMS_HAVE_OPENED = "由于您已办理了WLAN自动认证专属资费，无须重复办理";
	private static final String SMS_OPENED_SUCCESS = "成功开通了WLAN自动认证专属资费";
	private static final String SMS_CHANGE_PSW_SUCCESS = "已成功修改WLAN自动认证密码";

	private static final String TAG = "SMSReceiver";
	private Runnable task; // the account info sms maybe receive later than the
							// other sms,
	private Handler timer = new Handler();
	public static boolean enable;
	// if not receive any sms in 10s, then regard as failed
	@Override
	public void onReceive(final Context context, Intent intent) {
		if(!enable) {
			return;
		}
		String action = intent.getAction();
		if (action.equals(ACTION_SEND_SMS)) {
			try {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(
							context,
							context.getResources()
									.getText(R.string.str_sms_sent_success)
									.toString(), Toast.LENGTH_LONG).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(
							context,
							context.getResources()
									.getText(R.string.str_sms_sent_failed)
									.toString(), Toast.LENGTH_LONG).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(
							context,
							context.getResources()
									.getText(R.string.str_sms_sent_failed)
									.toString(), Toast.LENGTH_LONG).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(
							context,
							context.getResources()
									.getText(R.string.str_sms_sent_failed)
									.toString(), Toast.LENGTH_LONG).show();
					break;
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		} else if (action.equals(ACTION_RECEIVE_SMS)) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String[] result;
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] msg = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				for (SmsMessage currMsg : msg) {
					if (!currMsg.getDisplayOriginatingAddress().equals("10086")) {
						continue;
					}
					String content = currMsg.getDisplayMessageBody();
					System.out.println(content);

					if (sendConfirmSMS(context,
							currMsg.getDisplayOriginatingAddress(), content))
						break;

					result = getUserNameAndPsw(context, content);
					if (result[0] != null && !result[0].trim().equals("")
							&& result[1] != null
							&& !result[1].trim().equals("")) {
						AppApplication.peap_username = result[0];
						AppApplication.peap_password = result[1];
						enable = false;
						System.out.println("name:" + result[0]);
						System.out.println("password:" + result[1]);
						Intent autoFillIntent = new Intent(
								AccountActivity.ACCOUNT_AUTO_FILL_ACTION);
						autoFillIntent.putExtra("fill_account", result[0]);
						autoFillIntent.putExtra("fill_pwd", result[1]);
						context.sendBroadcast(autoFillIntent);

						Editor editor = context.getSharedPreferences(
								AppApplication.SP_DATA_NAME,
								Context.MODE_PRIVATE).edit();
						editor.putString("peap_username",
								AppApplication.peap_username);
						editor.putString("peap_password",
								AppApplication.peap_password);
						editor.commit();

						if (task != null) {
							timer.removeCallbacks(task);
						}

						Toast.makeText(
								context,
								context.getResources().getString(
										R.string.sms_finish_msg),
								Toast.LENGTH_SHORT).show();

						break;
					}

					if (AccountIsOpened(context, content)) {
						enable = false;
						break;
					}

					if (AccountOpenedOnce(context, content)) {
						if (task != null) {
							timer.removeCallbacks(task);
						}
						task = new Runnable() {

							@Override
							public void run() {
								enable = false;
							}
						};
						timer.postDelayed(task, 10000);
					}

				}
			}
		}

	}

	/**
	 * send the confirm sms after receive the first sms
	 * <p>it is different in different provinces
	 * @param context
	 * @param address
	 * @param content
	 * @return
	 */
	private boolean sendConfirmSMS(Context context, String address,
			String content) {
		Pattern pConfirm = Pattern.compile(SMS_CONFIRM);
		Matcher mConfirm = pConfirm.matcher(content);
		if (mConfirm.find()) {
			SMSUtil.getInstance(context).send(address, "是");
			return true;
		}
		return false;
	}

	/**
	 * the account is already opened
	 * @param context
	 * @param content
	 * @return
	 */
	private boolean AccountIsOpened(Context context, String content) {
		Pattern openedP = Pattern.compile(SMS_HAVE_OPENED);
		Matcher openedM = openedP.matcher(content);
		if (openedM.find()) {
			Intent intent = new Intent(context, AccountIsOpenDialog.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return true;
		}
		return false;
	}

	/**
	 * reteive user name and password
	 * @param context
	 * @param content
	 * @return
	 */
	private String[] getUserNameAndPsw(Context context, String content) {
		String[] result = new String[2];
		Pattern p1 = Pattern.compile(SMS_USER_NAME);
		Matcher m1 = p1.matcher(content);
		if (m1.find()) {
			Pattern p11 = Pattern.compile("[1][\\d]{10}");
			Matcher m11 = p11.matcher(m1.group());
			if (m11.find()) {
				result[0] = m11.group();
			}
			Pattern p2 = Pattern.compile(SMS_PASSWORD);
			Matcher m2 = p2.matcher(content.replace(result[0], ""));
			if (m2.find()) {
				Pattern p22 = Pattern.compile("\\d{6}");
				Matcher m22 = p22.matcher(m2.group());
				if (m22.find()) {
					result[1] = m22.group();
				}
			}
		}

		return result;
	}

	/**
	 * account opened once in this month, the sms is different
	 * @param context
	 * @param content
	 * @return
	 */
	private boolean AccountOpenedOnce(Context context, String content) {
		Pattern openedP = Pattern.compile(SMS_OPENED_SUCCESS);
		Matcher openedM = openedP.matcher(content);
		if (openedM.find()) {
			Intent intent = new Intent(context, AccountOpenedOnceDialog.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return true;
		}
		return false;
	}

}
