package com.omni.y5citysdk.module.info;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CoverData implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("image")
    private String image;

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

}
