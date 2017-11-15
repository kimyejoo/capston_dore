package com.foodtruck.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by evilstorm on 2017. 11. 13..
 */

public class StoreInfoVo implements Serializable {
    private String _id;
    private String truck_id;
    private String say;
    private String sDate;
    private String eDate;
    private String holidays;
    private Date created_at;

    public String getTruck_id() {
        return truck_id;
    }

    public void setTruck_id(String truck_id) {
        this.truck_id = truck_id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        this.say = say;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String geteDate() {
        return eDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }

    public String getHolidays() {
        return holidays;
    }

    public void setHolidays(String holidays) {
        this.holidays = holidays;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
