package com.gkzxhn.gkprison.presenter.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.blankj.utilcode.utils.RegexUtils;
import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.PerActivity;
import com.gkzxhn.gkprison.constant.Config;
import com.gkzxhn.gkprison.dagger.contract.LoginContract;
import com.gkzxhn.gkprison.model.net.api.LoginService;
import com.gkzxhn.gkprison.model.net.api.wrap.LoginWrap;
import com.gkzxhn.gkprison.model.net.bean.UserInfo;
import com.gkzxhn.gkprison.ui.activity.LoginActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.gkzxhn.gkprison.utils.CustomUtils.OkHttpUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.Log;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.google.gson.Gson;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.LoginStateManager;
import com.kedacom.kdv.mt.api.Configure;
import com.kedacom.kdv.mt.bean.TMtH323PxyCfg;
import com.kedacom.kdv.mt.constant.EmConfProtocol;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.pc.utils.DNSParseUtil;
import com.pc.utils.FormatTransfer;
import com.pc.utils.NetWorkUtils;

import java.io.IOException;
import java.net.InetAddress;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Subscription;

import static com.gkzxhn.gkprison.constant.Config.mAccount;
import static com.gkzxhn.gkprison.constant.Config.mAddr;
import static com.gkzxhn.gkprison.constant.Config.mPassword;

/**
 * Author: Huang ZN
 * Date: 2016/12/22
 * Email:943852572@qq.com
 * Description:登录页面所有逻辑操作类
 */
@PerActivity
public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getName();
    private LoginContract.View loginContractView;
    private Context mContext;

    private LoginService loginService;
    private Subscription mVerifySubscription;
    private Subscription mLoginPersonSubscription;

    private UserInfo userInfo;// 个人用户登录成功返回的个人信息
    private boolean isCommonUser = true; // 是否普通用户  默认true

    @Inject
    public LoginPresenter(LoginService loginService, Context context){
        this.mContext = context;
        this.loginService = loginService;
    }

    private Subscription mCheckSubscription;

    @Override
    public void login(final boolean isCommonUser, final String str, String json_str) {
        android.util.Log.i(TAG, "login: ------ " + str);
        loginWithRightCode(isCommonUser, str);
        /*mCheckSubscription = LoginWrap.getInstance().checkVerifyCode(loginService, OkHttpUtils.getRequestBody(json_str), new SimpleObserver<ResponseBody>(){
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "check code failed : " + e.getMessage());
                loginContractView.showToast(mContext.getString(R.string.verify_failed));
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String result = responseBody.string();
                    Log.e(TAG, "check code success : " + result);
                    if(result.contains(mContext.getString(R.string.code_200))){
                    loginWithRightCode(isCommonUser, str);
                    }else {
                        loginContractView.showToast(mContext.getString(R.string.verify_code_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loginContractView.showToast(mContext.getString(R.string.exception));
                }
            }
        });*/
    }

    private void loginWithRightCode(boolean isCommonUser, String str) {
        loginContractView.showProgress(mContext.getString(R.string.logining));
        LoginPresenter.this.isCommonUser = isCommonUser;
        if (isCommonUser) {// 普通用户登录
            mLoginPersonSubscription = LoginWrap.getInstance().loginPersonal(loginService, OkHttpUtils.getRequestBody(str), new SimpleObserver<UserInfo>() {
                @Override public void onError(Throwable e) {
                    Log.i("loginPersonal failed: " + e.getMessage());
                    loginContractView.dismissProgress();
                    loginContractView.showToast(mContext.getString(R.string.login_failed_retry));
                }
                @Override public void onNext(UserInfo userInfo) {
                    android.util.Log.i(TAG, "onNext: loign---- " + userInfo);
                    LoginPresenter.this.userInfo = userInfo;
                    int statusCode = userInfo.getCode();
                    if (statusCode == 401) {
                        loginContractView.dismissProgress();
                        loginContractView.showToast(mContext.getString(R.string.unexist_account));
                    } else if (statusCode == 404 || statusCode == 413) {
                        loginContractView.dismissProgress();
                        loginContractView.showToast(mContext.getString(R.string.verify_code_error));
                    } else if (statusCode == 200) {
                        Log.i(TAG, "login success : " + userInfo.toString());
                        LoginInfo info = new LoginInfo(userInfo.getToken(), userInfo.getToken()); // config...
                        NIMClient.getService(AuthService.class).login(info)
                                .setCallback(callback);
                        String avatarStr = userInfo.getAvatar().split("\\|")[2];
                        android.util.Log.i(TAG, "onNext: avatar-------  " + avatarStr);
                        SPUtil.put(mContext, SPKeyConstants.AVATAR, avatarStr);
                    } else {
                        Log.e(TAG, "login failed, code is : " + statusCode);
                        loginContractView.dismissProgress();
                        loginContractView.showToast(mContext.getString(R.string.login_failed_retry));
                    }
                }
            });
        }else {// 监狱管理用户  str = 用户名 + "-" + 密码
            String[] infos = str.split("-");
            LoginInfo info = new LoginInfo(infos[0], infos[1]); // config...
            NIMClient.getService(AuthService.class).login(info)
                    .setCallback(callback);
        }
    }

//    private Subscription mCheckSubscription;
//    public void checkVerifyCode(String phone_num, String code) {
//        loginContractView.showProgress(mContext.getString(R.string.logining));
//
//        String json_str = "{\"session\":{\"phone\":\"" + phone_num + "\",\"code\":\"" + code + "\"}}";
//        mCheckSubscription = LoginWrap.getInstance().checkVerifyCode(loginService, OkHttpUtils.getRequestBody(json_str), new SimpleObserver<ResponseBody>(){
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, "check code failed : " + e.getMessage());
//                loginContractView.dismissProgress();
//                loginContractView.showToast(mContext.getString(R.string.verify_failed));
//            }
//
//            @Override
//            public void onNext(ResponseBody responseBody) {
//                try {
//                    String result = responseBody.string();
//                    Log.e(TAG, "check code success : " + result);
//                    if(result.contains(mContext.getString(R.string.code_200))){
//                        loginContractView.checkVerifyCodeSuccess();
//                    }else {
//                        loginContractView.dismissProgress();
//                        loginContractView.showToast(mContext.getString(R.string.verify_code_error));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    registerView.dismissProgress();
//                    registerView.showToast(mContext.getString(R.string.exception));
//                }
//            }
//        });
//    }


    /**
     * 云信sdk登录回调
     */
    private RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
        @Override public void onSuccess(LoginInfo loginInfo) {
            if (isCommonUser) {// 普通个人用户
                if (userInfo != null ) {
                    Log.i(TAG, "login nim success:" + loginInfo.toString());
                    Log.i(TAG, "login nim success:" + userInfo.toString());
                    // 云信登录成功
                    putSP(SPKeyConstants.USERNAME, userInfo.getPhone());
//
                    // 开启/关闭通知栏消息提醒
//                    NIMClient.toggleNotification(true);

                    // 更新消息提醒配置 StatusBarNotificationConfig
//                    StatusBarNotificationConfig config = loginInfo;
//                    NIMClient.updateStatusBarNotificationConfig(config);

                    putSP(SPKeyConstants.NAME, userInfo.getName());
                    putSP(SPKeyConstants.RELATION_SHIP, userInfo.getRelationship());
                    putSP(SPKeyConstants.ACCESS_TOKEN, userInfo.getToken());
                    putSP(SPKeyConstants.FAMILY_ID, TextUtils.isEmpty(userInfo.getId())?-1:Integer.valueOf(userInfo.getId()));
                    putSP(SPKeyConstants.JAIL, userInfo.getJail());
                    putSP(SPKeyConstants.JAIL_ID, TextUtils.isEmpty(userInfo.getId())?-1:Integer.valueOf(userInfo.getJail_id()));
                    putSP(SPKeyConstants.ACCESS_TOKEN, userInfo.getToken());
                    putSP(SPKeyConstants.IS_COMMON_USER, true);
                    putSP(SPKeyConstants.IS_REGISTERED_USER, true);
                    mAccount=userInfo.getPhone();
//                    login();
                    ((LoginActivity)loginContractView).toNextPage();
                } else {
                    Log.i("user info is null");
                    loginContractView.dismissProgress();
                    loginContractView.showToast(mContext.getString(R.string.login_failed_retry));
                }
            }else {// 监狱管理用户
                putSP(SPKeyConstants.ACCESS_TOKEN, loginInfo.getAccount());
                putSP(SPKeyConstants.USERNAME, loginInfo.getAccount());
                putSP(SPKeyConstants.PASSWORD, loginInfo.getToken());
                putSP(SPKeyConstants.IS_COMMON_USER, false);
                if (!Config.isModify) {
                    mAccount = "6011";
                }
                loginContractView.toNextPage();
//                login();
            }
        }

        @Override public void onFailed(int i) {
            loginContractView.dismissProgress();
            switch (i) {
                case 302:loginContractView.showToast(mContext.getString(R.string.account_pwd_error));break;
                case 503:loginContractView.showToast(mContext.getString(R.string.server_busy));break;
                case 415:loginContractView.showToast(mContext.getString(R.string.network_error));break;
                case 408:loginContractView.showToast(mContext.getString(R.string.time_out));break;
                case 403:loginContractView.showToast(mContext.getString(R.string.illegal_control));break;
                case 422:loginContractView.showToast(mContext.getString(R.string.account_disable));break;
                case 500:loginContractView.showToast(mContext.getString(R.string.server_error));break;
                default:loginContractView.showToast(mContext.getString(R.string.login_failed_retry));break;
            }
        }

        @Override public void onException(Throwable throwable) {
            loginContractView.dismissProgress();
            loginContractView.showToast(mContext.getString(R.string.login_exception_retry));
            Log.i(TAG, "云信id登录异常 : " + throwable.getMessage());
        }
    };

    @Override
    public void sendVerifyCode(String content) {
        if (TextUtils.isEmpty(content)){
            loginContractView.showToast(mContext.getString(R.string.null_phone));
            return;
        }
        if (!RegexUtils.isMobileExact(content)){
            loginContractView.showToast(mContext.getString(R.string.unavailable_phone));
            return;
        }
        loginContractView.showProgress(mContext.getString(R.string.sending));
        loginContractView.startCountDown();
        String phone_str = "{\"registration\":{\"phone\":\"" + content + "\"}}";
//TODO        String phone_str = "{\"apply\":{\"phone\":\"" + content + "\"}}";
        mVerifySubscription = LoginWrap.getInstance().sendVerifyCode(loginService, OkHttpUtils.getRequestBody(phone_str), new SimpleObserver<ResponseBody>(){
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "get verification code failed : " + e.getMessage());
                loginContractView.dismissProgress();
                loginContractView.removeCountDown();
                loginContractView.showToast(mContext.getString(R.string.verify_failed));
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                loginContractView.dismissProgress();
                try {
                    String result = responseBody.string();
                    if (result.contains(mContext.getString(R.string.code_200))){
                        Log.d(TAG, "get verification code success : " + result);
                        loginContractView.showToast(mContext.getString(R.string.sended));
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                loginContractView.showToast(mContext.getString(R.string.verify_failed));
                loginContractView.removeCountDown();
            }
        });
    }

    @Override
    public boolean checkInputText(String... editTexts) {
        if (editTexts.length == 2){
            // 监狱端登录输入框验证
            return checkPrisonLoginText(editTexts);
        }else {
            // 3个的话就是普通用户端登录输入框验证
            return checkCommonLoginText(editTexts);
        }
    }

    /**
     * 普通用户登录文本验证
     * @param editTexts 三个输入框  顺序是 手机号  身份证号  验证码
     * @return {@code true}为通过可登录 <br>  {@code true}验证失败
     */
    private boolean checkCommonLoginText(String[] editTexts) {
        String phone_num = editTexts[0];
        String id_num = editTexts[1];
        String verifyCode = editTexts[2];
        if (TextUtils.isEmpty(phone_num)){
            loginContractView.showToast(mContext.getString(R.string.null_phone));
            return false;
        }
        if (TextUtils.isEmpty(id_num)){
            loginContractView.showToast(mContext.getString(R.string.id_empty));
            return false;
        }
        if (TextUtils.isEmpty(verifyCode)){
            loginContractView.showToast(mContext.getString(R.string.verify_code_empty));
            return false;
        }
        if (!RegexUtils.isMobileExact(phone_num)){
            loginContractView.showToast(mContext.getString(R.string.unavailable_phone));
            return false;
        }
        if (!RegexUtils.isIDCard15(id_num) && !RegexUtils.isIDCard18(id_num)){
            loginContractView.showToast(mContext.getString(R.string.unexist_id_num));
            return false;
        }
        return true;
    }

    /**
     * 监狱用户登录文本验证
     * @param editTexts 两个输入框  顺序是 用户名 密码
     * @return {@code true}为通过可登录 <br>  {@code true}验证失败
     */
    private boolean checkPrisonLoginText(String[] editTexts) {
        if (TextUtils.isEmpty(editTexts[0])){
            loginContractView.showToast(mContext.getString(R.string.username_empty));
            return false;
        }
        if (TextUtils.isEmpty(editTexts[1])){
            loginContractView.showToast(mContext.getString(R.string.pwd_empty));
            return false;
        }
        return true;
    }

    @Override
    public void attachView(@NonNull LoginContract.View view) {
        loginContractView = view;
    }

    @Override
    public void detachView() {
        RxUtils.unSubscribe(mVerifySubscription, mLoginPersonSubscription/*, mCheckSubscription*/);
        loginContractView = null;
    }

    /**
     * 存sp
     * @param key
     * @param defaultValue
     */
    private void putSP(String key, Object defaultValue){
        SPUtil.put(mContext, key, defaultValue);
    }

    private void login() {
        KDInitUtil.isH323 = true;
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
        android.util.Log.i("Login setting", "H323设置代理:" + dwIp);
        new Thread(new Runnable() {

            @Override
            public void run() {
                Configure.setH323PxyCfgCmd(true, false, dwIp);
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
            android.util.Log.i("Login", "tmtH323Pxy.dwSrvIp   " + tmtH323Pxy.dwSrvIp);
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
            android.util.Log.i("Login", "是否h323代理   " + tmtH323Pxy.bEnable);
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
     * 设置代理模式成功/失败
     * @param isEnable true:设置代理可用
     */
    public static void setH323PxyCfgCmdResult(final boolean isEnable) {
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
}
