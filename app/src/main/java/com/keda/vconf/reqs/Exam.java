package com.keda.vconf.reqs;

/**
 * Author: Huang ZN
 * Date: 2016/10/27
 * Email:943852572@qq.com
 * Description:
 */

public class Exam {


    /**
     * code : 200
     * receiver : xxxxxxxx
     * sender : xxxxxxx
     */

    private NotificationBean notification;

    public NotificationBean getNotification() {
        return notification;
    }

    public void setNotification(NotificationBean notification) {
        this.notification = notification;
    }

    public static class NotificationBean {
        private int code;
        private String receiver;
        private String sender;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }
    }
}
