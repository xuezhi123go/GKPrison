package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by 方 on 2017/4/8.
 */

public class LogRequestBody {

    /**
     * logger : {"phone":"123","contents":"111","device_name":"小米","sys_version":"6.0","device_type":"MI","app_version":12}
     */

    public LoggerBean logger = new LoggerBean();

    public static class LoggerBean {
        /**
         * phone : 123
         * contents : 111
         * device_name : 小米
         * sys_version : 6.0
         * device_type : MI
         * app_version : 12
         */

        public String phone;
        public String contents;
        public String device_name;
        public String sys_version;
        public String device_type;
        public int app_version;

        @Override
        public String toString() {
            return "LoggerBean{" +
                    "phone='" + phone + '\'' +
                    ", contents='" + contents + '\'' +
                    ", device_name='" + device_name + '\'' +
                    ", sys_version='" + sys_version + '\'' +
                    ", device_type='" + device_type + '\'' +
                    ", app_version=" + app_version +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LogRequestBody{" +
                "logger=" + logger +
                '}';
    }
}
