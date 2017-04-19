package com.gkzxhn.gkprison.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: Huang ZN
 * Date: 2016/12/22
 * Email:943852572@qq.com
 * Description:WelComeActivity
 */

public class WelComeActivity extends BaseActivityNew {

    @BindView(R.id.vp_welcome) ViewPager vp_welcome;
    private int[] imgs = {R.drawable.welcome01, R.drawable.welcome02, R.drawable.welcome03};

    /**
     * 开启当前页面
     * @param mContext
     */
    public static void startActivity(Context mContext){
        Intent intent = new Intent(mContext, WelComeActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public int setLayoutResId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        vp_welcome.setAdapter(new WelComeAdapter(this, imgs));
        vp_welcome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset,
                                                 int positionOffsetPixels) {}
            @Override public void onPageScrollStateChanged(int state) {}
            @Override public void onPageSelected(int position) {
                if(position == imgs.length - 1){
                    new Handler().postDelayed(new Runnable() {
                        @Override public void run() {
                            LoginActivity.startActivity(WelComeActivity.this);
                            WelComeActivity.this.finish();
                        }
                    }, 1000);
                }
            }
        });
    }

    @Override
    protected void initInjector() {}

    @Override
    protected boolean isApplyStatusBarColor() {
        return false;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }
}
