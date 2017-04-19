package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by admin on 2016/1/15.
 * 监狱长回复
 */
public class Reply {

    /**
     * title : 主题:无法睡觉
     * contents : 就餐许多人不排队，经常引起争执，以至于最后没有饭吃，请予以处理。
     * replies : 11月份的回复
     */

    private String title;
    private String contents;
    private String replies;
    private String reply_date;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReply_date() {
        return reply_date;
    }

    public void setReply_date(String reply_date) {
        this.reply_date = reply_date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public String getReplies() {
        return replies;
    }
}
