package com.omni.y5citysdk.module.point;

import com.google.gson.annotations.SerializedName;
import com.omni.y5citysdk.Y5CitySDKActivity;

import java.io.Serializable;

public class ObjectiveData implements Serializable {

    @SerializedName("ro_id")
    private String ro_id;
    @SerializedName("title")
    private String title;
    @SerializedName("title_en")
    private String title_en;

    public String getRo_id() {
        return ro_id;
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
