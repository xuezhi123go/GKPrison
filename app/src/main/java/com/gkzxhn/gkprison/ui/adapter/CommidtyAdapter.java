package com.gkzxhn.gkprison.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gkzxhn.gkprison.R;
import com.gkzxhn.gkprison.model.net.bean.Commodity;
import com.gkzxhn.gkprison.ui.activity.normal_activity.ShoppingRecordActivity;

import java.util.List;

/**
 * Created by admin on 2016/1/14.
 */
public class CommidtyAdapter extends BaseAdapter {
    private List<Commodity> list;
    ShoppingAdapter adapter;
    int cartPosition;
    ShoppingRecordActivity context;

    public CommidtyAdapter(ShoppingAdapter adapter, int cartPosition, ShoppingRecordActivity context, List<Commodity> list) {
        this.adapter = adapter;
        this.cartPosition = cartPosition;
        this.context = context;
        this.list = list;
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
        ViewHolder1 holder1;
        if (convertView == null){
            convertView = View.inflate(context, R.layout.recoding_items, null);
            holder1 = new ViewHolder1();
            holder1.description = (TextView)convertView.findViewById(R.id.tv_shopping_desciption);
            holder1.price = (TextView)convertView.findViewById(R.id.tv_shopping_mongey);
            holder1.qty = (TextView)convertView.findViewById(R.id.tv_shopping_qty);
            holder1.unitPrice = (LinearLayout)convertView.findViewById(R.id.ll_unit);
            convertView.setTag(holder1);
        }else {
            holder1 = (ViewHolder1)convertView.getTag();
        }
        final Commodity commodity = list.get(position);
        String title = commodity.getTitle();
        holder1.description.setText(TextUtils.isEmpty(title) ? "" : title);
        String price = commodity.getPrice();
        String qty = String.valueOf(commodity.getQty());
        if (TextUtils.isEmpty(price) || TextUtils.isEmpty(qty)) {
            holder1.unitPrice.setVisibility(View.GONE);
        }else {
            holder1.price.setText(price);
            holder1.qty.setText(qty);
        }
        return convertView;
    }
    private class ViewHolder1{
        TextView description;
        TextView price;
        TextView qty;
        LinearLayout unitPrice;
    }
}
