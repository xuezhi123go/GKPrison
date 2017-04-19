/**
 * @(#)Toast.java 2014-1-15 Copyright 2014 it.kedacom.com, Inc. All rights
 *                reserved.
 */

package com.pc.utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gkzxhn.gkprison.R;

/**
 * 自定义Toast
 * 
 * @author chenjian
 * @date 2014-1-15
 */

public class PcToast {

	private Context mContext;

	private Toast toast;
	private TextView mTextView;
	private Handler mHandler;

	public PcToast(Context context) {
		mContext = context;

		mHandler = new Handler();
		toast = new Toast(context);
		View view = LayoutInflater.from(context).inflate(R.layout.ab__toast_layout, null);
		mTextView = (TextView) view.findViewById(R.id.toastMsg);
		toast.setView(view);
	}

	/**
	 * 自定义短时间toast
	 * 
	 * @param msgResId
	 */
	public void showCustomShortToast(final int msgResId) {
		showCustomToast(msgResId, Toast.LENGTH_SHORT);
	}

	/**
	 * 自定义短时间toast
	 * 
	 * @param text
	 */
	public void showCustomShortToast(final String text) {
		showCustomToast(text, Toast.LENGTH_SHORT);
	}

	/**
	 * 自定义长时间toast
	 * 
	 * @param msgResId
	 */
	public void showCustomLongToast(final int msgResId) {
		showCustomToast(msgResId, Toast.LENGTH_LONG);
	}

	/**
	 * 自定义长时间toast
	 * 
	 * @param text
	 */
	public void showCustomLongToast(final String text) {
		showCustomToast(text, Toast.LENGTH_LONG);
	}

	/**
	 * 自定义toast
	 * 
	 * @param msgResId
	 * @param duration
	 */
	public void showCustomToast(final int msgResId, final int duration) {
		String msg = mContext.getString(msgResId);
		showCustomToast(msg, duration);
	}

	public void showCustomToast(final String msg, final int duration) {
		showCustomToast(msg, duration, false);
	}

	/**
	 * 自定义toas
	 * 
	 * @param msg
	 * @param duration
	 * @param judgeAppIsForeground
	 *            判断APP是否在前台
	 */
	public synchronized void showCustomToast(final String msg, final int duration, final boolean judgeAppIsForeground) {
		if (null == mTextView) {
			return;
		}

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// 不在前台不需要提示
				if (judgeAppIsForeground && !TerminalUtils.appIsForeground(mContext)) {
					return;
				}

				if (duration < Toast.LENGTH_SHORT) {
					toast.setDuration(Toast.LENGTH_SHORT);
				} else if (duration > Toast.LENGTH_LONG) {
					toast.setDuration(Toast.LENGTH_LONG);
				} else {
					toast.setDuration(duration);
				}
				mTextView.setText(msg);
				toast.show();
			}
		});
	}

	/**
	 * 带背景图片的toast
	 * 显示短时间LENGTH_SHORT的toast,不需要传入对应的时间
	 * 
	 * @param msg 传入字符串
	 */
	public void showWithBackGround(final int resourceId, final int resourecDrawableBg) {
		if (!TerminalUtils.appIsForeground(mContext)) {// 不在前台不需要提示
			return;
		}

		if (null == mTextView) {
			return;
		}
		
         mHandler.post(new Runnable() {

             @Override
             public void run() {
                 Toast toast = new Toast(mContext);
                 View view = LayoutInflater.from(mContext).inflate(R.layout.ab__toast_layout, null);
                 TextView textView = (TextView) view.findViewById(R.id.toastMsg);
                 textView.setText(mContext.getString(resourceId));
                 toast.setView(view);

                 textView.setText(mContext.getString(resourceId));
                 textView.setBackgroundResource(resourecDrawableBg);
                 toast.setDuration(Toast.LENGTH_SHORT);
                 toast.setGravity(Gravity.CENTER, 0, 0);
                 toast.show();
             }
         });
	}
}
