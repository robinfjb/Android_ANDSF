package com.chinamobile.android.connectionmanager.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.chinamobile.android.connectionmanager.model.PolicyModel.TimeAndDate;

import android.text.format.Time;

public class TimeUtil {
	/**
	 * return current full time format as YYYYMMDDHHMMSS
	 * 
	 * @return long
	 */
	public static long getNowFullTime() {
		Time time = new Time();
		time.setToNow();
		String fullTime = time.format2445();
		return Long.parseLong(fullTime.substring(0, 8) + fullTime.substring(9));
	}

	/**
	 * return current time format as HHMMSS
	 * @return int
	 */
	public static int getNowTime() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		return Integer.parseInt(timeFormat.format(new Date()));
	}
	
	/**
	 * return current date format as YYYYMMDD
	 * @return int
	 */
	public static int getNowDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return Integer.parseInt(dateFormat.format(new Date()));
	}
	
	/**
	 * return custom date base of today, format as YYYYMMDD
	 * @param day, the number of day after or before, 0 represents today
	 * @return int
	 */
	public static int getDateCompareToday(int day) {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		date = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return Integer.parseInt(dateFormat.format(date));
	}
	
	/**
	 * calculate the time distance between now and target time in millisecond
	 * @param day
	 * @param time
	 * @return millisecond
	 */
	public static long calculateBySecondsCompareNow(long timeday) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		ParsePosition pos = new ParsePosition(0);
		Date dt1 = formatter.parse(String.valueOf(timeday), pos);
		Date date = new Date();
		return dt1.getTime() - date.getTime();
		
	}
	
	/**
	 * validate the system time by comparing with time and date in policy
	 * @param timeAndDate
	 * @return the status of time: {@link TimeAndDate#VALID}, {@link TimeAndDate#INVALID}
	 * , {@link TimeAndDate#EXPIRED}
	 * @see TimeThread
	 */
	public static int validateTime(TimeAndDate timeAndDate) {
//		Log.d(TAG, "validateTime---->" + timeAndDate.toString());
		int startTime = timeAndDate.getStartTime();
		int endTime = timeAndDate.getEndTime();
		int startDate = timeAndDate.getStartDate();
		int endDate = timeAndDate.getEndDate();
		if (startDate < 1 || endDate < 1) {
			int nowTime = getNowTime();
			if (nowTime >= startTime && nowTime <= endTime) {
				return TimeAndDate.VALID;
			} else if (nowTime < startTime) {
				return TimeAndDate.FUTURE;
			} else if (nowTime > endTime) {
				return TimeAndDate.EXPIRED;
			}
		} else {
			long nowFullTime = getNowFullTime();
			long restraintStartTime = (long) (startDate * 1E6 + startTime);
			long restraintEndTime = (long) (endDate * 1E6 + endTime);
			if (nowFullTime >= restraintStartTime
					&& nowFullTime <= restraintEndTime) {
				return TimeAndDate.VALID;
			} else if (nowFullTime < restraintStartTime) {
				return TimeAndDate.FUTURE;
			} else if (nowFullTime > restraintEndTime) {
				return TimeAndDate.EXPIRED;
			}
		}
		return 0;
	}
	
	/**
	 * check time data 
	 * <br>if invalid, throw exception
	 * @param data
	 * @throws Throwable
	 */
	public static void checkTimeData(TimeAndDate data) throws Throwable {
		Throwable throwable = new Throwable("updatePolicyStats------->error data!!");
		final int startTime = data.getStartTime();
		final int endTime = data.getEndTime();
		final int startDate = data.getStartDate();
		final int endDate = data.getEndDate();
		final long startDateTime = data.getStartDateTime();
		final long endDateTime = data.getEndDateTime();
		
		CommonUtil.checkPair(startTime, endTime);
		CommonUtil.checkPair(startDate, endDate);
		CommonUtil.checkPair(startDateTime, endDateTime);
		
		if((startDate != -1 && startDate < Constants.MIN_DATE) || startDateTime > endDateTime) {
			throw throwable;
		}
	}
}
