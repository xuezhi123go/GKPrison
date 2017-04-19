/**
 * @(#)VConfDetails.java   2014-8-26
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.vconf.controller;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.keda.sky.app.PcAppStackManager;
import com.kedacom.kdv.mt.bean.TMtConfDetailInfo;
import com.kedacom.kdv.mt.constant.EmEncryptArithmetic;
import com.pc.utils.StringUtils;

/**
  * 会议详情
  * 
  * @author chenj
  * @date 2014-8-26
  */

public class VConfDetailsUI extends ActionBarActivity {

	private TextView achMasterMtAlias;
	private TextView achConfE164;
	private TextView emEncryptMode;
	private TextView mConfNameText;
	private TextView mShortNumText;
	private TextView dwBitrate;
	private TextView emConfResultion;
	private TextView mConfTimeText;
	private TextView bNeedPwd;
	private TextView mConfTimeLenText;
	private TextView mConfDefinitionText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PcAppStackManager.Instance().pushActivity(this);
		setContentView(R.layout.vconf_details_layout);
		findViews();
	}

	public void findViews() {
		achMasterMtAlias = (TextView) findViewById(R.id.achMasterMtAliasText);
		achConfE164 = (TextView) findViewById(R.id.achConfE164Text);
		emEncryptMode = (TextView) findViewById(R.id.emEncryptModeText);
		mConfNameText = (TextView) findViewById(R.id.confNameText);
		mShortNumText = (TextView) findViewById(R.id.shortNumText);
		dwBitrate = (TextView) findViewById(R.id.dwBitrateText);
		emConfResultion = (TextView) findViewById(R.id.emConfResultionText);
		mConfTimeText = (TextView) findViewById(R.id.confTimeText);
		bNeedPwd = (TextView) findViewById(R.id.bNeedPwdText);
		mConfTimeLenText = (TextView) findViewById(R.id.confTimeLenText);
		mConfDefinitionText = (TextView) findViewById(R.id.confDefinitionText);
	}

	/**
	 * @see com.kedacom.truetouch.sky.app.TTActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		// showDetails();
	}

	/**
	 * 显示详情
	 */
	public void showDetails(final TMtConfDetailInfo tmtConfDetailInfo) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				if (null != tmtConfDetailInfo.tConfBaseInfo) {

					// 会议名称
					if (!StringUtils.isNull(tmtConfDetailInfo.tConfBaseInfo.achConfName)) {
						mConfNameText.setText(tmtConfDetailInfo.tConfBaseInfo.achConfName);
					} else {
						findViewById(R.id.achMasterMtAlias).setVisibility(View.GONE);
					}

					// 会议e164
					if (!StringUtils.isNull(tmtConfDetailInfo.tConfBaseInfo.achConfE164)) {
						achConfE164.setText(tmtConfDetailInfo.tConfBaseInfo.achConfE164);
					} else {
						findViewById(R.id.achConfE164).setVisibility(View.GONE);
					}

				}

				// 加密模式
				if (null != tmtConfDetailInfo.emEncryptMode) {
					if (tmtConfDetailInfo.emEncryptMode == EmEncryptArithmetic.emAES) {
						emEncryptMode.setText("AES加密");
					} else if (tmtConfDetailInfo.emEncryptMode == EmEncryptArithmetic.emEncryptNone) {
						emEncryptMode.setText("未加密");
					}
				} else {
					findViewById(R.id.emEncryptMode).setVisibility(View.GONE);
				}
				// 会议码率
				dwBitrate.setText(tmtConfDetailInfo.dwBitrate + "K");

				// 会议模式
				if (null != tmtConfDetailInfo.emConfResultion) {
					emConfResultion.setText(tmtConfDetailInfo.emConfResultion.name());
				} else {
					findViewById(R.id.emConfResultion).setVisibility(View.GONE);
				}
				// 发起人
				if (!StringUtils.isNull(tmtConfDetailInfo.achMasterMtAlias)) {
					achMasterMtAlias.setText(tmtConfDetailInfo.achMasterMtAlias);
				} else {
					findViewById(R.id.achMasterMtAlias).setVisibility(View.GONE);
				}

				// 会议号码
				if (!StringUtils.isNull(tmtConfDetailInfo.achShortNo)) {
					mShortNumText.setText(tmtConfDetailInfo.achShortNo);
				} else {
					findViewById(R.id.shortNumFrame).setVisibility(View.GONE);
				}

				// 会议时间
				if (null != tmtConfDetailInfo.tStartTime) {
					mConfTimeText.setText(tmtConfDetailInfo.tStartTime.tMTTime2String());
				} else {
					findViewById(R.id.confTimeFrame).setVisibility(View.GONE);
				}

				// 会议时长
				if (tmtConfDetailInfo.dwDuration != 0) {
					mConfTimeLenText.setText(tmtConfDetailInfo.dwDuration + "");
				} else {
					findViewById(R.id.confTimeLenFrame).setVisibility(View.GONE);
				}

				// 是否密码会议
				if (tmtConfDetailInfo.bNeedPwd) {
					bNeedPwd.setText("密码会议");
				} else {
					bNeedPwd.setText("非密码会议");
				}

				// 清晰度
				if (null != tmtConfDetailInfo.emVidResolution) {
					mConfDefinitionText.setText(tmtConfDetailInfo.emVidResolution.name());
				} else {
					findViewById(R.id.confDefinitionFrame).setVisibility(View.GONE);
				}

			}
		});
	}

	/**
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */

	@Override
	protected void onDestroy() {
		PcAppStackManager.Instance().popActivity(this, false);
		super.onDestroy();
	}
}
