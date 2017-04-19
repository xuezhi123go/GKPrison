/**
 * @(#)Context.java   2014-8-15
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.keda.sky.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.keda.callback.MyMtcCallback;
import com.keda.vconf.manager.VConferenceManager;
import com.kedacom.kdv.mt.api.Base;
import com.kedacom.kdv.mt.api.IM;
import com.kedacom.kdv.mt.bean.TImUserLogin;
import com.kedacom.kdv.mt.bean.TMTApsLoginParam;
import com.kedacom.kdv.mt.bean.TMtNMAAddr;
import com.kedacom.kdv.mt.bean.TMtXAPSvr;
import com.kedacom.kdv.mt.bean.TMtXAPSvrList;
import com.kedacom.kdv.mt.constant.EmServerAddrType;
import com.pc.utils.DNSParseUtil;
import com.pc.utils.FormatTransfer;
import com.pc.utils.NetWorkUtils;
import com.pc.utils.StringUtils;
import com.pc.utils.TerminalUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

/**
  * 登录状态管理机
  * 
  * @author chenj
  * @date 2014-8-15
  */

public class LoginStateManager {

	public static final int APS_ERR_ACCOUNT_NAME_NOTEXIST = 22001; // 用户不存在
	public static final int APS_ERR_ACCOUNT_PWD_ERROR = 22002; // 用户密码错误
	public static final int APS_ERR_NO_ROAM_PRIVILEGE = 22006; // 没有漫游权限

	public static String mAccount;
	public static String mPassword;

	public static boolean imLogin = false;
	public static boolean imLogining = false;

	/**
	 * 本端ip
	 * 
	 * @param context
	 * @return
	 */
	public static long getTerminalIp(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getLong("DevIp", 0);
	}

	/**
	 * 登录ASP
	 * 
	 * <pre>
	 * 	http://172.16.179.3:60080/v5/aps/login?Version=2.6.0.1&UserName=0512800960000&PassWord=21218cca77804d2ba1922c33e0151105&DeviceType=TrueLink&ApsLocalIp=172.16.179.3
	 * 
	 * @param account
	 * @param pwd
	 */
	public synchronized static void loginAps(final String account, final String pwd, final String serverAddr) {
		if (KDInitUtil.isH323) {
			return;
		}
		if (imLogin || imLogining) {
			Log.e("Login", "当前登录状态处于登录中或已登录成功 LoginStateMannager.loginAps()返回");
			return;
		}
		Log.i("Login", "登录APS " + account + "-" + pwd + "-" + serverAddr);
		new Thread(new Runnable() {

			@Override
			public void run() {

				MyMtcCallback.getInstance().stopHanldeJni = false;

				final TMtXAPSvrList xapSvrList = new TMtXAPSvrList();
				xapSvrList.bAutoSelect = false;
				xapSvrList.byCurIndex = 0;
				xapSvrList.byCnt = 1;

				// mtXAPSvr.achDomain = "172.16.176.214";

				final TMtXAPSvr mtXAPSvr = new TMtXAPSvr();
				mtXAPSvr.emAddrType = EmServerAddrType.emSrvAddrTypeCustom;
				xapSvrList.arrMtXAPSvr = new TMtXAPSvr[] {
					mtXAPSvr
				};

				mtXAPSvr.achDomain = serverAddr;

				// 地址为空
				if (StringUtils.isNull(mtXAPSvr.achDomain)) {
					Log.e("Login", "地址为空 LoginStateMannager.loginAps()");
					return;
				}

				String ip = DNSParseUtil.dnsParse(mtXAPSvr.achDomain);

				// 域名解析失败
				if (StringUtils.isNull(ip)) {
					Log.e("Login", "域名解析失败 LoginStateMannager.loginAps()");
					return;
				}
				// 解析成功，登录APS
				try {
					mtXAPSvr.dwIp = FormatTransfer.lBytesToLong(InetAddress.getByName(ip).getAddress());
				} catch (Exception e) {
					mtXAPSvr.dwIp = FormatTransfer.reverseInt((int) NetWorkUtils.ip2int(ip));
				}
				Base.setAPSListCfgCmd(xapSvrList);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}

				Log.i("Login", "LoginStateMannager.loginAps()...");

				final TMTApsLoginParam apsLoginParam = new TMTApsLoginParam();
				apsLoginParam.achPassword = pwd;
				apsLoginParam.achUsername = account;
				apsLoginParam.achSoftwareVer = TerminalUtils.versionName(MyApplication.getContext(), "5.0.0.0");
				apsLoginParam.achModelName = TruetouchGlobal.MTINFO_SKYWALKER_APS;

				imLogining = true;
				imLogin = false;
				Base.loginApsServerCmd(apsLoginParam);

				LoginStateManager.mAccount = account;
				LoginStateManager.mPassword = pwd;
			}
		}).start();
	}

	/**
	 * 登录IM
	 * 
	 * @param userLogin
	 */
	public synchronized static void loginIm(TImUserLogin userLogin) {

		Log.i("Login", "LoginIm...");

		imLogining = true;
		imLogin = false;
		Base.loginIm(userLogin);
	}

	/**
	* 还原所有登录状态
	*/
	public static void restoreLoginState() {
		imLogining = false;
		imLogin = false;
	}

	/**
		* 获取平台Token
		* 
		* @param platformIp
		*/

	public synchronized static void getPlatformToken(String platformIp) {

		Log.i("Login", "登录平台 获取Token");
		Base.mgRestGetMeetingAccountTokenReq(platformIp);
	}

	/**
	 * 登陆NMS服务器请求命令
	 * {
	 *    "achDomain" : "172.16.185.160",
	 *    "dwIp" : 0
	 * }
	 */
	public static void loginNmsServerCmd(StringBuffer nmsCfgBuff) {
		if (null == nmsCfgBuff) {
			return;
		}

		try {
			JSONObject nmsCfgBuffJObj = new JSONObject(nmsCfgBuff.toString());
			String achDomain = nmsCfgBuffJObj.getString("achDomain");
			long dwIp = nmsCfgBuffJObj.getLong("dwIp");
			TMtNMAAddr tNetAddr = new TMtNMAAddr();
			tNetAddr.dwIp = dwIp;
			tNetAddr.achDomain = achDomain;
			Base.loginNmsServerCmd(tNetAddr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改自己的状态请求
	 * @return 
	 */

	public static int imModifySelfStateReq() {
		return IM.imModifySelfStateReq(imLogin, GKStateMannager.mRegisterGK, VConferenceManager.isCSVConf(),
				TruetouchGlobal.achJid);
	}
}
