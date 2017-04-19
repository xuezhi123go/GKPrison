package com.gkzxhn.gkprison.model.net.bean;

import java.util.List;

/**
 * Created by 方 on 2017/4/8.
 */

public class LogInfo {
    public int code;
    public String msg;
    public ErrorsBean errors;
    /**
     * code : 400
     * errors : {"contents":["can't be blank"],"phone":["can't be blank"],"device_name":["can't be blank"],"device_type":["can't be blank"],"sys_version":["can't be blank"]}
     */

    public static class ErrorsBean {
        public List<String> contents;
        public List<String> phone;
        public List<String> device_name;
        public List<String> device_type;
        public List<String> sys_version;
        public List<String> app_version;

        @Override
        public String toString() {
            return "ErrorsBean{" +
                    "contents=" + contents +
                    ", phone=" + phone +
                    ", device_name=" + device_name +
                    ", device_type=" + device_type +
                    ", sys_version=" + sys_version +
                    ", app_version=" + app_version +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LogInfo{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", errors=" + errors +
                '}';
    }
}
