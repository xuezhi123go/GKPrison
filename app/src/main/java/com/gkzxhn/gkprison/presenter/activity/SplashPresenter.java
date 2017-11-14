package com.gkzxhn.gkprison.presenter.activity;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.gkzxhn.gkprison.base.PerActivity;
import com.gkzxhn.gkprison.dagger.contract.SplashContract;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;

import javax.inject.Inject;

import static com.gkzxhn.gkprison.constant.Config.mAccount;

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
    }

    @Override
    public void next() {
        mSplashView.showMainUi();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isFirst = (boolean) getSharedPres(SPKeyConstants.FIRST_LOGIN, true);
                if (!isFirst){
                    boolean isLock = (boolean) getClearableSharedPres(SPKeyConstants.APP_LOCK, false);
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
