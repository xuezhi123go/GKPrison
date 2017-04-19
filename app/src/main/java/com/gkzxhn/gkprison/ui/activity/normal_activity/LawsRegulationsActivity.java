package com.gkzxhn.gkprison.ui.activity.normal_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.BaseActivityNew;
import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.ApiRequest;
import com.gkzxhn.gkprison.model.net.bean.Laws;
import com.gkzxhn.gkprison.utils.CustomUtils.RxUtils;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.SystemUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/21
 * Email:943852572@qq.com
 * Description:法律法规页面
 */
public class LawsRegulationsActivity extends BaseActivityNew {

    private static final String TAG = LawsRegulationsActivity.class.getSimpleName();
    @BindView(R.id.lv_laws_regulations) ListView lv_laws_regulations;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.rl_back) RelativeLayout rl_back;

    private ProgressDialog dialog;
    private Subscription getLawsSubscription;

    private Laws laws;

    @Override
    public int setLayoutResId() {
        return R.layout.activity_laws_regulations;
    }

    @Override
    protected void initUiAndListener() {
        ButterKnife.bind(this);
        tv_title.setText(R.string.laws_regulations);
        rl_back.setVisibility(View.VISIBLE);
        getLawsAndRegulations();
    }

    @OnItemClick(R.id.lv_laws_regulations)
    public void onItemClickListener(int position){
        if (laws != null && laws.getLaws().size() > 0) {
            int i = laws.getLaws().get(position).getId();
            Intent intent = new Intent(LawsRegulationsActivity.this, LawsDetailActivity.class);
            intent.putExtra("id", i);
            startActivity(intent);
        }
    }

    @OnClick(R.id.rl_back)
    public void onClick(){
        finish();
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
        UIUtils.dismissProgressDialog(dialog);
        RxUtils.unSubscribe(getLawsSubscription);
        super.onDestroy();
    }

    /**
     * 获取法律法规
     */
    private void getLawsAndRegulations() {
        if (!SystemUtil.isNetWorkUnAvailable()){
            dialog = UIUtils.showProgressDialog(this, "");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.URL_HEAD)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiRequest apiRequest = retrofit.create(ApiRequest.class);
             int jail_id = (int) SPUtil.get(this, SPKeyConstants.JAIL_ID, 0);
            String token = (String) SPUtil.get(this, SPKeyConstants.ACCESS_TOKEN, "");
            getLawsSubscription = apiRequest.getLawsAndRegulationsList(jail_id).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SimpleObserver<Laws>(){
                @Override public void onError(Throwable e) {
                    UIUtils.dismissProgressDialog(dialog);
                    ToastUtil.showShortToast(getString(R.string.load_data_failed));
                    Log.i(TAG, "get data failed: " + e.getMessage());
                }

                @Override public void onNext(Laws laws) {
                    Log.i(TAG, "get data result: " + laws.getError() + ", size: " + laws.getLaws().size());
                    UIUtils.dismissProgressDialog(dialog);
                    if (laws.getError() == 0){// 成功返回
                        if (laws.getLaws().size() > 0){
                            LawsRegulationsActivity.this.laws = laws;
                            lv_laws_regulations.setAdapter(new MyAdapter(laws.getLaws()));
                        }else {
                            ToastUtil.showShortToast(getString(R.string.no_data));
                        }
                    }else {
                        ToastUtil.showShortToast(getString(R.string.load_data_failed));
                    }
                }
            });
        }else {
            ToastUtil.showShortToast(getString(R.string.net_broken));
        }
    }

    private class MyAdapter extends BaseAdapter {

        private List<Laws.LawsBean> lawsBeanList;

        public MyAdapter(List<Laws.LawsBean> bean){
            this.lawsBeanList = bean;
        }

        @Override
        public int getCount() {
            return lawsBeanList.size();
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.laws_regulations_item, null);
                holder = new ViewHolder();
                holder.tv_laws_regulations_item = (TextView) convertView.findViewById(R.id.tv_laws_regulations_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_laws_regulations_item.setText(Html.fromHtml(lawsBeanList.get(position).getTitle()));
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView tv_laws_regulations_item;
    }
}
