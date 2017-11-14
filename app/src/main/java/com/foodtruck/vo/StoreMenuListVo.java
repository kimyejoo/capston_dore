package com.foodtruck.vo;

import java.util.ArrayList;

/**
 * Created by evilstorm on 2017. 11. 14..
 */

public class StoreMenuListVo extends ResponseVoBase {
    private ArrayList<StoreMenuVo> data = new ArrayList<>();

    public ArrayList<StoreMenuVo> getData() {
        return data;
    }

    public void setData(ArrayList<StoreMenuVo> data) {
        this.data = data;
    }
}
