/**
 * @(#)ISimpleTouch.java 2013-8-23 Copyright 2013 it.kedacom.com, Inc. All
 *                       rights reserved.
 */

package com.pc.ui.layout;

import android.view.MotionEvent;
import android.view.View;

/**
 * @author chenjian
 * @date 2013-8-23
 */

public interface ISimpleTouchListener {

	/**
	 * 触发ACTION_DOWN时触发onDown
	 * @param e
	 */
	void onDown(View v, MotionEvent e);

	void onUp(View v, MotionEvent e);

	/**
	 * 轻触触摸屏后松开，由一个1个MotionEvent ACTION_UP触发
	 * <p>
	 * onClick()和onDoubleClick()之前触发
	 * </p>
	 */
	void onSingleTapUp(View v, MotionEvent e);

	/**
	 * 单击事件
	 */
	void onClick(View v);

	/**
	 * 双击事件
	 */
	void onDoubleClick(View v);

	/**
	 * 长按
	 */
	void onLongPress(View v);

	/**
	 * 触摸移动
	 */
	void onMove(View v, int distanceX, int distanceY);

	void onMoveScroll(View v, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

}
