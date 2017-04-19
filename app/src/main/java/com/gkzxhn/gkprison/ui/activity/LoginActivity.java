package com.gkzxhn.gkprison.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.dagger.componet.activity.DaggerLoginComponent;
import com.gkzxhn.gkprison.dagger.contract.LoginContract;
import com.gkzxhn.gkprison.presenter.activity.LoginPresenter;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.pc.utils.StringUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil.mContext;


/**
 * Author: Huang ZN
 * Date: 2016/12/22
 * Email:943852572@qq.com
 * Description:LoginActivity登录
 */

public class LoginActivity extends BaseActivityNew implements LoginContract.View{

    // 绑定id
    @BindView(R.id.tv_title)
    TextView tv_title;

//    @BindView(R.id.layout_prison)
//    LinearLayout layout_prison;
//    @BindView(R.id.et_username)
//    EditText et_username;
//    @BindView(R.id.et_password)
//    EditText et_password;

    @BindView(R.id.layout_personal)
    RelativeLayout layout_personal;
    @BindView(R.id.et_personal_username)
    EditText et_personal_username;
    @BindView(R.id.et_personal_id_code)
    EditText et_personal_id_code;
    @BindView(R.id.et_verify_code)
    EditText et_verify_code;
    @BindView(R.id.tv_send_verify_code)
    TextView tv_send_verify_code;

    @Inject
    LoginPresenter mPresenter;

    private Handler handler;
    private int countdown = 60;

    private ProgressDialog verify_dialog;

    private int count = 0;
    private long time = 0;

//    @OnClick({R.id.et_personal_username, R.id.et_username})
//    public void onClick(View view){
//        time = System.currentTimeMillis();
//        if (System.currentTimeMillis() - time < 3000){
//            count++;
//            if (count > 10){
//                Intent intent = new Intent(LoginActivity.this, ConfigActivity.class);
//                startActivity(intent);
//                count = 0;
//            }
//        }else {
//            count = 0;
//        }
//    }

    /**
     * 开启此activity
     * @param mContext
     */
    public static void startActivity(Context mContext){
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 清除任务站开启此页面
     * @param mContext
     */
    public static void startActivityClearTask(Context mContext){
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 登录成功
     * @param isSuccess
     * @param failedMsg
     */
    public void loginSuccess(final boolean isSuccess, final String failedMsg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgress();
                if (isSuccess) {
                    showToast(mContext.getString(R.string.login_success));
                    // 进入下一页
                    toNextPage();
                    return;
                }
                if (StringUtils.isNull(failedMsg)) {
                    showToast(mContext.getString(R.string.login_failed_retry));
                } else {
                    showToast(failedMsg);
                }
            }
        });
    }

    @Override
    public int setLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(getString(R.string.login_text));
        mPresenter.attachView(this);
    }

    @Override
    protected void initInjector() {
        DaggerLoginComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build()
                .inject(this);
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
    public void showProgress(String msg) {
        if (verify_dialog == null){
            verify_dialog = UIUtils.showProgressDialog(this, msg);
        }else {
            if (!verify_dialog.isShowing())
                verify_dialog.show();
        }
    }

    @Override
    public void dismissProgress() {
        UIUtils.dismissProgressDialog(verify_dialog);
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.showShortToast(msg);
    }

    @Override
    public void startCountDown() {
        if (!isRunning) {
            handler = new Handler();
            tv_send_verify_code.setEnabled(false);
            tv_send_verify_code.setBackgroundColor(getResources().getColor(R.color.tv_gray));
            tv_send_verify_code.setTextColor(getResources().getColor(R.color.white));
            handler.post(count_down_task);
        }
    }

    @Override
    public void removeCountDown() {
        removeCodeTask();
    }

    @Override
    public void toNextPage() {

        boolean isCommonUser = layout_personal.getVisibility() == View.VISIBLE;
        if (isCommonUser){
            //存储身份份证好吗
            String id_num = et_personal_id_code.getText().toString().trim();
            SPUtil.put(this, SPKeyConstants.PASSWORD,id_num);
            MainActivity.startActivity(this);
        }else {
//            DateMeetingListActivity.startActivity(this);
        }
        finish();
    }

    private boolean isRunning = false;
    /**
     * 验证码发送倒计时任务
     */
    private Runnable count_down_task = new Runnable() {
        @Override
        public void run() {
            isRunning = true;
            String text = countdown + " 秒";
            tv_send_verify_code.setText(text);
            countdown--;
            if (countdown == 0) {
                removeCodeTask();
            } else {
                handler.postDelayed(count_down_task, 1000);
            }
        }
    };

    /**
     * 移除倒计时任务
     */
    private void removeCodeTask() {
        if (isRunning) {
            handler.removeCallbacks(count_down_task);
            tv_send_verify_code.setEnabled(true);
            tv_send_verify_code.setBackgroundColor(getResources().getColor(R.color.white));
            tv_send_verify_code.setTextColor(getResources().getColor(R.color.theme));
            tv_send_verify_code.setText(getString(R.string.send_verify_code));
            countdown = 60;
            isRunning = false;
        }
    }
//
//    @OnClick({R.id.tv_send_verify_code, R.id.btn_person_login,
//            R.id.btn_personal_switch, R.id.bt_register, R.id.bt_fast_login,
//            R.id.btn_prison_login, R.id.btn_prison_switch})

    @OnClick({R.id.tv_send_verify_code, R.id.btn_person_login,
            R.id.bt_register, R.id.bt_fast_login,
    })
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.tv_send_verify_code:// 发送验证码
                if (isRunning)
                    return; // 正在倒计时
                if (SystemUtil.isNetWorkUnAvailable())
                    return;// 网络不可用
                mPresenter.sendVerifyCode(et_personal_username.getText().toString().trim());
                break;
            case R.id.btn_person_login:// 个人用户登录
                String phone_num = et_personal_username.getText().toString().trim();
                String id_num = et_personal_id_code.getText().toString().trim();
                String verify_code = et_verify_code.getText().toString().trim();
//                String phone_num ="18163657553";
//                String id_num = "431023198908066547";
//                String verify_code = "8256";
                if (!mPresenter.checkInputText(phone_num,
                        id_num, verify_code))// 校验输入文本可行性
                    return;
                if (SystemUtil.isNetWorkUnAvailable())
                    return;
                String login_json = "{\"session\":{ \"phone\":\"" + phone_num + "\", \"uuid\":\""
                        + id_num + "\", \"code\":\"" + verify_code + "\"}}";
                String json_str = "{\"session\":{\"phone\":\"" + phone_num + "\",\"code\":\"" + verify_code + "\"}}";
                mPresenter.login(true, login_json, json_str);
                break;
//            case R.id.btn_personal_switch:// 个人用户登录切换到监狱用户登录
//                switchLoginUi();
//                break;
            case R.id.bt_register:// 跳转注册页面
                RegisterActivity.startActivity(this);
                break;
            case R.id.bt_fast_login:// 无账号快捷登录
                if (SystemUtil.isNetWorkUnAvailable())
                    return;
                // 标注非注册用户进入主页
                SPUtil.put(this, SPKeyConstants.IS_REGISTERED_USER, false);
                MainActivity.startActivity(this);
                finish();
                break;
//            case R.id.btn_prison_login:// 监狱管理用户登录
//                // 非普通用户  即监狱用户
//                String username = et_username.getText().toString().trim();
//                String password = et_password.getText().toString().trim();
//                if (!mPresenter.checkInputText(username, password))
//                    return;
//                if (SystemUtil.isNetWorkUnAvailable())
//                    return;
//                mPresenter.login(false, username + "-" + password);
//                break;
//            case R.id.btn_prison_switch:// 监狱用户切换至普通个人用户登录界面
//                switchLoginUi();
//                break;
        }
    }

    /**
     * 切换登录方式UI
     */
    private void switchLoginUi() {
        layout_personal.setVisibility(layout_personal.getVisibility()
                == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (isRunning)
            removeCountDown();
        super.onDestroy();
        mPresenter.detachView();
    }
}
