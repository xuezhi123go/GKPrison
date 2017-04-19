package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by 方 on 2017/3/27.
 */

public class Balances {

    /**
     * code : 200
     * balance : {"id":null,"balance":"1000.0"}
     */

    public int code;
    public BalanceBean balance;

    public static class BalanceBean {
        /**
         * id : null
         * balance : 1000.0
         */

        public int id;
        public String balance;

        @Override
        public String toString() {
            return "BalanceBean{" +
                    "id=" + id +
                    ", balance='" + balance + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Balances{" +
                "code=" + code +
                ", balance=" + balance +
                '}';
    }
}
