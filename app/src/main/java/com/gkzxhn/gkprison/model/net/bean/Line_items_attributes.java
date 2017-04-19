package com.gkzxhn.gkprison.model.net.bean;

/**
 * 每件商品的信息
 * Created by admin on 2015/12/29.
 */
public class Line_items_attributes {
    private int item_id;
    private int quantity;

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Line_items_attributes{" +
                "item_id=" + item_id +
                ", quantity=" + quantity +
                '}';
    }
}
