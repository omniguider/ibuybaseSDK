package com.omni.y5citysdk.module.favorite;

import com.google.gson.annotations.SerializedName;
import com.omni.y5citysdk.Y5CitySDKActivity;

import java.io.Serializable;

public class FavoriteInfo implements Serializable {

    @SerializedName("p_id")
    private int p_id;
    @SerializedName("type")
    private String type;
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
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    @SerializedName("image")
    private String image;

    public int getP_id() {
        return p_id;
    }

    public String getType() {
        return type;
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

    public String getArea() {
        if (Y5CitySDKActivity.locale.equals("en")) {
            return area_en;
        } else {
            return area;
        }
    }

    public String getlat() {
        return lat;
    }

    public String getImage() {
        return image;
    }

    public String getlng() {
        return lng;
    }

}
