package com.gkzxhn.gkprison.model.net.bean;

/**
 * Author: Huang ZN
 * Date: 2017/1/19
 * Email:943852572@qq.com
 * Description:家属服务中需要的bean
 */

public class FamilyServerBean {

    /**
     * code : 200
     * prisoner : {"id":22,"prisoner_number":"4000022","name":"张三22","gender":"m","crimes":"抢劫","jail_id":11,"prison_term_started_at":"2009-01-07","prison_term_ended_at":"2019-01-07","created_at":"2016-01-09T02:01:28.000Z","updated_at":"2016-01-09T02:01:28.000Z","prison_area":"第三监区","isvisit":null}
     */

    private int code;
    /**
     * id : 22
     * prisoner_number : 4000022
     * name : 张三22
     * gender : m
     * crimes : 抢劫
     * jail_id : 11
     * prison_term_started_at : 2009-01-07
     * prison_term_ended_at : 2019-01-07
     * created_at : 2016-01-09T02:01:28.000Z
     * updated_at : 2016-01-09T02:01:28.000Z
     * prison_area : 第三监区
     * isvisit : null
     */

    private PrisonerBean prisoner;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public PrisonerBean getPrisoner() {
        return prisoner;
    }

    public void setPrisoner(PrisonerBean prisoner) {
        this.prisoner = prisoner;
    }

    public static class PrisonerBean {
        private int id;
        private String prisoner_number;
        private String name;
        private String gender;
        private String crimes;
        private int jail_id;
        private String prison_term_started_at;
        private String prison_term_ended_at;
        private String created_at;
        private String updated_at;
        private String prison_area;
        private Object isvisit;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPrisoner_number() {
            return prisoner_number;
        }

        public void setPrisoner_number(String prisoner_number) {
            this.prisoner_number = prisoner_number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getCrimes() {
            return crimes;
        }

        public void setCrimes(String crimes) {
            this.crimes = crimes;
        }

        public int getJail_id() {
            return jail_id;
        }

        public void setJail_id(int jail_id) {
            this.jail_id = jail_id;
        }

        public String getPrison_term_started_at() {
            return prison_term_started_at;
        }

        public void setPrison_term_started_at(String prison_term_started_at) {
            this.prison_term_started_at = prison_term_started_at;
        }

        public String getPrison_term_ended_at() {
            return prison_term_ended_at;
        }

        public void setPrison_term_ended_at(String prison_term_ended_at) {
            this.prison_term_ended_at = prison_term_ended_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getPrison_area() {
            return prison_area;
        }

        public void setPrison_area(String prison_area) {
            this.prison_area = prison_area;
        }

        public Object getIsvisit() {
            return isvisit;
        }

        public void setIsvisit(Object isvisit) {
            this.isvisit = isvisit;
        }

        @Override
        public String toString() {
            return "PrisonerBean{" +
                    "id=" + id +
                    ", prisoner_number='" + prisoner_number + '\'' +
                    ", name='" + name + '\'' +
                    ", gender='" + gender + '\'' +
                    ", crimes='" + crimes + '\'' +
                    ", jail_id=" + jail_id +
                    ", prison_term_started_at='" + prison_term_started_at + '\'' +
                    ", prison_term_ended_at='" + prison_term_ended_at + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    ", prison_area='" + prison_area + '\'' +
                    ", isvisit=" + isvisit +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FamilyServerBean{" +
                "code=" + code +
                ", prisoner=" + prisoner.toString() +
                '}';
    }
}
