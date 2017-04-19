package com.gkzxhn.gkprison.model.net.bean;

/**
 * author:huangzhengneng
 * email:943852572@qq.com
 * date: 2016/8/3.
 * function:余额
 */

public class Balance {

    /**
     * code : 200
     * family : {"id":33,"prisoner_id":22,"name":"黄正能","uuid":"430482199404073618","phone":"15313817873","relationship":"兄弟","created_at":"2016-07-27T01:56:35.000Z","updated_at":"2016-07-27T02:10:46.000Z","image_url":"/system/uuid_images/images/000/000/175/original/278-1469584568.png|/system/uuid_images/images/000/000/176/original/278-1469584569.png|/system/uuid_images/images/000/000/177/original/278-1469584570.png","balance":"90.0","last_trade_no":null,"prisoner_number":null,"csrbirthday":null,"csrdwellingaddress":null,"family_id":null}
     * accid : bdd997d3e0de28d87c0ea1fdef744dd7
     */

    private int code;
    /**
     * id : 33
     * prisoner_id : 22
     * name : 黄正能
     * uuid : 430482199404073618
     * phone : 15313817873
     * relationship : 兄弟
     * created_at : 2016-07-27T01:56:35.000Z
     * updated_at : 2016-07-27T02:10:46.000Z
     * image_url : /system/uuid_images/images/000/000/175/original/278-1469584568.png|/system/uuid_images/images/000/000/176/original/278-1469584569.png|/system/uuid_images/images/000/000/177/original/278-1469584570.png
     * balance : 90.0
     * last_trade_no : null
     * prisoner_number : null
     * csrbirthday : null
     * csrdwellingaddress : null
     * family_id : null
     */

    private FamilyBean family;
    private String accid;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public FamilyBean getFamily() {
        return family;
    }

    public void setFamily(FamilyBean family) {
        this.family = family;
    }

    public String getAccid() {
        return accid;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "code=" + code +
                ", family=" + family.toString() +
                ", accid='" + accid + '\'' +
                '}';
    }

    public static class FamilyBean {
        private int id;
        private int prisoner_id;
        private String name;
        private String uuid;
        private String phone;
        private String relationship;
        private String created_at;
        private String updated_at;
        private String image_url;
        private String balance;
        private Object last_trade_no;
        private Object prisoner_number;
        private Object csrbirthday;
        private Object csrdwellingaddress;
        private Object family_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPrisoner_id() {
            return prisoner_id;
        }

        public void setPrisoner_id(int prisoner_id) {
            this.prisoner_id = prisoner_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
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

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public Object getLast_trade_no() {
            return last_trade_no;
        }

        public void setLast_trade_no(Object last_trade_no) {
            this.last_trade_no = last_trade_no;
        }

        public Object getPrisoner_number() {
            return prisoner_number;
        }

        public void setPrisoner_number(Object prisoner_number) {
            this.prisoner_number = prisoner_number;
        }

        public Object getCsrbirthday() {
            return csrbirthday;
        }

        public void setCsrbirthday(Object csrbirthday) {
            this.csrbirthday = csrbirthday;
        }

        public Object getCsrdwellingaddress() {
            return csrdwellingaddress;
        }

        public void setCsrdwellingaddress(Object csrdwellingaddress) {
            this.csrdwellingaddress = csrdwellingaddress;
        }

        public Object getFamily_id() {
            return family_id;
        }

        public void setFamily_id(Object family_id) {
            this.family_id = family_id;
        }

        @Override
        public String toString() {
            return "FamilyBean{" +
                    "id=" + id +
                    ", prisoner_id=" + prisoner_id +
                    ", name='" + name + '\'' +
                    ", uuid='" + uuid + '\'' +
                    ", phone='" + phone + '\'' +
                    ", relationship='" + relationship + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    ", image_url='" + image_url + '\'' +
                    ", balance='" + balance + '\'' +
                    ", last_trade_no=" + last_trade_no +
                    ", prisoner_number=" + prisoner_number +
                    ", csrbirthday=" + csrbirthday +
                    ", csrdwellingaddress=" + csrdwellingaddress +
                    ", family_id=" + family_id +
                    '}';
        }
    }
}
