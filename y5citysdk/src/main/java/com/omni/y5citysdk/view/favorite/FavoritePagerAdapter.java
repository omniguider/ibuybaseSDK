package com.omni.y5citysdk.view.favorite;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.omni.y5citysdk.manager.DataCacheManager;
import com.omni.y5citysdk.module.favorite.FavoriteData;

public class FavoritePagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    public FavoritePagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        mContext = context;
    }

    @Override
    public int getCount() {
        return FavoritePagerModule.values().length;
    }

    @Override
    public Fragment getItem(int position) {
        FavoriteData favoriteData = DataCacheManager.getInstance().getAllFavorite(mContext);

//        if (position == FavoritePagerModule.RELIGION.ordinal()) {
//            return FavoriteListFragment.newInstance(favoriteData.getReligion());
//        } else
        if (position == FavoritePagerModule.FOOD.ordinal()) {
            return FavoriteListFragment.newInstance(favoriteData.getFood());
        } else if (position == FavoritePagerModule.VIEW.ordinal()) {
            return FavoriteListFragment.newInstance(favoriteData.getView());
        } else if (position == FavoritePagerModule.SHOPPING.ordinal()) {
            return FavoriteListFragment.newInstance(favoriteData.getShopping());
        } else if (position == FavoritePagerModule.HOTEL.ordinal()) {
            return FavoriteListFragment.newInstance(favoriteData.getHotel());
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(FavoritePagerModule.values()[position].getTitleResId());
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
