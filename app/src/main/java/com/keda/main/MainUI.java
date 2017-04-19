/**
 * @(#)MainUI.java   2015-8-19
 * Copyright 2015  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.keda.sky.app.TruetouchGlobal;
import com.keda.sky.app.base.PcActivity;
import com.keda.vconf.dialog.CreateConfDialog;
import com.keda.vconf.dialog.MultiCallDialog;
import com.keda.vconf.dialog.P2PCallDialog;

/**
  * @author weiyunliang 
  * @date 2015-8-19
  */

public class MainUI extends PcActivity {

	private Button mConfContact;
	private Button mContact;
	private Button mConfList;
	private Button mMakeCall;
	private Button mMutiCall;
	private Button mCreateVconf;
	private Button mSetting;
	private Button mLogout;

	/**
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);
		onViewCreated();

	}

	@Override
	public void findViews() {
		mContact = (Button) findViewById(R.id.getContact);
		mConfContact = (Button) findViewById(R.id.getConfContact);
		mConfList = (Button) findViewById(R.id.getConfList);
		mMakeCall = (Button) findViewById(R.id.makeCall);
		mMutiCall = (Button) findViewById(R.id.mutiCall);
		mCreateVconf = (Button) findViewById(R.id.createVconf);
		mSetting = (Button) findViewById(R.id.setting);
		mLogout = (Button) findViewById(R.id.logout);
	}

	/**
	 */

	@Override
	public void initComponentValue() {
		if (KDInitUtil.isH323) {
			mContact.setVisibility(View.GONE);
			mConfContact.setVisibility(View.GONE);
			mConfList.setVisibility(View.GONE);
			mMakeCall.setVisibility(View.GONE);
			mCreateVconf.setVisibility(View.GONE);
		}
	}

	@Override
	public void registerListeners() {
		// 显示联系人
		mContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainUI.this, ContactListUI.class);
				startActivity(intent);
			}
		});
		// 搜索联系人
		mConfContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainUI.this, ContactSearchListUI.class);
				startActivity(intent);
			}
		});
		// 获取会议列表
		mConfList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainUI.this, VConfListActivity.class);
				startActivity(intent);
			}
		});
		// 呼叫点对点会议
		mMakeCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new P2PCallDialog(MainUI.this);
				dialog.show();
			}
		});
		// 呼叫多点会议
		mMutiCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new MultiCallDialog(MainUI.this);
				dialog.show();
			}
		});
		// 创建视频会议
		mCreateVconf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new CreateConfDialog(MainUI.this);
				dialog.show();
			}
		});
		// 设置
		mSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainUI.this, SettingsUI.class);
				startActivity(intent);
			}
		});
		// 注销
		mLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				TruetouchGlobal.logOff();
				finish();
			}
		});
	}

	/**
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}
}
