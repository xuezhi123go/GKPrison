package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by admin on 2015/12/21.
 */
public class UserInfo {

    /**
     * code : 200
     * id : 3
     * jail_id : 1
     * name : 方宇星
     * phone : 15243686547
     * relationship : 父子
     * balance : 78.0
     * avatar : /system/uuid_images/images/000/000/007/original/3-1490844072.png|/system/uuid_images/images/000/000/008/original/3-1490844073.png|/system/uuid_images/images/000/000/009/original/3-1490844073.png
     * jail : 英山监狱
     * modules : {"meeting":1,"shopping":1}
     * token : 258787844470435be99583cec8349df2
     */

    private int code;
    private String id;
    private String jail_id;
    private String name;
    private String phone;
    private String relationship;
    private String balance;
    private String avatar;
    private String jail;
    private ModulesBean modules;
    private String token;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJail_id() {
        return jail_id;
    }

    public void setJail_id(String jail_id) {
        this.jail_id = jail_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getJail() {
        return jail;
    }

    public void setJail(String jail) {
        this.jail = jail;
    }

    public ModulesBean getModules() {
        return modules;
    }

    public void setModules(ModulesBean modules) {
        this.modules = modules;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class ModulesBean {
        @Override
        public String toString() {
            return "ModulesBean{" +
                    "meeting=" + meeting +
                    ", shopping=" + shopping +
                    '}';
        }

        /**
         * meeting : 1  表示开通
         * shopping : 1  表示开通
         */

        private int meeting;
        private int shopping;

        private int prison_term; //刑期变动

        private int rewards; //奖惩

        public int getPrisonTerm() {
            return prison_term;
        }

        public void setPrisonTerm(int prisonTerm) {
            this.prison_term = prisonTerm;
        }

        public int getRewards() {
            return rewards;
        }

        public void setRewards(int rewards) {
            this.rewards = rewards;
        }

        public int getMeeting() {
            return meeting;
        }

        public void setMeeting(int meeting) {
            this.meeting = meeting;
        }

        public int getShopping() {
            return shopping;
        }

        public void setShopping(int shopping) {
            this.shopping = shopping;
        }
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "code=" + code +
                ", id='" + id + '\'' +
                ", jail_id='" + jail_id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", relationship='" + relationship + '\'' +
                ", balance='" + balance + '\'' +
                ", avatar='" + avatar + '\'' +
                ", jail='" + jail + '\'' +
                ", modules=" + modules +
                ", token='" + token + '\'' +
                '}';
    }
}
