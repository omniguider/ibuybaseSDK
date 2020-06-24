package com.omni.y5citysdk.module.info;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BannerData implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("p_id")
    private String p_id;
    @SerializedName("url")
    private String url;

    public String getId() {
        return id;
    }

    public String getTtile() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getP_id() {
        return p_id;
    }

}
