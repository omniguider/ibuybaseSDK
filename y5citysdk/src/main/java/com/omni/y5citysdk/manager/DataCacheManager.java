package com.omni.y5citysdk.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.omni.y5citysdk.module.favorite.FavoriteData;
import com.omni.y5citysdk.module.favorite.FavoriteInfo;
import com.omni.y5citysdk.tool.PreferencesTools;

public class DataCacheManager {

    private static DataCacheManager mDataCacheManager;
    private Gson mGson;

    public static DataCacheManager getInstance() {
        if (mDataCacheManager == null) {
            mDataCacheManager = new DataCacheManager();
        }
        return mDataCacheManager;
    }

    private Gson getGson() {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson;
    }

    public FavoriteData getAllFavorite(Context context) {
        String allFavoriteStr = PreferencesTools.getInstance().getProperty(context, PreferencesTools.KEY_PREFERENCES_FAVORITE);
        if (allFavoriteStr == null) {
            return null;
        } else {
            return getGson().fromJson(allFavoriteStr, FavoriteData.class);
        }
    }

    public boolean isFavorite(Context context, String name) {
        FavoriteData favoriteData = getAllFavorite(context);
        if (favoriteData != null) {
            for (FavoriteInfo favoriteInfo : favoriteData.getFood()) {
                if (favoriteInfo.getname().equals(name)) {
                    return true;
                }
            }
            for (FavoriteInfo favoriteInfo : favoriteData.getHotel()) {
                if (favoriteInfo.getname().equals(name)) {
                    return true;
                }
            }
            for (FavoriteInfo favoriteInfo : favoriteData.getReligion()) {
                if (favoriteInfo.getname().equals(name)) {
                    return true;
                }
            }
            for (FavoriteInfo favoriteInfo : favoriteData.getShopping()) {
                if (favoriteInfo.getname().equals(name)) {
                    return true;
                }
            }
            for (FavoriteInfo favoriteInfo : favoriteData.getView()) {
                if (favoriteInfo.getname().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}
