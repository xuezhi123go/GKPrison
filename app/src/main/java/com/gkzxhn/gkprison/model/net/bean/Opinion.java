package com.gkzxhn.gkprison.model.net.bean;

/**
 * author:huangzhengneng
 * email:943852572@qq.com
 * date: 2016/8/2.
 * function:意见反馈
 */

public class Opinion {

    private OpinionBean feedback;

    public OpinionBean getFeedback() {
        return feedback;
    }

    public void setFeedback(OpinionBean feedback) {
        this.feedback = feedback;
    }

    public class OpinionBean{

        private String contents;

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }
    }
}
