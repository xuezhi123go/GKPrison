package com.keda.vconf.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.keda.sky.app.PcAppStackManager;
import com.keda.vconf.manager.VConferenceManager;
import com.pc.utils.StringUtils;
import com.pc.utils.ValidateUtils;

public class MultiCallDialog extends Dialog {

	private EditText mConfNumberEdit;
	private Button mCallButton;
	private Button mCancelButton;
	private String mConfNumber;

	public MultiCallDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MultiCallDialog(Context context, String mConfNumber) {
		super(context);
		this.mConfNumber = mConfNumber;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setTitle("输入会议号码");
		setContentView(R.layout.dialog_multicall);

		mConfNumberEdit = (EditText) findViewById(R.id.number_conf);
		if (null != mConfNumber) {
			mConfNumberEdit.setText(mConfNumber);
		}

		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		// 呼叫
		findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = mConfNumberEdit.getText().toString();
				if (StringUtils.isNull(number)) {
					return;
				}

				if (!VConferenceManager.isAvailableVCconf(true, true, true)) {
					dismiss();
					return;
				}
				Activity currActivity = PcAppStackManager.Instance().currentActivity();
				if (ValidateUtils.isIP(number) || KDInitUtil.isH323) {
					VConferenceManager.openVConfVideoUI(currActivity, true, number, number);
				} else {
					VConferenceManager.openVConfVideoUI(currActivity, false, number, number);
				}
				dismiss();
			}
		});
	}

}
