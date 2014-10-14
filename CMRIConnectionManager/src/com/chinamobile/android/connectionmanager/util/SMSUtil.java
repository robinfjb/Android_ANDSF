package com.chinamobile.android.connectionmanager.util;

import java.util.ArrayList;

import com.chinamobile.android.connectionmanager.ui.AccountActivity;
import com.chinamobile.android.connectionmanager.ui.SMSCenterActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * util for send sms and receive sms
 */
public class SMSUtil {
	public Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	private ArrayList<SMSItem> mSmsList = new ArrayList<SMSItem>();
	public static SMSUtil instance;
	private Context mContext;

	public SMSUtil(Context context) {
		mContext = context;
	}
	
	public static SMSUtil getInstance(Context context) {
		if(instance == null) {
			instance = new SMSUtil(context);
		}
		
		return instance;
	}

	/**
	 * get {@link SMSItem}
	 * @param idx
	 * @return
	 */
	SMSItem get(int idx) {
		return mSmsList.get(idx);
	}

	int count() {
		return mSmsList.size();
	}
	
	/**
	 * get list of{@link SMSItem}
	 * @return
	 */
	public ArrayList<SMSItem> getAll() {
		return mSmsList;
	}

	/**
	 * send a sms to target address
	 * @param address
	 * @param content
	 */
	public void send(String address, String content) {
		 SmsManager sms = SmsManager.getDefault();
		 PendingIntent mPI = PendingIntent.getBroadcast(mContext, 0, new
				 Intent(SMSCenterActivity.ACTION_SEND_SMS), 0);
		 Log.d("SMSUtil", "address:" + address + "|  |" + "content:" + content);
		 sms.sendTextMessage(address, null, content, mPI, null);
	}
	
	@SuppressWarnings("deprecation")
	@Deprecated
	public int read(Activity activity) {
		Cursor cur = activity.managedQuery(SMS_INBOX, null, null, null, null);
		if (cur != null && cur.moveToFirst()) {
			SMSItem.initIdx(cur);
			do {
				SMSItem item = new SMSItem(cur);
				mSmsList.add(item);
			} while (cur.moveToNext());
		}
		return count();
	}

	@Deprecated
	public int read(Activity activity, String address) {
		Cursor cur = activity.getContentResolver().query(SMS_INBOX, null, 
				" address=?", new String[] { address }, "date desc");
		if (cur != null && cur.moveToFirst()) {
			SMSItem.initIdx(cur);
			do {
				SMSItem item = new SMSItem(cur);
				mSmsList.add(item);
			} while (cur.moveToNext());
		}
		return count();
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	/*public ContactItem getContact(Activity activity, final SMSItem sms) {
		if (sms.mPerson == 0)
			return null;
		Cursor cur = activity.managedQuery(
				ContactsContract.Contacts.CONTENT_URI,
				new String[] { PhoneLookup.DISPLAY_NAME }, " _id=?",
				new String[] { String.valueOf(sms.mPerson) }, null);
		if (cur != null && cur.moveToFirst()) {
			int idx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			ContactItem item = new ContactItem();
			item.mName = cur.getString(idx);
			return item;
		}
		return null;
	}*/
	
	/**
	 * SMS model
	 *
	 */
	public static class SMSItem {
		public static final String ID = "_id";
		public static final String THREAD = "thread_id";
		public static final String ADDRESS = "address";
		public static final String PERSON = "person";
		public static final String DATE = "date";
		public static final String READ = "read";
		public static final String BODY = "body";
		public static final String SUBJECT = "subject";

		public String mAddress;
		public String mBody;
		public String mSubject;
		public long mID;
		public long mThreadID;
		public long mDate;
		public long mRead;
		public long mPerson;

		private static int mIdIdx;
		private static int mThreadIdx;
		private static int mAddrIdx;
		private static int mPersonIdx;
		private static int mDateIdx;
		private static int mReadIdx;
		private static int mBodyIdx;
		private static int mSubjectIdx;

		public SMSItem(Cursor cur) {
			mID = cur.getLong(mIdIdx);
			mThreadID = cur.getLong(mThreadIdx);
			mAddress = cur.getString(mAddrIdx);
			mPerson = cur.getLong(mPersonIdx);
			mDate = cur.getLong(mDateIdx);
			mRead = cur.getLong(mReadIdx);
			mBody = cur.getString(mBodyIdx);
			mSubject = cur.getString(mSubjectIdx);
		}

		public static void initIdx(Cursor cur) {
			mIdIdx = cur.getColumnIndex(ID);
			mThreadIdx = cur.getColumnIndex(THREAD);
			mAddrIdx = cur.getColumnIndex(ADDRESS);
			mPersonIdx = cur.getColumnIndex(PERSON);
			mDateIdx = cur.getColumnIndex(DATE);
			mReadIdx = cur.getColumnIndex(READ);
			mBodyIdx = cur.getColumnIndex(BODY);
			mSubjectIdx = cur.getColumnIndex(SUBJECT);
		}

		public String toString() {
			String ret = ID + ":" + String.valueOf(mID) + " " + THREAD + ":"
					+ String.valueOf(mThreadID) + " " + ADDRESS + ":" + mAddress
					+ " " + PERSON + ":" + String.valueOf(mPerson) + " " + DATE
					+ ":" + String.valueOf(mDate) + " " + READ + ":"
					+ String.valueOf(mRead) + " " + SUBJECT + ":" + mSubject + " "
					+ BODY + ":" + mBody;
			return ret;
		}
	}
}
