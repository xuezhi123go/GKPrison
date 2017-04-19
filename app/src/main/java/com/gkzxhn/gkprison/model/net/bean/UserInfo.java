package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by admin on 2015/12/21.
 */
public class UserInfo {
    int code;
    String token;
    String avatar;
    String jail;
    String jail_id;
//    User user;
    String relationship;
    String phone;
    String balance;
    String name;
    String id;

    public String getJail() {
        return jail;
    }

    public void setJail(String jail) {
        this.jail = jail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getJail_id() {
        return jail_id;
    }

    public void setJail_id(String jail_id) {
        this.jail_id = jail_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

//    public class User{
//        String created_at;
//        int id;
//        String name;
//        String phone;
//        int prisoner_id;
//        String relationship;
//        String updated_at;
//        String uuid;
//
//        public String getCreated_at() {
//            return created_at;
//        }
//
//        public void setCreated_at(String created_at) {
//            this.created_at = created_at;
//        }
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getPhone() {
//            return phone;
//        }
//
//        public void setPhone(String phone) {
//            this.phone = phone;
//        }
//
//        public int getPrisoner_id() {
//            return prisoner_id;
//        }
//
//        public void setPrisoner_id(int prisoner_id) {
//            this.prisoner_id = prisoner_id;
//        }
//
//        public String getRelationship() {
//            return relationship;
//        }
//
//        public void setRelationship(String relationship) {
//            this.relationship = relationship;
//        }
//
//        public String getUpdated_at() {
//            return updated_at;
//        }
//
//        public void setUpdated_at(String updated_at) {
//            this.updated_at = updated_at;
//        }
//
//        public String getUuid() {
//            return uuid;
//        }
//
//        public void setUuid(String uuid) {
//            this.uuid = uuid;
//        }
//
//        @Override
//        public String toString() {
//            return "UserInfo{" +
//                    "created_at='" + created_at + '\'' +
//                    ", id=" + id +
//                    ", name='" + name + '\'' +
//                    ", phone='" + phone + '\'' +
//                    ", prisoner_id=" + prisoner_id +
//                    ", relationship='" + relationship + '\'' +
//                    ", updated_at='" + updated_at + '\'' +
//                    ", uuid='" + uuid + '\'' +
//                    '}';
//        }
//    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "code=" + code +
                ", token='" + token + '\'' +
                ", avatar='" + avatar + '\'' +
                ", jail='" + jail_id + '\'' +

                '}';
    }
}
