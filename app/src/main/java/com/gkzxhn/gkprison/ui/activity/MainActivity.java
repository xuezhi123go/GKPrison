package com.gkzxhn.gkprison.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gkzxhn.gkprison.BuildConfig;
import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.base.BaseFragmentNew;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Config;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.dagger.componet.activity.DaggerMainComponent;
import com.gkzxhn.gkprison.dagger.contract.MainContract;
import com.gkzxhn.gkprison.model.dao.GreenDaoHelper;
import com.gkzxhn.gkprison.model.dao.bean.CartDao;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.api.wrap.MainWrap;
import com.gkzxhn.gkprison.model.net.bean.Balances;
import com.gkzxhn.gkprison.model.net.bean.VersionInfo;
import com.gkzxhn.gkprison.presenter.activity.MainPresenter;
import com.gkzxhn.gkprison.ui.activity.normal_activity.MyWalletActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.RemittanceRecordActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.SettingActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.ShoppingRecordActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.SystemMessageActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.UserInfoActivity;
import com.gkzxhn.gkprison.ui.fragment.canteen.CanteenBaseFragment;
import com.gkzxhn.gkprison.ui.fragment.home.HomeFragment;
import com.gkzxhn.gkprison.ui.fragment.visit.RemoteMeetFragment;
import com.gkzxhn.gkprison.utils.CustomUtils.MainUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.NimInitUtil;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.DensityUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.DownloadManager;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.StatusBarUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.gkzxhn.gkprison.utils.event.RechargeEvent;
import com.gkzxhn.gkprison.widget.view.auto.AutoCompleteTv;
import com.keda.sky.app.GKStateMannager;
import com.keda.sky.app.TruetouchGlobal;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/28
 * Email:943852572@qq.com
 * Description:主页
 */
public class MainActivity extends BaseActivityNew implements MainContract.View,
        View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.navigationView) NavigationView navigationView;
    @BindView(R.id.iv_user_icon) ImageView iv_user_icon;
    @BindView(R.id.tv_menu_user_name) TextView tv_menu_user_name;
    @BindView(R.id.tool_bar) Toolbar tool_bar;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_home_menu) RelativeLayout rl_home_menu;

    @BindView(R.id.main_content) FrameLayout main_content;
    @BindView(R.id.rg_bottom_guide) RadioGroup rg_bottom_guide;
    @BindView(R.id.rb_bottom_guide_home) RadioButton rb_bottom_guide_home;
    @BindView(R.id.rb_bottom_guide_visit) RadioButton rb_bottom_guide_visit;
    @BindView(R.id.rb_bottom_guide_canteen) RadioButton rb_bottom_guide_canteen;

    private List<BaseFragmentNew> fragments = new ArrayList<>();
    private FragmentManager manager;
    private FragmentTransaction transaction = null;
    private HomeFragment homeFragment = null;
    private RemoteMeetFragment remoteMeetFragment = null;
    private CanteenBaseFragment canteenBaseFragment = null;

    @Inject
    MainPresenter mPresenter;

    private ProgressDialog progressDialog;
    private AlertDialog reLoginDialog;
    private AlertDialog fastLoginDialog;
    private AlertDialog kickoutDialog;
    private AlertDialog logoutDialog;

    private boolean isRegisterUser;
//    private SQLiteDatabase database;
    private long mExitTime;
    private String mStart_time;
    private long mStart_time_mills;
    private long mEnd_time_mills;
    private CartDao mCartDao;


    /**
     * 开启当前activity
     * @param mContext
     */
    public static void startActivity(Context mContext){
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public int setLayoutResId() {
        return R.layout.main_layout;
    }

    @Override
    protected void initUiAndListener() {
        EventBus.getDefault().register(this);
        if (!MyApplication.isV7) {
            UIUtils.showAlertDialog(this, "您的手机CPU类型版本太低,无法进行视频通话...",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }
        mPresenter.attachView(this);
        ButterKnife.bind(this);
        setSupportActionBar(tool_bar);
        tv_title.setText(getString(R.string.main_page));
        rl_home_menu.setVisibility(View.VISIBLE);
        rl_home_menu.setOnClickListener(this);
        rg_bottom_guide.setOnCheckedChangeListener(this);
        setNavigationViewWidth();
        StatusBarUtil.setColorForDrawerLayout(this, drawerLayout, Color.parseColor("#EE6495ed"), 0);
        setNavigationItemClick();
        setBottomGuideIcon();
        isRegisterUser = (boolean) SPUtil.get(this, SPKeyConstants.IS_REGISTERED_USER, false);
//        database = StringUtils.getSQLiteDB(this);
        mCartDao = GreenDaoHelper.getDaoSession().getCartDao();
        mPresenter.checkStatus();
        tv_title.setOnClickListener(this);

        /*Observer<List<IMMessage>> incomingMessageObserver =
                new Observer<List<IMMessage>>() {
                    @Override
                    public void onEvent(List<IMMessage> messages) {
                        // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                        for (IMMessage message : messages) {
                            android.util.Log.i(TAG, "onEvent: nim--- " + message.getContent());
                            ToastUtil.showShortToast(message.getContent());
                        }
                    }
                };
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        NimInitUtil.checkStatus(this);
        /*Config.mAccount= (String) SPUtil.get(MainActivity.this, SPKeyConstants.USERNAME, "6010");
        String meet_time = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.MEETINGS_TIME, "");
        android.util.Log.i(TAG, "onResume: ==== " + meet_time);
        if (TextUtils.isEmpty(meet_time)) {
            return;
        }
        MeetingTimeInfo meetingTimeInfo = new Gson().fromJson(meet_time, MeetingTimeInfo.class);
        List<String> meetings = meetingTimeInfo.meetings;
        List<Long> meetings_mills = new ArrayList<>();
        if (meetings == null) {
            return;
        }
        for (String meeting : meetings) {
            int lastIndexOf = meeting.lastIndexOf("-");
            meeting = meeting.substring(0, lastIndexOf);
            try {
                long meeting_mills = StringUtils.formatToMill(meeting, "yyyy-MM-dd HH:mm");
                meetings_mills.add(meeting_mills);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(meetings_mills);
        long latestMills = getLatestMills(meetings_mills) - 30 * 60 *1000;
        if (latestMills < 0) {
            flag = false;
            if (mThread != null) {
                mThread.stop();
                mThread = null;
            }
            return;
        }
        mStart_time_mills = latestMills;
        *//*try {
            //测试时间
            mStart_time_mills = StringUtils.formatToMill("2017-04-06 16:55", "yyyy-MM-dd HH:mm");
        } catch (ParseException e) {
            e.printStackTrace();
        }*//*
        //注销GK时间为视频会见结束时间之后30分钟,注册GK时间为视频会见开始时间前30分钟
        mEnd_time_mills = mStart_time_mills + 90 * 60 * 1000;
//        mEnd_time_mills = mStart_time_mills + 1 * 60 * 1000;

        android.util.Log.i(TAG, "onResume: start mills ==  : " + mStart_time_mills + "end mills === " +mEnd_time_mills);
        flag = true;
        if (mThread == null) {
            android.util.Log.i(TAG, "mThread: start mills ==  : " + mStart_time_mills + "end mills === " +mEnd_time_mills);
            mThread = new Thread(mRunnable);
            mThread.start();
        }*/
//        NimInitUtil.registerGK();
    }

    /**
     * 获取大于最接近当前时间的集合毫秒值
     * @param meetings_mills
     * @return 0表示没有符合条件的值
     */
    private long getLatestMills(List<Long> meetings_mills) {
        long currentTimeMillis = System.currentTimeMillis();
        for (Long meetings_mill : meetings_mills) {
            if (meetings_mill > currentTimeMillis - 60 * 60 * 1000) {
                android.util.Log.i(TAG, "getLatestMills: meetings_mill=== " + meetings_mill);
                return meetings_mill;
            }
        }
        return 0;
    }

    private Thread mThread;

    private boolean flag = true;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (flag) {
                // ------- code for task to run
                android.util.Log.i(TAG, "Runnable: start mills ==  : " + mStart_time_mills + "end mills === " +mEnd_time_mills);
                android.util.Log.i(TAG, "Runnable: current mills ==  : " + System.currentTimeMillis());
                if (mStart_time_mills != 0 && System.currentTimeMillis() > mStart_time_mills && System.currentTimeMillis() < mEnd_time_mills) {
                    NimInitUtil.registerGK();
                }else{
                    if (GKStateMannager.mRegisterGK){
                        GKStateMannager.instance().unRegisterGK();
                    }
                }
                // ------- ends here
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 设置侧栏item点击事件
     */
    private void setNavigationItemClick() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()){
                    case R.id.user_info:
                        if (isRegisterUser) {
                            intent = new Intent(MainActivity.this, UserInfoActivity.class);
                            startActivity(intent);
                        } else {
                            showToast(getString(R.string.enable_logined));
                        }
                        break;
                    case R.id.remittance_record:
                        if (isRegisterUser) {
                            intent = new Intent(MainActivity.this, RemittanceRecordActivity.class);
                            startActivity(intent);
                        } else {
                            showToast(getString(R.string.enable_logined));
                        }
                        break;
                    case R.id.shopping_record:
                        if (isRegisterUser) {
                            intent = new Intent(MainActivity.this, ShoppingRecordActivity.class);
                            startActivity(intent);
                        } else {
                            showToast(getString(R.string.enable_logined));
                        }
                        break;
                    case R.id.message_record:
                        if (isRegisterUser) {
                            intent = new Intent(MainActivity.this, SystemMessageActivity.class);
                            startActivity(intent);
                        } else {
                            showToast(getString(R.string.enable_logined));
                        }
                        break;
                    case R.id.my_wallet:
                        if (isRegisterUser) {
                            intent = new Intent(MainActivity.this, MyWalletActivity.class);
                            startActivity(intent);
                        } else {
                            showToast(getString(R.string.enable_logined));
                        }
                        break;
                    case R.id.setting:
                        intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        if (isRegisterUser){
                            logoutDialog = MainUtils.showConfirmDialog(MainActivity.this);
                        }else {
                            LoginActivity.startActivityClearTask(MainActivity.this);
                            MainActivity.this.finish();
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 设置navigationView宽度
     */
    private void setNavigationViewWidth() {
        ViewGroup.LayoutParams params = navigationView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels / 2;
        navigationView.setLayoutParams(params);
    }

    /**
     * 调整底部导航栏的图标大小
     */
    private void setBottomGuideIcon() {
        Drawable[] drawables = rb_bottom_guide_home.getCompoundDrawables();
        drawables[1].setBounds(0, DensityUtil.dip2px(getApplicationContext(), 5),
                getResources().getDimensionPixelSize(R.dimen.home_tab_width),
                getResources().getDimensionPixelSize(R.dimen.home_tab_height));
        rb_bottom_guide_home.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        Drawable[] drawables2 = rb_bottom_guide_visit.getCompoundDrawables();
        drawables2[1].setBounds(0, DensityUtil.dip2px(getApplicationContext(), 5),
                getResources().getDimensionPixelSize(R.dimen.home_tab_width),
                getResources().getDimensionPixelSize(R.dimen.home_tab_height));
        rb_bottom_guide_visit.setCompoundDrawables(drawables2[0], drawables2[1], drawables2[2], drawables2[3]);
        Drawable[] drawables3 = rb_bottom_guide_canteen.getCompoundDrawables();
        drawables3[1].setBounds(0, DensityUtil.dip2px(getApplicationContext(), 5),
                getResources().getDimensionPixelSize(R.dimen.home_tab_width),
                getResources().getDimensionPixelSize(R.dimen.home_tab_height));
        rb_bottom_guide_canteen.setCompoundDrawables(drawables3[0], drawables3[1], drawables3[2], drawables3[3]);
    }

    @Override
    protected void initInjector() {
        DaggerMainComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build()
                .inject(this);
    }

    @Override
    protected boolean isApplyStatusBarColor() {
        return false;
    }

    @Override
    protected boolean isApplyTranslucentStatus() {
        return false;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, TAG + " onDestroy");
        UIUtils.dismissProgressDialog(progressDialog);
        UIUtils.dismissAlertDialog(reLoginDialog, fastLoginDialog, kickoutDialog, logoutDialog);
        flag = false;
        super.onDestroy();
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_home_menu:
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.tv_title:// debug模式下
                if (BuildConfig.DEBUG) {
                    showToast("当前登录账号为：" + Config.mAccount + ",GK状态：" + GKStateMannager.mRegisterGK);
                }
                break;
        }
    }

    @Override
    public void showProgress(String msg) {
        if (progressDialog == null){
            progressDialog = UIUtils.showProgressDialog(this, msg);
        }else {
            if (!progressDialog.isShowing())
                progressDialog.show();
        }
    }

    @Override
    public void dismissProgress() {
        UIUtils.dismissProgressDialog(progressDialog);
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.showShortToast(msg);
    }

    @Override
    public void reLoginNotGetUserInfo() {
        reLoginDialog = UIUtils.showReLoginDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reLogin();
            }
        });
    }

    /**
     * 重新登录
     */
    private void reLogin() {
        LoginActivity.startActivityClearTask(MainActivity.this);
        if (isRegisterUser) {
            SPUtil.clear(MainActivity.this);
            NIMClient.getService(AuthService.class).logout();
            TruetouchGlobal.logOff();
        }
    }

    @Override
    public void getUserInfoSuccess() {
        initFragment();
        if (isRegisterUser) {
            tv_menu_user_name.setText((String) SPUtil.get(this, SPKeyConstants.NAME, getString(R.string.user_name)));
            String ICON_URL = (String) SPUtil.get(this, SPKeyConstants.AVATAR, "");
            Log.i(TAG, "getUserInfoSuccess: avatar_url  " + ICON_URL);
            if(!TextUtils.isEmpty(ICON_URL)){
                Picasso.with(this).load(Constants.RESOURSE_HEAD + ICON_URL)
                        .error(R.drawable.default_icon).into(iv_user_icon);
                mPresenter.downloadAvatar(Constants.RESOURSE_HEAD + ICON_URL);
            }
            String times = getIntent().getStringExtra("times");
            if (!TextUtils.isEmpty(times)) {
                mPresenter.doWXPayController(times, mCartDao);
            }
        }
    }

    @Override
    public void fastLoginWithoutAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View prison_choose = View.inflate(this, R.layout.prison_input, null);
        builder.setView(prison_choose);
        Button bt_ok = (Button) prison_choose.findViewById(R.id.bt_ok);
        final AutoCompleteTv actv_prison_input = (AutoCompleteTv) prison_choose.findViewById(R.id.actv_prison_input);
        fastLoginDialog = builder.create();
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = actv_prison_input.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    showToast(getString(R.string.input_prison_name));
                } else {
                    Map<String, Integer> prison_map = actv_prison_input.getDataList();
                    if (prison_map.containsKey(content)) {
                        int jail_id = prison_map.get(content);
                        SPUtil.put(MainActivity.this, SPKeyConstants.JAIL_ID, jail_id);
                        fastLoginDialog.dismiss();
                        addHomeFragment();
                    } else {
                        showToast(getString(R.string.not_open_prison));
                    }
                }
            }
        });
        fastLoginDialog.show();
    }

    @Override
    public void accountKickout() {
        kickoutDialog = UIUtils.showKickoutDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reLogin();//
            }
        });
    }

    /**
     * 初始化fragment
     */
    @SuppressLint("CommitTransaction")
    private void initFragment() {
        fragments.clear();
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        if(homeFragment == null) {
            homeFragment = new HomeFragment();
            fragments.add(homeFragment);
        }
        if(homeFragment.isAdded()){
            transaction.remove(homeFragment);
        }
        transaction.add(R.id.main_content, homeFragment);
        if(remoteMeetFragment == null) {
            remoteMeetFragment = new RemoteMeetFragment();
            fragments.add(remoteMeetFragment);
        }
        if(remoteMeetFragment.isAdded()){
            transaction.remove(remoteMeetFragment);
        }
        transaction.add(R.id.main_content, remoteMeetFragment);
        if(canteenBaseFragment == null) {
            canteenBaseFragment = new CanteenBaseFragment();
            fragments.add(canteenBaseFragment);
        }
        if(remoteMeetFragment.isAdded()){
            transaction.remove(remoteMeetFragment);
        }
        transaction.add(R.id.main_content, canteenBaseFragment);
        transaction.show(homeFragment).hide(remoteMeetFragment)
                .hide(canteenBaseFragment);
        remoteMeetFragment.setUserVisibleHint(false);
        canteenBaseFragment.setUserVisibleHint(false);
        homeFragment.setUserVisibleHint(true);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 添加主Fragment
     */
    @SuppressLint("CommitTransaction")
    public void addHomeFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        if(homeFragment != null && homeFragment.isAdded()){
            transaction.remove(homeFragment);
        }
        if(homeFragment == null){
            homeFragment = new HomeFragment();
        }
        transaction.add(R.id.main_content, homeFragment);
        transaction.commit();
        // 设置侧拉的相关界面
        iv_user_icon.setImageResource(R.drawable.default_icon);
        tv_menu_user_name.setText(R.string.user_name);
        navigationView.getMenu().getItem(6).setTitle(getString(R.string.login_text));
    }

    /**
     * 切换并设置相关ui
     * @param index
     * @param title
     * @param menuVisibility
     * @param messageVisibility
     * @param data 切换fragment可携带bundle对象
     */
    private void switchUI(int index, String title, int menuVisibility, int messageVisibility, Bundle data) {
        NimInitUtil.checkStatus(this);
        getBalanceFromNet();
        switchFragment(index, data); // 切换fragment
        tv_title.setText(title);// 设置标题
        rl_home_menu.setVisibility(menuVisibility); // 设置菜单图标
    }

    private void getBalanceFromNet() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest api = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(this, SPKeyConstants.ACCESS_TOKEN, "");
        int family_id = (int) SPUtil.get(this, SPKeyConstants.FAMILY_ID, -1);
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);
        api.getBalance(header,family_id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Balances>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        Log.i(TAG, "get balance failed : " + e.getMessage());
                    }

                    @Override public void onNext(Balances result) {
                        Log.i(TAG, "get balance success : " + result.toString());
                        int code = result.code;
                        if(code == 200) {
                            SPUtil.put(MyApplication.getContext(), SPKeyConstants.USER_BALANCES, result.balance.balance);
                        }else {
                            android.util.Log.i(TAG, "onNext: failed_code ... " + code);
                        }
                    }
                });
    }

    /**
     *  切换fragment
     * @param index 索引
     */
    @SuppressLint("CommitTransaction")
    private void switchFragment(int index, Bundle data) {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        boolean isFirst = (boolean) SPUtil.get(MainActivity.this, SPKeyConstants.FIRST_CLICK, true);
        for (int i = 0; i < fragments.size(); i++) {
            if(index == i) {
                if (i == 1) {
                    android.util.Log.i(TAG, "switchFragment: showPopupWindow----" + isFirst);
                    if (isFirst) {
                        showPopupWindow();
                    }else {
                        transaction.show(fragments.get(index));
                        fragments.get(i).setUserVisibleHint(true);
                    }
                }else{
                    transaction.show(fragments.get(index));
                    fragments.get(i).setUserVisibleHint(true);
                }
            }else {
                transaction.hide(fragments.get(i));
                fragments.get(i).setUserVisibleHint(false);
            }
        }
        transaction.commit();
    }

    private PopupWindow mPopupWindow;
    private AlertDialog agreement_dialog;
    /**
     * TODO... 显示协议
     */
    private void showPopupWindow() {
        AlertDialog.Builder agreement_builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.contract_popupwindow, null, false);
        agreement_builder.setView(view);
        agreement_dialog = agreement_builder.create();
        agreement_dialog.setCancelable(true);
        TextView tv_agree = (TextView) view.findViewById(R.id.tv_agree);
        tv_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreement_dialog.dismiss();
                SPUtil.put(MainActivity.this, SPKeyConstants.FIRST_CLICK, false);
                switchFragment(1, null);
            }
        });
        agreement_dialog.show();

        /*View view = LayoutInflater.from(this).inflate(R.layout.contract_popupwindow, null, false);
        TextView tv_agree = (TextView) view.findViewById(R.id.tv_agree);
        TextView tv_contract_content = (TextView) view.findViewById(R.id.tv_contract_content);
        tv_contract_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        tv_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                SPUtil.put(MainActivity.this, SPKeyConstants.FIRST_CLICK, false);
                switchFragment(1);
            }
        });
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.popupwindow_background));
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE && !mPopupWindow.isFocusable()) {
                    //如果焦点不在popupWindow上，且点击了外面，不再往下dispatch事件：
                    //不做任何响应,不 dismiss popupWindow
                    return true;
                }

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        //设置背景透明度
//        backgroundAlpha(0.2f);
//
//        //添加pop窗口关闭事件
//        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha(1f);
//            }
//        });

        // 设置好参数之后再show
        mPopupWindow.showAtLocation(main_content, Gravity.CENTER_HORIZONTAL, 0, 0);*/
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        NimInitUtil.registerGK();
        switch (checkedId) {
            case R.id.rb_bottom_guide_home: // 首页
                switchUI(0, getString(R.string.main_page), View.VISIBLE, View.VISIBLE, null);
                break;
            case R.id.rb_bottom_guide_visit: // 探监
                boolean meeting = ((int) SPUtil.get(this, SPKeyConstants.MEETING, 1)) == 1;
                if (!meeting) {
                    showToast(getString(R.string.not_open_module));
                    return;
                }
                if (isRegisterUser) {
                    switchUI(1, getString(R.string.visit_prison), View.GONE, View.GONE, null);
                } else {
                    showToast(getString(R.string.enable_logined));
                }
                break;
            case R.id.rb_bottom_guide_canteen: // 电子商务
                boolean shopping = ((int) SPUtil.get(this, SPKeyConstants.SHOPPING, 1)) == 1;
                if (!shopping) {
                    showToast(getString(R.string.not_open_module));
                    return;
                }
                if (isRegisterUser) {
                    switchUI(2, getString(R.string.canteen), View.GONE, View.GONE, null);
                } else {
                    showToast(getString(R.string.enable_logined));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (progressDialog != null && progressDialog.isShowing())
            return;
        if (reLoginDialog != null && reLoginDialog.isShowing())
            return;
        if (kickoutDialog != null && kickoutDialog.isShowing())
            return;
        if (fastLoginDialog != null && fastLoginDialog.isShowing())
            return;
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        if (logoutDialog != null && logoutDialog.isShowing()) {
            logoutDialog.dismiss();
            return;
        }
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    private TextView tv_progress;
    private ProgressBar pb_update;
    private TextView tv_update;
    private TextView tv_cancel;
    private TextView tv_confirm;
    //    private DownloadProgressBar dpv_update;
    private AlertDialog dialog;//升级对话框
    private boolean download_successed = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        }
    };

    private VersionInfo versionInfo;

    /**
     * 更新对话框
     */
    public void showUpdateDialog(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View update_view = View.inflate(this, R.layout.update_dialog, null);
        tv_update = (TextView) update_view.findViewById(R.id.tv_update);
        tv_cancel = (TextView) update_view.findViewById(R.id.tv_cancel);
        tv_confirm = (TextView) update_view.findViewById(R.id.tv_confirm);
        tv_progress = (TextView) update_view.findViewById(R.id.tv_progress);
        pb_update = (ProgressBar) update_view.findViewById(R.id.pb_update);

        tv_update.setText("有新的版本,请更新");
        tv_progress.setText("");
        pb_update.setVisibility(View.GONE);
        builder.setView(update_view);
        if (!versionInfo.is_force) {
            tv_cancel.setVisibility(View.VISIBLE);
        }else{
            tv_cancel.setVisibility(View.GONE);
        }
        dialog = builder.create();
        dialog.show();

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadNewVersion();
                tv_confirm.setVisibility(View.GONE);
            }
        });

        /**
         * Intent intent = new Intent();
         intent.setAction("android.intent.action.VIEW");
         Uri content_url = Uri.parse(url);
         intent.setData(content_url);
         startActivity(intent);
         */
    }
    private void downloadNewVersion() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sd卡可用，用于存放下载的apk
            //1.下载
            String APK_URL = versionInfo.download;
            MainWrap.downloadFile(null, APK_URL, new SimpleObserver<ResponseBody>(){

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    String path = getExternalFilesDir(null) + File.separator + "yuwutong.apk";
                    File file = new File(path);
                    // 若文件已下载则直接安装
                    if(file.exists() && file.length() == responseBody.contentLength()) {
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        handler.postDelayed(install_apk_task, 500);
                        return;
                    }
                    boolean isSuccess = DownloadManager.writeResponseBodyToDisk(MainActivity.this, responseBody, new DownloadManager.ProgressListener(){
                        @Override
                        public void upDateProgress(long fileSizeDownloaded, long fileSize) {
                            int progress = (int) (fileSizeDownloaded * 100 / fileSize);
                            pb_update.setVisibility(View.VISIBLE);
                            tv_update.setText("正在更新: ");
                            pb_update.setMax(100);
                            pb_update.setProgress(progress);
                            tv_progress.setText(progress + "%");
                            Log.i("下载进度", fileSizeDownloaded + "----" + progress + "---" + fileSize);
                        }
                    });
                    if (isSuccess) {
                        DownloadManager.setProgressListener(null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            ToastUtil.showShortToast("下载成功");
                            dialog.dismiss();
                            }
                        });
                        handler.postDelayed(install_apk_task, 500);
                    }else {
                        android.util.Log.i(TAG, "onNext: failed=== ");
                    }
                }
            });
            /*HttpUtils httpUtils = new HttpUtils();
            Log.i(TAG, APK_URL);
            String path = Environment.getExternalStorageDirectory() + "/yuwutong-" + versionInfo.version_code + ".apk";
            android.util.Log.i(TAG, "downloadNewVersion: path==== " + path);
            File file = new File(path);
            // 若文件已下载则直接安装
            if(!file.exists()) {
                httpUtils.download(APK_URL, path, new RequestCallBack<File>() {
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        //2.安装apk
                        download_successed = true;
                        Log.i("变啦", "变啦" + download_successed);
                        dialog.dismiss();
                        handler.postDelayed(install_apk_task, 1000);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        e.printStackTrace();
                        Log.i("版本更新...", e.getMessage() + "----" + s);
                        ToastUtil.showShortToast("网络不好, 下载失败啦");
                        dialog.dismiss();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        int progress = (int) (current * 100 / total);
                        pb_update.setVisibility(View.VISIBLE);
                        tv_update.setText("正在更新: ");
                        pb_update.setMax(100);
                        pb_update.setProgress(progress);
                        tv_progress.setText(progress + "%");
                        Log.i("下载进度", current + "----" + progress + "---" + total);
                    }
                });
            }else {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                handler.postDelayed(install_apk_task, 1000);
            }*/
        } else {
            //sd卡不可用
            Toast.makeText(this, "sdcard不可用, 下载失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 安装apk任务
     */
    private Runnable install_apk_task = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(new File(getExternalFilesDir(null) + File.separator + "yuwutong.apk")),
                    "application/vnd.android.package-archive");
            MainActivity.this.startActivity(intent);
        }
    };

    public void onEvent(RechargeEvent event) {
        CanteenBaseFragment canteenBaseFragment = (CanteenBaseFragment) fragments.get(2);
        if (canteenBaseFragment.isInit) {
            canteenBaseFragment.changeUIbyclass(4);
        }else {
            Bundle data = new Bundle();
            data.putInt("leibie", 5); // 将时间及类别发送至商品展示fragment, 5表示亲情电话卡
            canteenBaseFragment.setData(data);
        }
        rb_bottom_guide_canteen.setChecked(true);
    }
}
