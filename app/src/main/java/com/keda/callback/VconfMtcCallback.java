/**
 * @(#)VconfMtcCallback.java   2014-12-25
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.callback;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.gkzxhn.gkprison.base.MyApplication;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.keda.main.VConfListActivity;
import com.keda.sky.app.LoginStateManager;
import com.keda.sky.app.PcAppStackManager;
import com.keda.vconf.bean.VConf;
import com.keda.vconf.controller.VConfAVResponseUI;
import com.keda.vconf.controller.VConfDetailsUI;
import com.keda.vconf.controller.VConfFunctionFragment;
import com.keda.vconf.controller.VConfVideoUI;
import com.keda.vconf.dialog.ApplyDialog;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Conference;
import com.kedacom.kdv.mt.bean.LabelAssign;
import com.kedacom.kdv.mt.bean.TAssVidStatus;
import com.kedacom.kdv.mt.bean.TMTEntityInfo;
import com.kedacom.kdv.mt.bean.TMtCallLinkSate;
import com.kedacom.kdv.mt.bean.TMtConfDetailInfo;
import com.kedacom.kdv.mt.bean.TMtConfInfo;
import com.kedacom.kdv.mt.bean.TMtId;
import com.kedacom.kdv.mt.bean.TMtSimpConfInfo;
import com.kedacom.kdv.mt.constant.EmConfListType;
import com.kedacom.kdv.mt.constant.EmMtChanState;
import com.kedacom.kdv.mt.constant.EmNativeConfType;
import com.pc.utils.NetWorkUtils;
import com.pc.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
  *  音视频 callback
  */

public class VconfMtcCallback {

	/**
	* 获取会议信息
	* @param body 
	*/

	public static void parseConfInfo(String body) {
		final TMtConfInfo confInfo = new TMtConfInfo().fromJson(body);
		VConferenceManager.mConfInfo = confInfo;

		if (null == confInfo) {
			return;
		}

		if (StringUtils.isNull(confInfo.achConfE164) || StringUtils.isNull(confInfo.achConfName)) {
			return;
		}

		/*	// 获取画面合成参数
			ConfLibCtrl.confGetConfVMPParamCmd();*/

	}

	/**
		* 会议消息simple
		* @param body 
		*/

	public static void parseSimpleConfInfo(String body) {
		TMtSimpConfInfo simpConfInfo = new TMtSimpConfInfo().fromJson(body);
		if (VConferenceManager.mConfInfo != null) {
			VConferenceManager.mConfInfo.tChairman = simpConfInfo.tChairman;
			VConferenceManager.mConfInfo.tSpeaker = simpConfInfo.tSpeaker;
			refreshBottomFragment();
		}
	}

	/**
	* 解析呼叫状态
	* 状态通知
	* 
	*/

	public static void parseCallLinkSate(String callingSateJson) {
		TMtCallLinkSate callLinkSate = new TMtCallLinkSate().fromJson(callingSateJson);
		if (null == callLinkSate) {
			return;
		}

		if (null != callLinkSate.tPeerAlias) {
			String e164 = callLinkSate.tPeerAlias.getAliasE164();
			if (!StringUtils.isNull(e164)) {
				VConferenceManager.mCallPeerE164Num = e164;
			}
		}

		// 被叫
		if (callLinkSate.isCallIncoming()) {
			if (!VConferenceManager.isAvailableVCconf(true, false, true, true, false) || VConferenceManager.isCallIncoming() || VConferenceManager.isCalling()) {
				Conference.rejectConf();
				return;
			}
			// 2G拒绝呼叫
			if (NetWorkUtils.is2G(MyApplication.getContext())) {
				Conference.rejectConf();
				return;
			}

			VConferenceManager.currTMtCallLinkSate = callLinkSate;

			if (MyApplication.getApplication() != null) {
				// 跳转到应答界面
				Intent responseIntent = new Intent();
				responseIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				responseIntent.setClass(MyApplication.getApplication(), VConfAVResponseUI.class);
				MyApplication.getApplication().startActivity(responseIntent);
			}
		} else {
			VConferenceManager.currTMtCallLinkSate = callLinkSate;

			if (VConferenceManager.answerMode == 0) {//自动接听
				/*Activity ac = PcAppStackManager.Instance().currentActivity();

				if (VConferenceManager.currTMtCallLinkSate.isAudio()) {
					VConferenceManager.nativeConfType = EmNativeConfType.AUDIO;
					Intent i =new Intent();
					i.setClass(ac, VConfAudioUI.class);
					ac.startActivity(i);
				} else {
					VConferenceManager.nativeConfType = EmNativeConfType.VIDEO;
					Intent i = new Intent();
					i.setClass(ac, VConfVideoUI.class);
					ac.startActivity(i);
				}
				return;*/
			}

			// 入会成功
			if (VConferenceManager.isCSVConf()) {
				VConferenceManager.switchVConfViewFromCallLinkSate();

				VConfAVResponseUI avrResponesUI = (VConfAVResponseUI) PcAppStackManager.Instance().getActivity(VConfAVResponseUI.class);
				if (null != avrResponesUI) {
					//					avrResponesUI.releasMyFacingSV();
					Boolean mIsAudioConf = avrResponesUI.ismIsAudioConf();
					if (mIsAudioConf) {
						VConferenceManager
								.openVConfAudioUI(avrResponesUI, false, VConferenceManager.currTMtCallLinkSate.tPeerAlias.getAlias(), VConferenceManager.mCallPeerE164Num);
					} else {
						VConferenceManager
								.openVConfVideoUI(avrResponesUI, false, VConferenceManager.currTMtCallLinkSate.tPeerAlias.getAlias(), VConferenceManager.mCallPeerE164Num);
					}
//					avrResponesUI.finish();
				}
				// 修改自己的状态
				LoginStateManager.imModifySelfStateReq();
			}
		}
	}

	/**
		* 多点会议中解析 发送者
		* @param b
		* @param jsonBodyObj 
		*/
	/*
	public static void parseAssSreamSender(JSONObject jsonBodyObj) {
	// @吴虎， v4 不支持sender
	if (EmModle.isH323(TruetouchApplication.getApplication().currLoginModle())) {
		return;
	}
	// JSONObject assParamJsonObj;
	try {
		// assParamJsonObj = jsonBodyObj.getJSONObject(MyMtcCallback.KEY_AssParam);
		// if (assParamJsonObj.getBoolean("basetype")) {
		String dualStreamSender = jsonBodyObj.getString(MyMtcCallback.KEY_MainParam);
		TMtId dualStreamMan = (TMtId) new TMtId().fromJson(dualStreamSender);
		VConferenceManager.dualStreamMan = dualStreamMan;
		vConfInfoRefresh();
	} catch (JSONException e) {
		e.printStackTrace();
	}
	}

	*//**
		* 辅流状态通知
		* 
		* @param assSendSreamStatusNtf true:辅流发送状态通知,false:辅流接收状态通知
		* @param jsonBodyObj
		*/

	public static void parseAssSreamStatus(boolean assSendSreamStatusNtf, JSONObject jsonBodyObj) {
		if (null == jsonBodyObj || !jsonBodyObj.has("arrTAssVidStatus")) return;
		TAssVidStatus tAssVidStatus = null;
		try {
			String arrTAssVidStatus = jsonBodyObj.getString("arrTAssVidStatus");
			List<TAssVidStatus> assVidStatuslist = TAssVidStatus.createDeserializerGsonBuilder().create().fromJson(arrTAssVidStatus, new TypeToken<List<TAssVidStatus>>() {
			}.getType());

			if (null != assVidStatuslist && !assVidStatuslist.isEmpty()) {
				tAssVidStatus = assVidStatuslist.get(0);
			}
		} catch (Exception e) {
		}

		if (null != tAssVidStatus) {
			// 判断通道打开，而且有发送双流
			Boolean isDualStream = (EmMtChanState.emChanConnected.ordinal() == tAssVidStatus.emChanState.ordinal()) && tAssVidStatus.bActive;
			// 前后状态相同则退出，不处理,@wuhu发的这个通知太多。
			if (isDualStream == VConferenceManager.isDualStream) {
				return;
			} else {
				VConferenceManager.isDualStream = isDualStream;
			}

			if (VConferenceManager.isDualStream && EmNativeConfType.isAudio(VConferenceManager.nativeConfType)) {
				VConferenceManager.nativeConfType = EmNativeConfType.AUDIO_AND_DOAL;
			} else {
				if (VConferenceManager.currTMtCallLinkSate.isVConf() && EmNativeConfType.isAudioAndDoal(VConferenceManager.nativeConfType)) {
					VConferenceManager.nativeConfType = EmNativeConfType.AUDIO;
				}
			}

			// 接收双流
			if (!assSendSreamStatusNtf) {
				// final VConfAudioUI audioActivity = (VConfAudioUI) AppGlobal.getActivity(VConfAudioUI.class);
				final VConfVideoUI videoActivity = (VConfVideoUI) PcAppStackManager.Instance().getActivity(VConfVideoUI.class);
				// 音频播放界面 切换到双流
				// if (audioActivity != null) {
				// audioActivity.switchVConfFragment();
				// }
				// 视频播放界面 切换到双流
				if (videoActivity != null) {
					if (videoActivity.getVConfContentFrame() == null) {
						return;
					}
					videoActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							videoActivity.getVConfContentFrame().switchDualStreamCtrl(VConferenceManager.isDualStream);
						}
					});
				}
				if (VConferenceManager.isDualStream) {
				} else {
					// 取消双流
					VConferenceManager.dualStreamMan = null;
					// vConfInfoRefresh();
					// 音频播放界面
					// if (audioActivity != null) {
					// if (audioActivity.getVConfContentFrame().getBottomFunctionFragment() == null) {
					// return;
					// }
					// audioActivity.getVConfContentFrame().getBottomFunctionFragment().cleanSecondEncryption();
					// }

					// 视频播放界面
					if (videoActivity != null) {
						if (videoActivity.getVConfContentFrame().getBottomFunctionFragment() == null) {
							return;
						}
						// videoActivity.getVConfContentFrame().getBottomFunctionFragment().cleanSecondEncryption();
					}
				}
			}
		} else {
			// 发送双流(手机不支持)
		}
	}

	/**
	* 多点会议，设置本地终端信息
	* @param body 
	*/
	public static void setTerLable(String body) {
		try {
			TMtId terLable = (TMtId) new TMtId().fromJson(body);
			LabelAssign labelAssign = new LabelAssign();
			labelAssign.mcuNo = terLable.dwMcuId;
			labelAssign.terNo = terLable.dwTerId;
			VConferenceManager.mLabelAssign = labelAssign;
		} catch (Exception e) {
		}
	}

	/**
	* 会议设备信息
	* 
	* @param jsonBodyObj 
	*/

	public static void parseOnLineTerList(JSONObject jsonBodyObj) {
		if (null == jsonBodyObj || !jsonBodyObj.has("atMtEntitiy")) return;

		String arratEntityInfo;

		try {
			arratEntityInfo = jsonBodyObj.getString("atMtEntitiy");

			// 解析出会议终端
			final List<TMTEntityInfo> entityInfoList = TMTEntityInfo.createDeserializerGsonBuilder().create().fromJson(arratEntityInfo, new TypeToken<List<TMTEntityInfo>>() {
			}.getType());

			if (null == entityInfoList || entityInfoList.isEmpty()) {
				return;
			}

			VConferenceManager.addTMtInfoEx(entityInfoList, false);

			// 判断是否需要申请主席
			if (entityInfoList.size() == 1 && null != VConferenceManager.mLabelAssign && VConferenceManager.isCSMCC() && !VConferenceManager.isChairMan()
					&& entityInfoList.get(0).dwTerId == VConferenceManager.mLabelAssign.terNo) {
				Conference.applyChairman();
			}

		} catch (JSONException e) {
			Log.e("Test", "parseOnLineTerList", e);
		}
	}

	/**
		* 会议信息界面刷新 
		*/
	/*

	public static void vConfInfoRefresh() {
	VConfInfoUI vconfInfo = (VConfInfoUI) AppGlobal.getActivity(VConfInfoUI.class);
	if (vconfInfo != null) {
		vconfInfo.refresh();
	}
	}

	*//**
		* 终端信息结构
		* @param body 
		*/

	public static void setChairPos(String body) {
		TMTEntityInfo entityInfo = new TMTEntityInfo().fromJson(body);// 主席终端信息结构
		TMtId chair = VConferenceManager.getChairMan();
		if (chair != null) {
			chair.dwMcuId = entityInfo.dwMcuId;
			chair.dwTerId = entityInfo.dwTerId;
		}
		// vConfInfoRefresh();
	}

	/**
	* 申请主席返回消息处理
	*/

	public static void parseChairToken(boolean isSuccess) {
		// 设置本终端为主席终端
		TMtId chair = new TMtId(0, 0);
		if (isSuccess && null != VConferenceManager.mLabelAssign && null != VConferenceManager.mConfInfo) {
			chair.dwMcuId = VConferenceManager.mLabelAssign.mcuNo;
			chair.dwTerId = VConferenceManager.mLabelAssign.terNo;
			VConferenceManager.mConfInfo.tChairman = chair;

		} else if (null != VConferenceManager.mConfInfo) {
			VConferenceManager.mConfInfo.tChairman = chair; // 释放主席权限
		}
		// vConfInfoRefresh();
	}

	/**
	* 申请主讲返回消息处理
	*/
	public static void parseSeenByAll() {
		TMtId speaker = new TMtId();
		if (null != VConferenceManager.mLabelAssign && null != VConferenceManager.mConfInfo) {
			speaker.dwMcuId = VConferenceManager.mLabelAssign.mcuNo;
			speaker.dwTerId = VConferenceManager.mLabelAssign.terNo;

			VConferenceManager.mConfInfo.tSpeaker = speaker;

		}
	}

	/**
	* 当前看的视频源通知
	* @param body 
	*/
	/*

	public static void parseYouAreSing(String body) {
	TMTEntityInfo entityInfo = new TMTEntityInfo().fromJson(body);
	// // vconfInfo.refresh();

	}

	*//**
		* 设置静音
		* 
		* @param bQuiet 
		*/

	public static void parseCodecQuiet(boolean bQuiet) {
		VConfFunctionFragment vconfFunctionView = getVConfFunctionFragment();
		if (null != vconfFunctionView) {
			vconfFunctionView.setQuietImageView(bQuiet);
		}
	}

	/**
	* 设置哑音
	* 
	* @param bIsMute 
	*/

	public static void parseCodecMute(boolean bIsMute) {
		VConfFunctionFragment vconfFunctionView = getVConfFunctionFragment();
		if (null != vconfFunctionView) {
			vconfFunctionView.setMuteImageView(bIsMute);
		}
	}

	/**
	* 向本终端申请主席（本端为主席）
	* 
	*  {"mtapi":{"head":{"eventid":2074,"eventname":"ApplyChairNtf","SessionID": "1"},"body":{
	*    "dwMcuId" : 192,
	*    "dwTerId" : 1
	*  }
	*  }}
	* @param body 
	*/
	public static void applyChair(String body) {
		try {
			final Activity currActivity = PcAppStackManager.Instance().currentActivity();
			if (null == currActivity) return;

			TMtId tMtId = (TMtId) new TMtId().fromJson(body);
			if (null == tMtId) {
				return;
			}

			final TMTEntityInfo entityInfo = VConferenceManager.getMtInfoByTerId(tMtId.dwTerId);
			if (null != entityInfo && null != entityInfo.tMtAlias) {
				currActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ApplyDialog ap = new ApplyDialog(currActivity, entityInfo, true);
						ap.show();
					}
				});

			}

		} catch (Exception e) {
		}
	}

	/**
	* 向本终端申请发言（本端为主席）
	* @param body 
	*/

	public static void applySpeakPos(String body) {
		try {
			final Activity currActivity = PcAppStackManager.Instance().currentActivity();
			if (null == currActivity) return;

			TMtId tMtId = (TMtId) new TMtId().fromJson(body);
			if (null == tMtId) {
				return;
			}

			final TMTEntityInfo entityInfo = VConferenceManager.getMtInfoByTerId(tMtId.dwTerId);
			if (null != entityInfo && null != entityInfo.tMtAlias) {
				currActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ApplyDialog ap = new ApplyDialog(currActivity, entityInfo, false);
						ap.show();
					}
				});

			}
		} catch (Exception e) {
		}
	}

	/**
	* 终端加入会议
	* 
	* @param body 
	*/

	public static void pareseTerJoinVconf(String body) {
		TMTEntityInfo entityInfo = new TMTEntityInfo().fromJson(body);
		String alias = "";
		if (null != entityInfo && null != entityInfo.tMtAlias) {
			alias = entityInfo.tMtAlias.getAlias();
		}
		if (StringUtils.isNull(alias)) {
			return;
		}

		if (null != VConferenceManager.getTMtInfoList()) { // 将终端信息加入list中
			VConferenceManager.getTMtInfoList().add(entityInfo);
		}

	}

	/**
	* 终端退出会议
	* @param body 
	*/

	public static void pareseTerLeftVconf(String body) {
		TMtId mtId = (TMtId) new TMtId().fromJson(body);

		Activity currentActivity = PcAppStackManager.Instance().currentActivity();
		if (currentActivity == null) {
			return;
		}
		VConferenceManager.delTmtInfoByTerId(mtId.dwTerId);// 删除此终端
	}

	/**
	* 获取会议底部工具栏
	* @return 
	*/

	public static VConfFunctionFragment getVConfFunctionFragment() {
		Activity currActivity = PcAppStackManager.Instance().currentActivity();
		if (null == currActivity) {
			return null;
		}
		VConfFunctionFragment vconfFunctionView = null;
		if (currActivity instanceof VConfVideoUI && null != ((VConfVideoUI) currActivity).getVConfContentFrame()) {
			vconfFunctionView = ((VConfVideoUI) currActivity).getVConfContentFrame().getBottomFunctionFragment();
		}
		return vconfFunctionView;
	}

	/**
	* 刷新音视频下面的工具栏 
	*/

	private static void refreshBottomFragment() {
		VConfFunctionFragment vconfFunctionView = getVConfFunctionFragment();
		if (null != vconfFunctionView) {
			if (VConferenceManager.isChairMan()) {
				vconfFunctionView.removeReqChairmanHandler();
			}
			if (VConferenceManager.isSpeaker()) {
				vconfFunctionView.removeReqSpeakerHandler();
			}
			vconfFunctionView.updateOperationView();
		}
	}

	/**
	* 轮询信息
	* @param body 
	*/
	/*

	public static void parseTMtPollInfo(String body) {
	TMtPollInfo mPollInfo = new TMtPollInfo().fromJson(body);

	}

	*//**
		* CSU服务器配置
		* 
		* <pre>
		* {"mtapi":{"head":{"eventid":1066,"eventname":"SetCSUCfgNtf","SessionID": "1"},
		* 		"body":{
		* 			"achDomain" : "172.16.79.8",
		* 			"achNumber" : "009", 
		* 			"achPassword" : "",
		* 			"bUsedCSU" : true,
		* 			"dwIp" : 139399340
		* 		}
		* }}
		* 
		* @param jsonBodyObj 
		*/
	/*

	public static void parseCSUCfg(String body) {
	// TMtCSUAddrApi mCSUAddrApi = (TMtCSUAddrApi) new TMtCSUAddrApi().fromJson(body);

	}

	*//**
		* 获取会议列表
		* @param jsonBodyObj 
		*/

	public static void parseConfList(final JSONObject jsonBodyObj) {
		if (jsonBodyObj == null) {
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject assParamJsonObj = jsonBodyObj.getJSONObject(MyMtcCallback.KEY_AssParam);
					JSONObject mainParamJsonObj = jsonBodyObj.getJSONObject(MyMtcCallback.KEY_MainParam);
					int confListType = mainParamJsonObj.getInt("basetype");// 正在召开0、预约1、空闲2

					// 解析出会议列表信息
					List<VConf> vconfList = null;

					// 0 finish;other notFinish
					if (0 == assParamJsonObj.getInt("basetype")) {
						StringBuffer outBuf = new StringBuffer();// 存放会议信息

						Conference.getConfList(0, outBuf, 100, confListType);

//						System.out.println("outBuf:" + outBuf);

						String arrConfNameInfo = outBuf.toString();

						if (!StringUtils.isNull(arrConfNameInfo) && arrConfNameInfo.contains("arrConfNameInfo")) {
							JSONObject jsonObj = new JSONObject(arrConfNameInfo);
							arrConfNameInfo = jsonObj.getString("arrConfNameInfo");
							if (null != arrConfNameInfo) {
								vconfList = new GsonBuilder().create().fromJson(arrConfNameInfo, new TypeToken<List<VConf>>() {
								}.getType());
							}
						}
					}

					// 查询会议类型：正在召开、预约、空闲、全部
					// 目前查询会议是2种一起查询，查询顺序一次为：正在召开、预约
					if (confListType == EmConfListType.Hold.ordinal()) {
						VConferenceManager.resetVConf(vconfList, confListType);
					} else if (confListType == EmConfListType.Book.ordinal()) {
						VConferenceManager.addVConf(vconfList, confListType);
					} else if (confListType == EmConfListType.Tmplt.ordinal()) {
						VConferenceManager.addVConf(vconfList, confListType);
					} else if (confListType == EmConfListType.End.ordinal()) {
						VConferenceManager.resetVConf(vconfList, confListType);
					}

					if (confListType == EmConfListType.Book.ordinal()) {
						// 查询结束
						Activity ac = PcAppStackManager.Instance().currentActivity();
						if (ac != null && (ac instanceof VConfListActivity)) {
							((VConfListActivity) ac).showList();
						}
					}
					// SlidingMenuManager.refreshVConfView(EmConfListType.toEmConfListType(confListType));

					// 发送查询会议详情请求
					/*if (null != vconfList && !vconfList.isEmpty()) {
						for (VConf vConf : vconfList) {
							if (null == vConf || StringUtils.isNull(vConf.getAchConfE164())) continue;
							KdvMtConf.requestConfDetailInfo(vConf.getAchConfE164());
						}
					}*/
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
		* H323 查询在线终端列表
		* 
		* @param jsonBodyObj
		*/
	/*
	public static void parseOnLineTerListRsp(JSONObject jsonBodyObj) {
	try {
		if (EmModle.isH323(TruetouchApplication.getApplication().currLoginModle())) {
			return;
		}

	} catch (Exception e) {
		if (PcLog.isPrint) e.printStackTrace();
	}
	}

	*//**
		*  H323 在线终端列表通知
		*  
		* @param jsonBodyObj
		*/
	/*
	public static void parseOnLineTerListNtf(JSONObject jsonBodyObj) {
	try {

	} catch (Exception e) {
		if (PcLog.isPrint) e.printStackTrace();
	}
	}

	*//**
		* 解析音视频统计信息
		* @param jsonBodyObj 
		*/
	/*

	public static void parseCodecStatic(String body) {
	VConfFunctionFragment vconfFunctionView = getVConfFunctionFragment();
	if (null == vconfFunctionView) {
		return;
	}

	TMtCodecStatistic tMtCodecStatistic = (TMtCodecStatistic) new TMtCodecStatistic().fromJson(body);
	vconfFunctionView.showCodeStatusDetails(tMtCodecStatistic);
	}

	*//**
		* callmissed
		* @param body 
		*/
	/*

	public static void parseCallMissed(String body) {
	TMtMissCallParam tMtMissCallParam = (TMtMissCallParam) new TMtMissCallParam().fromJson(body);

	}

	*//**
		* 解析会议详情信息
		* @param jsonBodyObj 
		*/

	public static void parseConfDetailInfo(final JSONObject jsonBodyObj) {
		if (jsonBodyObj == null) {
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String assParamJsonObj = jsonBodyObj.getString(MyMtcCallback.KEY_AssParam);
					TMtConfDetailInfo tmtConfDetailInfo = (TMtConfDetailInfo) new TMtConfDetailInfo().fromJson(assParamJsonObj);
					if (null == tmtConfDetailInfo || null == tmtConfDetailInfo.tConfBaseInfo || StringUtils.isNull(tmtConfDetailInfo.tConfBaseInfo.achConfE164)) {
						return;
					}
					Activity ac = PcAppStackManager.Instance().currentActivity();
					if (ac != null && (ac instanceof VConfDetailsUI)) {
						((VConfDetailsUI) ac).showDetails(tmtConfDetailInfo);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	* 加入 会议创建会议成功或者失败
	* 
	* @param jsonBodyObj 
	* // EmMtJoinCreateConfRsp
	*/

	public static void paresJoinCreateConfResult(final JSONObject jsonBodyObj) {
		try {
			// 创会成功
			if (jsonBodyObj.has(MyMtcCallback.KEY_basetype) && 0 == jsonBodyObj.getInt(MyMtcCallback.KEY_basetype)) {
				return;
			}
			PcAppStackManager.Instance().currentActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						Toast.makeText(MyApplication.getContext(),
								"入会失败 EmMtJoinCreateConfRsp = " + jsonBodyObj.getString(MyMtcCallback.KEY_basetype),
								Toast.LENGTH_SHORT)
								.show();
					} catch (JSONException e) {
						// TODO 尚未处理异常
						e.printStackTrace();
					}
				}
			});

			VConferenceManager.quitConfAction(false, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	* 切换视频到音频
	* 
	* @param body 
	*/
	/*
	public static void parsePrimoVideoOff(String body) {
	if (true) {
		return;
	}
	// @wuhu PrimoVideoOff_Ntf 在会议结束通知之前过来，无法判断是 对端调的mainVideoOff接口，还是 自己挂断会议。
	new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			VConfVideoUI vconfVideo = (VConfVideoUI) AppGlobal.getActivity(VConfVideoUI.class);
			if (null != vconfVideo && null != VConferenceManager.currTMtCallLinkSate) {
				VConferenceManager.nativeConfType = EmNativeConfType.AUDIO;
				if (VideoCapServiceManager.getVideoCapServiceConnect() != null) {
					VideoCapServiceManager.getVideoCapServiceConnect().stopVideoCapture();
				}
				VideoCapServiceManager.unBindService();
				VConferenceManager.openVConfAudioUI(vconfVideo, VConferenceManager.isP2PVConf(), vconfVideo.getConfTitle(), vconfVideo.getE164());
				PcAppStackManager.Instance().popActivity(vconfVideo);
			}

		}
	}).start();
	}

	*//**
		* 会议15分钟提醒
		* 
		* {"mtapi":{"head":{"eventid":1039,"eventname":"ConfWillEndNtf","SessionID": "1"},"body":{
		* "basetype" : 15
		* }
		* }}
		* @param jsonBodyObj  
		*/
	/*
	public static void parseConfWillEndNtf(JSONObject jsonBodyObj) {
	if (null == jsonBodyObj) {
		return;
	}

	try {
		// 获取当前的时间 / 分钟
		final String remindTime = jsonBodyObj.getString("basetype");

		if (StringUtils.isNull(remindTime)) {
			return;
		}

		long finishTime = System.currentTimeMillis() + StringUtils.str2Long(remindTime, 0) * 60 * 1000;
		String fininshTimeStr = TimeUtils.formatMilliseconds(finishTime, TimeUtils.TIMEFORMAT_HM);
		if (StringUtils.isNull(fininshTimeStr)) {
			return;
		}

		if (!VConferenceManager.isChairMan()) {
			PcToastUtil.Instance().showCustomShortToast(TruetouchApplication.getContext().getString(R.string.vconf_delay_string_for_normal, fininshTimeStr, remindTime));
			return;
		}

		VConferenceManager.vconfWillEndDialog(TruetouchApplication.getApplication().getString(R.string.vconf_delay_string_for_normal, fininshTimeStr, remindTime));
	} catch (JSONException e) {
		if (PcLog.isPrint) e.printStackTrace();
	}
	}

	*//**
		* 会议延长通知, 单位为分钟
		* 
		* {"mtapi":{"head":{"eventid":2063,"eventname":"ConfDelayNtf","SessionID": "1"},"body":{
		* 		"basetype" : 1670821206
		* }
		* }}
		* 
		* @param jsonBodyObj   // 延长到
		*/
	/*
	public static void parseConfDelayNtf(JSONObject jsonBodyObj) {
	try {
		if (null == jsonBodyObj) {
			return;
		}

		int mins = 0;
		if (jsonBodyObj.has(MyMtcCallback.KEY_basetype)) {
			mins = jsonBodyObj.getInt(MyMtcCallback.KEY_basetype);
		}

		// 主席延长时间会收到ProlongResultNtf通知，提醒为成功/失败，
		if (VConferenceManager.isChairMan()) {
			return;
		}

		if (mins <= 0) {
			return;
		}

		PcToastUtil.Instance().showCustomLongToast(TruetouchApplication.getContext().getString(R.string.vconf_delay_string_arg1, mins));
	} catch (Exception e) {
	}
	}

	*//**
		* 会议密码框 
		* @param jsonBodyObj 
		*/
	/*

	public static void paresReqTerPwdNtf(JSONObject jsonBodyObj) {
	final PcActivity currActivity = (PcActivity) AppGlobal.getCurrPActivity();
	if (null == currActivity) {
		return;
	}
	VConferenceManager.mIsPopPwd = true;// 标记需要弹出密码框
	VConferenceManager.mIsPopPwdFirst = true;// 标记第一次进入视频界面，视频界面的密码框不弹出

	VConferenceManager.pupPwdDialog(currActivity);

	}
	*/
}
