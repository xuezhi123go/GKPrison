/**
 * @(#)VideoCapService.java 2013-8-14 Copyright 2013 it.kedacom.com, Inc. All
 *                          rights reserved.
 */

package com.keda.vconf.modle.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.kedacom.truetouch.video.capture.VideoCapture;

/**
 * Video Cap Service 
 * 
 * @author chenjian
 * @date 2013-8-14
 */

public class VideoCapService extends Service {

	private final String Tag = "vconf service";
	private short resolution = 9;

	public class VideoCapServiceBinder extends Binder {

		public VideoCapService getService() {
			return VideoCapService.this;
		}

	}

	private SurfaceHolder mSurfaceHolder;

	// 是否已经初始化采集图像
	private boolean mIsInitVideoCapture;

	// 正在采集数据
	private boolean mVideoCapturing;

	private boolean mIsDestoryCapture;

	/**
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		startForeground(0, new Notification());
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		try {
			// initVideoCapture(this);
			//
			// // set automatic rotation correct mode for video capture
			// if (!VideoCapture.isAutoRotationCorrect()) {
			// VideoCapture.setAutoRotationCorrect(true);
			// }
		} catch (Exception e) {
			Log.e(Tag, "Video cap service onstart...", e);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new VideoCapService.VideoCapServiceBinder();
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(Tag, "VideoCapService onUnbind...");

		return super.onUnbind(intent);
	}

	@Override
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i(Tag, "VideoCapService onDestroy...");

		if (VERSION.SDK_INT >= VERSION_CODES.ECLAIR) {
			stopForeground(true);
		}
		try {
			destroyVideoCapture();
		} catch (Exception e) {
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		Log.e(Tag, "VideoCapService onLowMemory...");

		try {
			// destroyVideoCapture();
		} catch (Exception e) {
		}

		super.onLowMemory();
	}

	/**
	 * 初始化采集图像
	 */
	public void initVideoCapture(Context context) throws Exception {
		// SlidingMenuManager.updateCaptureFrame(true);

		if (!mIsInitVideoCapture) {
			// initialize video capture // 相机故障true表示正常，false表示相机故障
			VideoCapture.initialize(context);
			mIsInitVideoCapture = true;
			Log.i(Tag, "VideoCapService VideoCapture.initialize(context)");
		}

		mIsDestoryCapture = false;

		// set automatic rotation correct mode for video capture
		if (!VideoCapture.isAutoRotationCorrect()) {
			VideoCapture.setAutoRotationCorrect(true);
		}
	}

	/**
	 * 重新开始采集图像
	 *
	 * @param surfaceHolder
	 * @param portrait
	 * @throws Exception
	 */
	public void reStartVideoCapture(SurfaceHolder surfaceHolder, boolean portrait) throws Exception {
		stopVideoCapture();
		startVideoCapture(surfaceHolder, portrait);
		Thread.sleep(1500);
	}

	/**
	 * 开始采集图像
	 * 
	 * @param prevSurfaceView
	 * @param portrait 竖屏
	 * @throws Exception
	 */
	public void startVideoCapture(SurfaceHolder surfaceHolder, boolean portrait) throws Exception {
		startVideoCapture(surfaceHolder, this.resolution, portrait);
	}

	/**
	 * 开始采集图像
	 * 
	 * @param prevSurfaceView
	 * @param resolution
	 * @param portrait 竖屏
	 * @throws Exception
	 */
	public void startVideoCapture(SurfaceHolder surfaceHolder, short resolution, boolean portrait) throws Exception {
		if (null == surfaceHolder || mVideoCapturing) {
			return;
		}

		mSurfaceHolder = surfaceHolder;
		if (resolution > 0) {
			this.resolution = resolution;
		}

		int maxFPS = 20; // frames per seconds
		int width = 704;
		int height = 576;
		switch (resolution) {
			case 3:
				width = 352;
				height = 288;
				break;

			case 25:
				width = 512;
				height = 288;
				break;

			case 5:
			default:
				width = 704;
				height = 576;
				break;
		}

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();

		// * 0 - angles to rotate is 0.
		// * 1 - angles to rotate is 90.
		// * 2 - angles to rotate is 180.
		// * 3 - angles to rotate is 270.

		int w = width;
		int h = height;
		// 竖屏
		if (portrait) {
			w = Math.min(width, height);
			h = Math.max(width, height);
		}

		// 横屏
		else {
			w = Math.max(width, height);
			h = Math.min(width, height);
		}

		Log.i(Tag, "VideoCapService startVideoCapture");
		VideoCapture.start(surfaceHolder, w, h, maxFPS, d.getRotation());

		Log.e("VConfVideo", "VideoCapService VideoCapture.start(surfaceHolder, w, h, maxFPS, d.getRotation()):" + "w=" + w + "; h=" + h + ";maxFPS=" + maxFPS + ";portrait ="
				+ (portrait ? "竖屏" : "横屏") + ";d.getRotation()=" + d.getRotation());
		mVideoCapturing = true;
	}

	/**
	 * 停止采集图像
	 */
	public void stopVideoCapture() throws Exception {
		// 当前没有采集数据
		if (!mVideoCapturing) {
			return;
		}

		Log.i(Tag, "VideoCapService stopVideoCapture");

		VideoCapture.stop();

		mVideoCapturing = false;
	}

	/**
	 * 返回当前采集数据的SurfaceHolder
	 *
	 * @return
	 */
	public SurfaceHolder getSurfaceHolder() {
		if (mVideoCapturing) {
			return mSurfaceHolder;
		}

		return null;
	}

	/**
	* 销毁采集模块
	*/
	public void destroyVideoCapture() throws Exception {
		stopVideoCapture();

		Log.i(Tag, "VideoCapService destroyVideoCapture");

		if (!mIsDestoryCapture) {
			VideoCapture.destroy();
			mIsDestoryCapture = true;
		}

		mIsInitVideoCapture = false;
		mVideoCapturing = false;

		mSurfaceHolder = null;
	}

}
