package com.gkzxhn.gkprison.utils.event;

import java.util.List;

/**
 * Created by admin on 2016/1/19.
 */
public class ClickEven1 {
    private List<Integer> envntlist;
    private int delete;

    public ClickEven1(int delete, List<Integer> envntlist) {
        this.delete = delete;
        this.envntlist = envntlist;
    }

    public List<Integer> getList(){
        return envntlist;
    }
    public int getDelete(){
        return delete;
    }
}
