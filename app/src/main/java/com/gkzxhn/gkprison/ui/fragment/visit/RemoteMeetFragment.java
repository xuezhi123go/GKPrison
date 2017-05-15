package com.gkzxhn.gkprison.ui.fragment.visit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseFragmentNew;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.bean.Balances;
import com.gkzxhn.gkprison.utils.CustomUtils.MainUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.Log;
import com.gkzxhn.gkprison.utils.NomalUtils.NotificationsUtils;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.gkzxhn.gkprison.utils.NomalUtils.Utils;
import com.gkzxhn.gkprison.utils.event.RechargeEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * author:huangzhengneng
 * email:943852572@qq.com
 * date: 2016/8/2.
 * function:会见页面fragment
 */

public class RemoteMeetFragment extends BaseFragmentNew implements AdapterView.OnItemSelectedListener{

    private static final String[] REQUEST_TIME = Utils.afterNDay(30).toArray(new String[Utils.afterNDay(30).size()]);// 时间选择
    private static final String TAG = "RemoteMeetFragment";

    @BindView(R.id.rl_meeting) RelativeLayout rl_meeting;// 会见
    @BindView(R.id.tv_meeting_request_name) TextView tv_meeting_request_name;// 会见申请姓名
    @BindView(R.id.tv_meeting_request_id_num) TextView tv_meeting_request_id_num;// 会见申请身份证
    @BindView(R.id.tv_meeting_request_relationship) TextView tv_meeting_request_relationship;// 会见申请人与服刑人员关系
    @BindView(R.id.tv_meeting_request_phone) TextView tv_meeting_request_phone;// 会见申请电话号码
    @BindView(R.id.bs_meeting_request_time) Spinner sp_meeting_request_time;// 会见申请时间
    @BindView(R.id.tv_meeting_last_time) TextView tv_meeting_last_time;// 上次会见时间
    @BindView(R.id.bt_commit_request) Button bt_commit_request;// 提交会见申请按钮
    @BindView(R.id.tv_remotely_visit_num) TextView tv_remotly_num;
    @BindView(R.id.bt_remotely) TextView bt_recharge;

    private boolean isCommonUser;// 普通用户/监狱用户
    private int family_id = 0;
    private int vedionum;

    private String meeting_request_time = ""; // 会见申请时间
    private ProgressDialog dialog;
    private String id_num;// 身份证号
    private String name;// 姓名
    private String username;// 用户名
    private String relationship;// 与囚犯关系
    private String last_meeting_time;// 上次会见时间


    /**
     * 远程会见请求结果
     * @param result
     */
    private boolean checkRequestMeetResult(String result) {
        dialog.dismiss();
        bt_commit_request.setEnabled(true);
        int code = MainUtils.getJsonCode(result);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        View commit_success_dialog_view = View.inflate(getActivity(), R.layout.msg_ok_cancel_dialog, null);
        View view_01 = commit_success_dialog_view.findViewById(R.id.view_01);
        view_01.setVisibility(View.GONE);
        TextView tv_msg_dialog = (TextView) commit_success_dialog_view.findViewById(R.id.tv_msg_dialog);
        TextView tv_sub_msg = (TextView) commit_success_dialog_view.findViewById(R.id.tv_sub_msg);
        TextView tv_cancel = (TextView) commit_success_dialog_view.findViewById(R.id.tv_cancel);
        tv_cancel.setVisibility(View.GONE);
        TextView tv_ok = (TextView) commit_success_dialog_view.findViewById(R.id.tv_ok);
        builder.setView(commit_success_dialog_view);
        final AlertDialog commit_success_dialog = builder.create();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit_success_dialog.dismiss();
            }
        });
        if(code == 200) {
            //更新余额
            String balance = MainUtils.getJsonBalance(result);
            SPUtil.put(RemoteMeetFragment.this.getActivity(), SPKeyConstants.USER_BALANCES, balance);
            float a = Float.parseFloat(balance);
            int n = (int) a;
            vedionum = n / 50;
            tv_remotly_num.setText(String.valueOf(balance));
            android.util.Log.i(TAG, "checkRequestMeetResult: balance--- " + balance);
            //预支付金额
            String cost = MainUtils.getJsonCost(result);
            android.util.Log.i(TAG, "checkRequestMeetResult: balance--- " + cost);
//            tv_msg_dialog.setText(R.string.apply_meeting_success);
            tv_sub_msg.setVisibility(View.VISIBLE);
            tv_sub_msg.setText("如果未完成通话,预付金会自动退回到您的账户。");
            tv_msg_dialog.setText("    申请亲情电话成功, 已预支付" + cost + "元,可用余额" + balance + "元。");
            commit_success_dialog.show();
            String committed_meeting_time = (String) SPUtil.get(getActivity(),SPKeyConstants.COMMITTED_MEETING_TIME, "");
            android.util.Log.i(TAG, "checkRequestMeetResult: committed_meeting_time:   " + committed_meeting_time);
            android.util.Log.i(TAG, "checkRequestMeetResult: meeting_request_time:   " + meeting_request_time);
            SPUtil.put(getActivity(), SPKeyConstants.COMMITTED_MEETING_TIME, committed_meeting_time + meeting_request_time + "/");
            getBalanceFromNet();
            return true;
        }else {
            String reason = MainUtils.getApplyFailedResult(result);
            String reg = "[^\u4e00-\u9fa5]";
            reason = reason.replaceAll(reg, "");
            String text = getString(R.string.commit_failed_reason) + reason;
            tv_msg_dialog.setText(text);
            commit_success_dialog.show();
        }
        return false;
    }

    @Override
    protected void initUiAndListener(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_remote_meeting;
    }

    @Override
    protected void initData() {
        id_num = (String) SPUtil.get(getActivity(), SPKeyConstants.PASSWORD, "");
        family_id = (int) SPUtil.get(getActivity(), SPKeyConstants.FAMILY_ID, -1);
        isCommonUser = (boolean) SPUtil.get(getActivity(), SPKeyConstants.IS_COMMON_USER, false);
        getBalanceFromSp(); // 获取余额
        getBalanceFromNet();
        name = (String) SPUtil.get(getActivity(), SPKeyConstants.NAME, "");
        username = (String) SPUtil.get(getActivity(), SPKeyConstants.USERNAME, "");
        relationship = (String) SPUtil.get(getActivity(), SPKeyConstants.RELATION_SHIP, "");
        last_meeting_time = getString(R.string.last_meeting_time) + SPUtil.get(getActivity(),
                SPKeyConstants.LAST_MEETING_TIME, getString(R.string.no_meeting));
        if(isCommonUser){
            tv_meeting_request_name.setText(name);
            String start_ = id_num.substring(0, 5);
            String end_ = id_num.substring(id_num.length() - 4, id_num.length());
            tv_meeting_request_id_num.setText(start_ + "******" + end_);// 显示身份证前4位和后4位
            tv_meeting_request_phone.setText(username);
            tv_meeting_request_relationship.setText(relationship);
            tv_meeting_last_time.setText(last_meeting_time);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, REQUEST_TIME);
        sp_meeting_request_time.setAdapter(adapter);
        sp_meeting_request_time.setOnItemSelectedListener(this);
    }

    /**
     * 获取申请会见可用余额
     */
    private void getBalanceFromSp() {
        android.util.Log.i(TAG, "getBalanceFromSp: 获取余额... ");
        String balance = (String) SPUtil.get(MyApplication.getContext(), SPKeyConstants.USER_BALANCES, "");
        if (!TextUtils.isEmpty(balance)) {
            float a = Float.parseFloat(balance);
            int n = (int) a;
            vedionum = n / 50;
            tv_remotly_num.setText(String.valueOf(vedionum));
        }
    }

    /**
     * 从服务器获取可用余额
     */
    private void getBalanceFromNet() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest api = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(getActivity(), SPKeyConstants.ACCESS_TOKEN, "");
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);
        api.getBalance(header,family_id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Balances>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        Log.i(TAG, "get balance failed : " + e.getMessage());
                        vedionum = 0;
                        tv_remotly_num.setText(vedionum + "");
                    }

                    @Override public void onNext(Balances result) {
                        Log.i(TAG, "get balance success : " + result.toString());
                        int code = result.code;
                        if(code == 200) {
                            Balances.BalanceBean bean =  result.balance;
                            String balance = bean.balance;
                            SPUtil.put(RemoteMeetFragment.this.getActivity(), SPKeyConstants.USER_BALANCES, balance);
                            float a = Float.parseFloat(balance);
                            int n = (int) a;
                            vedionum = n / 50;
                        }else {
                            vedionum = 0;
                        }
                        tv_remotly_num.setText(String.valueOf(vedionum));
                    }
                });
    }

    /**
     * 设置上次会见时间
     */
    public void setLastMeetingTime(){
        String text = getString(R.string.last_meeting_time) + SPUtil.get(getActivity(),
                SPKeyConstants.LAST_MEETING_TIME, getString(R.string.no_meeting));
        tv_meeting_last_time.setText(text);
    }

    @OnClick({R.id.bt_commit_request, R.id.bt_remotely})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_commit_request:
                if(isCommonUser) {
                    if(!"请选择日期".equals(meeting_request_time)) {
                        String committed_meeting_time = (String) SPUtil.get(getActivity(), SPKeyConstants.COMMITTED_MEETING_TIME, "");
                        if(committed_meeting_time.contains(meeting_request_time)){
                            showToastMsgLong(getString(R.string.requested_choose_other));
                            return;
                        }else if (vedionum == 0){
                            showToastMsgShort(getString(R.string.balance_insufficient));
                            return;
                        }else {
                            sendMeetingRequestToServer();
                        }
                    }else {
                        showToastMsgShort(getString(R.string.choose_meeting_time));
                        return;
                    }
                }else {
                    showToastMsgShort(getString(R.string.please_pre_login));
                }
                break;

            case R.id.bt_remotely:
                EventBus.getDefault().post(new RechargeEvent());
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 发送探监申请至服务器
     */
    private void sendVisitRequestToServer() {

    }

    /**
     * 发送会见申请至服务器
     */
    private void sendMeetingRequestToServer() {
        if(!SystemUtil.isNetWorkUnAvailable()) {
            bt_commit_request.setEnabled(false);
            showProgressDialog();
            String param = "{\"meeting\":{\"application_date\":\"" + meeting_request_time+"\",\"family_id\":"+SPUtil.get(getActivity(),SPKeyConstants.FAMILY_ID, -1)+"}}";
            Log.i(TAG, param);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.URL_HEAD)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), param);
            ApiRequest api = retrofit.create(ApiRequest.class);
            String token= (String) SPUtil.get(getActivity(), SPKeyConstants.ACCESS_TOKEN, "");
            Map<String, String> header = new HashMap<>();
            header.put("authorization", token);
            header.put("Content-Type:application/json", "Accept:application/json");
            api.sendMeetingRequest(header, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "send meet request failed : " + e.getMessage());
                            showToastMsgLong(getString(R.string.commit_execption_retry));
                            bt_commit_request.setEnabled(true);
                            dialog.dismiss();
                        }

                        @Override
                        public void onNext(ResponseBody response) {
                            try {
                                String result = response.string();
                                Log.i(TAG, "send meet request success : " + result);
                                boolean b = checkRequestMeetResult(result);
                                if (b) {
                                    checkNotificationStatus();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, "send meet request exception : " + e.getMessage());
                            }
                        }
                    });
        }else {
            showToastMsgShort(getString(R.string.network_unavailable));
        }
    }

    /**
     * 检查通知栏权限状态
     */
    private void checkNotificationStatus() {
        if (NotificationsUtils.isNotificationEnabled(getActivity())) {
            return;
        }else {
            UIUtils.showAlertDialog(getActivity(), "请您在\"通知管理\"中开启应用通知权限,以便审核通过时能及时地通知到您", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    NotificationsUtils.requestPermission(getActivity(), 0);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    /**
     * 初始化并显示加载进度条对话框
     */
    private void showProgressDialog() {
        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("正在提交，请稍后");
        dialog.show();
    }

    boolean isSpinnerFirst = true ;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /*if (isSpinnerFirst) {
            //第一次初始化spinner时，不显示默认被选择的第一项即可
            view.setVisibility(View.INVISIBLE) ;
            isSpinnerFirst = false ;
            return;
        }*/
        if (parent.getId() == R.id.bs_meeting_request_time){
            meeting_request_time = REQUEST_TIME[position];
        }
    }
    @Override public void onNothingSelected(AdapterView<?> parent) {}


}
