package com.keda.vconf.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gkzxhn.gkprison.R;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.PcAppStackManager;
import com.keda.vconf.manager.VConferenceManager;
import com.pc.utils.StringUtils;
import com.pc.utils.ValidateUtils;

public class P2PCallDialog extends Dialog {

	private EditText mConfNumberEdit;
	private Button mCallButton;
	private Button mCancelButton;
	private String e164;

	public P2PCallDialog(Context context) {
		super(context);
	}

	public P2PCallDialog(Context context, String e164) {
		super(context);
		this.e164 = e164;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("输入对端E164号码");
		setContentView(R.layout.dialog_multicall);

		mConfNumberEdit = (EditText) findViewById(R.id.number_conf);

		if (!StringUtils.isNull(e164)) {
			mConfNumberEdit.setText(e164);
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
				String number = mConfNumberEdit.getText().toString().trim();
				if (StringUtils.isNull(number) || number.equals(GKStateMannager.mE164)) {
					return;
				}

				if (!VConferenceManager.isAvailableVCconf(true, true, true)) {
					dismiss();
					return;
				}
				Activity currActivity = PcAppStackManager.Instance().currentActivity();
				if (ValidateUtils.isIP(number)) {
					VConferenceManager.openVConfVideoUI(currActivity, true, number, number);
				} else {
					VConferenceManager.openVConfVideoUI(currActivity, true, number, number);
				}
				dismiss();
			}
		});
	}

}
