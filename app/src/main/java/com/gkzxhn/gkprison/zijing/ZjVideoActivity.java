package com.gkzxhn.gkprison.zijing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.utils.CustomUtils.NimInitUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.oney.WebRTCModule.WebRTCModulePackage;
import com.zjrtc.LogcatHelper;
import com.zjrtc.ZjVideoManager;
import com.zjrtc.ZjVideoPackage;

public class ZjVideoActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {

    private static final String DEVICE_TYPE = "type";
    private static final String DEVICE_PHONE = "phone";
    private static final String DEVICE_TV = "tv";
    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (ZjVideoManager.getInstance().isPrintLogs()){
            LogcatHelper.getInstance(this).start();
        }
        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                //添加原生模块
                .addPackage(new MainReactPackage())
                .addPackage(new WebRTCModulePackage())
                .addPackage(new ZjVideoPackage())
                .setUseDeveloperSupport(false)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();

        // 注意这里的MyReactNativeApp必须对应“index.android.js”中的
        // “AppRegistry.registerComponent()”的第一个参数
        mReactRootView.startReactApplication(mReactInstanceManager, "zjAndroidRN", getLaunchOptions());

        setContentView(mReactRootView);

    }

    private Bundle getLaunchOptions() {
        Bundle bundle = new Bundle();
        if (ZjVideoManager.getInstance().isTvSupport()){
            bundle.putString(DEVICE_TYPE,DEVICE_TV);  //盒子/TV
        } else {
            bundle.putString(DEVICE_TYPE,DEVICE_PHONE); //手机端
        }
        return bundle;
    }


    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this,this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);//注销广播监听器
        NimInitUtil.sendNotificationToPrison(-2);
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }
        if (ZjVideoManager.getInstance().isPrintLogs()){
            LogcatHelper.getInstance(this).stop();
        }
        if (ZjVideoManager.getInstance()!=null){
            ZjVideoManager.getInstance().destroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((Constants.ZIJING_ACTION).equals(intent.getAction())) {
                ToastUtil.showShortToast("对方已挂断");
                if (mReactInstanceManager != null) {
                    mReactInstanceManager.onBackPressed();
                }
            }
        }
    };

    /**
     * 注册广播监听器
     */
    private void registerReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.ZIJING_ACTION);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }
}
