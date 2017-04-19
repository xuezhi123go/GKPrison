package com.gkzxhn.gkprison.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;

/**
 * add by hzn 2015.12.03
 * Fragment基类
 */
public abstract class BaseFragmentNew extends Fragment {

    public static final String TAG = BaseFragmentNew.class.getSimpleName();
    private boolean isFirstLoad = true;
    private boolean isPrepare = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(setLayoutResId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUiAndListener(view);
        isPrepare = true;
    }

    /**
     * 初始化ui及坚挺着
     */
    protected abstract void initUiAndListener(View view);

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume(): " + getUserVisibleHint());
        if (getUserVisibleHint())
            onVisible();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG, "onResume(): " + isVisibleToUser);
        if (isVisibleToUser)
            onVisible();
    }

    private void onVisible(){
        if (isFirstLoad && isPrepare){
            initData();
            isFirstLoad = false;
        }
    }

    /**
     * 子类必须实现  即布局
     * @return
     */
    protected abstract int setLayoutResId();

    /**
     * 子类要想显示动态数据必须实现
     */
    protected abstract void initData();

    /**
     * 弹出toast 显示时长short
     * @param pMsg
     */
    protected void showToastMsgShort(String pMsg) {
        ToastUtil.showShortToast(pMsg);
    }
    /**
     * 弹出toast 显示时长long
     * @param pMsg
     */
    protected void showToastMsgLong(String pMsg) {
        ToastUtil.showLongToast(pMsg);
    }
}
