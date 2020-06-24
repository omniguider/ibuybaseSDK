package com.omni.y5citysdk.module.geo_fence;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GeoFenceInfo implements Serializable {

    transient SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH");

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("range")
    private int range;
    @SerializedName("trigger")
    private GeoFenceTrigger[] triggers;

    private boolean isUnread = false;
    private long lastPushTimeInMillis;
    private String lastPushDate;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getRange() {
        return range;
    }

    public GeoFenceTrigger[] getTriggers() {
        return triggers;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public String getInfoKey() {
        return getId() + "";
    }

    public long getLastPushTimeInMillis() {
        return lastPushTimeInMillis;
    }

    public String getLastPushDate() {
        return lastPushDate;
    }

    public void setLastPushDate(String lastPushDate) {
        this.lastPushDate = lastPushDate;
    }

    public void initPushDate() {
        if (!TextUtils.isEmpty(lastPushDate)) {
            return;
        }

        Date now = Calendar.getInstance().getTime();
        lastPushTimeInMillis = now.getTime();

        lastPushDate = dateFormat.format(now);
    }
}
