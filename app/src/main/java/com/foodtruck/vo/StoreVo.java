package com.foodtruck.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by evilstorm on 2017. 11. 13..
 */

public class StoreVo extends ResponseVoBase implements Serializable{
    private String _id;
    private String img;
    private String name;
    private String phone;
    private String location;
    private double lon;
    private double lat;

    private ArrayList<StoreMenuVo> menus = new ArrayList<>();
    private ArrayList<StoreReplyVo> replys = new ArrayList<>();
    private StoreInfoVo info;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<StoreReplyVo> getReplys() {
        return replys;
    }

    public void setReplys(ArrayList<StoreReplyVo> replys) {
        this.replys = replys;
    }

    public ArrayList<StoreMenuVo> getMenus() {
        return menus;
    }

    public void setMenus(ArrayList<StoreMenuVo> menus) {
        this.menus = menus;
    }

    public StoreInfoVo getInfo() {
        return info;
    }

    public void setInfo(StoreInfoVo info) {
        this.info = info;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
