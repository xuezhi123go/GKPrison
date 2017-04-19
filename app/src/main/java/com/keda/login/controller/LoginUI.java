/**
 * @(#)LoginJoinVConfUI.java   2014-9-28
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.login.controller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.google.gson.Gson;
import com.keda.main.MainUI;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.LoginStateManager;
import com.keda.sky.app.PcAppStackManager;
import com.keda.sky.app.TruetouchGlobal;
import com.kedacom.kdv.mt.api.Configure;
import com.kedacom.kdv.mt.bean.TMtH323PxyCfg;
import com.kedacom.kdv.mt.constant.EmConfProtocol;
import com.pc.utils.DNSParseUtil;
import com.pc.utils.FormatTransfer;
import com.pc.utils.NetWorkUtils;
import com.pc.utils.StringUtils;

import java.net.InetAddress;
import java.util.Timer;

/**
  * 用户登录界面
  * 
  * @author chenj
  * @date 2014-9-28
  */

public class LoginUI extends ActionBarActivity {

	private EditText mServerAddrEdit;
	private EditText mAccountEdit;
	private EditText mPasswdEdit;
	private Button mLoginButton;
	private Button mLoginOffButton;
	private ToggleButton mIsH323Tog;
	private ProgressDialog mPDialog;
	private Timer mTimer;
	private String mAddr;
	private String mAccount;
	private String mPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PcAppStackManager.Instance().pushActivity(this);
		setContentView(R.layout.activity_login_layout);
		findViews();
		initComponentValue();
		registerListeners();
	}

	/**
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	/**
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	*/
	@Override
	protected void onResume() {
		super.onResume();
	}

	public void findViews() {
		mServerAddrEdit = (EditText) findViewById(R.id.addr_et);
		mAccountEdit = (EditText) findViewById(R.id.account_et);
		mPasswdEdit = (EditText) findViewById(R.id.pwd_et);
		mLoginButton = (Button) findViewById(R.id.login_btn);
		mLoginOffButton = (Button) findViewById(R.id.logOff_btn);
		mIsH323Tog = (ToggleButton) findViewById(R.id.isH323);
	}

	/**
	 */
	public void initComponentValue() {
		// 测试
		mServerAddrEdit.setText("ap-beta.kedacom.com");
//		mServerAddrEdit.setText("27.115.110.57");
		mAccountEdit.setText("0512121880141");
		mPasswdEdit.setText("111111");
		/*		mServerAddrEdit.setText("172.16.185.146");
				mAccountEdit.setText("0812110000392");
				mPasswdEdit.setText("888888");
		*/if (null != GKStateMannager.mE164) {
			mAccountEdit.setText(GKStateMannager.mE164);
		}
		if (null != GKStateMannager.mServerAddr) {
			mServerAddrEdit.setText(GKStateMannager.mServerAddr);
		}
		if (null != GKStateMannager.mPszPassword) {
			mPasswdEdit.setText(GKStateMannager.mPszPassword);
		}
		mServerAddrEdit.setSelection(mServerAddrEdit.getText().toString().length());
		mIsH323Tog.setChecked(KDInitUtil.isH323);//是否h323代理
	}

	public void registerListeners() {
		mLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mAddr = mServerAddrEdit.getText().toString();
				mAccount = mAccountEdit.getText().toString();
				mPassword = mPasswdEdit.getText().toString();
				if (TextUtils.isEmpty(mAddr) || TextUtils.isEmpty(mAccount)) {
					Toast.makeText(LoginUI.this, "登陆失败..", Toast.LENGTH_SHORT).show();
					// GKStateMannager.instance().unRegisterGK();
				} else {
					showDialog("正在登录...");
					login();
				}
			}
		});
		// 退出
		mLoginOffButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				TruetouchGlobal.logOut();
				mLoginOffButton.postDelayed(new Runnable() {

					@Override
					public void run() {
						PcAppStackManager.Instance().popAllActivity();
						PcAppStackManager.relaseActivityStack();

						// 退出程序
						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(0);
					}
				}, 1000);
			}
		});
		//H323代理
		mIsH323Tog.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//设置代理
				KDInitUtil.isH323 = isChecked;
			}
		});
	}

	protected void login() {
		if (!KDInitUtil.isH323) {
			Configure.setAudioPriorCfgCmd(false);
			if (isMtH323Local()) {
				// 取消代理，成功则 登陆aps
				setCancelH323PxyCfgCmd();
				return;
			}
			LoginStateManager.loginAps(mAccount, mPassword, mAddr);
		} else {
			Configure.setAudioPriorCfgCmd(true);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					String ip = DNSParseUtil.dnsParse(mAddr);
					// 解析成功，注册代理
					long dwIp = 0;
					try {
						dwIp = FormatTransfer.lBytesToLong(InetAddress.getByName(ip).getAddress());
					} catch (Exception e) {
						dwIp = FormatTransfer.reverseInt((int) NetWorkUtils.ip2int(ip));
					}
					long localH323Ip = getMtH323IpLocal();
					// 没有注册代理，或者 注册代理的ip 改变了
					if (localH323Ip == 0 || dwIp != localH323Ip) {
						setH323PxyCfgCmd(dwIp);
						return;
					}
					// 注册代理
					GKStateMannager.instance().registerGKFromH323(mAccount, mPassword, "");
				}
			}).start();
		}
	}

	/**
	 * 注册H323代理
	 */
	private void setH323PxyCfgCmd(final long dwIp) {
		Log.i("Login setting", "H323设置代理:" + dwIp);
		new Thread(new Runnable() {

			@Override
			public void run() {
				Configure.setH323PxyCfgCmd(true, true, dwIp);
				// 关闭并重新开启协议栈
				Configure.stackOnOff((short) EmConfProtocol.em323.ordinal());
			}
		}).start();
	}
	
	/**
	 * 检测本地 是否是代理 代理ip
	 * @return
	 */

	private long getMtH323IpLocal() {
		// 从数据库获取当前 是否注册了代理
		StringBuffer H323PxyStringBuf = new StringBuffer();
		Configure.getH323PxyCfg(H323PxyStringBuf);
		String h323Pxy = H323PxyStringBuf.toString();
		TMtH323PxyCfg tmtH323Pxy = new Gson().fromJson(h323Pxy, TMtH323PxyCfg.class);
		// { "achNumber" : "", "achPassword" : "", "bEnable" : true, "dwSrvIp" : 1917977712, "dwSrvPort" : 2776 }
		if (null != tmtH323Pxy && tmtH323Pxy.bEnable) {
			Log.i("Login", "tmtH323Pxy.dwSrvIp   " + tmtH323Pxy.dwSrvIp);
			return tmtH323Pxy.dwSrvIp;
		}
		return 0;
	}
	
	/**
	 * 检测本地 是否是代理
	 * @return
	 */

	private boolean isMtH323Local() {
		// 从数据库获取当前 是否注册了代理
		StringBuffer H323PxyStringBuf = new StringBuffer();
		Configure.getH323PxyCfg(H323PxyStringBuf);
		String h323Pxy = H323PxyStringBuf.toString();
		TMtH323PxyCfg tmtH323Pxy = new Gson().fromJson(h323Pxy, TMtH323PxyCfg.class);
		// { "achNumber" : "", "achPassword" : "", "bEnable" : true, "dwSrvIp" : 1917977712, "dwSrvPort" : 2776 }
		if (null != tmtH323Pxy) {
			Log.i("Login", "是否h323代理   " + tmtH323Pxy.bEnable);
			return tmtH323Pxy.bEnable;
		}
		return false;
	}
	
	/**
	 * 设置取消注册H323代理
	 */
	private void setCancelH323PxyCfgCmd() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 取消代理
				Configure.setH323PxyCfgCmd(false, false, 0);
				// 关闭并重新开启协议栈
				Configure.stackOnOff((short) EmConfProtocol.em323.ordinal());
			}
		}).start();
	}
	
	/**
	 * 登录成功/失败
	 *
	 * @param isSuccessed
	 */
	public void loginSuccessed(boolean isSuccessed, int failedMsgResouceId) {
		String failedMsg = "";
		if (failedMsgResouceId != 0) {
			try {
				failedMsg = getString(failedMsgResouceId);
			} catch (Exception e) {
				failedMsg = "";
			}
		}
		loginSuccessed(isSuccessed, failedMsg);
	}

	/**
	 * 登录成功/失败
	 *
	 * @param isSuccessed
	 */
	public void loginSuccessed(boolean isSuccessed, final String failedMsg) {
		dismissDialog();
		if (isSuccessed) {
			Intent intent = new Intent();
			intent.setClass(LoginUI.this, MainUI.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			return;
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (StringUtils.isNull(failedMsg)) {
					Toast.makeText(LoginUI.this, "登陆失败", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LoginUI.this, failedMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
	public void cancelTimer() {
		if (null == mTimer) return;

		mTimer.cancel();
		mTimer = null;
	}
	
	/**
	 *   等待进度条 
	 */
	private void showDialog(String msg) {
		dismissDialog();
		if (null == mPDialog) {
			mPDialog = new ProgressDialog(this);
		}
		mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mPDialog.setMessage(msg);
		mPDialog.setIndeterminate(false);
		mPDialog.setCancelable(true);
		mPDialog.setOnCancelListener(new OnCancelListener() {

			// 取消登录
			@Override
			public void onCancel(DialogInterface dialog) {
				dismissDialog();
				TruetouchGlobal.logOff();
			}
		});

		mPDialog.show();
	}

	private void dismissDialog() {
		if (null != mPDialog) {
			mPDialog.dismiss();
		}
	}

	/**
	 * 设置代理模式成功/失败
	 * @param isEnable true:设置代理可用
	 */
	public void setH323PxyCfgCmdResult(final boolean isEnable) {
		KDInitUtil.isH323 = isEnable;
		if (!isEnable) {
			Log.i("Login", "取消代理 -- 登录APS " + mAccount + "-" + mPassword);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(2 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					LoginStateManager.loginAps(mAccount, mPassword, mAddr);
				}
			}).start();
		} else {
			Log.i("Login", " 注册代理 -- 登录gk ");
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(2 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 注册代理
					GKStateMannager.instance().registerGKFromH323(mAccount, mPassword, "");
				}
			}).start();

			return;

		}
	}
	/**
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PcAppStackManager.Instance().popActivity(this, false);
	}

	/**
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
}
