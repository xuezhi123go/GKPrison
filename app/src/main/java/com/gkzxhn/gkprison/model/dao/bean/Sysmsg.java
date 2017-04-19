package com.gkzxhn.gkprison.model.dao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 方 on 2017/4/13.
 */

@Entity
public class Sysmsg {
    @Id(autoincrement = true)
    private Long id;
    private String apply_date;     //     申请日期
    private String name;           // 姓名
    private String result;         // 申请结果
    private String is_read;        // 是否已读
    private String meeting_date;   //         会见日期
    private Integer type_id;       //     会见类型id
    private String reason;         // 理由
    private String receive_time;   //         消息接收时间
    private String user_id;        // 用户id
    @Generated(hash = 293675800)
    public Sysmsg(Long id, String apply_date, String name, String result,
            String is_read, String meeting_date, Integer type_id, String reason,
            String receive_time, String user_id) {
        this.id = id;
        this.apply_date = apply_date;
        this.name = name;
        this.result = result;
        this.is_read = is_read;
        this.meeting_date = meeting_date;
        this.type_id = type_id;
        this.reason = reason;
        this.receive_time = receive_time;
        this.user_id = user_id;
    }
    @Generated(hash = 697515506)
    public Sysmsg() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getApply_date() {
        return this.apply_date;
    }
    public void setApply_date(String apply_date) {
        this.apply_date = apply_date;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getResult() {
        return this.result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getIs_read() {
        return this.is_read;
    }
    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }
    public String getMeeting_date() {
        return this.meeting_date;
    }
    public void setMeeting_date(String meeting_date) {
        this.meeting_date = meeting_date;
    }
    public Integer getType_id() {
        return this.type_id;
    }
    public void setType_id(Integer type_id) {
        this.type_id = type_id;
    }
    public String getReason() {
        return this.reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getReceive_time() {
        return this.receive_time;
    }
    public void setReceive_time(String receive_time) {
        this.receive_time = receive_time;
    }
    public String getUser_id() {
        return this.user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
