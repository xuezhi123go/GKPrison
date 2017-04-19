/**
 * @(#)VConfAudioFrame.java   2014-9-4
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.vconf.audio.controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gkzxhn.gkprison.R;
import com.keda.vconf.controller.VConfAudioUI;
import com.kedacom.truetouch.video.player.EGLConfigChooser;
import com.kedacom.truetouch.video.player.EGLContextFactory;
import com.kedacom.truetouch.video.player.EGLWindowSurfaceFactory;
import com.kedacom.truetouch.video.player.Renderer;
import com.kedacom.truetouch.video.player.VidGestureDetector;
import com.pc.ui.layout.SimpleGestureDetectorRelative;

/**
 * 音频双流界面
 * @author chenj
 * @date 2014-9-4
 */

public class VConfAudioDualFrame extends Fragment {

	// 音频会议管理界面
	private VConfAudioUI mVConfAudioUI;

	private GLSurfaceView mGlPlayView;
	private View mSendingDesktopLayout;
	private SimpleGestureDetectorRelative mPlayFrame;

	private VidGestureDetector mGestureGlPlayView;

	// 双流Renderer
	private Renderer mMainRenderer;

	// 正在接收双流
	private boolean mIsReceivingDual;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mVConfAudioUI = (VConfAudioUI) getActivity();
		if (mVConfAudioUI.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
			mVConfAudioUI.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 *      android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.vconf_audio_dual_layout, null);
	}

	/**
	 * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
	 */

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findViews();
		initComponentValue();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initPlayGLSurfaceView();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onResume() {
		super.onResume();

		mIsReceivingDual = true;
		registerPlayGLSurfaceListener();

		if (null != mGlPlayView) {
			mGlPlayView.onResume();
		}

	}

	public void findViews() {
		if (getView() == null) {
			return;
		}
		RelativeLayout mChronometerView = (RelativeLayout) getView().findViewById(R.id.chronometerViewDual);

		mGlPlayView = (GLSurfaceView) getView().findViewById(R.id.gl_SV);
		mSendingDesktopLayout = getView().findViewById(R.id.sending_desktop_layout);
		mPlayFrame = (SimpleGestureDetectorRelative) getView().findViewById(R.id.play_frame);
	}

	public void initComponentValue() {
		if (mVConfAudioUI.ismIsShowSendingDesktop()) {
			mGlPlayView.setVisibility(View.GONE);
			mSendingDesktopLayout.setVisibility(View.VISIBLE);
		} else {
			mGlPlayView.setVisibility(View.VISIBLE);
			mSendingDesktopLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化视频播放
	 */
	private void initPlayGLSurfaceView() {
		mGlPlayView.setEGLConfigChooser(new EGLConfigChooser(8, 8, 8, 8, 0, 0));
		mGlPlayView.setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory());
		mGlPlayView.setEGLContextFactory(new EGLContextFactory());
		mGlPlayView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

		mGlPlayView.setEGLContextClientVersion(2);

		mMainRenderer = new Renderer();
		mGlPlayView.setRenderer(mMainRenderer);

		mGlPlayView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
		mGlPlayView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		// start drawings only after this activity lives on screen.
		mMainRenderer.setChannel(Renderer.Channel.second);
	}


	/**
	 * 注册GLSurface事件
	 */
	private void registerPlayGLSurfaceListener() {
		if (null == mGestureGlPlayView) {
			// configure gesture detectors
			mGestureGlPlayView = new VidGestureDetector();
			// for preview is on surface, only need listen touches of surface.
			mGlPlayView.setOnTouchListener(mGestureGlPlayView);
			// mGestureGlPlayView.setOnDragListener(mGlPlayView, OnDragGlPlayView);
			mGestureGlPlayView.setOnDragListener(mGlPlayView, mMainRenderer);
			// // register renderer as scaling listener.
			mGestureGlPlayView.setOnScaleListener(mGlPlayView, mMainRenderer);
		}

		if (null == mMainRenderer.getListener()) {
			mMainRenderer.setListener(new Renderer.FrameListener() {

				public void onNewFrame() {
					mGlPlayView.requestRender();

					// 正在接收双流
					if (mIsReceivingDual && !mMainRenderer.isEmptyFrame()) {
						mIsReceivingDual = false;

						mGlPlayView.postDelayed(new Runnable() {

							@Override
							public void run() {
								mGlPlayView.setVisibility(View.VISIBLE);
								mSendingDesktopLayout.setVisibility(View.GONE);

								mVConfAudioUI.setmIsShowSendingDesktop(false);
							}
						}, 2000);
					}
				}
			});
		}
	}

	@Override
	public void onPause() {
		if (null != mGlPlayView) {
			mGlPlayView.onPause();
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		if (null != mGestureGlPlayView) {
			mGestureGlPlayView.destroy();
		}
		super.onDestroyView();
	}
}
