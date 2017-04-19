/**
 * @(#)SimpleGestureDetectorFrame.java 2013-9-3 Copyright 2013 it.kedacom.com,
 *                                     Inc. All rights reserved.
 */

package com.pc.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 带简单手势的Layout
 * @author chenjian
 * @date 2013-9-3
 */

public class SimpleGestureDetectorRelative extends RelativeLayout {

	// 当前触摸X、Y坐标
	private int mCurrX = 0;
	private int mCurrY = 0;

	private View mView;

	private GestureDetector mGestureDetector;
	private ISimpleTouchListener mSimpleTouchListener;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SimpleGestureDetectorRelative(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SimpleGestureDetectorRelative(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 */
	public SimpleGestureDetectorRelative(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mView = this;
		init();
	}

	private void init() {
		mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				if (null != mSimpleTouchListener) {
					mSimpleTouchListener.onDown(mView, e);
				}

				return super.onDown(e);
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				if (null != mSimpleTouchListener) {
					mSimpleTouchListener.onSingleTapUp(mView, e);
				}

				return true;
			}

			// single Click : onSingleTapUp-->onSingleTapConfirmed
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				if (null != mSimpleTouchListener) {
					mSimpleTouchListener.onClick(mView);
				}

				return true;
			}

			/**
			 * Double Click:按两下的第二下Touch down时触发,onSingleTapUp-->onDoubleTap
			 */
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				if (null != mSimpleTouchListener) {
					mSimpleTouchListener.onDoubleClick(mView);
				}

				return true;
			}

			// long click
			@Override
			public void onLongPress(MotionEvent e) {
				if (null != mSimpleTouchListener) {
					mSimpleTouchListener.onLongPress(mView);
				}

				super.onLongPress(e);
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				if (null != mSimpleTouchListener) {
					mSimpleTouchListener.onMoveScroll(mView, e1, e2, distanceX, distanceY);
				}

				return super.onScroll(e1, e2, distanceX, distanceY);
			}
		});
	}

	/**
	 * @param mSimpleTouchListener the mSimpleTouchListener to set
	 */
	public void setOnSimpleTouchListener(ISimpleTouchListener simpleTouchListener) {
		this.mSimpleTouchListener = simpleTouchListener;
		if (null == this.mSimpleTouchListener) {
			setLongClickable(false);
			setOnTouchListener(null);
			return;
		}

		setLongClickable(true);

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean handled = false;
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mCurrX = (int) event.getRawX();
						mCurrY = (int) event.getRawY();
						break;

					case MotionEvent.ACTION_MOVE:
						int dx = Math.round(event.getRawX() - mCurrX);
						int dy = Math.round(event.getRawY() - mCurrY);

						if (null != mSimpleTouchListener) {
							mSimpleTouchListener.onMove(mView, dx, dy);
						}

						mCurrX = (int) event.getRawX();
						mCurrY = (int) event.getRawY();
						break;

					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						if (null != mSimpleTouchListener) {
							mSimpleTouchListener.onUp(mView, event);
						}
						handled = true;
						break;
				}

				if (null != mGestureDetector && mGestureDetector.onTouchEvent(event)) {
					handled = true;
				}

				return handled;
			}
		});
	}

	/** @return the mSimpleTouchListener */
	public ISimpleTouchListener getSimpleTouchListener() {
		return mSimpleTouchListener;
	}

}
