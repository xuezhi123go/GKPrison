package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by huangzhengneng on 2016/1/28
 * 联系我们页面
 */
public class ContactUsActivity extends BaseActivityNew {

    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_contact_us;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(getString(R.string.contract_us));
        rl_back.setVisibility(View.VISIBLE);
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    @OnClick(R.id.rl_back)
    public void onClick(){
        finish();
    }
}
