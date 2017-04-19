package com.keda.vconf.manager;

/**
 * @(#)VideoCapServiceManager.java 2013-8-14 Copyright 2013 it.kedacom.com, Inc.
 *                                 All rights reserved.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gkzxhn.gkprison.base.MyApplication;
import com.keda.vconf.modle.service.VideoCapService;
import com.keda.vconf.modle.service.VideoCapServiceConnect;

/**
 * @author chenjian
 * @date 2013-8-14
 */

public class VideoCapServiceManager {

	private static boolean mIsBindService = false;
	private static VideoCapServiceConnect mVideoCapServiceConnect;

	public static VideoCapServiceConnect getVideoCapServiceConnect() {
		return mVideoCapServiceConnect;
	}

	/**
	 * 绑定采集视频服务
	 * 
	 */
	public synchronized static void bindService() {
		// 已经绑定了采集视频服务，不需要再次绑定
		if (mVideoCapServiceConnect != null) {
			return;
		}

		if (mIsBindService) {
			return;
		}

		Context context = MyApplication.getApplication();
		if (context == null) {
			return;
		}

		Intent service = new Intent(context, VideoCapService.class);
		mVideoCapServiceConnect = new VideoCapServiceConnect();
		mIsBindService = context.bindService(service, mVideoCapServiceConnect, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 解绑定采集视频服务
	 * 
	 */
	public synchronized static void unBindService() {
		if (null == mVideoCapServiceConnect) {
			return;
		}

		Context context = MyApplication.getApplication();
		if (context == null) {
			return;
		}

		if (mIsBindService) {
			mIsBindService = false;

			Log.w("vconf service", "VideoCapService unBindService...");

			context.unbindService(mVideoCapServiceConnect);
		}
		mVideoCapServiceConnect = null;
	}

}
