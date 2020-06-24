package com.omni.y5citysdk.module.project;

import com.google.gson.annotations.SerializedName;
import com.omni.y5citysdk.module.favorite.FavoriteInfo;

import java.io.Serializable;

public class ProjectData implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("id")
    private String id;
    @SerializedName("code")
    private String code;

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

}
