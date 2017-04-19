package com.gkzxhn.gkprison.ui.pay;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;

/**
 * Author: Huang ZN
 * Date: 2017/1/9
 * Email:943852572@qq.com
 * Description:
 */

public class PayMentAdapter extends BaseAdapter {

    private static final String[] pay_ways = {"支付宝支付", "微信支付"};
    private static final int[] pay_way_icons = {R.drawable.pay_way_zhifubao, R.drawable.pay_way_weixin};
    private boolean[] WHICH_CHECKED = {true, false, false};
    private Context mContext;

    public PayMentAdapter(Context context){
        this.mContext = context;
    }

    /**
     * 获取被选中的支付方式
     * @return  position 初始值为0
     */
    public int getWhichChecked(){
        for (int i = 0; i < WHICH_CHECKED.length; i++){
            if (WHICH_CHECKED[i])
                return i;
        }
        return 0;
    }

    /**
     * 设置条目点击事件
     * @param position
     */
    public void setOnItemClickListener(int position){
        for (int i = 0; i < WHICH_CHECKED.length; i++) {
            if (i == position) {
                WHICH_CHECKED[i] = true;
            } else {
                WHICH_CHECKED[i] = false;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pay_ways.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.pay_way_item, null);
            holder = new ViewHolder();
            holder.iv_pay_way_icon = (ImageView) convertView.findViewById(R.id.iv_pay_way_icon);
            holder.tv_pay_way = (TextView) convertView.findViewById(R.id.tv_pay_way);
            holder.cb_pay_way = (CheckBox) convertView.findViewById(R.id.cb_pay_way);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv_pay_way_icon.setImageResource(pay_way_icons[position]);
        holder.tv_pay_way.setText(pay_ways[position]);

        holder.cb_pay_way.setChecked(WHICH_CHECKED[position]);
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_pay_way_icon;
        TextView tv_pay_way;
        CheckBox cb_pay_way;
    }
}
