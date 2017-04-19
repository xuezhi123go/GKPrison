/**
 * @(#)VConfAudioFrame.java   2014-9-4
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.vconf.audio.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.keda.vconf.controller.VConfAudioUI;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.constant.EmNativeConfType;

/**
 * 音频界面
 * @author chenj
 * @date 2014-9-4
 */

public class VConfAudioFrame extends Fragment {

	// 音频会议管理界面
	private VConfAudioUI mVConfAudioUI;

	private TextView mVconfAudioInfoTxt;

	private ImageView mTelephoneOnImg;
	private ImageView mTelephoneOffImg;
	private TextView mTelephonereceiverInfo;

	// 音频麦克风
	private ImageView mMicrophoneImg;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_CALL);// zte手机听筒没有声音
		mVConfAudioUI = (VConfAudioUI) getActivity();
		if (null != mVConfAudioUI) { // 竖屏
			if (mVConfAudioUI.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {// 音频手机
				mVConfAudioUI.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 *      android.os.Bundle)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.vconf_audio_layout, null);
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findViews();
		initComponentValue();
	}

	@Override
	public void onStart() {
		Log.i("VConfAudioUI", "VConfAudioFrame-->onRestart");

		VConferenceManager.nativeConfType = EmNativeConfType.AUDIO;
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void findViews() {
		if (getView() == null) {
			return;
		}
		mTelephoneOnImg = (ImageView) getView().findViewById(R.id.telephoneOnImg);
		mTelephoneOffImg = (ImageView) getView().findViewById(R.id.telephoneOffImg);
		mTelephonereceiverInfo = (TextView) getView().findViewById(R.id.telephonereceiver_info);

		mVconfAudioInfoTxt = (TextView) getView().findViewById(R.id.vconfAudio_infoTxt);

		// 音频麦克风
		mMicrophoneImg = (ImageView) getView().findViewById(R.id.microphone_img);
	}

	public void initComponentValue() {
		final View peerHeadFrame = getView().findViewById(R.id.peerHeadFrame);
		if (mVConfAudioUI.isP2PConf()) {
			mMicrophoneImg.setVisibility(View.GONE);
			peerHeadFrame.setVisibility(View.VISIBLE);
		} else {
			peerHeadFrame.setVisibility(View.GONE);
			mMicrophoneImg.setVisibility(View.VISIBLE);
		}

		setTelephoneReceverImg(false, false);

		showMCCdetails();
	}

	/**
	 * 多点会议，显示会议室名称
	 */
	private void showMCCdetails() {
		if (mVConfAudioUI == null || mVConfAudioUI.isP2PConf()) {
			return;
		}

		mVconfAudioInfoTxt.setText(mVConfAudioUI.getConfTitle());
	}

	/**
	 * 设置听筒模式显示图片
	 * @param on
	 * @param showInfo
	 */
	private void setTelephoneReceverImg(boolean on, boolean showInfo) {
		if (on) {
			if (mTelephoneOffImg != null) {
				mTelephoneOffImg.setVisibility(View.GONE);
			}

			if (mTelephoneOnImg != null) {
				mTelephoneOnImg.setVisibility(View.VISIBLE);
			}

			if (mTelephonereceiverInfo != null) {
				mTelephonereceiverInfo.setText(R.string.telephonereceiver_close_info);
			}
		} else {
			if (mTelephoneOffImg != null) {
				mTelephoneOffImg.setVisibility(View.VISIBLE);
			}

			if (mTelephoneOnImg != null) {
				mTelephoneOnImg.setVisibility(View.GONE);
			}

			if (mTelephonereceiverInfo != null) {
				mTelephonereceiverInfo.setText(R.string.telephonereceiver_open_info);
			}
		}

		if (showInfo) {
			mTelephonereceiverInfo.setVisibility(View.VISIBLE);
		} else {
			mTelephonereceiverInfo.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
