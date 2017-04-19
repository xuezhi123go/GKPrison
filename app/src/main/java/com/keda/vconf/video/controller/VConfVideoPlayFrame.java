package com.keda.vconf.video.controller;

/**
 * @(#)VConfAudioFrame.java   2014-9-4
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.google.gson.Gson;
import com.keda.vconf.controller.VConfVideoUI;
import com.keda.vconf.manager.VConferenceManager;
import com.keda.vconf.reqs.Exam;
import com.keda.vconf.reqs.ExamReq;
import com.kedacom.truetouch.video.player.EGLConfigChooser;
import com.kedacom.truetouch.video.player.EGLContextFactory;
import com.kedacom.truetouch.video.player.EGLWindowSurfaceFactory;
import com.kedacom.truetouch.video.player.Renderer;
import com.kedacom.truetouch.video.player.VidGestureDetector;
import com.pc.ui.layout.ISimpleTouchListener;
import com.pc.ui.layout.SimpleGestureDetectorRelative;
import com.pc.utils.TerminalUtils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
  * 视频播放界面
  * 
  * @author chenj
  * @date 2014-9-4
  */

public class VConfVideoPlayFrame extends Fragment implements View.OnClickListener{

	// 视频会议管理界面
	private VConfVideoUI mVConfVideoUI;

	private GLSurfaceView mGlPlayView;

	// PlayView GLSurfaceView
	private Renderer mMainRenderer;

	// 正在接收双流
	private boolean isReceivingDual;

	private VConfVideoFrame mVConfContentFrame;

	private ImageView iv_example;
	private ImageView iv_meeting_ic_card;
	private ImageView iv_meeting_icon;
	private Button bt_through_examine;
	private Button bt_not_through_examine;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.i("VConfVideo", "VConfVideoPlayFrame-->onAttach ");

		mVConfVideoUI = (VConfVideoUI) getActivity();
		if (null != mVConfVideoUI) {
			mVConfContentFrame = mVConfVideoUI.getVConfContentFrame();
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("VConfVideo", "VConfVideoPlayFrame-->onCreateView ");
		View view = inflater.inflate(R.layout.vconf_video_play, null);
		iv_example = (ImageView) view.findViewById(R.id.iv_example);
		iv_meeting_ic_card = (ImageView) view.findViewById(R.id.iv_meeting_ic_card);
		iv_meeting_icon = (ImageView) view.findViewById(R.id.iv_meeting_icon);
		bt_through_examine = (Button) view.findViewById(R.id.bt_through_examine);
		bt_not_through_examine = (Button) view.findViewById(R.id.bt_not_through_examine);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.i("VConfVideo", "VConfVideoPlayFrame-->onViewCreated ");
		findViews();
		initComponentValue();
		super.onViewCreated(view, savedInstanceState);
		initPlayGLSurfaceView();
		if (VConferenceManager.isCSVConf()) {
            if (mVConfVideoUI.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                mVConfVideoUI.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }
	}

	@Override
	public void onStart() {
		Log.i("VConfVideo", "VConfVideoPlayFrame-->onStart ");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.i("VConfVideo", "VConfVideoPlayFrame-->onResume ");
		super.onResume();
		if (null != mVConfContentFrame) {
			mVConfContentFrame.initChannel();
			// 摄像头初始状态关闭
			mVConfContentFrame.setCameraState(true);
		}

		if (null != mGlPlayView) {
			mGlPlayView.onResume();
		}

		computePlayViewLayoutParams(true, true);
		registerPlayGLSurfaceListener();

		mVConfContentFrame.setVisibilityPip(true);

	}

	public void findViews() {

	}

	public void initComponentValue() {

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 视频播放界面 
	 *
	 * @return
	 */
	public GLSurfaceView getPlayGLSurfaceView() {
		return mGlPlayView;
	}

	/** 
	 * 播放窗口的静态图片
	 * 
	 * @return the mStaticPlaypicImg
	 */
	public ImageView getStaticPlaypicImg() {
		if (null == getView()) {
			return null;
		}
		return (ImageView) getView().findViewById(R.id.staticpic_Img);
	}

	/** 
	 * 是否正在接收双流
	 * 
	 * @return the isReceivingDual
	 */
	public boolean isReceivingDual() {
		return isReceivingDual;
	}

	/**
	 * 设置是否正在接收双流
	 * 
	 * @param isReceivingDual the isReceivingDual to set
	 */
	public void setReceivingDual(boolean isReceivingDual) {
		this.isReceivingDual = isReceivingDual;
	}

	/**
	 * 初始化GLSurfaceView
	 * 
	 * <pre>
	 * Big GLSurfaceView 播放大窗口
	 * </pre>
	 */
	private void initPlayGLSurfaceView() {
		// ToDo 显示正在审核图片
		String logined_account = (String) SPUtil.get(getActivity(), "username", "");
//		if(!TextUtils.isEmpty(logined_account)){ // 已登录(几乎是肯定的)
//			if (MainUtils.isNumeric(logined_account)){ // 全数字组成即手机号登陆的  家属用户
//				// 家属用户显示正在审核
//				iv_example.setVisibility(View.VISIBLE);
//			}else {
//				// 监狱用户  显示头像身份证照  审核按钮
//				iv_meeting_ic_card.setVisibility(View.VISIBLE);
//				iv_meeting_icon.setVisibility(View.VISIBLE);
//				bt_not_through_examine.setVisibility(View.VISIBLE);
//				bt_through_examine.setVisibility(View.VISIBLE);
//				bt_through_examine.setOnClickListener(VConfVideoPlayFrame.this);
//				bt_not_through_examine.setOnClickListener(VConfVideoPlayFrame.this);
//			}
//		}

		Log.i("VConfVideo", "VConfVideoPlayFrame-->initPlayGLSurfaceView ");
		// 播放大窗口的装载布局
		SimpleGestureDetectorRelative playPicFrame = (SimpleGestureDetectorRelative) getView().findViewById(R.id.pic_frame);
		if (playPicFrame == null) {
			return;
		}

		mGlPlayView = (GLSurfaceView) playPicFrame.findViewById(R.id.gl_SV);
		playPicFrame.findViewById(R.id.staticpic_Img).setVisibility(View.GONE);

		mGlPlayView.setZOrderOnTop(false);
		mGlPlayView.setKeepScreenOn(true);
		mGlPlayView.setEGLConfigChooser(new EGLConfigChooser(8, 8, 8, 8, 0, 0));
		mGlPlayView.setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory());
		mGlPlayView.setEGLContextFactory(new EGLContextFactory());
		mGlPlayView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mGlPlayView.setEGLContextClientVersion(2);

		mMainRenderer = new Renderer();
		mGlPlayView.setRenderer(mMainRenderer);

		// xiezhigang]]
		mGlPlayView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
		mGlPlayView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	/**
	 * set MainRendererChannel
	 * 
	 * @param channel
	 */
	protected void setMainRendererChannel(Renderer.Channel channel) {
		if (null == mMainRenderer || null == channel) {
			return;
		}
		// start drawings only after this activity lives on screen.
		mMainRenderer.setChannel(channel);
	}

	/**
	 * 计算PalyView LayoutParams
	 * 
	 * @param autoCheck 自动检测大小
	 * @param matchParent 全屏 自动检测时，参数无效
	 */
	protected void computePlayViewLayoutParams(boolean autoCheck, boolean matchParent) {

		if (true) {
			return;
		}
		if (null == mGlPlayView) {
			return;
		}

		int[] wh = TerminalUtils.terminalWH(getActivity());
		if (null == wh || wh.length != 2 || wh[0] == 0) {
			return;
		}

		RelativeLayout.LayoutParams flLP = (RelativeLayout.LayoutParams) mGlPlayView.getLayoutParams();
		if (null == flLP) {
			return;
		}

		final int scalH = 9;
		final int scalW = 11;

		// 非自动检测
		if (!autoCheck) {
			if (matchParent) {
				flLP.width = FrameLayout.LayoutParams.MATCH_PARENT;
				flLP.height = FrameLayout.LayoutParams.MATCH_PARENT;
				mGlPlayView.setLayoutParams(flLP);
				mGlPlayView.invalidate();
			} else {
				int newH = wh[0] * scalH / scalW;
				flLP.width = FrameLayout.LayoutParams.MATCH_PARENT;
				flLP.height = newH;
				mGlPlayView.setLayoutParams(flLP);
				mGlPlayView.invalidate();
			}

			return;
		}

		// 点对点视频会议对端型号是否为Phone
		if (mVConfVideoUI.ismIsP2PConf() && VConferenceManager.currTMtCallLinkSate != null && VConferenceManager.currTMtCallLinkSate.isPeerMtModelPhone()) {
			flLP.width = FrameLayout.LayoutParams.MATCH_PARENT;
			flLP.height = FrameLayout.LayoutParams.MATCH_PARENT;
			mGlPlayView.setLayoutParams(flLP);
			mGlPlayView.invalidate();

			return;
		}

		// 主窗口当前显示信道类型是预览图/双流，则全屏显示
		if (mVConfVideoUI.getVConfContentFrame().getCurrMainChannel() == Renderer.Channel.preview
				|| mVConfVideoUI.getVConfContentFrame().getCurrMainChannel() == Renderer.Channel.second) {
			flLP.width = FrameLayout.LayoutParams.MATCH_PARENT;
			flLP.height = FrameLayout.LayoutParams.MATCH_PARENT;
			mGlPlayView.setLayoutParams(flLP);
			mGlPlayView.invalidate();

			return;
		}

		// 横屏
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			flLP.width = FrameLayout.LayoutParams.MATCH_PARENT;
			flLP.height = FrameLayout.LayoutParams.MATCH_PARENT;
			mGlPlayView.setLayoutParams(flLP);
			mGlPlayView.invalidate();
		}

		// 竖屏
		else {
			int newH = wh[0] * scalH / scalW;
			flLP.width = FrameLayout.LayoutParams.MATCH_PARENT;
			flLP.height = newH;
			mGlPlayView.setLayoutParams(flLP);
			mGlPlayView.invalidate();
		}
	}

	/**
	 * 注册主窗口的Listner
	 */
	private void registerPlayGLSurfaceListener() {
		if (null == mGestureGlPlayView) {
			// configure gesture detectors
			mGestureGlPlayView = new VidGestureDetector();
			// for preview is on surface, only need listen touches of surface.
			mGlPlayView.setOnTouchListener(mGestureGlPlayView);
			// register onTap to surface view.
			mGestureGlPlayView.setOnTapListener(mGlPlayView, OnTapGlPlayViewListener);
			// register onDrag to surface view.
			mGestureGlPlayView.setOnDragListener(mGlPlayView, OnDragGlPlayViewListener);
			// // register renderer as scaling listener.
			// mGestureGlPlayView.setOnScaleListener(mGlPlayView, mMainRenderer);
			mGestureGlPlayView.setOnScaleListener(mGlPlayView, OnScaleGlPlayViewListener);
		}

		if (null != mMainRenderer && null == mMainRenderer.getListener()) {
			// [[xiezhigang
			mMainRenderer.setListener(new Renderer.FrameListener() {

				public void onNewFrame() {
					mGlPlayView.requestRender();

					// 正在接收双流
					if (isReceivingDual && !mMainRenderer.isEmptyFrame()) {
						isReceivingDual = false;

						mGlPlayView.postDelayed(new Runnable() {

							@Override
							public void run() {
								mVConfVideoUI.getVConfContentFrame().autoSwitchStaticPicVisibility();
								computePlayViewLayoutParams(true, true);
							}
						}, 2000);
					}
				}
			});
			// xiezhigang]]
		}

		SimpleGestureDetectorRelative playPicFrame = (SimpleGestureDetectorRelative) getView().findViewById(R.id.pic_frame);
		if (null != playPicFrame && null == playPicFrame.getSimpleTouchListener()) {
			playPicFrame.setOnSimpleTouchListener(new ISimpleTouchListener() {

				private int limit = 120;
				private int updateCount = 1;
				private boolean showingStreamVolumeView;

				// 是否更新过音量
				private boolean isUpdatedVolume;

				@Override
				public void onDown(View v, MotionEvent e) {
					// initVolume();
				}

				@Override
				public void onSingleTapUp(View v, MotionEvent e) {
				}

				@Override
				public void onClick(View v) {
					// mVConfVideoUI.toggleFunctionview();
				}

				@Override
				public void onDoubleClick(View v) {
				}

				@Override
				public void onLongPress(View v) {
				}

				@Override
				public void onMove(View v, int distanceX, int distanceY) {
				}

				@Override
				public void onMoveScroll(View v, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				}

				@Override
				public void onUp(View v, MotionEvent e) {
				}

			});
		}
	}

	public void registerListeners() {

	}
	@Override
	public void onPause() {
		Log.w("VConfVideo", "VConfVideoPlayFrame-->onPause ");

		if (null != mGlPlayView) {
			mGlPlayView.onPause();
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.w("VConfVideo", "VConfVideoPlayFrame-->onStop ");
		if (null != mVConfContentFrame) {
			mVConfContentFrame.setCameraState(false);
		}
		super.onStop();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.w("VConfVideo", "VConfVideoPlayFrame-->onDetach ");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.w("VConfVideo", "VConfVideoPlayFrame-->onDestroyView ");
	}
	@Override
	public void onDestroy() {
		Log.w("VConfVideo", "VConfVideoPlayFrame-->onDestroy ");

		// destroy all renderers.
		destroyMainRenderer();
		super.onDestroy();
	}

	/**
	 * destroy MainRenderer
	 */
	private void destroyMainRenderer() {
		if (null == mMainRenderer) {
			return;
		}
		mMainRenderer.setListener(null);
		if (null != mVConfVideoUI.getVConfContentFrame().getCurrMainChannel()) {
			mMainRenderer.destroy();
		}
	}

	private VidGestureDetector mGestureGlPlayView;
	/**
	 *GlPlayView OnTapListener
	 */
	private final VidGestureDetector.OnTapListener OnTapGlPlayViewListener = new VidGestureDetector.OnTapListener() {

		@Override
		public void onTap(int tapCount, PointF point, long time) {
			if (tapCount == 1) {
				// single-tap to show/hide preview window.
				if (null != getView()) {
					getView().post(new Runnable() {

						public void run() {
							// mVConfVideoUI.toggleFunctionview();
						}
					});
				}
			} else if (tapCount > 1 && mGestureGlPlayView.isHit(mVConfVideoUI.getVConfContentFrame().getGlPreview(), point)) {
				// double-tap to switch camera.
				// VideoCapture.switchCamera();
			} else {
				if (null != mMainRenderer && mVConfVideoUI.getVConfContentFrame().getCurrMainChannel() != Renderer.Channel.preview) {
					mMainRenderer.onTap(tapCount, point, time);
				}
			}
		}
	};

	/**
	 * GlPlayView OnScaleListener
	 */
	private final VidGestureDetector.OnScaleListener OnScaleGlPlayViewListener = new VidGestureDetector.OnScaleListener() {

		private boolean isDrag = true;

		@Override
		public void onScaleBegin(PointF arg0, PointF arg1, long arg2) {
			if (null == mMainRenderer || mVConfVideoUI.getVConfContentFrame().getCurrMainChannel() == Renderer.Channel.preview) {
				isDrag = false;
			} else {
				isDrag = true;
			}

			if (isDrag) {
				mMainRenderer.onScaleBegin(arg0, arg1, arg2);
			}
		}

		@Override
		public void onScale(PointF arg0, PointF arg1, long arg2) {
			if (isDrag) {
				mMainRenderer.onScale(arg0, arg1, arg2);
			}
		}

		@Override
		public void onScaleEnd(PointF arg0, PointF arg1, long arg2) {
			if (isDrag) {
				mMainRenderer.onScaleEnd(arg0, arg1, arg2);
			}
		}
	};

	/**
	 * GlPlayView OnDragListener
	 */
	private final VidGestureDetector.OnDragListener OnDragGlPlayViewListener = new VidGestureDetector.OnDragListener() {

		private boolean m_bDragPreview = false; // whether dragging preview.
		private PointF m_pDragPreviewBegin = new PointF(); // start position of dragging.
		private int m_nDragPreviewBeginLeft = 0; // begin left of preview.
		private int m_nDragPreviewBeginTop = 0; // begin top of preview.
		private int m_nDragPreviewBeginWidth = 0; // begin width of preview.
		private int m_nDragPreviewBeginHeight = 0; // begin height of preview.

		private boolean isDrag = true;

		@Override
		public void onDragBegin(PointF point, long time) {
			if (null == mMainRenderer || mVConfVideoUI.getVConfContentFrame().getCurrMainChannel() == Renderer.Channel.preview) {
				isDrag = false;
			} else {
				isDrag = true;
			}

			if (mGestureGlPlayView.isHit(mVConfVideoUI.getVConfContentFrame().getGlPreview(), point)) {
				/*
				// if start begin on preview, then drag preview.
				m_bDragPreview = true;
				m_pDragPreviewBegin.set(point);
				m_nDragPreviewBeginLeft = mGlPreview.getLeft();
				m_nDragPreviewBeginTop = mGlPreview.getTop();
				m_nDragPreviewBeginWidth = mGlPreview.getWidth();
				m_nDragPreviewBeginHeight = mGlPreview.getHeight();
				*/
			} else {
				// else drag renderer's video while playing.
				m_bDragPreview = false;
				if (isDrag) {
					mMainRenderer.onDragBegin(point, time);
				}
			}
		}

		@Override
		public void onDrag(PointF point, long time) {
			if (m_bDragPreview) {
				/*
				// finish the preview dragging.
				int dx = (int) (point.x - m_pDragPreviewBegin.x);
				int dy = (int) (point.y - m_pDragPreviewBegin.y);
				int new_x = m_nDragPreviewBeginLeft + dx;
				int new_y = m_nDragPreviewBeginTop + dy;
				int new_right = new_x + m_nDragPreviewBeginWidth;
				int new_bottom = new_y + m_nDragPreviewBeginHeight;
				mGlPreview.layout(new_x, new_y, new_right, new_bottom);
				*/
			} else {
				// complete the renderer's video dragging.
				if (isDrag) {
					mMainRenderer.onDrag(point, time);
				}
			}
		}

		@Override
		public void onDragEnd(PointF point, long time) {
			if (m_bDragPreview) {
				/*
				// yes, drags the preview.
				int dx = (int) (point.x - m_pDragPreviewBegin.x);
				int dy = (int) (point.y - m_pDragPreviewBegin.y);
				int new_x = m_nDragPreviewBeginLeft + dx;
				int new_y = m_nDragPreviewBeginTop + dy;
				int new_right = new_x + m_nDragPreviewBeginWidth;
				int new_bottom = new_y + m_nDragPreviewBeginHeight;
				mGlPreview.layout(new_x, new_y, new_right, new_bottom);
				*/
			} else {
				// Oh, drags the renderer's video while playing.
				if (isDrag) {
					mMainRenderer.onDragEnd(point, time);
				}
			}
		}

	};

	/** @return the mMainRenderer */
	public Renderer getmMainRenderer() {
		return mMainRenderer;
	}

	public static final String TAG = "VConfVideoPlayFrame";

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.bt_not_through_examine:
				// 审核不通过
				RequestBody _body = getRequestBody(401);
				ExamReq.exam(_body, new Subscriber<ResponseBody>() {
					@Override
                    public void onCompleted() {}

					@Override
                    public void onError(Throwable e) {
						Log.e(TAG, "exam failed exception : " + e.getMessage());
						ToastUtil.showLongToast("发送异常");
					}

					@Override
                    public void onNext(ResponseBody responseBody) {
						try {
							String result = responseBody.string();
							Log.i(TAG, "exam failed success : " + result);
						} catch (IOException e) {
							e.printStackTrace();
						}
						// 把审核按钮隐藏
						bt_through_examine.setVisibility(View.GONE);
						bt_not_through_examine.setVisibility(View.GONE);
					}
				});
				break;
			case R.id.bt_through_examine:
				// 审核通过
				RequestBody body = getRequestBody(200);
				ExamReq.exam(body, new Subscriber<ResponseBody>() {
					@Override
                    public void onCompleted() {}

					@Override
                    public void onError(Throwable e) {
						Log.e(TAG, "exam exception : " + e.getMessage());
						ToastUtil.showLongToast("发送异常");
					}

					@Override
                    public void onNext(ResponseBody responseBody) {
						try {
							String result = responseBody.string();
							Log.i(TAG, "exam success : " + result);
						} catch (IOException e) {
							e.printStackTrace();
						}
						// 把审核按钮隐藏
						bt_through_examine.setVisibility(View.GONE);
						bt_not_through_examine.setVisibility(View.GONE);
					}
				});
				break;
		}
	}

	/**
	 * 获取审核参数
	 * @param code
	 * @return
     */
	@NonNull
	private RequestBody getRequestBody(int code) {
		Exam exam = new Exam();
		Exam.NotificationBean notificationBean = new Exam.NotificationBean();
		notificationBean.setCode(code);
		notificationBean.setReceiver((String) SPUtil.get(getActivity(), "family_accid", ""));
		notificationBean.setSender((String) SPUtil.get(getActivity(), "token", ""));
		exam.setNotification(notificationBean);
		String exam_str = new Gson().toJson(exam);
		return RequestBody.create(MediaType.
                parse("application/json; charset=utf-8"),  exam_str);
	}
}
