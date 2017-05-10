package com.keda.vconf.manager;

/**
 * @(#)VConferenceManager.java   2014-8-15
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.utils.CustomUtils.KDConstants;
import com.google.gson.Gson;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.LoginStateManager;
import com.keda.sky.app.PcAppStackManager;
import com.keda.sky.app.TruetouchGlobal;
import com.keda.vconf.bean.VConf;
import com.keda.vconf.controller.VConfAVResponseUI;
import com.keda.vconf.controller.VConfAudioUI;
import com.keda.vconf.controller.VConfDetailsUI;
import com.keda.vconf.controller.VConfVideoUI;
import com.keda.vconf.dialog.InviteVConfDialog;
import com.kedacom.kdv.mt.api.Conference;
import com.kedacom.kdv.mt.bean.EmPeerProductId;
import com.kedacom.kdv.mt.bean.LabelAssign;
import com.kedacom.kdv.mt.bean.TMTAudioFormatList;
import com.kedacom.kdv.mt.bean.TMTCreateConfMember;
import com.kedacom.kdv.mt.bean.TMTCreateConfPoll;
import com.kedacom.kdv.mt.bean.TMTCreateConfSatellite;
import com.kedacom.kdv.mt.bean.TMTCreateConfVmp;
import com.kedacom.kdv.mt.bean.TMTEntityInfo;
import com.kedacom.kdv.mt.bean.TMTVideoFormatList;
import com.kedacom.kdv.mt.bean.TMtAddr;
import com.kedacom.kdv.mt.bean.TMtCallLinkSate;
import com.kedacom.kdv.mt.bean.TMtConfInfo;
import com.kedacom.kdv.mt.bean.TMtCreateConfParamApi;
import com.kedacom.kdv.mt.bean.TMtId;
import com.kedacom.kdv.mt.bean.TMtJoinConfParam;
import com.kedacom.kdv.mt.constant.EmAacChnlNum;
import com.kedacom.kdv.mt.constant.EmAudFormat;
import com.kedacom.kdv.mt.constant.EmClosedMeeting;
import com.kedacom.kdv.mt.constant.EmConfProtocol;
import com.kedacom.kdv.mt.constant.EmEncryptArithmetic;
import com.kedacom.kdv.mt.constant.EmH264Profile;
import com.kedacom.kdv.mt.constant.EmMeetingSafeType;
import com.kedacom.kdv.mt.constant.EmMtAddrType;
import com.kedacom.kdv.mt.constant.EmMtCallDisReason;
import com.kedacom.kdv.mt.constant.EmMtDualMode;
import com.kedacom.kdv.mt.constant.EmMtMixType;
import com.kedacom.kdv.mt.constant.EmMtModel;
import com.kedacom.kdv.mt.constant.EmMtOpenMode;
import com.kedacom.kdv.mt.constant.EmMtResolution;
import com.kedacom.kdv.mt.constant.EmMtVmpMode;
import com.kedacom.kdv.mt.constant.EmMtVmpStyle;
import com.kedacom.kdv.mt.constant.EmNativeConfType;
import com.kedacom.kdv.mt.constant.EmRestCascadeMode;
import com.kedacom.kdv.mt.constant.EmVidFormat;
import com.kedacom.kdv.mt.constant.EmVideoQuality;
import com.pc.utils.ActivityUtils;
import com.pc.utils.NetWorkUtils;
import com.pc.utils.StringUtils;
import com.pc.utils.TerminalUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
  * 
  * @author chenj
  * @date 2014-8-15
  */

public class VConferenceManager {

	// 音视频码率分割值，大于分割值为视频
	public static final int CALLRATE_SPLITLINE = 64;

	// 主动执行退出会议的动作
	public static boolean mIsQuitAction;

	// 本地音视频会议类型
	public static EmNativeConfType nativeConfType = EmNativeConfType.END;

	// 会议当前状态
	public static TMtCallLinkSate currTMtCallLinkSate;

	// 会议详情
	public static TMtConfInfo mConfInfo;

	// 多点会议时，本终端编号
	public static LabelAssign mLabelAssign;

	// 音视频对端E164号
	public static String mCallPeerE164Num;

	// public static boolean isCreateMulitChat4CONF;

	public static boolean isDualStream;
	// 双流终端
	public static TMtId dualStreamMan;
	// 会议包含终端信息
	private static List<TMTEntityInfo> mTMtInfoList;
	// 是否是加密会议
	public static boolean isCallEncryptNtf;

	public static Boolean mIsCameraFront;

	public static boolean isAES;//是否AES加密
	public static int answerMode = 1;// 是否自动接听 0是，1否
	public static int callRate = 512;//会议码率

	/**
	 * 会议结束时，清除会议相关数据
	 */
	public static void cleanConf() {
		currTMtCallLinkSate = null;
		mCallPeerE164Num = null;
		mLabelAssign = null;
		mConfInfo = null;
		if (null != mTMtInfoList) {
			mTMtInfoList.clear();
		}
		mTMtInfoList = null;
		nativeConfType = EmNativeConfType.END;
		mIsQuitAction = false;
		isDualStream = false;
		// isCreateMulitChat4CONF = false;
		mIsCameraFront = false;
		isAES = false;
	}

	/**
	 * 判断是否存在两画面合成
	 * 
	 * @return 
	 */
	public static boolean isVmpStyle1X2() {
		if (!EmNativeConfType.isVideo(nativeConfType)) {
			return false;
		}

		// 多点 两画面合成
		if (isCSMCC()) {
			if (null != mConfInfo && null != mConfInfo.tVmpParam && null != mConfInfo.tVmpParam) {
				if (mConfInfo.tVmpParam.emStyle == EmMtVmpStyle.emMt_VMP_STYLE_2_1X2) {
					return true;
				}

				if (mConfInfo.tVmpParam.emVmpMode == EmMtVmpMode.emMt_VMP_MODE_AUTO && mConfInfo.tVmpParam.emStyle == EmMtVmpStyle.emMt_VMP_STYLE_DYNAMIC && null != mTMtInfoList
						&& 2 == mTMtInfoList.size()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 被呼叫状态
	 * 
	 * @return
	 */
	public static boolean isCallIncoming() {
		if (null == currTMtCallLinkSate) {
			return false;
		}
		return currTMtCallLinkSate.isCallIncoming();
	}

	/**
	 * 是否为正在发起呼叫状态
	 * 
	 * @return
	 */
	public static boolean isCalling() {
		if (null == currTMtCallLinkSate) {
			return false;
		}
		return currTMtCallLinkSate.isCalling();
	}

	/**
	 * 当前呼叫状态是否为空闲状态
	 * @return
	 */
	public static boolean isCSIDLE() {
		if (null == currTMtCallLinkSate) {
			return false;
		}
		return currTMtCallLinkSate.isCSIDLE();
	}

	/**
	 * 是否正在会议中
	 * 
	 * @return
	 */
	public static boolean isCSVConf() {
		if (null == currTMtCallLinkSate) {
			return false;
		}

		// 正处于会议中
		if (currTMtCallLinkSate.isP2P() || currTMtCallLinkSate.isMCC()) {
			return true;
		}

		return false;
	}

	/**
	 * 当前会议是不是点对点会议
	 *
	 * @return
	 */
	public static boolean isP2PVConf() {
		if (null == currTMtCallLinkSate) {
			return false;
		}

		return currTMtCallLinkSate.isP2PVConf();
	}

	/**
	 * 点对点会议
	 *
	 * @return
	 */
	public static boolean isCSP2P() {
		if (null == currTMtCallLinkSate) {
			return false;
		}

		return currTMtCallLinkSate.isP2P();
	}

	/**
	 * 多点会议 
	 *
	 * @return
	 */
	public static boolean isCSMCC() {
		if (null == currTMtCallLinkSate) {
			return false;
		}

		return currTMtCallLinkSate.isMCC();
	}

	/**
	 * 对端型号   
	 *
	 * @return
	 */
	public static String getEmPeerMtModel4Media() {
		if (null == currTMtCallLinkSate || StringUtils.isNull(currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emModelBegin.toString();
		}

		if (null != currTMtCallLinkSate.emPeerModel && EmMtModel.emModelBegin != currTMtCallLinkSate.emPeerModel) {
			return currTMtCallLinkSate.emPeerModel.toString();
		}

		// 5.0
		if (EmPeerProductId.SkywalkerForIpad.toString().equals(VConferenceManager.currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emSkyIPad.toString();
		}
		if (EmPeerProductId.SkywalkerForIPhone.toString().equals(VConferenceManager.currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emTrueTouchIphone.toString();
		}
		if (EmPeerProductId.SkywalkerForIPhone4s.toString().equals(VConferenceManager.currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emTrueTouchIphone.toString();
		}
		if (EmPeerProductId.SkywalkerForIPhone5.toString().equals(VConferenceManager.currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emTrueTouchIphone.toString();
		}
		if (EmPeerProductId.SkywalkerForIPhone6.toString().equals(VConferenceManager.currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emTrueTouchIphone.toString();
		}
		if (EmPeerProductId.SkywalkerForAndroidPad.toString().equals(VConferenceManager.currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emTrueTouchAndroidPad.toString();
		}
		if (EmPeerProductId.SkywalkerForAndroidPhone.toString().equals(VConferenceManager.currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emTrueTouchAndroidPhone.toString();
		}

		// 2.6
		if (EmPeerProductId.TTiPhone.toString().equals(VConferenceManager.currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emTrueTouchIphone.toString();
		}
		if (EmPeerProductId.TTAndroidPhone.toString().equals(VConferenceManager.currTMtCallLinkSate.achPeerProductId)) {
			return EmMtModel.emTrueTouchAndroidPhone.toString();
		}

		return EmMtModel.emModelBegin.toString();
	}

	/**
	 * 是否正在当前处于当前会议室的会议中
	 * 
	 * @param e164
	 * @return
	 */
	public static boolean isCSVConf(String e164) {
		if (StringUtils.isNull(e164)) {
			return false;
		}

		if (!isCSVConf()) {
			return false;
		}

		return StringUtils.equals(mCallPeerE164Num, e164);
	}

	/**
	 * 当前在视频会议中
	 * 
	 * @return
	 */
	public static boolean isCurrVideoConf() {
		if (!isCSVConf()) {
			return false;
		}
		return nativeConfType == EmNativeConfType.VIDEO;
	}

	/**
	 * 是否是有主席权限
	 * 
	 * @return
	 */
	public static boolean isChairMan() {
		return mConfInfo != null && mConfInfo.tChairman != null && mLabelAssign != null && (mConfInfo.tChairman.dwTerId == mLabelAssign.terNo);
	}

	/**
	 * 主席
	 * 
	 * @return
	 */
	public static TMtId getChairMan() {
		if (mConfInfo == null) {
			return null;
		}

		return mConfInfo.tChairman;
	}

	/**
	 * 主讲
	 * 
	 * @return
	 */
	public static TMtId getSpeaker() {
		if (mConfInfo == null) {
			return null;
		}

		return mConfInfo.tSpeaker;
	}

	/**
	 * 是否是是发言人
	 * @return
	 */
	public static boolean isSpeaker() {
		return mConfInfo != null && mConfInfo.tSpeaker != null && mLabelAssign != null && (mConfInfo.tSpeaker.dwTerId == mLabelAssign.terNo);
	}

	/**
	 * 获取 会议码率 取小 
	 * @return 
	 */

	public static int getCallRate() {
		if (null == currTMtCallLinkSate || 0 == currTMtCallLinkSate.dwCallRate) {
			return confCallRete(TruetouchGlobal.getContext());
		}
		return Math.min(confCallRete(TruetouchGlobal.getContext()), currTMtCallLinkSate.dwCallRate);
	}

	/**
	 * 判断码率是否为音频的码率
	 * 
	 * @param rate
	 * @return
	 */
	public static boolean isAudioCallRate(int rate) {
		if (rate <= CALLRATE_SPLITLINE) {
			return true;
		}

		return false;

		// EmAudFormat emAudioFormat = null;
		// MtVConfInfo mtVConfInfo = new MtVConfInfo();
		// TMTAudioPriorCfg audioPriorCfg = mtVConfInfo.getAudioPriorCfg();
		// boolean isSetAudioPrecedence = mtVConfInfo.isSetAudioPrecedence(true);
		//
		// if (null != audioPriorCfg) {
		// emAudioFormat = audioPriorCfg.emAudioFormat;
		// }
		//
		// // 没有开启音频优先
		// if (!isSetAudioPrecedence) {
		// return false;
		// }
		//
		// if (emAudioFormat == EmAudFormat.emAOpus) {
		// return false;
		// }
		//
		// // 开启音频优先之后非OPUS格式需要*3，如果差值不够64，视频不能被开启
		// if ((rate - 40 * 3) < CALLRATE_SPLITLINE) {
		// return true;
		// }
		//
		// return true;
	}

	/**
	 * 参会码率
	 * 
	 * @param context
	 * @return
	 */
	public static int confCallRete(Context context) {
		if (context == null) {
			context = MyApplication.getContext();
		}

		if (context == null) {
			return CALLRATE_SPLITLINE;
		}

		return callRate;
	}

	/**
	 * P2P呼叫
	 *
	 * @param e164
	 */
	public static void makeCall(final String e164, final boolean isAudio) {
		if (e164 == null || e164.length() == 0) {
			return;
		}
		short callRate = (short) confCallRete(TruetouchGlobal.getContext());

		// 音频关闭第一路视频流
		if (isAudio) {
			Conference.mainVideoOff();
		} else {
			// // 0 h323 , 1 sip
			Conference.setCallCapPlusCmd(getSendResolutionByCallRate(callRate), getRecResolutionByCallRate(callRate),
					EmConfProtocol.em323.ordinal());
		}
		Conference.makeCall(e164, callRate);
	}

	public static int getConfProtocolOrigin(int type) { // 0 h323 , 1 sip
		if (type == 1) {
			return EmConfProtocol.emsip.ordinal();
		}
		return EmConfProtocol.em323.ordinal();
	}

	public static int getSendResolutionByCallRate(int callRate) {
		if (callRate < 320) {
			return EmMtResolution.emMtCIF_Api.ordinal();
		} else if (callRate < 512) {
			return EmMtResolution.emMtCIF_Api.ordinal();
		} else {
			return EmMtResolution.emMt4CIF_Api.ordinal();
		}
	}

	public static int getRecResolutionByCallRate(int callRate) {
		if (callRate < 320) {
			return EmMtResolution.emMtCIF_Api.ordinal();
		} else if (callRate < 512) {
			return EmMtResolution.emMt4CIF_Api.ordinal();
		} else {
			return EmMtResolution.emMtHD720p1280x720_Api.ordinal();
		}
	}

	/**
	 * 音视频入会
	 * 
	 * @param vconf
	 *  isAudio
	 */
	private static void joinConf(VConf vconf) {
		if (vconf == null) {
			return;
		}

		String confE164 = vconf.getAchConfE164();
		VConferenceManager.mCallPeerE164Num = confE164;

		if (confE164 == null) {
			confE164 = "";
		}

		short callRate = (short) confCallRete(TruetouchGlobal.getContext());

		Conference.setCallCapPlusCmd(getSendResolutionByCallRate(callRate), getRecResolutionByCallRate(callRate),
				EmConfProtocol.em323.ordinal());
		TMtJoinConfParam mTMtJoinConfParam = new TMtJoinConfParam(callRate, confE164, "", false, null);

		Conference.joinConf(mTMtJoinConfParam);
	}

	/**
	 * 打开视频会议界面
	 * 
	 * @param cucrActivity
	 * @param isMackCall
	 * @param vconfName
	 * @param e164
	 */
	public static void openVConfAudioUI(Activity cucrActivity, boolean isMackCall, String vconfName, String e164) {
		Bundle extras = new Bundle();
		extras.putString("VconfName", vconfName);
		extras.putString(KDConstants.E164NUM, e164);
		extras.putBoolean("MackCall", isMackCall);
		extras.putBoolean("JoinConf", !isMackCall);

		if (VConferenceManager.nativeConfType == EmNativeConfType.AUDIO || VConferenceManager.nativeConfType == EmNativeConfType.AUDIO_AND_DOAL) {
		} else {
			if (isMackCall) {
				VConferenceManager.nativeConfType = EmNativeConfType.CALLING_AUDIO;
			} else {
				VConferenceManager.nativeConfType = EmNativeConfType.JOINING_AUDIO;
			}
		}
		if (null != e164) {
			VConferenceManager.mCallPeerE164Num = e164;
		}

		ActivityUtils.openActivity(cucrActivity, VConfAudioUI.class, extras);
	}

	/**
	 * 打开视频会议界面
	 * 
	 * @param cucrActivity
	 * @param isMackCall
	 * @param vconfName
	 * @param e164
	 */
	public static void openVConfVideoUI(Activity cucrActivity, boolean isMackCall, String vconfName, String e164) {
		// 注册视频会议Service
		VideoCapServiceManager.bindService();

		Bundle extras = new Bundle();
		extras.putString("VconfName", vconfName);
		extras.putString(KDConstants.E164NUM, e164);
		extras.putBoolean("MackCall", isMackCall);
		extras.putBoolean("JoinConf", !isMackCall);

		if (isMackCall) {
			VConferenceManager.nativeConfType = EmNativeConfType.CALLING_VIDEO;
		} else {
			VConferenceManager.nativeConfType = EmNativeConfType.JOINING_VIDEO;
		}
		if (null != e164) {
			VConferenceManager.mCallPeerE164Num = e164;
		}

		ActivityUtils.openActivity(cucrActivity, VConfVideoUI.class, extras);
        cucrActivity.finish();
	}

	/**
	 * 呼叫音频会议
	 */
	public static void makeCallAudio(String e164) {
		makeCall(e164, true);
	}

	/**
	 * 呼叫视频会议
	 */
	public static void makeCallVideo(String e164) {
		makeCall(e164, false);
	}

	/**
	 * 加入视频会议
	 */
	public static void joinConfByVideo(VConf vconf) {

		if (vconf == null) {
			return;
		}

		String confE164 = vconf.getAchConfE164();
		VConferenceManager.mCallPeerE164Num = confE164;

		if (confE164 == null) {
			confE164 = "";
		}

		short callRate = (short) confCallRete(TruetouchGlobal.getContext());

		Conference.setCallCapPlusCmd(getSendResolutionByCallRate(callRate), getRecResolutionByCallRate(callRate),
				EmConfProtocol.em323.ordinal());
		TMtJoinConfParam mTMtJoinConfParam = new TMtJoinConfParam(callRate, confE164, "", false, null);

		Conference.joinConf(mTMtJoinConfParam);
	}

	/**
	 * 加入音频会议
	 */
	public static void joinConfByAudio(VConf vconf) {
		if (vconf == null) {
			return;
		}

		String confE164 = vconf.getAchConfE164();
		VConferenceManager.mCallPeerE164Num = confE164;

		if (confE164 == null) {
			confE164 = "";
		}

		short callRate = (short) confCallRete(TruetouchGlobal.getContext());
		// 音频入会先关闭第一路视频流
		Conference.mainVideoOff();
		VideoCapServiceManager.unBindService();

		Conference.setCallCapPlusCmd(getSendResolutionByCallRate(callRate), getRecResolutionByCallRate(callRate),
				EmConfProtocol.em323.ordinal());
		TMtJoinConfParam mTMtJoinConfParam = new TMtJoinConfParam(callRate, confE164, "", false, null);
		Conference.joinConf(mTMtJoinConfParam);
	}

	/**
	 * 无帐号加入会议
	 *
	 * @param e164
	 *  alias
	 */
	public static void joinConfByVideo(Activity activity, String e164) {
		Bundle pBundle = new Bundle();
		pBundle.putString(KDConstants.E164NUM, e164);
		pBundle.putBoolean("JoinVConf", true);
		VConferenceManager.nativeConfType = EmNativeConfType.JOINING_VIDEO;
		ActivityUtils.openActivity(activity, VConfVideoUI.class, pBundle);
	}


	/**
	 * 根据呼叫状态选择是否切换界面
	 * 
	 *  currTMtCallLinkSate
	 */
	public static void switchVConfViewFromCallLinkSate() {
		Activity currActivity = PcAppStackManager.Instance().currentActivity();
		// mcu 音频呼叫 码率为64
		if (VConferenceManager.currTMtCallLinkSate.isAudio()) {
			VConferenceManager.nativeConfType = EmNativeConfType.AUDIO;
		} else {
			// 处于被叫界面
			if (currActivity instanceof VConfAVResponseUI) {
				if (((VConfAVResponseUI) currActivity).ismIsAudioConf()) {
					VConferenceManager.nativeConfType = EmNativeConfType.AUDIO;
				} else {
					VConferenceManager.nativeConfType = EmNativeConfType.VIDEO;
				}
			}
		}
		if (currActivity instanceof VConfVideoUI) {
			((VConfVideoUI) currActivity).switchVConfFragment();
		} else if (currActivity instanceof VConfAudioUI) {
			((VConfAudioUI) currActivity).switchVConfFragment();
		}
	}

	/**
	 * 退出音视频会议
	 * 
	 * @param hasConnected	是否已经接通
	 */
	public static void quitConfAction(boolean hasConnected, boolean isJoinConfByPhone) {
		final String peerE164Num = mCallPeerE164Num;

		/*		if (!isJoinConfByPhone) {
					saveVconfRecord(hasConnected);
				}
		*/
		cleanConf();
		forceCloseVConfActivity();
		VideoCapServiceManager.unBindService();
		// 修改自己的状态
		LoginStateManager.imModifySelfStateReq();
	}

	/**
	 *  保存VConf记录
	 *  
	 * @param hasConnected	是否已经接通
	 */

	/**
	 * 会议持续时间
	 * 
	 * @param startTime
	 * @param endTime
	 * @return	00:00:00
	 */
	public static String getVConfDuration(long startTime, long endTime) {
		long duration = endTime - startTime;
		if (duration <= 0) {
			return "00:00:00";
		}

		long h = duration / 60 / 60 / 1000;
		long moH = duration % (60 * 60 * 1000);
		long m = moH / 60 / 1000;
		long moM = moH % (60 * 1000);
		long s = moM / 1000;

		StringBuffer sb = new StringBuffer();
		if (h < 10) {
			sb.append(0);
		}
		sb.append(h);
		sb.append(":");
		if (m < 10) {
			sb.append(0);
		}
		sb.append(m);
		sb.append(":");
		if (s < 10) {
			sb.append(0);
		}
		sb.append(s);

		return sb.toString();
	}

	/**
	 * 结束音视频会议的原因
	 * 
	 * @param context
	 * @param isCaller		主叫
	 * @param isAudio		音频
	 * @param vconPprefix	添加前缀
	 * @param emReason		结束原因
	 * @return
	 */
	public static String getVConReasonFromEndVConf(Context context, boolean isCaller, boolean isAudio, boolean vconPprefix, EmMtCallDisReason emReason) {
		if (null == context) {
			context = MyApplication.getContext();
		}

		if (null == context) {
			return "";
		}

		int resId = getVConReasonFromEndVConfResId(isCaller, isAudio, emReason);
		if (vconPprefix) {
			if (isAudio) {
				return "音频 " + context.getString(resId);
			} else {
				return "视频" + context.getString(resId);
			}
		}

		return context.getString(resId);
	}

	/**
	 * 结束音视频会议的原因ResourcesId
	 * 
	 * @param isCaller
	 * @param isAudio
	 * @param emReason
	 * @return
	 */
	public static int getVConReasonFromEndVConfResId(boolean isCaller, boolean isAudio, EmMtCallDisReason emReason) {
		int resId;
		if (isCaller) {
			if (emReason == EmMtCallDisReason.emDisconnect_Rejected) {
				resId = R.string.vconf_rejected;
			} else if (emReason == EmMtCallDisReason.emDisconnect_Local || emReason == EmMtCallDisReason.emDisconnect_Normal) {
				resId = R.string.vconf_canceled;
			} else if (emReason == EmMtCallDisReason.emDisconnect_P2Ptimeout) {
				resId = R.string.vconf_noresponse;
			} else {
				resId = R.string.vconf_unconnect;
			}
		} else {
			if (emReason == EmMtCallDisReason.emDisconnect_Local || emReason == EmMtCallDisReason.emDisconnect_Rejected) {
				resId = R.string.vconf_rejected;
			} else if (emReason == EmMtCallDisReason.emDisconnect_Normal) {
				resId = R.string.vconf_canceled;
			} else if (emReason == EmMtCallDisReason.emDisconnect_P2Ptimeout) {
				resId = R.string.vconf_noresponse;
			} else {
				resId = R.string.vconf_unconnect;
			}
		}

		return resId;
	}

	/**
	 * 关闭音视频相关界面
	 */
	public static void forceCloseVConfActivity() {
		Activity currentActivity = PcAppStackManager.Instance().currentActivity();
		// 接听界面是否存在
		if (currentActivity instanceof VConfAVResponseUI) {
			PcAppStackManager.Instance().popActivity(currentActivity);
		}

		VConfAudioUI vconfAudioUI = (VConfAudioUI) PcAppStackManager.Instance().getActivity(VConfAudioUI.class);
		if (vconfAudioUI != null) {
			PcAppStackManager.Instance().popActivity(vconfAudioUI);
		}

		VConfVideoUI vconfVideoUI = (VConfVideoUI) PcAppStackManager.Instance().getActivity(VConfVideoUI.class);
		if (vconfVideoUI != null) {
			PcAppStackManager.Instance().popActivity(vconfVideoUI);
		}
	}

	/**
	 * 结束音视频通话时，恢复到扬声器模式
	 * 
	 * <pre>
	 * 每次入会会通过MtVConfInfo检测通话模式
	 * 所以，在恢复扬声器模式时，如果发现当前有耳机连接
	 * 则，先存储模式的字段即可
	 * </pre>
	 */
	public static void recoverSpeakerphoneOn() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 连接到耳机，设置模式无效
				TerminalUtils.setSpeakerphoneOn(TruetouchGlobal.getContext(), true, true);
			}
		}).start();
	}

	/**
	 * 检测会议是否可用
	 * 
	 * @param toastInfo				 会议不可用时Toast提示
	 * @param reRegisterGK 			GK失败时重新连接
	 * @param isCheckVCVonf 		检测是否正在会议中
	 * @return
	 */
	public static boolean isAvailableVCconf(boolean toastInfo, boolean reRegisterGK, boolean isCheckVCVonf) {
		return isAvailableVCconf(toastInfo, reRegisterGK, isCheckVCVonf, false, true);
	}

	/**
	 * 检测会议是否可用
	 * 
	 * @param toastInfo			会议不可用时Toast提示
	 * @param reRegisterGK		GK失败时重新连接
	 * @param isCheckVCVonf		检测是否正在会议中
	 * @param isPhone
	 * @return
	 */
	public static boolean isAvailableVCconf(boolean toastInfo, boolean reRegisterGK, boolean isCheckVCVonf, boolean isPhone) {
		return isAvailableVCconf(toastInfo, reRegisterGK, isCheckVCVonf, isPhone, true);
	}

	/**
	 * 检测会议是否可用
	 * 
	 * @param toastInfo 			会议不可用时Toast提示
	 * @param reRegisterGK 			GK失败时重新连接
	 * @param isCheckVCVonf 		检测是否正在会议中
	 * @param toastPullToRefresh 	电话入会方式
	 * @param toastPullToRefresh 	提示下拉刷新
	 * @return
	 */
	public static boolean isAvailableVCconf(boolean toastInfo, boolean reRegisterGK, boolean isCheckVCVonf, boolean isPhone, boolean toastPullToRefresh) {
		Context context = MyApplication.getContext();

		// 无网络提示
		if (!NetWorkUtils.isAvailable(context)) {
			return false;
		}

		// 2G网络不能入会
		if (!isPhone && NetWorkUtils.is2G(context)) {

			return false;
		}

		// GK注册失败
		if (!GKStateMannager.mRegisterGK) {
			if (reRegisterGK) {
				GKStateMannager.instance().registerGK();
				Toast.makeText(MyApplication.getContext(), "GK重连中...", Toast.LENGTH_SHORT).show();
			}

			return false;
		}

		// 正在发起呼叫的状态 or 正在呼叫会议
		if (isCalling() || EmNativeConfType.isCalling(nativeConfType)) {
			if (toastInfo) {
				Toast.makeText(MyApplication.getContext(), "正在呼叫中。。。", Toast.LENGTH_SHORT).show();
			}
			return false;
		}

		// 正处于被呼叫状态
		if ((isCallIncoming())) {
			if (toastInfo) {
				Toast.makeText(MyApplication.getContext(), "正在被叫中。。。", Toast.LENGTH_SHORT).show();
			}
			return false;
		}

		// 正在召集会议
		if (EmNativeConfType.isConveneing(nativeConfType)) {
			if (toastInfo) {
				Toast.makeText(MyApplication.getContext(), "正在召集会议。。。", Toast.LENGTH_SHORT).show();
			}
			return false;
		}

		return true;
	}

	/**
	 * 添加 会议包含终端信息
	 * @param tMtInfoList
	 * @param reset
	 */
	public static void addTMtInfoEx(List<TMTEntityInfo> tMtInfoList, boolean reset) {
		if (null == mTMtInfoList) {
			mTMtInfoList = new ArrayList<TMTEntityInfo>();
		}

		if (reset) {
			mTMtInfoList.clear();
		}

		if (null == tMtInfoList || tMtInfoList.isEmpty()) {
			return;
		}

		if (reset) {
			mTMtInfoList.addAll(tMtInfoList);
		}

		for (TMTEntityInfo tMtInfoEx : tMtInfoList) {
			if (tMtInfoEx == null) continue;
			if (mTMtInfoList.contains(tMtInfoEx)) {
				mTMtInfoList.remove(tMtInfoEx);
			}
			mTMtInfoList.add(tMtInfoEx);
		}
	}

	/**
	 * 删除会议包含终端信息
	 * @param tMtInfoEx
	 */
	public static void delTmtInfoEx(TMTEntityInfo tMtInfoEx) {
		if (tMtInfoEx == null || mTMtInfoList == null || mTMtInfoList.isEmpty()) {
			return;
		}

		mTMtInfoList.remove(tMtInfoEx);
	}

	/**
	 * 删除会议包含终端信息
	 *  tMtInfoEx
	 */
	public static void delTmtInfoByTerId(int terId) {
		if (mTMtInfoList == null || mTMtInfoList.isEmpty()) {
			return;
		}

		for (TMTEntityInfo tMtInfoEx : mTMtInfoList) {
			if (tMtInfoEx.dwTerId == terId) {
				mTMtInfoList.remove(tMtInfoEx);
				break;
			}
		}
	}

	/**
	 * 通过terNo获取alias
	 *  terNo
	 * @return
	 */
	public static String getAliasByTerId(int terId) {
		if (null == mTMtInfoList) {
			return null;
		}

		for (TMTEntityInfo tMtInfoEx : mTMtInfoList) {
			if (tMtInfoEx.dwTerId == terId) {
				if (null != tMtInfoEx.tMtAlias) {
					return tMtInfoEx.tMtAlias.getAlias();
				}
			}
		}

		return null;
	}

	/**
	 * 通过terNo获取alias
	 *  terNo
	 * @return
	 */
	public static TMTEntityInfo getMtInfoByTerId(int terId) {
		if (null == mTMtInfoList) {
			return null;
		}

		for (TMTEntityInfo tMtInfoEx : mTMtInfoList) {
			if (tMtInfoEx.dwTerId == terId) {
				return tMtInfoEx;
			}
		}

		return null;
	}

	/**
	 * 会议包含终端信息
	 * @return
	 */
	public static List<TMTEntityInfo> getTMtInfoList() {
		return mTMtInfoList;
	}

	/**
	 * 创建视频会议
	 * 
	 * @param activity
	 * @param vconfName 会议名称
	 */

	public static void createVConf2OpenVideoUI(Activity activity, String vconfName, List<TMtAddr> tMtList, int vconfQuality, int duration) {
		if (null == tMtList) {
			return;
		}

		// 本端
		TMtAddr mTMAddr = new TMtAddr(EmMtAddrType.emAddrE164, null, GKStateMannager.mE164);
		if (tMtList.contains(mTMAddr)) {// 统一不包含本端
			tMtList.remove(mTMAddr);
		}

		Bundle extras = new Bundle();
		extras.putString("VconfName", vconfName);
		extras.putString("tMtList", new Gson().toJson(tMtList));
		extras.putInt("VconfQuality", vconfQuality);
		extras.putInt("VconfDuration", duration);

		// 正在会议中
		if (isCSVConf()) {
			Conference.hangupConfByReason(EmMtCallDisReason.emDisconnect_PeerInConf);
		} else {
			VConferenceManager.nativeConfType = EmNativeConfType.CONVENEING_VIDEO;
		}
		// 注册视频会议Service
		VideoCapServiceManager.bindService();

		ActivityUtils.openActivity(activity, VConfVideoUI.class, extras);

	}

	/**
	 * 创建会议
	 * @param activity
	 * @param vconfName 会议名称
	 *  tmplt 会议模板
	 * @param tMtList 与会人员
	 * @param vconfQuality 会议质量
	 * @param duration 会议时长
	 */

	public static void createVConf2OpenAudioUI(Activity activity, String vconfName, List<TMtAddr> tMtList,
                                               int vconfQuality, int duration) {
		if (null == tMtList) {
			return;
		}

		// 本端
		TMtAddr mTMAddr = new TMtAddr(EmMtAddrType.emAddrE164, null, GKStateMannager.mE164);
		if (tMtList.contains(mTMAddr)) {// 统一不包含本端
			tMtList.remove(mTMAddr);
		}

		Bundle extras = new Bundle();
		extras.putString("VconfName", vconfName);
		extras.putString("tMtList", new Gson().toJson(tMtList));
		extras.putInt("VconfQuality", vconfQuality);
		extras.putInt("VconfDuration", duration);

		// 正在会议中
		if (isCSVConf()) {
			Conference.hangupConfByReason(EmMtCallDisReason.emDisconnect_PeerInConf);
		} else {
			VConferenceManager.nativeConfType = EmNativeConfType.CONVENEING_AUDIO;
		}

		ActivityUtils.openActivity(activity, VConfAudioUI.class, extras);
	}

	/**
	 * 通过ras 请求创建会议  tMtList不带自己
	 *
	 *   StringBuffer(TMtCreateConfParam_Api)	要创建的会议信息
	 * @return
	 */
	public static int confCreateConfCmd(String confTitle, List<TMtAddr> tmtList, int rate, int duration, boolean isAudio) {
		Conference.setCallCapPlusCmd(getSendResolutionByCallRate(getCallRate()),
				getRecResolutionByCallRate(getCallRate()), EmConfProtocol.em323.ordinal());

		if (null == confTitle) {
			confTitle = "";
		}

		TMtCreateConfParamApi tmtCreateConfParamApi = new TMtCreateConfParamApi();
		tmtCreateConfParamApi.achName = confTitle;// 会议名字
		tmtCreateConfParamApi.dwDuration = duration * 60;// 会议持续时长
		tmtCreateConfParamApi.dwBitrate = rate;// 视频码率
		if (isAudio) {
			tmtCreateConfParamApi.dwBitrate = 64;// 音频码率
		}
		tmtCreateConfParamApi.emMeetingSafe = EmMtOpenMode.emMt_Open;// 会议安全mode
		tmtCreateConfParamApi.achPassword = "";// 会议密码，如果为0，表示没有不需要密码
		if (isAES) {
			tmtCreateConfParamApi.emEncryptedType = EmEncryptArithmetic.emAES;// 会议加密方式
		} else {
			tmtCreateConfParamApi.emEncryptedType = EmEncryptArithmetic.emEncryptNone;// 会议加密方式
		}
		tmtCreateConfParamApi.emMeetingType = EmMeetingSafeType.emRestMeetingType_Public;// 会议是否是 端口会议

		tmtCreateConfParamApi.emClosedMeeting = EmClosedMeeting.emClosedMeeting_Close;// 会议免打扰，1开启，0关闭
		tmtCreateConfParamApi.emVideoQuality = EmVideoQuality.emRestSpeedPrecedence;// 视频质量,0：质量优先；1：速度优先
		tmtCreateConfParamApi.emDualMode = EmMtDualMode.emMt_Dual_Mode_Everyone;// 双流权限
		// boolean mute = new MtVConfInfo().isMtMute(false);
		tmtCreateConfParamApi.bInitMute = false;// 初始化哑音：1是，0否
		tmtCreateConfParamApi.bPublicMeeting = false;// 是否是公共会议室
		// 视频格式
		tmtCreateConfParamApi.dwVideoCount = 1;
		TMTVideoFormatList videoFormat = new TMTVideoFormatList();
		videoFormat.emVideoFormat = EmVidFormat.emVH264;
		videoFormat.emVideoProfile = EmH264Profile.emBaseline;
		videoFormat.emResolution = EmMtResolution.emMt4CIF_Api;
		videoFormat.dwFrame = 30;
		List<TMTVideoFormatList> videoFormatList = new ArrayList<TMTVideoFormatList>();
		videoFormatList.add(videoFormat);
		tmtCreateConfParamApi.atVideoFormatList = videoFormatList;

		// 音频格式
		tmtCreateConfParamApi.dwAudioCount = 1;
		TMTAudioFormatList audioFormat = new TMTAudioFormatList();
		audioFormat.emAudioFormat = EmAudFormat.emAOpus;
		audioFormat.emAaccnnlNum = EmAacChnlNum.emCnNumCust;
		List<TMTAudioFormatList> audioFormatList = new ArrayList<TMTAudioFormatList>();
		audioFormatList.add(audioFormat);
		tmtCreateConfParamApi.atAudioFormatList = audioFormatList;

		// 参会成员列表
		if (null != tmtList) {
			tmtCreateConfParamApi.dwMemberCount = tmtList.size();
			tmtCreateConfParamApi.atMembers = new ArrayList<TMTCreateConfMember>();
			for (TMtAddr addr : tmtList) {
				TMTCreateConfMember confMember = new TMTCreateConfMember();
				confMember.achAccount = addr.achAlias;
				confMember.achName = addr.achAlias;
				confMember.emAccountType = addr.emAddrType;
				tmtCreateConfParamApi.atMembers.add(confMember);
			}
		}
		// 混音,画面合成参数
		tmtCreateConfParamApi.emMixMode = EmMtMixType.mcuWholeMix;
		// / 画面合成设置
		TMTCreateConfVmp tConfVmp = new TMTCreateConfVmp();
		tConfVmp.bEnable = true;
		tConfVmp.emStyle = EmMtVmpStyle.emMt_VMP_STYLE_DYNAMIC;
		tConfVmp.bShowMTName = true;
		tConfVmp.bIsBroadcast = true;
		tmtCreateConfParamApi.tVmp = tConfVmp;

		// 轮询设置
		TMTCreateConfPoll tMtConfPoll = new TMTCreateConfPoll();
		tMtConfPoll.bEnable = false;
		tmtCreateConfParamApi.tPoll = tMtConfPoll;
		tmtCreateConfParamApi.bCallChase = false;
		tmtCreateConfParamApi.bVoiceInspireEnable = false;
		tmtCreateConfParamApi.emCascadeMode = EmRestCascadeMode.emRestCascade_Merge;
		tmtCreateConfParamApi.bCascadeUpload = true;
		tmtCreateConfParamApi.bCascadeReturn = false;

		// 设置管理员
		TMTCreateConfMember tAdminConf = new TMTCreateConfMember();
		tAdminConf.achAccount = GKStateMannager.mE164;
		tAdminConf.emAccountType = EmMtAddrType.emAddrE164;
		tmtCreateConfParamApi.tAdmin = tAdminConf;
		// 设置speaker为空
		tmtCreateConfParamApi.tSpeaker = null;

		// 录像设置
		/*TMTCreateRecordRecord record = new TMTCreateRecordRecord();
		record.bDoubleFlow = false;
		record.bEnable = false;
		record.bMain = true;
		record.bPublish = false;
		mTMtCreateConfParamApi.tRecord = record;*/
		// 主播设置
		/*TMTCreateConfMultiCast confMultiCast = new TMTCreateConfMultiCast();
		confMultiCast.bEnable = true;
		mTMtCreateConfParamApi.tMultiCast = confMultiCast;*/
		// 卫星会议
		TMTCreateConfSatellite confSatellite = new TMTCreateConfSatellite();
		confSatellite.bEnable = false;
		tmtCreateConfParamApi.tSatellite = confSatellite;
		Log.w("Test", "召集会议请求: " + tmtCreateConfParamApi.toJson());
		return Conference.createConf(tmtCreateConfParamApi);
	}

	/**
	 * 获取视频会议
	 * 
	 * @return
	 */
	public static List<VConf> getVConfs(boolean sort) {
		if (sort && null != vconfList && !vconfList.isEmpty()) {
			Collections.sort(vconfList);
		}
		return vconfList;
	}

	private static List<VConf> vconfList = new ArrayList<VConf>();

	/**
	 * 重新设置视频会议
	 * @param vConfs
	 */
	public static void resetVConf(Collection<VConf> vConfs, int confListType) {
		vconfList.clear();

		if (null == vConfs || vConfs.isEmpty()) {
			return;
		}

		for (VConf vconf : vConfs) {
			if (null == vconf) continue;

			if (StringUtils.isNull(vconf.getAchConfName())) {
				vconf.setAchConfName(vconf.getAchConfE164());
			}
			vconf.setConfListType(confListType);
			vconf.setStartTime(vconf.gettConfStartTime().tMTTime2String()); // 2015-03-02 13:00:07
			vconfList.add(vconf);
		}
	}

	/**
	 * 添加会议
	 * 
	 * <pre>
	 * 在添加会议时，必须先保证数据表中没有对应的会议
	 * 本方法只进行插入，不检测是否重复，也不检测表中是否存在
	 * </pre>
	 * @param vConfs
	 */
	public static void addVConf(Collection<VConf> vConfs, int confListType) {
		if (null == vConfs || vConfs.isEmpty()) {
			return;
		}

		for (VConf vconf : vConfs) {
			if (null == vconf) continue;

			if (StringUtils.isNull(vconf.getAchConfName())) {
				vconf.setAchConfName(vconf.getAchConfE164());
			}
			vconf.setConfListType(confListType);
			vconf.setStartTime(vconf.gettConfStartTime().tMTTime2String());// 2015-03-02 13:00:07
			vconfList.add(vconf);
		}
	}

	/**
	 * 添加一个视频会议
	 * @param vConf
	 */
	public static void addVConf(VConf vConf, int confListType) {
		if (null == vConf) {
			return;
		}
		vConf.setConfListType(confListType);
		vConf.setStartTime(vConf.gettConfStartTime().tMTTime2String());// 2015-03-02 13:00:07
		vconfList.add(vConf);
	}

	/**
	 *  会议中 主席邀请其他终端入会
	 */

	public static void inviteVConfPersonWin() {
		Dialog dialog = new InviteVConfDialog(PcAppStackManager.Instance().currentActivity());
		dialog.show();
	}

	/**
	 * 打开会议详情
	 *
	 * @param activity
	 * @param e164
	 */
	public static void openVConfDetails(Activity activity, String e164) {
		if (activity == null) {
			return;
		}

		Intent intent = new Intent();
		intent.setClass(activity, VConfDetailsUI.class);
		Bundle bundle = new Bundle();
		bundle.putString(KDConstants.E164NUM, e164);
		intent.putExtras(bundle);
		ActivityUtils.openActivity(activity, intent);
		// 获取会议详情
		Conference.requestConfDetailInfo(e164);
	}
}
