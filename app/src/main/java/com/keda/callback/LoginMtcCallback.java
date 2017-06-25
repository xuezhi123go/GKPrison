package com.keda.callback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.ui.activity.LoginActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.gkzxhn.gkprison.utils.CustomUtils.NimInitUtil;
import com.google.gson.Gson;
import com.keda.main.MainUI;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.LoginStateManager;
import com.keda.sky.app.PcAppStackManager;
import com.keda.sky.app.TruetouchGlobal;
import com.kedacom.kdv.mt.api.Base;
import com.kedacom.kdv.mt.api.Configure;
import com.kedacom.kdv.mt.api.IM;
import com.kedacom.kdv.mt.bean.TImUserLogin;
import com.kedacom.kdv.mt.bean.TMTLoginParam;
import com.kedacom.kdv.mt.bean.TMTWbParseKedaEntUser;
import com.kedacom.kdv.mt.bean.TRestErrorInfo;
import com.kedacom.kdv.mt.bean.UserInfoFromApsCfg;
import com.kedacom.kdv.mt.bean.XNUCfg;
import com.kedacom.kdv.mt.constant.EmRegFailedReason;
import com.pc.utils.DNSParseUtil;
import com.pc.utils.NetWorkUtils;
import com.pc.utils.StringUtils;

import org.json.JSONObject;

/**
 * Login mtc call back
 * 
 * @author chenj
 * @date 2014-12-22
 */
public class LoginMtcCallback {

	/**
	 * APS登录信息
	 * 
	 * <pre>
	 * 	APS成功切正确：bSucess=true && dwApsErroce==0 && dwHttpErrcode==200
	 * 
	 * <pre>
	 * {"mtapi":{"head":{"eventid":1352,"eventname":"ApsLoginResultNtf","SessionID": "1"},
	 * 		"body":{
	 *			"bSucess" : true,
	 *			"dwApsErroce" : 0,
	 *			"dwHttpErrcode" : 200
	 *		}
	 * }}
	 * @param jsonBodyObj
	 */
	public static void parseApsLoginResultNtf(String sessionId, JSONObject jsonBodyObj) {
		if (null == jsonBodyObj) {
			return;
		}

		int apsErr = -1;
		int httpErrcode = 0;
		boolean isSucess = false;
		try {
			// 这里的bSuccessed表示的是，这个请求是否发送成功了,并不代表的连接成功了
			if (jsonBodyObj.has("bSucess")) {
				isSucess = jsonBodyObj.getBoolean("bSucess");
			}
			if (jsonBodyObj.has("dwApsErroce")) {
				apsErr = jsonBodyObj.getInt("dwApsErroce");
			}
			if (jsonBodyObj.has("dwHttpErrcode")) {
				httpErrcode = jsonBodyObj.getInt("dwHttpErrcode");
			}
		} catch (Exception e) {
		}

		// APS登录失败
		if (httpErrcode != MyMtcCallback.HTTPCODE_SUCCESSID || !isSucess || apsErr != 0) {
			Log.e("Login", "Login Aps 失败");
			LoginStateManager.restoreLoginState();
			Base.logoutApsServerCmd();
			Activity currActivity = PcAppStackManager.Instance().currentActivity();
			if (currActivity instanceof LoginActivity){
				LoginActivity activity = (LoginActivity) currActivity;
				activity.loginSuccess(false,"APS登录失败:" + apsErr);
			}
			return;
		}

		Log.i("Login", "Login Aps 成功");

		// APS用户信息
		StringBuffer userInfoBuff = new StringBuffer();
		Configure.getUserInfoFromApsCfg(userInfoBuff);
		Log.e("Test", "APS用户信息GetUserInfoFromApsCfg : " + userInfoBuff.toString());
		final UserInfoFromApsCfg userInfoFromApsCfg = new Gson().fromJson(userInfoBuff.toString(), UserInfoFromApsCfg.class);

		if (null == userInfoFromApsCfg) {
			LoginStateManager.restoreLoginState();
			Base.logoutApsServerCmd();
			return;
		}else{
			TruetouchGlobal.achJid = userInfoFromApsCfg.achJid;
			TruetouchGlobal.achMoid = userInfoFromApsCfg.achMoid;
			TruetouchGlobal.achE164 = userInfoFromApsCfg.achE164;
			TruetouchGlobal.achEmail = userInfoFromApsCfg.achEmail;
			TruetouchGlobal.achXmppPwd = userInfoFromApsCfg.achXmppPwd;
		}

		// 注册gk
		if (!GKStateMannager.mRegisterGK) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// GK配置信息
					StringBuffer gkCfgBuff = new StringBuffer();
					Configure.getCSUCfg(gkCfgBuff);
					Log.e("Test", "GK配置信息GetCSUCfg： " + gkCfgBuff.toString());

					long gkIP = 0;
					String gkDomain = "";
					try {
						JSONObject gkCfgJObj = new JSONObject(gkCfgBuff.toString());
						gkIP = gkCfgJObj.getLong("dwIp");
						gkDomain = gkCfgJObj.getString("achDomain");
					} catch (Exception e) {
					}
					// 注意：注销GK之前先获取GK配置信息，否则配置可能会被更改
					// 先注销GK，保证注册GK信息是最新的
					GKStateMannager.instance().unRegisterGK();

					try {
						Thread.sleep(2 * 1000);
					} catch (Exception e) {
					}

					// 注册GK
					GKStateMannager.instance().registerGK(userInfoFromApsCfg.achE164, "", gkDomain, gkIP, "");
				}
			}).start();
		}

		// 登陆IM
		TImUserLogin userLogin = new TImUserLogin();
		userLogin.achNO = userInfoFromApsCfg.achJid;
		userLogin.achUserPwd = userInfoFromApsCfg.achXmppPwd;
		userLogin.byPwdLen = (short) userLogin.achUserPwd.length();
		userLogin.achDefaultSaveDir = KDInitUtil.getSaveDir();
		userLogin.achPicSaveDir = KDInitUtil.getPictureDir();
		userLogin.achConfigPath = MyApplication.getContext().getFilesDir().getAbsolutePath();

		// XMPP登录配置
		StringBuffer xmppBuff = new StringBuffer();
		Configure.getXNUCfg(xmppBuff);

		Log.e("Test", "XMPP登录配置GetXNUCfg : " + xmppBuff.toString());
		XNUCfg xnuCfg = new Gson().fromJson(xmppBuff.toString(), XNUCfg.class);
		if (null != xnuCfg) {
			userLogin.dwImIP = xnuCfg.dwIp;
			userLogin.wPort = xnuCfg.dwPort;
		}

		// ip为0，登录失败
		if (0 == userLogin.dwImIP) {
			LoginStateManager.restoreLoginState();
			Base.logoutApsServerCmd();
			return;
		}

		// 不在登录过程中
		if (!LoginStateManager.imLogining) {
			LoginStateManager.restoreLoginState();
			Base.logoutApsServerCmd();
			return;
		}

		// 登录IM
		userLogin.bFileShareEnable = true;
		LoginStateManager.loginIm(userLogin);

		// 用户权限 #TMtUserPrevilege
		/*StringBuffer userPrevilegeBuff = new StringBuffer();
		ConfigCtrl.GetUserPrevilegeCfg(userPrevilegeBuff);
		Log.e("Test", "用户权限GetUserPrevilegeCfg： " + userPrevilegeBuff.toString());
		try {
			if (null != userPrevilegeBuff && !StringUtils.isNull(userPrevilegeBuff.toString())) {
				TMtUserPrevilege userPrevilege = new Gson().fromJson(userPrevilegeBuff.toString(), TMtUserPrevilege.class);
				clientAccountInformation.putUserPrevilege(userPrevilege);
			}
		} catch (Exception e) {
		}*/

		// // 获取XAP服务器列表(其它APS(Xap)地址列表)
		// StringBuffer apsListBuff = new StringBuffer();
		// ConfigCtrl.GetAPSListCfg(apsListBuff);
		// try {
		// JSONObject apsListJObj = new JSONObject(apsListBuff.toString());
		// JSONArray apsListJArr = apsListJObj.getJSONArray("arrMtXAPSvr");
		// for (int i = 0; i < apsListJArr.length(); i++) {
		// JSONObject jobj = apsListJArr.getJSONObject(i);
		// if (null == jobj) continue;
		// }
		// } catch (Exception e) {
		// }

		// 平台配置信息
		StringBuffer platformBuff = new StringBuffer();
		Configure.getMtPlatformApiCfg(platformBuff);
		Log.e("Test", "平台配置信息GetMtPlatformApiCfg： " + platformBuff.toString());
		try {
			JSONObject platformJObj = new JSONObject(platformBuff.toString());
			long platformIP = platformJObj.getLong("dwIp");

			// 获取头像下载的Http
			TMTWbParseKedaEntUser.mPhotosHttp = DNSParseUtil.dnsParse(platformJObj.getString("achDomain"));
			TMTWbParseKedaEntUser.mWeiboHttp = DNSParseUtil.dnsParse(TMTWbParseKedaEntUser.OFFICIAL_WEBSITE);
			String platformDomain = NetWorkUtils.LongToIp(platformIP);
			// 会管登录
			LoginStateManager.getPlatformToken(platformDomain);
		} catch (Exception e1) {
		}

		/*	// 开始登录NMS
			StringBuffer nmsCfgBuff = new StringBuffer();
			ConfigCtrl.GetNMSCfg(nmsCfgBuff);
			Log.e("Test", "通过ConfigCtrl.GetNMSCfg()获取网管服务器地址： " + nmsCfgBuff.toString());
			LoginStateManager.loginNmsServerCmd(nmsCfgBuff);*/
	}

	/**
	 * GK状态处理
	 * 
	 * <pre>
	 * {"mtapi":{"head":{"eventid":2103,"eventname":"RegResultNtf","SessionID": "1"},"body":{
	 *		"AssParam" : {"basetype" : 100},
	 *		"MainParam" : {"basetype" : 1}
	 * }
	 * }}
	 * 
	 * @param jsonBodyObj
	 */
	public static void parseGKRegResultNtf(JSONObject jsonBodyObj) {

		if (null == jsonBodyObj) {
			return;
		}

		try {
			final int resultType = jsonBodyObj.getJSONObject(MyMtcCallback.KEY_AssParam).getInt(
					MyMtcCallback.KEY_basetype);
			boolean isSuccess = resultType == EmRegFailedReason.emRegSuccess.value;
			boolean isH323 = KDInitUtil.isH323;
			Activity currActivity = PcAppStackManager.Instance().currentActivity();
			// GK 注册成功
			if (isSuccess) {
				GKStateMannager.mRegisterGK = true;
				GKStateMannager.mRegisteringGK = false;
				Configure.setCallProtocolCfgCmd(1);
				Configure.setAnswerMode(1);// 设置应答模式
				Configure.setASymmetricNetCfgCmd(true);// 设置非对称网络
				Log.i("Login", "注册GK成功");
				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MyApplication.getContext(), "成功进入等待通话状态...", Toast.LENGTH_SHORT).show();
					}
				});

				Log.i("Login", "注册GK成功");
				if (currActivity instanceof LoginActivity){
					LoginActivity activity = (LoginActivity) currActivity;
					activity.loginSuccess(true,"");
				}
			} else {
				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						if (resultType != EmRegFailedReason.emUnRegSuc.value) {
							Toast.makeText(MyApplication.getContext(), "进入等待通话状态失败..." + resultType,
									Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
				GKStateMannager.restoreLoginState();
			}
			if (isH323) {
				StringBuffer terminalNameCfgBuff = new StringBuffer();
				Configure.GetTerminalNameCfg(terminalNameCfgBuff);

				Log.e("Test", "终端名称 terminalNameCfgBuff： " + terminalNameCfgBuff.toString());

				if (isSuccess && null != terminalNameCfgBuff && !StringUtils.isNull(terminalNameCfgBuff.toString())) {
					JSONObject terminalNameCfgJsonObject = new JSONObject(terminalNameCfgBuff.toString());
				}

				if (isSuccess) {
					Configure.setASymmetricNetCfgCmd(true);
					Log.i("Login", "H323 注册GK成功");
				} else {
					Log.i("Login", "H323 注册GK失败" + resultType);
					if (currActivity instanceof LoginActivity) {
						if (resultType != EmRegFailedReason.emUnRegSuc.value) {// 不用多次注销
							LoginActivity activity = (LoginActivity) currActivity;
							activity.loginSuccess(false,"错误码" + resultType);
							// 注销gk，不然会一直注册
							GKStateMannager.instance().unRegisterGK();
						}
					} else {
						if (resultType == EmRegFailedReason.emRegNumberFull.value
								|| resultType == EmRegFailedReason.emGKSecurityDenial.value) {
							GKStateMannager.instance().unRegisterGK();
//							GKStateMannager.pupLogFailed2LoginUI("",
//									"错误码" + resultType);
						} else {
							// GK被抢登
							if (resultType == EmRegFailedReason.emUnRegGKReq.value) {
								GKStateMannager.instance().unRegisterGK();
							} else if (!MyMtcCallback.getInstance().stopHanldeJni
									&& null != PcAppStackManager.Instance().getActivity(MainUI.class)
									&& resultType != EmRegFailedReason.emUnRegSuc.value) { // 如果是logout，或者在launcher界面，不注册gk
								GKStateMannager.instance().registerGK();
							}
						}
					}
				}
			} else {
				// 修改自己的状态
				LoginStateManager.imModifySelfStateReq();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Im登录消息
	 * 
	 * <pre>
	 * "body":{
	 * 		"dwErrID" : 0,
	 * 		"dwHandle" : 1612146776,
	 * 		"dwReserved" : 0
	 * }
	 * @param jsonBodyObj
	 */
	public static void parseImLoginRsp(JSONObject jsonBodyObj) {
		if (null == jsonBodyObj) {
			return;
		}

		try {
			int errId = -1;
			if (jsonBodyObj.has(MyMtcCallback.KEY_dwErrID)) {
				errId = jsonBodyObj.getInt(MyMtcCallback.KEY_dwErrID);
			}
			int preImHandle = IM.imHandle;
			if (preImHandle == 0) {
				SharedPreferences mPreferences = MyApplication.getContext().getSharedPreferences("com.kedacom.truetouch_preferences", Context.MODE_PRIVATE);
				preImHandle = mPreferences.getInt("imHandle", 0);
			}

			if (jsonBodyObj.has(MyMtcCallback.KEY_dwHandle)) {
				IM.imHandle = jsonBodyObj.getInt(MyMtcCallback.KEY_dwHandle);
				if (IM.imHandle != 0) {
					SharedPreferences mPreferences = MyApplication.getContext().getSharedPreferences("com.kedacom.truetouch_preferences", Context.MODE_PRIVATE);
					Editor mEditor = mPreferences.edit();
					mEditor.putInt("imHandle", IM.imHandle);
					mEditor.commit();
				}
			}

			// 登录成功
			if (0 == errId) {
				Log.i("Login", "LoginIm成功");

				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MyApplication.getContext(), "登陆Im成功...", Toast.LENGTH_SHORT).show();
					}
				});
				// 设置传输文件缓存路径,此处必须设置
				Configure.imSetTempPathCmd(IM.imHandle, KDInitUtil.getTmpDir());
				LoginStateManager.imLogin = true;
				LoginStateManager.imLogining = false;

				/*Activity currActivity = PcAppStackManager.Instance().currentActivity();
				if (currActivity instanceof LoginUI) {
					((LoginUI) currActivity).loginSuccessed(true, "");
				}*/

			} else {
				if (errId == 2003 || errId == 65) {
					Base.logOutIMCmd(IM.imHandle);
				}
				LoginStateManager.restoreLoginState();
				Base.logoutApsServerCmd();
				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MyApplication.getContext(), "登陆Im失败...", Toast.LENGTH_SHORT).show();
					}
				});
				Log.i("Login", "LoginIm失败");
				Activity currActivity = PcAppStackManager.Instance().currentActivity();
				if (!(currActivity instanceof LoginActivity)) {
					 GKStateMannager.instance().registerGK();
				} else {
					LoginActivity activity = (LoginActivity) currActivity;
					activity.loginSuccess(false, "");
				}
			}

			// 修改自己的状态
			LoginStateManager.imModifySelfStateReq();
		} catch (Exception e) {
			Log.e("Test", "parseImLoginRsp", e);
		}
	}

	/**
	 * 获取平台Token登录信息
	 * 
	 * <pre>
	 * {"mtapi":{"head":{"eventid":4071,"eventname":"RestGetPlatformAccountTokenRsp","SessionID": "1"},
	 * 		"body":{
	 * 			"achErrorInfo" : "",
	 * 			"adwParams" : [ 0, 0, 0, 0 ],
	 * 			"dwErrorID" : 1000,
	 * 			"dwNackEventId" : 0,
	 * 			"emApiType" : 0
	 * 		}
	 *  }}
	 * 
	 * @param jsonBodyObj
	 */
	public static void parseRestGetPlatformAccountTokenRsp(String jsonBodyObj) {
		if (null == jsonBodyObj) {
			return;
		}

		try {
			TRestErrorInfo entityInfos = new TRestErrorInfo().fromJson(jsonBodyObj);
			// 获取Token失败
			if (entityInfos.dwErrorID != MyMtcCallback.PLATFORMAPI_SUCCESSID || StringUtils.isNull(LoginStateManager.mAccount) || StringUtils.isNull(LoginStateManager.mPassword)) {
				return;
			}
			loginPlatForm(LoginStateManager.mAccount, LoginStateManager.mPassword);
		} catch (Exception e) {
			Log.e("Test", "parseRestGetPlatformAccountTokenRsp", e);
		}
	}

	/**
	 * 登录平台
	 * 
	 * @param account
	 * @param passwd
	 */
	public synchronized static void loginPlatForm(String account, String passwd) {

		Log.i("Login", "登录平台...");
		TMTLoginParam mLoginPlatformJson = new TMTLoginParam(account, passwd);
		Base.loginPlatformServerReq(mLoginPlatformJson);
	}

// 2016.7.5 add
	/**
	 * 设置H323代理模式
	 * 
	 * <pre>
	 * 	{"mtapi":{"head":{"eventid":1223,"eventname":"SetH323PxyCfgNtf","SessionID": "1"},"body":{
	 * 	"achNumber" : "",
	 * 	"achPassword" : "",
	 * 	"bEnable" : true,
	 * 	"dwSrvIp" : 16777343,
	 * 	"dwSrvPort" : 2776
	 * }
	 * }}
	 * 
	 * @param sessionId
	 * @param jsonBodyObj
	 */
	public static void parseSetH323PxyCfgNtf(String sessionId, JSONObject jsonBodyObj) {
		try {
			long srvIp = jsonBodyObj.getLong("dwSrvIp");
			boolean isEnable = jsonBodyObj.getBoolean("bEnable");
			Activity currActivity = PcAppStackManager.Instance().currentActivity();
			if (currActivity instanceof LoginActivity) {
				NimInitUtil.setH323PxyCfgCmdResult(isEnable);
			}
		} catch (Exception e) {
		}
	}

}
