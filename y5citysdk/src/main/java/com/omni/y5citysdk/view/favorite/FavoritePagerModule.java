package com.omni.y5citysdk.view.favorite;

import com.omni.y5citysdk.R;

public enum FavoritePagerModule {

//    RELIGION(R.string.fragment_favorite_tab_religion),
    FOOD(R.string.fragment_favorite_tab_food),
    VIEW(R.string.fragment_favorite_tab_view),
    SHOPPING(R.string.fragment_favorite_tab_shopping),
    HOTEL(R.string.fragment_favorite_tab_hotel);

    private int mTitleResId;

    FavoritePagerModule(int titleResId) {
        mTitleResId = titleResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }
}
