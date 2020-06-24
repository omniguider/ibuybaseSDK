package com.omni.y5citysdk.module.info;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InstructionData implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;

    public String getTtile() {
        return title;
    }

    public String getImage() {
        return image;
    }

}
