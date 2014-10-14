package com.chinamobile.android.connectionmanager.ui;

import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.TextView;

import com.chinamobile.android.connectionmanager.AppApplication;

/**
 * Class that implements a simple timer.
 * <p>
 * You can give it a start time in the {@link SystemClock#elapsedRealtime} timebase,
 * and it counts up from that, or if you don't give it a base time, it will use the
 * time at which you call {@link #start}.  By default it will display the current
 * timer value in the form "MM:SS" or "H:MM:SS", or you can use {@link #setFormat}
 * to format the timer value into an arbitrary string.
 *
 * @attr ref android.R.styleable#Chronometer_format
 */
public class CMChronometer extends TextView {
	private static final String TAG = "CMChronometer";

	private long mBase;
	private boolean mVisible;
	private boolean mStarted;
	private boolean mRunning;
	private boolean mLogged;
	private String mFormat;
	private Formatter mFormatter;
	private Locale mFormatterLocale;
	private Object[] mFormatterArgs = new Object[1];
	private StringBuilder mFormatBuilder;
	private StringBuilder mRecycle = new StringBuilder(8);

	private static final int TICK_WHAT = 2;

	public CMChronometer(Context context) {
		this(context, null, 0);
	}

	public CMChronometer(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CMChronometer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mBase = SystemClock.elapsedRealtime();
		updateText(mBase);
	}

	/**
	 * Set the time that the count-up timer is in reference to.
	 * 
	 * @param base
	 *            Use the {@link SystemClock#elapsedRealtime} time base.
	 */
	public void setBase(long base) {
		mBase = base;
		updateText(SystemClock.elapsedRealtime());
	}

	/**
	 * Return the base time as set through {@link #setBase}.
	 */
	public long getBase() {
		return mBase;
	}

	/**
	 * Sets the format string used for display. The Chronometer will display
	 * this string, with the first "%s" replaced by the current timer value in
	 * "MM:SS" or "H:MM:SS" form.
	 * 
	 * If the format string is null, or if you never call setFormat(), the
	 * Chronometer will simply display the timer value in "MM:SS" or "H:MM:SS"
	 * form.
	 * 
	 * @param format
	 *            the format string.
	 */
	public void setFormat(String format) {
		mFormat = format;
		if (format != null && mFormatBuilder == null) {
			mFormatBuilder = new StringBuilder(format.length() * 2);
		}
	}

	/**
	 * Returns the current format string as set through {@link #setFormat}.
	 */
	public String getFormat() {
		return mFormat;
	}

	/**
	 * Start counting up. This does not affect the base as set from
	 * {@link #setBase}, just the view display.
	 * 
	 * Chronometer works by regularly scheduling messages to the handler, even
	 * when the Widget is not visible. To make sure resource leaks do not occur,
	 * the user should make sure that each start() call has a reciprocal call to
	 * {@link #stop}.
	 */
	public void start() {
		mStarted = true;
		AppApplication.isTiming = true;
		updateRunning();
	}

	/**
	 * Stop counting up. This does not affect the base as set from
	 * {@link #setBase}, just the view display.
	 * 
	 * This stops the messages to the handler, effectively releasing resources
	 * that would be held as the chronometer is running, via {@link #start}.
	 */
	public void stop() {
		mStarted = false;
		AppApplication.time_base = SystemClock.elapsedRealtime();
		setBase(AppApplication.time_base);
		AppApplication.isTiming = false;
		updateRunning();
	}

	/**
	 * The same as calling {@link #start} or {@link #stop}.
	 * 
	 * @hide pending API council approval
	 */
	public void setStarted(boolean started) {
		mStarted = started;
		updateRunning();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mVisible = false;
		updateRunning();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		mVisible = visibility == VISIBLE;
		updateRunning();
	}

	/**
	 * uppdate the text
	 * @param now
	 */
	private synchronized void updateText(long now) {
		long seconds = now - mBase;
		seconds /= 1000;
		String text = DateUtils.formatElapsedTime(mRecycle, seconds);

		if (mFormat != null) {
			Locale loc = Locale.getDefault();
			if (mFormatter == null || !loc.equals(mFormatterLocale)) {
				mFormatterLocale = loc;
				mFormatter = new Formatter(mFormatBuilder, loc);
			}
			mFormatBuilder.setLength(0);
			mFormatterArgs[0] = text;
			try {
				mFormatter.format(mFormat, mFormatterArgs);
				text = mFormatBuilder.toString();
			} catch (IllegalFormatException ex) {
				if (!mLogged) {
					Log.w(TAG, "Illegal format string: " + mFormat);
					mLogged = true;
				}
			}
		}
		setText(text);
	}

	private void updateRunning() {
		boolean running = mVisible && mStarted;
		if (running != mRunning) {
			if (running) {
				updateText(SystemClock.elapsedRealtime());
				mHandler.sendMessageDelayed(
						Message.obtain(mHandler, TICK_WHAT), 1000);
			} else {
				mHandler.removeMessages(TICK_WHAT);
			}
			mRunning = running;
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message m) {
			if (mRunning) {
				updateText(SystemClock.elapsedRealtime());
				sendMessageDelayed(Message.obtain(this, TICK_WHAT), 1000);
			}
		}
	};
}
