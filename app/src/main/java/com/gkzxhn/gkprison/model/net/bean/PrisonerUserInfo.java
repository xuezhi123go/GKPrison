package com.gkzxhn.gkprison.model.net.bean;

/**
 * author:huangzhengneng
 * email:943852572@qq.com
 * date: 2016/8/1.
 * function:
 */

public class PrisonerUserInfo {
    private int code;
    private Prisoner prisoner;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Prisoner getPrisoner() {
        return prisoner;
    }

    public void setPrisoner(Prisoner prisoner) {
        this.prisoner = prisoner;
    }

    public class Prisoner {
        public String id;
        public String prisoner_number;
        public String name;
        public String gender;
        public String crimes;
        public String started_at;
        public String ended_at;
        public String prison_area;

        @Override
        public String toString() {
            return "Prisoner{" +
                    "id='" + id + '\'' +
                    ", prisoner_number='" + prisoner_number + '\'' +
                    ", name='" + name + '\'' +
                    ", gender='" + gender + '\'' +
                    ", crimes='" + crimes + '\'' +
                    ", started_at='" + started_at + '\'' +
                    ", ended_at='" + ended_at + '\'' +
                    ", prison_area='" + prison_area + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PrisonerUserInfo{" +
                "code=" + code +
                ", prisoner=" + prisoner +
                '}';
    }
}
