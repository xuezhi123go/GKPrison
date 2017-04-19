package com.gkzxhn.gkprison.model.net.bean;

import java.util.List;

/**
 * Author: Huang ZN
 * Date: 2016/12/27
 * Email:943852572@qq.com
 * Description:
 */

public class PrisonList {

    /**
     * id : 1
     * title :
     */

    private List<JailsBean> jails;

    public List<JailsBean> getJails() {
        return jails;
    }

    public void setJails(List<JailsBean> jails) {
        this.jails = jails;
    }

    public static class JailsBean {
        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "JailsBean{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PrisonList{" +
                "jails=" + jails.size() +
                '}';
    }
}
