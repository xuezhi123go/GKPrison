/**
 * @(#)MyFaceView.java 2013-8-1 Copyright 2013 it.kedacom.com, Inc. All rights
 *                     reserved.
 */

package com.keda.vconf.controller;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pc.utils.TerminalUtils;

import java.util.List;

/**
 * @author chenjian
 * @date 2013-8-1
 */

public class MyFacingView extends SurfaceView implements SurfaceHolder.Callback {

	private final String TAG = "MyFacingView";

	private int mCameraId;
	private Camera mCameraDevice;
	private SurfaceHolder mHolder;
	private boolean mIsCameraError;

	public MyFacingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MyFacingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 */
	public MyFacingView(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		init();
	}

	private void init() {
		// 创建一个新的SurfaceHolder， 并分配MyFacingView作为它的回调
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setKeepScreenOn(true);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	private boolean mIsRemoveCallback;

	public void removeCallback() {
		Log.i(TAG, "removeCallback ");

		if (null != mHolder && !mIsRemoveCallback) {
			mHolder.removeCallback(this);
			mIsRemoveCallback = true;
		}

		releaseCameraDevice();
	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated");

		// Unlock the camera object before passing it to media recorder.
		try {
			releaseCameraDevice();

			initCamera();

			// 通过SurfaceView显示取景画面
			if (mCameraDevice != null) {
				mCameraDevice.setPreviewDisplay(holder);
			}
			// mCameraDevice.unlock();
		} catch (Exception e) {
			releaseCameraDevice();

			Log.e(TAG, "init CamerDevice " + e.toString());
		}
	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder,
	 *      int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(TAG, "surfaceChanged " + width + "  " + height);

		if (mCameraDevice != null) {
			try {
				// 设置参数并开始预览
				mCameraDevice.stopPreview();

				// Camera.Size lSize = findBestResolution(width, height);
				// 优化预览画面的物体长宽失真,原因是Surfaceview和Previewsize的长宽比率不一致，就会导致预览画面上失真。
				Camera.Size lSize = getOptimalPreviewSize(width, height);
				invalidate();

				PixelFormat pixelFormat = new PixelFormat();
				PixelFormat.getPixelFormatInfo(mCameraDevice.getParameters().getPreviewFormat(), pixelFormat);
				Camera.Parameters parameters = mCameraDevice.getParameters();
				parameters.setPreviewSize(lSize.width, lSize.height);
				parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
				// parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				mCameraDevice.setParameters(parameters);
				mCameraDevice.startPreview();
			} catch (Exception e) {
			}

		} else {
			Log.e(TAG, "Camera is null!");
		}
	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");

		releaseCameraDevice();
	}

	public void releaseCameraDevice() {
		if (mCameraDevice == null) {
			return;
		}

		// close display
		mCameraDevice.setPreviewCallback(null);
		mCameraDevice.stopPreview();
		mCameraDevice.release();
		mCameraDevice = null;
	}

	private Size findBestResolution(int pWidth, int pHeight) {
		if (mCameraDevice == null) {
			return null;
		}
		List<Size> lSizes = mCameraDevice.getParameters().getSupportedPreviewSizes();
		Size lSelectedSize = mCameraDevice.new Size(0, 0);
		for (Size lSize : lSizes) {
			if ((lSize.width <= pWidth) && (lSize.height <= pHeight) && (lSize.width >= lSelectedSize.width) && (lSize.height >= lSelectedSize.height)) {
				lSelectedSize = lSize;
			}
		}
		if ((lSelectedSize.width == 0) || (lSelectedSize.height == 0)) {
			lSelectedSize = lSizes.get(0);
		}

		return lSelectedSize;
	}

	private Size getOptimalPreviewSize(int w, int h) {
		List<Size> sizes = mCameraDevice.getParameters().getSupportedPreviewSizes();
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null) return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - h) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - h);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - h) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - h);
				}
			}
		}
		return optimalSize;
	}

	/**
	 * initialize Camera Device
	 */
	public void initCamera() throws Exception {
		int cameraId = 0;
		int numberOfCameras = Camera.getNumberOfCameras();
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (; cameraId < numberOfCameras; cameraId++) {
			Camera.getCameraInfo(cameraId, cameraInfo);
			// 前置摄像头
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				break;
			}
		}
		try {
			if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && cameraId != numberOfCameras) {
				mCameraId = cameraId;
				mCameraDevice = Camera.open(cameraId);
			} else {
				mCameraDevice = Camera.open();
				mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
			}

		} catch (Exception e) {
		}

		adjustCameraDisplayOrientation();
	}

	/**
	 * 校准摄像头方向
	 */
	public void adjustCameraDisplayOrientation() {
		if (null == mCameraDevice) {
			return;
		}

		Camera.CameraInfo info = new Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(mCameraId, info);

		int degrees = TerminalUtils.getRotationAngle(getContext());
		int angle;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			angle = (info.orientation + degrees) % 360;
			angle = (360 - angle) % 360; // compensate the mirror
		}
		// back-facing
		else {
			angle = (info.orientation - degrees + 360) % 360;
		}

		mCameraDevice.setDisplayOrientation(angle);
	}

	/** @return the mIsCameraError */
	public boolean ismIsCameraError() {
		return mIsCameraError;
	}

}
