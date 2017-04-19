/**
 * @(#)ActivityUtils.java 2014-2-18 Copyright 2014 it.kedacom.com, Inc. All
 *                        rights reserved.
 */

package com.pc.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * startActivity工具
 * 
 * @author chenjian
 * @date 2014-2-18
 */

public class ActivityUtils {

	/**
	 * 打开Activity
	 *
	 * @param activity 当前Activity
	 * @param intent Intent
	 * @param options Bundle
	 */
	public static void openActivity(Activity activity, Intent intent, Bundle options) {
		if (null == activity || null == intent) return;

		activity.startActivity(intent, options);
	}

	/**
	 * 打开Activity
	 *
	 * @param activity 当前Activity
	 * @param intent Intent
	 */
	public static void openActivity(Activity activity, Intent intent) {
		if (null == activity || null == intent) return;

		activity.startActivity(intent);
	}

	/**
	 * 打开Activity
	 *
	 * @param activity 当前Activity
	 * @param pClass 目标Activity
	 */
	public static void openActivity(Activity activity, Class<?> pClass) {
		openActivity(activity, pClass, null, -1, -1, -1);
	}

	/**
	 * 打开Activity
	 * 
	 * @param activity 当前Activity
	 * @param pClass 目标Activity
	 * @param pBundle Bundle
	 */
	public static void openActivity(Activity activity, Class<?> pClass, Bundle pBundle) {
		openActivity(activity, pClass, pBundle, -1, -1, -1);
	}

	/**
	 * 打开Activity
	 *
	 * @param activity 当前Activity
	 * @param pClass 目标Activity
	 * @param pBundle Bundle
	 * @param requestCode 返回码
	 */
	public static void openActivity(Activity activity, Class<?> pClass, Bundle pBundle, int requestCode) {
		openActivity(activity, pClass, pBundle, requestCode, -1, -1);
	}

	/**
	 * 打开Activity
	 * 
	 * @param activity 当前Activity
	 * @param pClass 目标Activity
	 * @param enterAnim 进入动画
	 * @param exitAnim 退出动画
	 */
	public static void openActivity(Activity activity, Class<?> pClass, int enterAnim, int exitAnim) {
		openActivity(activity, pClass, null, -1, enterAnim, exitAnim);
	}

	/**
	 * 打开Activity
	 *
	 * @param activity 当前Activity
	 * @param pClass 目标Activity
	 * @param requestCode 返回码
	 */
	public static void openActivity(Activity activity, Class<?> pClass, int requestCode) {
		openActivity(activity, pClass, null, requestCode, -1, -1);
	}

	/**
	 * 打开Activity
	 *
	 * @param activity 当前Activity
	 * @param pAction Action
	 * @param requestCode 返回码
	 */
	public static void openActivity(Activity activity, String pAction, int requestCode) {
		openActivity(activity, pAction, null, requestCode);
	}

	/**
	 * 打开Activity
	 *
	 * @param activity 当前Activity
	 * @param pClass 目标Activity
	 * @param pAction Action
	 * @param pBundle Bundle
	 * @param requestCode 返回码
	 */
	public static void openActivity(Activity activity, Class<?> pClass, String pAction, Bundle pBundle, int requestCode) {
		if (null == activity) return;

		Intent intent = new Intent(pAction);
		intent.setClass(activity, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}

		if (requestCode < 0) {
			activity.startActivity(intent);
		} else {
			activity.startActivityForResult(intent, requestCode);
		}
	}

	/**
	 * 打开Activity
	 * 
	 * @param activity 当前Activity
	 * @param pAction Action
	 * @param pBundle Bundle
	 * @param requestCode 返回码
	 */
	public static void openActivity(Activity activity, String pAction, Bundle pBundle, int requestCode) {
		if (null == activity) return;

		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}

		if (requestCode < 0) {
			activity.startActivity(intent);
		} else {
			activity.startActivityForResult(intent, requestCode);
		}
	}

	/**
	 * 打开Activity
	 *
	 * @param activity 当前Activity
	 * @param intent Intent
	 * @param requestCode 返回码
	 */
	public static void openActivity(Activity activity, Intent intent, int requestCode) {
		openActivity(activity, intent, requestCode, -1, -1);
	}

	/**
	 * 打开Activity
	 * 
	 * @param activity 当前Activity
	 * @param intent Intent
	 * @param requestCode 返回码
	 * @param enterAnim 进入动画
	 * @param exitAnim 退出动画
	 */
	public static void openActivity(Activity activity, Intent intent, int requestCode, int enterAnim, int exitAnim) {
		if (null == activity) return;

		if (requestCode < 0) {
			activity.startActivity(intent);
		} else {
			activity.startActivityForResult(intent, requestCode);
		}

		if (enterAnim > 0 && exitAnim > 0) {
			activity.overridePendingTransition(enterAnim, exitAnim);
		}
	}

	/**
	 * 打开Activity
	 * 
	 * @param activity 当前Activity
	 * @param pClass 目标Activity
	 * @param requestCode 返回码
	 * @param enterAnim 进入动画
	 * @param exitAnim 退出动画
	 */
	public static void openActivity(Activity activity, Class<?> pClass, int requestCode, int enterAnim, int exitAnim) {
		openActivity(activity, pClass, null, requestCode, enterAnim, exitAnim);
	}

	/**
	 * 打开Activity
	 *
	 * @param activity 当前Activity
	 * @param pClass 目标Activity
	 * @param pBundle Bundle
	 * @param requestCode 返回码
	 * @param enterAnim 进入动画
	 * @param exitAnim 退出动画
	 */
	public static void openActivity(Activity activity, Class<?> pClass, Bundle pBundle, int requestCode, int enterAnim, int exitAnim) {
		if (null == activity) return;

		Intent intent = new Intent(activity, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}

		if (requestCode < 0) {
			activity.startActivity(intent);
		} else {
			activity.startActivityForResult(intent, requestCode);
		}

		if (enterAnim > 0 && exitAnim > 0) {
			activity.overridePendingTransition(enterAnim, exitAnim);
		}
	}

}
