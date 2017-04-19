/**
 * @(#)VConfJoinAudioFrame.java   2014-9-4
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.vconf.audio.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.keda.vconf.controller.VConfAudioUI;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Conference;
import com.kedacom.kdv.mt.constant.EmMtCallDisReason;
import com.kedacom.kdv.mt.constant.EmNativeConfType;
import com.pc.utils.StringUtils;

/**
 * 音频入会界面
 * @author chenj
 * @date 2014-9-4
 */

public class VConfJoinAudioFrame extends Fragment implements View.OnClickListener {

	// 音频会议管理界面
	private VConfAudioUI mVConfAudioUI;

	// 音频闪烁灯
	private AnimationDrawable mFlasherAnimationDrawable;

	// // 头像
	// private ImageView mHeadPortraitImg;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mVConfAudioUI = (VConfAudioUI) getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.vconf_join_audio_layout, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initComponentValue();
		registerListeners();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		startFlasherAnimationDrawable();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 1s之后发送请求，防止入会失败界面被立即关闭
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				/*if (mIsHangup) { // 挂断，退出
					VConferenceManager.cleanNativeConfType();
					return;
				}*/
				if (VConferenceManager.isCallIncoming()) {
					mVConfAudioUI.finish();
					return;
				}

				// 召集会议
				if (VConferenceManager.nativeConfType == EmNativeConfType.CONVENEING_AUDIO) {
					if (null != mVConfAudioUI.getTMtList()) {
						VConferenceManager.confCreateConfCmd(mVConfAudioUI.getConfTitle(),
								mVConfAudioUI.getTMtList(), mVConfAudioUI.getVConfQuality(), mVConfAudioUI.getDuration(), true);
					}
					return;
				}

				if (mVConfAudioUI.isP2PConf()) {
					VConferenceManager.makeCallAudio(mVConfAudioUI.getE164());
				} else {
					VConferenceManager.joinConfByAudio(mVConfAudioUI.getVConf());
				}
			}
		}).start();
	}


	public void initComponentValue() {
		final ImageView flasherImg = (ImageView) getView().findViewById(R.id.flasher_img);
		mFlasherAnimationDrawable = (AnimationDrawable) flasherImg.getBackground();

		if (!mVConfAudioUI.isP2PConf() && mVConfAudioUI.getVConf() != null) {
			mVConfAudioUI.setConfTitle(mVConfAudioUI.getVConf().achConfName);
		}

		TextView vconfTitleText = (TextView) getView().findViewById(R.id.vconf_title);
		if (StringUtils.isNull(mVConfAudioUI.getConfTitle())) {
			vconfTitleText.setVisibility(View.GONE);
		} else {
			vconfTitleText.setText(mVConfAudioUI.getConfTitle());
		}

	}

	/**
	 * update VConf Title
	 * @param confInfo
	 */
	public void updateVConfTitle(final String confName) {
		if (null == getView() || StringUtils.isNull(confName)) {
			return;
		}

		final TextView vconfTitleText = (TextView) getView().findViewById(R.id.vconf_title);
		if (null == vconfTitleText || StringUtils.isNull(confName)) {
			return;
		}

		getView().post(new Runnable() {

			@Override
			public void run() {
				vconfTitleText.setText(confName);
				mVConfAudioUI.setConfTitle(confName);
				if (vconfTitleText.getVisibility() != View.VISIBLE) {
					vconfTitleText.setVisibility(View.VISIBLE);
				}
			}
		});
	}


	/**
	 * 开始播放闪烁灯
	 */
	private void startFlasherAnimationDrawable() {
		if (null == mFlasherAnimationDrawable) {
			return;
		}

		mFlasherAnimationDrawable.start();
	}

	/**
	 * 停止播放闪烁灯
	 */
	private void stopFlasherAnimationDrawable() {
		if (null == mFlasherAnimationDrawable) {
			return;
		}

		mFlasherAnimationDrawable.stop();
	}

	public void registerListeners() {
		getView().findViewById(R.id.hang_img).setOnClickListener(this);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
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
		Conference.hangupConfByReason(EmMtCallDisReason.emDisconnect_Normal);

		mVConfAudioUI.finish();
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onPause()
	 */
	@Override
	public void onPause() {
		stopFlasherAnimationDrawable();

		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
