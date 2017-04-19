/**
 * @(#)VConfAudioUI.java   2014-8-28
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.vconf.controller;

import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.gkzxhn.gkprison.utils.CustomUtils.KDConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.keda.sky.app.PcAppStackManager;
import com.keda.vconf.audio.controller.VConfAudioContentFrame;
import com.keda.vconf.audio.controller.VConfAudioDualFrame;
import com.keda.vconf.audio.controller.VConfAudioFrame;
import com.keda.vconf.audio.controller.VConfJoinAudioFrame;
import com.keda.vconf.bean.VConf;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Conference;
import com.kedacom.kdv.mt.bean.TMtAddr;
import com.kedacom.kdv.mt.constant.EmMtCallDisReason;
import com.kedacom.kdv.mt.constant.EmNativeConfType;
import com.pc.utils.StringUtils;

import java.util.List;


/**
  * 音频会议
  * 
  * @author chenj
  * @date 2014-8-28
  */

public class VConfAudioUI extends ActionBarActivity {

	private final int id = 0x1e010112;
	protected VConf mVConf;
	protected String mE164;
	protected String mConfTitle;
	protected boolean mIsP2PConf;
	protected boolean mIsJoinConf;

	// 当前的音视频面
	protected Fragment mCurrFragmentView;

	// 当前的ContentFrame
	protected Fragment mVConfContentFrame;
	private List<TMtAddr> mTMtList;// 邀请的视频会议终端
	private int mVConfQuality;// 会议质量 2M.1M.256,192
	private int mDuration;// 会议时长

	private boolean mIsShowSendingDesktop;// 接收音频双流时，前面的等待图标

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PcAppStackManager.Instance().pushActivity(this);
		// 让音量键固定为媒体音量控制,其他的页面不要这样设置--只在音视频的界面加入这段代码
		this.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

		Log.i("VConfAudioUI", "VConfAudioUI-->onCreate");

		FrameLayout c = new FrameLayout(this);
		c.setId(id);
		setContentView(c, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

		initExtras();
		onViewCreated();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	/**
	 * @see com.kedacom.truetouch.sky.app.TTActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * @see com.kedacom.truetouch.sky.app.TTActivity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	/**
	 * @see com.kedacom.truetouch.vconf.controller.AbsVConfActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	public void initExtras() {

		Bundle extra = getIntent().getExtras();
		if (null == extra) {
			return;
		}
		if (null != extra) {
			mConfTitle = extra.getString("VconfName");
			mE164 = extra.getString(KDConstants.E164NUM);
			mIsP2PConf = extra.getBoolean("MackCall", false);
			mIsJoinConf = extra.getBoolean("JoinConf", false);
		}

		if (null != VConferenceManager.mConfInfo) {
			mConfTitle = VConferenceManager.mConfInfo.achConfName;
		}
		if (null != VConferenceManager.currTMtCallLinkSate) {
			mIsP2PConf = VConferenceManager.currTMtCallLinkSate.isP2PVConf();
		}
		if (null != VConferenceManager.mCallPeerE164Num) {
			mE164 = VConferenceManager.mCallPeerE164Num;
		}
		mVConfQuality = extra.getInt("VconfQuality"); //会议质量
		mDuration = extra.getInt("VconfDuration");//会议时长
		try {
			mTMtList = new Gson().fromJson(extra.getString("tMtList"), new TypeToken<List<TMtAddr>>() {
			}.getType());
		} catch (Exception e) {
		}

	}
	/**
	 * @see com.kedacom.truetouch.vconf.controller.AbsVConfActivity#onViewCreated()
	 */
	protected void onViewCreated() {
		mVConf = new VConf();
		mVConf.setAchConfE164(mE164);
		mVConf.setAchConfName(mConfTitle);
		if (!StringUtils.isNull(VConferenceManager.mCallPeerE164Num)) {
			mVConf.setAchConfE164(VConferenceManager.mCallPeerE164Num);
		}
		switchVConfFragment();
	}

	/**
	 * 切换音频界面
	 */
	public void switchVConfFragment() {
		// 音频双流
		if (VConferenceManager.nativeConfType == EmNativeConfType.AUDIO_AND_DOAL) {
			if (null == mVConfContentFrame) {
				mVConfContentFrame = new VConfAudioContentFrame();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(id, mVConfContentFrame);
				ft.commitAllowingStateLoss();
			}

			if (!(mCurrFragmentView instanceof VConfAudioDualFrame)) {
				mCurrFragmentView = new VConfAudioDualFrame();
				((VConfAudioContentFrame) mVConfContentFrame).replaceContentFrame(mCurrFragmentView);
			}
		}
		// 音频会议
		else if (VConferenceManager.nativeConfType == EmNativeConfType.AUDIO
				|| (null != VConferenceManager.currTMtCallLinkSate && !VConferenceManager.currTMtCallLinkSate.isCaller())) {
			if (null == mVConfContentFrame) {
				mVConfContentFrame = new VConfAudioContentFrame();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(id, mVConfContentFrame);
				ft.commitAllowingStateLoss();
			}

			if (!(mCurrFragmentView instanceof VConfAudioFrame)) {
				mCurrFragmentView = new VConfAudioFrame();
				((VConfAudioContentFrame) mVConfContentFrame).replaceContentFrame(mCurrFragmentView);
			}
		}

		// 音频入会
		else {
			if (mCurrFragmentView instanceof VConfJoinAudioFrame) {
				return;
			}

			mVConfContentFrame = null;
			mCurrFragmentView = new VConfJoinAudioFrame();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(id, mCurrFragmentView);
			ft.commitAllowingStateLoss();
		}
	}

	/**
	 * update vconf title
	 * 
	 * @param confInfo
	 */
	public void updateVConfTitle(String confName) {
		if (null == confName) {
			return;
		}

		if (null == mCurrFragmentView || !(mCurrFragmentView instanceof VConfJoinAudioFrame)) {
			return;
		}

		((VConfJoinAudioFrame) mCurrFragmentView).updateVConfTitle(confName);
	}

	/**
	 * 当前是否是音频双流界面
	 *
	 * @return
	 */
	public boolean isCurrVConfAudioDualFrame() {
		if (null == mCurrFragmentView) {
			return false;
		}

		if (mCurrFragmentView instanceof VConfAudioDualFrame) {
			return true;
		}

		return false;
	}

	/**
	 * @see com.kedacom.truetouch.vconf.controller.AbsVConfActivity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.w("VConfAudioUI", "VConfAudioUI-->onPause");
		super.onPause();
	}

	/**
	 * @see com.kedacom.truetouch.vconf.controller.AbsVConfActivity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.w("VConfAudioUI", "VConfAudioUI-->onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.w("VConfAudioUI", "VConfAudioUI-->onDestroy");
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// 如果正在请求进入音频会议,返回 取消加入会议
		if (mCurrFragmentView != null && mCurrFragmentView instanceof VConfJoinAudioFrame) {
			Conference.hangupConfByReason(EmMtCallDisReason.emDisconnect_Normal);
			super.onBackPressed();
		}
	}

	/**
	 * @return the mIsShowSendingDesktop
	 */
	public boolean ismIsShowSendingDesktop() {
		return mIsShowSendingDesktop;
	}

	/**
	 * @param mIsShowSendingDesktop the mIsShowSendingDesktop to set
	 */
	public void setmIsShowSendingDesktop(boolean mIsShowSendingDesktop) {
		this.mIsShowSendingDesktop = mIsShowSendingDesktop;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/** @return the mVConf */
	public VConf getVConf() {
		return mVConf;
	}

	/** @param mVConf the mVConf to set */
	public void setVConf(VConf mVConf) {
		this.mVConf = mVConf;
	}

	/** @return the mE164 */
	public String getE164() {
		return mE164;
	}

	/** @param mE164 the mE164 to set */
	public void setE164(String mE164) {
		this.mE164 = mE164;
	}

	/** @return the mConfTitle */
	public String getConfTitle() {
		return mConfTitle;
	}

	/** @param mConfTitle the mConfTitle to set */
	public void setConfTitle(String mConfTitle) {
		this.mConfTitle = mConfTitle;
	}

	/** @return the mIsP2PConf */
	public boolean isP2PConf() {
		return mIsP2PConf;
	}

	/** @param mIsP2PConf the mIsP2PConf to set */
	public void setIsP2PConf(boolean mIsP2PConf) {
		this.mIsP2PConf = mIsP2PConf;
	}

	/** @return the mIsJoinConf */
	public boolean isIsJoinConf() {
		return mIsJoinConf;
	}

	/** @param mIsJoinConf the mIsJoinConf to set */
	public void setIsJoinConf(boolean mIsJoinConf) {
		this.mIsJoinConf = mIsJoinConf;
	}


	/** @return the mVConfContentFrame */
	public Fragment getVConfContentFrame() {
		return mVConfContentFrame;
	}

	/** @param mVConfContentFrame the mVConfContentFrame to set */
	public void setVConfContentFrame(Fragment mVConfContentFrame) {
		this.mVConfContentFrame = mVConfContentFrame;
	}

	/** @return the mTMtList */
	public List<TMtAddr> getTMtList() {
		return mTMtList;
	}

	/** @param mTMtList the mTMtList to set */
	public void setTMtList(List<TMtAddr> mTMtList) {
		this.mTMtList = mTMtList;
	}

	/** @return the mVConfQuality */
	public int getVConfQuality() {
		return mVConfQuality;
	}

	/** @param mVConfQuality the mVConfQuality to set */
	public void setVConfQuality(int mVConfQuality) {
		this.mVConfQuality = mVConfQuality;
	}

	/** @return the mDuration */
	public int getDuration() {
		return mDuration;
	}

	/** @param mDuration the mDuration to set */
	public void setDuration(int mDuration) {
		this.mDuration = mDuration;
	}

}
