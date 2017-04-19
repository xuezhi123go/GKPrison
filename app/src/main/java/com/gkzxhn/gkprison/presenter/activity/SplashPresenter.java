package com.gkzxhn.gkprison.presenter.activity;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.gkzxhn.gkprison.base.PerActivity;
import com.gkzxhn.gkprison.dagger.contract.SplashContract;
import com.gkzxhn.gkprison.model.dao.SQLiteHelper;
import com.gkzxhn.gkprison.utils.CustomUtils.KDInitUtil;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.google.gson.Gson;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.LoginStateManager;
import com.keda.sky.app.TruetouchGlobal;
import com.kedacom.kdv.mt.api.Configure;
import com.kedacom.kdv.mt.bean.TMtH323PxyCfg;
import com.kedacom.kdv.mt.constant.EmConfProtocol;
import com.pc.utils.DNSParseUtil;
import com.pc.utils.FormatTransfer;
import com.pc.utils.NetWorkUtils;

import java.net.InetAddress;

import javax.inject.Inject;

import static com.gkzxhn.gkprison.constant.Config.mAccount;
import static com.gkzxhn.gkprison.constant.Config.mAddr;
import static com.gkzxhn.gkprison.constant.Config.mPassword;

/**
 * Author: Huang ZN
 * Date: 2016/12/21
 * Email:943852572@qq.com
 * Description:SplashPresenter
 */
@PerActivity
public class SplashPresenter implements SplashContract.Presenter{

    private static final String TAG = SplashPresenter.class.getName();
    private SplashContract.View mSplashView;
    private Context mContext;

    @Inject
    public SplashPresenter(Context context){
        this.mContext = context;
    }

    @Override
    public void attachView(@NonNull SplashContract.View view) {
        mSplashView = view;
    }

    @Override
    public void detachView() {
        mSplashView = null;
    }

    @Override
    public void initDB() {
        SQLiteHelper.init(mContext);
    }

    @Override
    public void next() {
        mSplashView.showMainUi();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isFirst = (boolean) getSharedPres(SPKeyConstants.FIRST_LOGIN, true);
                if (!isFirst){
                    boolean isLock = (boolean) getSharedPres(SPKeyConstants.APP_LOCK, false);
                    if (isLock){// 已加锁进入输入密码页面
                        mSplashView.toInputPassWord();
                        Log.i(TAG, "user will go to input password!");
                    }else {
                        String account = (String) getClearableSharedPres(SPKeyConstants.USERNAME, "");
                        String password = (String) getClearableSharedPres(SPKeyConstants.PASSWORD, "");
                        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
                            mSplashView.toLogin();
                            Log.i(TAG, "user will go to login!");
                        }else {
                            boolean isCommonUser = (boolean) getClearableSharedPres(SPKeyConstants.IS_COMMON_USER, true);
                            if (isCommonUser){
                                mAccount= (String) SPUtil.get(mContext, SPKeyConstants.USERNAME, "6010");
                                //registerGK();
                                mSplashView.toMain();
                                Log.i(TAG, "isCommonUser, user will go to main!");
                            }else {
                                mSplashView.toDateMeetingList();
                                Log.i(TAG, "isNotCommonUser, user will go to DateMeetingList!");
                            }
                        }
                    }
                }else {// 第一次  进入欢迎页面
                    mSplashView.toWelCome();
                    SPUtil.putCanNotClear(mContext, SPKeyConstants.FIRST_LOGIN, false);
                    Log.i(TAG, "new user!!!!!!!!!");
                }
            }
        }, 1000);
    }

    private void registerGK() {
        if (!GKStateMannager.mRegisterGK){
//            GKStateMannager.instance().unRegisterGK();
//            GKStateMannager.instance().registerGK();// 失败原因见枚举类EmRegFailedReason
            TruetouchGlobal.logOff();
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
                        android.util.Log.i(TAG, "run: 没有注册代理 , 或者代理的ip改变了");
                        setH323PxyCfgCmd(dwIp);
                        return;
                    }
                    android.util.Log.i(TAG, "run: 开始注册GK...");
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

    /**
     * 获取sp的值
     * @param key
     * @param defaultValue
     * @return
     */
    private Object getSharedPres(String key, Object defaultValue){
        return SPUtil.getCanNotClear(mContext, key, defaultValue);
    }

    /**
     * 获取sp的值
     * @param key
     * @param defaultValue
     * @return
     */
    private Object getClearableSharedPres(String key, Object defaultValue){
        return SPUtil.get(mContext, key, defaultValue);
    }
}
