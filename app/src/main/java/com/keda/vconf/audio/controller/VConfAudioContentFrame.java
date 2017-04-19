/**
 * @(#)VConfAudioContentFrame.java   2014-10-15
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.vconf.audio.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import com.gkzxhn.gkprison.R;
import com.keda.vconf.controller.VConfAudioUI;
import com.keda.vconf.controller.VConfFunctionFragment;
import com.keda.vconf.manager.VConferenceManager;

/**
  * 音频管理界面
  * 
  * @author chenj
  * @date 2014-10-15
  */

public class VConfAudioContentFrame extends Fragment {

	// 音频会议管理界面
	private VConfAudioUI mVConfAudioUI;

	private VConfFunctionFragment mBottomFunctionFragment;

	// Cached values.
	private int mControlsBottomHeight;
	private int mControlsTopHeight;
	private int mShortAnimTime;

	// 计时器
	private Chronometer mChronometer;
	
	private static final String VCONF_AUDIO_FRAME_TAG = "VConfAudioFrame_Tag";
	private static final String VCONF_SHARE_FRAME_TAG = "VConfShareFrame_Tag";
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mVConfAudioUI = (VConfAudioUI) getActivity();
		// mVConfAudioUI.setAutoHide(true);

	}


	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.vconf_audio_content, null);

		mBottomFunctionFragment = new VConfFunctionFragment();
		getFragmentManager().beginTransaction().replace(R.id.bottomFunction_Frame, mBottomFunctionFragment).commitAllowingStateLoss();

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}


	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onResume()
	 */

	@Override
	public void onResume() {
		VConferenceManager.mIsQuitAction = false;
		/*	if (VConferenceManager.mIsPopPwd) {
				// 判断是否需要弹出密码框
				// VConferenceManager.pupPwdDialog(mVConfVideoUI);
				//ActivityUtils.openActivity(mVConfAudioUI, VConfPasswordUI.class);
			}*/
		super.onResume();
	}


	/**
	 * 底部工具栏
	 *
	 * @return
	 */
	public VConfFunctionFragment getBottomFunctionFragment() {
		// if (null == mBottomFunctionFragment) {
		// mBottomFunctionFragment = (VConfFunctionFragment)
		// getFragmentManager().findFragmentById(R.id.bottomFunction_Frame);
		// }
		return mBottomFunctionFragment;
	}

	/**
	 * 底部工具栏（View）
	 * 
	 * @return
	 */
	public View getBottomFunctionFragmentView() {
		return getView().findViewById(R.id.bottomFunction_Frame);
	}

	/**
	 * 音频内容 
	 *
	 * @param tfragment
	 */
	public void replaceContentFrame(Fragment tfragment) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame, tfragment, VCONF_AUDIO_FRAME_TAG);
		ft.commitAllowingStateLoss();
	}
	

}
