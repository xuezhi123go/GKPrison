package com.gkzxhn.gkprison.model.net.bean;

import java.util.List;

/**
 * Created by 方 on 2017/4/6.
 * 家属端可会见列表
 */

public class MeetingTimeInfo {

    /**
     * code : 200
     * meetings : ["2017-04-14 9:00 - 9:30","2017-04-06 9:00 - 9:30","2017-04-10 9:00 - 9:30","2017-04-17 9:00 - 9:30","2017-04-26 9:00 - 9:30","2017-04-27 9:00 - 9:30","2017-04-11 9:00 - 9:30","2017-04-18 9:00 - 9:30","2017-04-20 9:00 - 9:30"]
     */

    public int code;
    public List<String> meetings;

    @Override
    public String toString() {
        return "MeetingTimeInfo{" +
                "code=" + code +
                ", meetings=" + meetings +
                '}';
    }
}
