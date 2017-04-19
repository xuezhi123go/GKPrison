/**
 * @(#)SettingsUI.java   2015-8-28
 * Copyright 2015  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.main;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gkzxhn.gkprison.R;
import com.google.gson.Gson;
import com.keda.sky.app.PcAppStackManager;
import com.keda.sky.app.base.PcActivity;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Configure;
import com.kedacom.kdv.mt.bean.TTcpUdpBasePortCfg;

/**
  * @author weiyunliang 
  * @date 2015-8-28
  */

public class SettingsUI extends PcActivity {

	private ToggleButton aesTog;
	private ToggleButton answerModeTog;
	private Spinner spinnerRate;
	private EditText tCPEditTex;
	private EditText uDPEditTex;
	private RadioGroup SettingRadioGroup;

	private final String[] rates = {
			"128", "512", "768", "1024", "2048", "4096", "8192"
	};

	/**
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PcAppStackManager.Instance().pushActivity(this);
		setContentView(R.layout.settings_layout);
		onViewCreated();

	}

	@Override
	public void findViews() {
		aesTog = (ToggleButton) findViewById(R.id.aesTog);
		answerModeTog = (ToggleButton) findViewById(R.id.ansTog);
		spinnerRate = (Spinner) findViewById(R.id.spinnerRate);
		tCPEditTex = (EditText) findViewById(R.id.tcp_et);
		uDPEditTex = (EditText) findViewById(R.id.udp_et);
		SettingRadioGroup = (RadioGroup) findViewById(R.id.setting_radio);
	}

	@Override
	public void initComponentValue() {
		aesTog.setChecked(VConferenceManager.isAES);
		answerModeTog.setChecked(VConferenceManager.answerMode == 0);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rates);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRate.setAdapter(adapter);
		spinnerRate.setSelection(1);

		//get TCP/UDP起始配置信息 
		StringBuffer sb = new StringBuffer();
		Configure.getPortCfg(sb);
		TTcpUdpBasePortCfg tcpUdpBasePortCfg = new Gson().fromJson(sb.toString(), TTcpUdpBasePortCfg.class);
		if (null != tcpUdpBasePortCfg) {
			if (tcpUdpBasePortCfg.bAuto) {
				SettingRadioGroup.check(R.id.auto);
			} else {
				SettingRadioGroup.check(R.id.customer);
				tCPEditTex.setText("" + tcpUdpBasePortCfg.wTcpBasePort);
				uDPEditTex.setText("" + tcpUdpBasePortCfg.wUdpBasePort);
			}
		}
	}

	@Override
	public void registerListeners() {
		aesTog.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				VConferenceManager.isAES = isChecked;
			}
		});

		answerModeTog.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					VConferenceManager.answerMode = 0;
				} else {
					VConferenceManager.answerMode = 1;
				}
				//				Configure.setAnswerMode(VConferenceManager.answerMode);// 设置应答模式 1,手动，0自动
			}
		});
		//添加Spinner事件监听器  
		spinnerRate.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
				VConferenceManager.callRate = Integer.valueOf(rates[pos]);//设置呼叫码率
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		// 自动设置，还是手动设置端口号
		SettingRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (R.id.auto == checkedId) {
					TTcpUdpBasePortCfg tcpUdpBasePortCfg = new TTcpUdpBasePortCfg(true, 0, 0);
					Configure.setPortCfgCmd(tcpUdpBasePortCfg);

				} else {
					TTcpUdpBasePortCfg tcpUdpBasePortCfg = new TTcpUdpBasePortCfg(false, Integer.valueOf(tCPEditTex.getText().toString()), Integer.valueOf(uDPEditTex.getText()
							.toString()));
					Configure.setPortCfgCmd(tcpUdpBasePortCfg);
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(SettingsUI.this, "请重启App", Toast.LENGTH_SHORT).show();
					}
				});

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
