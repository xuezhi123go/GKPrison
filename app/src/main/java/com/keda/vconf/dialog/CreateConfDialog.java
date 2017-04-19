package com.keda.vconf.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.gkzxhn.gkprison.R;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.PcAppStackManager;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.bean.TMtAddr;
import com.pc.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateConfDialog extends Dialog {

	private EditText mConfNameET;//
	private EditText mRateET;
	private EditText mDurationET;

	public CreateConfDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("召集会议");
		setContentView(R.layout.dialog_create_conf);

		mConfNameET = (EditText) findViewById(R.id.conf_name);
		mConfNameET.setText(GKStateMannager.mE164 + "的会议");
		mConfNameET.setSelection(mConfNameET.getText().toString().length());
		mRateET = (EditText) findViewById(R.id.conf_rate);
		mDurationET = (EditText) findViewById(R.id.duration_time);

		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		// 呼叫
		findViewById(R.id.video_btnCall).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = mConfNameET.getText().toString();
				int rate, duration;
				if (StringUtils.isNull(name)) {
					return;
				}// 邀请终端列表
				if (StringUtils.isNull(mRateET.getText().toString())) {
					rate = 512;
				} else {
					rate = Integer.valueOf(mRateET.getText().toString());
				}
				if (StringUtils.isNull(mDurationET.getText().toString())) {
					duration = 512;
				} else {
					duration = Integer.valueOf(mDurationET.getText().toString());
				}
				List<TMtAddr> tMtList = new ArrayList<TMtAddr>();
				VConferenceManager.createVConf2OpenVideoUI(PcAppStackManager.Instance().currentActivity(), name, tMtList, rate, duration);
				dismiss();
			}
		});
		// 呼叫
		findViewById(R.id.audio_btnCall).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = mConfNameET.getText().toString();
				int rate, duration;
				if (StringUtils.isNull(name)) {
					return;
				}// 邀请终端列表
				if (StringUtils.isNull(mRateET.getText().toString())) {
					rate = 512;
				} else {
					rate = Integer.valueOf(mRateET.getText().toString());
				}
				if (StringUtils.isNull(mDurationET.getText().toString())) {
					duration = 512;
				} else {
					duration = Integer.valueOf(mDurationET.getText().toString());
				}
				List<TMtAddr> tMtList = new ArrayList<TMtAddr>();
				VConferenceManager.createVConf2OpenAudioUI(PcAppStackManager.Instance().currentActivity(), name, tMtList, rate, duration);
				dismiss();
			}
		});
	}
}
