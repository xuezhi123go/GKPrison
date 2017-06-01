package com.gkzxhn.gkprison.ui.activity;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.dagger.componet.activity.DaggerSplashComponent;
import com.gkzxhn.gkprison.dagger.contract.SplashContract;
import com.gkzxhn.gkprison.dagger.module.activity.SplashModule;
import com.gkzxhn.gkprison.presenter.activity.SplashPresenter;
import com.gkzxhn.gkprison.ui.activity.normal_activity.InputPasswordActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: Huang ZN
 * Date: 2016/12/21
 * Email:943852572@qq.com
 * Description:启动页面
 */
public class SplashActivity extends BaseActivityNew implements SplashContract.View{

    @BindView(R.id.tv_version)
    TextView tv_version_name;
    @BindView(R.id.splash)
    LinearLayout splash;

    @Inject
    SplashPresenter mPresenter;

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        mPresenter.initDB();
        mPresenter.next();
    }

    @Override
    public void showMainUi() {
        String app_version_info = getString(R.string.app_v)
                + SystemUtil.getVersionName(this);
        tv_version_name.setText(app_version_info);
        setUpBackGroundResources();
    }

    @Override
    public void toWelCome() {
        WelComeActivity.startActivity(this);
        finish();
    }

    @Override
    public void toInputPassWord() {
        InputPasswordActivity.startActivity(this);
        finish();
    }

    @Override
    public void toLogin() {
        // 标注非注册用户进入主页
        SPUtil.put(this, SPKeyConstants.IS_REGISTERED_USER, false);
        MainActivity.startActivity(this);
        finish();
    }

    @Override
    public void toMain() {
        MainActivity.startActivity(this);
        finish();
    }

    @Override
    public void toDateMeetingList() {
//        DateMeetingListActivity.startActivity(this);
        finish();
    }

    private void setUpBackGroundResources() {
        if (SystemUtil.isTablet(this)){
            splash.setBackgroundResource(R.drawable.splash_common_tablet);
        }
    }

    @Override
    protected void initInjector() {
        DaggerSplashComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .splashModule(new SplashModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return false;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    @Override
    public int setLayoutResId() {
        return R.layout.activity_splash_new;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
