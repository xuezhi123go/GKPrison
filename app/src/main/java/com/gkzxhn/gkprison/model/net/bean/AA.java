package com.gkzxhn.gkprison.model.net.bean;

/**
 * 方便订单信息转json建立此类，名字没意义
 * Created by admin on 2015/12/31.
 */
public class AA {
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "AA{" +
                "order=" + order +
                '}';
    }
}
