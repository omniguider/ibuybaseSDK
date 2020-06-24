package com.omni.y5citysdk.module.geo_fence;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeoFenceResponse implements Serializable {

    @SerializedName("cooldown")
    private int cooldown;
    @SerializedName("geofence")
    private GeoFenceInfo[] geoFenceInfos;

    public int getCooldown() {
        return cooldown;
    }

    public GeoFenceInfo[] getGeoFenceInfos() {
        return geoFenceInfos;
    }
}
