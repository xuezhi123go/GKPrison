package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.jungly.gridpasswordview.GridPasswordView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置密码
 */
public class SettingPasswordActivity extends BaseActivityNew {

    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;
    @BindView(R.id.tv_please_input_pwd) TextView tv_please_input_pwd;
    @BindView(R.id.gpv_pwd)
    GridPasswordView gpv_pwd;
    @BindView(R.id.gpv_confirm_pwd) GridPasswordView gpv_confirm_pwd;
    @BindView(R.id.gpv_cancel_pwd) GridPasswordView gpv_cancel_pwd;
    @BindView(R.id.rl_pwd) RelativeLayout rl_pwd;
    @BindView(R.id.tv_not_match_pwd) TextView tv_not_match_pwd;
    private String pwd;
    private String confirm_pwd;
    private AlertDialog dialog;
    private Handler handler = new Handler();

    @Override
    public int setLayoutResId() {
        return R.layout.activity_setting_password;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(R.string.set_pwd);
        rl_back.setVisibility(View.VISIBLE);
        String type = getIntent().getStringExtra("type");
        if (type.equals("close")) {
            gpv_cancel_pwd.setVisibility(View.VISIBLE);
            gpv_pwd.setVisibility(View.GONE);
            gpv_confirm_pwd.setVisibility(View.GONE);
        }
        gpv_cancel_pwd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {
            }

            @Override
            public void onInputFinish(String psw) {
                if (psw.equals(getSPValue(SPKeyConstants.APP_PASSWORD, "") + "")) {
                    SPUtil.put(SettingPasswordActivity.this, SPKeyConstants.APP_PASSWORD, "canceled");
                    SPUtil.put(SettingPasswordActivity.this, SPKeyConstants.APP_LOCK, false);
                    handler.postDelayed(show_dialog_task, 500);
                } else {
                    tv_not_match_pwd.setVisibility(View.VISIBLE);
                    tv_not_match_pwd.setText(getString(R.string.pwd_not_match));
                    gpv_cancel_pwd.clearPassword();
                }
            }
        });
        gpv_pwd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {
                tv_not_match_pwd.setVisibility(View.GONE);
            }

            @Override
            public void onInputFinish(String psw) {
                pwd = psw;
                handler.postDelayed(delay_dismiss_pwd, 500);
            }
        });
        gpv_confirm_pwd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                confirm_pwd = psw;
                if (pwd.equals(confirm_pwd)) {
                    SPUtil.put(SettingPasswordActivity.this, SPKeyConstants.APP_LOCK, true);
                    SPUtil.put(SettingPasswordActivity.this, SPKeyConstants.APP_PASSWORD, pwd);
                    handler.postDelayed(show_dialog_task, 500);
                } else {
                    handler.postDelayed(delay_dismiss_confirm_pwd, 1000);
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
     * 密码不匹配时延时隐藏确认输入密码
     */
    private Runnable delay_dismiss_confirm_pwd = new Runnable() {
        @Override
        public void run() {
            gpv_confirm_pwd.clearPassword();
            gpv_confirm_pwd.setVisibility(View.GONE);
            gpv_pwd.clearPassword();
            gpv_pwd.setVisibility(View.VISIBLE);
            tv_please_input_pwd.setText(getString(R.string.input_pwd));
            tv_not_match_pwd.setVisibility(View.VISIBLE);
        }
    };

    /**
     * 延时隐藏输入密码
     */
    private Runnable delay_dismiss_pwd = new Runnable() {
        @Override
        public void run() {
            gpv_pwd.setVisibility(View.GONE);
            gpv_confirm_pwd.setVisibility(View.VISIBLE);
            tv_please_input_pwd.setText(R.string.confirm_pwd);
        }
    };

    /**
     * 提示对话框
     */
    private Runnable show_dialog_task = new Runnable() {
        @Override
        public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingPasswordActivity.this);
            builder.setMessage(R.string.set_success);
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    SettingPasswordActivity.this.finish();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
    };

    @Override
    public void onBackPressed() {
        finishPage();
    }

    /**
     * 关闭页面
     */
    private void finishPage() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.rl_back)
    public void onClick(){
        finishPage();
    }

    @Override
    protected void onDestroy() {
        UIUtils.dismissAlertDialog(dialog);
        super.onDestroy();
    }
}
