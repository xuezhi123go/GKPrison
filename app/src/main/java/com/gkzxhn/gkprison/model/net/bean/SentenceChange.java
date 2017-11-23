package com.gkzxhn.gkprison.model.net.bean;

import java.util.List;

/**
 * Created by 方 on 2017/11/22.
 */

public class SentenceChange {

    /**
     * code : 200
     * status : SUCCESS
     * data : [{"id":2,"term_start":"2007-08-12 00:00:00.0","term_finish":"2024-02-11 00:00:00.0","prisoner_id":1,"changetype":"狱内减刑","changeyear":1,"changemonth":3,"changeday":0}]
     */

    public int code;
    public String status;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * id : 2
         * term_start : 2007-08-12 00:00:00.0
         * term_finish : 2024-02-11 00:00:00.0
         * prisoner_id : 1
         * changetype : 狱内减刑
         * changeyear : 1
         * changemonth : 3
         * changeday : 0
         */

        public int id;
        public String term_start;
        public String term_finish;
        public int prisoner_id;
        public String changetype;
        public int changeyear;
        public int changemonth;
        public int changeday;
    }
}
