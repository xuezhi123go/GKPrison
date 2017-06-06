package com.keda.vconf.controller;

/**
 * @(#)AVResponseActivity.java   2014-8-28
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.ui.activity.normal_activity.VideoFace;
import com.keda.sky.app.PcAppStackManager;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Conference;
import com.kedacom.kdv.mt.constant.EmConfProtocol;
import com.kedacom.kdv.mt.constant.EmNativeConfType;
import com.pc.utils.NetWorkUtils;

import java.io.IOException;

/**
  * 音视频应答
  * 
  * @author chenj
  * @date 2014-8-28
  */

public class VConfAVResponseUI extends ActionBarActivity implements View.OnClickListener {

	private static final int ACTIVITY_REQUEST_CODE = 1;
	private static final String TAG = "VConfAVResponseUI";

	private String mE164Num;
	// private String mPeerAlias;// 呼叫方名称

	private TextView mFlowTextView;

	private TextView mConnTextView;

	private TextView mPeerAliasTextView;

	private LinearLayout mAudioRspBtn;

	private LinearLayout mVideoRspBtn;

	// private LinearLayout mTelRspBtn;

	private boolean mIsAudioConf;// true 音频应答，false 视频应答
	private LinearLayout mRefusedLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PcAppStackManager.Instance().pushActivity(this);

		startAlarm();
		if (VConferenceManager.answerMode == 0) {//自动应答  --规避，手机不使用自动接听
			acceptVconfCall(true, false);
			setTheme(android.R.style.Theme_Translucent);
		} else {
			setContentView(R.layout.avresponse_layout);
			initExtras();
			onViewCreated();
			showP2PDetails();
		}
	}

	MediaPlayer mMediaPlayer;

	private void startAlarm() {
		mMediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());
		mMediaPlayer.setLooping(true);
		try {
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mMediaPlayer.start();
	}

	//获取系统默认铃声的Uri
	private Uri getSystemDefultRingtoneUri() {
		return RingtoneManager.getActualDefaultRingtoneUri(this,
				RingtoneManager.TYPE_RINGTONE);
	}

	public void initExtras() {

	}

	public void onViewCreated() {
		findViews();
		registerListeners();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

	public void findViews() {
		mFlowTextView = (TextView) findViewById(R.id.flow_remind);
		mConnTextView = (TextView) findViewById(R.id.joinVConf_waitingText);
		mPeerAliasTextView = (TextView) findViewById(R.id.peer_alias);
		mAudioRspBtn = (LinearLayout) findViewById(R.id.audio_response_btn);
		mVideoRspBtn = (LinearLayout) findViewById(R.id.video_response_btn);
		mRefusedLayout = (LinearLayout)findViewById(R.id.ll_refused);
		// mTelRspBtn = (LinearLayout) findViewById(R.id.tel_response_btn);

	}

	public void initComponentValue() {
		if (null == VConferenceManager.currTMtCallLinkSate) {
			finish();
		}
		mE164Num = VConferenceManager.mCallPeerE164Num;

		if (NetWorkUtils.isWiFi(this)) {
			mFlowTextView.setVisibility(View.INVISIBLE);
		}
		// 2G网络给出友好提示
		else if (NetWorkUtils.is2G(this)) {
			mVideoRspBtn.setVisibility(View.GONE);
			mAudioRspBtn.setVisibility(View.GONE);
			mFlowTextView.setVisibility(View.VISIBLE);
			mFlowTextView.setText(R.string.vconf_2g_unable_tel_join);
		} else {
			mFlowTextView.setVisibility(View.VISIBLE);
			if (VConferenceManager.isP2PVConf()) {// p2p
				mFlowTextView.setText(R.string.vconf_chooseJoinWay_3GInfo_normal);
			} else {// mcc
				mFlowTextView.setText(R.string.vconf_chooseJoinWay_3GInfo_mobile);
			}
		}

		// 判断码率是否音频码率（64），64的码率只能音频接听
		if ((null != VConferenceManager.currTMtCallLinkSate && VConferenceManager.currTMtCallLinkSate.isAudio())
				|| VConferenceManager.isAudioCallRate(VConferenceManager.confCallRete(getApplicationContext()))) {
			mVideoRspBtn.setVisibility(View.GONE);
			VConferenceManager.nativeConfType = EmNativeConfType.AUDIO;
			((TextView) findViewById(R.id.audio_response_txt)).setText(R.string.vconf_answer);
		} else {
			VConferenceManager.nativeConfType = EmNativeConfType.VIDEO;
		}

		// p2p
		if (VConferenceManager.isP2PVConf()) {
			// mTelRspBtn.setVisibility(View.GONE);
		} else {
			String alias = "";
			if (null != VConferenceManager.currTMtCallLinkSate && null != VConferenceManager.currTMtCallLinkSate.tPeerAlias) {
				alias = VConferenceManager.currTMtCallLinkSate.tPeerAlias.getAlias();
			}
			mPeerAliasTextView.setText(alias);
			mConnTextView.setText(R.string.vconf_join_waitingTxt_mul);
		}

	}

	public void registerListeners() {
		mVideoRspBtn.setOnClickListener(this);
		mAudioRspBtn.setOnClickListener(this);
		// mTelRspBtn.setOnClickListener(this);

		findViewById(R.id.refuse_response_btn).setOnClickListener(this);
	}

	/**
	 * P2P呼叫，对端头像、Name
	 */
	private void showP2PDetails() {
		if (null != VConferenceManager.currTMtCallLinkSate && null != VConferenceManager.currTMtCallLinkSate.tPeerAlias) {
			String alias = VConferenceManager.currTMtCallLinkSate.tPeerAlias.getAlias();
			mPeerAliasTextView.setText(alias);
		}

	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
            case R.id.video_response_btn:// 视频应答
			hintResponseBtn();
			//为了测试方便, 跳过人脸识别直接通话
			/*if (VConferenceManager.currTMtCallLinkSate != null && VConferenceManager.
					currTMtCallLinkSate.isCallIncoming() && !VConferenceManager.
					currTMtCallLinkSate.isAudio()) {
				acceptVconfCall(true, false);
				mConnTextView.setVisibility(View.VISIBLE);
				mFlowTextView.setVisibility(View.INVISIBLE);
				mIsAudioConf = false;
			}
			finish();*/
			// 正在发起呼叫状态 , 准备进行人脸识别
				showCheckIdDialog();
				break;

		// 音频应答,Note:视频会议可以音频应答
			case R.id.audio_response_btn:

				if (VConferenceManager.currTMtCallLinkSate != null && VConferenceManager.currTMtCallLinkSate.isCallIncoming()) {
					acceptVconfCall(true, true);
					mConnTextView.setVisibility(View.VISIBLE);
					mFlowTextView.setVisibility(View.INVISIBLE);
					mIsAudioConf = true;
					hintResponseBtn();
				}
				finish();
				break;

		case R.id.refuse_response_btn:// 拒绝呼叫
                hintResponseBtn();
                acceptVconfCall(false, false);
				finish();
				break;

			default:
				break;
		}

	}

	/**
	 * 提示即将进行人脸身份识别
	 */
	private void showCheckIdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("即将进行人脸身份识别，请注意周边环境，以防影响结果。");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
//				WarrantyTask task = new WarrantyTask();
//				task.execute();
                startActivityForResult(new Intent(VConfAVResponseUI.this, VideoFace.class), ACTIVITY_REQUEST_CODE);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == ACTIVITY_REQUEST_CODE){
			boolean verify = data.getBooleanExtra(VideoFace.CONFIDENCE_RESULT, false);
			double value = data.getDoubleExtra(VideoFace.CONFIDENCE_VALUE, 0);
			Log.i(TAG, "result : " + verify + ", value: " + value);
			if (verify){
				// 应答
				if (VConferenceManager.currTMtCallLinkSate != null && VConferenceManager.
						currTMtCallLinkSate.isCallIncoming() && !VConferenceManager.
						currTMtCallLinkSate.isAudio()) {
					acceptVconfCall(true, false);
					mConnTextView.setVisibility(View.VISIBLE);
					mFlowTextView.setVisibility(View.INVISIBLE);
					mIsAudioConf = false;

				}
                VConfAVResponseUI.this.finish();
			}
		}else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setTitle("提示");
			builder.setMessage("人脸身份验证失败!");
			builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					acceptVconfCall(false, false);
					VConfAVResponseUI.this.finish();
				}
			});
			builder.create().show();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PcAppStackManager.Instance().popActivity(this, false);
	}

	/**
	 * accept vconf
	  *
	  * @param bIsAccept
	  * @param audio
	 */
	private void acceptVconfCall(final boolean bIsAccept, final boolean audio) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (!bIsAccept) {
					Conference.rejectConf();
					return;
				}

				if (audio) {
					// 关闭第一路视频流
					Conference.mainVideoOff();

					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
				} else {
					int minCallRate = VConferenceManager.getCallRate();
					Conference.setCallCapPlusCmd(VConferenceManager.getSendResolutionByCallRate(minCallRate),
							VConferenceManager.getRecResolutionByCallRate(minCallRate),
							EmConfProtocol.em323.ordinal());
				}
				Conference.acceptConf();
			}
		}).start();
	}

	/**
	 * 禁用后退按键
	 */
	@Override
	public void onBackPressed() {
	}

	private void hintResponseBtn() {
		mVideoRspBtn.setVisibility(View.GONE);
		mAudioRspBtn.setVisibility(View.GONE);
		mRefusedLayout.setVisibility(View.GONE);
		// mTelRspBtn.setVisibility(View.GONE);
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				mMediaPlayer.release();
			}
		}
	}

	/** 
	 * @return 
	 * 是否是音频接听
	*/
	public boolean ismIsAudioConf() {
		return mIsAudioConf;
	}

}
