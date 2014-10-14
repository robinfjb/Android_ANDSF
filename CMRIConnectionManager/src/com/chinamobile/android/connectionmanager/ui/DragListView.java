/*package com.chinamobile.android.connectionmanager.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class DragListView extends ListView {
	private ImageView dragImageView;// the dragged image
	private int dragSrcPosition;// the original position
	private int dragPosition;// the position when dragged
	private int dragPoint;// the position after dragged
	private int dragOffset;// the dragged image offset of screen top

	private WindowManager windowManager;
	private WindowManager.LayoutParams windowParams;

	private int scaledTouchSlop;// the touch minus distance
	private int upScrollboundary;// the boundary of start scroll up
	private int downScrollboundary;// the boundary of start scroll down

	private DragListener mDragListener;

	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// to intercept down action
		if (ev.getAction() == MotionEvent.ACTION_DOWN && mDragListener != null) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();

			dragSrcPosition = dragPosition = pointToPosition(x, y);
			if (dragPosition == AdapterView.INVALID_POSITION) {
				return super.onInterceptTouchEvent(ev);
			}

			View itemView = (View) getChildAt(dragPosition
					- getFirstVisiblePosition());
			dragPoint = y - itemView.getTop();
			dragOffset = (int) (ev.getRawY() - y);

			upScrollboundary = Math.min(y - scaledTouchSlop, getHeight() / 3);
			downScrollboundary = Math.max(y + scaledTouchSlop,
					getHeight() * 2 / 3);

			
//			Log.d("DragListView", "dragPoint--->" + dragPoint);
//			Log.d("DragListView", "dragOffset--->" + dragOffset);
			itemView.setDrawingCacheEnabled(true);
			Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
			startDrag(bm, y);
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (dragImageView != null && dragPosition != INVALID_POSITION && mDragListener != null) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				int upY = (int) ev.getY();
				stopDrag();
				onDrop(upY);
				break;
			case MotionEvent.ACTION_MOVE:
				int moveY = (int) ev.getY();
				onDrag(moveY);
				break;
			default:
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	public void startDrag(Bitmap bm, int y) {
		stopDrag();

		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP;
		windowParams.x = 0;
		windowParams.y = y - dragPoint + dragOffset;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView imageView = new ImageView(getContext());
		imageView.setImageBitmap(bm);
		windowManager = (WindowManager) getContext().getSystemService("window");
		windowManager.addView(imageView, windowParams);
		dragImageView = imageView;
	}

	public void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	public void onDrag(int y) {
		if (dragImageView != null) {
			windowParams.alpha = 0.8f;
			windowParams.y = y - dragPoint + dragOffset;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}

		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}

//		Log.d("DragListView", "dragSrcPosition--->" + dragSrcPosition);
//		Log.d("DragListView", "dragPosition--->" + dragPosition);
		int scrollHeight = 0;
		if (y < upScrollboundary) {
			scrollHeight = 8;
		} else if (y > downScrollboundary) {
			scrollHeight = -8;
		}

		if (scrollHeight != 0) {
			setSelectionFromTop(dragPosition,
					getChildAt(dragPosition - getFirstVisiblePosition())
							.getTop() + scrollHeight);
		}
	}

	public void onDrop(int y) {

		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}

		if (y < getChildAt(1).getTop()) {
			// out of up boundary
			dragPosition = 0;
		} else if (y > getChildAt(getChildCount() - 1).getBottom()) {
			// out of below boundary
			dragPosition = getAdapter().getCount() - 1;
		}

		// data exchange
		if (dragPosition >= 0 && dragPosition < getAdapter().getCount()) {
			mDragListener.drag(dragSrcPosition, dragPosition);
			@SuppressWarnings("unchecked")
			ArrayAdapter<String> adapter = (ArrayAdapter<String>) getAdapter();
			String dragItem = adapter.getItem(dragSrcPosition);
			adapter.remove(dragItem);
			adapter.insert(dragItem, dragPosition);
		}

	}

	public interface DragListener {
		void drag(int from, int to);
	}

	public void setDragListener(DragListener l) {
		mDragListener = l;
	}
}
*/