package com.keda.vconf.controller;

/**
 * @(#)VConfFunctionFragment.java   2014-9-5
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.MyApplication;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.PcAppStackManager;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Conference;
import com.kedacom.kdv.mt.constant.EmMtCallDisReason;
import com.pc.utils.NetWorkUtils;

/**
  * 会议底部工具栏
  * 
  * @author chenj
  * @date 2014-9-5
  */

public class VConfFunctionFragment extends Fragment implements View.OnClickListener {

	private final int WHAT_REQCHAIRMAN = 0x963;
	private final int WHAT_REQSPEAKER = 0x964;
	private final long DELAY_MILLIS = 10 * 1000;

	// 哑音
	private TextView mDumbView;

	// 静音
	private TextView mQuietView;

	// 更多弹出框的未读消息数目
	private TextView mUnreadMessageNumText;

	// 之前的未读消息数
	private int preUnreadNum;

	// 会议统计信息弹出框
	private PopupWindow mCodecStatusWin;

	// 更多会议信息弹出框
	private PopupWindow mVConfInfoMoreWin;

	// 退出/结束会议弹出框
	private PopupWindow mExitVConfWin;

	// 会议邀请弹出框
	private PopupWindow mInviteVConfPersonWin;

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (null == msg) {
				return;
			}

			switch (msg.what) {
				case WHAT_REQCHAIRMAN:
				// 申请管理方失败
					if (!VConferenceManager.isChairMan()) {
					Toast.makeText(MyApplication.getContext(), "申请主席失败", Toast.LENGTH_SHORT).show();
					}
					break;
				case WHAT_REQSPEAKER:
				// 申请主讲失败
					if (!VConferenceManager.isSpeaker()) {
					Toast.makeText(MyApplication.getContext(), "申请主讲失败", Toast.LENGTH_SHORT).show();
					}
					break;

				default:
					break;
			}
		};
	};

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.vconf_function_layout, null);
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		findViews();
		initComponentValue();
		registerListeners();
		setQuietImageView(false);
		setMuteImageView(false);
		Conference.setLocalTerQuite(false);
		Conference.setLocalTerMute(false);
		super.onViewCreated(view, savedInstanceState);
	}

	/**
	 */
	@Override
	public void onStart() {
		Log.i("VConfVideo", "VConfFunctionFragment-->onStart");
		super.onStart();

		updateOperationView();

		/*	setQuietImageView(false);
			setMuteImageView(false);*/
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.i("VConfVideo", "VConfFunctionFragment-->onResume");
		super.onResume();
	}

	public void findViews() {
		mDumbView = (TextView) getView().findViewById(R.id.mute_text);
		mQuietView = (TextView) getView().findViewById(R.id.quiet_text);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (mExitVConfWin != null && mExitVConfWin.isShowing()) {
			mExitVConfWin.dismiss();
		}
		mExitVConfWin = null;

		if (mVConfInfoMoreWin != null && mVConfInfoMoreWin.isShowing()) {
			mVConfInfoMoreWin.dismiss();
		}
		mVConfInfoMoreWin = null;

	}

	public void initComponentValue() {

	}

	/**
	 * 更新显示工具栏
	 */
	public void updateOperationView() {
		if (!isAdded()) {
			return;
		}

		if (null == VConferenceManager.currTMtCallLinkSate || null == getView() || null == getActivity()) {
			return;
		}

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// View messageFrame = getView().findViewById(R.id.message_frame);
				TextView inviteVConfText = (TextView) getView().findViewById(R.id.inviteVConf_text);
				if (VConferenceManager.isP2PVConf() || !VConferenceManager.isChairMan()) {
					if (inviteVConfText.getVisibility() != View.GONE) {
						inviteVConfText.setVisibility(View.GONE);
					}
					// if (messageFrame.getVisibility() != View.VISIBLE) {
					// messageFrame.setVisibility(View.VISIBLE);
					// }
					return;
				}
				if (inviteVConfText.getVisibility() != View.VISIBLE) {
					inviteVConfText.setVisibility(View.VISIBLE);
				}
				// if (messageFrame.getVisibility() != View.GONE) {
				// messageFrame.setVisibility(View.GONE);
				// }
			}
		});
	}

	/**
	* 删除申请管理权限的提示
	*/
	public void removeReqChairmanHandler() {
		if (null == mHandler) {
			return;
		}
		mHandler.removeMessages(WHAT_REQCHAIRMAN);
	}

	/**
	 * 删除申请主讲失败的提示
	 */
	public void removeReqSpeakerHandler() {
		if (null == mHandler) {
			return;
		}
		mHandler.removeMessages(WHAT_REQSPEAKER);
	}

	public void registerListeners() {
		getView().findViewById(R.id.exit_Img).setOnClickListener(this);
		getView().findViewById(R.id.mute_text).setOnClickListener(this);
		getView().findViewById(R.id.quiet_text).setOnClickListener(this);
		getView().findViewById(R.id.moreInfo_btn).setOnClickListener(this);
		getView().findViewById(R.id.inviteVConf_text).setOnClickListener(this);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */

	@Override
	public void onClick(View v) {
		if (v == null) {
			return;
		}

		String e164 = "";
		if (getActivity() instanceof VConfVideoUI) {
			e164 = ((VConfVideoUI) getActivity()).getmE164();
		}

		switch (v.getId()) {
		// 延长会议30分钟
			case R.id.vconfDelay:
			Conference.extendConf(30);
				break;
		// 哑音
			case R.id.mute_text:
			Conference.requestOnLineTerList();
				toggleMute();
				break;

		// 静音
			case R.id.quiet_text:
				toggleQuiet();
				break;
		// 添加
			case R.id.inviteVConf_text:
				VConferenceManager.inviteVConfPersonWin();
				break;
		// 更多信息
			case R.id.moreInfo_btn:
				toggleVConfInfoMoreWindow();
				break;
		// 统计信息
			case R.id.vconfStatisticalInformation_Text:
			Conference.getCallStatisticsInfoReq();
				break;

		// 申请、释放管理员权限
			case R.id.vconf_chairtext:
				mHandler.removeMessages(WHAT_REQCHAIRMAN);

			// 释放主席
				if (VConferenceManager.isChairMan()) {
				Conference.releaseChairman();
				}
			// 申请主席
				else {
				Conference.applyChairman();
					mHandler.sendEmptyMessageDelayed(WHAT_REQCHAIRMAN, DELAY_MILLIS);
				}

				disssVConfInfoMoreWin();
				break;

		// 申请主讲
			case R.id.vconf_speakertext:
				mHandler.removeMessages(WHAT_REQSPEAKER);

			if (!VConferenceManager.isSpeaker()) {// 申请主讲
				Conference.applySpeaker();
					mHandler.sendEmptyMessageDelayed(WHAT_REQSPEAKER, DELAY_MILLIS);
				}
				disssVConfInfoMoreWin();
				break;
		// 挂断
			case R.id.exit_Img:
				if (checkExceptionQuit()) {
					VConferenceManager.quitConfAction(VConferenceManager.isCSVConf(), false);
					break;
				}

			// 结束点对点会议
				if (VConferenceManager.isCSP2P()) {
				Conference.hangupConfByReason(EmMtCallDisReason.emDisconnect_Normal);
					break;
				}

			// 主席权限，有主席权限时，可选择 "退出会议"或"结束会议"
				if (VConferenceManager.isChairMan()) {
					toggleExitVConfWindow();
				}
			// 没有主席权限，直接主动退出会议
				else {
				Conference.hangupConfByReason(EmMtCallDisReason.emDisconnect_Normal);
				}
				break;

		// 退出会议
			case R.id.ExitConf_Text:
				if (checkExceptionQuit()) {
					VConferenceManager.quitConfAction(VConferenceManager.isCSVConf(), false);
					break;
				}
			Conference.hangupConfByReason(EmMtCallDisReason.emDisconnect_Normal);
				// pupHangupOrEndConfDialog(true);
				disssExitVConfWin();
				break;

		// 结束会议, 需主席权限
			case R.id.EndConf_Text:
				if (checkExceptionQuit()) {
					VConferenceManager.quitConfAction(VConferenceManager.isCSVConf(), false);
					break;
				}
			Conference.endConf();
				// pupHangupOrEndConfDialog(false);
				disssExitVConfWin();
				break;

			default:
				break;
		}
	}

	/**
	 * 检测是否是异常退会
	 * @return
	 */
	private boolean checkExceptionQuit() {
		if (!NetWorkUtils.isAvailable(getActivity()) || VConferenceManager.currTMtCallLinkSate == null || !GKStateMannager.mRegisterGK || !VConferenceManager.isCSVConf()) {
			return true;
		}

		return false;
	}

	/**
	 * 哑音开关
	 */
	private void toggleMute() {
		if (null == mDumbView || null == mDumbView.getTag()) {
			return;
		}

		boolean mute = (Boolean) mDumbView.getTag();
		Conference.setLocalTerMute(!mute);
		setMuteImageView(!mute);
	}

	/**
	 * 静音
	 */
	public void toggleQuiet() {
		if (null == mQuietView || null == mQuietView.getTag()) {
			return;
		}

		boolean quiet = (Boolean) mQuietView.getTag();
		Conference.setLocalTerQuite(!quiet);
		setQuietImageView(!quiet);
	}

	/**
	 * 当前是否为静音状态
	 * @return
	 */
	public boolean isQuiet() {
		if (null == mQuietView || null == mQuietView.getTag()) {
			return false;
		}

		return (Boolean) mQuietView.getTag();
	}

	/**
	 * 当前是否为哑音状态
	 *
	 * @return
	 */
	public boolean isMute() {
		if (null == mDumbView || null == mDumbView.getTag()) {
			return false;
		}

		return (Boolean) mDumbView.getTag();
	}

	/**
	 * 设置静音图标
	 *
	 * @param quiet
	 */
	public void setQuietImageView(final boolean quiet) {
		if (!isAdded() || null == getActivity()) {
			return;
		}

		if (null == mQuietView || null == getView()) {
			return;
		}

		getView().post(new Runnable() {

			@Override
			public void run() {
				if (quiet) {
					mQuietView.setCompoundDrawablesWithIntrinsicBounds(null, MyApplication.getApplication().getResources().getDrawable(R.drawable.vconf_mute_selector), null,
							null);
				} else {
					mQuietView.setCompoundDrawablesWithIntrinsicBounds(null, MyApplication.getApplication().getResources().getDrawable(R.drawable.vconf_speaker_selector),
							null, null);
				}
				mQuietView.setTag(quiet);
			}
		});
	}

	/**
	 * 设置哑音图标
	 *
	 * @param mute
	 */
	public void setMuteImageView(final boolean mute) {
		if (!isAdded() || null == getActivity()) {
			return;
		}

		if (null == mDumbView || null == getView()) {
			return;
		}

		getView().post(new Runnable() {

			@Override
			public void run() {
				if (mute) {
					mDumbView.setCompoundDrawablesWithIntrinsicBounds(null, MyApplication.getApplication().getResources()
							.getDrawable(R.drawable.vconf_microphone_off_selector), null, null);
				} else {
					mDumbView.setCompoundDrawablesWithIntrinsicBounds(null,
							MyApplication.getApplication().getResources().getDrawable(R.drawable.vconf_microphone_on_selector), null, null);
				}
				mDumbView.setTag(mute);
			}
		});
	}

	/**
	 * 更多会议信息弹出框
	 */
	private void toggleVConfInfoMoreWindow() {
		View moreInfoFrame = getView().findViewById(R.id.moreInfo_btn);
		if (moreInfoFrame == null) {
			return;
		}
		if (mVConfInfoMoreWin == null) {
			mVConfInfoMoreWin = createVConfInfoMorePopWindow();
		}
		if (mVConfInfoMoreWin.isShowing()) {
			mVConfInfoMoreWin.dismiss();
			return;
		}

		View view = mVConfInfoMoreWin.getContentView();
		int vconf_speakertext_Height = 0;
		if (null != view) {
			// 判断自己是否有主席权限
			if (VConferenceManager.isChairMan()) {
				((TextView) view.findViewById(R.id.vconf_chairtext)).setText(R.string.vconf_releaseAdministrativePrivileges);
				(view.findViewById(R.id.vconfDelay)).setVisibility(View.VISIBLE);
			} else {
				((TextView) view.findViewById(R.id.vconf_chairtext)).setText(R.string.vconf_Apply4AdministrativePrivileges);
				(view.findViewById(R.id.vconfDelay)).setVisibility(View.GONE);
			}
			// 判断自己是否是主讲
			if (VConferenceManager.isSpeaker()) {
				if (((TextView) view.findViewById(R.id.vconf_speakertext)).getVisibility() != View.GONE) {
					((TextView) view.findViewById(R.id.vconf_speakertext)).setVisibility(View.GONE);
				}
				vconf_speakertext_Height = 0;
			} else {
				if (((TextView) view.findViewById(R.id.vconf_speakertext)).getVisibility() != View.VISIBLE) {
					((TextView) view.findViewById(R.id.vconf_speakertext)).setVisibility(View.VISIBLE);
				}
				((TextView) view.findViewById(R.id.vconf_speakertext)).setText(R.string.vconf_Apply4Speaker);
				vconf_speakertext_Height = 100;
			}

			if (VConferenceManager.isP2PVConf()) {
				(view.findViewById(R.id.vconfDelay)).setVisibility(View.GONE);
				((TextView) view.findViewById(R.id.vconf_speakertext)).setVisibility(View.GONE);
				((TextView) view.findViewById(R.id.vconf_chairtext)).setVisibility(View.GONE);
			}
		}

		// 更多信息 中间里屏幕右边框的间距
		int x = getView().getWidth() / 8;
		int y = moreInfoFrame.getHeight() + 2 * 10;
		mVConfInfoMoreWin.showAtLocation(getView(), Gravity.BOTTOM, x, y);

		/*		int[] location = new int[2];
				moreInfoFrame.getLocationOnScreen(location);
				int y1 = getView().getHeight();
				int y2 = moreInfoFrame.getHeight() + 5 * 5 + vconf_speakertext_Height;
				int x = (location[0] + moreInfoFrame.getWidth() / 2) - mVConfInfoMoreWin.getWidth() / 2;
				int y = location[1] - y1 - y2;*/

		// 防止从最下面弹出 PopWindow
		/*if (y >= location[1]) {
			return;
		}*/
		// mVConfInfoMoreWin.showAtLocation(moreInfoFrame, Gravity.NO_GRAVITY, x, y);
	}

	/**
	 * 退出/结束会议
	 */
	private void toggleExitVConfWindow() {
		ImageView exitImg = (ImageView) getView().findViewById(R.id.exit_Img);
		if (exitImg == null) {
			return;
		}

		if (mExitVConfWin == null) {
			mExitVConfWin = createExitPopWindow();
		}

		if (mExitVConfWin.isShowing()) {
			mExitVConfWin.dismiss();
			return;
		}

		int x = exitImg.getWidth() / 5;
		int y = getView().getHeight();
		mExitVConfWin.showAtLocation(exitImg, Gravity.BOTTOM | Gravity.RIGHT, x, y);

		/*int[] location = new int[2];
		exitImg.getLocationOnScreen(location);
		int y1 = getView().getHeight();
		int x = (location[0] + exitImg.getWidth() / 2) - mExitVConfWin.getWidth() / 2;
		int y = location[1] - exitImg.getHeight() - y1;
		mExitVConfWin.showAtLocation(exitImg, Gravity.NO_GRAVITY, x, y);*/
	}

	/**
	 * 创建会议信息更多弹出框
	 *
	 * @return
	 */
	@SuppressLint("InflateParams")
	private PopupWindow createVConfInfoMorePopWindow() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.vconf_info_more_layout, null);
		if (view == null) {
			return null;
		}

		final int wLP = 400;
		final int hLP = ViewGroup.LayoutParams.WRAP_CONTENT;
		final PopupWindow popWin = new PopupWindow(view, wLP, hLP, true);
		popWin.setBackgroundDrawable(new BitmapDrawable());
		popWin.setOutsideTouchable(true);
		popWin.setAnimationStyle(android.R.style.Animation_Dialog);

		view.findViewById(R.id.vconfInfo_Text).setOnClickListener(this);
		view.findViewById(R.id.vconf_chairtext).setOnClickListener(this);
		view.findViewById(R.id.vconf_speakertext).setOnClickListener(this);
		view.findViewById(R.id.vconfStatisticalInformation_Text).setOnClickListener(this);
		view.findViewById(R.id.vconfDelay).setOnClickListener(this);

		return popWin;
	}

	/**
	 * 创建退出/结束会议弹出框
	 *
	 * @return
	 */
	@SuppressLint("InflateParams")
	private PopupWindow createExitPopWindow() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.vconf_exit_or_finish_layout, null);
		if (view == null) {
			return null;
		}

		final int wLP = 250;
		final int hLP = ViewGroup.LayoutParams.WRAP_CONTENT;
		final PopupWindow popWin = new PopupWindow(view, wLP, hLP, true);
		popWin.setBackgroundDrawable(new BitmapDrawable());
		popWin.setOutsideTouchable(true);
		popWin.setAnimationStyle(android.R.style.Animation_Dialog);
		view.findViewById(R.id.ExitConf_Text).setOnClickListener(this);
		view.findViewById(R.id.EndConf_Text).setOnClickListener(this);
		return popWin;
	}

	/**
	 * 是否有弹出框显示
	 * @return
	 */
	public boolean hasPopWindowShowing() {
		if (mExitVConfWin != null && mExitVConfWin.isShowing()) {
			return true;
		}

		if (mVConfInfoMoreWin != null && mVConfInfoMoreWin.isShowing()) {
			return true;
		}

		// if (mCodecStatusWin != null && mCodecStatusWin.isShowing()) {
		// return true;
		// }

		return false;
	}

	/**
	 * 隐藏更多会议信息弹出框
	 */
	private void disssVConfInfoMoreWin() {
		if (mVConfInfoMoreWin == null) {
			return;
		}

		try {
			if (mVConfInfoMoreWin.isShowing()) {
				mVConfInfoMoreWin.dismiss();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 隐藏退出/结束会议弹出框
	 */
	private void disssExitVConfWin() {
		if (mExitVConfWin == null) {
			return;
		}

		try {
			if (mExitVConfWin.isShowing()) {
				mExitVConfWin.dismiss();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 关闭所有弹出框
	 */
	public void dissPopWin() {
		if (null == getView()) {
			return;
		}

		getView().post(new Runnable() {

			@Override
			public void run() {
				disssExitVConfWin();
				// disssCodecStatusWin();
				disssVConfInfoMoreWin();
			}
		});
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onPause()
	 */
	@Override
	public void onPause() {
		Log.e("VConfVideo", "VConfFunctionFragment-->onPause");
		Log.e("VConfAudioUI", "VConfFunctionFragment-->onPause");
		super.onPause();
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.e("VConfVideo", "VConfFunctionFragment-->onStop");
		Log.e("VConfAudioUI", "VConfFunctionFragment-->onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.e("VConfVideo", "VConfFunctionFragment-->onDestroyView");
		Log.e("VConfAudioUI", "VConfFunctionFragment-->onDestroyView");
		super.onDestroyView();
		dissPopWin();

		mExitVConfWin = null;
		mVConfInfoMoreWin = null;
		mCodecStatusWin = null;
	}

	/**
	 * @see com.pc.app.v4fragment.PcAbsFragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.e("VConfVideo", "VConfFunctionFragment-->onDestroy");
		Log.e("VConfAudioUI", "VConfFunctionFragment-->onDestroy");
		super.onDestroy();
	}

	public void pupHangupOrEndConfDialog(final boolean isHangup) {
		final Activity ttBaseActivity = (Activity) PcAppStackManager.Instance().currentActivity();
		if (null == ttBaseActivity) {
			return;
		}
		String title = ttBaseActivity.getString(R.string.vconf_end_msg);
		String okTxt = ttBaseActivity.getString(R.string.vconf_end);
		String cancelTxt = ttBaseActivity.getString(R.string.cancel);
		if (isHangup) {
			title = ttBaseActivity.getString(R.string.vconf_quit_msg);
			okTxt = ttBaseActivity.getString(R.string.vconf_quit);
		}

		// 确定
		android.view.View.OnClickListener okListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dissPopWin();
				if (isHangup) {
					Conference.hangupConfByReason(EmMtCallDisReason.emDisconnect_Normal);
				} else {
					Conference.endConf();
				}
			}
		};

		// 取消

		android.view.View.OnClickListener cancelListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dissPopWin();
			}
		};
	}
}
