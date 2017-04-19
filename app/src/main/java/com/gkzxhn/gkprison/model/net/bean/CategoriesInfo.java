package com.gkzxhn.gkprison.model.net.bean;

import java.util.List;

/**
 * Created by 方 on 2017/4/1.
 */

public class CategoriesInfo {

    /**
     * code : 200
     * categories : [{"id":1,"title":"洗化用品"},{"id":2,"title":"食品饮料"},{"id":3,"title":"医药保健"}]
     */

    public int code;
    public String msg;
    public String errors;

    public List<CategoriesBean> categories;

    public static class CategoriesBean {
        /**
         * id : 1
         * title : 洗化用品
         */

        public int id;
        public String title;

        @Override
        public String toString() {
            return "CategoriesBean{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "categories{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", errors='" + errors + '\'' +
                ", categories=" + categories +
                '}';
    }
}
