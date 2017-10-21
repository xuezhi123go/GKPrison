/**
 * @(#)VideoCapServiceConnect.java 2013-8-14 Copyright 2013 it.kedacom.com, Inc.
 *                                 All rights reserved.
 */

package com.keda.vconf.modle.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;

import com.gkzxhn.gkprison.base.MyApplication;

/**
 * @author chenjian
 * @date 2013-8-14
 */

public class VideoCapServiceConnect implements ServiceConnection {

	private final String TAG = getClass().getSimpleName();
	// Service is started
	private boolean mIsStarted;

	private VideoCapService mVideoCapService;

	/**
	 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName,
	 *      android.os.IBinder)
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, "onServiceConnected: ......连接图像采集服务...");
        mVideoCapService = ((VideoCapService.VideoCapServiceBinder) service).getService();

		mIsStarted = true;
	}

	/**
	 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
		mVideoCapService = null;

        Log.i(TAG, "onServiceDisconnected: ....图像采集服务断开...");
        mIsStarted = false;
	}

	/** 
	 * @return the mIsStarted 
	 */
	public boolean isStarted() {
		return mIsStarted;
	}

	/**
	 * 初始化采集图像
     * return true 表示采集成功
	 */
	public boolean initVideoCapture() {
		if (null == mVideoCapService) {
            Log.i(TAG, "initVideoCapture: .....return ... ");
            return false;
		}
        Log.i(TAG, "initVideoCapture: .....");

		try {
			mVideoCapService.initVideoCapture(MyApplication.getApplication());
		} catch (Exception e) {
			Log.i(TAG, "initVideoCapture", e);
		}
		return true;
	}

	/**
	 * 重新开始采集图像
	 *
	 * @param surfaceHolder
	 * @param portrait
	 */
	public void reStartVideoCapture(SurfaceHolder surfaceHolder, boolean portrait) {
		if (null == mVideoCapService) {
			return;
		}

		try {
			mVideoCapService.reStartVideoCapture(surfaceHolder, portrait);
		} catch (Exception e) {
			Log.i(getClass().getSimpleName(), "reStartVideoCapture", e);
//			System.out.println("  VideoCapture.getParameters() === " + VideoCapture.getParameters());
		}
	}

	/**
	 * 重新开始采集图像
	 *
	 * @param surfaceHolder
	 * @param portrait
	 */
	private void reStartVideoCapture(boolean portrait) {
		// if (null == mVideoCapService) {
		// return;
		// }
		//
		// try {
		// mVideoCapService.reStartVideoCapture(SlidingMenuManager.getCaptureSurfaceHolder(), portrait);
		// } catch (Exception e) {
		// Log.i(getClass().getSimpleName(), "reStartVideoCapture", e);
		// }
	}

	/**
	 * 开始采集图像
	 *
	 * @param resolution
	 * @param portrait
	 */
	public void startVideoCapture(SurfaceHolder surfaceHolder, short resolution, boolean portrait) {
		if (null == mVideoCapService) {
			return;
		}

		try {
			mVideoCapService.startVideoCapture(surfaceHolder, resolution, portrait);
		} catch (Exception e) {
			Log.i(getClass().getSimpleName(), "startVideoCapture", e);
		}
	}

	/**
	 * 开始采集图像
	 *
	 * @param resolution
	 * @param portrait
	 */
	private void startVideoCapture(short resolution, boolean portrait) {
		// if (null == mVideoCapService) {
		// return;
		// }
		//
		// try {
		// mVideoCapService.startVideoCapture(SlidingMenuManager.getCaptureSurfaceHolder(), resolution, portrait);
		// } catch (Exception e) {
		// Log.i(getClass().getSimpleName(), "startVideoCapture", e);
		// }
	}

	/**
	 * 返回当前采集数据的SurfaceHolder
	 *
	 * @return
	 */
	public SurfaceHolder getSurfaceHolder() {
		if (null == mVideoCapService) {
			return null;
		}

		try {
			return mVideoCapService.getSurfaceHolder();
		} catch (Exception e) {
			Log.i(getClass().getSimpleName(), "startVideoCapture", e);
		}

		return null;
	}

	/**
	 * 停止采集图像
	 */
	public void stopVideoCapture() {
		if (null == mVideoCapService) {
			return;
		}

		try {
			mVideoCapService.stopVideoCapture();
		} catch (Exception e) {
			Log.i(getClass().getSimpleName(), "stopVideoCapture", e);
		}
	}

	/**
	 * destroy VideoCapture
	 */
	public void destroyVideoCapture() {
		if (null == mVideoCapService) {
			return;
		}

		try {
			mVideoCapService.destroyVideoCapture();
		} catch (Exception e) {
			Log.i(getClass().getSimpleName(), "destroyVideoCapture", e);
		}
	}

}
