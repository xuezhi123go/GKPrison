package com.gkzxhn.gkprison.ui.activity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gkzxhn.gkprison.R;

/**
 * Author: Huang ZN
 * Date: 2016/12/22
 * Email:943852572@qq.com
 * Description:WelComeAdapter 欢迎页面适配器
 */

public class WelComeAdapter extends PagerAdapter {

    private int[] WELCOME_IMGS;
    private Context mContext;

    public WelComeAdapter(Context context, int[] imgs){
        this.WELCOME_IMGS = imgs;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return WELCOME_IMGS.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = View.inflate(mContext, R.layout.welcome_item, null);
        ImageView iv_welcome = (ImageView) view.findViewById(R.id.iv_welcome);
        iv_welcome.setImageResource(WELCOME_IMGS[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
