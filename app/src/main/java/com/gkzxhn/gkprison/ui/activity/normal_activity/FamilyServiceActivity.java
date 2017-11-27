package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.dao.GreenDaoHelper;
import com.gkzxhn.gkprison.model.dao.bean.CartDao;
import com.gkzxhn.gkprison.model.dao.bean.LineItemsDao;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.api.wrap.MainWrap;
import com.gkzxhn.gkprison.model.net.bean.AA;
import com.gkzxhn.gkprison.model.net.bean.AwardPunishInfo;
import com.gkzxhn.gkprison.model.net.bean.Line_items_attributes;
import com.gkzxhn.gkprison.model.net.bean.Order;
import com.gkzxhn.gkprison.model.net.bean.PrisonerDetail;
import com.gkzxhn.gkprison.model.net.bean.SentenceChange;
import com.gkzxhn.gkprison.ui.pay.PaymentActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.MainUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.OkHttpUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.ListViewParamsUtils;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.StringUtils;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;
import com.gkzxhn.gkprison.utils.event.RechargeEvent;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 家属服务
 */
public class FamilyServiceActivity extends BaseActivityNew {

    private static final String TAG = FamilyServiceActivity.class.getSimpleName();

    @BindView(R.id.el_items) ExpandableListView el_items;
    @BindView(R.id.tv_title) TextView tv_title;// 标题
    @BindView(R.id.rl_back) RelativeLayout rl_back;// 返回
    @BindView(R.id.tv_remittance) TextView tv_remittance;// 汇款
    @BindView(R.id.tv_balance_money) TextView tv_balance_money;// 余额
    @BindView(R.id.tv_current_month_available_money) TextView tv_current_month_available_money;// 本月可用余额

    @BindView(R.id.tv_name) TextView tv_name;// 姓名
    @BindView(R.id.tv_prisoner_start_time) TextView tv_prisoner_start_time;// 入监日期
    @BindView(R.id.tv_original_prison_term) TextView tv_original_prison_term;// 原判刑期
    @BindView(R.id.tv_sentence_time_start) TextView tv_sentence_time_start;// 原判刑期起日
    @BindView(R.id.tv_fujiaxing) TextView tv_fujiaxing;// 附加刑
    @BindView(R.id.tv_prison_num) TextView tv_prison_num;// 罪犯编号
    @BindView(R.id.tv_crime_type) TextView tv_crime_type;// 罪名
    @BindView(R.id.tv_remain_prison_term) TextView tv_remain_prison_term;// 剩余刑期
    @BindView(R.id.tv_sentence_time_end) TextView tv_sentence_time_end;// 原判刑期止日
    @BindView(R.id.tv_accumulated) TextView tv_accumulated;// 累计减刑
    @BindView(R.id.tv_last_reduce) TextView tv_last_reduce;// 上次减刑时间

    private ProgressDialog getInfoDialog;
    private Subscription getInfoSub;
    private Subscription getOrderNoSub;

    private String TradeNo;
    private String times = "";
//    private SQLiteDatabase database;
    private String money = "";
    private List<Line_items_attributes> line_items_attributes = new ArrayList<>();
    private int jail_id;

    private List<Integer> image_messge = new ArrayList<Integer>() {
        {
            boolean prisonterm = ((int) SPUtil.get(MyApplication.getContext(), SPKeyConstants.PRISONTERM, 1)) == 1;
            if (prisonterm) {
                add(R.drawable.sentence);
            }
            boolean rewards = ((int) SPUtil.get(MyApplication.getContext(), SPKeyConstants.REWARDS, 1)) == 1;
            if (rewards) {
                add(R.drawable.consumption);
            }
//            add(R.drawable.buy);
        }
    };

    private List<String> text_messge = new ArrayList<String>() {
        {
            add("刑期变动");
            add("奖励惩罚");
            add("消费记录");
            add("购物签收");
        }
    };
    private List<String> sentence_time = new ArrayList<String>(){
        {
            add("2017-05-30");
            add("2017-08-20");
            add("2017-09-18");
        }
    };
    //奖惩时间
    private List<String> re_pun_time = new ArrayList<String>(){
        {
            add("2017-03-23");
            add("2017-06-20");
            add("2017-08-06");
        }
    };
    //奖惩类型
    private List<String> re_pun_type = new ArrayList<String>(){
        {
            add("奖励");
            add("奖励");
            add("惩罚");
        }
    };
    //奖惩内容
    private List<String> re_pun_content = new ArrayList<String>(){
        {
            add("水杯");
            add("牙刷");
            add("除草");
        }
    };
    private List<String> sentence_cause = new ArrayList<String>(){
        {
            add("制止狱内暴力");
            add("制止狱内暴力");
            add("制止狱内暴力");
        }
    };
    private List<String> sentence_time_add = new ArrayList<String>(){
        {
            add("减刑三个月");
            add("减刑三个月");
            add("减刑三个月");
        }
    };
    private List<String> sentence_after = new ArrayList<String>(){
        {
            add("十一年九个月");
            add("十一年六个月");
            add("十一年三个月");
        }
    };
    private List<String> buyer_id = new ArrayList<String>(){
        {
            add("1232423423423");
            add("1232423423423");
            add("1232423423423");
        }
    };
    private List<String> money1 = new ArrayList<String>(){
        {
            add("120元");
            add("120元");
            add("120元");
        }
    };
    private List<String> commodity = new ArrayList<String>(){
        {
            add("水杯");
            add("水杯");
            add("水杯");
        }
    };
    private CartDao mCartDao;
    private LineItemsDao mLineItemsDao;
    private SentenceAdapter mSentenceAdapter;
    private RewardAndPunishAdapter mRewardPunishAdapter;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_family_service;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        el_items.setGroupIndicator(null);
        tv_title.setText(R.string.family_server);
//        tv_remittance.setVisibility(View.VISIBLE);
        rl_back.setVisibility(View.VISIBLE);
//        database = StringUtils.getSQLiteDB(this);
        mCartDao = GreenDaoHelper.getDaoSession().getCartDao();
        mLineItemsDao = GreenDaoHelper.getDaoSession().getLineItemsDao();
        jail_id = (int) SPUtil.get(this, SPKeyConstants.JAIL_ID, 0);
        MyAdapter adapter = new MyAdapter();
        mSentenceAdapter = new SentenceAdapter();//刑期变动adapter
        mRewardPunishAdapter = new RewardAndPunishAdapter();//奖励惩罚adapter


        el_items.setAdapter(adapter);
        String prisoner_number = (String) getSPValue(SPKeyConstants.PRISONER_NUMBER, "");
//        prisoner_number = "4000001";
        getPrisonerInformation(prisoner_number);

        getSentenceChangeInfo(prisoner_number);

        getAwardPunishInfo(prisoner_number);
    }

    /**
     * 获取奖惩信息
     * @param prisoner_number
     */
    private void getAwardPunishInfo(String prisoner_number) {
        MainWrap.getAwardPunish(Long.parseLong(prisoner_number), new SimpleObserver<AwardPunishInfo>(){
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(AwardPunishInfo awardPunishInfo) {
                super.onNext(awardPunishInfo);
                mRewardPunishAdapter.setData(awardPunishInfo.data);
                mRewardPunishAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取刑期变动信息
     * @param prisoner_number
     */
    private void getSentenceChangeInfo(String prisoner_number) {
        MainWrap.getSentenceChange(Long.parseLong(prisoner_number), new SimpleObserver<SentenceChange>(){
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(SentenceChange sentenceChange) {
                super.onNext(sentenceChange);
                mSentenceAdapter.setData(sentenceChange.data);
                mSentenceAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override protected void initInjector() {}

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
        RxUtils.unSubscribe(getInfoSub, getOrderNoSub);
        UIUtils.dismissProgressDialog(getInfoDialog);
        super.onDestroy();
    }

    /**
     * 获取囚犯数据
     * @param prisoner_number
     */
    private void getPrisonerInformation(String prisoner_number) {
        String prison_term_started_at = (String) getSPValue(SPKeyConstants.PRISON_TERM_STARTED_AT, "");
        final String prison_term_ended_at = (String) getSPValue(SPKeyConstants.PRISON_TERM_ENDED_AT, "");
        String gender = (String) getSPValue(SPKeyConstants.GENDER, "");
        final String prisoner_crimes = (String) getSPValue(SPKeyConstants.PRISONER_CRIMES, "");
        String name = (String)getSPValue(SPKeyConstants.PRISONER_NAME, "李新开");

        tv_prison_num.setText(prisoner_number);

        tv_name.setText(name);

        tv_crime_type.setText(prisoner_crimes);

        MainWrap.getPrisonerDetail(Long.parseLong(prisoner_number), new SimpleObserver<PrisonerDetail>(){
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: prisonerDetail : " + e.getMessage());
                super.onError(e);
            }

            @Override
            public void onNext(PrisonerDetail prisonerDetail) {
                super.onNext(prisonerDetail);
                Log.i(TAG, "onNext: prisonerDetail : " + prisonerDetail);
                String startTime = prisonerDetail.data.started_at;
                String endedTime = prisonerDetail.data.ended_at;
                int remainYear = 0;
                int remainMonth = 0;
                int remainDay = 0;
                try {
                    remainYear = Integer.parseInt(prisonerDetail.data.sentence_year);
                    remainMonth = Integer.parseInt(prisonerDetail.data.sentence_month);
                    remainDay = Integer.parseInt(prisonerDetail.data.sentence_day);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                StringBuffer remain = StringUtils.getYearMonthDay(remainYear, remainMonth, remainDay);
                tv_remain_prison_term.setText(remain);
                if (TextUtils.isEmpty(startTime)) {
                }else {
                    tv_prisoner_start_time.setText(startTime);
                    tv_sentence_time_start.setText(startTime);
                }
                if (TextUtils.isEmpty(endedTime)) {
                }else {
                    tv_sentence_time_end.setText(endedTime);
                    StringBuffer timeStringBetween = null;
                    try {
                        timeStringBetween = StringUtils.getTimeStringBetween(startTime, endedTime, "yyyy-MM-dd");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(timeStringBetween)) {
                        timeStringBetween = new StringBuffer("undefind");
                    }
                    tv_original_prison_term.setText(timeStringBetween);
                }
                String punishment = prisonerDetail.data.additional_punishment;
                if (!TextUtils.isEmpty(punishment)) {
                    tv_fujiaxing.setText(punishment);
                }
                String prisonerNumber = prisonerDetail.data.prisoner_number;
                if (!TextUtils.isEmpty(prisonerNumber)) {
                    tv_prison_num.setText(prisonerNumber);
                }

                String crimes = prisonerDetail.data.crimes;
                if (!TextUtils.isEmpty(crimes)) {
                    tv_crime_type.setText(crimes);
                }
                String times = String.valueOf(prisonerDetail.data.times);
                if (!TextUtils.isEmpty(times)) {
                    tv_accumulated.setText(times);
                }

                String last_time = prisonerDetail.data.term_start;
                if (!TextUtils.isEmpty(last_time) && last_time.length()>=10) {
                    tv_last_reduce.setText(last_time.substring(0, 10));
                }else {
                    tv_last_reduce.setText("undefind");
                }
            }
        });

/*        if (!SystemUtil.isNetWorkUnAvailable()) {
            getInfoDialog = UIUtils.showProgressDialog(this, "");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.URL_HEAD)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiRequest apiRequest = retrofit.create(ApiRequest.class);
            String token = (String) SPUtil.get(this, SPKeyConstants.ACCESS_TOKEN, "");
            Map<String, String> header = new HashMap<>();
            header.put("authorization", token);
            getInfoSub = apiRequest.getFamilyServerInfo(header,token).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new SimpleObserver<FamilyServerBean>(){
                @Override public void onError(Throwable e) {
                    UIUtils.dismissProgressDialog(getInfoDialog);
                    Log.i(TAG, "get prisoner info failed: " + e.getMessage());
                    ToastUtil.showShortToast(getString(R.string.load_data_failed));
                }

                @Override public void onNext(FamilyServerBean familyServerBean) {
                    UIUtils.dismissProgressDialog(getInfoDialog);
                    Log.i(TAG, "get prisoner info success: " + familyServerBean.toString());
                    if (familyServerBean.getCode() == 200 && familyServerBean.getPrisoner() != null){
                        tv_prison_num.setText(familyServerBean.getPrisoner().getPrisoner_number());
                        if (familyServerBean.getPrisoner().getGender().equals("m")) {
                            prisoner_name.setText(getString(R.string.man));
                        } else {
                            prisoner_name.setText(getString(R.string.woman));
                        }
                        tv_crime_type.setText(familyServerBean.getPrisoner().getCrimes());
                        tv_sentence_time_start.setText(familyServerBean.getPrisoner().getPrison_term_started_at());
                        prison_end_time.setText(familyServerBean.getPrisoner().getPrison_term_ended_at());
                    }else {
                        ToastUtil.showShortToast(getString(R.string.load_data_failed));
                    }
                }
            });
        } else {
            ToastUtil.showShortToast(getString(R.string.net_broken));
        }*/
    }

    @OnClick({R.id.tv_remittance, R.id.rl_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_remittance:
                /*AlertDialog.Builder builder = new AlertDialog.Builder(FamilyServiceActivity.this);
                View view = View.inflate(this, R.layout.remittance_dialog, null);
                final EditText et_money = (EditText) view.findViewById(R.id.et_money);
                Editable ea = et_money.getText();
                et_money.setSelection(ea.length());
                TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
                TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, true);
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        money = et_money.getText().toString();
                        if (TextUtils.isEmpty(money)) {
                            ToastUtil.showShortToast(getString(R.string.input_remittance_count));
                            try {
                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                field.setAccessible(true);
                                field.set(dialog, false);
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            getOrderNoFromServer();
                            times = StringUtils.formatTime("yyyy-MM-dd HH:mm:ss");
                            Cart cart = new Cart();
                            cart.setTime(times);
                            cart.setOut_trade_no(TradeNo);
                            cart.setIsfinish(false);
                            cart.setTotal_money(money);
                            cart.setRemittance(true);
                            mCartDao.insert(cart);

                            Cart cart1 = mCartDao.queryBuilder().where(CartDao.Properties.Time.eq(times)).build().unique();
                            if (cart1 != null) {
                                LineItems lineItems = new LineItems();
                                lineItems.setCart_id(cart1.getId());
                                lineItems.setItems_id(99999);
                                mLineItemsDao.insert(lineItems);
                            }

                            *//*String sql = "insert into Cart(time,out_trade_no,isfinish,total_money,remittance) values('" + times + "','" + TradeNo + "',0,'" + money + "',1)";
                            database.execSQL(sql);
                            int cart_id = 0;
                            String sql1 = "select id from CartInfo where time = '" + times + "'";
                            Cursor cursor = database.rawQuery(sql1, null);
                            while (cursor.moveToNext()) {
                                cart_id = cursor.getInt(cursor.getColumnIndex("id"));
                            }
                            String sql2 = "insert into line_items(Items_id,cart_id) values (9999," + cart_id + ")";
                            database.execSQL(sql2);
                            cursor.close();*//*
                        }
                    }
                });
                dialog.show();*/
                finish();
                EventBus.getDefault().post(new RechargeEvent());
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }

    /**
     * 获取订单号
     */
    private void getOrderNoFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiRequest apiRequest = retrofit.create(ApiRequest.class);
        String token = (String) SPUtil.get(FamilyServiceActivity.this, SPKeyConstants.ACCESS_TOKEN, "");
        Map<String, String> header = new HashMap<>();
        header.put("authorization", token);
        getOrderNoSub = apiRequest.getOrderInfo(header,jail_id, token, OkHttpUtils.getRequestBody(getRequestBody()))
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<ResponseBody>(){
                    @Override public void onError(Throwable e) {
                        UIUtils.dismissProgressDialog(getInfoDialog);
                        ToastUtil.showShortToast(getString(R.string.get_order_failed));
                        Log.i(TAG, "get order number failed: " + e.getMessage());
                    }

                    @Override public void onNext(ResponseBody responseBody) {
                        UIUtils.dismissProgressDialog(getInfoDialog);
                        try {
                            String result = responseBody.string();
                            int pass_code = MainUtils.getResultCode(result);
                            if (pass_code == 200) {
                                TradeNo = MainUtils.getResultTradeNo(result);
                                Intent intent = new Intent(FamilyServiceActivity.this, PaymentActivity.class);
                                intent.putExtra("totalmoney", money);
                                intent.putExtra("times", times);
                                intent.putExtra("TradeNo", TradeNo);
                                intent.putExtra("saletype", "汇款");
                                startActivity(intent);
                            }else {
                                // 其他情况就是等于500  超出每月800额度
                                // {"code":500,"msg":"Create order failed","errors":{"order":["超出每月800元限额"]}}
                                ToastUtil.showShortToast(getString(R.string.out_of_800));
                            }
                        } catch (IOException e) {
                            ToastUtil.showShortToast(getString(R.string.get_order_failed));
                            Log.i(TAG, "get order number exception: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * 获取请求体
     * @return
     */
    private String getRequestBody() {
        int family_id = (int) SPUtil.get(FamilyServiceActivity.this, SPKeyConstants.FAMILY_ID, -1);
        Log.i(TAG, "getRequestBody: family_id === " + family_id);
        Order order = new Order();
        order.setFamily_id(family_id);
        Line_items_attributes lineitemsattributes = new Line_items_attributes();
        lineitemsattributes.setItem_id(9999);
        lineitemsattributes.setQuantity(1);
        line_items_attributes.add(lineitemsattributes);
        order.setLine_items_attributes(line_items_attributes);
        order.setJail_id(jail_id);
        order.setCreated_at(times);
        order.setAmount(Float.parseFloat(money));
        AA aa = new AA();
        aa.setOrder(order);
        Log.i(TAG, "getRequestBody: aa====  " + aa);
        return new Gson().toJson(aa);
    }

    private class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return image_messge.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.familyservice_item, null);
                viewHolder = new GroupViewHolder();
                viewHolder.image_click = (ImageView) convertView.findViewById(R.id.image_click);
                viewHolder.img_messge = (ImageView) convertView.findViewById(R.id.image_messge);
                viewHolder.tv_messge = (TextView) convertView.findViewById(R.id.tv_messge);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GroupViewHolder) convertView.getTag();
            }
            if (isExpanded) {
                viewHolder.image_click.setImageResource(R.drawable.touchup);
            } else {
                viewHolder.image_click.setImageResource(R.drawable.touchdown);
            }
            viewHolder.img_messge.setImageResource(image_messge.get(groupPosition));
            viewHolder.tv_messge.setText(text_messge.get(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (groupPosition == 0) {
                convertView = View.inflate(getApplicationContext(), R.layout.sentence_change, null);
                ListView lv_sentence = (ListView)convertView.findViewById(R.id.lv_sentence_recod);
                 lv_sentence.setAdapter(mSentenceAdapter);
                ListViewParamsUtils.setListViewHeightBasedOnChildren(lv_sentence);
            } else if (groupPosition == 1) {
                //奖励惩罚
                convertView = View.inflate(getApplicationContext(), R.layout.sentence_change, null);
                  ListView lv_consumption = (ListView)convertView.findViewById(R.id.lv_sentence_recod);
                   lv_consumption.setAdapter(mRewardPunishAdapter);
                 ListViewParamsUtils.setListViewHeightBasedOnChildren(lv_consumption);
            } /*else if (groupPosition == 2) {
                convertView = View.inflate(getApplicationContext(), R.layout.shoppingreceipt, null);
                  ListView lv_shopping = (ListView)convertView.findViewById(R.id.lv_shopping);
                ReceiptAdapter adapter = new ReceiptAdapter();
                  lv_shopping.setAdapter(adapter);
                   ListViewParamsUtils.setListViewHeightBasedOnChildren(lv_shopping);
            }*/
            TextView textView = new TextView(FamilyServiceActivity.this);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setPadding(0, 20, 0, 20);
            textView.setText(R.string.upcoming);
            textView.setGravity(Gravity.CENTER);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private class GroupViewHolder {
        ImageView img_messge;
        TextView tv_messge;
        ImageView image_click;

    }

    private class SentenceAdapter extends BaseAdapter {

        private List<SentenceChange.DataBean> mDatas = new ArrayList<>();

        @Override
        public int getCount() {
            return mDatas.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.sentence_change_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_sentence_time = (TextView) convertView.findViewById(R.id.tv_sentence_time);
                viewHolder.tv_sentence_case = (TextView) convertView.findViewById(R.id.tv_sentence_case);
                viewHolder.tv_sentence_add = (TextView) convertView.findViewById(R.id.tv_sentence_add);
                viewHolder.tv_after = (TextView) convertView.findViewById(R.id.tv_after);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHolder.tv_sentence_time.setText("变动后止日");
                viewHolder.tv_sentence_case.setText("变动类型");
                viewHolder.tv_sentence_add.setText("变动幅度");
                viewHolder.tv_after.setText("变动后刑期");
            } else {
                String term_finish = mDatas.get(position - 1).term_finish;
                if (!TextUtils.isEmpty(term_finish) && term_finish.length() >= 10) {
                    viewHolder.tv_sentence_time.setText(term_finish.substring(0, 10));
                }
                String changetype = mDatas.get(position - 1).changetype;
                if (!TextUtils.isEmpty(changetype)) {
                    viewHolder.tv_sentence_case.setText(changetype);
                }
                int changeyear = mDatas.get(position - 1).changeyear;
                int changemonth = mDatas.get(position - 1).changemonth;
                int changeday = mDatas.get(position - 1).changeday;
                StringBuffer change = StringUtils.getYearMonthDay(changeyear, changemonth, changeday);
                viewHolder.tv_sentence_add.setText(change);

                int sentence_year = 0;
                int sentence_month = 0;
                int sentence_day = 0;
                try {
                    sentence_year = Integer.parseInt(mDatas.get(position - 1).sentence_year);
                    sentence_month = Integer.parseInt(mDatas.get(position - 1).sentence_month);
                    sentence_day = Integer.parseInt(mDatas.get(position - 1).sentence_day);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                StringBuffer sentence = StringUtils.getYearMonthDay(sentence_year, sentence_month, sentence_day);
                viewHolder.tv_after.setText(sentence);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView tv_sentence_time;
            TextView tv_sentence_case;
            TextView tv_sentence_add;
            TextView tv_after;
        }

        public void setData(List<SentenceChange.DataBean> datas){
            mDatas = datas;
        }
    }

    private class RewardAndPunishAdapter extends BaseAdapter {

        private List<AwardPunishInfo.DataBean> mData = new ArrayList<>();

        @Override
        public int getCount() {
            return mData.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.sentence_re_pun, null);
                viewHolder = new ViewHolder();
                viewHolder.buy_time = (TextView) convertView.findViewById(R.id.tv_sentence_time);
                viewHolder.buy_commodity = (TextView) convertView.findViewById(R.id.tv_sentence_case);
                viewHolder.buy_money = (TextView) convertView.findViewById(R.id.tv_sentence_add);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHolder.buy_time.setText("奖惩时间");
                viewHolder.buy_commodity.setText("奖惩类型");
                viewHolder.buy_money.setText("奖惩内容");
            } else {
                viewHolder.buy_time.setText(mData.get(position - 1).datayear + "");
                viewHolder.buy_commodity.setText(mData.get(position - 1).rp_type + "");
                viewHolder.buy_money.setText(mData.get(position - 1).ndry + "");
            }
            return convertView;
        }

        public void setData(List<AwardPunishInfo.DataBean> data) {
            mData = data;
        }

        private class ViewHolder {
            TextView buy_time;
            TextView buy_commodity;
            TextView buy_money;
        }
    }

    private class ConsumptionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.sentence_change_item, null);
                viewHolder = new ViewHolder();
                viewHolder.buy_time = (TextView) convertView.findViewById(R.id.tv_sentence_time);
                viewHolder.buy_commodity = (TextView) convertView.findViewById(R.id.tv_sentence_case);
                viewHolder.buy_money = (TextView) convertView.findViewById(R.id.tv_sentence_add);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHolder.buy_time.setText("购买时间");
                viewHolder.buy_commodity.setText("商品");
                viewHolder.buy_money.setText("金额");
            } else {
                viewHolder.buy_time.setText(sentence_time.get(position-1));
                viewHolder.buy_commodity.setText(commodity.get(position-1));
                viewHolder.buy_money.setText(money1.get(position-1));
            }
            return convertView;
        }

        private class ViewHolder {
            TextView buy_time;
            TextView buy_commodity;
            TextView buy_money;
        }
    }

    private class ReceiptAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.shoppingreceipt_item, null);
                viewHolder = new ViewHolder();
                viewHolder.receipt = (ImageView) convertView.findViewById(R.id.image_receipt);
                viewHolder.qianshou = (TextView) convertView.findViewById(R.id.tv_qianshou);
                viewHolder.qianshou_time = (TextView) convertView.findViewById(R.id.tv_receipt_time);
                viewHolder.qianshou_id = (TextView) convertView.findViewById(R.id.tv_buy_id);
                viewHolder.qianshou_money = (TextView) convertView.findViewById(R.id.tv_money);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHolder.qianshou.setText("确认签收");
                viewHolder.qianshou_time.setText("签收时间");
                viewHolder.qianshou_id.setText("购物ID");
                viewHolder.qianshou_money.setText("购物数值");
                viewHolder.receipt.setVisibility(View.GONE);
            } else {
                viewHolder.receipt.setVisibility(View.VISIBLE);
                viewHolder.qianshou_time.setText(sentence_time.get(position-1));
                viewHolder.qianshou_id.setText(buyer_id.get(position-1));
                viewHolder.qianshou_money.setText(money1.get(position-1));
            }
            return convertView;
        }

        private class ViewHolder {
            TextView qianshou_time;
            TextView qianshou_id;
            TextView qianshou_money;
            TextView qianshou;
            ImageView receipt;
        }
    }
}
