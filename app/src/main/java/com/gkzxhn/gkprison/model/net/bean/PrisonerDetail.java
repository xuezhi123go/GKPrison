package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by 方 on 2017/11/23.
 */

public class PrisonerDetail {

    /**
     * total : 1
     * data : {"id":3372,"prisoner_number":"4000001","name":"章子子","gender":"m","crimes":"偷窃","additional_punishment":"附加罚款一个亿","prison_term_started_at":"2017-07-27","prison_term_ended_at":"2019-07-27","created_at":"2017-07-27T08:30:14.000Z","updated_at":"2017-07-27T08:30:14.000Z","prison_area":"一监区","times":65,"last_time":"2012-06-01 00:00:00"}
     */

    public int total;
    public DataBean data;

    public static class DataBean {
        /**
         * id : 3372
         * prisoner_number : 4000001
         * name : 章子子
         * gender : m
         * crimes : 偷窃
         * additional_punishment : 附加罚款一个亿
         * prison_term_started_at : 2017-07-27
         * prison_term_ended_at : 2019-07-27
         * created_at : 2017-07-27T08:30:14.000Z
         * updated_at : 2017-07-27T08:30:14.000Z
         * prison_area : 一监区
         * times : 65
         * last_time : 2012-06-01 00:00:00
         */

        public int id;
        public String prisoner_number;
        public String name;
        public String gender;
        public String crimes;
        public String additional_punishment;
        public String prison_term_started_at;
        public String prison_term_ended_at;
        public String created_at;
        public String updated_at;
        public String prison_area;
        public int times;
        public String last_time;
    }
}
