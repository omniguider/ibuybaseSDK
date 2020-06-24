package com.omni.y5citysdk.module.favorite;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FavoriteData implements Serializable {

    @SerializedName("religion")
    private FavoriteInfo[] religion;
    @SerializedName("food")
    private FavoriteInfo[] food;
    @SerializedName("hotel")
    private FavoriteInfo[] hotel;
    @SerializedName("shopping")
    private FavoriteInfo[] shopping;
    @SerializedName("view")
    private FavoriteInfo[] view;

    public FavoriteInfo[] getReligion() {
        return religion;
    }

    public FavoriteInfo[] getFood() {
        return food;
    }

    public FavoriteInfo[] getHotel() {
        return hotel;
    }

    public FavoriteInfo[] getShopping() {
        return shopping;
    }

    public FavoriteInfo[] getView() {
        return view;
    }

}
