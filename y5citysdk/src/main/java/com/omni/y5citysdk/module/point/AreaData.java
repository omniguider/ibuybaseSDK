package com.omni.y5citysdk.module.point;

import com.google.gson.annotations.SerializedName;
import com.omni.y5citysdk.Y5CitySDKActivity;

import java.io.Serializable;

public class AreaData implements Serializable {

    @SerializedName("a_id")
    private String a_id;
    @SerializedName("title")
    private String title;
    @SerializedName("title_en")
    private String title_en;

    public String getA_id() {
        return a_id;
    }

    public String getTitle() {
        if (Y5CitySDKActivity.locale.equals("en")) {
            return title_en;
        } else {
            return title;
        }
    }

    public String getTitle_en() {
        return title_en;
    }

}
