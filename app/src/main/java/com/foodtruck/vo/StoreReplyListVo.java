package com.foodtruck.vo;

import java.util.ArrayList;

/**
 * Created by evilstorm on 2017. 11. 14..
 */

public class StoreReplyListVo extends ResponseVoBase {

    private ArrayList<StoreReplyVo> data = new ArrayList<>();

    public ArrayList<StoreReplyVo> getData() {
        return data;
    }

    public void setData(ArrayList<StoreReplyVo> data) {
        this.data = data;
    }
}
