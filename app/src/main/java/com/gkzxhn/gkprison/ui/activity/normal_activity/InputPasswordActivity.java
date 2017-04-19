package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.ui.activity.LoginActivity;
import com.gkzxhn.gkprison.ui.activity.WelComeActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.jungly.gridpasswordview.GridPasswordView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 输入app解锁密码
 */
public class InputPasswordActivity extends BaseActivityNew {

    @BindView(R.id.tv_pwd_error) TextView tv_pwd_error;
    @BindView(R.id.gpv_input_pwd) GridPasswordView gpv_input_pwd;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;

    /**
     * 开启当前activity
     * @param mContext
     */
    public static void startActivity(Context mContext){
        Intent intent = new Intent(mContext, InputPasswordActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public int setLayoutResId() {
        return R.layout.activity_input_password;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(R.string.input_pwd);
        rl_back.setVisibility(View.VISIBLE);
        gpv_input_pwd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override public void onTextChanged(String psw) {
                tv_pwd_error.setVisibility(View.GONE);
            }

            @Override public void onInputFinish(String psw) {
                String app_password = (String)getSPValue(SPKeyConstants.APP_PASSWORD, "");
                if(!TextUtils.isEmpty(app_password) && app_password.equals(psw)){
                    new Handler().post(go_to_next_task);
                }else {
                    tv_pwd_error.setVisibility(View.VISIBLE);
                    gpv_input_pwd.clearPassword();
                }
            }
        });
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    /**
     * 密码输入正确，下一步
     */
    private Runnable go_to_next_task = new Runnable() {
        @Override
        public void run() {
            Intent intent;
            if ((boolean)getSPValue(SPKeyConstants.FIRST_LOGIN, true)) {
                intent = new Intent(InputPasswordActivity.this, WelComeActivity.class);
                startActivity(intent);
            } else {
                if (TextUtils.isEmpty((String)getSPValue(SPKeyConstants.USERNAME, "")) ||
                        TextUtils.isEmpty((String)getSPValue(SPKeyConstants.PASSWORD, ""))) {
                    LoginActivity.startActivity(InputPasswordActivity.this);
                } else {
                    /*if ((boolean)getSPValue(SPKeyConstants.IS_COMMON_USER, true)) {
                        intent = new Intent(InputPasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(InputPasswordActivity.this, DateMeetingListActivity.class);
                        startActivity(intent);
                    }*/
                }
            }
            InputPasswordActivity.this.finish();
        }
    };

    @OnClick(R.id.rl_back)
    public void onClick(){
        finish();
    }
}
