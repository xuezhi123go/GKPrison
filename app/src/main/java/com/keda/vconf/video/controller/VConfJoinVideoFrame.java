package com.keda.vconf.video.controller;

/**
 * @(#)VConfAudioFrame.java   2014-9-4
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gkzxhn.gkprison.R;
import com.keda.sky.app.GKStateMannager;
import com.keda.vconf.controller.MyFacingView;
import com.keda.vconf.controller.VConfVideoUI;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Conference;
import com.kedacom.kdv.mt.constant.EmMtCallDisReason;
import com.kedacom.kdv.mt.constant.EmNativeConfType;
import com.pc.utils.NetWorkUtils;
import com.pc.utils.StringUtils;
import com.pc.utils.TerminalUtils;

/**
  * 视频入会界面
  * 
  * @author chenj
  * @date 2014-9-4
  */

public class VConfJoinVideoFrame extends Fragment implements View.OnClickListener {

	// 摄像标准宽/高比
	private final int normW = 3, normH = 4;
	// 主动挂断
	private boolean mIsHangup;

	// 视频会议管理界面
	private VConfVideoUI mVConfVideoUI;
	private MyFacingView myFaceSV;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mVConfVideoUI = (VConfVideoUI) getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 
	 * @see com.pc.app.v4fragment.PcAbsFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.vconf_video_join_layout, null);

	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		myFaceSV = (MyFacingView) getView().findViewById(R.id.myFace_sv);
		// 暂时排除米2手机
		if (StringUtils.equals(android.os.Build.MODEL, "MI 2")) {

		} else {
			int h = TerminalUtils.terminalH(getActivity());
			int w = TerminalUtils.terminalW(getActivity());

			// 保证预览头像为4:3
			FrameLayout facingFrame = (FrameLayout) getView().findViewById(R.id.facingFrame);
			int nh = h;
			int nw = h * normW / normH;
			if (nw < w) {
				nw = w;
				nh = w * normH / normW;
			}

			facingFrame.getLayoutParams().height = nh;
			facingFrame.getLayoutParams().width = nw;
		}

		findViews();
		initComponentValue();
		registerListeners();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// 旋转屏幕时，校准摄像头方向
		myFaceSV.adjustCameraDisplayOrientation();
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		final long delayMillis = 3000;

		// 没有网络情况下，3s之后关闭
		if (!NetWorkUtils.isAvailable(getActivity())) {
			getView().postDelayed(new Runnable() {

				@Override
				public void run() {
					mVConfVideoUI.finish();
				}
			}, delayMillis);

			return;
		}

		// GK注册失败，3s之后关闭
		if (!GKStateMannager.mRegisterGK) {
			Toast.makeText(getActivity(), "加入视频会议室失败 ", Toast.LENGTH_LONG).show();

			getView().postDelayed(new Runnable() {

				@Override
				public void run() {
					mVConfVideoUI.finish();
				}
			}, delayMillis);
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 1s之后发送请求，防止入会失败界面被立即关闭
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				if (VConferenceManager.isCallIncoming()) {
					mVConfVideoUI.finish();
					return;
				}

				if (mIsHangup) {// 如果立马挂断，则不呼叫
					return;
				}
				// 召集会议
				if (VConferenceManager.nativeConfType == EmNativeConfType.CONVENEING_VIDEO) {
					if (null != mVConfVideoUI.gettMtList()) {
						VConferenceManager.confCreateConfCmd(mVConfVideoUI.getmConfTitle(), mVConfVideoUI.gettMtList(), mVConfVideoUI.getVConfQuality(),
								mVConfVideoUI.getDuration(), false);
					}

					return;
				}

				if (mVConfVideoUI.ismIsP2PConf()) {
					VConferenceManager.makeCallVideo(mVConfVideoUI.getmE164());
				} else {
					VConferenceManager.joinConfByVideo(mVConfVideoUI.getmVConf());
				}
			}
		}).start();
	}

	public void findViews() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void initComponentValue() {
		final TextView vconfTitle = (TextView) getView().findViewById(R.id.vconf_title);

		if (mVConfVideoUI.ismIsP2PConf()) {
			mVConfVideoUI.setmConfTitle(mVConfVideoUI.getmE164());
		}

		if (StringUtils.isNull(mVConfVideoUI.getmConfTitle())) {
			vconfTitle.setVisibility(View.GONE);
		} else {
			vconfTitle.setText(mVConfVideoUI.getmConfTitle());
		}

		final TextView vconfJoinInfo = (TextView) getView().findViewById(R.id.vconf_joinInfo);
		switch (VConferenceManager.nativeConfType) {
			case CALLING_AUDIO:
			case CALLING_VIDEO:
				vconfJoinInfo.setText(getString(R.string.vconf_call));
				break;

			case CONVENEING_AUDIO:
			case CONVENEING_VIDEO:
				vconfJoinInfo.setText(getString(R.string.vconf_convene));
				break;

			case JOINING_AUDIO:
			case JOINING_VIDEO:
			default:
				vconfJoinInfo.setText(getString(R.string.vconf_join));
				break;
		}
	}

	public void registerListeners() {
		getView().findViewById(R.id.hang_img).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (null == v || null == getActivity()) return;

		// 挂断
		if (v.getId() != R.id.hang_img) {
			return;
		}

		// 已经入会成功或者正在会议中时，点击挂断无效
		if (VConferenceManager.isCSVConf()) {
			return;
		}

		if (GKStateMannager.mRegisterGK) {
			Conference.hangupConfByReason(EmMtCallDisReason.emDisconnect_Normal);
			mIsHangup = true;
		}

		mVConfVideoUI.finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
