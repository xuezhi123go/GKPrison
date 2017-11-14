package com.gkzxhn.gkprison.ui.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.base.MyApplication;
import com.gkzxhn.gkprison.ui.activity.normal_activity.FamilyServiceActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.LawsRegulationsActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.PrisonIntroductionActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.PrisonOpenActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.PrisonWardenActivity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.WorkDynamicActivity;
import com.gkzxhn.gkprison.utils.CustomUtils.SPKeyConstants;
import com.gkzxhn.gkprison.utils.NomalUtils.SPUtil;
import com.gkzxhn.gkprison.utils.NomalUtils.ToastUtil;

import static com.gkzxhn.gkprison.utils.CustomUtils.MainUtils.OPTIONS_IVS;
import static com.gkzxhn.gkprison.utils.CustomUtils.MainUtils.OPTIONS_IVS_PRESS;

/**
 * Author: Huang ZN
 * Date: 2016/12/29
 * Email:943852572@qq.com
 * Description:
 */

public class MyOptionsAdapter extends BaseAdapter {

    private Context mContext;

    private final String[] OPTIONS_TVS = {MyApplication.getContext().getString(R.string.prison_introduction),
            MyApplication.getContext().getString(R.string.laws_regulations),
            MyApplication.getContext().getString(R.string.prison_open),
            MyApplication.getContext().getString(R.string.work_dynamic),
            MyApplication.getContext().getString(R.string.family_server),
            MyApplication.getContext().getString(R.string.warden)};

    public MyOptionsAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return OPTIONS_IVS.length;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OptionsViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.home_options_item, null);
            holder = new OptionsViewHolder();
            holder.iv_home_options = (ImageView) convertView.findViewById(R.id.iv_home_options);
            holder.tv_home_options = (TextView) convertView.findViewById(R.id.tv_home_options);
            convertView.setTag(holder);
        }else {
            holder = (OptionsViewHolder) convertView.getTag();
        }
        holder.iv_home_options.setImageResource(OPTIONS_IVS[position]);
        holder.tv_home_options.setText(OPTIONS_TVS[position]);
        final View finalConvertView = convertView;
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        finalConvertView.setBackgroundColor(mContext.getResources().getColor(R.color.theme));
                        holder.tv_home_options.setTextColor(mContext.getResources().getColor(R.color.white));
                        holder.iv_home_options.setImageResource(OPTIONS_IVS_PRESS[position]);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        finalConvertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        holder.tv_home_options.setTextColor(mContext.getResources().getColor(R.color.tv_bg));
                        holder.iv_home_options.setImageResource(OPTIONS_IVS[position]);
                        boolean isRegisteredUser = (boolean) SPUtil.get(mContext, SPKeyConstants.IS_REGISTERED_USER, false);
                        switch (position) {
                            case 0:
                                intent = new Intent(mContext, PrisonIntroductionActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(mContext, LawsRegulationsActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(mContext, PrisonOpenActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(mContext, WorkDynamicActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case 5:
                                if (isRegisteredUser) {
                                    intent = new Intent(mContext, PrisonWardenActivity.class);
                                    mContext.startActivity(intent);
                                }else {
                                    ToastUtil.showShortToast(mContext.getString(R.string.enable_logined));
                                }
                                break;
                            case 4:
                                if(isRegisteredUser){
                                    intent = new Intent(mContext, FamilyServiceActivity.class);
                                    mContext.startActivity(intent);
                                }else {
                                    ToastUtil.showShortToast(mContext.getString(R.string.enable_logined));
                                }
                                break;
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        finalConvertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        holder.tv_home_options.setTextColor(mContext.getResources().getColor(R.color.tv_bg));
                        holder.iv_home_options.setImageResource(OPTIONS_IVS[position]);
                        break;
                }
                return true;
            }
        });
        return convertView;
    }

    private static class OptionsViewHolder{
        ImageView iv_home_options;
        TextView tv_home_options;
    }
}
