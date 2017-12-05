package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by 方 on 2017/11/23.
 */

public class PrisonerDetail {
    /**
     * code : 200
     * data : {"name":"银辉长","original_sentence":null,"gender":"m","crimes":"抢夺、抢劫","additional_punishment":null,"prisoner_number":"4501005066","started_at":"2009-05-14","ended_at":"2024-05-27","created_at":"2017-07-27T08:30:19.000Z","updated_at":"2017-11-27T07:02:50.000Z","prison_area":"","times":5,"term_start":"2017-08-25 00:00:00.0","term_finish":"2018-11-27 00:00:00.0","sentence":100600,"sentence_desc":"10年06个月00天","sentence_year":"10","sentence_month":"06","sentence_day":"00"}
     */

    public int code;
    public DataBean data;

    public static class DataBean {
        /**
         * name : 银辉长
         * original_sentence : null
         * gender : m
         * crimes : 抢夺、抢劫
         * additional_punishment : null
         * prisoner_number : 4501005066
         * started_at : 2009-05-14
         * ended_at : 2024-05-27
         * created_at : 2017-07-27T08:30:19.000Z
         * updated_at : 2017-11-27T07:02:50.000Z
         * prison_area :
         * times : 5
         * term_start : 2017-08-25 00:00:00.0
         * term_finish : 2018-11-27 00:00:00.0
         * sentence : 100600
         * sentence_desc : 10年06个月00天
         * sentence_year : 10
         * sentence_month : 06
         * sentence_day : 00
         */

        public String name;
        public String original_sentence;
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
