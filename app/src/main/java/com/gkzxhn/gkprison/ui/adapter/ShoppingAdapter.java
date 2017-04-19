package com.gkzxhn.gkprison.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.model.net.bean.CartInfo;
import com.gkzxhn.gkprison.ui.activity.normal_activity.ShoppingRecordActivity;
import com.gkzxhn.gkprison.utils.NomalUtils.ListViewParamsUtils;
import com.gkzxhn.gkprison.widget.view.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/1/14.
 */
public class ShoppingAdapter extends BaseAdapter {
    private List<CartInfo> list;
    List<CommidtyAdapter> commodityAdapterList = new ArrayList<CommidtyAdapter>();
    ShoppingRecordActivity context;

    public ShoppingAdapter(ShoppingRecordActivity context, List<CartInfo> list) {
        this.context = context;
        this.list = list;
        for (int i = 0; i < list.size(); i++) {
            CommidtyAdapter commidtyAdapter = new CommidtyAdapter(this,i,context,
                     list.get(i).getCommodityList());
            commodityAdapterList.add(commidtyAdapter);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler viewHoler;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.shoppingrecode_item, null);
            viewHoler = new ViewHoler();
            viewHoler.tv_paytime = (TextView)convertView.findViewById(R.id.tv_paytime);
            viewHoler.tv_qty = (TextView)convertView.findViewById(R.id.tv_qty);
            viewHoler.tvshopping_money = (TextView)convertView.findViewById(R.id.tvshopping_money);
            viewHoler.lv_recode = (CustomListView)convertView.findViewById(R.id.lv_recode);
            viewHoler.tv_alipay_trading_num = (TextView) convertView.findViewById(R.id.tv_alipay_trading_num);
            viewHoler.tv_transact_state = (TextView) convertView.findViewById(R.id.tv_transact_state);
            convertView.setTag(viewHoler);
        }else {
            viewHoler = (ViewHoler)convertView.getTag();
        }
        viewHoler.lv_recode.setAdapter(commodityAdapterList.get(position));
        ListViewParamsUtils.setListViewHeightBasedOnChildren(viewHoler.lv_recode);
        final CartInfo cartInfo = list.get(position);
        viewHoler.tv_paytime.setText(cartInfo.getTime());
        viewHoler.tv_alipay_trading_num.setText(cartInfo.getOut_trade_no());
        viewHoler.tvshopping_money.setText(cartInfo.getTotal_money());
        viewHoler.tv_qty.setText(cartInfo.getCount()+"");

       //
        return convertView;
    }
    private class ViewHoler{
        TextView tv_alipay_trading_num;
        TextView tvshopping_money;
        TextView tv_paytime;
        TextView tv_transact_state;
        TextView tv_qty;
        CustomListView lv_recode;
    }
}
