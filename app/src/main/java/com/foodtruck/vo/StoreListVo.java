package com.foodtruck.vo;

import java.util.ArrayList;

/**
 * Created by evilstorm on 2017. 11. 13..
 */

public class StoreListVo {
    private int code;
    private String message;
    private ArrayList<StoreVo> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<StoreVo> getData() {
        return data;
    }

    public void setData(ArrayList<StoreVo> data) {
        this.data = data;
    }
}
