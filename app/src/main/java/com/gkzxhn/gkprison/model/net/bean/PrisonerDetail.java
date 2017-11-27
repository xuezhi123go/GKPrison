package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by 方 on 2017/11/23.
 */

public class PrisonerDetail {
    /**
     * code : 200
     * data : {"name":"章子子","gender":"m","crimes":"偷窃","additional_punishment":"附加罚款一个亿","prisoner_number":"4000001","started_at":"2017-07-27","ended_at":"2019-07-27","created_at":"2017-07-27T08:30:14.000Z","updated_at":"2017-07-27T08:30:14.000Z","prison_area":"一监区","times":33,"term_start":"2007-08-12 00:00:00.0","term_finish":"2025-05-11 00:00:00.0","sentence":170900,"sentence_desc":"17年09个月00天","sentence_year":"17","sentence_month":"09","sentence_day":"00"}
     */

    public int code;
    public DataBean data;

    public static class DataBean {
        /**
         * name : 章子子
         * gender : m
         * crimes : 偷窃
         * additional_punishment : 附加罚款一个亿
         * prisoner_number : 4000001
         * started_at : 2017-07-27
         * ended_at : 2019-07-27
         * created_at : 2017-07-27T08:30:14.000Z
         * updated_at : 2017-07-27T08:30:14.000Z
         * prison_area : 一监区
         * times : 33
         * term_start : 2007-08-12 00:00:00.0
         * term_finish : 2025-05-11 00:00:00.0
         * sentence : 170900
         * sentence_desc : 17年09个月00天
         * sentence_year : 17
         * sentence_month : 09
         * sentence_day : 00
         */

        public String name;
        public String gender;
        public String crimes;
        public String additional_punishment;
        public String prisoner_number;
        public String started_at;
        public String ended_at;
        public String created_at;
        public String updated_at;
        public String prison_area;
        public int times;
        public String term_start;
        public String term_finish;
        public int sentence;
        public String sentence_desc;
        public String sentence_year;
        public String sentence_month;
        public String sentence_day;
    }
}
