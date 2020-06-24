package com.omni.y5citysdk.module.info;

import com.google.gson.annotations.SerializedName;
import com.omni.y5citysdk.Y5CitySDKActivity;

import java.io.Serializable;

public class InfoData implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("name")
    private String name;
    @SerializedName("content")
    private String content;
    @SerializedName("content_en")
    private String content_en;

    public String getName() {
        return name;
    }

    public String getTtile() {
        return title;
    }

    public String getContent() {
        if (Y5CitySDKActivity.locale.equals("en")) {
            return content_en;
        } else {
            return content;
        }
    }

    public String getContent_en() {
        return content_en;
    }

}
