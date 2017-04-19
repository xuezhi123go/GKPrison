package com.gkzxhn.gkprison.widget.view.auto;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.gkzxhn.gkprison.constant.Constants;
import com.gkzxhn.gkprison.model.net.api.UtilsService;
import com.gkzxhn.gkprison.model.net.bean.PrisonList;
import com.gkzxhn.gkprison.utils.CustomUtils.SimpleObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: Huang ZN
 * Date: 2016/12/27
 * Email:943852572@qq.com
 * Description:自动补齐textview  此处用于监狱名称提醒
 */

public class AutoCompleteTv extends AutoCompleteTextView implements TextWatcher{

    private static final String TAG = "AutoCompleteTv";

    private Map<String, Integer> dataList = new HashMap<>();
    private List<String> nameList = new ArrayList<>();
    private AutoTextAdapater adapater;

    public AutoCompleteTv(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(this);
    }

    public AutoCompleteTv(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(this);
    }

    public AutoCompleteTv(Context context) {
        super(context);
        addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        Log.i(TAG, "onTextChanged" + newText);
        getDataList(newText);
    }

    /**
     * 发起请求获取结果
     * @param newText
     */
    private void getDataList(String newText) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_HEAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UtilsService service = retrofit.create(UtilsService.class);
        service.getPrisonList(newText)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<PrisonList>(){
                    @Override public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override public void onNext(PrisonList prisonList) {
                        Log.i(TAG, "onNext");
                        if (prisonList.getJails().size() > 0 ){
                            Log.i(TAG, "prison list ：" + prisonList.toString());
                            nameList.clear();
                            dataList.clear();
                            for (PrisonList.JailsBean jailsBean : prisonList.getJails()){
                                nameList.add(jailsBean.getTitle());
                                dataList.put(jailsBean.getTitle(), jailsBean.getId());
                                Log.i(TAG, "prison ：" + jailsBean.toString());
                            }
                            adapater = new AutoTextAdapater(nameList, getContext());
                            AutoCompleteTv.this.setAdapter(adapater);
                        }
                    }
                });
    }

    /**
     * 获取监狱名称集合
     * @return
     */
    public List<String> getNameList(){
        return nameList;
    }

    /**
     * 获取监狱名称和id键值对map
     * @return
     */
    public Map<String, Integer> getDataList(){
        return dataList;
    }


}
