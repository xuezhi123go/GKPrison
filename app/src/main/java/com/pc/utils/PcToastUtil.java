/**
 * @(#)ToastUtil.java 2014-1-15 Copyright 2014 it.kedacom.com, Inc. All rights
 *                    reserved.
 */

package com.pc.utils;

import android.content.Context;

import com.gkzxhn.gkprison.base.MyApplication;


/**
 * 自定义Toast Util
 * 
 * @author chenjian
 * @date 2014-1-15
 */

public class PcToastUtil {

	private PcToast mCustomToast;

	private static PcToastUtil toastUtil;

	private PcToastUtil(Context _Context) {
		// if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
		// mCustomToast = new PcToast(_Context);
		// } else {
		// Looper.prepare();
		// mCustomToast = new PcToast(_Context);
		// Looper.loop();
		// }
		mCustomToast = new PcToast(_Context);
	}

	public synchronized static PcToastUtil Instance(Context _Context) {
		synchronized (PcToastUtil.class) {
			if (toastUtil == null) {
				toastUtil = new PcToastUtil(_Context);
			}
		}

		return toastUtil;
	}

	public synchronized static PcToastUtil Instance() {
		synchronized (PcToastUtil.class) {
			if (toastUtil == null) {
				toastUtil = new PcToastUtil(MyApplication.getContext());
			}
		}

		return toastUtil;
	}

	public synchronized static void releaseToast() {
		toastUtil = null;
	}

	/**
	 * 自定义短时间toast
	 * @param msgResId
	 */
	public synchronized void showCustomShortToast(final int msgResId) {
		mCustomToast.showCustomShortToast(msgResId);
	}

	/**
	 * 自定义短时间toast
	 * @param text
	 */
	public synchronized void showCustomShortToast(final String text) {
		mCustomToast.showCustomShortToast(text);
	}

	/**
	 * 自定义长时间toast
	 * @param msgResId
	 */
	public synchronized void showCustomLongToast(final int msgResId) {
		mCustomToast.showCustomLongToast(msgResId);
	}

	/**
	 * 自定义长时间toast
	 * @param text
	 */
	public synchronized void showCustomLongToast(final String text) {
		mCustomToast.showCustomLongToast(text);
	}

	/**
	 * 自定义toast
	 * @param msgResId
	 * @param duration
	 */
	public synchronized void showCustomToast(final int msgResId, final int duration) {
		mCustomToast.showCustomToast(msgResId, duration);
	}

	public synchronized void showCustomToast(final String msg, final int duration) {
		mCustomToast.showCustomToast(msg, duration);
	}

	/**
	 * 自定义toas
	 * @param msg
	 * @param duration
	 * @param judgeAppIsForeground 判断APP是否在前台
	 */
	public synchronized void showCustomToast(final String msg, final int duration, final boolean judgeAppIsForeground) {
		mCustomToast.showCustomToast(msg, duration, judgeAppIsForeground);
	}

	/**
	 * 带背景图片的toast<br>
	 * 显示短时间LENGTH_SHORT的toast,不需要传入对应的时间
	 * @param msg 传入字符串
	 */
	public synchronized void showWithBackGround(final int resourceId, int resourecDrawableBg) {
		mCustomToast.showWithBackGround(resourceId, resourecDrawableBg);
	}

}
