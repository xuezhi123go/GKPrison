package com.gkzxhn.gkprison.model.net.bean;

import java.util.List;

/**
 * 所有注册信息放入此对象，方便转成json;
 * Created by admin on 2015/12/31.
 */
public class Register {
    private String name;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String uuid;
    private String relationship;
    private String prisoner_number;
    private String prison;
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private int jail_id;
    private int type_id;
    private List<Uuid_images_attributes> uuid_images_attributes;



    public String getPrison() {
        return prison;
    }

    public void setPrison(String prison) {
        this.prison = prison;
    }

    public int getJail_id() {
        return jail_id;
    }

    public void setJail_id(int jail_id) {
        this.jail_id = jail_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getPrisoner_number() {
        return prisoner_number;
    }

    public void setPrisoner_number(String prisoner_number) {
        this.prisoner_number = prisoner_number;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Uuid_images_attributes> getUuid_images_attributes() {
        return uuid_images_attributes;
    }

    public void setUuid_images_attributes(List<Uuid_images_attributes> uuid_images_attributes) {
        this.uuid_images_attributes = uuid_images_attributes;
    }
}
