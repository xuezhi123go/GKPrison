/**
 * @(#)PcActivity.java 2014-5-16 Copyright 2014 it.kedacom.com, Inc. All rights
 *                     reserved.
 */

package com.keda.sky.app.base;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;

import com.keda.sky.app.PcAppStackManager;
import com.pc.utils.ActivityUtils;
import com.pc.utils.PcToastUtil;


/**
 * pc ActionBar Activity 
 * 
 * @author chenjian
 * @date 2014-5-16
 */

public abstract class PcActivity extends ActionBarActivity implements PcIActivity {

	private boolean isAvailable = true;

	/**
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isAvailable = true;
		PcAppStackManager.Instance().pushActivity(this);
	}

	/**
	 * @see android.support.v7.app.ActionBarActivity#setContentView(int)
	 */
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}

	/**
	 * initialize action bar
	 */
	protected void initActionBar() {

	}

	/**
	 * created View.建议在OnCreate()或在onPostCreate()中调用
	 */
	protected void onViewCreated() {
		findViews();
		initComponentValue();
		registerListeners();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	/**
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		isAvailable = true;

		super.onRestart();
	}

	/**
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		isAvailable = true;

		super.onResume();
	}

	/**
	 * @see android.support.v7.app.ActionBarActivity#onPostResume()
	 */
	@Override
	protected void onPostResume() {
		super.onPostResume();
	}

	@Override
	public boolean hasExtra(String pExtraKey) {
		Bundle b = getExtra();
		if (null == b) return false;

		return b.containsKey(pExtraKey);
	}

	@Override
	public String getAction() {
		Intent intent = getIntent();
		if (null == intent) {
			return "";
		}

		return intent.getAction();
	}

	@Override
	public Bundle getExtra() {
		Intent intent = getIntent();
		if (null == intent) {
			return null;
		}

		return intent.getExtras();
	}

	@Override
	public void initExtras() {

	}


	/**
	 * @see android.content.ContextWrapper#registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter)
	 */
	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		if (null == receiver) {
			return null;
		}

		return super.registerReceiver(receiver, filter);
	}

	/**
	 * @see android.content.ContextWrapper#unregisterReceiver(android.content.BroadcastReceiver)
	 */
	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {
		if (null == receiver) {
			return;
		}
		super.unregisterReceiver(receiver);
	}

	/**
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * @see android.support.v7.app.ActionBarActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();

		isAvailable = false;
	}

	/**
	 * @see com.pc.app.base.PcIBaseActivity#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return isAvailable;
	}

	/**
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		isAvailable = false;

		PcAppStackManager.Instance().popActivity(this, false);

		super.onDestroy();
	}

	/**
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		isAvailable = false;

		super.finish();
	}

	@Override
	public void onBackPressed() {
		isAvailable = false;

		super.onBackPressed();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public boolean isFinishing() {
		return super.isFinishing();
	}

	@Override
	public boolean isDestroyed() {
		return super.isDestroyed();
	}

	/**
	 * @see com.pc.app.base.PcIBaseActivity#onFinish()
	 */
	@Override
	public void onFinish() {
		finish(true);
	}

	/**
	 * @see com.pc.app.base.PcIActivity#finish(boolean)
	 */
	@Override
	public void finish(boolean checkSoftInput) {
		finish(checkSoftInput, -1, -1);
	}

	@Override
	public void finish(int enterAnim, int exitAnim) {
		finish(false, enterAnim, enterAnim);
	}

	/**
	 * @see com.pc.app.base.PcIActivity#finish(boolean, int, int)
	 */
	@Override
	public void finish(boolean checkSoftInput, int enterAnim, int exitAnim) {
		isAvailable = false;
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean moveTaskToBack(boolean nonRoot) {
		return super.moveTaskToBack(nonRoot);
	}


	@Override
	public void openActivity(Class<?> pClass) {
		ActivityUtils.openActivity(this, pClass);
	}

	@Override
	public void openActivity(Class<?> pClass, int requestCode) {
		ActivityUtils.openActivity(this, pClass, requestCode);
	}

	@Override
	public void openActivity(String pAction, int requestCode) {
		ActivityUtils.openActivity(this, pAction, requestCode);
	}

	@Override
	public void openActivity(Class<?> pClass, Bundle pBundle) {
		ActivityUtils.openActivity(this, pClass, pBundle);
	}

	@Override
	public void openActivity(String pAction, Bundle pBundle, int requestCode) {
		ActivityUtils.openActivity(this, pAction, pBundle, requestCode);
	}

	@Override
	public void openActivity(Class<?> pClass, int requestCode, int enterAnim, int exitAnim) {
		ActivityUtils.openActivity(this, pClass, requestCode, enterAnim, exitAnim);
	}

	@Override
	public void openActivity(Intent intent, int requestCode, int enterAnim, int exitAnim) {
		ActivityUtils.openActivity(this, intent, requestCode, enterAnim, exitAnim);
	}

	@Override
	public void openActivity(Class<?> pClass, Bundle pBundle, int requestCode, int enterAnim, int exitAnim) {
		ActivityUtils.openActivity(this, pClass, pBundle, requestCode, enterAnim, exitAnim);
	}

	@Override
	public void openActivity(Class<?> pClass, int enterAnim, int exitAnim) {
		ActivityUtils.openActivity(this, pClass, enterAnim, exitAnim);
	}

	@Override
	public void show(final String pMsg, final int duration) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				PcToastUtil.Instance().showCustomToast(pMsg, duration);
			}
		});
	}

	@Override
	public void showShortToast(final int pResId) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				PcToastUtil.Instance().showCustomShortToast(pResId);
			}
		});
	}

	@Override
	public void showLongToast(final int pResId) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				PcToastUtil.Instance().showCustomLongToast(pResId);
			}
		});
	}

	@Override
	public void showLongToast(final String pMsg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				PcToastUtil.Instance().showCustomLongToast(pMsg);
			}
		});
	}

	@Override
	public void showShortToast(final String pMsg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				PcToastUtil.Instance().showCustomShortToast(pMsg);
			}
		});
	}

	/**
	 * @see com.pc.app.base.PcIActivity#measureView(android.view.View)
	 */
	@Override
	public void measureView(View view) {
	}

}
