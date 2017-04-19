package com.gkzxhn.gkprison.model.net.bean;

/**
 * Created by Administrator on 2016/1/27.
 * 版本信息bean
 */
public class VersionInfo {

    /**
     * code : 200
     * version_code : 1.2.1
     * version_number : 12
     * description : 解决了某些bug
     * download : https://www.fushuile.com/app/yuwutong_f.apk
     * is_force : true
     */

    public int code;
    public String version_code;
    public int version_number;
    public String description;
    public String download;
    public boolean is_force;

    @Override
    public String toString() {
        return "VersionInfo{" +
                "code=" + code +
                ", version_code='" + version_code + '\'' +
                ", version_number=" + version_number +
                ", description='" + description + '\'' +
                ", download='" + download + '\'' +
                ", is_force=" + is_force +
                '}';
    }
}
