package com.keda.vconf.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gkzxhn.gkprison.R;
import com.keda.sky.app.GKStateMannager;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Conference;
import com.kedacom.kdv.mt.bean.TMtAddr;
import com.kedacom.kdv.mt.constant.EmMtAddrType;
import com.pc.utils.StringUtils;

public class InviteVConfDialog extends Dialog {

	private EditText mConfNumberEdit;
	private Button mCallButton;
	private Button mCancelButton;

	public InviteVConfDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("输入邀请E164号码");
		setContentView(R.layout.dialog_multicall);

		mCallButton = (Button) findViewById(R.id.btnCall);
		mCallButton.setText("邀请");
		mConfNumberEdit = (EditText) findViewById(R.id.number_conf);

		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		// 邀请
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

				TMtAddr tMtAddr = new TMtAddr(EmMtAddrType.emAddrE164, null, number);
				Conference.inviteTerCmd(tMtAddr);
				dismiss();
			}
		});
	}
}
