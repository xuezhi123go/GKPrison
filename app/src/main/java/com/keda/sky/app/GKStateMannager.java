/**
 * 
 */
package com.keda.sky.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.google.gson.Gson;
import com.kedacom.kdv.mt.api.Base;
import com.kedacom.kdv.mt.api.Configure;
import com.kedacom.kdv.mt.bean.TMtCSUAddrApi;
import com.kedacom.kdv.mt.bean.TMtTerminalNameApi;
import com.pc.utils.NetWorkUtils;
import com.pc.utils.StringUtils;

/**
 * GK登录状态管理机
 * 
 * @author chenj
 * @date 2014-12-9
 */
public class GKStateMannager {

	// 登录帐号(E164号)
	public static String mE164;
	public static String mAlias;
	// 登录密码
	public static String mPszPassword;

	// 服务器地址
	public static String mServerAddr;

	// 第一次注册GK
	public static boolean mIsFirstLogin = true;

	// 正在注册GK
	public static boolean mRegisteringGK;
	// gk注册是否已经成功
	public volatile static boolean mRegisterGK;

	static {
		mRegisteringGK = false;
		mRegisterGK = false;
	}

	private static GKStateMannager mGKStateMannager = new GKStateMannager();

	private GKStateMannager() {

	}

	public static GKStateMannager instance() {
		return mGKStateMannager;
	}

	/**
	 * 注册GK
	 * @param achE164
	 * @param gkIp
	 */
	public void registerGKFromH323(final String achE164, final String password, final String terlAlias) {
		if (mRegisteringGK || mRegisterGK) {
			return;
		}

		Log.i("Login flow", "GK DNS解析 H323 achE164: " + achE164 + " terlAlias:" + terlAlias);

		new Thread(new Runnable() {

			@Override
			public void run() {
				registerGK(achE164, password, "", 0, terlAlias);
			}
		}).start();
	}


	/**
	 * 存储GK注册信息
	 * 
	 * @param context
	 * @param userLogin
	 */
	public static void setRegisterGKBind(Context context, TMtCSUAddrApi mtCSUAddrApi) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString(TMtCSUAddrApi.class.getSimpleName(), new Gson().toJson(mtCSUAddrApi));
		editor.commit();
	}

	/**
	 * 获取GK注册信息
	 * 
	 * @param context
	 * @return
	 */
	public static TMtCSUAddrApi getRegisterGKFromSharedPreferences(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			return new Gson().fromJson(sp.getString(TMtCSUAddrApi.class.getSimpleName(), ""), TMtCSUAddrApi.class);
		} catch (Exception e) {
		}

		return null;
	}

	/**
	 * 注册GK
	 * 
	 * @param achE164
	 * @param gkIp
	 */
	public void registerGK(String achE164, String password, String domain, long gkIp, String terlAlias) {
		// ConfLibCtrl.confSetStackCmd(false, (short) EmConfProtocol.em323.ordinal()); // 走代理不用设协议栈
		boolean isH323 = KDInitUtil.isH323;
		// 设置别名
		TMtTerminalNameApi mtTerminalNameApi = new TMtTerminalNameApi();
		mtTerminalNameApi.achE164 = achE164;
		if (!StringUtils.isNull(terlAlias)) {
			mtTerminalNameApi.achTerlAlias = terlAlias;
		} else {
			mtTerminalNameApi.achTerlAlias = achE164;
			if (isH323) {
				mtTerminalNameApi.achTerlAlias = "Android" + achE164;
			}
		}
		Configure.setTerminalNameCfgCmd(mtTerminalNameApi);

		if (isH323) {
			Configure.setLoginPlatformSrvCfgCmd(false);
		} else {
			Configure.setLoginPlatformSrvCfgCmd(true);
		}

		if (null == domain) domain = "";
		if (null == password) password = "";

		TMtCSUAddrApi mtCSUAddrApi = new TMtCSUAddrApi();
		mtCSUAddrApi.bUsedCSU = true;
		mtCSUAddrApi.achDomain = domain;
		mtCSUAddrApi.achNumber = achE164;
		mtCSUAddrApi.achPassword = password;
		mtCSUAddrApi.dwIp = gkIp;

		if (NetWorkUtils.isAvailable(MyApplication.getContext())) {
			Log.i("Login flow", "GK 注册" + "  achDomain-" + domain + "  achNumber-" + achE164 + "  dwIp-" + gkIp);
			mRegisteringGK = true;
			mRegisterGK = false;
			Base.registerGK(mtCSUAddrApi);

			setRegisterGKBind(MyApplication.getContext(), mtCSUAddrApi);
			mE164 = achE164;
		} else {
			Log.i("Login flow", "网络不可用");
			restoreLoginState();
		}

	}

	/**
	 *  重新注册GK
	 */
	public void registerGK() {
		Log.i("registerGK", mRegisteringGK + "xx" + mRegisterGK);
		if (mRegisteringGK || mRegisterGK) {
			return;
		}

		if (!NetWorkUtils.isAvailable(MyApplication.getContext())) {
			restoreLoginState();
			return;
		}

		TMtCSUAddrApi mtCSUAddrApi = getRegisterGKFromSharedPreferences(MyApplication.getContext());
		if (null == mtCSUAddrApi) {
			restoreLoginState();
			return;
		}

		Log.i("Login flow", "GK 注册");
		mRegisteringGK = true;
		Base.registerGK(mtCSUAddrApi);
	}

	/**
	 * 注销GK
	 */
	public void unRegisterGK() {
		if (!NetWorkUtils.isAvailable(MyApplication.getContext())) {
			return;
		}

		TMtCSUAddrApi mtCSUAddrApi = new TMtCSUAddrApi();
		Log.i("Login flow", "GK 注销");

		mtCSUAddrApi.bUsedCSU = false;
		Base.registerGK(mtCSUAddrApi);
	}

	/**
	* 还原所有登录状态
	*/
	public static void restoreLoginState() {
		mRegisteringGK = false;
		mRegisterGK = false;
	}
}
