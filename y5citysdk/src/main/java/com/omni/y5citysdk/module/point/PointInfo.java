package com.omni.y5citysdk.module.point;

import com.google.gson.annotations.SerializedName;
import com.omni.y5citysdk.Y5CitySDKActivity;

import java.io.Serializable;

public class PointInfo implements Serializable {

    @SerializedName("p_id")
    private int p_id;
    @SerializedName("name")
    private String name;
    @SerializedName("name_en")
    private String name_en;
    @SerializedName("area")
    private String area;
    @SerializedName("area_en")
    private String area_en;
    @SerializedName("address")
    private String address;
    @SerializedName("address_en")
    private String address_en;
    @SerializedName("web")
    private String web;
    @SerializedName("tel")
    private String tel;
    @SerializedName("web_phone")
    private String web_phone;
    @SerializedName("opentime")
    private String opentime;
    @SerializedName("total_fav")
    private int total_fav;
    @SerializedName("is_fav")
    private boolean is_fav;
    @SerializedName("lat")
    private float lat;
    @SerializedName("lng")
    private float lng;
    @SerializedName("image")
    private String image;

    public String getWeb() {
        return web;
    }

    public String getTel() {
        return tel;
    }

    public String getOpentime() {
        return opentime;
    }

    public int getTotal_fav() {
        return total_fav;
    }

    public boolean getIs_fav() {
        return is_fav;
    }

    public int getP_id() {
        return p_id;
    }

    public String getArea() {
        if (Y5CitySDKActivity.locale.equals("en")) {
            return area_en;
        } else {
            return area;
        }
    }

    public String getname() {
        if (Y5CitySDKActivity.locale.equals("en")) {
            return name_en;
        } else {
            return name;
        }
    }

    public String getname_en() {
        return name_en;
    }

    public String getaddress() {
        if (Y5CitySDKActivity.locale.equals("en")) {
            return address_en;
        } else {
            return address;
        }
    }

    public float getlat() {
        return lat;
    }

    public String getImage() {
        return image;
    }

    public float getlng() {
        return lng;
    }

}
