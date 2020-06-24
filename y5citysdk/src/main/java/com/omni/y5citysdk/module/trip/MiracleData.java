package com.omni.y5citysdk.module.trip;

import com.google.gson.annotations.SerializedName;
import com.omni.y5citysdk.Y5CitySDKActivity;

import java.io.Serializable;

public class MiracleData implements Serializable {

    @SerializedName("desc")
    private String desc;
    @SerializedName("desc_en")
    private String desc_en;
    @SerializedName("image")
    private String image;
    @SerializedName("audio")
    private String audio;
    @SerializedName("audio_en")
    private String audio_en;

    public String getDesc() {
        if (Y5CitySDKActivity.locale.equals("en")) {
            return desc_en;
        } else {
            return desc;
        }
    }

    public String getDesc_en() {
        return desc_en;
    }

    public String getImage() {
        return image;
    }

    public String getAudio() {
        if (Y5CitySDKActivity.locale.equals("en")) {
            return audio_en;
        } else {
            return audio;
        }
    }

}
