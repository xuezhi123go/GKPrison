package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by zhangjia on 2016/1/7.
 */
public class Letter {

    /**
     * contents : 哈哈没事
     * family_id : 33
     * jail_id : 11
     * theme : 主题:呵呵
     */

    private MessageBean message;

    public MessageBean getMessage() {
        return message;
    }

    public void setMessage(MessageBean message) {
        this.message = message;
    }

    public class MessageBean {
        private String contents;
        private int family_id;
        private int jail_id;
        private String title;

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

        public int getFamily_id() {
            return family_id;
        }

        public void setFamily_id(int family_id) {
            this.family_id = family_id;
        }

        public int getJail_id() {
            return jail_id;
        }

        public void setJail_id(int jail_id) {
            this.jail_id = jail_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String theme) {
            this.title = theme;
        }

        @Override
        public String toString() {
            return "{\"contents\":\"" + contents + "\", \"family_id\":" + family_id
                    + ", \"jail_id\":" + jail_id + ", \"title\": \"" + title + "\"}";
        }
    }

    @Override
    public String toString() {
        return "{\"message\":" + message.toString() + "}";
    }
}
