package com.keda.sky.app;

import android.content.Context;

import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.kedacom.kdv.mt.api.Base;
import com.kedacom.kdv.mt.api.IM;

public final class TruetouchGlobal {

	// public final static String MTINFO_SKYWALKER = "TrueLink";
	public final static String MTINFO_SKYWALKER = "SKY for Android Phone";

	public final static String MTINFO_SKYWALKER_APS = "Android_Phone";

	public final static String MTINFO_SKYWALKER_DEVTYPE = "SKY for Android";

	// public final static String MTINFO_SKYWALKER = "SKY-X500-1080P60";

	public static String achJid;
	public static String achMoid;
	public static String achE164;
	public static String achEmail;
	public static String achXmppPwd;

	/**
	 * 返回Context 
	 *
	 * @return
	 */
	public static Context getContext() {
		return MyApplication.getContext();
	}

	/**
	 * 注销
	 */
	public static void logOff() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				GKStateMannager.instance().unRegisterGK();
				GKStateMannager.restoreLoginState();
				if (!KDInitUtil.isH323) {
					// 退出平台
					Base.logOutPlatformServerCmd();
					Base.logOutIMCmd(IM.imHandle);
					/*	// 退出登陆NMS服务器
					KdvMtConfig.LogoutNmsServerCmd();*/
					// 注销Sps Server
					Base.logoutApsServerCmd();
					LoginStateManager.restoreLoginState();
				}
			}
		}).start();

//		Intent intent = new Intent();
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.setClass(MyApplication.getContext(), LoginUI.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		MyApplication.getContext().startActivity(intent);
	}

	/**
	 * 退出
	 */
	public static void logOut() {

		GKStateMannager.instance().unRegisterGK();
		GKStateMannager.restoreLoginState();
		// 退出平台
		if (!KDInitUtil.isH323) {
			// 退出平台
			Base.logOutPlatformServerCmd();
			Base.logOutIMCmd(IM.imHandle);
			/*	// 退出登陆NMS服务器
			KdvMtConfig.LogoutNmsServerCmd();*/
			// 注销Sps Server
			Base.logoutApsServerCmd();
			LoginStateManager.restoreLoginState();
		}
		Base.mtStop();
	}
}
