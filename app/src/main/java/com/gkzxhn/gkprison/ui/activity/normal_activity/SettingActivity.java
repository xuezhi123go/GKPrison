package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 设置页面
 */
public class SettingActivity extends BaseActivityNew {

    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;
    @BindView(R.id.tb_clock_remind) ToggleButton tb_clock_remind;
    @BindView(R.id.tb_pwd_set) ToggleButton tb_pwd_set;
    @BindView(R.id.tv_version) TextView tv_version;
    private AlertDialog agreement_dialog;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        checkStatus();// 检查相关设置状态
        tv_title.setText(getString(R.string.setting));
        rl_back.setVisibility(View.VISIBLE);
        tv_version.setText("V " + SystemUtil.getVersionName(getApplicationContext()));
    }

    private boolean isChecked = false;
    @OnClick({R.id.tb_clock_remind, R.id.tb_pwd_set})
    public void onClick(ToggleButton view) {
        switch (view.getId()){
            case R.id.tb_pwd_set:
                isChecked = !isChecked;
                startPwsSetting(isChecked);// 设置密码开关
                break;
        }
    }

    @OnCheckedChanged({R.id.tb_clock_remind, R.id.tb_pwd_set})
    public void onCheckedChanged(ToggleButton view, boolean isChecked){
        switch (view.getId()){
            case R.id.tb_clock_remind:
                if (isChecked) {
                    showReminderDialog();// 开启闹钟提醒对话框
                    SPUtil.put(SettingActivity.this, SPKeyConstants.IS_MSG_REMIND, true);
                } else {
                    ToastUtil.showShortToast(getString(R.string.clock_close));
                    SPUtil.put(SettingActivity.this, SPKeyConstants.IS_MSG_REMIND, false);
                }
                break;
            /*case R.id.tb_pwd_set:
                startPwsSetting(isChecked);// 设置密码开关
                break;*/
        }
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return true;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        UIUtils.dismissAlertDialog(agreement_dialog);
        super.onDestroy();
    }


    /**
     * 打开/关闭密码设置
     *
     * @param on
     */
    private void startPwsSetting(boolean on) {
        Intent intent = new Intent(SettingActivity.this, SettingPasswordActivity.class);
        if (on) {
            intent.putExtra("type", "open");
        } else {
            intent.putExtra("type", "close");
        }
        startActivity(intent);
    }

    /**
     * 开启闹钟提醒对话框
     */
    private void showReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setMessage(R.string.alarm_reminder_msg);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 判断是否枷锁和闹钟提醒
     */
    private void checkStatus() {
        boolean isLock = (boolean) SPUtil.get(this, SPKeyConstants.APP_LOCK, false);
        isChecked = isLock;
        tb_pwd_set.setChecked(isLock);
        boolean isMsgRemind = (boolean) SPUtil.get(this, SPKeyConstants.IS_MSG_REMIND, false);
        tb_clock_remind.setChecked(isMsgRemind);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus();// 检查状态
    }

    @Override
    public void onBackPressed() {
        finishPage();
    }

    /**
     * 关闭页面
     */
    private void finishPage() {
        if (agreement_dialog != null && agreement_dialog.isShowing()) {
            agreement_dialog.dismiss();
        } else {
            finish();
        }
    }

    @OnClick({R.id.rl_opinion_feedback, R.id.rl_version_update,
            R.id.tv_agreement, R.id.tv_contact_us, R.id.rl_back})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_opinion_feedback:
                String token = (String) SPUtil.get(this, SPKeyConstants.ACCESS_TOKEN, "");
                if (!TextUtils.isEmpty(token)) {
                    intent = new Intent(SettingActivity.this, OpinionFeedbackActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.showShortToast(getString(R.string.enable_logined));
                }
                break;
            case R.id.rl_version_update:
                intent = new Intent(this, VersionUpdateActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_agreement:
                showAgreementDialog();// 协议
                break;
            case R.id.tv_contact_us:
                intent = new Intent(this, ContactUsActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_back:
                finishPage();
                break;
        }
    }

    /**
     * 软件协议
     */
    private void showAgreementDialog() {
        AlertDialog.Builder agreement_builder = new AlertDialog.Builder(this);
        View agreement_view = View.inflate(this, R.layout.software_agreement_dialog, null);
        agreement_builder.setView(agreement_view);
        agreement_dialog = agreement_builder.create();
        agreement_dialog.setCancelable(true);
        Button btn_ok = (Button) agreement_view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreement_dialog.dismiss();
            }
        });
        agreement_dialog.show();
    }
}
